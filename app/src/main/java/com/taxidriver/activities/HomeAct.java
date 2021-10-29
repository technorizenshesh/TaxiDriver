package com.taxidriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.taxidriver.R;
import com.taxidriver.databinding.ActivityHomeBinding;
import com.taxidriver.databinding.ChangeLanguageDialogBinding;
import com.taxidriver.dialogs.NewRequestDialogTaxiNew;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.RequestDialogCallBackInterface;
import com.taxidriver.utils.SharedPref;

public class HomeAct extends AppCompatActivity implements OnMapReadyCallback, RequestDialogCallBackInterface {

    Context mContext = HomeAct.this;
    ActivityHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    SupportMapFragment mapFragment;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeAct.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NewRequestDialogTaxiNew.getInstance().Request(mContext, "");
                // NewRequestDialogTaxi.getInstance().Request(mContext,"");
            }
        }, 3000);

        itit();

    }

    private void itit() {

        binding.childDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvProfile.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.childNavDrawer.tvInvoice.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, InvoiceAct.class));
        });

        binding.childNavDrawer.tvDriverPref.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, DriverPrefrencesAct.class));
        });

        binding.childNavDrawer.tvAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, AccountAct.class));
        });

        binding.childNavDrawer.tvNotification.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, NotifyAct.class));
        });

        binding.childNavDrawer.tvMessage.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ChatListAct.class));
        });

        binding.childNavDrawer.tvWallet.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, WalletAct.class));
        });

        binding.childNavDrawer.tvRatingReview.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, RatingReviewAct.class));
        });

        binding.childNavDrawer.tvSubs.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, SubscriptionAct.class));
        });

        binding.childNavDrawer.tvRideHistory.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, RideHistoryAct.class));
        });

        binding.childNavDrawer.tvChnageLang.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            changeLangDialog();
        });

    }

    private void changeLangDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ChangeLanguageDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.change_language_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.rbEnglish.setOnClickListener(v -> {
            dialogBinding.rbItalian.setChecked(false);
            dialogBinding.rbSpanish.setChecked(false);
            dialogBinding.rbGerman.setChecked(false);
        });

        dialogBinding.rbItalian.setOnClickListener(v -> {
            dialogBinding.rbEnglish.setChecked(false);
            dialogBinding.rbSpanish.setChecked(false);
            dialogBinding.rbGerman.setChecked(false);
        });

        dialogBinding.rbSpanish.setOnClickListener(v -> {
            dialogBinding.rbEnglish.setChecked(false);
            dialogBinding.rbItalian.setChecked(false);
            dialogBinding.rbGerman.setChecked(false);
        });

        dialogBinding.rbGerman.setOnClickListener(v -> {
            dialogBinding.rbEnglish.setChecked(false);
            dialogBinding.rbItalian.setChecked(false);
            dialogBinding.rbSpanish.setChecked(false);
        });

        dialogBinding.btnSaveLang.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void bookingApiCalled() {

    }

}