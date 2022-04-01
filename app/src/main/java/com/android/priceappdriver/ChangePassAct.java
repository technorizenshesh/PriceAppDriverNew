package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityChangePassBinding;
import com.android.priceappdriver.model.SuccessResChangePass;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.NetworkAvailablity;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.priceappdriver.retrofit.Constant.USER_ID;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class ChangePassAct extends AppCompatActivity {

    ActivityChangePassBinding binding;
    ShoppingInterface apiInterface;

    String oldPass = "",newConfirmPass = "" ,newPass = "", pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_change_pass);
        binding.layoutMyProfile.imgHeader.setOnClickListener(v ->
                {
                    finish();
                }
                );
        binding.layoutMyProfile.tvHeader.setText(R.string.change_pass);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        binding.layoutMyProfile.imgHeader.setOnClickListener(v -> {
          finish();
        });

        binding.layoutMyProfile.tvHeader.setText(R.string.change_pass);
   
        binding.btnLogin.setOnClickListener(v ->
                {
                    oldPass = binding.etPass.getText().toString().trim();
                    newPass = binding.etNewPass.getText().toString().trim();
                    newConfirmPass = binding.etNewConPass.getText().toString().trim();
                    if(isValid())
                    {
                        if (NetworkAvailablity.checkNetworkStatus(ChangePassAct.this)) {
                            changePassword();
                        } else {
                            Toast.makeText(ChangePassAct.this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(ChangePassAct.this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private boolean isValid() {
        if (oldPass.equalsIgnoreCase("")) {
            binding.etPass.setError(getString(R.string.please_enter_old_pass));
            return false;
        }else if (newPass.equalsIgnoreCase("")) {
            binding.etNewPass.setError(getString(R.string.enter_new_password));
            return false;
        } else if (newConfirmPass.equalsIgnoreCase("")) {
            binding.etPass.setError(getString(R.string.please_enter_confirm_password));
            return false;
        }else if (!newConfirmPass.equalsIgnoreCase(newPass)) {
            binding.etNewConPass.setError(getString(R.string.password_mismatched));
            return false;
        }else if (!oldPass.equalsIgnoreCase(pass)) {
            binding.etPass.setError(getString(R.string.old_password_mismatched));
            return false;
        }
        return true;
    }

    public void changePassword()
    {
        String userId = SharedPreferenceUtility.getInstance(ChangePassAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(ChangePassAct.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("password",newPass);
        Call<SuccessResChangePass> call = apiInterface.changePass(map);
        call.enqueue(new Callback<SuccessResChangePass>() {
            @Override
            public void onResponse(Call<SuccessResChangePass> call, Response<SuccessResChangePass> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResChangePass data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        showToast(ChangePassAct.this, data.message);
                        String dataResponse = new Gson().toJson(response.body());
                        binding.etNewPass.setText("");
                        binding.etPass.setText("");
                        binding.etNewConPass.setText("");
                    } else if (data.status.equals("0")) {
                        showToast(ChangePassAct.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResChangePass> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}