package com.jkingone.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2018/6/22.
 */

public final class PermissionUtils {
    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static void hasPermission(Activity context) {

        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS)) {
            ActivityCompat.requestPermissions(context, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
