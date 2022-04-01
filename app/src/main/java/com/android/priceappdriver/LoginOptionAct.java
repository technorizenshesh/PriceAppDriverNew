package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.android.priceappdriver.databinding.ActivityLoginOptionBinding;

public class LoginOptionAct extends AppCompatActivity {

    ActivityLoginOptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login_option);
        binding.btnLogin.setOnClickListener(view ->
                {
                    startActivity(new Intent(LoginOptionAct.this,LoginAct.class));
                }
                );
        binding.btnCreateAcc.setOnClickListener(view ->
                {
                    startActivity(new Intent(LoginOptionAct.this,SignupAct.class));
                }
        );
    }
}