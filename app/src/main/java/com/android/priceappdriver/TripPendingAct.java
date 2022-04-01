package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import com.android.priceappdriver.model.SuccessResGetRequests;

import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.SharedPreferenceUtility;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.content.ContentValues.TAG;
import static com.android.priceappdriver.retrofit.Constant.USER_ID;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class TripPendingAct extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LatLng> mMarkerPoints;
    private String pickUpLat = "",pickUpLong = "",dropOffLat = "",dropOffLong = "";
    private SuccessResGetRequests.Result requestModel;
    private AppCompatButton btnAccept;
    private ShoppingInterface apiInterface;
    CardView cvBack;
    TextView tvPick,tvDrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_pending);
        init();
        Intent in = getIntent();
        if (in!=null)
        {
            String result = in.getStringExtra("data");
            requestModel = new Gson().fromJson(result,SuccessResGetRequests.Result.class);
        }
        pickUpLat = requestModel.getCompanyLatitude();
        pickUpLong = requestModel.getCompanyLongnitude();
        dropOffLat = requestModel.getLat();
        dropOffLong = requestModel.getLon();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();
        btnAccept.setOnClickListener(v ->
                {
                    acceptShift();
                }
                );
        tvPick.setText(requestModel.getCompanyLocation());
        tvDrop.setText(requestModel.getAddress());
    }

    private void init()
    {
        tvPick = findViewById(R.id.tvPickLoc);
        tvDrop = findViewById(R.id.tvDropLoc);
        btnAccept = findViewById(R.id.btnAccept);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        cvBack = findViewById(R.id.cvBack);
        cvBack.setOnClickListener(v -> finish());
    }

    private void acceptShift()
    {
        String userId = SharedPreferenceUtility.getInstance(TripPendingAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("driver_id",userId);
        map.put("order_id",requestModel.getId());
        map.put("status","Accepted");
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
                        showToast(TripPendingAct.this, message);
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