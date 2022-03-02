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

public class AdapterMakeVehicle extends BaseAdapter {

    Context mContext;
    ArrayList<ModelMake.Result> makeList;

    public AdapterMakeVehicle(Context mContext, ArrayList<ModelMake.Result> makeList) {
        this.mContext = mContext;
        this.makeList = makeList;
    }

    @Override
    public int getCount() {
        return makeList == null ? 0 : makeList.size();
    }

    @Override
    public Object getItem(int position) {
        return makeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_make, parent,false);

        TextView tvMakeName = view.findViewById(R.id.tvMakeName);
        tvMakeName.setText(makeList.get(position).getTitle());

        tvMakeName.setOnClickListener(v -> {
            ((EditVehicleAct) mContext).getMakeId(makeList.get(position).getId(), makeList.get(position).getTitle());
        });

        return view;

    }

}
