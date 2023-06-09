package com.example.foodineye_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionRequester {

    //
    // Constants
    //
    public static final int REQUEST_CODE = 3030;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };


    //
    // Cores
    //
    public static void request(final Activity activity) {
        if (hasPermissions(activity, getRequirePermissions())) {
            return;
        }

        ActivityCompat.requestPermissions(activity, getRequirePermissions(), REQUEST_CODE);
    }

    private static void showRequestDialog(final Activity activity) {
        OnClickListener listener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                request(activity);
            }
        };

        AlertDialog.Builder builder = new AlertDialog
                .Builder(activity)
                .setTitle("Error")
                .setMessage("You must Camera Permission Agree")
                .setPositiveButton("Retry", listener);

        builder.show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context == null) {
            return false;
        }

        if (permissions == null) {
            return true;
        }

        int[] grants = new int[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            grants[i] = ContextCompat.checkSelfPermission(context, permissions[i]);
        }

        return checkAllGranted(grants);
    }

    public static boolean checkAllGranted(int[] grants) {
        for (int grant : grants) {
            if (PackageManager.PERMISSION_GRANTED != grant) {
                return false;
            }
        }

        return true;
    }


    //
    // Accessor
    //
    public static String[] getRequirePermissions() {
        return PERMISSIONS;
    }

}
