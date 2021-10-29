package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;
import com.taxidriver.databinding.ActivityForgotPassBinding;

public class ForgotPassAct extends AppCompatActivity {
    Context mContext = ForgotPassAct.this;
    ActivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSubmit.setOnClickListener(v -> {
            finish();
//            if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
//                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
//            } else {
//                if(InternetConnection.checkConnection(mContext)) forgotPassApiCall();
//                else Toast.makeText(mContext, getString(R.string.check_internet_text), Toast.LENGTH_LONG).show();
//            }
        });

    }


}