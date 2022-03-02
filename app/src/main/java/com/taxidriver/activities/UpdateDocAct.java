package com.taxidriver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityUpdateDocBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.Compress;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDocAct extends AppCompatActivity {

    Context mContext = UpdateDocAct.this;
    ActivityUpdateDocBinding binding;
    int imageCapturedCode;
    private final int GALLERY = 0, CAMERA = 1;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    File lisenceFile, pancard;
    private String str_image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_doc);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.ivDriverLisence.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivPan.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if (lisenceFile == null) {
                Toast.makeText(mContext, getString(R.string.driving_licesne_text), Toast.LENGTH_SHORT).show();
            } else if (pancard == null) {
                Toast.makeText(mContext, getString(R.string.pan_card_text), Toast.LENGTH_LONG).show();
            } else {
                addDocumentApiCall();
            }
        });

    }

    private void addDocumentApiCall() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part lisenceFilePart;
        MultipartBody.Part identityFilePart;

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        lisenceFilePart = MultipartBody.Part.createFormData("driver_lisence", lisenceFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), lisenceFile));
        identityFilePart = MultipartBody.Part.createFormData("pan_card_imag", pancard.getName(), RequestBody.create(MediaType.parse("car_document/*"), pancard));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addDriverDocumentApiCall(user_id, lisenceFilePart, identityFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("documentsdriver", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        modelLogin.getResult().setStep("2");
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        startActivity(new Intent(mContext, HomeV3CubeAct.class));
                        finish();

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setImageFromCameraGallery(File file) {
        if (imageCapturedCode == 1) {
            lisenceFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    lisenceFile = file;
                    binding.ivDriverLisence.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            pancard = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    pancard = file;
                    binding.ivPan.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                setImageFromCameraGallery(new File(path));
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                setImageFromCameraGallery(new File(str_image_path));
            }
        }

    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ProjectUtil.openGallery(mContext, GALLERY);
                                break;
                            case 1:
                                str_image_path = ProjectUtil.openCamera(mContext, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

}