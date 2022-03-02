package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterOfferPool;
import com.taxidriver.databinding.ActivityOfferPoolListBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelPoolList;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferPoolListAct extends AppCompatActivity {

    Context mContext = OfferPoolListAct.this;
    ActivityOfferPoolListBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offer_pool_list);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        getPoolOffers();
        itit();
    }

    private void itit() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoolOffersNew();
            }
        });

        binding.btOfferPool.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddPoolOfferAct.class));
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPoolOffersNew();
    }

    private void getPoolOffers() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getOfferedPoolApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelPoolList modelPoolList = new Gson().fromJson(stringResponse, ModelPoolList.class);
                        AdapterOfferPool adapterOfferPool = new AdapterOfferPool(mContext, modelPoolList.getResult());
                        binding.rvPools.setAdapter(adapterOfferPool);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        AdapterOfferPool adapterOfferPool = new AdapterOfferPool(mContext, null);
                        binding.rvPools.setAdapter(adapterOfferPool);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });
    }

    private void getPoolOffersNew() {

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getOfferedPoolApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelPoolList modelPoolList = new Gson().fromJson(stringResponse, ModelPoolList.class);
                        AdapterOfferPool adapterOfferPool = new AdapterOfferPool(mContext, modelPoolList.getResult());
                        binding.rvPools.setAdapter(adapterOfferPool);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        AdapterOfferPool adapterOfferPool = new AdapterOfferPool(mContext, null);
                        binding.rvPools.setAdapter(adapterOfferPool);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });
    }

}