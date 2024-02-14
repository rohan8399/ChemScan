package com.example.eathealthy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eathealthy.ApiKey.AuthKey;
import com.example.eathealthy.ApiKey.VolleyCallback;
import com.example.eathealthy.Bean.ThreeBean;
import com.example.eathealthy.Common.FusionBroadCast;
import com.example.eathealthy.Common.Helper;
import com.example.eathealthy.Common.SessionManager;
import com.example.eathealthy.URL.UrlBase;
import com.example.eathealthy.databinding.ActivityMainBinding;
import com.example.eathealthy.http.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static MainActivity instance = null;
    ActivityMainBinding binding;
    private static String url = "https://cloudcarbon.simpleitsolutions.co.in/products/";
    private static String news_url = "https://cloudcarbon.simpleitsolutions.co.in/news";
    private static String notification_url = "https://cloudcarbon.simpleitsolutions.co.in/api/pushNotification/rohan";
    ArrayList<ThreeBean> news_arrayList = new ArrayList<>();
    SessionManager sessionManager;
    private IntentFilter mIntentFilter;
    private String accuracy = "", latitude = "", longitude = "", UID = "";
    final String[] perms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(MainActivity.this);
        binding.LLScan.setOnClickListener(this);
        binding.LLLibrary.setOnClickListener(this);
        binding.LLReports.setOnClickListener(this);
        binding.LLRecommendations.setOnClickListener(this);
        binding.ImgLogout.setOnClickListener(this);
        binding.TvRefresh.setOnClickListener(this);
        this.instance = this;
        EnableGPSAutoMatically();
        if (methodRequiresPermission(perms, 111)) {
            Helper.l("permissions granted");

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(FusionBroadCast.BROADCAST_ACTION);

            registerReceiver(broadcastReceiver, mIntentFilter);
            startService(new Intent(MainActivity.this, FusionBroadCast.class));
            Helper.l("service start called");

        } else {
            Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
        }
        new NewsApi().execute();

//        Map<String, String> params = new LinkedHashMap<String, String>();
//        params.put("token", sessionManager.getStrVal("fcm_token"));
//        params.put("username", sessionManager.getStrVal("Username"));
//        GetDATA(1, params, "saveFcmKey", "PATCH");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Helper.l("token123"+token);

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d("TAG", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

//        Map<String, String> params2 = new LinkedHashMap<String, String>();
//        params2.put("username", sessionManager.getStrVal("Username"));
//        GetDATA(2, params2, "pushNotification/rohan", "post");
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void VerifyQR(String scanResult) {
        Toast.makeText(instance, "Scan successfully", Toast.LENGTH_SHORT).show();
        Log.e("scan_result", scanResult);

        new ValidateQR(scanResult).execute();
//            HttpRequest request = HttpRequest.post( url+ scanResult);
//            if (request.ok()) {
//                System.out.println("Status was updated");
//                String response = request.body();
//                try {
//                    JSONObject result1 = new JSONObject(response);
//                    if(result1.has("Status")) {
//                        if(result1.getString("Status").equalsIgnoreCase("1")) {
//                            ShowSuccessDialog("1",result1);
//                        }else{
//                           ShowSuccessDialog("0",result1);
//                        }
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            } else {
//                String response = "app failed";
//            }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LLScan:
                startActivity(new Intent(MainActivity.this, Scan.class));
                break;
            case R.id.LLReports:
                finish();
                startActivity(new Intent(MainActivity.this, ReportsActivity.class));
                break;
            case R.id.ImgLogout:
                ShowLogout();
                break;
            case R.id.LLLibrary:
                finish();
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)
                        .putExtra("index","history"));
                break;
            case R.id.TvRefresh:
//                int random1 = (int) (Math.random() * 50 + 1);
//                int random2 = (int) (Math.random() * 50 + 1);
//                binding.LLNews.removeAllViews();
//                if (news_arrayList.size() >= random1) {
//                    newsFeedRefresh(random1);
//                } else {
//                    random1 = (int) (Math.random() * 50 + 1);
//                    newsFeedRefresh(random1);
//                }
//                if (news_arrayList.size() >= random2) {
//                    newsFeedRefresh(random2);
//                } else {
//                    random2 = (int) (Math.random() * 50 + 1);
//                    newsFeedRefresh(random2);
//                }
                new NewsApi().execute();
                break;
            case R.id.LLRecommendations:
//                finish();
//                startActivity(new Intent(MainActivity.this, HistoryActivity.class)
//                        .putExtra("index","search"));
                finish();
                startActivity(new Intent(MainActivity.this,SearchAllProductsActivity.class));
                break;
        }
    }

    private void newsFeedRefresh(int random) {
        ThreeBean b = news_arrayList.get(random);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout row = (LinearLayout) inflater.inflate(R.layout.card_news, null);
        TextView TvTitle = (TextView) row.findViewById(R.id.TvTitle);
        TvTitle.setId(random);
        TvTitle.setText(b.getTitle());
        TextView TvDescription = (TextView) row.findViewById(R.id.TvDescription);
        TvDescription.setId(10 + random);
        TvDescription.setText(b.getDescription());
        ImageView Image = (ImageView) row.findViewById(R.id.image_small);
        Image.setId(100 + random);
        Glide.with(MainActivity.this)
                .load(b.getImage())
                .centerCrop()
                .into(Image);
        binding.LLNews.addView(row);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static class ValidateQR extends AsyncTask<Void, Void, String> {
        String scan_result = "";

        public ValidateQR(String scanResult) {
            scan_result = scanResult;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return postData();
        }

        private String postData() {
            String response = "default response";
            HttpRequest request = HttpRequest.get(url + scan_result);
            Log.e("request_status", request.message());
            if (request.ok()) {
                System.out.println("Status was updated");
                response = request.body();
            } else {
                response = "app failed";
            }
            return response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        protected void onPostExecute(String response) {
            try {
                Log.e("response_product", response);
                if (response.equalsIgnoreCase("app failed")) {
                } else {
                    JSONObject result1 = new JSONObject(response);
                    Log.e("data", result1.toString());
                    MovetoNext(result1.toString());
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static void MovetoNext(String result) {
        instance.finish();
        instance.startActivity(new Intent(MainActivity.getInstance(), GetProductDetailswithBarcodeActivity.class)
                .putExtra("data", result));
    }

    private class NewsApi extends AsyncTask<Void, Void, String> {


        public NewsApi() {

        }

        @Override
        protected String doInBackground(Void... voids) {
            return postData();
        }

        private String postData() {
            String response = "default response";
            HttpRequest request = HttpRequest.get(news_url);
            Log.e("request_status", request.message());
            if (request.ok()) {
                System.out.println("Status was updated");
                response = request.body();
            } else {
                response = "app failed";
            }
            return response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        protected void onPostExecute(String response) {
            try {
                Log.e("response111", response);
                if (response.equalsIgnoreCase("app failed")) {
                } else {
                    JSONObject result1 = new JSONObject(response);
                    JSONArray data_array = result1.getJSONArray("articles");
                    news_arrayList.clear();
                    for (int i = 0; i < data_array.length(); i++) {
                        ThreeBean bean = new ThreeBean();
                        JSONObject inner = data_array.getJSONObject(i);
                        bean.setTitle(inner.getString("source"));
                        bean.setDescription(inner.getString("description"));
                        bean.setContent(inner.getString("content"));
                        bean.setImage(inner.getString("image_url"));
                        news_arrayList.add(bean);
                    }
                    binding.LLNews.removeAllViews();
                    if (news_arrayList.size() > 0) {
                        for (int j = 0; j < 2; j++) {
                            ThreeBean b = news_arrayList.get(j);
                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                    (Context.LAYOUT_INFLATER_SERVICE);
                            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.card_news, null);
                            TextView TvTitle = (TextView) row.findViewById(R.id.TvTitle);
                            TvTitle.setId(j);
                            TvTitle.setText(b.getTitle());
                            TextView TvDescription = (TextView) row.findViewById(R.id.TvDescription);
                            TvDescription.setId(10 + j);
                            TvDescription.setText(b.getDescription());
                            ImageView Image = (ImageView) row.findViewById(R.id.image_small);
                            Image.setId(100 + j);
                            Glide.with(MainActivity.this)
                                    .load(b.getImage())
                                    .centerCrop()
                                    .into(Image);
                            binding.LLNews.addView(row, j);
                        }
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void ShowLogout() {
        Dialog _dialogg = new Dialog(MainActivity.this, R.style.SuccessFailureDialogTheme);
        _dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogg.setCancelable(false);
        _dialogg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialogg.setContentView(R.layout.logout);
        Window window = _dialogg.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _dialogg.show();

        Button BtnLogou = _dialogg.findViewById(R.id.BtnLogou);
        BtnLogou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogg.dismiss();
                sessionManager.logoutUser();

                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        Button BtnLogoutCancel = _dialogg.findViewById(R.id.BtnLogoutCancel);
        BtnLogoutCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogg.dismiss();
            }
        });
    }

    //gps

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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.trim().equalsIgnoreCase(FusionBroadCast.BROADCAST_ACTION)) {

                Bundle b = intent.getExtras();
                String lat = b.getString("Lat");
                String lon = b.getString("Lon");
                accuracy = b.getString("Accuracy");

                Helper.l("lat........" + lat + " : " + "lon........" + lon);
                Helper.l("accuracy........" + accuracy);


                if (Double.parseDouble(accuracy) <= 500) {

                    unregisterReceiver(broadcastReceiver);
                    Intent sIntent = new Intent();
                    sIntent.setAction(FusionBroadCast.ACTION_STOP);
                    sendBroadcast(sIntent);

                    Log.e("Accuracy reached", accuracy.toString());
                    if (!lat.equalsIgnoreCase(null) || !lat.equalsIgnoreCase("")
                            || !lon.equalsIgnoreCase(null) || !lon.equalsIgnoreCase("")) {
                        ViewLocation(lon, lat, Float.parseFloat(accuracy));
                    }

                } else {
                    Toast.makeText(context, "Accuracy is high " + String.valueOf(accuracy), Toast.LENGTH_SHORT).show();

                }
            }
        }

    };

    protected void onResume() {
        super.onResume();
        try {
            if (methodRequiresPermission(perms, 111)) {
                Helper.l("permissions granted");

                mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(FusionBroadCast.BROADCAST_ACTION);

                LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, mIntentFilter);
                registerReceiver(broadcastReceiver, mIntentFilter);
                startService(new Intent(MainActivity.this, FusionBroadCast.class));
                Helper.l("service start called");

            } else {
                Helper.t(getApplicationContext(), "Please Grant required app permissions!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ViewLocation(String lon, String lat, float parseFloat) {
        latitude = lat;
        longitude = lon;

        LatLng startPoint = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        LatLng endPoint = new LatLng(39.74607663364362, -84.17184284425018);
        double distance_calculated2 = distanceBetween(startPoint, endPoint);

        Helper.l("user latlng: " + latitude + " : " + longitude);
        Helper.l("distance 2: " + String.valueOf(distance_calculated2));

        if (distance_calculated2 < 100) {
           // new sendNotification().execute();
            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("username", sessionManager.getStrVal("Username"));
            GetDATA(2, params, "pushNotification/"+sessionManager.getStrVal("Username"), "post");
        }

    }
    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }
    private class sendNotification extends AsyncTask<Void, Void, String> {


        public sendNotification() {

        }

        @Override
        protected String doInBackground(Void... voids) {
            return postData();
        }

        private String postData() {
            String response = "default response";
            HttpRequest request = HttpRequest.post(notification_url);
            Log.e("request_status123", request.message());
            if (request.ok()) {
                System.out.println("Status was updated");
                response = request.body();
            } else {
                response = "app failed";
            }
            return response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        protected void onPostExecute(String response) {
            Log.e("response111", response);
            if (response.equalsIgnoreCase("app failed")) {
            } else {
              Helper.l("notification sent successfully"+"");
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return false;
    }
    private void GetDATA(int login, Map<String, String> params, String api_name, String req_method) {
        if (Helper.isNetworkAvailable(MainActivity.this)) {
            AuthKey.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        //{"result":"success","error":"","data":[{"username":"admin434"}],
                        // "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImFkbWluNDM0IiwiaWF0IjoxNjA1Njk3Mzc1LCJleHAiOjE2MDU4NzAxNzV9.R9bUS0yG7xzTf9sCHLDvjSdjD62B5NU--HtqiHqBUFY"}
                        if (login == 1) {
                            Helper.t(getApplicationContext(),response_.getString("message"));
//                            Handler h = new Handler();
//                            h.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
                            Map<String, String> params2 = new LinkedHashMap<String, String>();
                            params2.put("username", sessionManager.getStrVal("Username"));
                            GetDATA(2, params2, "pushNotification/"+sessionManager.getStrVal("Username"), "post");
//                            }
//                        }, 1000);
                        }else if (login == 2) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject apierror) {
                    try {
                        Helper.t(getApplicationContext(), apierror.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String message) {
                    Helper.t(getApplicationContext(), message);
                }

                @Override
                public void onException(String message) {
                    Helper.t(getApplicationContext(), message);
                }

                @Override
                public void onLogout(String messgae) {
                    Helper.t(getApplicationContext(), "Unable to login");
                }
            }, UrlBase.BASE_URL + api_name, params, MainActivity.this, "show", req_method);
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }


}