package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityForgotPasswordBinding;
import com.android.priceappdriver.model.SuccessResForgotPassword;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.NetworkAvailablity;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.priceappdriver.retrofit.Constant.isValidEmail;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class ForgotPasswordAct extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    private String strEmail = "";
    ShoppingInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_password);

        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);

        binding.forgotAction.imgHeader.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        binding.forgotAction.tvHeader.setText(R.string.forgot);

        binding.btnSend.setOnClickListener(v ->
                {

                    strEmail = binding.etEmail.getText().toString().trim();

                    if(isValid())
                    {
                        if (NetworkAvailablity.checkNetworkStatus(this)) {
                            forgotPass();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void forgotPass() {

        DataManager.getInstance().showProgressMessage(ForgotPasswordAct.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("email",strEmail);
        Call<SuccessResForgotPassword> call = apiInterface.forgotPassword(map);
        call.enqueue(new Callback<SuccessResForgotPassword>() {
            @Override
            public void onResponse(Call<SuccessResForgotPassword> call, Response<SuccessResForgotPassword> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResForgotPassword data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        Toast.makeText(ForgotPasswordAct.this, ""+R.string.check_email,Toast.LENGTH_SHORT).show();

                        binding.etEmail.setText("");
                    } else if (data.status.equals("0")) {
                        showToast(ForgotPasswordAct.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResForgotPassword> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private boolean isValid() {
        if (strEmail.equalsIgnoreCase("")) {
            binding.etEmail.setError("Please enter email.");
            return false;
        } else if (!isValidEmail(strEmail)) {
            binding.etEmail.setError("Please enter valid email.");
            return false;
        }
        return true;
    }

}