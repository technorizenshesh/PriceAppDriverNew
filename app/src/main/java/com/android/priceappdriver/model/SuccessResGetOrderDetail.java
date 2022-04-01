package com.android.priceappdriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SuccessResGetOrderDetail
{

    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class ItemDatum {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("product_id")
        @Expose
        public String productId;
        @SerializedName("product_name")
        @Expose
        public String productName;
        @SerializedName("quntity")
        @Expose
        public String quntity;
        @SerializedName("price")
        @Expose
        public String price;
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("item_image")
        @Expose
        public String itemImage;
        @SerializedName("discount")
        @Expose
        public String discount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getQuntity() {
            return quntity;
        }

        public void setQuntity(String quntity) {
            this.quntity = quntity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getItemImage() {
            return itemImage;
        }

        public void setItemImage(String itemImage) {
            this.itemImage = itemImage;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

    }
    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("cart_id")
        @Expose
        public String cartId;
        @SerializedName("card_id")
        @Expose
        public String cardId;
        @SerializedName("address_id")
        @Expose
        public String addressId;
        @SerializedName("deliverytype")
        @Expose
        public String deliverytype;
        @SerializedName("company_id")
        @Expose
        public String companyId;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("product_id")
        @Expose
        public String productId;
        @SerializedName("total_amount")
        @Expose
        public String totalAmount;
        @SerializedName("payment_type")
        @Expose
        public String paymentType;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("driver_id")
        @Expose
        public String driverId;
        @SerializedName("accept_time")
        @Expose
        public String acceptTime;
        @SerializedName("arrived_time")
        @Expose
        public String arrivedTime;
        @SerializedName("confirm_time")
        @Expose
        public String confirmTime;
        @SerializedName("lat")
        @Expose
        public String lat;
        @SerializedName("lon")
        @Expose
        public String lon;
        @SerializedName("cancel_reason")
        @Expose
        public String cancelReason;
        @SerializedName("user_details")
        @Expose
        public UserDetails userDetails;
        @SerializedName("company_name")
        @Expose
        public String companyName;
        @SerializedName("location")
        @Expose
        public String location;
        @SerializedName("company_latitude")
        @Expose
        public String companyLatitude;
        @SerializedName("company_longnitude")
        @Expose
        public String companyLongnitude;
        @SerializedName("company_image")
        @Expose
        public String companyImage;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("address_lat")
        @Expose
        public String addressLat;
        @SerializedName("address_lon")
        @Expose
        public String addressLon;
        @SerializedName("item_data")
        @Expose
        public List<ItemDatum> itemData = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCartId() {
            return cartId;
        }

        public void setCartId(String cartId) {
            this.cartId = cartId;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getAddressId() {
            return addressId;
        }

        public void setAddressId(String addressId) {
            this.addressId = addressId;
        }

        public String getDeliverytype() {
            return deliverytype;
        }

        public void setDeliverytype(String deliverytype) {
            this.deliverytype = deliverytype;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getAcceptTime() {
            return acceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            this.acceptTime = acceptTime;
        }

        public String getArrivedTime() {
            return arrivedTime;
        }

        public void setArrivedTime(String arrivedTime) {
            this.arrivedTime = arrivedTime;
        }

        public String getConfirmTime() {
            return confirmTime;
        }

        public void setConfirmTime(String confirmTime) {
            this.confirmTime = confirmTime;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getCancelReason() {
            return cancelReason;
        }

        public void setCancelReason(String cancelReason) {
            this.cancelReason = cancelReason;
        }

        public UserDetails getUserDetails() {
            return userDetails;
        }

        public void setUserDetails(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCompanyLatitude() {
            return companyLatitude;
        }

        public void setCompanyLatitude(String companyLatitude) {
            this.companyLatitude = companyLatitude;
        }

        public String getCompanyLongnitude() {
            return companyLongnitude;
        }

        public void setCompanyLongnitude(String companyLongnitude) {
            this.companyLongnitude = companyLongnitude;
        }

        public String getCompanyImage() {
            return companyImage;
        }

        public void setCompanyImage(String companyImage) {
            this.companyImage = companyImage;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddressLat() {
            return addressLat;
        }

        public void setAddressLat(String addressLat) {
            this.addressLat = addressLat;
        }

        public String getAddressLon() {
            return addressLon;
        }

        public void setAddressLon(String addressLon) {
            this.addressLon = addressLon;
        }

        public List<ItemDatum> getItemData() {
            return itemData;
        }

        public void setItemData(List<ItemDatum> itemData) {
            this.itemData = itemData;
        }

    }
    public class UserDetails {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("country_code")
        @Expose
        public String countryCode;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("register_id")
        @Expose
        public String registerId;
        @SerializedName("social_id")
        @Expose
        public String socialId;
        @SerializedName("lat")
        @Expose
        public String lat;
        @SerializedName("lon")
        @Expose
        public String lon;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("acc_holder_name")
        @Expose
        public String accHolderName;
        @SerializedName("account_number")
        @Expose
        public String accountNumber;
        @SerializedName("ifsc_code")
        @Expose
        public String ifscCode;
        @SerializedName("bank_name")
        @Expose
        public String bankName;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("car_type_id")
        @Expose
        public String carTypeId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getSocialId() {
            return socialId;
        }

        public void setSocialId(String socialId) {
            this.socialId = socialId;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAccHolderName() {
            return accHolderName;
        }

        public void setAccHolderName(String accHolderName) {
            this.accHolderName = accHolderName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public void setIfscCode(String ifscCode) {
            this.ifscCode = ifscCode;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCarTypeId() {
            return carTypeId;
        }

        public void setCarTypeId(String carTypeId) {
            this.carTypeId = carTypeId;
        }

    }

}
