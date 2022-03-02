package com.taxidriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxidriver.R;
import com.taxidriver.activities.RideDetailsAct;
import com.taxidriver.databinding.ItemRideHistoryBinding;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelHistory;
import com.taxidriver.utils.AppConstant;
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

public class AdapterRideHistory extends RecyclerView.Adapter<AdapterRideHistory.MyRideHolder> {

    Context mContext;
    ArrayList<ModelHistory.Result> historyList;

    public AdapterRideHistory(Context mContext, ArrayList<ModelHistory.Result> historyList) {
        this.mContext = mContext;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_history, parent, false);
        return new MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideHolder holder, int position) {

        ModelHistory.Result data = historyList.get(position);

        holder.binding.tvFrom.setText(data.getPicuplocation());
        holder.binding.etDestination.setText(data.getDropofflocation());
        holder.binding.tvDateTime.setText(data.getAccept_time());
        holder.binding.tvStatus.setText(data.getStatus());

        holder.binding.getRoot().setOnClickListener(v -> {
            getBookingDetails(data.getId(), AppConstant.DRIVER, data);
        });

    }

    private void getBookingDetails(String requestId, String type, ModelHistory.Result dataHistory) {

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

                    Log.e("getBookingDetails", "responseString = " + responseString);

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
                            mContext.startActivity(new Intent(mContext, RideDetailsAct.class)
                                    .putExtra("data", data)
                                    .putExtra("datahistory", dataHistory)
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

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideHistoryBinding binding;

        public MyRideHolder(@NonNull ItemRideHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
