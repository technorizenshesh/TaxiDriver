package com.taxidriver.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterOfferPool;
import com.taxidriver.adapters.AdapterUserRequest;
import com.taxidriver.databinding.ActivityCarPoolTrackBinding;
import com.taxidriver.models.ModelUserPoolRequests;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarPoolTrackAct extends AppCompatActivity {

    Context mContext = CarPoolTrackAct.this;
    ActivityCarPoolTrackBinding binding;
    String requestID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_pool_track);
        requestID = getIntent().getStringExtra("id");
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserRequestNew();
            }
        });

        getUserRequest();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserRequestNew();
    }

    public void getUserRequestNew() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("pool_request_id", requestID);

        Log.e("getUserRequestNew", "getUserRequestNew = " + map);

        Call<ResponseBody> call = api.getUserRequestsApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("getUserRequestNew", "getUserRequestNew = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelUserPoolRequests modelPoolList = new Gson().fromJson(stringResponse, ModelUserPoolRequests.class);
                        AdapterUserRequest adapterOfferPool = new AdapterUserRequest(mContext, modelPoolList.getResult());
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

    public void getUserRequest() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("pool_request_id", requestID);

        Call<ResponseBody> call = api.getUserRequestsApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("getUserRequest", "getUserRequest = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getUserRequest", " status = 1");
                        ModelUserPoolRequests modelPoolList = new Gson().fromJson(stringResponse, ModelUserPoolRequests.class);
                        AdapterUserRequest adapterOfferPool = new AdapterUserRequest(mContext, modelPoolList.getResult());
                        binding.rvPools.setAdapter(adapterOfferPool);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("getUserRequest", " status = 0");
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