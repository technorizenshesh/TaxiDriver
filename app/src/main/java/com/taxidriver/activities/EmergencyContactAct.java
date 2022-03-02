package com.taxidriver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterEmergency;
import com.taxidriver.databinding.ActivityEmergencyContactBinding;
import com.taxidriver.models.ModelEmergencyContacts;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MyApplication;
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

public class EmergencyContactAct extends AppCompatActivity {

    private static final int CONTACT_REQUEST_CODE = 101;
    private static final int PERMISSION_ID = 102;
    Context mContext = EmergencyContactAct.this;
    ActivityEmergencyContactBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    public static int contactSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_emergency_contact);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getAllContactsApi();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllContactsApi();
            }
        });

        binding.btAddContacts.setOnClickListener(v -> {
            if (contactSize < 5) {
                if (checkPermissions()) {
                    Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(it, CONTACT_REQUEST_CODE);
                } else {
                    requestPermissions();
                }
            } else {
                MyApplication.showAlert(mContext, getString(R.string.you_can_add_maximum_5_contacts));
            }
        });

    }

    private void addContactsApi(String name, String number) {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("name", name);
        map.put("mobile", number);

        Log.e("addContactsApi", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addContactApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("addContactsApi", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        getAllContactsApi();
                    } else {
                        MyApplication.showAlert(mContext, getString(R.string.contact_already_exist));
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

    public static void getContactSize(int size) {
        Log.e("getContactSize", "getContactSize = " + size);
        contactSize = size;
    }

    private void getAllContactsApi() {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());

        Log.e("getAllContactsApi", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllEmerContacts(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();

                    Log.e("getAllContactsApi", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelEmergencyContacts modelEmergencyContacts = new Gson().fromJson(stringResponse, ModelEmergencyContacts.class);
                        AdapterEmergency adapterEmergency = new AdapterEmergency(mContext, modelEmergencyContacts.getResult());
                        binding.rvContacts.setAdapter(adapterEmergency);
                        getContactSize(modelEmergencyContacts.getResult().size());
                    } else {
                        AdapterEmergency adapterEmergency = new AdapterEmergency(mContext, null);
                        binding.rvContacts.setAdapter(adapterEmergency);
                        getContactSize(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_ID
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Uri contactData = data.getData();
                String cNumber = "";
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        phones.moveToFirst();
                        cNumber = phones.getString(phones.getColumnIndex("data1"));
                        // System.out.println("number is:" + cNumber);
                    }
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    Log.e("asdasdasdasda", "namename = " + name);
                    Log.e("asdasdasdasda", "cNumber = " + cNumber);

                    addContactsApi(name, cNumber);

                }

            }

        }
    }


}