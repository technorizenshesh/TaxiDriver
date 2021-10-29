package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityRatingReviewBinding;

public class RatingReviewAct extends AppCompatActivity {

    Context mContext = RatingReviewAct.this;
    ActivityRatingReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rating_review);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }


}