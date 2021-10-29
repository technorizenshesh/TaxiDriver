package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivitySignUpBinding;

public class SignUpAct extends AppCompatActivity {

    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvTerms.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TermConditionAct.class));
        });

        binding.btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(mContext, VerifyAct.class));
        });

        binding.ivAlready.setOnClickListener(v -> {
            finish();
        });

    }
}