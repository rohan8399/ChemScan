package com.example.eathealthy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Magnifier;

import com.bumptech.glide.Glide;
import com.example.eathealthy.Adapter.NutritionDetailsAdapter;
import com.example.eathealthy.Bean.NutritionDetailsBean;
import com.example.eathealthy.databinding.ActivityGetProductDetailswithBarcodeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

@RequiresApi(api = Build.VERSION_CODES.Q)

public class GetProductDetailswithBarcodeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityGetProductDetailswithBarcodeBinding binding;
    private String data = "";
    JSONObject jsonObject;
    ArrayList<NutritionDetailsBean> nutritionDetails_arrayList = new ArrayList<>();
    NutritionDetailsAdapter nutritionDetailsAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetProductDetailswithBarcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InitViews();
    }
    private void InitViews() {
        Intent i = getIntent();
        data = i.getStringExtra("data");
        try {
            jsonObject = new JSONObject(data);
            binding.TvProductName.setText(jsonObject.getString("product_name"));
            binding.TvProductBrand.setText(jsonObject.getString("brand"));
            if(jsonObject.getString("ingredients_text").equalsIgnoreCase("")) {
                binding.TvIngrediantsTitle.setVisibility(View.GONE);
                binding.TvIngrediants.setVisibility(View.GONE);
            }else {
                binding.TvIngrediantsTitle.setVisibility(View.VISIBLE);
                binding.TvIngrediants.setVisibility(View.VISIBLE);
                binding.TvIngrediants.setText(jsonObject.getString("ingredients_text"));
            }
            binding.TvAllergens.setText(jsonObject.getString("allergens"));
            binding.TvAdditives.setText(jsonObject.getString("additives"));
            if(jsonObject.getString("image_url").equalsIgnoreCase("")) {

            }else {
                Glide.with(GetProductDetailswithBarcodeActivity.this)
                        .load(jsonObject.getString("image_url"))
                        .centerCrop()
                        .into(binding.image);
                Glide.with(GetProductDetailswithBarcodeActivity.this)
                        .load(jsonObject.getString("image_url"))
                        .centerCrop()
                        .into(binding.imageSmall);
            }
            if(jsonObject.getString("nutriscore_grade").equalsIgnoreCase("a")) {
                binding.ImgNutriScore.setImageDrawable(getDrawable(R.drawable.a));
            }else if(jsonObject.getString("nutriscore_grade").equalsIgnoreCase("b")) {
                binding.ImgNutriScore.setImageDrawable(getDrawable(R.drawable.b));
            }else if(jsonObject.getString("nutriscore_grade").equalsIgnoreCase("c")) {
                binding.ImgNutriScore.setImageDrawable(getDrawable(R.drawable.c));
            }else if(jsonObject.getString("nutriscore_grade").equalsIgnoreCase("d")) {
                binding.ImgNutriScore.setImageDrawable(getDrawable(R.drawable.d));
            }else if(jsonObject.getString("nutriscore_grade").equalsIgnoreCase("e")) {
                binding.ImgNutriScore.setImageDrawable(getDrawable(R.drawable.e));
            }else {
                binding.LLNutriScore.setVisibility(View.GONE);
            }
          //  if(!(jsonObject.getString("nutrient_levels").equalsIgnoreCase(""))) {
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("nutrient_levels"));
                if (jsonObject1.length() > 0) {
                    Iterator<?> keys = jsonObject1.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        String value = (String) jsonObject1.get(key);

                        JSONObject jsonObject2 = new JSONObject(jsonObject.getString("nutriments"));
                        if (jsonObject2.has(key)) {
                            String content = jsonObject2.getString(key);
                            NutritionDetailsBean bean = new NutritionDetailsBean();
                            bean.setName(key);
                            bean.setContent(content);
                            bean.setValue(value);
                            nutritionDetails_arrayList.add(bean);
                        }
                    }
                }
                if (nutritionDetails_arrayList.size() > 0) {
                    nutritionDetailsAdapter = new NutritionDetailsAdapter(nutritionDetails_arrayList, GetProductDetailswithBarcodeActivity.this);
                    layoutManager = new LinearLayoutManager(GetProductDetailswithBarcodeActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    binding.rvTBCases.setLayoutManager(layoutManager);
                    binding.rvTBCases.setAdapter(nutritionDetailsAdapter);
                    nutritionDetailsAdapter.notifyDataSetChanged();
                }else{
                    binding.LLNutriFacts.setVisibility(View.GONE);
                }
          //  }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        binding.BtnNext.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
//        Magnifier magnifier = new Magnifier.Builder(binding.TvGradeD).build();
//        magnifier.setZoom(10);
//        magnifier.show(binding.TvGradeD.getWidth(), binding.TvGradeD.getHeight());

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BtnNext:
                finish();
                startActivity(new Intent(GetProductDetailswithBarcodeActivity.this,ReportsActivity.class)
                        .putExtra("data",data));
                break;
            case R.id.imgBack:
                finish();
                startActivity(new Intent(GetProductDetailswithBarcodeActivity.this,MainActivity.class));
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            startActivity(new Intent(GetProductDetailswithBarcodeActivity.this,MainActivity.class));
        }
        return false;
    }

}