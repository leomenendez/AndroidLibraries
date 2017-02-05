package com.electapps.customcamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LeoSdsol on 10/20/15.
 */
public class CameraCropView extends View {
    private Rect clearRect;
    private Point clearPoint;
    private int radius;

    public CameraCropView(Context context) {
        super(context);
    }

    public CameraCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setClearRect(Rect clearRect){
        this.clearRect = clearRect;
        //this.clearPoint = null;
    }

    public void setClearPoint(Point clearPoint, int radius){
        this.clearPoint = clearPoint;
        this.radius = radius;
       // this.clearRect = null;
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        if(clearRect!=null){
            canvas.drawRect(clearRect, paint);
        }
        if(clearPoint!=null){
            canvas.drawCircle(clearPoint.x, clearPoint.y, radius,paint);
        }
    }
}
