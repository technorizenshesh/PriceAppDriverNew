package com.android.priceappdriver.retrofit;


import com.android.priceappdriver.model.SuccessResAddVehicle;
import com.android.priceappdriver.model.SuccessResChangePass;
import com.android.priceappdriver.model.SuccessResForgotPassword;
import com.android.priceappdriver.model.SuccessResGetOrderDetail;
import com.android.priceappdriver.model.SuccessResGetRequests;
import com.android.priceappdriver.model.SuccessResGetVehicle;
import com.android.priceappdriver.model.SuccessResLogin;
import com.android.priceappdriver.model.SuccessResSignUp;
import com.android.priceappdriver.model.SuccessResVehicleType;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ShoppingInterface {

    @Multipart
    @POST("signup")
    Call<SuccessResSignUp> signup (@Part("first_name") RequestBody fName,
                                                 @Part("email") RequestBody emal,
                                                 @Part("country_code") RequestBody fullname,
                                                 @Part("mobile") RequestBody mobile,
                                                 @Part("lat") RequestBody lat,
                                                 @Part("lon") RequestBody lng,
                                                 @Part("password") RequestBody gender,
                                                 @Part("type") RequestBody tpe,
                                                 @Part("register_id") RequestBody registerId,
                                                 @Part("address") RequestBody address,
                                                @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("add_account_details")
    Call<SuccessResLogin> addBank(@FieldMap Map<String,String> params);

    @GET("get_car_list")
    Call<SuccessResVehicleType> getCarList();

    @Multipart
    @POST("add_vehicle")
    Call<SuccessResAddVehicle> addVehicle (@Part("user_id") RequestBody fName,
                                           @Part("car_type_id") RequestBody emal,
                                           @Part("brand") RequestBody fullname,
                                           @Part("car_model") RequestBody mobile,
                                           @Part("year_of_manufacture") RequestBody lat,
                                           @Part("car_color") RequestBody lng,
                                           @Part("car_number") RequestBody gender,
                                           @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("driver_login")
    Call<SuccessResLogin> login(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<SuccessResForgotPassword> forgotPassword(@FieldMap Map<String, String> paramHashMap);

    @FormUrlEncoded
    @POST("get_profile")
    Call<SuccessResLogin> getProfile(@FieldMap Map<String,String> params);

    @Multipart
    @POST("update_profile")
    Call<SuccessResLogin> updateProfile (@Part("user_id") RequestBody userId,
                                   @Part("first_name") RequestBody fName,
                                   @Part("email") RequestBody emal,
                                   @Part("country_code") RequestBody fullname,
                                   @Part("mobile") RequestBody mobile,
                                   @Part("lat") RequestBody lat,
                                   @Part("lon") RequestBody lng,
                                   @Part("address") RequestBody address,
                                   @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("get_car_details")
    Call<SuccessResGetVehicle> getVehicle(@FieldMap Map<String,String> params);

    @Multipart
    @POST("update_vehicle")
    Call<SuccessResAddVehicle> updateVehicle (@Part("user_id") RequestBody fName,
                                           @Part("car_type_id") RequestBody emal,
                                           @Part("brand") RequestBody fullname,
                                           @Part("car_model") RequestBody mobile,
                                           @Part("year_of_manufacture") RequestBody lat,
                                           @Part("car_color") RequestBody lng,
                                           @Part("car_number") RequestBody gender,
                                           @Part("vehicle_id") RequestBody vehicelId,
                                              @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("update_account_details")
    Call<SuccessResLogin> editBank(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("update_account_details")
    Call<SuccessResChangePass> changePass(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("get_order_driver")
    Call<SuccessResGetRequests> getRequest(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_cancel_order")
    Call<ResponseBody> accpetRejectSHift(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("get_order_by_id")
    Call<SuccessResGetOrderDetail> getOrderByOrderId(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("add_contact_us")
    Call<ResponseBody> contactUs(@FieldMap Map<String,String> params);


}
