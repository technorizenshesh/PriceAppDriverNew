package com.android.priceappdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.android.priceappdriver.databinding.ActivityOtpBinding;
import com.android.priceappdriver.model.SuccessResSignUp;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.Constant;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class OtpAct extends AppCompatActivity {

    ActivityOtpBinding binding;
    private Context mContext;
    private String finalOtp = "";
    ShoppingInterface apiInterface;
    HashMap<String, String> map;
    private FirebaseAuth mAuth;
    private String mVerificationId,mobile;
    String strName="",strEmail="",strLat="",strLong="", strcc="",strAddress="",strphone="",strPass="",str_image_path="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_otp);
        mContext = OtpAct.this;
        binding.ivBack.setOnClickListener(v ->
                {
                    finish();
                }
                );
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        FirebaseApp.initializeApp(OtpAct.this);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        strName =  intent.getStringExtra("fullname");
        strEmail =  intent.getStringExtra("email");
        strphone = intent.getStringExtra("mobile");
        strcc = intent.getStringExtra("cc");
        strPass =  intent.getStringExtra("password");
        strAddress =  intent.getStringExtra("address");
        strLat =  intent.getStringExtra("lat");
        strLong =  intent.getStringExtra("lon");
        str_image_path = intent.getStringExtra("image");
        mobile = "+"+strcc+strphone;
        VerifyPhoneNumber();
        init();
    }

    private void VerifyPhoneNumber(){
        binding.tvResentToMessage.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        //  mobile = "+"+session.getStringValue(SessionKey.mobile);
        // mobile = "+2348036624845";
        Log.e("mobilenumber====",mobile);
      /*  PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+"+session.getStringValue(SessionKey.mobile))       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);*/

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile.replace(" ", ""), 60,  TimeUnit.SECONDS,  OtpAct.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    // Phone number to verify
                    // Timeout duration
                    // Unit of timeout
                    // Activity (for callback binding)

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Toast.makeText(OtpAct.this, "Otp send successfully.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onCodeSent:" + verificationId);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText("Code Sent Successfully");
                        binding.tvResentToMessage.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        //   ProjectUtil.pauseProgressDialog()
                        //      DataManager.getInstance().hideProgressMessage();
                        //     Toast.makeText(OtpAct.this, ""+phoneAuthCredential.getSmsCode(), Toast.LENGTH_SHORT).show();
                        //        signInWithPhoneAuthCredential(phoneAuthCredential);
                        Log.d(TAG, "onVerificationCompleted:" + credential);
                        binding.tvResentToMessage.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ProjectUtil.pauseProgressDialog();
                        //   DataManager.getInstance().hideProgressMessage();
                        //   Toast.makeText(OtpAct.this, "Failed"+e, Toast.LENGTH_SHORT).show();

                        binding.tvResentToMessage.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e);
                        showToast(OtpAct.this,"Failed : "+e.getMessage());
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            binding.tvError.setVisibility(View.VISIBLE);
                            binding.tvError.setText(e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            binding.tvError.setVisibility(View.VISIBLE);
                            binding.tvError.setText(e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpAct.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.tvError.setVisibility(View.VISIBLE);
                            binding.tvError.setText("Success");
                            Log.d(TAG, "signInWithCredential:success");
                            signup();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                binding.tvError.setVisibility(View.VISIBLE);
                                binding.tvError.setText(task.getException().getLocalizedMessage());
                            }
                        }
                    }
                });
    }

    private void signup()
    {

         DataManager.getInstance().showProgressMessage(OtpAct.this, getString(R.string.please_wait));

        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            if(file!=null)
            {
                filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            }
            else
            {
                filePart = null;
            }
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), strName);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), strEmail);
        RequestBody cc = RequestBody.create(MediaType.parse("text/plain"), strcc);
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"),strphone);
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), strLat);
        RequestBody lon = RequestBody.create(MediaType.parse("text/plain"), strLong);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), strAddress);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), strPass);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "Driver");
        RequestBody registerId = RequestBody.create(MediaType.parse("text/plain"), "");

        Call<SuccessResSignUp> signupCall = apiInterface.signup(name,email,cc,mobile,lat,lon,password,type,registerId,address,filePart);
        signupCall.enqueue(new Callback<SuccessResSignUp>() {
            @Override
            public void onResponse(Call<SuccessResSignUp> call, Response<SuccessResSignUp> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResSignUp data = response.body();
                    if (data.status.equals("1")) {
                        showToast(OtpAct.this, data.message);
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        SharedPreferenceUtility.getInstance(OtpAct.this).putString(Constant.USER_ID,data.getResult().getId());
                        startActivity(new Intent(OtpAct.this, AddVehicleAct.class).putExtra("from","signup"));
                    } else if (data.status.equals("0")) {
                        showToast(OtpAct.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResSignUp> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void init() {

        binding.tvResentToMessage.setOnClickListener(v ->
                {
                    VerifyPhoneNumber();
                }
        );

        binding.btnVerify.setOnClickListener(v ->
                {

                    if (TextUtils.isEmpty(binding.et1.getText().toString().trim())) {
                        Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.et2.getText().toString().trim())) {
                        Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.et3.getText().toString().trim())) {
                        Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.et4.getText().toString().trim())) {
                        Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.et5.getText().toString().trim())) {
                        Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.et6.getText().toString().trim())) {
                        Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
                    } else {
                        finalOtp =
                                binding.et1.getText().toString().trim() +
                                        binding.et2.getText().toString().trim() +
                                        binding.et3.getText().toString().trim() +
                                        binding.et4.getText().toString().trim()+
                                        binding.et5.getText().toString().trim()+
                                        binding.et6.getText().toString().trim()
                        ;
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, finalOtp);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
        );

        binding.et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    binding.et2.setText("");
                    binding.et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        binding.et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    binding.et3.setText("");
                    binding.et3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });

        binding.et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    binding.et4.setText("");
                    binding.et4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        binding.et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    binding.et5.setText("");
                    binding.et5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }




        });

        binding.et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    binding.et6.setText("");
                    binding.et6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });


        binding.et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });

    }

}