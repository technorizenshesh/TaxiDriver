package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityRideDetailsBinding;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelHistory;
import com.taxidriver.utils.AppConstant;

public class RideDetailsAct extends AppCompatActivity {

    Context mContext = RideDetailsAct.this;
    ActivityRideDetailsBinding binding;
    ModelHistory.Result dataHistory;
    ModelCurrentBooking data;
    ModelCurrentBookingResult result;
    private String pickUp, dropOff, payType;
    double serviceTax, distanceCost, tollCost, otherCost, waitingCost, serviceCost, totalCostFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_details);
        data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
        dataHistory = (ModelHistory.Result) getIntent().getSerializableExtra("datahistory");
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        result = data.getResult().get(0);

        Log.e("dsfsdfdsf", "requestId = " + result.getId());
        pickUp = result.getPicuplocation();
        dropOff = result.getDropofflocation();
        payType = result.getPaymentType();

//        if ("POOL".equals(result.getBooktype())) {
//            binding.rlPassenger.setVisibility(View.VISIBLE);
//            binding.rlType.setVisibility(View.GONE);
//            binding.tvNoOfPassenger.setText(result.getBookedSeats());
//        } else {
//            binding.rlPassenger.setVisibility(View.GONE);
//            binding.rlType.setVisibility(View.VISIBLE);
//        }

        binding.tvMobile.setText(dataHistory.getUser_details().get(0).getMobile());
        binding.tvEmail.setText(dataHistory.getUser_details().get(0).getEmail());
        binding.tvDriverName.setText(dataHistory.getUser_details().get(0).getUser_name());
        Glide.with(mContext)
                .load(dataHistory.getUser_details().get(0).getProfile_image())
                .placeholder(R.drawable.user_ic).error(R.drawable.user_ic).into(binding.ivDriverPic);

        binding.tvDateTime.setText(dataHistory.getAccept_time());
        binding.tvPayType.setText(dataHistory.getPayment_type().toUpperCase());
        binding.tvStatus.setText(dataHistory.getStatus());
        binding.tvFrom.setText(dataHistory.getPicuplocation());
        binding.etDestination.setText(dataHistory.getDropofflocation());

        try {
            serviceTax = Double.parseDouble(result.getService_tax());
        } catch (Exception e) {
            serviceTax = 0.0;
        }

        try {
            distanceCost = Double.parseDouble(result.getAmount());
        } catch (Exception e) {
            distanceCost = 0.0;
        }

        try {
            tollCost = Double.parseDouble(result.getToll_charge());
        } catch (Exception e) {
            tollCost = 0.0;
        }

        try {
            otherCost = Double.parseDouble(result.getOther_charge());
        } catch (Exception e) {
            otherCost = 0.0;
        }

        try {
            waitingCost = Double.parseDouble(result.getWaiting_time_amount());
        } catch (Exception e) {
            waitingCost = 0.0;
        }

        try {
            serviceCost = (distanceCost * Integer.parseInt(result.getService_tax())) / 100.0;
        } catch (Exception e) {
            serviceCost = 0.0;
        }

        try {
            totalCostFinal = serviceCost + distanceCost + tollCost + otherCost + waitingCost;
        } catch (Exception e) {
            totalCostFinal = 0.0;
        }

        Log.e("dsfsdfdsf", "serviceCost = " + serviceCost);
        Log.e("dsfsdfdsf", "distanceCost = " + distanceCost);
        Log.e("dsfsdfdsf", "tollCost = " + tollCost);
        Log.e("dsfsdfdsf", "otherCost = " + otherCost);
        Log.e("dsfsdfdsf", "waitingCost = " + waitingCost);
        Log.e("dsfsdfdsf", "totalCostFinal = " + totalCostFinal);

        binding.tvFrom.setText(pickUp);
        binding.etDestination.setText(dropOff);
        binding.tvServiceTax.setText("Tax@" + result.getService_tax() + "%");
        binding.tvTaxAmount.setText(AppConstant.CURRENCY + serviceCost);
        binding.tvDistanceCost.setText(AppConstant.CURRENCY + result.getAmount());
        binding.tvDistance.setText(result.getDistance() + " Km");
        binding.tvTollCharge.setText(AppConstant.CURRENCY + result.getToll_charge());
        binding.tvOtherCharge.setText(AppConstant.CURRENCY + result.getOther_charge());
        binding.tvWaitingCharge.setText(AppConstant.CURRENCY + result.getWaiting_time_amount());
        binding.tvTotalPay.setText(String.valueOf(totalCostFinal));
        binding.tvPayType.setText(result.getPaymentType().toUpperCase());
        binding.tvMobile.setText(dataHistory.getUser_details().get(0).getMobile());
        binding.tvEmail.setText(dataHistory.getUser_details().get(0).getEmail());
        binding.tvDriverName.setText(dataHistory.getUser_details().get(0).getUser_name());

        Glide.with(mContext)
                .load(dataHistory.getUser_details().get(0).getProfile_image())
                .placeholder(R.drawable.user_ic).error(R.drawable.user_ic).into(binding.ivDriverPic);

        binding.tvPayType.setText(dataHistory.getPayment_type().toUpperCase());
        binding.tvFrom.setText(dataHistory.getPicuplocation());
        binding.etDestination.setText(dataHistory.getDropofflocation());

//        if (dataHistory.getBooktype().equals("POOL")) {
//            binding.tvPoolText.setVisibility(View.VISIBLE);
//            binding.tvPoolText.setText("Pool Booking For " + dataHistory.getBooked_seats() + " Passenger");
//        } else {
//            binding.tvPoolText.setVisibility(View.GONE);
//        }


    }


}