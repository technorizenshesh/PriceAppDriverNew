package com.android.priceappdriver;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.android.priceappdriver.databinding.ActivityAboutUsBinding;
public class AboutUs extends AppCompatActivity {
    ActivityAboutUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_about_us);
        binding.header.imgHeader.setOnClickListener(v ->
                {
                    finish();
                }
        );
        binding.header.tvHeader.setText(R.string.about_us);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://priceapp.co.za/about.html");
    }
}