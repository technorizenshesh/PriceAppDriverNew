package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.android.priceappdriver.databinding.ActivitySettingBinding;

public class SettingAct extends AppCompatActivity {

    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting);
        binding.header.tvHeader.setText(R.string.setting);
        binding.header.imgHeader.setOnClickListener(v -> finish());
        binding.tvProfile.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,ProfileAct.class));
                }
                );

        binding.tvTextPass.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,ChangePassAct.class));
                }
        );

        binding.bnkDetail.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,EditBankDetail.class));
                }
                );

        binding.term.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,TermsAndConditionAct.class));
                }
        );

        binding.pp.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,PrivacyPolicyAct.class));
                }
        );
        binding.about.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,AboutUs.class));
                }
        );

        binding.tvVechile.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,EditVehicleAct.class));
                }
        );

        binding.contactUs.setOnClickListener(v ->
                {
                    startActivity(new Intent(SettingAct.this,ContactUs.class));
                }
        );

    }
}