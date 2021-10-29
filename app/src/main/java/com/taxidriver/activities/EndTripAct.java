package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityEndTripBinding;

public class EndTripAct extends AppCompatActivity {

    Context mContext = EndTripAct.this;
    ActivityEndTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_end_trip);
        itit();
    }

    private void itit() {

        binding.btnCollectPay.setOnClickListener(v -> {
            startActivity(new Intent(mContext, FeedbackAct.class));
            finish();
        });

    }


}