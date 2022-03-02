package com.taxidriver.vehicles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterManageVehicle;
import com.taxidriver.databinding.ActivityManageVehicleBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelVehicle;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicleAct extends AppCompatActivity {

    Context mContext = ManageVehicleAct.this;
    ActivityManageVehicleBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_vehicle);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getVehicles();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.addVehicle.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddVehicleAct.class)
                    .putExtra("type", "manage")
            );
        });

    }

    private void getVehicles() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getVehicleListApi(paramHash);
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

                            ModelVehicle modelVehicle = new Gson().fromJson(stringResponse, ModelVehicle.class);
                            AdapterManageVehicle adapterManageVehicle = new AdapterManageVehicle(mContext, modelVehicle.getResult());
                            binding.rvVehicle.setAdapter(adapterManageVehicle);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_vehicle_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onResume() {
        super.onResume();
        getVehicleNew();
    }

    private void getVehicleNew(){

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getVehicleListApi(paramHash);
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

                            ModelVehicle modelVehicle = new Gson().fromJson(stringResponse, ModelVehicle.class);
                            AdapterManageVehicle adapterManageVehicle = new AdapterManageVehicle(mContext, modelVehicle.getResult());
                            binding.rvVehicle.setAdapter(adapterManageVehicle);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_vehicle_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

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