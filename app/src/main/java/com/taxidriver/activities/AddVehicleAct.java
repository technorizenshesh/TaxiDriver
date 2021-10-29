package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityAddVehicleBinding;

public class AddVehicleAct extends AppCompatActivity {

    Context mContext = AddVehicleAct.this;
    ActivityAddVehicleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_vehicle);
        itit();
    }

    private void itit() {

        binding.btnDone.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CummunityGuideAct.class));
        });

    }


}