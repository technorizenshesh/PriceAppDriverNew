package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityContactUsBinding;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.NetworkAvailablity;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.priceappdriver.retrofit.Constant.USER_ID;
import static com.android.priceappdriver.retrofit.Constant.isValidEmail;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class ContactUs extends AppCompatActivity {
    ActivityContactUsBinding binding;
    String strUserName="",strEmail="",strCcp="",strNumber="",strMessage="";
    private ShoppingInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_contact_us);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        binding.header.imgHeader.setOnClickListener(v ->
                {
                    finish();
                }
        );
        binding.header.tvHeader.setText(R.string.contact_us);
        binding.btnSubmit.setOnClickListener(v ->
                {
                    strUserName = binding.etName.getText().toString().trim();
                    strEmail = binding.etEmail.getText().toString().trim();
                    strCcp = binding.ccp.getSelectedCountryCode();
                    strNumber = binding.etNumber.getText().toString().trim();
                    strMessage = binding.etMessage.getText().toString().trim();
                    if (isValid()) {
                        if (NetworkAvailablity.checkNetworkStatus(this)) {
                            contactUs();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void contactUs()
    {
        String userId = SharedPreferenceUtility.getInstance(ContactUs.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("name",strUserName);
        map.put("mobile",strNumber);
        map.put("email",strEmail);
        map.put("message",strMessage);

        Call<ResponseBody> call = apiInterface.contactUs(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (data.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        binding.etMessage.setText("");
                        binding.etMessage.clearFocus();
                        binding.etNumber.setText("");
                        binding.etNumber.clearFocus();
                        binding.etName.setText("");
                        binding.etName.clearFocus();
                        binding.etEmail.setText("");
                        binding.etEmail.clearFocus();
                    } else if (data.equals("0")) {
                        showToast(ContactUs.this, message);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private boolean isValid() {
        if (strUserName.equalsIgnoreCase("")) {
            binding.etName.setError(getString(R.string.enter_user_name));
            return false;
        } else  if (strEmail.equalsIgnoreCase("")) {
            binding.etEmail.setError(getString(R.string.enter_email));
            return false;
        }  else if (!isValidEmail(strEmail)) {
            binding.etEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }else if (strNumber.equalsIgnoreCase("")) {
            binding.etNumber.setError(getString(R.string.please_enter_number));
            return false;
        }else if (strMessage.equalsIgnoreCase("")) {
            binding.etMessage.setError(getString(R.string.please_enter_message));
            return false;
        }
        return true;
    }

}