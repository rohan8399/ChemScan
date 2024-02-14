package com.example.eathealthy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eathealthy.Bean.NutritionDetailsBean;
import com.example.eathealthy.Bean.ThreeBean;
import com.example.eathealthy.Common.Helper;
import com.example.eathealthy.databinding.ActivityReportsBinding;
import com.example.eathealthy.http.HttpRequest;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityReportsBinding binding;
    private static String graph_url = "https://cloudcarbon.simpleitsolutions.co.in/graph";
    ArrayList<ThreeBean> graphPar_arrayList = new ArrayList<>();
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.ImgShare.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
        new reportsApi().execute();

//        pieDataSet = new PieDataSet(pieEntries, "");
//        pieData = new PieData(pieDataSet);
//        pieData.setDrawValues(true);
//        binding.PieChart.setData(pieData);
//        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        pieDataSet.setSliceSpace(2f);
//        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setValueLinePart1OffsetPercentage(100f);
//        pieDataSet.setValueLinePart1Length(0.8f);
//        pieDataSet.setValueLinePart2Length(0f);
//        pieDataSet.setValueTextColor(Color.BLACK);
//        pieDataSet.setValueTextSize(14f);
//        pieDataSet.setSliceSpace(2f);
//        binding.PieChart.getDescription().setEnabled(false);
//        binding.PieChart.setDrawEntryLabels(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImgShare:
                ShareScreenshot();
                break;
            case R.id.imgBack:
                finish();
                startActivity(new Intent(ReportsActivity.this,MainActivity.class));
                break;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(ReportsActivity.this,MainActivity.class));
        }
        return false;
    }


    private void ShareScreenshot() {
        // Get a reference to the root view of your app's main activity
        View rootView = getWindow().getDecorView().getRootView();

// Create a Bitmap object to hold the screenshot
        Bitmap screenshot = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);

// Create a Canvas object and draw the screenshot onto it
        Canvas canvas = new Canvas(screenshot);
        rootView.draw(canvas);

        String file_name = "screenshot.png";
        File photoFile = getPhotoFileUri(file_name);

// Save the screenshot to a file (optional)
        try {
            FileOutputStream fos = new FileOutputStream(photoFile.getAbsolutePath());
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
// Share the screenshot through a social media application
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(),
                BuildConfig.APPLICATION_ID + ".provider", photoFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileProvider);
        startActivity(Intent.createChooser(shareIntent, "Share screenshot"));
    }

    private class reportsApi extends AsyncTask<Void, Void, String> {

        public reportsApi() {

        }

        @Override
        protected String doInBackground(Void... voids) {
            return postData();
        }

        private String postData() {
            String response = "default response";
            HttpRequest request = HttpRequest.get(graph_url);
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
                    JSONObject data_array = new JSONObject(response);
                    Iterator<?> keys = data_array.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if((data_array.get(key) != null) || ((data_array.get(key) != "null"))) {
                            String value = (String) data_array.get(key);
                            ThreeBean bean = new ThreeBean();
                            bean.setTitle(key);
                            bean.setContent(value);
                            graphPar_arrayList.add(bean);
                        }
                    }
                    if (graphPar_arrayList.size() > 0) {
                        pieEntries = new ArrayList<>();
                        for (int i = 0; i < graphPar_arrayList.size(); i++) {
                            ThreeBean bean1 = graphPar_arrayList.get(i);
                            pieEntries.add(new PieEntry(Float.parseFloat(bean1.getContent()), bean1.getTitle()));
                        }
                        int[] colors = {Color.parseColor("#fd4431"),Color.parseColor("#2ec56e"),Color.parseColor("#F7B731"),
                        Color.parseColor("#FFBB86FC"),Color.parseColor("#FF03DAC5"),Color.parseColor("#B71C1C"),
                                Color.parseColor("#1D9BF0"),Color.parseColor("#097138"),Color.parseColor("#3c4a6f"),
                        Color.parseColor("#e0e0e0")};
                        pieDataSet = new PieDataSet(pieEntries, "");
                        pieDataSet.setColors(colors);
//                            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//                            pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//                            pieDataSet.setValueLinePart1OffsetPercentage(100f);
//                            pieDataSet.setValueLinePart1Length(0.8f);
//                            pieDataSet.setValueLinePart2Length(0f);
                        pieDataSet.setValueTextColor(Color.BLACK);
                        pieDataSet.setValueTextSize(10f);
                        pieData = new PieData(pieDataSet);
                        pieData.setDrawValues(true);
                        binding.PieChart.setData(pieData);
                        binding.PieChart.getDescription().setEnabled(false);
                        binding.PieChart.setDrawEntryLabels(false);
                        binding.PieChart.invalidate();
                        binding.PieChart.setUsePercentValues(true);
                        pieDataSet.setSliceSpace(2f);
                        binding.PieChart.setRotationEnabled(false);
                      //  binding.PieChart.setRotationAngle(0);

//                            Legend l = binding.PieChart.getLegend();
//                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//                            l.setOrientation(Legend.LegendOrientation.VERTICAL);
//                            l.setTextSize(18f);
//                            l.setEnabled(true);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "APP_TAG");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("APP_TAG", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

}