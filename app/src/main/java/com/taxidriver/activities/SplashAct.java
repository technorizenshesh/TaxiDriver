package com.taxidriver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MyService;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.vehicles.AddVehicleAct;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashAct extends AppCompatActivity {

    Context mContext = SplashAct.this;
    int PERMISSION_ID = 44;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean isDialogEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = SharedPref.getInstance(mContext);
        printHashKey(mContext);
    }

    public static void printHashKey(Context pContext) {
        Log.i("dsadadsdad", "printHashKey() Hash Key: aaya ander");
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("dsadadsdad", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("dsadadsdad", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("dsadadsdad", "printHashKey()", e);
        }
    }

    @Override
    protected void onResume() {

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (backgroundLocationPermissionApproved != 0) {
                if (isDialogEnable) {
                    isDialogEnable = false;
                    showLocationDialog();
                } else {
                    if (checkPermissions()) {
                        if (isLocationEnabled()) {
                            processNextActivity();
                        } else {
                            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    } else {
                        requestPermissions();
                    }
                }
            } else {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        processNextActivity();
                    } else {
                        Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {
                    requestPermissions();
                }
            }
        } else {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    processNextActivity();
                } else {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                requestPermissions();
            }
        }
        super.onResume();
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle("Need Important Permission");
        builder.setMessage("Click on Ok than -> \n\n Choose Location Permission ->" +
                "\n Allow all the time \n\n Than come back to Application");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDialogEnable = true;
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).create().show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                /*ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&*/
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions (
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNextActivity();
            }
        }

    }

    private void processNextActivity() {

        Log.e("adfasdfss", "processNextActivity");

        if ("en".equals(sharedPref.getLanguage("lan"))) {
            ProjectUtil.updateResources(mContext, "en");
        } else if ("ar".equals(sharedPref.getLanguage("lan"))) {
            ProjectUtil.updateResources(mContext, "ar");
        } else if ("fr".equals(sharedPref.getLanguage("lan"))) {
            ProjectUtil.updateResources(mContext, "fr");
        } else if ("ur".equals(sharedPref.getLanguage("lan"))) {
            ProjectUtil.updateResources(mContext, "ur");
        } else if ("zh".equals(sharedPref.getLanguage("lan"))) {
            ProjectUtil.updateResources(mContext, "zh");
        } else {
            sharedPref.setlanguage("lan", "en");
            ProjectUtil.updateResources(mContext, "en");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

                    ContextCompat.startForegroundService(SplashAct.this, new Intent(SplashAct.this, MyService.class));

                    Log.e("adfasdfss", "getDriver_lisence = " + modelLogin.getResult().getDriver_lisence());
                    Log.e("adfasdfss", "modelLogin = " + new Gson().toJson(modelLogin));
                    if (modelLogin.getResult().getStep().equals("1")) {
                        if(modelLogin.getResult().getCar_type_id() == null ||
                                modelLogin.getResult().getCar_type_id().equals("")){
                            startActivity(new Intent(mContext, AddVehicleAct.class));
                            finish();
                        }
                    } else if (modelLogin.getResult().getStep().equals("2")) {
                        if (modelLogin.getResult().getDriver_lisence() == null ||
                                "".equals(modelLogin.getResult().getDriver_lisence())) {
                            startActivity(new Intent(mContext, UpdateDocAct.class));
                            finish();
                        } else {
                            startActivity(new Intent(mContext, HomeV3CubeAct.class));
                            finish();
                        }
                    } else {
                        Intent i = new Intent(SplashAct.this, LoginAct.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Intent i = new Intent(SplashAct.this, LoginAct.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);

    }

}