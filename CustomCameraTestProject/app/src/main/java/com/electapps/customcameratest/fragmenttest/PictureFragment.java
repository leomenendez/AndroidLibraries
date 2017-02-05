package com.electapps.customcameratest.fragmenttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.enums.CameraType;
import com.electapps.customcamera.util.ImageUtil;
import com.electapps.customcameratest.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class PictureFragment extends Fragment {

    ImageView pictureView;
    View pictureError;
    Context context;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.activity_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        pictureView = (ImageView) view.findViewById(R.id.view_picture);
        pictureError = view.findViewById(R.id.picture_error);
    }

    public void showPicture(Uri pictureData){
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
            InputStream imageStream = context.getContentResolver().openInputStream(uri);
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
