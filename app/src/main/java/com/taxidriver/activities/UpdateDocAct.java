package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityUpdateDocBinding;

public class UpdateDocAct extends AppCompatActivity {

    Context mContext = UpdateDocAct.this;
    ActivityUpdateDocBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update_doc);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSave.setOnClickListener(v -> {
            finish();
        });

    }



}