package com.electapps.customcamera.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by leo on 2/4/17.
 */

public class Alerts {
    public static AlertDialog aDialog;

    public static void showAlertDialog(Context context, String title, String msg) {
        if(aDialog!=null && aDialog.isShowing())
            aDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                aDialog.dismiss();
            }
        });

        aDialog = builder.create();
        aDialog.show();
    }

}
