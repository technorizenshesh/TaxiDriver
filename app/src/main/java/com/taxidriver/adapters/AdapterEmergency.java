package com.taxidriver.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxidriver.R;
import com.taxidriver.activities.EmergencyContactAct;
import com.taxidriver.databinding.AdapterEmergencyContactBinding;
import com.taxidriver.models.ModelEmergencyContacts;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterEmergency extends RecyclerView.Adapter<AdapterEmergency.MyRideHolder> {

    Context mContext;
    ArrayList<ModelEmergencyContacts.Result> arrayList;

    public AdapterEmergency(Context context, ArrayList<ModelEmergencyContacts.Result> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterEmergencyContactBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_emergency_contact, parent, false);
        return new MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideHolder holder, int position) {
        holder.binding.setData(arrayList.get(position));
        holder.binding.ivDelete.setOnClickListener(v -> {
            deleteAlertDialog(position, arrayList.get(position).getId());
        });
    }

    private void deleteAlertDialog(int position, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_contact_text);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteContactApi(position, id);
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void deleteContactApi(int position, String id) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("id", id);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteEmergencyContactApi(paramHash);
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
                            arrayList.remove(position);
                            EmergencyContactAct.getContactSize(arrayList.size());
                            notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterEmergencyContactBinding binding;

        public MyRideHolder(@NonNull AdapterEmergencyContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
