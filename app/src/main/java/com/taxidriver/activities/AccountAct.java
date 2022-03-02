package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityAccountBinding;
import com.taxidriver.vehicles.ManageVehicleAct;
import com.taxidriver.vehicles.MyVehicleAct;

public class AccountAct extends AppCompatActivity {

    Context mContext = AccountAct.this;
    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        itit();
    }

    private void itit() {

        binding.cvVehicle.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ManageVehicleAct.class));
        });

        binding.cvDocument.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UpdateDocAct.class));
        });

        binding.cvEditAccount.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.cvInsurance.setOnClickListener(v -> {
            startActivity(new Intent(mContext, InsuranceAct.class));
        });

        binding.cvTaxInformation.setOnClickListener(v -> {
            startActivity(new Intent(mContext, InvoiceSettingAct.class));
        });

        binding.cvEarning.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EarningAct.class));
        });


    }


}