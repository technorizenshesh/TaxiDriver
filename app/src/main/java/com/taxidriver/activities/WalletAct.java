package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityWalletBinding;

public class WalletAct extends AppCompatActivity {

    Context mContext = WalletAct.this;
    ActivityWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvRecharge.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletRechargeAct.class));
        });

    }


}