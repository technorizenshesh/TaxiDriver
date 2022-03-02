package com.taxidriver.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxidriver.R;
import com.taxidriver.databinding.AdapterManageVehiclesBinding;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelVehicle;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.MyApplication;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import com.taxidriver.vehicles.EditVehicleAct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterManageVehicle extends RecyclerView.Adapter<AdapterManageVehicle.MyHolderManager> {

    Context mContext;
    ArrayList<ModelVehicle.Result> vehicleList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterManageVehicle(Context mContext, ArrayList<ModelVehicle.Result> vehicleList) {
        this.mContext = mContext;
        this.vehicleList = vehicleList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyHolderManager onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterManageVehiclesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_manage_vehicles, parent, false);
        return new MyHolderManager(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolderManager holder, int position) {

        ModelVehicle.Result data = vehicleList.get(position);

        holder.binding.carName.setText(data.getMake_name());
        holder.binding.CarNumber.setText(data.getCar_number());

        holder.binding.ivEdit.setOnClickListener(v -> {
            if (data.getOnline_status().equals("ONLINE")) {
                MyApplication.showAlert(mContext, mContext.getString(R.string.online_vehicle_update_note));
            } else {
                mContext.startActivity(new Intent(mContext, EditVehicleAct.class)
                        .putExtra("data", data)
                );
            }
        });

        holder.binding.ivDelete.setOnClickListener(v -> {
            if(vehicleList.size() == 1) {
                MyApplication.showAlert(mContext, mContext.getString(R.string.you_have_one_vehicle_text));
            } else {
                if (data.getCar_type_status().equals("ONLINE")) {
                    MyApplication.showAlert(mContext, mContext.getString(R.string.active_cannot_delete));
                } else {
                    deleteVehicleDialog(position, data.getId());
                }
            }
        });

    }

    private void deleteVehicleDialog(int position, String vehicleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.delete_vehicle_text));
        builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteVehicleApi(vehicleId, position);
            }
        }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void deleteVehicleApi(String vrehicleId, int position) {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("vehicle_id", vrehicleId);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteCarApi(paramHash);
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
                            vehicleList.remove(position);
                            notifyDataSetChanged();
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

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
    public int getItemCount() {
        return vehicleList == null ? 0 : vehicleList.size();
    }

    public class MyHolderManager extends RecyclerView.ViewHolder {
        AdapterManageVehiclesBinding binding;

        public MyHolderManager(AdapterManageVehiclesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
