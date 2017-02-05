package com.electapps.customcamera.enums;

/**
 * Created by leo on 6/23/2016.
 */
public enum CameraType {
    BACK(0), FRONT(1);

    private final int value;
    private CameraType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
