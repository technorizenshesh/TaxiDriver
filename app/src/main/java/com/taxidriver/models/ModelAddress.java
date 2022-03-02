package com.taxidriver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelAddress implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public ArrayList<Result> getResult() {
        return this.result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public class Result implements Serializable {

        private String id;

        private String address;

        private String lat;

        private String lon;

        private String date_time;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return this.address;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLat() {
            return this.lat;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLon() {
            return this.lon;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }
    }


}
