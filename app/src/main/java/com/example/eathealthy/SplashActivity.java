package com.example.eathealthy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.eathealthy.Common.Helper;
import com.example.eathealthy.Common.SessionManager;

import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity {
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setContentView(R.layout.activity_splash);
        InitViews();
        if (methodRequiresPermission(Helper.perms, 111)) {
            Helper.l("all permissions available");
        }
    }

    private void InitViews() {
        sessionManager = new SessionManager(SplashActivity.this);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn()) {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    finish();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        }, 3000);

    }
    public boolean methodRequiresPermission(String[] perms, int permission) {

        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            return true;
        } else {

            Helper.l("Requesting permissions");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need these permissions",
                    permission, perms);
            return false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        // Some permissions have been granted

    }
}