package com.example.eathealthy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eathealthy.Bean.ThreeBean;
import com.example.eathealthy.Common.SessionManager;
import com.example.eathealthy.databinding.ActivityHistoryBinding;
import com.example.eathealthy.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    SessionManager sessionManager;
    ActivityHistoryBinding binding;
    private static String news_url = "https://cloudcarbon.simpleitsolutions.co.in/history";
    private static String search_url = "https://cloudcarbon.simpleitsolutions.co.in/search/";
    ArrayList<ThreeBean> news_arrayList = new ArrayList<>();
    private String global_index = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InitViews();
    }

    private void InitViews() {
        sessionManager = new SessionManager(HistoryActivity.this);
        Intent i = getIntent();
        global_index  = i.getStringExtra("index");
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            }
        });
        if(global_index.equalsIgnoreCase("history")) {
            binding.TvUserName.setText("History");
            new NewsApi("history","").execute();
        }else{
            binding.TvUserName.setText("Search");
            binding.searchView.setVisibility(View.VISIBLE);
            new NewsApi("history","").execute();
        }
        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 2){
                    new NewsApi("search",s.toString()).execute();
                }
            }
        });
    }

    private class NewsApi extends AsyncTask<Void, Void, String> {
        String index_ = "",search_ = "";

        public NewsApi(String index,String search) {
            index_ = index;
            search_ = search;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return postData();
        }

        private String postData() {
            String response = "default response";
            HttpRequest request = null;
            if(index_.equalsIgnoreCase("history")) {
                 request = HttpRequest.get(news_url);
            }else{
                request = HttpRequest.get(search_url+search_);
            }
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
                    // JSONObject result1 = new JSONObject(response);
                    JSONArray data_array = new JSONArray(response);
                    news_arrayList.clear();
                    for (int i = 0; i < data_array.length(); i++) {
                        ThreeBean bean = new ThreeBean();
                        JSONObject inner = data_array.getJSONObject(i);
                        bean.setTitle(inner.getString("product_name"));
                        bean.setDescription(inner.getString("brand"));
                        bean.setQuality(inner.getString("grade"));
                        bean.setDate(inner.getString("days"));
                        bean.setImage(inner.getString("image_url"));
                        news_arrayList.add(bean);
                    }
                    if (news_arrayList.size() > 0) {
                        for (int j = 0; j < news_arrayList.size(); j++) {
                            ThreeBean b = news_arrayList.get(j);
                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                    (Context.LAYOUT_INFLATER_SERVICE);
                            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.card_history, null);
                            TextView TvTitle = (TextView) row.findViewById(R.id.TvProductName);
                            TvTitle.setId(j);
                            TvTitle.setText(b.getTitle());
                            TextView TvDescription = (TextView) row.findViewById(R.id.TvBrand);
                            TvDescription.setId(10 + j);
                            TvDescription.setText(b.getDescription());
                            TextView TvQuality = (TextView) row.findViewById(R.id.TvQuality);
                            TvQuality.setId(1000 + j);
                            ImageView ImageLevel = (ImageView) row.findViewById(R.id.imgNutriLevel);
                            ImageLevel.setId(000 + j);
                            if (b.getQuality().equalsIgnoreCase("a") || b.getQuality().equalsIgnoreCase("b")) {
                                TvQuality.setText("Good");
                                ImageLevel.setImageDrawable(getDrawable(R.drawable.round_normal));
                            } else if (b.getQuality().equalsIgnoreCase("c") || b.getQuality().equalsIgnoreCase("d")) {
                                TvQuality.setText("Poor");
                                ImageLevel.setImageDrawable(getDrawable(R.drawable.round_low));
                            } else {
                                TvQuality.setText("Bad");
                                ImageLevel.setImageDrawable(getDrawable(R.drawable.round_high));
                            }
                            TextView Tvdate = (TextView) row.findViewById(R.id.TvDate);
                            Tvdate.setId(00 + j);
                            if (b.getDate().equalsIgnoreCase("0")) {
                                Tvdate.setText("Today");
                            } else {
                                if(!b.getDate().isEmpty()) {
                                    int days = Integer.parseInt(b.getDate());
                                }
                                Tvdate.setText(b.getDate() + "day ago");
                            }
                            ImageView Image = (ImageView) row.findViewById(R.id.image_small);
                            Image.setId(100 + j);
                            if(!b.getImage().isEmpty()) {
                                Glide.with(HistoryActivity.this)
                                        .load(b.getImage())
                                        .centerCrop()
                                        .into(Image);
                            }
                            binding.LLNews.addView(row, j);
                        }
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(HistoryActivity.this,MainActivity.class));
        }
        return false;
    }


}