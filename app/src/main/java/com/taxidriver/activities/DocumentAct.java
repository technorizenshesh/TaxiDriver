package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityDocumentBinding;
import com.taxidriver.vehicles.AddVehicleAct;

public class DocumentAct extends AppCompatActivity {

    Context mContext = DocumentAct.this;
    ActivityDocumentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_document);
        itit();
    }

    private void itit() {

        binding.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddVehicleAct.class));
        });

    }


}