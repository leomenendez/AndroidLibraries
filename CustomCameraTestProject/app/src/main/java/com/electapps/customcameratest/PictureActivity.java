package com.electapps.customcameratest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.enums.CameraType;
import com.electapps.customcamera.util.ImageUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class PictureActivity extends Activity {

    ImageView pictureView;
    View pictureError;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Uri pictureData = getIntent().getData();

        setContentView(R.layout.activity_picture);
        pictureView = (ImageView) findViewById(R.id.view_picture);
        pictureError = findViewById(R.id.picture_error);

        if(pictureData!=null) {
            setPictureImage(pictureData);
            pictureView.setVisibility(View.VISIBLE);
            pictureError.setVisibility(View.GONE);
        }else{
            pictureView.setVisibility(View.INVISIBLE);
            pictureError.setVisibility(View.VISIBLE);
        }
    }

    private void setPictureImage(Uri uri){
        try{
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            int rotation = ImageUtil.getExifRotation(uri.getPath());
            if(CameraHandler.getSelectedCamera()== CameraType.FRONT)
                rotation+=180;

            bitmap = ImageUtil.getRotatedBitmap(bitmap, rotation);
            if (bitmap != null) {
                pictureView.setImageBitmap(bitmap);
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

}
