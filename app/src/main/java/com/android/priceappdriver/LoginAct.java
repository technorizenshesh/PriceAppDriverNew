package com.android.priceappdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityLoginBinding;
import com.android.priceappdriver.model.SuccessResLogin;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.Constant;
import com.android.priceappdriver.retrofit.NetworkAvailablity;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.priceappdriver.retrofit.Constant.showToast;

public class LoginAct extends AppCompatActivity {

    String str_image_path = "";
    private static final int RC_SIGN_IN = 9001;
    String  strPass = "",strCountryCode="",deviceToken = "",strPhoneNumber = "";
    private FirebaseAuth mAuth;
    ShoppingInterface apiInterface;
    public static String TAG = "LoginActivity";
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.cvCard.setOnClickListener(view -> finish());
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        getToken();
        binding.tvForgot.setOnClickListener(v ->
                {
                    startActivity(new Intent(LoginAct.this,ForgotPasswordAct.class));
                }
                );

        binding.btnLogin.setOnClickListener(v -> {
            strPhoneNumber = binding.etNumber.getText().toString().trim();
            strCountryCode = binding.ccp.getSelectedCountryCode();
            strPass = binding.etPass.getText().toString().trim();
            if(isValid())
            {
                if (NetworkAvailablity.checkNetworkStatus(LoginAct.this)) {
                    login();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this,  getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

     private void login() {
        DataManager.getInstance().showProgressMessage(LoginAct.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("mobile",strPhoneNumber);
        map.put("country_code",strCountryCode);
        map.put("password",strPass);
        map.put("register_id",deviceToken);
        Call<SuccessResLogin> call = apiInterface.login(map);
        call.enqueue(new Callback<SuccessResLogin>() {
            @Override
            public void onResponse(Call<SuccessResLogin> call, Response<SuccessResLogin> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResLogin data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        if(data.getResult().getCarTypeId().equalsIgnoreCase("0"))
                        {
                            SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_ID,data.getResult().getId());
                            startActivity(new Intent(LoginAct.this,AddVehicleAct.class).putExtra("from","login"));
                        } else if(data.getResult().getAccountNumber().equalsIgnoreCase(""))
                        {
                            SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_ID,data.getResult().getId());
                            startActivity(new Intent(LoginAct.this,AddCardDetailAct.class).putExtra("from","login"));
                        } else
                        {
                            SharedPreferenceUtility.getInstance(getApplication()).putBoolean(Constant.IS_USER_LOGGED_IN, true);
                            SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_ID,data.getResult().getId());
                            Toast.makeText(LoginAct.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginAct.this, HomeAct.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    } else if (data.status.equals("0")) {
                        showToast(LoginAct.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResLogin> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private boolean isValid() {
        if (strPhoneNumber.equalsIgnoreCase("")) {
            binding.etNumber.setError(getString(R.string.enter_number));
            return false;
        } else if (strPass.equalsIgnoreCase("")) {
            binding.etPass.setError(getString(R.string.please_enter_pass));
            return false;
        }
        return true;
    }

    private void getToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            // Get new FCM registration token
                            String token = task.getResult();
                            deviceToken = token;
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(LoginAct.this, "Error=>" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}