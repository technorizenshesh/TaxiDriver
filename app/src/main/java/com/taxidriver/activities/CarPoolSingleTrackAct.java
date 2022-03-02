package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityCarPoolSingleTrackBinding;

public class CarPoolSingleTrackAct extends AppCompatActivity {

    Context mContext = CarPoolSingleTrackAct.this;
    ActivityCarPoolSingleTrackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_car_pool_single_track);
        itit();
    }

    private void itit() {



    }


}