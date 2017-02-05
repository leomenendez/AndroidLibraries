package com.electapps.customcamera.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.electapps.customcamera.controller.CameraHandler;

import java.io.IOException;

/**
 * Created by leo on 12/28/2015.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;
    private String TAG = getClass().getSimpleName();


    public CameraPreview(Context context, Camera camera){
        super(context);
        this.camera = camera;

        holder = getHolder();
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            CameraHandler.setSurfaceHolder(getContext(), holder);
            CameraHandler.startPreview();
        }catch (IOException ioe){
            Log.d(TAG, "Could not set Camera Preview\n Error: "+ioe.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(this.holder.getSurface() == null)
            return;

        try{
            CameraHandler.stopPreview();
        }catch (Exception e){};

        surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraHandler.stopPreview();
        CameraHandler.releaseCamera();
    }


}
