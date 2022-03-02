package com.taxidriver.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityDriverDocUpdateBinding;
import com.taxidriver.databinding.UpdateDocDialogBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.RealPathUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverDocUpdateAct extends AppCompatActivity {

    Context mContext = DriverDocUpdateAct.this;
    ActivityDriverDocUpdateBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String urllisence = "", urlPan = "";
    Dialog dialog;
    UpdateDocDialogBinding dialogBinding;
    int whichImage = 0;
    File lisenceImg, panImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_doc_update);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getProfileApiCall();

        Log.e("asdfadasdasd", "modelLogin.getResult().getDriver_lisence_img() = " + modelLogin.getResult().getDriver_lisence_img());
        Log.e("asdfadasdasd", "modelLogin.getResult().getDriver_lisence_img() = " + modelLogin.getResult().getDriver_lisence_img());
        Log.e("asdfadasdasd", "modelLogin.getResult().getPan_card_imag() = " + modelLogin.getResult().getPan_card_imag());

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.drivingDetails.setOnClickListener(v -> {
            whichImage = 1;
            editCardDetailsDialog();
        });

        binding.panDetails.setOnClickListener(v -> {
            whichImage = 2;
            editCardDetailsDialog();
        });

        binding.ivDriverLisence.setOnClickListener(v -> {
            ProjectUtil.imageShowFullscreenDialog(mContext, urllisence);
        });

        binding.ivPan.setOnClickListener(v -> {
            ProjectUtil.imageShowFullscreenDialog(mContext, urlPan);
        });

    }

    private void editCardDetailsDialog() {

        dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.update_doc_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        if (whichImage == 1) {
            dialogBinding.tvExpire.setText(modelLogin.getResult().getExpiry_driving_lisence_date());
        } else {
            dialogBinding.tvExpire.setText(modelLogin.getResult().getExpiry_pan_date());
        }

        dialogBinding.btnChange.setOnClickListener(v -> {

            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Display Selected date in textbox
                    c.set(year,monthOfYear,dayOfMonth);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String dateString = simpleDateFormat.format(c.getTime());
                    dialogBinding.tvExpire.setText(dateString);
                }
            }, mYear, mMonth, mDay);
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dpd.show();

        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.tvView.setOnClickListener(v -> {
            if (whichImage == 1) {
                ProjectUtil.imageShowFullscreenDialog(mContext, urllisence);
            } else {
                ProjectUtil.imageShowFullscreenDialog(mContext, urlPan);
            }
        });

        dialogBinding.tvEdit.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            Log.e("asfasdasdasd"," btnSubmit whichImage = " + whichImage);
            if (whichImage == 0 || whichImage == 1) {
                updateLisencApi();
            } else if (whichImage == 2) {
                updatePanImage();
            }
        });

        dialog.show();

    }

    private void updatePanImage() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        RequestBody attachmentEmpty;
        MultipartBody.Part lisenceOrPanPart;

        if (whichImage == 2) {
            if (panImage == null) {
                attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
                lisenceOrPanPart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
            } else {
                lisenceOrPanPart = MultipartBody.Part.createFormData("pan_card_imag", panImage.getName(), RequestBody.create(MediaType.parse("car_document/*"), panImage));
            }
        } else {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            lisenceOrPanPart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody expiryDate = RequestBody.create(MediaType.parse("text/plain"), dialogBinding.tvExpire.getText().toString().trim());
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updatePanApiCall(expiryDate, userId, lisenceOrPanPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("updatePanImage", "updatePanImage = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        dialog.dismiss();
                        getProfileApiCall();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

    private void updateLisencApi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        RequestBody attachmentEmpty;
        MultipartBody.Part lisenceOrPanPart;

        if (whichImage == 1) {
            if (lisenceImg == null) {
                attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
                lisenceOrPanPart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
            } else {
                lisenceOrPanPart = MultipartBody.Part.createFormData("driver_lisence", lisenceImg.getName(), RequestBody.create(MediaType.parse("car_document/*"), lisenceImg));
            }
        } else {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            lisenceOrPanPart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody expiryDate = RequestBody.create(MediaType.parse("text/plain"), dialogBinding.tvExpire.getText().toString().trim());
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateLisenceApiCall(expiryDate, userId, lisenceOrPanPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("driversignup", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        dialog.dismiss();
                        getProfileApiCall();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.e("asfasdasdasd","whichImage = " + whichImage);
            Log.e("asfasdasdasd","panImage = " + panImage);
            Log.e("asfasdasdasd","lisenceImg = " + lisenceImg);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if (whichImage == 1) {
                    lisenceImg = new File(RealPathUtil.getRealPath(mContext, resultUri));
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    panImage = new File(RealPathUtil.getRealPath(mContext, resultUri));
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileApiCallNew();
    }

    private void getProfileApiCallNew() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
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

                            urllisence = modelLogin.getResult().getDriver_lisence();
                            urlPan = modelLogin.getResult().getPan_card_imag();

                            Glide.with(mContext)
                                    .load(modelLogin.getResult().getDriver_lisence())
                                    .into(binding.ivDriverLisence);

                            Glide.with(mContext)
                                    .load(modelLogin.getResult().getPan_card_imag())
                                    .into(binding.ivPan);

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

    private void getProfileApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
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

                            urllisence = modelLogin.getResult().getDriver_lisence();
                            urlPan = modelLogin.getResult().getPan_card_imag();

                            binding.tvDrivingDate.setText("Expire: " + modelLogin.getResult().getExpiry_driving_lisence_date());
                            binding.tvPanDate.setText("Expire: " + modelLogin.getResult().getExpiry_pan_date());

                            if("False".equals(modelLogin.getResult().getPan_status())) {
                                binding.tvStatusPan.setText("Not Approved By Admin!");
                            } else {
                                binding.tvStatusPan.setTextColor(ContextCompat.getColor(mContext,R.color.green_spalsh));
                                binding.tvStatusPan.setText("Approved By Admin!");
                            }

                            if("False".equals(modelLogin.getResult().getLisence_status())) {
                                binding.tvStatusLisecne.setText("Not Approved By Admin!");
                            } else {
                                binding.tvStatusLisecne.setTextColor(ContextCompat.getColor(mContext,R.color.green_spalsh));
                                binding.tvStatusLisecne.setText("Approved By Admin!");
                            }

                            Glide.with(mContext)
                                    .load(modelLogin.getResult().getDriver_lisence())
                                    .into(binding.ivDriverLisence);

                            Glide.with(mContext)
                                    .load(modelLogin.getResult().getPan_card_imag())
                                    .into(binding.ivPan);

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

}