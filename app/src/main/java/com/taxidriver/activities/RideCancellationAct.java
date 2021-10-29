package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityRideCancellationBinding;

public class RideCancellationAct extends AppCompatActivity {

    Context mContext = RideCancellationAct.this;
    ActivityRideCancellationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_cancellation);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            finish();
        });

    }


}