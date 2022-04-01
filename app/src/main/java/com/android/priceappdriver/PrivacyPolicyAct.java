package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.android.priceappdriver.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyAct extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_privacy_policy);
        binding.header.imgHeader.setOnClickListener(v ->
                {
                    finish();
                }
        );
        binding.header.tvHeader.setText(R.string.privacy_policy);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("https://priceapp.co.za/privacy.html");
    }
}