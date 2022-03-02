package com.taxidriver.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.taxidriver.R;
import com.taxidriver.databinding.AdapterAccountsOptionsBinding;
import com.taxidriver.databinding.ContactUsDialogBinding;
import com.taxidriver.models.FAQModel;
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

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterFAQ extends BaseAdapter {

    Context mContext;
    ArrayList<FAQModel.Result> accountList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterFAQ(Context mContext, ArrayList<FAQModel.Result> accountList) {
        this.mContext = mContext;
        this.accountList = accountList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @Override
    public int getCount() {
        return accountList == null ? 0 : accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AdapterAccountsOptionsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_accounts_options, parent, false);

        binding.tvTitle.setText(accountList.get(position).getQuestion());

        binding.getRoot().setOnClickListener(v -> {
            // MyApplication.showAlert(mContext, accountList.get(position).getAnswer());
            showAlert(accountList.get(position).getAnswer());
        });

        return binding.getRoot();

    }

    public void showAlert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton(mContext.getString(R.string.contact_us)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                openContactUsDialog();
                            }
                        }).create().show();
    }

    private void openContactUsDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ContactUsDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.contact_us_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etTitle.getText().toString().trim())) {
                MyApplication.showAlert(mContext, mContext.getString(R.string.field_is_required));
            } else if (TextUtils.isEmpty(dialogBinding.etdetail.getText().toString().trim())) {
                MyApplication.showAlert(mContext, mContext.getString(R.string.field_is_required));
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    contactUsApi(dialogBinding.etTitle.getText().toString().trim(),
                            dialogBinding.etdetail.getText().toString().trim(), dialog);
                } else {
                    MyApplication.showConnectionDialog(mContext);
                }
            }
        });

        dialog.show();

    }

    private void contactUsApi(String title, String detail, Dialog parentDialog) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("feedback", title);
        paramHash.put("detail", detail);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
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

    private void showAlertDialog(Dialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.contact_alert_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                parentDialog.dismiss();
            }
        }).create().show();
    }

}
