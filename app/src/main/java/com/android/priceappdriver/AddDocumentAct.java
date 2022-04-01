package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.android.priceappdriver.databinding.ActivityAddDocumentBinding;


public class AddDocumentAct extends AppCompatActivity {

    ActivityAddDocumentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_document);

        binding.header.tvHeader.setText(R.string.add_doc);
        binding.header.imgHeader.setOnClickListener(v -> finish());

    }
}