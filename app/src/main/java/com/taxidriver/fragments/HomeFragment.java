package com.taxidriver.fragments;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.activities.LoginAct;
import com.taxidriver.adapters.AdapterVehicleSelect;
import com.taxidriver.databinding.FragmentHomeBinding;
import com.taxidriver.databinding.SanitixzeAgreeDialogBinding;
import com.taxidriver.databinding.TaxiSelectDialogBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelVehicle;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.LatLngInterpolator;
import com.taxidriver.utils.MarkerAnimation;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.taxidriver.vehicles.AddVehicleAct;
import com.taxidriver.vehicles.ManageVehicleAct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    Context mContext;
    FragmentHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    GoogleMap mMap;
    private Marker currentLocationMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    private Location mLocation;
    SupportMapFragment mapFragment;
    private Location currentLocation;
    String carTypeId = "";
    private FusedLocationProviderClient fusedLocationClient;
    ModelVehicle modelVehicle;
    private String registerId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

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

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        itit();
        // Inflate the layout for this fragment
        return binding.getRoot();

    }

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your session is expired Please login Again!")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPref.clearAllPreferences();
                                getActivity().finishAffinity();
                                startActivity(new Intent(mContext, LoginAct.class));
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    @SuppressLint("MissingPermission")
    private void itit() {

        startLocationUpdates();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        binding.ivChange.setOnClickListener(v -> {
            getVehicles();
        });

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                        }
                    }
                });

        Log.e("adsasdasd", "Image = " + modelLogin.getResult().getImage());

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(binding.cvImg);
        // binding.tvName.setText(modelLogin.getResult().getUser_name());

        binding.switch4.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if (isOn) {
                    getProfileNew();
                } else {
                    onlinOfflineApi("OFFLINE");
                }
            }
        });

        binding.ivGPS.setOnClickListener(v -> {
            if (currentLocation != null) {
                animateCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            }
        });

    }

    public void setCarDetails(ModelVehicle.Result data) {
        binding.tvCarName.setText(data.getMake_name());
        binding.tvCarNumber.setText(data.getCar_number());
        carTypeId = data.getId();
    }

    private void openVehicleDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        TaxiSelectDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.taxi_select_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterVehicleSelect adapterVehicleSelect = new AdapterVehicleSelect(mContext, modelVehicle.getResult(), dialog, HomeFragment.this::setCarDetails, carTypeId);
        dialogBinding.carList.setAdapter(adapterVehicleSelect);

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialogBinding.btManage.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ManageVehicleAct.class));
            dialog.dismiss();
        });

        dialogBinding.btnAddNew.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddVehicleAct.class)
                    .putExtra("type", "manage")
            );
            dialog.dismiss();
        });

        dialog.show();

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

    private void getProfileNew() {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
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
                            sharedPref.setUserDetails(AppConstant.USER, modelLogin);
                            carTypeId = modelLogin.getResult().getCar_type_id();

                            Log.e("afsfdsgdfgs", "modellogin car Profile = " + modelLogin.getResult().getCar_type_id());

                            if ("ONLINE".equals(modelLogin.getResult().getOnline_status())) {
                                binding.switch4.setOn(true);
                            } else {
                                binding.switch4.setOn(false);
                            }

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

                            if (modelLogin.getResult().getCovid_screen_status() != null) {
                                if (modelLogin.getResult().getCovid_screen_status().equalsIgnoreCase("Active")) {
                                    sanitizeDialog();
                                } else {
                                    onlinOfflineApi("ONLINE");
                                }
                            } else {
                                onlinOfflineApi("ONLINE");
                            }

                            binding.tvCarName.setText(modelLogin.getResult().getMake_name());
                            binding.tvCarNumber.setText(modelLogin.getResult().getCar_number());

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
                            sharedPref.setUserDetails(AppConstant.USER, modelLogin);
                            carTypeId = modelLogin.getResult().getCar_type_id();

                            Log.e("afsfdsgdfgs", "modellogin car Profile = " + modelLogin.getResult().getCar_type_id());

                            if ("ONLINE".equals(modelLogin.getResult().getOnline_status())) {
                                binding.switch4.setOn(true);
                            } else {
                                binding.switch4.setOn(false);
                            }

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }
                            binding.tvCarName.setText(modelLogin.getResult().getMake_name());
                            binding.tvCarNumber.setText(modelLogin.getResult().getCar_number());

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void sanitizeDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SanitixzeAgreeDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.sanitixze_agree_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btCancel.setOnClickListener(v -> {
            binding.switch4.setOn(false);
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(getActivity())
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

    @Override
    public void onResume() {
        super.onResume();
        getProfile();
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {

        Log.e("asdfasfasf", "Location Akash = " + currentLocation);
        Log.e("asdfasfasf", "currentLocation = " + currentLocation);
        Log.e("asdfasfasf", "currentLocationMarker = " + currentLocationMarker);

        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (mMap != null) {
                    Log.e("gdfgdfsdfdf", "Map Andar = " + currentLocation);
                    int height = 95;
                    int width = 65;
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location")
                            .icon(smallMarkerIcon));
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
        return new CameraPosition.Builder().target(latLng).zoom(18).build();
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
                            binding.switch4.setOn(true);
                        } else {
                            modelLogin.getResult().setOnline_status("OFFLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.switch4.setOn(false);
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

}