package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityRideCancellationBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
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

public class RideCancellationAct extends AppCompatActivity {

    Context mContext = RideCancellationAct.this;
    ActivityRideCancellationBinding binding;
    String requestId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_cancellation);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        requestId = getIntent().getStringExtra("id");

        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.reason1.setOnClickListener(v -> {
            binding.reason2.setChecked(false);
            binding.reason3.setChecked(false);
        });

        binding.reason2.setOnClickListener(v -> {
            binding.reason1.setChecked(false);
            binding.reason3.setChecked(false);
        });

        binding.reason3.setOnClickListener(v -> {
            binding.reason1.setChecked(false);
            binding.reason2.setChecked(false);
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (binding.reason1.isChecked()) {
                AcceptCancel("Rider Isn't Here");
            } else if (binding.reason2.isChecked()) {
                AcceptCancel("Wrong Address Shown");
            } else if (binding.reason3.isChecked()) {
                AcceptCancel("Don't Charge Rider");
            } else {
                Toast.makeText(mContext, getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void AcceptCancel(String reason) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", requestId);
        map.put("status", "Cancel");
        map.put("cancel_reaison", reason);
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
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        startActivity(new Intent(mContext, HomeV3CubeAct.class));
                        finish();
                    } else {
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

