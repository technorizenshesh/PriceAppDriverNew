package com.android.priceappdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.android.priceappdriver.adapters.AdapterType;
import com.android.priceappdriver.databinding.ActivityEditVehicleBinding;
import com.android.priceappdriver.model.SuccessResAddVehicle;
import com.android.priceappdriver.model.SuccessResGetVehicle;
import com.android.priceappdriver.model.SuccessResVehicleType;
import com.android.priceappdriver.retrofit.ApiClient;
import com.android.priceappdriver.retrofit.NetworkAvailablity;
import com.android.priceappdriver.retrofit.ShoppingInterface;
import com.android.priceappdriver.utility.DataManager;
import com.android.priceappdriver.utility.RealPathUtil;
import com.android.priceappdriver.utility.SharedPreferenceUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.android.priceappdriver.retrofit.Constant.USER_ID;
import static com.android.priceappdriver.retrofit.Constant.showToast;

public class EditVehicleAct extends AppCompatActivity {

    ActivityEditVehicleBinding binding;
    private SuccessResGetVehicle.Result vehicleDetail;
    private ShoppingInterface apiInterface;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private static final int REQUEST_CAMERA = 1;
    private String strVehicleType="",strVehicleBrand="",strMenufectureYear="",strVehicleModel="",strVehicleNumber="",strVehicleColor="",str_image_path="",strVehicleID="";
    private ArrayList<SuccessResVehicleType.Result> vehicleTypeList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_vehicle);
        apiInterface = ApiClient.getClient().create(ShoppingInterface.class);
        getVehicleTypeList();
        getCarDetail();
        binding.header.tvHeader.setText(R.string.edit_vechie);

        binding.header.imgHeader.setOnClickListener(v ->
                {
                    finish();
                }
        );

        binding.ivCam.setOnClickListener(v -> 
                {
                    if(checkPermisssionForReadStorage())
                        showImageSelection();
                }
                );
        
        binding.btnVehicle.setOnClickListener(
                v ->
                {
                    strVehicleBrand = binding.etVehicleBrand.getText().toString();
                    strVehicleModel = binding.etVehicleModel.getText().toString();
                    strVehicleNumber = binding.etVehicleNumber.getText().toString();
                    strVehicleColor = binding.etVehicleColor.getText().toString();
                    strMenufectureYear = binding.etVehicleManufecture.getText().toString();
                    if (isValid()) {
                        if (NetworkAvailablity.checkNetworkStatus(this)) {
                            editVehicle();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void getCarDetail()
    {
        String userId = SharedPreferenceUtility.getInstance(EditVehicleAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        Call<SuccessResGetVehicle> call = apiInterface.getVehicle(map);
        call.enqueue(new Callback<SuccessResGetVehicle>() {
            @Override
            public void onResponse(Call<SuccessResGetVehicle> call, Response<SuccessResGetVehicle> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetVehicle data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        vehicleDetail = data.getResult().get(0);
                        setVehicleDetails();
                    } else if (data.status.equals("0")) {
                        showToast(EditVehicleAct.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResGetVehicle> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setVehicleDetails()
    {
        strVehicleID = vehicleDetail.getId();
        Glide
                .with(this)
                .load(vehicleDetail.getCarDocument())
                .centerCrop()
                .into(binding.ivUser);
        binding.etVehicleBrand.setText(vehicleDetail.getBrand());
        binding.etVehicleModel.setText(vehicleDetail.getCarModel());
        binding.etVehicleManufecture.setText(vehicleDetail.getYearOfManufacture());
        binding.etVehicleNumber.setText(vehicleDetail.getCarNumber());
        binding.etVehicleColor.setText(vehicleDetail.getCarColor());
        strVehicleType = vehicleDetail.getCarTypeId();
        int position = Integer.parseInt(strVehicleType);
        binding.spinnerServiceType.setSelection(--position);
    }

    private void getVehicleTypeList() {
        Call<SuccessResVehicleType> signupCall = apiInterface.getCarList();
        signupCall.enqueue(new Callback<SuccessResVehicleType>() {
            @Override
            public void onResponse(Call<SuccessResVehicleType> call, Response<SuccessResVehicleType> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResVehicleType data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Car Type Response :" + responseString);
                    if (data.status.equals("1")) {
                        vehicleTypeList.clear();
                        vehicleTypeList.addAll(data.result);
                        strVehicleType = vehicleTypeList.get(0).getId();
                        binding.spinnerServiceType.setAdapter(new AdapterType(EditVehicleAct.this, vehicleTypeList));
                    } else if (data.status.equals("0")) {
                        showToast(EditVehicleAct.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<SuccessResVehicleType> call, Throwable t) {
                DataManager.getInstance().hideProgressMessage();
                call.cancel();
            }
        });
    }

    private void editVehicle() {
        String id = SharedPreferenceUtility.getInstance(EditVehicleAct.this).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(EditVehicleAct.this, getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            if(file!=null)
            {
                filePart = MultipartBody.Part.createFormData("car_document", file.getName(), RequestBody.create(MediaType.parse("car_document/*"), file));
            }
            else
            {
                filePart = null;
            }
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody carType = RequestBody.create(MediaType.parse("text/plain"), strVehicleType);
        RequestBody brand = RequestBody.create(MediaType.parse("text/plain"), strVehicleBrand);
        RequestBody carModel = RequestBody.create(MediaType.parse("text/plain"), strVehicleModel);
        RequestBody year = RequestBody.create(MediaType.parse("text/plain"), strMenufectureYear);
        RequestBody carColor = RequestBody.create(MediaType.parse("text/plain"), strVehicleColor);
        RequestBody carNumber = RequestBody.create(MediaType.parse("text/plain"), strVehicleNumber);
        RequestBody vehicleId = RequestBody.create(MediaType.parse("text/plain"), strVehicleID);
        Call<SuccessResAddVehicle> call = apiInterface.updateVehicle(userId,carType,brand,carModel,year,carColor,carNumber,vehicleId,filePart);
        call.enqueue(new Callback<SuccessResAddVehicle>() {
            @Override
            public void onResponse(Call<SuccessResAddVehicle> call, Response<SuccessResAddVehicle> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddVehicle data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                    } else if (data.status.equals("0")) {
                        showToast(EditVehicleAct.this, data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            @Override
            public void onFailure(Call<SuccessResAddVehicle> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private boolean isValid() {
       if (strVehicleBrand.equalsIgnoreCase("")) {
            binding.etVehicleBrand.setError(getString(R.string.enter_vehicle_brand));
            return false;
        } else  if (strVehicleModel.equalsIgnoreCase("")) {
            binding.etVehicleModel.setError(getString(R.string.enter_vehicle_model));
            return false;
        } else  if (strMenufectureYear.equalsIgnoreCase("")) {
            binding.etVehicleManufecture.setError(getString(R.string.enter_menufecture_year));
            return false;
        } else  if (strVehicleNumber.equalsIgnoreCase("")) {
            binding.etVehicleNumber.setError(getString(R.string.enter_vehicle_number));
            return false;
        } else  if (strVehicleColor.equalsIgnoreCase("")) {
            binding.etVehicleColor.setError(getString(R.string.enter_vechile_color));
            return false;
        }
        return true;
    }

    public void showImageSelection() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getPhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(EditVehicleAct.this.getPackageManager()) != null)
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    public Bitmap BITMAP_RE_SIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {

                try {
                    Uri selectedImage = data.getData();
                    Bitmap bitmapNew = MediaStore.Images.Media.getBitmap(EditVehicleAct.this.getContentResolver(), selectedImage);
                    Bitmap bitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                    Glide.with(EditVehicleAct.this)
                            .load(selectedImage)
                            .centerCrop()
                            .into(binding.ivUser);
                    Uri tempUri = getImageUri(EditVehicleAct.this, bitmap);
                    String image = RealPathUtil.getRealPath(EditVehicleAct.this, tempUri);
                    str_image_path = image;

                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }

            } else if (requestCode == REQUEST_CAMERA) {

                try {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                        Uri tempUri = getImageUri(EditVehicleAct.this, imageBitmap);
                        String image = RealPathUtil.getRealPath(EditVehicleAct.this, tempUri);
                        str_image_path = image;
                        Glide.with(EditVehicleAct.this)
                                .load(imageBitmap)
                                .centerCrop()
                                .into(binding.ivUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
/*

                    Log.e("Latittude====", gpsTracker.getLatitude() + "");

                    strLat = Double.toString(gpsTracker.getLatitude()) ;
                    strLng = Double.toString(gpsTracker.getLongitude()) ;
*/

//
//                    if (isContinue) {
//                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditVehicleAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//                    } else {
//                        Log.e("Latittude====", gpsTracker.getLatitude() + "");
//
//                        strLat = Double.toString(gpsTracker.getLatitude()) ;
//                        strLng = Double.toString(gpsTracker.getLongitude()) ;
//                    }
                } else {
                    Toast.makeText(EditVehicleAct.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(EditVehicleAct.this, getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditVehicleAct.this, getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }


    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(EditVehicleAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(EditVehicleAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(EditVehicleAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditVehicleAct.this,
                    Manifest.permission.CAMERA)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(EditVehicleAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(EditVehicleAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {
            //  explain("Please Allow Location Permission");
            return true;
        }
    }


}