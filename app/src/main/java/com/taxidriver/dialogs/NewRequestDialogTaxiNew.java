package com.taxidriver.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxidriver.R;
import com.taxidriver.activities.ActiveScheduleDriverAct;
import com.taxidriver.activities.EndTripAct;
import com.taxidriver.activities.HomeV3CubeAct;
import com.taxidriver.activities.TrackAct;
import com.taxidriver.databinding.DialogNewRequestNewBinding;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MusicManager;
import com.taxidriver.utils.MyApplication;
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
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRequestDialogTaxiNew {

    public static String TAG = "NewRequestDialog";
    private static final NewRequestDialogTaxiNew ourInstance = new NewRequestDialogTaxiNew();
    RequestDialogCallBackInterface requestDialogCallBackInterface;
    String bookType = "";

    public static NewRequestDialogTaxiNew getInstance() {
        return ourInstance;
    }

    private NewRequestDialogTaxiNew() {
    }

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {STARTED, STOPPED}

    private ProgressBar progressBarCircle;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;
    private TextView textViewTime;
    Dialog dialog;
    DialogNewRequestNewBinding binding;
    String driver_id = "", request_id = "";

    public void Request(Context context, String obj) {

        requestDialogCallBackInterface = (RequestDialogCallBackInterface) context;

        JSONObject object;

        dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_new_request_new, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        try {
            // {message={"car_type_id":"1","driver_id":"10","booktype":"NOW","shareride_type":null,"picuplocation":"indore","result":"successful","estimated_arrival_distance":"17123.34","estimated_arrival_time":"17123.34","dropofflocation":"bhopal","droplon":"77.4126","alert":"Booking request","user_id":"1","picklatertime":"08:00","droplat":"23.2599","picuplat":"22.7196","picklaterdate":"2021-02-21","request_id":10,"key":"New Booking Request","status":"Pending","pickuplon":"75.8577"}}
            Log.e("DialogChala====", "=======" + obj);
            object = new JSONObject(obj);
            if (object.get("status").equals("Cancel_by_user")) {
                Log.e("DialogChala====", "====dissssss===" + obj);
                stopCountDownTimer();
                dialog.dismiss();
            } else {
                try {
                    driver_id = String.valueOf(object.get("driver_id"));
                } catch (Exception e) {
                }

                request_id = String.valueOf(object.get("request_id"));
                binding.tvFrom.setText(object.getString("picuplocation"));
                binding.tvDestination.setText(object.getString("dropofflocation"));
                bookType = object.getString("booktype");
                if (bookType != null && bookType.equals("LATER")) {
                    binding.llLater.setVisibility(View.VISIBLE);
                    binding.laterDate.setText(" Date : " + object.getString("picklaterdate"));
                    binding.laterTime.setText("  Time : " + object.getString("picklatertime"));
                } else {
                    binding.llLater.setVisibility(View.GONE);
                }

//              binding.tvFare.setText("       :       " + "$" + object.getString("amount"));
//              // binding.tvMinuts.setText("     :  " + object.getString("estimated_arrival_time")+ " Minuts");
//              // binding.tvText.setText(object.getString("user_name"));
//              binding.tvMinuts.setText("     :     " + object.getString("distance") + " Km");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.ripple.startRippleAnimation();

        binding.btAccept.setOnClickListener(v -> {
            dialog.dismiss();
            binding.ripple.stopRippleAnimation();
            AcceptCancel(context, request_id, "Accept");
        });

        binding.btReject.setOnClickListener(v -> {
            dialog.dismiss();
            binding.ripple.stopRippleAnimation();
            AcceptCancel(context, request_id, "Cancel");
        });

        startStop();

        dialog.show();

    }

    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
            dialog.dismiss();

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
       /* int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }*/
        // assigning values after converting to milliseconds
        // timeCountInMilliSeconds = 1 * 60 * 1000;
        timeCountInMilliSeconds = 40000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                binding.textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                // binding.progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                //  textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                //  setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
                dialog.dismiss();
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
        dialog.dismiss();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {
//     binding.progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
//     binding.progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

       /* String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));*/

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        String ms[] = hms.split(":");
        return ms[1] + ":" + ms[2];

    }

    public void AcceptCancel(Context context, String request_id, String status) {

        SharedPref sharedPref = SharedPref.getInstance(context);
        ModelLogin modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", request_id);
        map.put("status", status);
        map.put("toll_charge", "0.0");
        map.put("other_charge", "0.0");
        map.put("waiting_time", "00:00:00");
        map.put("timezone", TimeZone.getDefault().getID());

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(context, false, context.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(context).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.clearNortifications(context);
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                        if (status.equals("Accept")) {
                            MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
                                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
                            MusicManager.getInstance().stopPlaying();
                            if (bookType != null && bookType.equals("LATER")) {
                                context.startActivity(new Intent(context, ActiveScheduleDriverAct.class));
                            } else {
                                requestDialogCallBackInterface.bookingApiCalled();
                            }
                            dialog.dismiss();
                        } else {
                            MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
                                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
                            MusicManager.getInstance().stopPlaying();
                            requestDialogCallBackInterface.bookingApiCalled();
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                        MyApplication.showToast(context, context.getString(R.string.req_cancelled));
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


//  public void AcceptCancel(Context context,String request_id,String status) {
//      HashMap<String, String> map = new HashMap<>();
//      map.put("driver_id", SessionManager.get(context).getUserID());
//      map.put("request_id", request_id);
//      map.put("status", status);
//      ApiCallBuilder.build(context)
//                .setUrl(BaseClass.get().AcceptCancelRequest())
//                .setParam(map)
//                .isShowProgressBar(true)
//                .execute(new ApiCallBuilder.onResponse() {
//                    @Override
//                    public void Success(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if(jsonObject.getString("status").equals("1")) {
//                                Config.clearNotifications(context);
//                                if(status.equals("Accept")) {
////                                 context.startActivity(new Intent(context, TrackAct.class)
////                                        .putExtra("request_id",request_id));
//                                    MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
//                                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
//                                    MusicManager.getInstance().stopPlaying();
//                                    requestDialogCallBackInterface.bookingApiCalled();
//                                    dialog.dismiss();
//                                } else {
//                                    MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
//                                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
//                                    MusicManager.getInstance().stopPlaying();
//                                    requestDialogCallBackInterface.bookingApiCalled();
//                                    dialog.dismiss();
//                                }
//                            } else {
//                                dialog.dismiss();
//                                App.showToast(context,context.getString(R.string.req_cancelled), Toast.LENGTH_SHORT);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void Failed(String error) {
//
//                    }
//                });
//
//    }

}
