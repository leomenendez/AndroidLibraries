package com.electapps.customcamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.electapps.customcamera.R;
import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.enums.CameraType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LeoSdsol on 6/26/16.
 */
public class ImageUtil {

    public static String TAG = ImageUtil.class.getSimpleName();

    public static Bitmap getRotatedBitmap(Bitmap bm, float degree) {
        Bitmap bitmap = bm;
        if (degree != 0) {
            Matrix matrix = new Matrix();
            matrix.preRotate(degree);
//            if(shouldFlip)
//                matrix.preScale(-1,1);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        }

        return bitmap;
    }

    public static Uri getRotatedFile(Context context, Uri uri){
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            int rotation = getExifRotation(uri.getPath());
            bitmap = getRotatedBitmap(bitmap, rotation);

            File fileOut = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "img"+System.currentTimeMillis());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fos = new FileOutputStream(fileOut);
            fos.write(bytes.toByteArray());
            fos.close();

            uri = Uri.fromFile(fileOut);

        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        return uri;
    }

    public static int getExifRotation(String path){
        int angle = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);


            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
        }catch (IOException ioe){
            Log.e(TAG, "Erro retrieving Rotation Data\n" + ioe.getMessage());
        }
        return angle;
    }

    public static int getCameraRotation(Context context){
        int degrees = CameraHandler.getDeviceOrientationForPreview(context);
        boolean rotationFix = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.rotation_pref), false);

        if (CameraHandler.getSelectedCamera() == CameraType.FRONT) {
            degrees += 180;
            if (rotationFix)
                degrees -= CameraHandler.getCameraInfo().orientation;
        }else{
            if (rotationFix)
                degrees += CameraHandler.getCameraInfo().orientation;
        }

        return degrees;
    }
}
