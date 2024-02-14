package com.example.eathealthy.FireBase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.eathealthy.Common.Helper;
import com.example.eathealthy.Common.SessionManager;
import com.example.eathealthy.MainActivity;
import com.example.eathealthy.R;
import com.example.eathealthy.URL.UrlBase;
import com.example.eathealthy.ApiKey.AuthKey;
import com.example.eathealthy.ApiKey.VolleyCallback;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {
    String TAG = "FCM";
    String NOTIFICATION_CHANNEL_ID = "com.entrolabs.eathealthy";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String Title = remoteMessage.getNotification().getTitle();
            String Body = remoteMessage.getNotification().getBody();
            //String image_path = remoteMessage.getData().get("image");


            Helper.l("From: " + remoteMessage.getFrom());
            Helper.l("title and body : " + Title + " : " + Body);
            Helper.l("Message data payload: " + remoteMessage.getData());
          //  ShowNotification(Title, Body, image_path, remoteMessage.getData().get("ticketid"));

        } else {
            Helper.l("data is empty");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Helper.l("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void ShowNotification(String title, String body, String image_path, String ticketid) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ticket", ticketid);
        intent.putExtra("class", "fcm");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("EDMT Channel");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }

//        Bitmap bmp = null;
//        try {
//            //image_path
//            InputStream in = new URL(image_path).openStream();
////            InputStream in = new URL("http://fdapi.bingy.in/OfferBanners/1211_food1.jpeg").openStream();
//            bmp = BitmapFactory.decodeStream(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.release_icon_transparent)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentInfo("data")
//                        .setStyle(new NotificationCompat.BigPictureStyle()
//                                .bigPicture(bmp))
                        .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Helper.l("genrated token : " + s);

        sendRegistrationToServer(s.trim().replaceAll("\\s", ""));
    }

    private void sendRegistrationToServer(String s) {

        Helper.l("genrated token : " + s);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.storeVal("fcm_token", s);
        Map<String, String> params = new LinkedHashMap<String, String>();
//{
//    "fcm" : 1,
//     "mobile" : "7288877947",
//     "user_token" : "asdasdasdasdasdasdasd"
//}
        params.put("token", String.valueOf(s).trim());
      //  GetDATA(1, params,"saveFcmKey");
    }


    //Simple method for image downloading
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void GetDATA(int login, Map<String, String> params,String api_name) {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            AuthKey.VolleyCallBack(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response_) {
                    try {
                      //  Helper.t(getApplicationContext(), "fcm : " + response_.toString());
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
                public void onLogout(String message) {
                    Helper.t(getApplicationContext(), message);
                }
            }, UrlBase.BASE_URL+api_name, params, getApplicationContext(), "show","PATCH");
        } else {
            Helper.t(getApplicationContext(), "Need internet connection");
        }

    }



}
