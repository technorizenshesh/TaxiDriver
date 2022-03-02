package com.taxidriver.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityHomeBinding;
import com.taxidriver.databinding.SanitixzeAgreeDialogBinding;
import com.taxidriver.databinding.UploadImageDialogBinding;
import com.taxidriver.dialogs.NewRequestDialogTaxiNew;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.LatLngInterpolator;
import com.taxidriver.utils.MarkerAnimation;
import com.taxidriver.utils.MusicManager;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.RealPathUtil;
import com.taxidriver.utils.RequestDialogCallBackInterface;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAct extends AppCompatActivity implements OnMapReadyCallback,
        RequestDialogCallBackInterface {

    Context mContext = HomeAct.this;
    ActivityHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    GoogleMap mMap;
    File profilePhoto;
    Dialog uploadDialog;
    UploadImageDialogBinding dialogBinding;
    private Marker currentLocationMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    private Location mLocation;
    SupportMapFragment mapFragment;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeAct.this);

        itit();

    }

    @Override
    protected void onResume() {
        getProfile();
        super.onResume();
    }

    @SuppressLint("MissingPermission")
    private void itit() {

        startLocationUpdates();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                        }
                    }
                });

        Glide.with(mapFragment).load(modelLogin.getResult().getImage()).into(binding.childDashboard.cvImg);
        binding.childDashboard.tvName.setText(modelLogin.getResult().getUser_name());

        Glide.with(mapFragment).load(modelLogin.getResult().getImage()).into(binding.childNavDrawer.userImg);
        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.childDashboard.switch4.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if (isOn) {
                    sanitizeDialog();
                } else {
                    onlinOfflineApi("OFFLINE");
                }
            }
        });

//        binding.childDashboard.switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.e("SwitchState", "isChecked = " + isChecked);
//
//                if (isChecked) {
//                    onlinOfflineApi("ONLINE");
//                    binding.childDashboard.switchOnOff.setText("ONLINE");
//                } else {
//                    onlinOfflineApi("OFFLINE");
//                    binding.childDashboard.switchOnOff.setText("OFFLINE");
//                }
//
//            }
//        });

        binding.childDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvProfile.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.childNavDrawer.tvOfferPool.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, OfferPoolAct.class));
        });

        binding.childNavDrawer.tvMyOffer.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, OfferPoolListAct.class));
        });

        binding.childNavDrawer.tvInvoice.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, InvoiceAct.class));
        });

        binding.childNavDrawer.tvDriverPref.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, DriverPrefrencesAct.class));
        });

        binding.childNavDrawer.tvAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, AccountAct.class));
        });

        binding.childNavDrawer.tvNotification.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, NotifyAct.class));
        });

        binding.childNavDrawer.tvMessage.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ChatListAct.class));
        });

        binding.childNavDrawer.tvWallet.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, WalletAct.class));
        });

        binding.childNavDrawer.tvRatingReview.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, RatingReviewAct.class));
        });

        binding.childNavDrawer.tvSubs.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, SubscriptionAct.class));
        });

        binding.childNavDrawer.tvRideHistory.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, RideHistoryAct.class));
        });

        binding.childNavDrawer.tvChnageLang.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
           // changeLangDialog();
        });

        binding.childNavDrawer.signout.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            ProjectUtil.logoutAppDialog(mContext);
        });

    }

    private void sanitizeDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SanitixzeAgreeDialogBinding dialogBinding  = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.sanitixze_agree_dialog, null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btCancel.setOnClickListener(v -> {
            binding.childDashboard.switch4.setOn(false);
            dialog.dismiss();
        });

        dialogBinding.btnAgree.setOnClickListener(v -> {
            dialog.dismiss();
            onlinOfflineApi("ONLINE");
        });

        dialog.show();

    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(HomeAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(HomeAct.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeAct.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(HomeAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        }
                    }
                }, Looper.myLooper());

    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {

        Log.e("asdfasfasf", "Location Akash = " + currentLocation);
        Log.e("asdfasfasf", "currentLocation = " + currentLocation);
        Log.e("asdfasfasf", "currentLocationMarker = " + currentLocationMarker);

        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (mMap != null) {
                    Log.e("gdfgdfsdfdf", "Map Andar = " + currentLocation);
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top)));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("gdfgdfsdfdf", "Map Andar else = " + currentLocation);
                Log.e("gdfgdfsdfdf", "Hello Marker Anuimation");
                animateCamera(currentLocation);
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    private void onlinOfflineApi(String status) {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("status", status);
        Call<ResponseBody> call = api.updateOnOffApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                Log.e("xjgxkjdgvxsd", "response = " + response);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("xjgxkjdgvxsd", "response = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        if (status.equals("ONLINE")) {
                            modelLogin.getResult().setOnline_status("ONLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.childDashboard.switch4.setOn(true);
                        } else {
                            modelLogin.getResult().setOnline_status("OFFLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.childDashboard.switch4.setOn(false);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }

        });

    }


    private void getProfile() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

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

                            if ("ONLINE".equals(modelLogin.getResult().getOnline_status())) {
                                binding.childDashboard.switchOnOff.setText("ONLINE");
                                binding.childDashboard.switchOnOff.setChecked(true);
                            } else {
                                binding.childDashboard.switchOnOff.setText("OFFLINE");
                                binding.childDashboard.switchOnOff.setChecked(false);
                            }

                            Log.e("childDashboard", "getSecurity_status = " + modelLogin.getResult().getSecurity_status());

                            if(modelLogin.getResult().getSecurity_status() != null) {
                                if("True".equalsIgnoreCase(modelLogin.getResult().getSecurity_status())){
                                    uploadSelfieDialog();
                                }
                            }

                            Log.e("getProfileResponse", "response = " + response);
                            Log.e("getProfileResponse", "response = " + stringResponse);
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

    private void uploadSelfieDialog() {
        uploadDialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.upload_image_dialog, null, false);
        uploadDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivImage.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            uploadDialog.dismiss();
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (profilePhoto == null) {
                MyApplication.showAlert(mContext, getString(R.string.please_upload_your_photo));
            } else {
                uploadDialog.dismiss();
                uploadImageApi();
            }
        });

        uploadDialog.show();

    }

    private void uploadImageApi() {

        MultipartBody.Part profileFilePart;

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody securityDate = RequestBody.create(MediaType.parse("text/plain"), ProjectUtil.getCurrentTime());
        profileFilePart = MultipartBody.Part.createFormData("image", profilePhoto.getName(),
                RequestBody.create(MediaType.parse("car_document/*"), profilePhoto));

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateSecurityEssentials(userId, profileFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    modelLogin.getResult().setSecurity_status("");
                    Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(mContext).load(resultUri).into(dialogBinding.ivImage);
                profilePhoto = new File(RealPathUtil.getRealPath(mContext, resultUri));
                Log.e("asfasdasdad", "resultUri = " + resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void bookingApiCalled() {

    }

}