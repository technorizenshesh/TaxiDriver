package com.taxidriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxidriver.R;
import com.taxidriver.activities.CarPoolTrackAct;
import com.taxidriver.databinding.AdapterOoferPoolBinding;
import com.taxidriver.models.ModelPoolList;

import java.util.ArrayList;

public class AdapterOfferPool extends RecyclerView.Adapter<AdapterOfferPool.MyRideHolder> {

    Context mContext;
    ArrayList<ModelPoolList.Result> poolList;

    public AdapterOfferPool(Context mContext, ArrayList<ModelPoolList.Result> poolList) {
        this.mContext = mContext;
        this.poolList = poolList;
    }

    @NonNull
    @Override
    public AdapterOfferPool.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterOoferPoolBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_oofer_pool, parent, false);
        return new AdapterOfferPool.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOfferPool.MyRideHolder holder, int position) {

        ModelPoolList.Result data = poolList.get(position);

        holder.binding.tvPickup.setText(data.getStart_location());
        holder.binding.tvDrop.setText(data.getEnd_location());
        holder.binding.tvSeats.setText(data.getSeats_offer() + " Seats");
        holder.binding.tvDateTime.setText("Date Time \n" + data.getDate() + " " + data.getTime());

        holder.binding.tvCarNumber.setText(data.getCar_number());
        holder.binding.tvCharge.setText(data.getCharge_per_km() + "/Km");

        if ("Approve".equals(data.getStatus())) {
            holder.binding.btnUserRequests.setVisibility(View.VISIBLE);
            holder.binding.tvStatus.setText(R.string.approved_by_admin);
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green_spalsh));
        } else {
            holder.binding.btnUserRequests.setVisibility(View.GONE);
            holder.binding.tvStatus.setText(R.string.offer_not_approved_by_admin);
        }

        if (TextUtils.isEmpty(data.getStop_1())) {
            holder.binding.tvStop1.setVisibility(View.GONE);
        } else {
            holder.binding.tvStop1.setText(data.getStop_1());
        }

        if (TextUtils.isEmpty(data.getStop_2())) {
            holder.binding.tvStop2.setVisibility(View.GONE);
        } else {
            holder.binding.tvStop2.setText(data.getStop_2());
        }

        if (TextUtils.isEmpty(data.getStop_3())) {
            holder.binding.tvStop3.setVisibility(View.GONE);
        } else {
            holder.binding.tvStop3.setText(data.getStop_3());
        }

        holder.binding.btnUserRequests.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, CarPoolTrackAct.class)
                    .putExtra("id", data.getId())
            );
        });

        if (TextUtils.isEmpty(data.getStop_1())) {
            if (TextUtils.isEmpty(data.getStop_2())) {
                if (TextUtils.isEmpty(data.getStop_3())) {
                    holder.binding.Stops.setVisibility(View.GONE);
                }
            }
        } else {
            holder.binding.Stops.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return poolList == null ? 0 : poolList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterOoferPoolBinding binding;

        public MyRideHolder(@NonNull AdapterOoferPoolBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
