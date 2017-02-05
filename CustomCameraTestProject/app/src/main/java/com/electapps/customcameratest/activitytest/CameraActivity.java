package com.electapps.customcameratest.activitytest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.electapps.customcamera.callbacks.ShutterClickAdapter;
import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.object.Flash;
import com.electapps.customcamera.view.CameraContainer;
import com.electapps.customcamera.view.CameraPreview;
import com.electapps.customcameratest.PictureActivity;
import com.electapps.customcameratest.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class CameraActivity extends Activity implements View.OnClickListener, Camera.PictureCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CAMERA = 0x444;

    View btnShutter, btnFlash, btnSwitch;
    TextView txtFlash, txtSwitch;
    CameraContainer cameraView;
    CameraPreview cameraPreview;
    boolean hasAddedCamera = false;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_camera);

        btnShutter = findViewById(R.id.btn_shutter);
        btnFlash = findViewById(R.id.btn_flash);
        btnSwitch = findViewById(R.id.btn_switch);

        cameraView = (CameraContainer) findViewById(R.id.contain_camera);
        txtFlash = (TextView) findViewById(R.id.txt_flash);
        txtSwitch = (TextView) findViewById(R.id.txt_switch);

        btnShutter.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);

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
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
            return;
        }

        if (CameraHandler.isCameraAvailable(this)) {
            cameraView.measure(0, 0);
            if(cameraView.getSurfaceView()==null) {
                cameraPreview = new CameraPreview(this, CameraHandler.getCameraInstance());
            }
            cameraView.setSurfaceView(cameraPreview, null);

            hasAddedCamera = cameraView.isSurfaceAdded();

            if(!hasAddedCamera)
                return;

            CameraHandler.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            CameraHandler.enableZoom();

            txtFlash.setText(CameraHandler.getFlashMode(this).getTitle());
            txtSwitch.setText(CameraHandler.getSelectedCamera().toString());

        }
    }

    private void removeCamera() {
        if (hasAddedCamera) {
            CameraHandler.releaseCamera();
            hasAddedCamera = false;
        }
    }


    private void showPicture(Uri imageUri){
        Intent intent = new Intent(this, PictureActivity.class);
        intent.setData(imageUri);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shutter:
                CameraHandler.takePicture(this, new ShutterClickAdapter(this), this);
                break;
            case R.id.btn_flash:
                Flash flash = CameraHandler.toggleFlash(this);
                txtFlash.setText(flash.getTitle());
                break;
            case R.id.btn_switch:
                try {
                    CameraHandler.switchCamera(this);
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    Toast.makeText(this, "Cannot Switch Camera", Toast.LENGTH_SHORT).show();
                }finally {
                    txtSwitch.setText(CameraHandler.getSelectedCamera().toString());
                }
                break;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "img"+System.currentTimeMillis());
        if (outputFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            Toast.makeText(this, "Error creating media file, check storage permissions", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri imageUri = null;
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(data);
            outputStream.close();
            imageUri = Uri.fromFile(outputFile);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }finally {
            if(imageUri!=null)
                showPicture(imageUri);
        }

    }

    private void getPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
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
