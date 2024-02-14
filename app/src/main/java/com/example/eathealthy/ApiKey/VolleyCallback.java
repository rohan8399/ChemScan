package com.example.eathealthy.ApiKey;


import org.json.JSONObject;

public interface VolleyCallback{
    void onSuccess(JSONObject result);

    void onFailure(JSONObject apierror);

    void onError(String message);

    void onException(String message);

    void onLogout(String message);
}

