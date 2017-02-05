package com.electapps.customcameratest.fragmenttest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.view.CameraContainer;
import com.electapps.customcamera.view.CameraPreview;
import com.electapps.customcameratest.R;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class CameraFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CAMERA = 0x444;

    CameraContainer cameraView;
    CameraPreview cameraPreview;
    boolean hasAddedCamera = false;
    Context context;
    CameraFragmentCallback fragmentCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
        try {
            fragmentCallback = (CameraFragmentCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Could not cast" + context + "to CameraFragmentCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        cameraView = (CameraContainer) view.findViewById(R.id.contain_camera);
//        initCamera();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!hasAddedCamera)
            initCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        //remove camera to prevent locking it
        removeCamera();
    }



    private void initCamera() {
        //check for permissions
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
            return;
        }

        if (CameraHandler.isCameraAvailable(context)) {
            cameraView.measure(0, 0);
            if(cameraView.getSurfaceView()==null) {
                cameraPreview = new CameraPreview(getContext(), CameraHandler.getCameraInstance());
            }
            cameraView.setSurfaceView(cameraPreview, null);

            hasAddedCamera = cameraView.isSurfaceAdded();

            if(!hasAddedCamera)
                return;

            CameraHandler.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            CameraHandler.enableZoom();

            fragmentCallback.setFlash(CameraHandler.getFlashMode(context));
            fragmentCallback.setType(CameraHandler.getSelectedCamera());
        }
    }

    private void removeCamera() {
        if (hasAddedCamera) {
            CameraHandler.releaseCamera();
            hasAddedCamera = false;
        }
    }

    private void getPermissions(){
        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSIONS_REQUEST_CAMERA:
                if(grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initCamera();
                break;
        }
    }

}
