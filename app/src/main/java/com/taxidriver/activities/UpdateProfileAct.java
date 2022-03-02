package com.taxidriver.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityUpdateProfileBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.Compress;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.RealPathUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileAct extends AppCompatActivity {

    Context mContext = UpdateProfileAct.this;
    ActivityUpdateProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private final int GALLERY = 0, CAMERA = 1;
    String type = "", registerId = "";
    File profileImage123;
    private String str_image_path;
    private LatLng latLng, workLatLon;
    String driverEmail;
    private static final int PERMISSION_ID = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_WORK_CODE = 103;
    private static final int AUTOCOMPLETE_REQUEST_CODE_CITY = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        sharedPref = SharedPref.getInstance(mContext);

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.api_key));
        }

        Log.e("asdasdasdasdsa", new Gson().toJson(modelLogin));

        itit();
    }

    private void itit() {

        binding.setData(modelLogin.getResult());

        try {
            latLng = new LatLng(Double.parseDouble(modelLogin.getResult().getLat())
                    , Double.parseDouble(modelLogin.getResult().getLon()));
        } catch (Exception e) {
        }

        Glide.with(mContext).load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(binding.profileImage);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.etAdd1.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.etWorkAdd.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_WORK_CODE);
        });

        binding.etCityName.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_CITY);
        });

        binding.addIcon.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                // showPictureDialog();
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etFirstName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_firsttext), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etLastName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_lasttext), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_phone_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etCityName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_city_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etAdd1.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_address1_text), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            } else {
                updateProfileApi(profileImage123);
            }
        });

    }

    private void updateProfileApi(File profileImage) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part profileFilePart;
        RequestBody workLat, workLon, lat, lon;

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), binding.etFirstName.getText().toString().trim());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), binding.etLastName.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString().trim());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), binding.etPhone.getText().toString().trim());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), binding.etCityName.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.etAdd1.getText().toString().trim());

        try {
            lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.latitude));
            lon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.longitude));
        } catch (Exception e) {
            lat = RequestBody.create(MediaType.parse("text/plain"), "");
            lon = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody workplace = RequestBody.create(MediaType.parse("text/plain"), binding.etWorkAdd.getText().toString().trim());

        try {
            workLat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLatLon.latitude));
            workLon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLatLon.longitude));
        } catch (Exception e) {
            workLat = RequestBody.create(MediaType.parse("text/plain"), "");
            workLon = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody attachmentEmpty;

        if (profileImage == null) {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            profileFilePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        } else {
            profileFilePart = MultipartBody.Part.createFormData("image", profileImage.getName(), RequestBody.create(MediaType.parse("car_document/*"), profileImage));
        }

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateDriverCallApi(
                userId, first_name, last_name, mobile, email, address,
                lat, lon, workplace, workLon, workLat, city, profileFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("updateDriverCallApi", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        finish();

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

    private void showPictureDialog() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(mContext).load(resultUri).into(binding.profileImage);
                profileImage123 = new File(RealPathUtil.getRealPath(mContext, resultUri));
                Log.e("asfasdasdad", "resultUri = " + resultUri);

                // binding.profileImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etAdd1.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_WORK_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                workLatLon = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etWorkAdd.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_CITY) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    String addresses = place.getName();

                    Log.e("addresses", "addresses = " + addresses);
                    Log.e("addresses", "addresses = " + place.getName());

                    binding.etCityName.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                Log.e("asdfasfasfasd", "path = " + path);
                profileImage123 = new File(path);
                binding.profileImage.setImageURI(Uri.parse(path));
//                Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
//                    @Override
//                    public void response(boolean status, String message, File file) {
//
//                    }
//                }).CompressedImage(path);
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                profileImage123 = new File(str_image_path);
                Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        profileImage123 = file;
                        binding.profileImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(str_image_path);
            }
        }

    }

}