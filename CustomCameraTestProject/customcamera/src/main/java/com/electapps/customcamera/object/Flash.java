package com.electapps.customcamera.object;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by leo on 6/23/2016.
 */
public class Flash {

    private int resource;
    private String title;
    private Context context;

    public Flash (){

    }

    public Flash(int resource, String title){
        this.resource = resource;
        this.title = title;
    }

    public int getResource() {
        return resource;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getDrawable(){
        return context.getResources().getDrawable(resource);
    }

    public void setFlash(int resource, String title) {
        this.resource = resource;
        this.title = title;
    }

}

