package com.example.eathealthy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eathealthy.Bean.NutritionDetailsBean;
import com.example.eathealthy.GetProductDetailswithBarcodeActivity;
import com.example.eathealthy.R;

import java.util.ArrayList;

public class NutritionDetailsAdapter extends RecyclerView.Adapter<NutritionDetailsAdapter.ViewHolder> {
    ArrayList<NutritionDetailsBean> arrayList;
    Context context;
    public NutritionDetailsAdapter(ArrayList<NutritionDetailsBean> arrayList, GetProductDetailswithBarcodeActivity context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_nuturition, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutritionDetailsBean bean = arrayList.get(position);
        holder.TvName.setText(bean.getName());
        holder.TvContent.setText(bean.getContent());
        holder.TvRiskLevel.setText(bean.getValue());
        if(bean.getValue().equalsIgnoreCase("high")) {
            holder.imgNutriLevel.setImageDrawable(context.getDrawable(R.drawable.round_high));
        }else if(bean.getValue().equalsIgnoreCase("low")) {
            holder.imgNutriLevel.setImageDrawable(context.getDrawable(R.drawable.round_normal));
        }else if(bean.getValue().equalsIgnoreCase("moderate")) {
            holder.imgNutriLevel.setImageDrawable(context.getDrawable(R.drawable.round_low));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNutriLevel;
        TextView TvName,TvContent,TvRiskLevel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNutriLevel = itemView.findViewById(R.id.imgNutriLevel);
            TvName = itemView.findViewById(R.id.TvName);
            TvContent = itemView.findViewById(R.id.TvContent);
            TvRiskLevel = itemView.findViewById(R.id.TvRiskLevel);
        }
    }
}
