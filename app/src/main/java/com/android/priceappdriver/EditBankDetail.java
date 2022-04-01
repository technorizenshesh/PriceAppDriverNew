package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityEditBankDetailBinding;
import com.android.priceappdriver.model.SuccessResLogin;
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

public class EditBankDetail extends AppCompatActivity {

    ActivityEditBankDetailBinding binding;

    String bankName = "",holderName="",accountNumber = "",ifscCode="";

    private ShoppingInterface apiInterface;

    private SuccessResLogin.Result userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_bank_detail);
        binding.header.imgHeader.setOnClickListener(v -> finish());
        binding.header.tvHeader.setText(R.string.edit_bank_details);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);

        if (NetworkAvailablity.checkNetworkStatus(EditBankDetail.this)) {
            getProfile();
        } else {
            Toast.makeText(EditBankDetail.this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
        }

        binding.btnAdd.setOnClickListener(v ->
                {

                    bankName = binding.etBankName.getText().toString();
                    holderName = binding.etAccountHoldeName.getText().toString();
                    accountNumber = binding.etAccountNumber.getText().toString();
                    ifscCode = binding.etFyiCode.getText().toString();

                    if (isValid()) {
                        if (NetworkAvailablity.checkNetworkStatus(this)) {
                            editBankDetail();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void getProfile() {

        String userId = SharedPreferenceUtility.getInstance(EditBankDetail.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(EditBankDetail.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        Call<SuccessResLogin> call = apiInterface.getProfile(map);
        call.enqueue(new Callback<SuccessResLogin>() {
            @Override
            public void onResponse(Call<SuccessResLogin> call, Response<SuccessResLogin> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResLogin data = response.body();
                    userDetail = data.getResult();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        setBankDetails();
                    } else if (data.status.equals("0")) {
                        showToast(EditBankDetail.this, data.message);
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

    private void setBankDetails()
    {
        binding.etBankName.setText(userDetail.getBankName());
        binding.etAccountHoldeName.setText(userDetail.getAccHolderName());
        binding.etAccountNumber.setText(userDetail.getAccountNumber());
        binding.etFyiCode.setText(userDetail.getIfscCode());
    }

    private boolean isValid() {
        if (bankName.equalsIgnoreCase("")) {
            binding.etBankName.setError(getString(R.string.enter_bank_detail));
            return false;
        } else  if (holderName.equalsIgnoreCase("")) {
            binding.etAccountHoldeName.setError(getString(R.string.enter_holder));
            return false;
        } else  if (accountNumber.equalsIgnoreCase("")) {
            binding.etAccountNumber.setError(getString(R.string.enter_holder));
            return false;
        } else  if (ifscCode.equalsIgnoreCase("")) {
            binding.etFyiCode.setError(getString(R.string.enter_holder));
            return false;
        }
        return true;
    }

    private void editBankDetail() {
        String id = SharedPreferenceUtility.getInstance(EditBankDetail.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(EditBankDetail.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("acc_holder_name", holderName);
        map.put("account_number", accountNumber);
        map.put("ifsc_code", ifscCode);
        map.put("bank_name",bankName);
        map.put("user_id",id);
        Call<SuccessResLogin> call = apiInterface.editBank(map);
        call.enqueue(new Callback<SuccessResLogin>() {
            @Override
            public void onResponse(Call<SuccessResLogin> call, Response<SuccessResLogin> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResLogin data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);

                        getWindow().getDecorView().clearFocus();

                    } else if (data.status.equals("0")) {
                        showToast(EditBankDetail.this, data.message);
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
}