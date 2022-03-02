package com.taxidriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taxidriver.R;
import com.taxidriver.models.ModelMake;
import com.taxidriver.vehicles.EditVehicleAct;

import java.util.ArrayList;

public class AdapterModelVehicle extends BaseAdapter {

    Context mContext;
    ArrayList<ModelMake.Result> modelList;

    public AdapterModelVehicle(Context mContext, ArrayList<ModelMake.Result> modelList) {
        this.mContext = mContext;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList == null ? 0 : modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_make, parent,false);

        TextView tvMakeName = view.findViewById(R.id.tvMakeName);
        tvMakeName.setText(modelList.get(position).getTitle());

        tvMakeName.setOnClickListener(v -> {
            ((EditVehicleAct) mContext).getModelId(modelList.get(position).getId(), modelList.get(position).getTitle());
        });

        return view;

    }

}
