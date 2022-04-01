package com.android.priceappdriver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.priceappdriver.databinding.ActivityRideDetailBinding;

public class RideDetailActivity extends AppCompatActivity {

    ActivityRideDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_detail);

        binding.GoToDriver.setOnClickListener(v ->

                {
//                    startActivity(new Intent(this, DriverDetailActivity.class));
                }
                );

        binding.btnRaiseIssue.setOnClickListener(v ->
                {
//                    RaiseIssueBottomSheet bottomSheetFragment= new RaiseIssueBottomSheet();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("link","Hello Friends");
//                    bottomSheetFragment.setArguments(bundle);
//                    bottomSheetFragment.show(getSupportFragmentManager(),"ModalBottomSheet");
                }
                );

    }
}