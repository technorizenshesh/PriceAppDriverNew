package com.android.priceappdriver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.android.priceappdriver.adapters.CancelAdapter;
import com.android.priceappdriver.model.SuccessResGetOrderDetail;
import com.android.priceappdriver.model.SuccessResGetRequests;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.CancelClick;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.android.priceappdriver.retrofit.Constant.USER_ID;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class TripDetail extends AppCompatActivity implements OnMapReadyCallback {

    LinearLayout llBottomSheet;
    private Dialog dialog;
    ImageView ivCancel,ivPhone;
    private GoogleMap mMap;
    ArrayList<LatLng> mMarkerPoints;
    private String pickUpLat = "",pickUpLong = "",dropOffLat = "",dropOffLong = "";
    private SuccessResGetRequests.Result requestModel;
    TextView tvPick,tvDrop;
    private ShoppingInterface apiInterface;
    private int selectedPosition = -1;
    AppCompatButton btnPicked,btnDelivered;
    CircleImageView userImage;
    TextView tvUserName;
    CardView cvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        init();
        allClick();
        Intent in = getIntent();
        if (in!=null)
        {
            String result = in.getStringExtra("data");
            requestModel = new Gson().fromJson(result,SuccessResGetRequests.Result.class);
            getProductById(requestModel.getId());
        }
        cvBack.setOnClickListener(v ->
                {
                    finish();
                }
                );
        pickUpLat = requestModel.getCompanyLatitude();
        pickUpLong = requestModel.getCompanyLongnitude();
        dropOffLat = requestModel.getLat();
        dropOffLong = requestModel.getLon();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();
        tvPick.setText(requestModel.getCompanyLocation());
        tvDrop.setText(requestModel.getAddress());
        Glide
                .with(TripDetail.this)
                .load(requestModel.getUserDetails().getImage())
                .placeholder(R.drawable.male_ic)
                .into(userImage);
        tvUserName.setText(requestModel.getUserDetails().getFirstName());
    }
    private void init()
    {
        userImage = findViewById(R.id.ivDriverPropic);
        ivPhone = findViewById(R.id.ivCall);
        cvBack= findViewById(R.id.cvBack);
        tvUserName = findViewById(R.id.tvUserName);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        tvPick = findViewById(R.id.tvPickLoc);
        tvDrop = findViewById(R.id.tvDropLoc);
        btnDelivered = findViewById(R.id.btnDelivered);
        btnPicked = findViewById(R.id.btnPicked);
        ivCancel = findViewById(R.id.ivCancel);
        llBottomSheet = findViewById(R.id.botttomSheet);
    }
    public void allClick()
    {
        ivCancel.setOnClickListener(v ->
                {
                    fullScreenDialog();
                }
        );
        btnPicked.setOnClickListener(v ->
                {
                    new AlertDialog.Builder(TripDetail.this)
                            .setTitle(R.string.order_picked)
                            .setMessage(R.string.are_you_sure_picked_order)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    shiftInProgress();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
        );
        btnDelivered.setOnClickListener(v ->
                {
                    new AlertDialog.Builder(TripDetail.this)
                            .setTitle(R.string.order_delivered)
                            .setMessage(R.string.are_you_sure_delivered_order)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    shiftCompleted();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
        );
        ivPhone.setOnClickListener(v ->
                {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+requestModel.getUserDetails().getMobile()));//change the number
                    startActivity(callIntent);
                }
        );
    }

    private void shiftInProgress()
    {
//        'Pending','Accepted','Progress','Completed','Cancel'
        String userId = SharedPreferenceUtility.getInstance(TripDetail.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("driver_id",userId);
        map.put("order_id",requestModel.getId());
        map.put("status","Progress");
        map.put("cancel_reason","");
        Call<ResponseBody> call = apiInterface.accpetRejectSHift(map);
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
                        getProductById(requestModel.getId());
                    } else if (data.equals("0")) {
                        showToast(TripDetail.this, message);
                    }
                } catch (Exception e) {
                    getProductById(requestModel.getId());
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

    private void shiftCompleted()
    {
        String userId = SharedPreferenceUtility.getInstance(TripDetail.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("driver_id",userId);
        map.put("order_id",requestModel.getId());
        map.put("status","Completed");
        map.put("cancel_reason","");
        Call<ResponseBody> call = apiInterface.accpetRejectSHift(map);
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
                        finish();
                    } else if (data.equals("0")) {
                        showToast(TripDetail.this, message);
                    }
                } catch (Exception e) {
                    finish();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                finish();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        int height = 65;
        int width = 65;
        mMap = googleMap;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dropup);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        LatLng barcelona = new LatLng(Double.valueOf(pickUpLat),Double.valueOf(pickUpLong));
        mMap.addMarker(new MarkerOptions().position(barcelona).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pick_location)));
        LatLng madrid = new LatLng(Double.valueOf(dropOffLat),Double.valueOf(dropOffLong));
        mMap.addMarker(new MarkerOptions().position(madrid).icon(smallMarkerIcon));
        List<LatLng> path = new ArrayList();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.api_key))
                .build();
        String pick = pickUpLat+","+pickUpLong;
        String drop = dropOffLat+","+dropOffLong;
        DirectionsApiRequest req = DirectionsApi.getDirections(context, pick, drop);
        try {
            DirectionsResult res = req.await();
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }
        zoomMapInitial(new LatLng(Double.parseDouble(pickUpLat),Double.parseDouble(pickUpLong)),new LatLng(Double.parseDouble(dropOffLat),Double.parseDouble(dropOffLong)));
    }

    private void cancelShift(String cancelReason)
    {
        String userId = SharedPreferenceUtility.getInstance(TripDetail.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("driver_id",userId);
        map.put("order_id",requestModel.getId());
        map.put("status","Cancel");
        map.put("cancel_reason",cancelReason);
        Call<ResponseBody> call = apiInterface.accpetRejectSHift(map);
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
                        finish();
                    } else if (data.equals("0")) {
                        showToast(TripDetail.this, message);
                    }
                } catch (Exception e) {
                    finish();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                finish();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
    private void fullScreenDialog() {
        dialog = new Dialog(TripDetail.this, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.activity_ride_cancelation);
        AppCompatButton btnSubmit = dialog.findViewById(R.id.btnSubmit);
        RecyclerView rvCancelReason = dialog.findViewById(R.id.rvCancelReason);
        ImageView ivBack;
        ivBack = dialog.findViewById(R.id.img_back);
        ArrayList<String> lines = new ArrayList<>();
        lines.add(getString(R.string.i_dont_want_to_share));
        lines.add("Can't contact the User");
        lines.add(getString(R.string.the_price_is_not_reasonable));
        lines.add("Pickup address is incorrect");
        lines.add("Dropup address is incorrect");
        rvCancelReason.setHasFixedSize(true);
        rvCancelReason.setLayoutManager(new LinearLayoutManager(TripDetail.this));
        rvCancelReason.setAdapter(new CancelAdapter(TripDetail.this, lines, new CancelClick() {
            @Override
            public void cancelReason(int position) {
                selectedPosition = position;
            }
        }));
        btnSubmit.setOnClickListener(v ->
                {
                    if(selectedPosition!=-1)
                    {
                        cancelShift(lines.get(selectedPosition));
                    }
                    else
                    {
                        Toast.makeText(TripDetail.this,"Please select a reason.",Toast.LENGTH_SHORT).show();
                    }
                }
                );
        ivBack.setOnClickListener(v ->
                {
                    dialog.dismiss();
                }
                );
        dialog.show();
    }

    private void getProductById(String productId)
    {
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("order_id",productId);
        Call<SuccessResGetOrderDetail> call = apiInterface.getOrderByOrderId(map);
        call.enqueue(new Callback<SuccessResGetOrderDetail>() {
            @Override
            public void onResponse(Call<SuccessResGetOrderDetail> call, Response<SuccessResGetOrderDetail> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetOrderDetail data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        if(data.getResult().getStatus().equalsIgnoreCase("Accepted"))
                        {

                            btnDelivered.setVisibility(View.GONE);
                            btnPicked.setVisibility(View.VISIBLE);

                        }else if(data.getResult().getStatus().equalsIgnoreCase("Progress"))
                        {
                            btnPicked.setVisibility(View.GONE);
                            btnDelivered.setVisibility(View.VISIBLE);
                        }
                    } else if (data.status.equals("0")) {
                        showToast(TripDetail.this, data.message);
                    }
                } catch (Exception e) {
                    finish();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResGetOrderDetail> call, Throwable t) {
                call.cancel();
                finish();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}