package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityFeedbackBinding;

public class FeedbackAct extends AppCompatActivity {

    Context mContext = FeedbackAct.this;
    ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        itit();
    }

    private void itit() {
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HomeAct.class));
            finish();
        });
    }

}