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
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxidriver.R;
import com.taxidriver.activities.EndTripAct;
import com.taxidriver.databinding.AdapterUserReviewBinding;
import com.taxidriver.databinding.ItemRideHistoryBinding;
import com.taxidriver.models.ModelActiveBooking;
import com.taxidriver.models.ModelCurrentBooking;
import com.taxidriver.models.ModelCurrentBookingResult;
import com.taxidriver.models.ModelReviews;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUserFeedback extends RecyclerView.Adapter<AdapterUserFeedback.MyRideHolder> {

    Context mContext;
    ArrayList<ModelReviews.Result> reviewsList;

    public AdapterUserFeedback(Context mContext, ArrayList<ModelReviews.Result> reviewsList) {
        this.mContext = mContext;
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public AdapterUserFeedback.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterUserReviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_user_review, parent, false);
        return new AdapterUserFeedback.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserFeedback.MyRideHolder holder, int position) {

        ModelReviews.Result data = reviewsList.get(position);

        Glide.with(mContext).load(data.getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(holder.binding.ivUserPic);

        holder.binding.tvComment.setText(data.getReview());
        holder.binding.tvName.setText(data.getUser_name());
        holder.binding.tvDateTime.setText(data.getDate_time());

        try {
            holder.binding.rbRating.setRating(Float.parseFloat(data.getRating()));
        } catch (Exception e){}


    }

    @Override
    public int getItemCount() {
        return reviewsList == null ? 0 : reviewsList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterUserReviewBinding binding;

        public MyRideHolder(@NonNull AdapterUserReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
