package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityVerifyBinding;

public class VerifyAct extends AppCompatActivity {

    Context mContext = VerifyAct.this;
    ActivityVerifyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnVerify.setOnClickListener(v -> {
            startActivity(new Intent(mContext, DocumentAct.class));
        });

    }

}