package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.android.priceappdriver.adapters.HomeAdapter;
import com.android.priceappdriver.databinding.ActivityHomeBinding;
import com.android.priceappdriver.model.SuccessResGetRequests;
import com.android.priceappdriver.model.SuccessResLogin;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.Constant;
import com.android.priceappdriver.retrofit.NetworkAvailablity;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.priceappdriver.retrofit.Constant.USER_ID;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class HomeAct extends AppCompatActivity {

    ActivityHomeBinding binding;
    ImageView ivMenu;
    private String status = "all";
    private ShoppingInterface apiInterface;
    private ArrayList<SuccessResGetRequests.Result> requestList = new ArrayList<>();
    private HomeAdapter homeAdapter ;
    private SuccessResLogin.Result userDetail;
    CircleImageView ivProfile;
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        ivProfile = findViewById(R.id.ivProfilePic);
        tvUserName = findViewById(R.id.tvUserName);
        ivMenu = findViewById(R.id.menu);
        binding.tabLay.addTab(binding.tabLay.newTab().setText(R.string.all));
        binding.tabLay.addTab(binding.tabLay.newTab().setText(R.string.new1));
        binding.tabLay.addTab(binding.tabLay.newTab().setText(R.string.in_progress));
        binding.tabLay.addTab(binding.tabLay.newTab().setText(R.string.completed));
        binding.tabLay.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLay.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentTabSelected= tab.getPosition();
                if(currentTabSelected==0)
                {
                    status="all";
                    getRequests();
                }else if(currentTabSelected==1)
                {
                    status = "Pending";
                    getRequests();
                }else if(currentTabSelected==2)
                {
                    status = "Accepted";
                    getRequests();
                }else if(currentTabSelected==3)
                {
                    status = "Completed";
                    getRequests();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        homeAdapter = new HomeAdapter(HomeAct.this,requestList);
        binding.rvHome.setHasFixedSize(true);
        binding.rvHome.setLayoutManager(new LinearLayoutManager(HomeAct.this));
        binding.rvHome.setAdapter(homeAdapter);
        ivMenu.setOnClickListener(v ->
                {
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    else binding.drawerLayout.openDrawer(GravityCompat.START);
                }
        );
        binding.childNavDrawer.tvHome.setOnClickListener(v ->
                {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                );
        binding.childNavDrawer.tvSetting.setOnClickListener(v ->
                {
                    startActivity(new Intent(HomeAct.this,SettingAct.class));
                }
        );

        binding.childNavDrawer.tvLogout.setOnClickListener(v ->
                {

                    SharedPreferenceUtility.getInstance(HomeAct.this).putBoolean(Constant.IS_USER_LOGGED_IN, false);
                    Intent i = new Intent(HomeAct.this, LoginAct.class);
// set the new task and clear flags
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
                );

        binding.rvHome.setOnClickListener(v ->
                {
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    else binding.drawerLayout.openDrawer(GravityCompat.START);
                }
                );
        if (NetworkAvailablity.checkNetworkStatus(HomeAct.this)) {
            getRequests();
            getProfile();
        } else {
            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
        }
    }
    public void getRequests()
    {
        String userId = SharedPreferenceUtility.getInstance(HomeAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("status",status);
        Call<SuccessResGetRequests> call = apiInterface.getRequest(map);
        call.enqueue(new Callback<SuccessResGetRequests>() {
            @Override
            public void onResponse(Call<SuccessResGetRequests> call, Response<SuccessResGetRequests> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetRequests data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        requestList.clear();
                        requestList.addAll(data.getResult());
                        homeAdapter.notifyDataSetChanged();
                    } else if (data.status.equals("0")) {
                        showToast(HomeAct.this, data.message);
                        requestList.clear();
                        homeAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResGetRequests> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequests();
    }

    private void getProfile() {
        String userId = SharedPreferenceUtility.getInstance(HomeAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
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
                        setProfileDetails();
                    } else if (data.status.equals("0")) {
                        showToast(HomeAct.this, data.message);
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

    private void setProfileDetails()
    {
        Glide
                .with(this)
                .load(userDetail.getImage())
                .centerCrop()
                .into(ivProfile);
        tvUserName.setText(userDetail.getFirstName());
       }


}