package com.taxidriver.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterUserFeedback;
import com.taxidriver.databinding.ActivityUserFeedbackBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelReviews;
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

public class UserFeedbackAct extends AppCompatActivity {

    Context mContext = UserFeedbackAct.this;
    ActivityUserFeedbackBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_feedback);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsersFeedback();
            }
        });

        getUsersFeedback();

    }

    private void getUsersFeedback() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Log.e("sadasddasd", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getUserReviews(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                            ModelReviews modelReviews = new Gson().fromJson(stringResponse, ModelReviews.class);

                            Glide.with(mContext).load(modelReviews.getImages())
                                    .placeholder(R.drawable.user_ic)
                                    .error(R.drawable.user_ic).into(binding.ivProfile);
                            binding.tvName.setText(modelReviews.getDriver_name());
                            binding.rbRating.setRating(Float.parseFloat(modelReviews.getAvg_rating()));

                            AdapterUserFeedback adapterRideHistory = new AdapterUserFeedback(mContext, modelReviews.getResult());
                            binding.rvUserReview.setAdapter(adapterRideHistory);

                        } else {
                            AdapterUserFeedback adapterRideHistory = new AdapterUserFeedback(mContext, null);
                            binding.rvUserReview.setAdapter(adapterRideHistory);
                            Toast.makeText(mContext, getString(R.string.no_review_found), Toast.LENGTH_LONG).show();
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
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

}