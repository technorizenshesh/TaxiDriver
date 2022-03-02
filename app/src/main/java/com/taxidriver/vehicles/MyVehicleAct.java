package com.taxidriver.vehicles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityMyVehicleBinding;
import com.taxidriver.databinding.AddVehicleDialogBinding;

public class MyVehicleAct extends AppCompatActivity {

    Context mContext = MyVehicleAct.this;
    ActivityMyVehicleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_vehicle);
        itit();
    }

    private void itit() {

        TabLayout.Tab rideTab = binding.tabLayout.newTab();
        rideTab.setText("Normal Vehicle");
        binding.tabLayout.addTab(rideTab);

        TabLayout.Tab carTab = binding.tabLayout.newTab();
        carTab.setText("Car Pooling");
        binding.tabLayout.addTab(carTab);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.ivAdd.setOnClickListener(v -> {
            addVehicleDialog();
        });

    }

    private void addVehicleDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        AddVehicleDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.add_vehicle_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnAdd.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


}