package com.taxidriver.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxidriver.R;
import com.taxidriver.activities.CarPoolTrackAct;
import com.taxidriver.activities.EndTripAct;
import com.taxidriver.databinding.AdapterCarPoolAcceptRejectBinding;
import com.taxidriver.databinding.DialogCancelRequestBinding;
import com.taxidriver.databinding.NavigateDialogBinding;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelUserPoolRequests;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUserRequest extends RecyclerView.Adapter<AdapterUserRequest.MyRideHolder> {

    Context mContext;
    ArrayList<ModelUserPoolRequests.Result> userRequestList;

    public AdapterUserRequest(Context mContext, ArrayList<ModelUserPoolRequests.Result> userRequestList) {
        this.mContext = mContext;
        this.userRequestList = userRequestList;
    }

    @NonNull
    @Override
    public AdapterUserRequest.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCarPoolAcceptRejectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_car_pool_accept_reject, parent, false);
        return new AdapterUserRequest.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserRequest.MyRideHolder holder, int position) {

        ModelUserPoolRequests.Result data = userRequestList.get(position);

        holder.binding.tvPickup.setText(data.getPicuplocation());
        holder.binding.tvDrop.setText(data.getDropofflocation());
        holder.binding.tvDistance.setText("Distance\n" + data.getDistance() + " Km");
        // holder.binding.tvCharge.setText("$" + data.getAmount());
        holder.binding.tvTotalPrice.setText("$" + data.getAmount());
        holder.binding.tvSeats.setText(data.getBooked_seats() + " Passenger");

        Log.e("adasdasdas", "Status = " + data.getStatus());
        Log.e("adasdasdas", "User Id = " + data.getUser_id());
        Log.e("adasdasdas", "Mobile = " + data.getUser_details().getMobile());

        if ("Cancel_by_user".equals(data.getStatus())) holder.binding.tvStatus.setText("Cancel By User");
        else holder.binding.tvStatus.setText(data.getStatus().toUpperCase());

        if (data.getUser_details().getMobile() == null ||
                data.getUser_details().getMobile().equals("") ||
                data.getUser_details().getMobile().length() < 4) {
            holder.binding.rlCall.setVisibility(View.GONE);
        } else {
            holder.binding.rlCall.setVisibility(View.VISIBLE);
        }

        holder.binding.ivCall.setOnClickListener(v -> {
            ProjectUtil.callCustomer(mContext, data.getUser_details().getMobile());
        });

        if (!"Pending".equals(data.getStatus())) {
            holder.binding.llAcceptReject.setVisibility(View.GONE);
            if ("Cancel".equals(data.getStatus()) || "Cancel_by_user".equals(data.getStatus())) {
                holder.binding.llStCaNa.setVisibility(View.GONE);
            } else {
                holder.binding.llStCaNa.setVisibility(View.VISIBLE);
            }
        } else {
            holder.binding.llAcceptReject.setVisibility(View.VISIBLE);
            holder.binding.llStCaNa.setVisibility(View.GONE);
        }

        if ("Start".equals(data.getStatus())) {
            holder.binding.btnStart.setBackgroundResource(R.drawable.red_back_50);
            holder.binding.btnStart.setText("End Trip");
        } else if ("End".equals(data.getStatus())) {
            holder.binding.btnStart.setBackgroundResource(R.drawable.end_trip_back_green);
            holder.binding.btnStart.setText("Finish Trip");
            holder.binding.btnCancel.setVisibility(View.GONE);
        } else if ("Finish".equals(data.getStatus())) {
            holder.binding.llAcceptReject.setVisibility(View.GONE);
            holder.binding.llStCaNa.setVisibility(View.GONE);
        }

        holder.binding.btAccept.setOnClickListener(v -> {
            requestAlert("Are you sure you want to accept this request?", "Accept", data.getId());
        });

        holder.binding.btReject.setOnClickListener(v -> {
            requestAlert("Are you sure you want to cancel this request?", "Cancel", data.getId());
        });

        holder.binding.btnStart.setOnClickListener(v -> {
            if (data.getStatus().equals("Accept")) {
                requestAlertForStartCancel("Are you sure you want to start the trip?", "Start", data.getId());
            } else if (data.getStatus().equals("Start")) {
                requestAlertForStartCancel("Are you sure you want to end the trip?", "End", data.getId());
            } else if (data.getStatus().equals("End")) {
                getBookingDetails(data.getId(), "DRIVER");
            }
        });

        holder.binding.btnCancel.setOnClickListener(v -> {
            dialogForCancelRequest(data.getId());
        });

        holder.binding.btnNavigate.setOnClickListener(v -> {
            LatLng pickLatLon = new LatLng(Double.parseDouble(data.getPicuplat()), Double.parseDouble(data.getPickuplon()));
            LatLng dropLatLon = new LatLng(Double.parseDouble(data.getDroplat()), Double.parseDouble(data.getDroplon()));
            openNavigateionDialog(pickLatLon, dropLatLon);
        });

    }

    private void getBookingDetails(String requestId, String type) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("request_id", requestId);
        param.put("type", type);

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBookingDetails(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("getUserRatingStatus", "getUserRatingStatus = " + result.getUserRatingStatus());
                            Log.e("getUserRatingStatus", "ModelCurrentBookingResult = " + result.getPayment_status());
                            mContext.startActivity(new Intent(mContext, EndTripAct.class)
                                    .putExtra("data", data)
                            );
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
            }

        });

    }

    private void naigateToMap(double start, double end) {
        try {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + start + "," + end);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mContext.startActivity(mapIntent);
        } catch (Exception e) {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + start + "," + end);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mContext.startActivity(mapIntent);
        }
    }

    private void dialogForCancelRequest(String id) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogCancelRequestBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.dialog_cancel_request, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_back_color);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.reason1.setOnClickListener(v -> {
            dialogBinding.reason2.setChecked(false);
            dialogBinding.reason3.setChecked(false);
        });

        dialogBinding.reason2.setOnClickListener(v -> {
            dialogBinding.reason1.setChecked(false);
            dialogBinding.reason3.setChecked(false);
        });

        dialogBinding.reason3.setOnClickListener(v -> {
            dialogBinding.reason1.setChecked(false);
            dialogBinding.reason2.setChecked(false);
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (dialogBinding.reason1.isChecked()) {
                dialog.dismiss();
                getUserRequest(id, "Cancel", "Rider Isn't Here");
            } else if (dialogBinding.reason2.isChecked()) {
                dialog.dismiss();
                getUserRequest(id, "Cancel", "Wrong Address Shown");
            } else if (dialogBinding.reason3.isChecked()) {
                dialog.dismiss();
                getUserRequest(id, "Cancel", "Don't Charge Rider");
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    private void requestAlertForStartCancel(String msg, String status, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg);
        builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUserRequest(id, status, "");
            }
        }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void openNavigateionDialog(LatLng picklatLon, LatLng droplatlon) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        NavigateDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.navigate_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_back_color);

        dialogBinding.btPickup.setOnClickListener(v -> {
            naigateToMap(picklatLon.latitude, picklatLon.longitude);
        });

        dialogBinding.btDropOff.setOnClickListener(v -> {
            naigateToMap(droplatlon.latitude, droplatlon.longitude);
        });

        dialog.show();

    }

    private void requestAlert(String msg, String status, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg);
        builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUserRequest(id, status, "");
            }
        }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void getUserRequest(String requestID, String status, String reason) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("request_id", requestID);
        map.put("status", status);
        map.put("cancel_reaison", reason);

        Call<ResponseBody> call = api.acceptRejectPoolApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("getUserRequest", "getUserRequest = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ((CarPoolTrackAct) mContext).getUserRequest();
                        Toast.makeText(mContext, mContext.getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("getUserRequest", " status = 0");
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
    public int getItemCount() {
        return userRequestList == null ? 0 : userRequestList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterCarPoolAcceptRejectBinding binding;

        public MyRideHolder(@NonNull AdapterCarPoolAcceptRejectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
