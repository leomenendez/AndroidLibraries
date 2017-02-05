package com.electapps.customcamera.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;

import com.electapps.customcamera.controller.CameraHandler;


/**
 * Created by leo on 12/29/2015.
 */
public class Size {
    private int width, height;
    private int containerWidth, containerHeight;
    private float scale;
    private Camera.Size optimalSize;
    private Context context;
    private Offset offset;

    private static Size instance;

    public static Size getInstance(Context context){
        if(instance == null)
            instance = new Size(context);
        instance.context = context;
        return instance;
    }

    private Size(Context context){
        this.context = context;
        this.offset = new Offset(0,0);
    }


    public int getWidth() { return width; }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getScale() { return scale; }

    public Offset getOffset() { return offset; }

    public Size getScaledSize(int inWidth, int inHeight){
        containerHeight = inHeight;
        containerWidth = inWidth;

        optimalSize = CameraHandler.getPreviewSize();
        int rotation = CameraHandler.getDeviceOrientationForPreview(context);

        float outWidth = 0;
        float outHeight = 0;

        if(optimalSize!=null){
            outWidth = optimalSize.width;
            outHeight = optimalSize.height;
        }

        if(rotation == 90 || rotation == 270){
            //flip the width & height values
            float temp = outWidth;
            outWidth = outHeight;
            outHeight = temp;
        }

        float wScale = (float) inWidth/outWidth;
        float hScale = (float) inHeight/outHeight;

        scale = Math.max(wScale, hScale);

        outWidth = outWidth*scale;
        outHeight = outHeight*scale;

        width = (int) outWidth;
        height = (int) outHeight;

        offset.v = Offset.calcOffset(inHeight, height);
        offset.h = Offset.calcOffset(inWidth, width);

        return this;
    }

    public Point getScaledLocation(float x, float y){
        float xTranslation = (x+offset.h)/scale;
        float yTranslation = (y+offset.v)/scale;

        float scaledWidth = width/scale;
        float scaledHeight = height/scale;

        int left = (int) (xTranslation/scaledWidth * 2000 -1000);
        int top = (int) (yTranslation/scaledHeight * 2000 - 1000);
        return new Point(left, top);
    }

    public Rect getDisplayCroppingSquare(){
        int padding = 100;
        int square = Math.min(containerWidth, containerHeight) - padding;


        Rect rect =  new Rect((containerWidth-square)/2, (containerHeight-square)/2, ((containerWidth - square)/ 2)+square, ((containerHeight - square)/ 2)+square);
        return rect;
    }

    public static class Offset{
        public int v, h;

        public Offset (int v, int h){
            this.v = v;
            this.h = h;
        }

        public static int calcOffset(int displaySize, int totalSize){
            return (totalSize-displaySize)/2;
        }

    }



}
