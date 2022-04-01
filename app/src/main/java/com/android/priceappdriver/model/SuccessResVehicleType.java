package com.android.priceappdriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SuccessResVehicleType
{

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("message")
    @Expose
    public String message;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("car_name")
        @Expose
        public String carName;
        @SerializedName("car_image")
        @Expose
        public String carImage;
        @SerializedName("charge")
        @Expose
        public String charge;
        @SerializedName("per_km")
        @Expose
        public String perKm;
        @SerializedName("ride_time_charge_permin")
        @Expose
        public String rideTimeChargePermin;
        @SerializedName("status")
        @Expose
        public String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getCarImage() {
            return carImage;
        }

        public void setCarImage(String carImage) {
            this.carImage = carImage;
        }

        public String getCharge() {
            return charge;
        }

        public void setCharge(String charge) {
            this.charge = charge;
        }

        public String getPerKm() {
            return perKm;
        }

        public void setPerKm(String perKm) {
            this.perKm = perKm;
        }

        public String getRideTimeChargePermin() {
            return rideTimeChargePermin;
        }

        public void setRideTimeChargePermin(String rideTimeChargePermin) {
            this.rideTimeChargePermin = rideTimeChargePermin;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
    
}
