package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityInvoiceSettingBinding;

public class InvoiceSettingAct extends AppCompatActivity {

    Context mContext = InvoiceSettingAct.this;
    ActivityInvoiceSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_invoice_setting);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSave.setOnClickListener(v -> {
            finish();
        });

    }


}