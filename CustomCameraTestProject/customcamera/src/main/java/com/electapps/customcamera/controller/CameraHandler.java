package com.electapps.customcamera.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.electapps.customcamera.R;
import com.electapps.customcamera.enums.CameraType;
import com.electapps.customcamera.object.Flash;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leo on 12/28/2015.
 */
public class CameraHandler {

    private static Camera camera;
    private static Camera.CameraInfo cameraInfo;
    private static SurfaceHolder surfaceHolder;
    private static String TAG = "CameraHandler";
    private static boolean isTakingPicture = false;
    private static boolean isPreviewing = false;
    private static boolean isZoomEnabled = false;

    private static int maxZoom;

    private static CameraType lastCamera = CameraType.BACK;


    public static Camera getCameraInstance() {
        if (camera == null) {
            try {
                camera = Camera.open(lastCamera.getValue());
                cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(lastCamera.getValue(), cameraInfo);
                maxZoom = camera.getParameters().getMaxZoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return camera;
    }


    public static void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        camera = null;
    }

    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static CameraType getSelectedCamera() {
        return lastCamera;
    }

    public static Camera.Size getPreviewSize() {
        if (camera != null)
            return camera.getParameters().getPreviewSize();

        return null;
    }

    public static void setSurfaceHolder(Context context, SurfaceHolder holder) throws IOException {
        int orientationForPreview = getDeviceOrientationForPreview(context);
        int orientationForCapture = getDeviceOrientationForCapture(context);
        if (camera == null)
            camera = getCameraInstance();

        camera.setDisplayOrientation(orientationForPreview);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(orientationForPreview);
        camera.setParameters(params);
        camera.setPreviewDisplay(holder);
        surfaceHolder = holder;


    }

    public static int getDeviceOrientationForPreview(Context context) {
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;

        //for most device an adjustment is required
        Log.d(TAG, "Device Orientation: " + rotation);
        switch (rotation) {
            case Surface.ROTATION_0:
//                degrees = 90;
                degrees = 0;
                break;
            case Surface.ROTATION_90:
//                degrees = 0;
                degrees = 90;
                break;
            case Surface.ROTATION_180:
//                degrees = 270;
                degrees = 180;
                break;
            case Surface.ROTATION_270:
//                degrees = 180;
                degrees = 270;
                break;
        }

        int fixed = degrees;
        if (camera == null)
            return fixed;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            fixed = (cameraInfo.orientation + degrees) % 360;
            fixed = (360 - fixed) % 360;
        } else {
            fixed = (cameraInfo.orientation - degrees + 360) % 360;
        }

        return fixed;
    }

    public static int getDeviceOrientationForCapture(Context context) {
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;

        //for most device an adjustment is required
        Log.d(TAG, "Device Orientation: " + rotation);
        switch (rotation) {
            case Surface.ROTATION_0:
//                degrees = 90;
                degrees = 0;
                break;
            case Surface.ROTATION_90:
//                degrees = 0;
                degrees = 90;
                break;
            case Surface.ROTATION_180:
//                degrees = 270;
                degrees = 180;
                break;
            case Surface.ROTATION_270:
//                degrees = 180;
                degrees = 270;
                break;
        }

        int fixed = degrees;
        if (camera == null)
            return fixed;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            fixed = (cameraInfo.orientation + degrees) % 360;
        } else {
            fixed = (cameraInfo.orientation - degrees + 360) % 360;
        }

        return fixed;
    }

    public static void setRotationFix(Context context, boolean fixRotation) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String rotationPref = context.getString(R.string.rotation_pref);
        preferences.edit().putBoolean(rotationPref, fixRotation).commit();
    }

    public static boolean getRotationSetting(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String rotationPref = context.getString(R.string.rotation_pref);
        return preferences.getBoolean(rotationPref, false);
    }

    public static void focusCamera(Rect focusRect) {
        if (camera != null) {
            List<Camera.Area> areas = new LinkedList<>();
            areas.add(new Camera.Area(focusRect, 500));

            camera.cancelAutoFocus();

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusAreas(areas);

            try {
                camera.setParameters(parameters);
                camera.autoFocus(null);
            } catch (RuntimeException rte) {
                rte.printStackTrace();
            }
        }
    }

    public static void switchCamera(Context context) throws IOException {
        if (isPreviewing) {
            stopPreview();
            releaseCamera();
        }

        switch (lastCamera) {
            case FRONT:
                lastCamera = CameraType.BACK;
                break;
            case BACK:
                lastCamera = CameraType.FRONT;
                break;
            default:
                lastCamera = CameraType.BACK;
                break;
        }

        getCameraInstance();
        setSurfaceHolder(context, surfaceHolder);
        startPreview();


    }


    public static void takePicture(Context context, Camera.ShutterCallback shutterCallback, final Camera.PictureCallback jpegCallback) {
        if (camera != null && !isTakingPicture) {
            Camera.PictureCallback interruptCallback = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    isTakingPicture = false;
                    stopPreview();
                    jpegCallback.onPictureTaken(data, camera);
                    startPreview();
                }
            };

            camera.takePicture(shutterCallback, null, interruptCallback);
            isTakingPicture = true;
        }
    }

    public static void startPreview() {
        if (camera != null) {
            camera.startPreview();
            isPreviewing = true;
        }
    }

    public static void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            isPreviewing = false;
        }
    }

/*    public static Drawable toggleFlash(Context context){
        Drawable flashIcon = context.getResources().getDrawable(R.drawable.flash_icon); //FIXME add properly named icons for flash modes
        if(camera!=null){
            Camera.Parameters parameters = camera.getParameters();
            String flashMode = parameters.getFlashMode();
            switch (flashMode){
                case Camera.Parameters.FLASH_MODE_OFF:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    flashIcon = context.getResources().getDrawable(R.drawable.flash_icon); //FIXME add properly named icons for flash modes
                    break;
                case Camera.Parameters.FLASH_MODE_AUTO:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    flashIcon = context.getResources().getDrawable(R.drawable.flash_icon); //FIXME add properly named icons for flash modes
                    break;
                case Camera.Parameters.FLASH_MODE_ON:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flashIcon = context.getResources().getDrawable(R.drawable.flash_icon); //FIXME add properly named icons for flash modes
                    break;
            }
            camera.setParameters(parameters);
        }
        return flashIcon;
    }*/


    public static void setFlashMode(String flashMode) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            switch (flashMode) {
                case Camera.Parameters.FLASH_MODE_OFF:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    break;
                case Camera.Parameters.FLASH_MODE_AUTO:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    break;
                case Camera.Parameters.FLASH_MODE_ON:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    break;
                default:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    break;
            }

            try {
                camera.setParameters(parameters);
            } catch (RuntimeException rte) {
                rte.printStackTrace();
//                setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
    }

    public static Flash getFlashMode(Context context) {
        Flash flash = new Flash(R.drawable.flash_icon, context.getString(R.string.flash_off));
        try {
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                String flashMode = parameters.getFlashMode();
                switch (flashMode) {
                    case Camera.Parameters.FLASH_MODE_OFF:
                        flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_off));//FIXME add properly named icons for flash modes
                        break;
                    case Camera.Parameters.FLASH_MODE_AUTO:
                        flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_auto));//FIXME add properly named icons for flash modes
                        break;
                    case Camera.Parameters.FLASH_MODE_ON:
                        flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_on));//FIXME add properly named icons for flash modes
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flash;
    }

    public static Flash toggleFlash(Context context) {
        Flash flash = new Flash(R.drawable.flash_icon, context.getString(R.string.flash_on));

        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            String flashMode = parameters.getFlashMode();
            switch (flashMode) {
                case Camera.Parameters.FLASH_MODE_OFF:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_auto));//FIXME add properly named icons for flash modes
                    break;
                case Camera.Parameters.FLASH_MODE_AUTO:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_on));//FIXME add properly named icons for flash modes
                    break;
                case Camera.Parameters.FLASH_MODE_ON:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_off));//FIXME add properly named icons for flash modes
                    break;
            }

            try {
                camera.setParameters(parameters);
            } catch (RuntimeException rte) {
                rte.printStackTrace();
                setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                flash.setFlash(R.drawable.flash_icon, context.getString(R.string.flash_off));//FIXME add properly named icons for flash modes
            }

        }
        return flash;

    }

    public static void enableZoom(){
        if(camera!=null){
            Camera.Parameters parameters =  camera.getParameters();
            if(!parameters.isZoomSupported())
                return;
            parameters.setZoom(0);
            camera.setParameters(parameters);
            isZoomEnabled = true;
        }
    }

    public static void disableZoom(){
         if(camera!=null){
            Camera.Parameters parameters =  camera.getParameters();
            if(!parameters.isZoomSupported())
                return;
            parameters.setZoom(0);
            camera.setParameters(parameters);
         }
        isZoomEnabled = false;
    }

    public static void zoomCamera(float zoomFactor){
        if(camera!=null && isZoomEnabled){
            Camera.Parameters parameters = camera.getParameters();
            int zoomLevel = parameters.getZoom();
            if(zoomFactor>1)
                zoomLevel = (int) (zoomLevel + zoomFactor * .5);
            else
                zoomLevel = (int)(zoomLevel - 5 * (1-zoomFactor));

            if(zoomLevel>maxZoom)
                zoomLevel = maxZoom;

            if(zoomLevel<0)
                zoomLevel = 0;

            parameters.setZoom(zoomLevel);
            camera.setParameters(parameters);
        }
    }

    public static Camera.CameraInfo getCameraInfo(){
        return cameraInfo;
    }

}
