package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityAddCardDetailBinding;
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

public class AddCardDetailAct extends AppCompatActivity {

    ActivityAddCardDetailBinding binding;

    String bankName = "",holderName="",accountNumber = "",ifscCode="";

    private ShoppingInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_card_detail);

        binding.header.tvHeader.setText(R.string.add_abank);

        binding.header.imgHeader.setOnClickListener(v -> finish());

        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);

        binding.btnAdd.setOnClickListener(v ->
                {

                    bankName = binding.etBankName.getText().toString();
                    holderName = binding.etAccountHoldeName.getText().toString();
                    accountNumber = binding.etNumber.getText().toString();
                    ifscCode = binding.etFyiCode.getText().toString();

                    if (isValid()) {
                        if (NetworkAvailablity.checkNetworkStatus(this)) {
                            addBankDetail();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void addBankDetail() {

        String id = SharedPreferenceUtility.getInstance(AddCardDetailAct.this).getString(USER_ID);

        DataManager.getInstance().showProgressMessage(AddCardDetailAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("acc_holder_name", holderName);
        map.put("account_number", accountNumber);
        map.put("ifsc_code", ifscCode);
        map.put("bank_name",bankName);
        map.put("user_id",id);

        Call<SuccessResLogin> call = apiInterface.addBank(map);

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

                        startActivity(new Intent(AddCardDetailAct.this, LoginAct.class));
                        finish();
                    } else if (data.status.equals("0")) {
                        showToast(AddCardDetailAct.this, data.message);
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
        if (bankName.equalsIgnoreCase("")) {
            binding.etBankName.setError(getString(R.string.enter_bank_detail));
            return false;
        } else  if (holderName.equalsIgnoreCase("")) {
            binding.etAccountHoldeName.setError(getString(R.string.enter_holder));
            return false;
        } else  if (accountNumber.equalsIgnoreCase("")) {
            binding.etNumber.setError(getString(R.string.enter_holder));
            return false;
        } else  if (ifscCode.equalsIgnoreCase("")) {
            binding.etFyiCode.setError(getString(R.string.enter_holder));
            return false;
        }
        return true;
    }



}