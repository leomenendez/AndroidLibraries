package com.electapps.customcameratest.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.electapps.customcamera.callbacks.ShutterClickAdapter;
import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.enums.CameraType;
import com.electapps.customcamera.object.Flash;
import com.electapps.customcameratest.R;

import java.io.IOException;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class CameraControlsFragment extends Fragment implements View.OnClickListener{

    Context context;
    CameraFragmentCallback fragmentCallback;
    View btnShutter, btnFlash, btnSwitch;
    TextView txtFlash, txtSwitch;

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
        return inflater.inflate(R.layout.fragment_controls, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        btnShutter = view.findViewById(R.id.btn_shutter);
        btnFlash = view.findViewById(R.id.btn_flash);
        btnSwitch = view.findViewById(R.id.btn_switch);

        txtFlash = (TextView) view.findViewById(R.id.txt_flash);
        txtSwitch = (TextView) view.findViewById(R.id.txt_switch);

        btnShutter.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shutter:
                CameraHandler.takePicture(getContext(), new ShutterClickAdapter(context), fragmentCallback.getPictureCallback());
                break;
            case R.id.btn_flash:
                Flash flash = CameraHandler.toggleFlash(context);
                txtFlash.setText(flash.getTitle());
                break;
            case R.id.btn_switch:
                try {
                    CameraHandler.switchCamera(context);
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    Toast.makeText(context, "Cannot Switch Camera", Toast.LENGTH_SHORT).show();
                }finally {
                    txtSwitch.setText(CameraHandler.getSelectedCamera().toString());
                }
                break;
        }

    }

    public void setFlash(Flash flash){
        if(txtFlash!=null)
            txtFlash.setText(flash.getTitle());
    }

    public void setCameraType(CameraType cameraType){
        if(txtSwitch!=null)
            txtSwitch.setText(cameraType.toString());
    }
}
