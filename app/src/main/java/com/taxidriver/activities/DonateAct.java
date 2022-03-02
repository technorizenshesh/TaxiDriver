package com.taxidriver.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterDonation;
import com.taxidriver.databinding.ActivityDonateBinding;
import com.taxidriver.databinding.AdapterDonateBinding;
import com.taxidriver.models.ModelDonation;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonateAct extends AppCompatActivity {

    Context mContext = DonateAct.this;
    ActivityDonateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donate);
        itit();
    }

    private void itit() {

        getDonationApi();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void getDonationApi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.gteDonationApiCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        Log.e("asfddasfasdf", "response = " + stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);
                            ModelDonation modelDonation = new Gson().fromJson(stringResponse,ModelDonation.class);
                            AdapterDonation adapterDonation = new AdapterDonation(mContext,modelDonation.getResult());
                            binding.rvDonate.setAdapter(adapterDonation);
                        } else {
                            AdapterDonation adapterDonation = new AdapterDonation(mContext,null);
                            binding.rvDonate.setAdapter(adapterDonation);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

}