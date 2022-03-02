package com.taxidriver.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.taxidriver.R;

public class TrackPoolRequestAct extends AppCompatActivity {

    Context mContext = TrackPoolRequestAct.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_pool_request);
    }

}