package com.taxidriver.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityCardDetailsBinding;
import com.taxidriver.databinding.CardDialogBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.InternetConnection;
import com.taxidriver.utils.MyApplication;
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

public class CardDetailsAct extends AppCompatActivity {

    Context mContext = CardDetailsAct.this;
    ActivityCardDetailsBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_details);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        try {
            binding.tvAccountCardNumber.setText("XXXXXXXXXXXX" + modelLogin.getResult().getCard_number().substring(12));
        } catch (Exception e){}

        binding.tvCardNumber.setText(modelLogin.getResult().getCard_number());
        binding.tvCvv.setText(modelLogin.getResult().getCvc_code());

        if (modelLogin.getResult().getExpiry_month() == null ||
                modelLogin.getResult().getExpiry_month().equals("")) {
            binding.tvCardHideShow.setVisibility(View.VISIBLE);
            binding.btChange.setText(getString(R.string.add_your_card));
        } else {
            binding.tvCardHideShow.setVisibility(View.GONE);
            binding.tvExipry.setText(modelLogin.getResult().getExpiry_month() + "/"
                    + modelLogin.getResult().getExpiry_year());
            binding.btChange.setText(getString(R.string.change_card));
        }
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btChange.setOnClickListener(v -> {
            openCardDialog();
        });

    }

    private void openCardDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        CardDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.card_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(CardDetailsAct.this);

        dialogBinding.cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        dialogBinding.btChangeCard.setOnClickListener(v -> {
            if (dialogBinding.cardForm.isValid()) {
                dialog.dismiss();

                HashMap<String, String> params = new HashMap<>();

                params.put("card_number", dialogBinding.cardForm.getCardNumber());
                params.put("expiry_month", dialogBinding.cardForm.getExpirationMonth());
                params.put("expiry_year", "20" + dialogBinding.cardForm.getExpirationYear());
                params.put("cvc_code", dialogBinding.cardForm.getCvv());
                params.put("user_id", modelLogin.getResult().getId());

                if (InternetConnection.checkConnection(mContext)) {
                    changeCardApi(params);
                } else {
                    MyApplication.showConnectionDialog(mContext);
                }

            } else {
                MyApplication.showAlert(mContext, getString(R.string.please_complete_the_form));
            }
        });

        dialog.show();

    }

    private void changeCardApi(HashMap<String, String> params) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addCardApiCall(params);
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

                            ModelLogin modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            binding.tvAccountCardNumber.setText("XXXXXXXXXXXX" + modelLogin.getResult().getCard_number().substring(12));
                            binding.tvCardNumber.setText(modelLogin.getResult().getCard_number());
                            binding.tvCvv.setText(modelLogin.getResult().getCvc_code());

                            binding.tvCardHideShow.setVisibility(View.GONE);

                            binding.btChange.setText(getString(R.string.change_card));
                            binding.tvCardNumber.setText(modelLogin.getResult().getCard_number());
                            binding.tvExipry.setText(modelLogin.getResult().getExpiry_month() + "/" + modelLogin.getResult().getExpiry_year());
                            binding.tvCvv.setText(modelLogin.getResult().getCvc_code());

                            Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_LONG).show();

                        } else {}

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