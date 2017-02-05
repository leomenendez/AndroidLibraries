package com.electapps.customcamera.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.electapps.customcamera.R;
import com.electapps.customcamera.callbacks.SurfaceCreatedCallback;
import com.electapps.customcamera.controller.CameraHandler;
import com.electapps.customcamera.util.Alerts;
import com.electapps.customcamera.util.Size;

/**
 * Created by leo on 12/29/2015.
 */
public class CameraContainer extends FrameLayout {

    public static final String TAG = CameraContainer.class.getSimpleName();

    private SurfaceView surfaceView;
    private SurfaceCreatedCallback callback;
    private Size scaledSize;
    private ScaleGestureDetector zoomDetector;

    private boolean shouldReLayout = false;

    public CameraContainer(Context context) {
        super(context);
    }

    public CameraContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView, SurfaceCreatedCallback callback) {
        //check for permisions
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            Alerts.showAlertDialog(getContext(), getContext().getString(R.string.alert_permission_title), getContext().getString(R.string.alert_permission_camera));
        else {
            removeAllViews();
            this.surfaceView = surfaceView;
            this.callback = callback;
            addView(surfaceView);
            zoomDetector = new ScaleGestureDetector(getContext(), new ZoomListener());
        }
    }

/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
*/

    public boolean isSurfaceAdded(){
        View view;
        for(int i=0; i<getChildCount(); i++){
            view = getChildAt(i);
            if(view instanceof SurfaceView)
                return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if ((changed || shouldReLayout) && surfaceView != null) {
            scaledSize = Size.getInstance(getContext()).getScaledSize(getMeasuredWidth(), getMeasuredHeight());
            Size.Offset offset = scaledSize.getOffset();
            surfaceView.layout(0-offset.h, 0-offset.v, scaledSize.getWidth()-offset.h, scaledSize.getHeight()-offset.v);

            if(callback!=null)
                callback.didCreateSurface();

            shouldReLayout = false;
        }else if(surfaceView==null)
            //layout was done without surface view. Subsequent calls should attempt to layout regardless of any changes in layout size
            shouldReLayout = true;
    }

    @Override
    public void removeAllViews(){
        super.removeAllViews();
        surfaceView = null;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_UP) {
            Point focusPoint = scaledSize.getScaledLocation(event.getX(), event.getY());
            int size = 20;
            Rect focusRect = new Rect(focusPoint.x, focusPoint.y, focusPoint.x + size, focusPoint.y + size);

            CameraHandler.focusCamera(focusRect);

        } else if(zoomDetector!=null){
            return zoomDetector.onTouchEvent(event);

        }
        return super.onTouchEvent(event);
    }


    private class ZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector gestureDetector){
            float zoomFactor = gestureDetector.getScaleFactor();
            Log.d(TAG, "Zoom factor: "+zoomFactor);
            CameraHandler.zoomCamera(zoomFactor);
            return false;
        }
    }
}
