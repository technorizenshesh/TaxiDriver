package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityOfferPoolBinding;

public class OfferPoolAct extends AppCompatActivity {

    Context mContext = OfferPoolAct.this;
    ActivityOfferPoolBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_offer_pool);
        itit();
    }

    private void itit() {

        binding.btOfferPool.setOnClickListener(v -> {
            finish();
        });

    }

}