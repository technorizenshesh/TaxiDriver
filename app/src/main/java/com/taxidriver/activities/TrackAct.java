package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityTrackBinding;
import com.taxidriver.databinding.TripStatusDailogBinding;

public class TrackAct extends AppCompatActivity {

    Context mContext = TrackAct.this;
    ActivityTrackBinding binding;
    String status = "Start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track);
        itit();
    }

    private void itit() {

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext,RideCancellationAct.class));
        });

        binding.icCall.setOnClickListener(v -> {
            //  finish();
        });

        binding.btnStatus.setOnClickListener(v -> {
            if(status.equals("Start")) {
                startEndTripDialog("Are you sure you want to start the trip?");
            } else {
                startEndTripDialog("Are you sure you want to finish the trip?");
            }
        });

    }

    private void startEndTripDialog(String text) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        TripStatusDailogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.trip_status_dailog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvText.setText(text);

        dialogBinding.btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            if(status.equals("Start")) {
                status = "finish";
                binding.btnStatus.setText("Finish");
            } else {
                startActivity(new Intent(mContext,EndTripAct.class));
                finish();
            }
        });

        dialogBinding.btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}