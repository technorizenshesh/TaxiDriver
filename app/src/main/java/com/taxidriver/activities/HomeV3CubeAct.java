package com.taxidriver.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityHomeV3CubeBinding;
import com.taxidriver.dialogs.NewRequestDialogTaxiNew;
import com.taxidriver.fragments.BookingFragment;
import com.taxidriver.fragments.HomeFragment;
import com.taxidriver.fragments.ProfileFragment;
import com.taxidriver.fragments.WalletFragment;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MusicManager;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.RequestDialogCallBackInterface;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeV3CubeAct extends AppCompatActivity implements
        NavigationBarView.OnItemSelectedListener, RequestDialogCallBackInterface {

    Context mContext = HomeV3CubeAct.this;
    ActivityHomeV3CubeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private boolean isOnREsumeCalled;
    private String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_v3_cube);
        sharedPref = SharedPref.getInstance(mContext);
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

        if (getIntent().getStringExtra("object") != null) {
            Log.e("DialogChala123", "Object = " + getIntent().getStringExtra("object"));
            MusicManager.getInstance().initalizeMediaPlayer(mContext, Uri.parse
                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName()
                            + "/" + R.raw.doogee_ringtone));
            MusicManager.getInstance().stopPlaying();
            Log.e("DialogChala====", "DialogChala Neeche" + getIntent().getStringExtra("object"));
            NewRequestDialogTaxiNew.getInstance().Request(mContext, getIntent().getStringExtra("object"));
        }

        itit();
        getProfile();
    }

    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("object") != null) {
                JSONObject object = null;
                try {
                    object = new JSONObject(intent.getStringExtra("object"));
                    if ("Pending".equals(object.getString("status"))) {
                        Log.e("DialogChala123", "BroadcastReceiver Dialog = " + object.getString("status"));
                        NewRequestDialogTaxiNew.getInstance().Request(mContext, intent.getStringExtra("object"));
                    } else getCurrentBooking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals("data_update_location")) {
                double lat = intent.getDoubleExtra("latitude", 0);
                double lng = intent.getDoubleExtra("longitude", 0);
                float bearing = intent.getFloatExtra("bearing", 0);
                //                if (carMarker != null) {
//                    Log.e("locationResult", "" + bearing);
//                    carMarker.position(new LatLng(lat, lng));
//                    ProjectUtil.rotateMarker(carMarker, bearing);
//                }
            } else {
                getCurrentBooking();
            }
        }
    };

    private void itit() {
        loadFragment(new HomeFragment());
        binding.navigation.setOnItemSelectedListener(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your session is expired Please login Again!")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPref.clearAllPreferences();
                                finishAffinity();
                                startActivity(new Intent(mContext, LoginAct.class));
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public void getCurrentBooking() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "DRIVER");
        param.put("timezone", TimeZone.getDefault().getID());

        Log.e("paramparam", "param = " + param);
        Log.e("paramparam", "getCurrentBooking Called");

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBooking(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("asdasdasdas", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("asdasdasdas", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {

                            Log.e("asdasdasdas", "data = " + new Gson().toJson(data));

                            ModelCurrentBookingResult result = data.getResult().get(0);
                            isOnREsumeCalled = false;

                            Log.e("asdasdasdas", "ModelCurrentBookingResult = " + new Gson().toJson(result));

                            if (result.getStatus().equalsIgnoreCase("Pending")) {

                            } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                Intent j = new Intent(mContext, EndTripAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            }
                        }
                    } else {

                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .addToBackStack(null)
                    .commit();

            return true;

        }

        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnREsumeCalled = true;
        unregisterReceiver(JobStatusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action_Taxi"));

        MusicManager.getInstance().initalizeMediaPlayer(HomeV3CubeAct.this, Uri.parse
                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
        MusicManager.getInstance().stopPlaying();

        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action_Taxi"));

        Log.e("fdasfasfasf", "isOnREsumeCalled After = " + isOnREsumeCalled);

        // getCurrentBooking();

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            ProjectUtil.exitAppDialog(mContext);
            // exitAppDialog();
            finish();
            // startActivity(new Intent(mContext, HomeV3CubeAct.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.home:
                loadFragment(new HomeFragment());
                break;

            case R.id.booking:
                loadFragment(new BookingFragment());
                break;

            case R.id.wallet:
                loadFragment(new WalletFragment());
                break;

            case R.id.profile:
                loadFragment(new ProfileFragment());
                break;

        }

        return true;

    }

    @Override
    public void bookingApiCalled() {
        Log.e("paramparam", "bookingApiCalled");
        getCurrentBooking();
    }

}