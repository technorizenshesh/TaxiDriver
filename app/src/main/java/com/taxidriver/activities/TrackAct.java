package com.taxidriver.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityTrackBinding;
import com.taxidriver.databinding.AdditionChargeDialogBinding;
import com.taxidriver.databinding.DriverChangeStatusDialogBinding;
import com.taxidriver.databinding.NavigateDialogBinding;
import com.taxidriver.databinding.SanitixzeAgreeDialogBinding;
import com.taxidriver.databinding.UploadImageDialogBinding;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.LatLngInterpolator;
import com.taxidriver.utils.MarkerAnimation;
import com.taxidriver.utils.MusicManager;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.RealPathUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.directionclasses.DrawPollyLine;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackAct.this;
    ActivityTrackBinding binding;
    SharedPref sharedPref;
    private ModelCurrentBooking data;
    ModelLogin modelLogin;
    private Marker currentLocationMarker;
    private ModelLogin.Result UserDetails;
    private ModelCurrentBookingResult result;
    private String requestId, userMobile, userId, userName;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    Location currentLocation;
    Vibrator vibrator;
    double finalTollCharge = 0.0;
    double finalOtherCharge = 0.0;
    GoogleMap mMap;
    private LatLng currentlocation;
    private LatLng pickLocation, droplocation;
    SupportMapFragment mapFragment;
    private String status;
    private Marker pCurrentLocationMarker;
    private Marker dcurrentLocationMarker;
    UploadImageDialogBinding dialogBinding;
    Dialog uploadDialog;
    File profilePhoto;

    String waitingTime = "00:00:00";
    private int seconds = 0;
    private boolean wasRunning, isFirstTime = false, isWaiting = false;
    boolean running = false;

    BroadcastReceiver statusBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("job_status")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    MusicManager.getInstance().initalizeMediaPlayer(TrackAct.this, Uri.parse
                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                    MusicManager.getInstance().stopPlaying();
                    Toast.makeText(context, "Ride cancelled by user", Toast.LENGTH_LONG).show();
                    finishAffinity();
                    startActivity(new Intent(TrackAct.this, HomeV3CubeAct.class));
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);

        if (savedInstanceState != null) {
            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackAct.this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        startLocationUpdates();

        try {
            if (getIntent() != null) {
                data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                Log.e("ModelCurrentBooking", "ModelCurrentBooking = Akash  " + new Gson().toJson(data));
                result = data.getResult().get(0);
                requestId = result.getId();
                userMobile = data.getResult().get(0).getUser_details().get(0).getMobile();
                userId = data.getResult().get(0).getUser_details().get(0).getId();
                userName = data.getResult().get(0).getUser_details().get(0).getUser_name();
                UserDetails = result.getUser_details().get(0);
                if (UserDetails.getImage() != null) {
                    Glide.with(mContext)
                            .load(UserDetails.getImage())
                            .placeholder(R.drawable.user_ic)
                            .error(R.drawable.user_ic)
                            .into(binding.driverImage);
                }
            }
        } catch (Exception e) {
        }

        itit();

    }

    private void itit() {

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        pickLocation = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));
        droplocation = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));

        binding.pickUp.setText(result.getPicuplocation());
        binding.tvDestination.setText(result.getDropofflocation());
        binding.tvName.setText(result.getUser_details().get(0).getUser_name());

        binding.llWait.setOnClickListener(v -> {
            running = true;
            if (!isFirstTime) {
                waitingTimeApi();
            }
            isFirstTime = true;
        });

        try {
            droplocation = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
        } catch (Exception e) {
        }

        if (result.getStatus().equalsIgnoreCase("Accept")) {
            binding.llWait.setVisibility(View.GONE);
            binding.btnWaiting.setVisibility(View.GONE);
            binding.btnStatus.setText(R.string.update_when_you_arrived);
        } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
            binding.llWait.setVisibility(View.GONE);
            binding.btnWaiting.setVisibility(View.GONE);
            binding.tvFrom.setText(R.string.desti_loc);
            binding.tvFrom.setText(result.getDropofflocation());
            binding.btnStatus.setText(R.string.start_the_trip);
        } else if (result.getStatus().equalsIgnoreCase("Start")) {
            binding.tvFrom.setText(R.string.desti_loc);
            binding.tvFrom.setText(result.getDropofflocation());
            binding.btnStatus.setText(R.string.end_the_trip);
            binding.llWait.setVisibility(View.VISIBLE);
            binding.btnWaiting.setVisibility(View.VISIBLE);
        } else if (result.getStatus().equalsIgnoreCase("End")) {
            binding.btnStatus.setText("Finish");
        } else if (result.getStatus().equalsIgnoreCase("Cancel")) {
            finish();
        }

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext, userMobile);
        });

        binding.ivNavigate.setOnClickListener(v -> {
            openNavigateionDialog();
        });

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideCancellationAct.class)
                    .putExtra("id", result.getId())
            );
        });

        binding.btnStatus.setOnClickListener(v -> {
            startEndTripDialog(result.getStatus());
        });

    }

    // Trigger new location updates at interval
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(TrackAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(TrackAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(TrackAct.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    currentlocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());

    }

    private void startEndTripDialog(String status) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DriverChangeStatusDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.driver_change_status_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        if (status.equalsIgnoreCase("Accept")) {
            dialogBinding.tvMessage.setText(getString(R.string.arrived_text));
        } else if (status.equalsIgnoreCase("Arrived")) {
            dialogBinding.tvMessage.setText(getString(R.string.start_the_trip_text));
        } else if (status.equalsIgnoreCase("Start")) {
            dialogBinding.tvMessage.setText(getString(R.string.end_the_trip_text));
        }

        dialogBinding.tvOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (status.equalsIgnoreCase("Accept")) {
                AcceptCancel("Arrived", "0.0", "0.0");
            } else if (status.equalsIgnoreCase("Arrived")) {
                AcceptCancel("Start", "0.0", "0.0");
                // uploadSelfieDialog();
            } else if (status.equalsIgnoreCase("Start")) {
                additionalChargeDialog();
                // AcceptCancel("End");
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

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
                // AcceptCancel("Start", "0.0", "0.0");
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

                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        AcceptCancel("Start", "0.0", "0.0");
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

    private void additionalChargeDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        AdditionChargeDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.addition_charge_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvSkip.setOnClickListener(v -> {
            AcceptCancel("End", "0.0", "0.0");
        });

        dialogBinding.tvSubmit.setOnClickListener(v -> {
            AdditionalAlertDialog(dialogBinding.etTollCharge.getText().toString().trim()
                    , dialogBinding.etOtherCharge.getText().toString().trim());
        });

        dialogBinding.etOtherCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.equals("")) {
                    double otherTotal, tollTotal, finalTotal;

                    try {
                        otherTotal = Double.parseDouble(s.toString());
                    } catch (Exception e) {
                        otherTotal = 0.0;
                    }

                    try {
                        tollTotal = Double.parseDouble(dialogBinding.etTollCharge.getText().toString().trim());
                    } catch (Exception e) {
                        tollTotal = 0.0;
                    }

                    finalTotal = otherTotal + tollTotal;
                    dialogBinding.tvTotalPrice.setText(String.valueOf(finalTotal));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogBinding.etTollCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.equals("")) {

                    double otherTotal, tollTotal, finalTotal;

                    try {
                        otherTotal = Double.parseDouble(s.toString());
                    } catch (Exception e) {
                        otherTotal = 0.0;
                    }

                    try {
                        tollTotal = Double.parseDouble(dialogBinding.etOtherCharge.getText().toString().trim());
                    } catch (Exception e) {
                        tollTotal = 0.0;
                    }

                    finalTotal = otherTotal + tollTotal;
                    dialogBinding.tvTotalPrice.setText(String.valueOf(finalTotal));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        dialog.show();

    }

    private void AdditionalAlertDialog(String toll, String other) {

        if (TextUtils.isEmpty(toll)) {
            toll = "0.0";
        }

        if (TextUtils.isEmpty(other)) {
            other = "0.0";
        }

//        String finalToll = toll;
//        String finalOther = other;

        try {
            finalTollCharge = Double.parseDouble(toll);
        } catch (Exception e) {
            finalTollCharge = Integer.parseInt(toll) + 0.0;
        }

        try {
            finalOtherCharge = Double.parseDouble(other);
        } catch (Exception e) {
            finalOtherCharge = Integer.parseInt(other) + 0.0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.additional_alert_text);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AcceptCancel("End", String.valueOf(finalTollCharge), String.valueOf(finalOtherCharge));
            }
        }).create().show();
    }

    private void runTimer() {

        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);

                waitingTime = time;
                // Set the text view text.
                binding.btnWaiting.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
        registerReceiver(statusBroadCast, new IntentFilter("job_status"));
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusBroadCast);
        wasRunning = running;
        running = false;
    }

    public void waitingTimeApi() {

        HashMap<String, String> map = new HashMap<>();
        map.put("request_id", result.getId());
        map.put("waiting_status", "START");

        Log.e("waitingTimeApi", "Param = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.waitingTimeAPiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("waitingTimeApi", "waitingTimeApi = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        runTimer();
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

    private void sanitizeDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SanitixzeAgreeDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.sanitixze_agree_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnAgree.setOnClickListener(v -> {
            AcceptCancel("Start", "0.0", "0.0");
            dialog.dismiss();
        });

        dialog.show();

    }

    public void AcceptCancel(String status, String tollCharge, String otherCharge) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("status", status);
        map.put("cancel_reaison", "");
        map.put("toll_charge", tollCharge);
        map.put("other_charge", otherCharge);
        map.put("waiting_time", "0" + binding.btnWaiting.getText().toString().trim());
        map.put("timezone", TimeZone.getDefault().getID());

        Log.e("AcceptCancelCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.pauseProgressDialog();
                        ProjectUtil.clearNortifications(mContext);
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                        if (status.equalsIgnoreCase("Arrived")) {
                            result.setStatus("Arrived");
                            binding.llWait.setVisibility(View.GONE);
                            binding.btnWaiting.setVisibility(View.GONE);
                            binding.btnStatus.setText(R.string.start_the_trip);
                        } else if (status.equalsIgnoreCase("Start")) {
                            result.setStatus("Start");
                            binding.llWait.setVisibility(View.VISIBLE);
                            binding.btnWaiting.setVisibility(View.VISIBLE);
                            binding.btnStatus.setText(R.string.end_the_trip);
                        } else if (status.equalsIgnoreCase("End")) {
                            result.setStatus("End");
                            startActivity(new Intent(mContext, EndTripAct.class)
                                    .putExtra("data", data)
                            );
                            finish();
                        }
                    } else {
                        MyApplication.showToast(mContext, getString(R.string.req_cancelled));
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

    private void openNavigateionDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        NavigateDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.navigate_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_back_color);

        dialogBinding.btPickup.setOnClickListener(v -> {
            naigateToMap(pickLocation.latitude, pickLocation.longitude);
        });

        dialogBinding.btDropOff.setOnClickListener(v -> {
            naigateToMap(droplocation.latitude, droplocation.longitude);
        });

        dialog.show();

    }

    private void naigateToMap(double start, double end) {
        try {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + start + "," + end);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + start + "," + end);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            startActivity(mapIntent);
        }

    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(19).build();
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                int height = 95;
                int width = 65;
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentlocation).title("My Location")
                        .icon(smallMarkerIcon));
                animateCamera(currentLocation);
            } else {
                Log.e("sdfdsfdsfds", "Hello Marker Anuimation");
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    private void showMarkerPickUp(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (pCurrentLocationMarker == null) {
                pCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("PickUp Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
                animateCamera(currentLocation);
            }
        }
    }

    private void showDestinationMarker(@NonNull LatLng dcurrentLocation) {
        Log.e("TAG", "showDestinationMarker: " + dcurrentLocation);
        if (dcurrentLocation != null) {
            if (dcurrentLocationMarker == null) {
                dcurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(droplocation).title("Destination Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
            }
        }
    }

    private void animateCamera(@NonNull LatLng location) {
        // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);

        showMarkerPickUp(pickLocation);
        showDestinationMarker(droplocation);

        DrawPollyLine.get(this)
                .setOrigin(pickLocation)
                .setDestination(droplocation)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(latLngs);
                        options.color(Color.BLUE);
                        options.width(10);
                        options.startCap(new SquareCap());
                        options.endCap(new SquareCap());
                        Polyline line = mMap.addPolyline(options);
                    }
                });

        showMarkerCurrentLocation(currentlocation);

    }

}