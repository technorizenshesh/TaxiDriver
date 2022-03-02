package com.taxidriver.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.taxidriver.R;
import com.taxidriver.activities.AddPoolOfferAct;
import com.taxidriver.databinding.AdapterAdminAddressBinding;
import com.taxidriver.databinding.DialogAdminAddressBinding;
import com.taxidriver.models.ModelAddress;

import java.util.ArrayList;

public class AdapterAdminAddress extends RecyclerView.Adapter<AdapterAdminAddress.MyAddressHolder> {

    Context mContext;
    ArrayList<ModelAddress.Result> listAddress;
    String type;
    Dialog dialog;

    public AdapterAdminAddress(Context mContext, ArrayList<ModelAddress.Result> listAddress, String type, Dialog dialog) {
        this.mContext = mContext;
        this.listAddress = listAddress;
        this.type = type;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public MyAddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAdminAddressBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_admin_address, parent, false);
        return new MyAddressHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAddressHolder holder, int position) {

        ModelAddress.Result data = listAddress.get(position);
        holder.binding.tvPickup.setText(listAddress.get(position).getAddress());

        holder.binding.getRoot().setOnClickListener(v -> {
            if (type.equals("start")) {
                AddPoolOfferAct.binding.etStart.setText(data.getAddress());
                AddPoolOfferAct.latLngStart = new LatLng(Double.parseDouble(data.getLat())
                        , Double.parseDouble(data.getLon()));
                dialog.dismiss();
            } else if (type.equals("end")) {
                AddPoolOfferAct.binding.etEnd.setText(data.getAddress());
                AddPoolOfferAct.latLngEnd = new LatLng(Double.parseDouble(data.getLat())
                        , Double.parseDouble(data.getLon()));
                dialog.dismiss();
            } else if (type.equals("inter1")) {
                AddPoolOfferAct.binding.etInter1.setText(data.getAddress());
                AddPoolOfferAct.latLngInter1 = new LatLng(Double.parseDouble(data.getLat())
                        , Double.parseDouble(data.getLon()));
                dialog.dismiss();
            } else if (type.equals("inter2")) {
                AddPoolOfferAct.binding.etInter2.setText(data.getAddress());
                AddPoolOfferAct.latLngInter2 = new LatLng(Double.parseDouble(data.getLat())
                        , Double.parseDouble(data.getLon()));
                dialog.dismiss();
            } else if (type.equals("inter3")) {
                AddPoolOfferAct.binding.etInter3.setText(data.getAddress());
                AddPoolOfferAct.latLngInter3 = new LatLng(Double.parseDouble(data.getLat())
                        , Double.parseDouble(data.getLon()));
                dialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listAddress == null ? 0 : listAddress.size();
    }

    public class MyAddressHolder extends RecyclerView.ViewHolder {

        AdapterAdminAddressBinding binding;

        public MyAddressHolder(AdapterAdminAddressBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }


}
