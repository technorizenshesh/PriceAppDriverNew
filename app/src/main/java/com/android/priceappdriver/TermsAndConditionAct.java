package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.android.priceappdriver.databinding.ActivityTermsAndConditionBinding;

public class TermsAndConditionAct extends AppCompatActivity {
    ActivityTermsAndConditionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_terms_and_condition);
        binding.header.imgHeader.setOnClickListener(v ->
                {
                    finish();
                }
                );
        binding.header.tvHeader.setText(R.string.terms_and_condi);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://technorizen.com/speed_meeting/terms&condition.html");
    }
}