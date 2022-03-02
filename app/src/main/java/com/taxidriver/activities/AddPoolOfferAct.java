package com.taxidriver.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterAdminAddress;
import com.taxidriver.adapters.AdapterVehicleSelectPool;
import com.taxidriver.databinding.ActivityAddPoolOfferBinding;
import com.taxidriver.databinding.DialogAdminAddressBinding;
import com.taxidriver.databinding.SelectPoolVehicleDialogBinding;
import com.taxidriver.models.ModelAddress;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelVehicle;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPoolOfferAct extends AppCompatActivity {

    Context mContext = AddPoolOfferAct.this;
    public static ActivityAddPoolOfferBinding binding;
    final int AUTOCOMPLETE_START = 101;
    final int AUTOCOMPLETE_END = 102;
    final int AUTOCOMPLETE_INTER1 = 103;
    final int AUTOCOMPLETE_INTER2 = 104;
    final int AUTOCOMPLETE_INTER3 = 105;
    public static LatLng latLngStart, latLngEnd, latLngInter1, latLngInter2, latLngInter3;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelVehicle modelVehicle;
    DialogAdminAddressBinding dialogBinding;
    Dialog dialog;
    Calendar calendar = Calendar.getInstance();
    private String carTypeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_pool_offer);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.api_key));
        }

        itit();

    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btOfferPool.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etStart.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_enter_start_loc));
            } else if (TextUtils.isEmpty(binding.etEnd.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_enter_end_loc));
            } else if (TextUtils.isEmpty(binding.etDate.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_select_date));
            } else if (TextUtils.isEmpty(binding.etTime.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_select_time));
            } else if (TextUtils.isEmpty(binding.etAvalSeat.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_enteraval_seats));
            } else if (Integer.parseInt(binding.etAvalSeat.getText().toString().trim()) > 12) {
                MyApplication.showAlert(mContext, getString(R.string.enter_10_seats));
            } else if (carTypeId == null || carTypeId.equals("")) {
                MyApplication.showAlert(mContext, getString(R.string.select_your_vehicle));
            } else if (TextUtils.isEmpty(binding.etCharge.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_enter_charge));
            } else {
                offerPoolApi();
            }
        });

        binding.startRemove.setOnClickListener(v -> {
            binding.etStart.setText("");
            latLngStart = null;
        });

        binding.endRemove.setOnClickListener(v -> {
            binding.etEnd.setText("");
            latLngEnd = null;
        });

        binding.etInter1.setOnClickListener(v -> {
            binding.etInter1.setText("");
            latLngInter1 = null;
        });

        binding.inter2Remove.setOnClickListener(v -> {
            binding.etInter2.setText("");
            latLngInter2 = null;
        });

        binding.inter3Remove.setOnClickListener(v -> {
            binding.etInter3.setText("");
            latLngInter3 = null;
        });

        binding.etCarType.setOnClickListener(v -> {
            getVehicles();
        });

        binding.etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");
                    String time = mSDF.format(calendar.getTime());
                    binding.etTime.setText(time);
                    Log.e("time", time);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });

        binding.etDate.setOnClickListener(v -> {
            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            binding.etDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);

            dpd.getDatePicker().setMinDate(new Date().getTime());
            dpd.show();

        });

        binding.etStart.setOnClickListener(v -> {
            if (binding.rbAdmin.isChecked()) {
                getAddressApi("start");
            } else if (binding.rbOwn.isChecked()) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_START);
            } else {
                MyApplication.showAlert(mContext,getString(R.string.please_select_location_type));
            }
        });

        binding.etEnd.setOnClickListener(v -> {
            if (binding.rbAdmin.isChecked()) {
                getAddressApi("end");
            } else if (binding.rbOwn.isChecked()) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_END);
            } else {
                MyApplication.showAlert(mContext,getString(R.string.please_select_location_type));
            }
        });

        binding.etInter1.setOnClickListener(v -> {
            if (binding.rbAdmin.isChecked()) {
                getAddressApi("inter1");
            } else if (binding.rbOwn.isChecked()) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_INTER1);
            } else {
                MyApplication.showAlert(mContext,getString(R.string.please_select_location_type));
            }
        });

        binding.etInter2.setOnClickListener(v -> {
            if (binding.rbAdmin.isChecked()) {
                getAddressApi("inter2");
            } else if (binding.rbOwn.isChecked()) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_INTER2);
            } else {
                MyApplication.showAlert(mContext,getString(R.string.please_select_location_type));
            }
        });

        binding.etInter3.setOnClickListener(v -> {
            if (binding.rbAdmin.isChecked()) {
                getAddressApi("inter3");
            } else if (binding.rbOwn.isChecked()) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_INTER3);
            } else {
                MyApplication.showAlert(mContext,getString(R.string.please_select_location_type));
            }
        });

    }

    private void getAddressApi(String type) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getRoutes();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelAddress modelAddress = new Gson().fromJson(stringResponse, ModelAddress.class);
                        openAddressDialog(modelAddress, type);
                        // Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
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

    private void openAddressDialog(ModelAddress modelAddress, String type) {
        dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_admin_address, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterAdminAddress adapterAdminAddress = new AdapterAdminAddress(mContext, modelAddress.getResult(), type, dialog);
        dialogBinding.rvAddress.setAdapter(adapterAdminAddress);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void offerPoolApi() {

        HashMap<String, String> map = new HashMap<>();

        map.put("driver_id", modelLogin.getResult().getId());
        map.put("start_location", binding.etStart.getText().toString().trim());
        map.put("end_location", binding.etEnd.getText().toString().trim());
        map.put("car_type_id", carTypeId);
        map.put("charge_per_km", binding.etCharge.getText().toString().trim());

        map.put("start_lat", String.valueOf(latLngStart.latitude));
        map.put("start_lon", String.valueOf(latLngStart.longitude));

        map.put("end_lat", String.valueOf(latLngEnd.latitude));
        map.put("end_lon", String.valueOf(latLngEnd.longitude));

        if (TextUtils.isEmpty(binding.etInter1.getText().toString().trim())) {
            map.put("stop_1", "");
            map.put("lat1", "");
            map.put("lon1", "");
        } else {
            map.put("stop_1", binding.etInter1.getText().toString().trim());
            map.put("lat1", String.valueOf(latLngInter1.latitude));
            map.put("lon1", String.valueOf(latLngInter1.longitude));
        }

        if (TextUtils.isEmpty(binding.etInter2.getText().toString().trim())) {
            map.put("stop_2", "");
            map.put("lat2", "");
            map.put("lon2", "");
        } else {
            map.put("stop_2", binding.etInter2.getText().toString().trim());
            map.put("lat2", String.valueOf(latLngInter2.latitude));
            map.put("lon2", String.valueOf(latLngInter2.longitude));
        }

        if (TextUtils.isEmpty(binding.etInter2.getText().toString().trim())) {
            map.put("stop_3", "");
            map.put("lat3", "");
            map.put("lon3", "");
        } else {
            map.put("stop_3", binding.etInter3.getText().toString().trim());
            map.put("lat3", String.valueOf(latLngInter3.latitude));
            map.put("lon3", String.valueOf(latLngInter3.longitude));
        }

        map.put("date", binding.etDate.getText().toString().trim());
        map.put("time", binding.etTime.getText().toString().trim());
        map.put("seats_offer", binding.etAvalSeat.getText().toString().trim());

        Log.e("offerPoolApi", "offerPoolApi = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.offerPoolRequestApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        finish();
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
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
                            modelVehicle = new Gson().fromJson(stringResponse, ModelVehicle.class);
                            openVehicleDialog();
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

    private void openVehicleDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        SelectPoolVehicleDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.select_pool_vehicle_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterVehicleSelectPool adapterVehicleSelect = new AdapterVehicleSelectPool(mContext, modelVehicle.getResult(), dialog, AddPoolOfferAct.this::setCarDetails, carTypeId);
        dialogBinding.carList.setAdapter(adapterVehicleSelect);

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog.show();

    }

    public void setCarDetails(ModelVehicle.Result data) {
        carTypeId = data.getId();
        binding.etCarType.setText(data.getCar_number());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_START) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLngStart = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etStart.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_END) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLngEnd = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etEnd.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_INTER1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLngInter1 = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etInter1.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_INTER2) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLngInter2 = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etInter2.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_INTER3) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLngInter3 = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etInter3.setText(addresses);
                } catch (Exception e) {
                }
            }
        }
    }


}
