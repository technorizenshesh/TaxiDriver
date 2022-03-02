package com.taxidriver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityLoginBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.InternetConnection;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.MyService;
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

public class LoginAct extends AppCompatActivity {

    Context mContext = LoginAct.this;
    ActivityLoginBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {});

        itit();

    }

    private void itit() {

        binding.btnSignin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    loginCheck();
                    // loginApiCall();
                } else {
                    Toast.makeText(mContext, getString(R.string.check_internet_text), Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.ivForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ForgotPassAct.class));
        });

        binding.btSignup.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SignUpAct.class));
        });

        binding.changeLang.setOnClickListener(v -> {
            changeLangDialog();
        });

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioUrdu = dialog.findViewById(R.id.radioUrdu);
        RadioButton radioChinese = dialog.findViewById(R.id.radioChinese);
        RadioButton radioArabic = dialog.findViewById(R.id.radioArabic);
        RadioButton radioFrench = dialog.findViewById(R.id.radioFrench);

        if ("en".equals(sharedPref.getLanguage("lan"))) {
            radioEng.setChecked(true);
        } else if ("ar".equals(sharedPref.getLanguage("lan"))) {
            radioArabic.setChecked(true);
        } else if ("fr".equals(sharedPref.getLanguage("lan"))) {
            radioFrench.setChecked(true);
        } else if ("ur".equals(sharedPref.getLanguage("lan"))) {
            radioUrdu.setChecked(true);
        } else if ("zh".equals(sharedPref.getLanguage("lan"))) {
            radioChinese.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext, "en");
                sharedPref.setlanguage("lan", "en");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            } else if (radioUrdu.isChecked()) {
                ProjectUtil.updateResources(mContext, "ur");
                sharedPref.setlanguage("lan", "ur");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            } else if (radioArabic.isChecked()) {
                ProjectUtil.updateResources(mContext, "ar");
                sharedPref.setlanguage("lan", "ar");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            } else if (radioFrench.isChecked()) {
                ProjectUtil.updateResources(mContext, "fr");
                sharedPref.setlanguage("lan", "fr");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            } else if (radioChinese.isChecked()) {
                ProjectUtil.updateResources(mContext, "zh");
                sharedPref.setlanguage("lan", "zh");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void loginCheck() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("register_id", registerId);

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.checkLoginValidCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        loginApiCall();
                    } else if (jsonObject.getString("status").equals("2")) {
                        MyApplication.showAlert(mContext, getString(R.string.email_not_exist));
                    } else {
                        logoutAlertDialog();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("You are logged in with another device do you want to login with these device?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loginApiCall();
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void loginApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("password", binding.etPassword.getText().toString().trim());
        paramHash.put("lat", "");
        paramHash.put("lon", "");
        paramHash.put("type", AppConstant.DRIVER);
        paramHash.put("register_id", registerId);

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.loginApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        ContextCompat.startForegroundService(LoginAct.this, new Intent(LoginAct.this, MyService.class));

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        startActivity(new Intent(mContext, HomeV3CubeAct.class));
                        finish();

                    } else {
                        Toast.makeText(LoginAct.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }



}