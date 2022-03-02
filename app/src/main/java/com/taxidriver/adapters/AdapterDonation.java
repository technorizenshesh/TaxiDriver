package com.taxidriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxidriver.R;
import com.taxidriver.databinding.AdapterDonateBinding;
import com.taxidriver.models.ModelDonation;

import java.util.ArrayList;

public class AdapterDonation extends RecyclerView.Adapter<AdapterDonation.DonationHolder> {

    Context mContext;
    ArrayList<ModelDonation.Result> donationList;

    public AdapterDonation(Context mContext, ArrayList<ModelDonation.Result> donationList) {
        this.mContext = mContext;
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterDonateBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_donate, parent, false);
        return new DonationHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationHolder holder, int position) {

        holder.binding.setData(donationList.get(position));

        holder.binding.btnDonate.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(donationList.get(position).getLink()));
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return donationList == null ? 0 : donationList.size();
    }

    public class DonationHolder extends RecyclerView.ViewHolder {

        AdapterDonateBinding binding;

        public DonationHolder(@NonNull AdapterDonateBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }

}
