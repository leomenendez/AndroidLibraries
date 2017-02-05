package com.electapps.customcamera.callbacks;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;

import com.electapps.customcamera.R;

/**
 * Created by LeoSdsol on 6/24/16.
 */
public class ShutterClickAdapter implements Camera.ShutterCallback {

    private Context context;

    public ShutterClickAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onShutter() {
        MediaPlayer.create(context, R.raw.shutter_click).start();
    }
}
