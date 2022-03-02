package com.taxidriver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityEndTripBinding;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndTripAct extends AppCompatActivity {

    Context mContext = EndTripAct.this;
    ActivityEndTripBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private ModelCurrentBooking data;
    ModelCurrentBookingResult result;
    String requestId;
    private String pickUp, dropOff, payType;
    double serviceTax, serviceTaxAmount, distanceCost, tollCost,
           otherCost, waitingCost, serviceCost, totalCostFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_trip);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        try {
            if (getIntent() != null) {
                data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                Log.e("dsfsdfdsf", "ModelCurrentBooking = " + new Gson().toJson(data));
                result = data.getResult().get(0);
                requestId = result.getId();

                Log.e("dsfsdfdsfAkash", "toll charge = " + result.getToll_charge());
                Log.e("dsfsdfdsfAkash", "other charge = " + result.getOther_charge());

                pickUp = result.getPicuplocation();
                dropOff = result.getDropofflocation();
                payType = result.getPaymentType();

                if ("POOL".equals(result.getBooktype())) {
                    binding.rlPassenger.setVisibility(View.VISIBLE);
                    binding.rlType.setVisibility(View.GONE);
                    binding.tvNoOfPassenger.setText(result.getBookedSeats());
                } else {
                    binding.rlPassenger.setVisibility(View.GONE);
                    binding.rlType.setVisibility(View.VISIBLE);
                }

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
                    totalCostFinal = serviceCost + distanceCost + tollCost + otherCost + waitingCost + serviceTaxAmount;
                } catch (Exception e) {
                    totalCostFinal = 0.0;
                }

                Log.e("dsfsdfdsf", "serviceCost = " + serviceCost);
                Log.e("dsfsdfdsf", "distanceCost = " + distanceCost);
                Log.e("dsfsdfdsf", "tollCost = " + tollCost);
                Log.e("dsfsdfdsf", "otherCost = " + otherCost);
                Log.e("dsfsdfdsf", "waitingCost = " + waitingCost);
                Log.e("dsfsdfdsf", "serviceTaxAmount = " + serviceTaxAmount);
                Log.e("dsfsdfdsf", "totalCostFinal = " + totalCostFinal);

                binding.tvFrom.setText(pickUp);
                binding.tvDestination.setText(dropOff);
                binding.tvServiceTax.setText("Tax@" + result.getService_tax() + "%");
                binding.tvTaxAmount.setText(AppConstant.CURRENCY + serviceCost);
                binding.tvDistanceCost.setText(AppConstant.CURRENCY + result.getAmount());
                binding.tvDistance.setText(result.getDistance() + " Km");
                binding.tvTollCharge.setText(AppConstant.CURRENCY + result.getToll_charge());
                binding.tvOtherCharge.setText(AppConstant.CURRENCY + result.getOther_charge());
                binding.tvWaitingCharge.setText(AppConstant.CURRENCY + result.getWaiting_time_amount());
                binding.tvTotalPay.setText(String.valueOf(totalCostFinal));
                binding.tvPayType.setText(result.getPaymentType().toUpperCase());

            }
        } catch (Exception e) {
        }

        itit();

    }

    private void itit() {

        binding.btnCollectPay.setOnClickListener(v -> {
            AcceptCancel("Finish");
        });

    }

    public void AcceptCancel(String status) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("status", status);
        map.put("cancel_reaison", "");
        map.put("timezone", TimeZone.getDefault().getID());

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.pauseProgressDialog();
                        ProjectUtil.clearNortifications(mContext);
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                        finishAffinity();
                        startActivity(new Intent(mContext, HomeV3CubeAct.class));
                    } else {
                        MyApplication.showToast(mContext, getString(R.string.req_cancelled));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });

    }


}