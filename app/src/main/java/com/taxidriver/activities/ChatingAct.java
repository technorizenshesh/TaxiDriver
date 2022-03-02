package com.taxidriver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterAllChats;
import com.taxidriver.databinding.ActivityChatingBinding;
import com.taxidriver.models.ModelChating;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.BottomReachedInterface;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatingAct extends AppCompatActivity implements BottomReachedInterface {

    Context mContext = ChatingAct.this;
    ActivityChatingBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelChating modelAllchats;
    ArrayList<ModelChating.Result> allChatList;
    AdapterAllChats adapterAllChats;
    private Timer timer;
    boolean isBottom;
    String timezoneID = TimeZone.getDefault().getID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chating);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getAllChats();

        binding.ivSendMsg.setOnClickListener(v -> {
            if (!binding.etSendMsg.getText().toString().trim().isEmpty()) {
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(50);
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                binding.ivSendMsg.startAnimation(anim);
                sendMessageApi(binding.etSendMsg.getText().toString().trim());
                binding.etSendMsg.setText("");
            } else {
                Toast.makeText(mContext, getString(R.string.please_write_a_message), Toast.LENGTH_SHORT).show();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            try {
                timer.cancel();
            } catch (Exception e) {
            }
            finish();
            startActivity(new Intent(mContext, HomeV3CubeAct.class));
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(mContext, HomeV3CubeAct.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isLastVisible()) {
                    getAllChat2();
                }
            }
        }, 0, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    private boolean isLastVisible() {

        if (modelAllchats != null) {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) binding.rvChating.getLayoutManager());
            int pos = layoutManager.findLastCompletelyVisibleItemPosition();
            int numItems = binding.rvChating.getAdapter().getItemCount();
            return (pos >= numItems - 1);
        }

        return false;

    }

    private void getAllChat2() {

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("sender_id", modelLogin.getResult().getId());
        params.put("receiver_id", "1");

        Call<ResponseBody> call = api.getConversationApiCAll(params);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {

                    String stringResponse = response.body().string();

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        modelAllchats = new Gson().fromJson(stringResponse, ModelChating.class);
                        allChatList = modelAllchats.getResult();

                        Log.e("stringResponse", "stringResponse = " + stringResponse);
                        Log.e("stringResponse", "response = " + response);

                        try {
                            binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);
                        } catch (Exception e) {
                        }

                        adapterAllChats = new AdapterAllChats(mContext, allChatList);
                        binding.rvChating.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvChating.setAdapter(adapterAllChats);

                        binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);

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

    private void getAllChats() {

        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        HashMap<String, String> params = new HashMap<>();
        params.put("sender_id", modelLogin.getResult().getId());
        params.put("receiver_id", "1");

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getConversationApiCAll(params);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {

                    String stringResponse = response.body().string();

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("stringResponse", "stringResponse = " + stringResponse);
                    Log.e("stringResponse", "response = " + response);

                    if (jsonObject.getString("status").equals("1")) {

                        modelAllchats = new Gson().fromJson(stringResponse, ModelChating.class);
                        allChatList = modelAllchats.getResult();

                        try {
                            binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);
                        } catch (Exception e) {

                        }

                        adapterAllChats = new AdapterAllChats(mContext, modelAllchats.getResult());
                        binding.rvChating.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvChating.setAdapter(adapterAllChats);

                        binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);

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

    private void sendMessageApi(String msg) {

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("sender_id", modelLogin.getResult().getId());
        params.put("receiver_id", "1");
        params.put("chat_message", msg);
        params.put("timezone", timezoneID);

        Call<ResponseBody> call = api.insertChatApiCall(params);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                Log.e("response", "response = " + response);
                getAllChats();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    public void isBottomReached(boolean isBottomreached) {
        isBottom = isBottomreached;
    }

}