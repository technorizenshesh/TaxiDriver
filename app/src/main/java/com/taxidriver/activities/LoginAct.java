package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityLoginBinding;

public class LoginAct extends AppCompatActivity {

    Context mContext = LoginAct.this;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        itit();
    }

    private void itit() {

        binding.btnSignin.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HomeAct.class));
        });

        binding.ivForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ForgotPassAct.class));
        });

        binding.btSignup.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SignUpAct.class));
        });

    }
}