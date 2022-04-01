package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.android.priceappdriver.model.SuccessResGetRequests;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class OrderCompletedAct extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String pickUpLat = "",pickUpLong = "",dropOffLat = "",dropOffLong = "";
    private SuccessResGetRequests.Result requestModel;
    ImageView ivBack,userImage;
    TextView tvPick,tvDrop,tvHeader,tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed);
        init();
        allClick();
        Intent in = getIntent();

        if (in!=null)
        {
            String result = in.getStringExtra("data");
            requestModel = new Gson().fromJson(result,SuccessResGetRequests.Result.class);
        }

        tvPick.setText(requestModel.getCompanyLocation());
        tvDrop.setText(requestModel.getAddress());
        Glide
                .with(OrderCompletedAct.this)
                .load(requestModel.getUserDetails().getImage())
                .placeholder(R.drawable.male_ic)
                .into(userImage);
        tvUserName.setText(requestModel.getUserDetails().getFirstName());
        pickUpLat = requestModel.getCompanyLatitude();
        pickUpLong = requestModel.getCompanyLongnitude();
        dropOffLat = requestModel.getLat();
        dropOffLong = requestModel.getLon();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init()
    {
        ivBack = findViewById(R.id.img_header);
        tvHeader = findViewById(R.id.tv_header);
        tvHeader.setText(R.string.order_details);
        tvPick = findViewById(R.id.tvFromLocation);
        tvDrop = findViewById(R.id.tvtoLocation);
        tvUserName = findViewById(R.id.tvUserName);
        userImage = findViewById(R.id.userImage);
    }

    private void allClick()
    {
        ivBack.setOnClickListener(v -> finish());
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
            int padding = 150;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}