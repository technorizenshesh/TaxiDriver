package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityCummunityGuideBinding;

public class CummunityGuideAct extends AppCompatActivity {

    Context mContext = CummunityGuideAct.this;
    ActivityCummunityGuideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cummunity_guide);
        itit();
    }

    private void itit() {

        binding.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HomeAct.class));
        });

    }

}