package com.electapps.customcameratest.fragmenttest;

import android.hardware.Camera;

import com.electapps.customcamera.enums.CameraType;
import com.electapps.customcamera.object.Flash;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public interface CameraFragmentCallback {
    void setFlash(Flash flash);
    void setType(CameraType cameraType);
    Camera.PictureCallback getPictureCallback();

}
