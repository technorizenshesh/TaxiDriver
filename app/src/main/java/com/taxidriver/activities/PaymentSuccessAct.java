package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.taxidriver.R;

public class PaymentSuccessAct extends AppCompatActivity {

    Context mContext = PaymentSuccessAct.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
    }


}