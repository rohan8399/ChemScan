package com.example.eathealthy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.eathealthy.ApiKey.AuthKey;
import com.example.eathealthy.ApiKey.VolleyCallback;
import com.example.eathealthy.Common.Helper;
import com.example.eathealthy.Common.SessionManager;
import com.example.eathealthy.URL.UrlBase;
import com.example.eathealthy.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    SessionManager sessionManager;
    String fcm_token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.TvSignin.setOnClickListener(this);
        sessionManager = new SessionManager(LoginActivity.this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                         fcm_token = task.getResult();
                        Helper.l("fcm_token"+fcm_token);

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d("TAG", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


//        if(sessionManager.isLoggedIn()) {
//            finish();
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TvSignin:
                String username = binding.EtUserName.getText().toString().trim();
                String password = binding.EtPassword.getText().toString().trim();
                if (username.isEmpty()) {
                    Helper.t(getApplicationContext(), "Please enter username");
                } else if (password.isEmpty()) {
                    Helper.t(getApplicationContext(), "Please enter password");
                } else if ((!username.equalsIgnoreCase("rohan")) || (!password.equals("roHan@123"))) {
                    Helper.t(getApplicationContext(), "Invalid Username or Password");
                } else {
                    try {
                        JSONObject input = new JSONObject();
                        input.put("username", username);
                        input.put("pwd", password);
                        sessionManager.createLoginSession(input);

                        Map<String, String> params = new LinkedHashMap<String, String>();
                        params.put("token", fcm_token);
                        params.put("username", username);
                        GetDATA(2, params, "saveFcmKey", "PATCH");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
                break;
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return false;
    }
    private void GetDATA(int login, Map<String, String> params, String api_name, String req_method) {
        if (Helper.isNetworkAvailable(LoginActivity.this)) {
            AuthKey.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                        //{"result":"success","error":"","data":[{"username":"admin434"}],
                        // "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImFkbWluNDM0IiwiaWF0IjoxNjA1Njk3Mzc1LCJleHAiOjE2MDU4NzAxNzV9.R9bUS0yG7xzTf9sCHLDvjSdjD62B5NU--HtqiHqBUFY"}

                        if (login == 1) {
                            JSONArray data_array = response_.getJSONArray("data");
                            if (data_array.length() > 0) {
                                JSONObject inner = data_array.getJSONObject(0);
                                sessionManager.createLoginSession(inner);
                                //go to registration

                            }
//                            Map<String, String> params = new LinkedHashMap<String, String>();
//                            params.put("token", sessionManager.getStrVal(Helper.FcmKey));
//                            GetDATA(2, params, "saveFcmKey", "PATCH");
                        } else if (login == 2) {
                            Helper.t(getApplicationContext(),response_.getString("message"));
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
            }, UrlBase.BASE_URL + api_name, params, LoginActivity.this, "show", req_method);
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }


}