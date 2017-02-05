package com.electapps.customcameratest.fragmenttest;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.electapps.customcamera.enums.CameraType;
import com.electapps.customcamera.object.Flash;
import com.electapps.customcameratest.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class FragmentCameraActivity extends FragmentActivity implements Camera.PictureCallback, CameraFragmentCallback {

    public static String TAG = FragmentCameraActivity.class.getSimpleName();

    CameraFragment cameraFragment;
    CameraControlsFragment cameraControlsFragment;
    PictureFragment pictureFragment;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_fragment_camera);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        cameraFragment = new CameraFragment();
        cameraControlsFragment = new CameraControlsFragment();
        pictureFragment = new PictureFragment();
        transaction.replace(R.id.contain_camera, cameraFragment);
        transaction.replace(R.id.contain_picture, pictureFragment);
        transaction.replace(R.id.contain_controls, cameraControlsFragment);

        transaction.commit();
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
            if(pictureFragment!=null)
                pictureFragment.showPicture(imageUri);
        }

    }

    @Override
    public void setFlash(Flash flash) {
        if(cameraControlsFragment!=null)
            cameraControlsFragment.setFlash(flash);
    }

    @Override
    public void setType(CameraType cameraType) {
        if(cameraControlsFragment!=null)
            cameraControlsFragment.setCameraType(cameraType);
    }

    @Override
    public Camera.PictureCallback getPictureCallback() {
        return this;
    }
}
