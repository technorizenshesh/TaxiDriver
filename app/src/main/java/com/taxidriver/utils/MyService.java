package com.taxidriver.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service {

    public static String TAG ="MyService";
    FusedLocationProviderClient mFusedLocationClient;
    public static final int notify = 5000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    private SharedPref sharedPref;
    private ModelLogin modelLogin;
    private long UPDATE_INTERVAL = 3000; /* 5 secs */
    private long FASTEST_INTERVAL = 3000;
    private LocationRequest mLocationRequest;

    public MyService() {}

    @Override
    public void onCreate() {
        sharedPref = SharedPref.getInstance(this);
        requestNewLocationData();
        String channelId = "channel-01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }

        if (mTimer != null) mTimer.cancel();
        else mTimer = new Timer();

        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {

        String NOTIFICATION_CHANNEL_ID = "com.taxidriver";
        String channelName = "My Background Service";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("service is ", "running");
                    try {
                        // startLocationUpdates();
                        getLastLocation();
                    } catch (Exception e){}
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        try {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                Intent intent1 = new Intent("data_update_location");
                                intent1.putExtra("lat", location.getLatitude());
                                intent1.putExtra("lon", location.getLongitude());

                                Log.e("c","Lat = " + String.valueOf(location.getLatitude()));
                                Log.e("fasdasdasd","Lon = " + String.valueOf(location.getLongitude()));

                                sendBroadcast(intent1);

                                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                                   updateProviderLatLon(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                }

                            }
                        } catch (Exception e) {}

                    }
                }

        );

    }

    public void updateProviderLatLon(String lat,String lon) {

        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        HashMap<String,String> map = new HashMap<>();
        map.put("user_id",modelLogin.getResult().getId());
        map.put("lat",lat);
        map.put("lon",lon);
        Log.e(TAG,"Upldate Driver Location Request " + map);
        Api api = ApiFactory.getClientWithoutHeader(this).create(Api.class);
        Call<Map<String,String>> loginCall = api.updateLocation(map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call,Response<Map<String,String>> response) {
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG,"Upldate Driver Location Response :" + responseString);
                    // getCurrentBooking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                call.cancel();
            }

        });

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if (mLastLocation == null) {
                requestNewLocationData();
            } else {
                Log.e("user Latitude", "" + mLastLocation.getLatitude() + "");
                Log.e("user Longitude", "" + mLastLocation.getLongitude() + "");
                Intent intent1 = new Intent("data_update_location");
                intent1.putExtra("latitude", mLastLocation.getLatitude());
                intent1.putExtra("longitude", mLastLocation.getLongitude());
                intent1.putExtra("bearing", mLastLocation.getBearing());
                sendBroadcast(intent1);
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("service in onTaskRemoved");
        long ct = System.currentTimeMillis(); // get current time
        Intent restartService = new Intent(getApplicationContext(), MyService.class);
        PendingIntent restartServicePI = PendingIntent.getService (
                getApplicationContext(), 0, restartService,0);

        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 1000, restartServicePI);
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); // true will remove notification
//          this.stopSelf();
        }
    }


}
