package com.taxidriver.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.activities.ActiveScheduleDriverAct;
import com.taxidriver.activities.CardDetailsAct;
import com.taxidriver.activities.ChatingAct;
import com.taxidriver.activities.DonateAct;
import com.taxidriver.activities.DriverDocUpdateAct;
import com.taxidriver.activities.EmergencyContactAct;
import com.taxidriver.activities.FAQAct;
import com.taxidriver.activities.LoginAct;
import com.taxidriver.activities.OfferPoolListAct;
import com.taxidriver.activities.RideHistoryAct;
import com.taxidriver.activities.SplashAct;
import com.taxidriver.activities.StatisticsAct;
import com.taxidriver.activities.UpdateProfileAct;
import com.taxidriver.activities.UserFeedbackAct;
import com.taxidriver.activities.WalletAct;
import com.taxidriver.activities.WebviewAct;
import com.taxidriver.databinding.AddMoneyDialogBinding;
import com.taxidriver.databinding.BankdetailsDialogBinding;
import com.taxidriver.databinding.ChangePasswordDialogBinding;
import com.taxidriver.databinding.ContactUsDialogBinding;
import com.taxidriver.databinding.FragmentProfileBinding;
import com.taxidriver.databinding.SendMoneyDialogBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.InternetConnection;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.taxidriver.vehicles.ManageVehicleAct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    Context mContex;
    FragmentProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private double walletTmpAmt = 0.0;
    private String registerId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContex = getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        sharedPref = SharedPref.getInstance(mContex);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        });

        Log.e("asdasdasd", "asdasdas" + modelLogin.getResult().getId());

        itit();

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPref = SharedPref.getInstance(mContex);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        Glide.with(mContex)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(binding.cvImg);

        if (modelLogin.getResult().getUser_name() == null ||
                modelLogin.getResult().getUser_name().equals("")) {
            binding.tvName.setText(modelLogin.getResult().getFirst_name() + " " + modelLogin.getResult().getLast_name());
        } else {
            binding.tvName.setText(modelLogin.getResult().getUser_name());
        }

        binding.tvEmail.setText(modelLogin.getResult().getEmail());

    }

    private void itit() {

        getProfileApiCall();

        Glide.with(mContex)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(binding.cvImg);

        binding.tvName.setText(modelLogin.getResult().getUser_name());
        binding.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.editProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContex, UpdateProfileAct.class));
        });

        binding.llPersonalDetail.setOnClickListener(v -> {
            startActivity(new Intent(mContex, UpdateProfileAct.class));
        });

        Log.e("sdkjfhsdjf", "user_id = " + modelLogin.getResult().getId());

        binding.rlLogout.setOnClickListener(v -> {
            ProjectUtil.logoutAppDialog(mContex);
        });

        binding.topUp.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.llDriverDoc.setOnClickListener(v -> {
            startActivity(new Intent(mContex, DriverDocUpdateAct.class));
        });

        binding.llFAQ.setOnClickListener(v -> {
            startActivity(new Intent(mContex, FAQAct.class));
        });

        binding.llLiveChat.setOnClickListener(v -> {
            startActivity(new Intent(mContex, ChatingAct.class));
        });

        binding.llDonate.setOnClickListener(v -> {
            startActivity(new Intent(mContex, DonateAct.class));
        });

        binding.lllang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.rlStatistics.setOnClickListener(v -> {
            startActivity(new Intent(mContex, StatisticsAct.class));
        });

        binding.llAddMoney.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.llSendMoney.setOnClickListener(v -> {
            tranferMOneyDialog();
        });

        binding.llEmergencyContact.setOnClickListener(v -> {
            startActivity(new Intent(mContex, EmergencyContactAct.class));
        });

        binding.llContactUs.setOnClickListener(v -> {
            openContactUsDialog();
        });

        binding.llBankDetails.setOnClickListener(v -> {
           // bankDetailDialog();
        });

        binding.llPaymentMethod.setOnClickListener(v -> {
            startActivity(new Intent(mContex, CardDetailsAct.class));
        });

        binding.llAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(mContex, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/about_us.html")
            );
        });

        binding.llTermsAndCondition.setOnClickListener(v -> {
            startActivity(new Intent(mContex, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/terms&condition.html")
            );
        });

        binding.llPrivacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(mContex, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/privacy-policy.html")
            );
        });

        binding.rlManageVehicle.setOnClickListener(v -> {
            startActivity(new Intent(mContex, ManageVehicleAct.class));
        });

        binding.llYourTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContex, RideHistoryAct.class));
        });

        binding.llUserFeedback.setOnClickListener(v -> {
            startActivity(new Intent(mContex, UserFeedbackAct.class));
        });

        binding.llMyWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContex, WalletAct.class));
        });

        binding.llWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContex, WalletAct.class));
        });

        binding.rlOfferPool.setOnClickListener(v -> {
            startActivity(new Intent(mContex, OfferPoolListAct.class));
        });

        binding.llScheduleTrips.setOnClickListener(v -> {
            startActivity(new Intent(mContex, ActiveScheduleDriverAct.class));
        });

        binding.llBooking.setOnClickListener(v -> {
            startActivity(new Intent(mContex, ActiveScheduleDriverAct.class));
        });

        binding.llChangePass.setOnClickListener(v -> {
            changePasswordDialog();
        });

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContex, WindowManager.LayoutParams.MATCH_PARENT);
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
                ProjectUtil.updateResources(mContex, "en");
                sharedPref.setlanguage("lan", "en");
                getActivity().finishAffinity();
                startActivity(new Intent(mContex, SplashAct.class));
                dialog.dismiss();
            } else if (radioUrdu.isChecked()) {
                ProjectUtil.updateResources(mContex, "ur");
                sharedPref.setlanguage("lan", "ur");
                getActivity().finishAffinity();
                startActivity(new Intent(mContex, SplashAct.class));
                dialog.dismiss();
            } else if (radioArabic.isChecked()) {
                ProjectUtil.updateResources(mContex, "ar");
                sharedPref.setlanguage("lan", "ar");
                getActivity().finishAffinity();
                startActivity(new Intent(mContex, SplashAct.class));
                dialog.dismiss();
            } else if (radioFrench.isChecked()) {
                ProjectUtil.updateResources(mContex, "fr");
                sharedPref.setlanguage("lan", "fr");
                getActivity().finishAffinity();
                startActivity(new Intent(mContex, SplashAct.class));
                dialog.dismiss();
            } else if (radioChinese.isChecked()) {
                ProjectUtil.updateResources(mContex, "zh");
                sharedPref.setlanguage("lan", "zh");
                getActivity().finishAffinity();
                startActivity(new Intent(mContex, SplashAct.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void bankDetailDialog() {
        Dialog dialog = new Dialog(mContex, WindowManager.LayoutParams.MATCH_PARENT);

        BankdetailsDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContex), R.layout.bankdetails_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etPaymentEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.enter_payment_email));
            } else if (!ProjectUtil.isValidEmail(dialogBinding.etPaymentEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.invalid_email));
            } else if (TextUtils.isEmpty(dialogBinding.etHolderName.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.bank_holdername_text));
            } else if (TextUtils.isEmpty(dialogBinding.etAccountNumber.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.account_number_text));
            } else if (TextUtils.isEmpty(dialogBinding.etBankName.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.bank_name_text));
            } else if (TextUtils.isEmpty(dialogBinding.etBankLocation.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.bank_location_text));
            } else if (TextUtils.isEmpty(dialogBinding.etBICCode.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.bic_swift_code_text));
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("pay_email",dialogBinding.etPaymentEmail.getText().toString().trim());
                params.put("holder_name",dialogBinding.etHolderName.getText().toString().trim());
                params.put("account_no",dialogBinding.etAccountNumber.getText().toString().trim());
                params.put("bank_name",dialogBinding.etBankName.getText().toString().trim());
                params.put("bank_location",dialogBinding.etBankLocation.getText().toString().trim());
                params.put("bic_code",dialogBinding.etBICCode.getText().toString().trim());

                bankDetailsApi(params);
            }
        });

        dialog.show();

    }

    private void bankDetailsApi(HashMap<String, String> params) {



    }

    private void openContactUsDialog() {

        Dialog dialog = new Dialog(mContex, WindowManager.LayoutParams.MATCH_PARENT);
        ContactUsDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContex), R.layout.contact_us_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etTitle.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.field_is_required));
            } else if (TextUtils.isEmpty(dialogBinding.etdetail.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.field_is_required));
            } else {
                if (InternetConnection.checkConnection(mContex)) {
                    contactUsApi(dialogBinding.etTitle.getText().toString().trim(),
                            dialogBinding.etdetail.getText().toString().trim(), dialog);
                } else {
                    MyApplication.showConnectionDialog(mContex);
                }
            }
        });

        dialog.show();

    }

    private void contactUsApi(String title, String detail, Dialog parentDialog) {
        ProjectUtil.showProgressDialog(mContex, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("feedback", title);
        paramHash.put("detail", detail);

        Api api = ApiFactory.getClientWithoutHeader(mContex).create(Api.class);
        Call<ResponseBody> call = api.contactUsApiCall(paramHash);
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
                            showAlertDialog(parentDialog);
                            Log.e("asfddasfasdf", "response = " + response);
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContex, "Success", Toast.LENGTH_SHORT).show();

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

    private void showAlertDialog(Dialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContex);
        builder.setMessage(R.string.contact_alert_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                parentDialog.dismiss();
            }
        }).create().show();
    }

    private void getProfile() {
        Api api = ApiFactory.getClientWithoutHeader(mContex).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getProfileCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

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

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContex);
        builder.setMessage("Your session is expired Please login Again!")
                .setCancelable(false)
                .setPositiveButton(mContex.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPref.clearAllPreferences();
                                getActivity().finishAffinity();
                                startActivity(new Intent(mContex, LoginAct.class));
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void addMoneyDialog() {

        Dialog dialog = new Dialog(mContex, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AddMoneyDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContex),
                R.layout.add_money_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if (!(dialogBinding.etMoney.getText().toString().trim().equals("") || dialogBinding.etMoney.getText().toString().trim().equals("0"))) {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) - 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.btDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                dialogBinding.etMoney.setText("0");
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) + 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            } else {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) + 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.tv699.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("699");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.tv799.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("799");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.tv899.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("899");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.btDone.setOnClickListener(v -> {
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                Toast.makeText(mContex, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else if (walletTmpAmt == 0.0) {
                Toast.makeText(mContex, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            } else {
                // dialog.dismiss();
                addWalletAmountApi(String.valueOf(walletTmpAmt), dialog);
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void addWalletAmountApi(String amount, Dialog dialog) {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("amount", amount);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContex, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContex).create(Api.class);
        Call<ResponseBody> call = api.addWalletAmount(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        dialog.dismiss();
                        getProfileApiCall();
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

    private void getProfileApiCall() {
        ProjectUtil.showProgressDialog(mContex, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContex).create(Api.class);
        Call<ResponseBody> call = api.getProfileCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            binding.tvWalletAmount.setText(AppConstant.CURRENCY + " " + modelLogin.getResult().getWallet());

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
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

    private void tranferMOneyDialog() {

        Dialog dialog = new Dialog(mContex, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SendMoneyDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContex),
                R.layout.send_money_dialog, null, false);

        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btDone.setOnClickListener(v -> {

            if (TextUtils.isEmpty(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContex, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etEnterAmount.getText().toString().trim())) {
                Toast.makeText(mContex, getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContex, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else {
                if (dialogBinding.rbUser.isChecked()) {
                    sendMoneyAPiCall(dialog, dialogBinding.etEmail.getText().toString().trim()
                            , dialogBinding.etEnterAmount.getText().toString().trim(), "USER");
                } else if (dialogBinding.rbDriver.isChecked()) {
                    sendMoneyAPiCall(dialog, dialogBinding.etEmail.getText().toString().trim()
                            , dialogBinding.etEnterAmount.getText().toString().trim(), "DRIVER");
                } else {
                    MyApplication.showAlert(mContex, getString(R.string.select_user_driver_text));
                }
            }

        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void sendMoneyAPiCall(Dialog dialog, String email, String amount, String type) {

        ProjectUtil.showProgressDialog(mContex, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("amount", amount);
        paramHash.put("email", email);
        paramHash.put("type", type);

        Log.e("paramHashparamHash", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContex).create(Api.class);
        Call<ResponseBody> call = api.walletTransferApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        Log.e("sendMoneyAPiCall", "sendMoneyAPiCall = " + stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("sendMoneyAPiCall", "sendMoneyAPiCall = " + stringResponse);

                            getProfileApiCall();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(mContex, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
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

    private void changePasswordDialog() {
        Dialog dialog = new Dialog(mContex, WindowManager.LayoutParams.MATCH_PARENT);

        ChangePasswordDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContex)
                , R.layout.change_password_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etOldPass.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.enter_old_pass));
            } else if (TextUtils.isEmpty(dialogBinding.etNewPass.getText().toString().trim())) {
                MyApplication.showAlert(mContex, getString(R.string.enter_new_pass));
            } else if (!(dialogBinding.etNewPass.getText().toString().trim().length() >= 5)) {
                MyApplication.showAlert(mContex, getString(R.string.password_validation_text));
            } else {
                if (modelLogin.getResult().getPassword().trim().equals(dialogBinding.etOldPass.getText().toString().trim())) {
                    changePasswordApi(dialogBinding.etNewPass.getText().toString().trim(), dialog);
                } else {
                    MyApplication.showAlert(mContex, getString(R.string.old_pass_is_incorrect));
                }
            }
        });

        dialog.show();

    }

    private void changePasswordApi(String password, Dialog dialog) {
        ProjectUtil.showProgressDialog(mContex, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("password", password);

        Log.e("sadasddasd", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContex).create(Api.class);
        Call<ResponseBody> call = api.changePass(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                dialog.dismiss();
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                            modelLogin.getResult().setPassword(password);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            Toast.makeText(mContex, "Success", Toast.LENGTH_SHORT).show();
                        } else {

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