package com.seanschlaefli.nanofitness;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    private Activity mActivity;
    private boolean mHasLocationPermission;

    public PermissionManager(Activity activity) {
        mActivity = activity;
        checkLocationPermission();
    }

    public boolean hasLocationPermission() {
        checkLocationPermission();
        return mHasLocationPermission;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mHasLocationPermission = true;
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(mActivity,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);

            }
        } else {
            mHasLocationPermission = false;
        }
    }
}
