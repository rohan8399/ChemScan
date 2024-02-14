package com.example.eathealthy;

import static android.Manifest.permission.CAMERA;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import java.util.StringTokenizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
@RequiresApi(api = Build.VERSION_CODES.Q)
public class Scan extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private static final  int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    String scanResult,or_type;
    // JSONObject scanner_result = new JSONObject();


    // ASKING CAMERA PERMISSIONS FROM THE USER'S PHONE TO USE SCANNER

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(getSupportActionBar() != null){
            setTitle("Scan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (checkPermission()){
            //Toast.makeText(Scan.this, "Permission is  Granted!", Toast.LENGTH_SHORT).show();

        }
        else {
            requestPermission();
        }

    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(Scan.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    Toast.makeText(Scan.this, "Permission is Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Scan.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        displayAlertMessage(
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                    }
                                });

                    }
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        //{
        if (checkPermission())
        {
            if (scannerView == null)
            {
                scannerView = new ZXingScannerView(this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
        else
        {
            requestPermission();
        }
        //}
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(Scan.this)
                .setMessage("You need to allow access for both permissions")
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void handleResult(final Result result) {
        scanResult = result.getText();
        Log.e("Result1",scanResult.toString());

        MainActivity.VerifyQR(scanResult);
        StringTokenizer tokens = new StringTokenizer(scanResult, "\n");
        final String one=tokens.nextToken();

        onBackPressed();

    }
}
