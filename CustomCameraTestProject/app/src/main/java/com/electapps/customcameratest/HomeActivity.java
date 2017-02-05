package com.electapps.customcameratest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcameratest.activitytest.CameraActivity;
import com.electapps.customcameratest.fragmenttest.FragmentCameraActivity;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class HomeActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    View btnActivity, btnFragment;
    Switch rotationToggle;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_home);

        btnActivity = findViewById(R.id.btn_activity);
        btnFragment = findViewById(R.id.btn_fragment);
        rotationToggle = (Switch) findViewById(R.id.rotation_toggle);

        btnActivity.setOnClickListener(this);
        btnFragment.setOnClickListener(this);

        rotationToggle.setOnCheckedChangeListener(this);
    }

    private void launchCameraActivity(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void launchFragmentActivity(){
        Intent intent = new Intent(this, FragmentCameraActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_activity:
                launchCameraActivity();
                break;
            case R.id.btn_fragment:
                launchFragmentActivity();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CameraHandler.setRotationFix(this, isChecked);
    }
}
