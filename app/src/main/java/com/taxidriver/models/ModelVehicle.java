package com.taxidriver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelVehicle implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }

    public class Result implements Serializable
    {
        private String id;

        private String driver_id;

        private String car_type_id;

        private String car_number;

        private String brand;

        private String car_model;

        private String make_name;

        private String model_name;

        private String service_type;

        private String car_type_status;

        private String online_status;

        private String car_image;

        private String year_of_manufacture;

        private String status;

        private String driver_email;

        private String driver_mobile;

        private String car_seats;

        private String car_color;

        private String driver_car_name;

        private String car_extra_detail;

        private String license_front;

        private String license_back;

        private String road_worthy_certificate;

        private String road_worthy_expiry_date;

        private String comprehensive_insurance;

        private String comprehensive_insurance_expiry_date;

        private String car_inspection_document;

        private String car_inspection_expiry_date;

        private String basic_car;

        private String normal_car;

        private String luxurious_car;

        private String pool_car;

        public String getCar_type_status() {
            return car_type_status;
        }

        public void setCar_type_status(String car_type_status) {
            this.car_type_status = car_type_status;
        }

        public String getOnline_status() {
            return online_status;
        }

        public void setOnline_status(String online_status) {
            this.online_status = online_status;
        }

        public String getMake_name() {
            return make_name;
        }

        public void setMake_name(String make_name) {
            this.make_name = make_name;
        }

        public String getModel_name() {
            return model_name;
        }

        public void setModel_name(String model_name) {
            this.model_name = model_name;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setDriver_id(String driver_id){
            this.driver_id = driver_id;
        }
        public String getDriver_id(){
            return this.driver_id;
        }
        public void setCar_type_id(String car_type_id){
            this.car_type_id = car_type_id;
        }
        public String getCar_type_id(){
            return this.car_type_id;
        }
        public void setCar_number(String car_number){
            this.car_number = car_number;
        }
        public String getCar_number(){
            return this.car_number;
        }
        public void setBrand(String brand){
            this.brand = brand;
        }
        public String getBrand(){
            return this.brand;
        }
        public void setCar_model(String car_model){
            this.car_model = car_model;
        }
        public String getCar_model(){
            return this.car_model;
        }
        public void setService_type(String service_type){
            this.service_type = service_type;
        }
        public String getService_type(){
            return this.service_type;
        }
        public void setCar_image(String car_image){
            this.car_image = car_image;
        }
        public String getCar_image(){
            return this.car_image;
        }
        public void setYear_of_manufacture(String year_of_manufacture){
            this.year_of_manufacture = year_of_manufacture;
        }
        public String getYear_of_manufacture(){
            return this.year_of_manufacture;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setDriver_email(String driver_email){
            this.driver_email = driver_email;
        }
        public String getDriver_email(){
            return this.driver_email;
        }
        public void setDriver_mobile(String driver_mobile){
            this.driver_mobile = driver_mobile;
        }
        public String getDriver_mobile(){
            return this.driver_mobile;
        }
        public void setCar_seats(String car_seats){
            this.car_seats = car_seats;
        }
        public String getCar_seats(){
            return this.car_seats;
        }
        public void setCar_color(String car_color){
            this.car_color = car_color;
        }
        public String getCar_color(){
            return this.car_color;
        }
        public void setDriver_car_name(String driver_car_name){
            this.driver_car_name = driver_car_name;
        }
        public String getDriver_car_name(){
            return this.driver_car_name;
        }
        public void setCar_extra_detail(String car_extra_detail){
            this.car_extra_detail = car_extra_detail;
        }
        public String getCar_extra_detail(){
            return this.car_extra_detail;
        }
        public void setLicense_front(String license_front){
            this.license_front = license_front;
        }
        public String getLicense_front(){
            return this.license_front;
        }
        public void setLicense_back(String license_back){
            this.license_back = license_back;
        }
        public String getLicense_back(){
            return this.license_back;
        }
        public void setRoad_worthy_certificate(String road_worthy_certificate){
            this.road_worthy_certificate = road_worthy_certificate;
        }
        public String getRoad_worthy_certificate(){
            return this.road_worthy_certificate;
        }
        public void setRoad_worthy_expiry_date(String road_worthy_expiry_date){
            this.road_worthy_expiry_date = road_worthy_expiry_date;
        }
        public String getRoad_worthy_expiry_date(){
            return this.road_worthy_expiry_date;
        }
        public void setComprehensive_insurance(String comprehensive_insurance){
            this.comprehensive_insurance = comprehensive_insurance;
        }
        public String getComprehensive_insurance(){
            return this.comprehensive_insurance;
        }
        public void setComprehensive_insurance_expiry_date(String comprehensive_insurance_expiry_date){
            this.comprehensive_insurance_expiry_date = comprehensive_insurance_expiry_date;
        }
        public String getComprehensive_insurance_expiry_date(){
            return this.comprehensive_insurance_expiry_date;
        }
        public void setCar_inspection_document(String car_inspection_document){
            this.car_inspection_document = car_inspection_document;
        }
        public String getCar_inspection_document(){
            return this.car_inspection_document;
        }
        public void setCar_inspection_expiry_date(String car_inspection_expiry_date){
            this.car_inspection_expiry_date = car_inspection_expiry_date;
        }
        public String getCar_inspection_expiry_date(){
            return this.car_inspection_expiry_date;
        }
        public void setBasic_car(String basic_car){
            this.basic_car = basic_car;
        }
        public String getBasic_car(){
            return this.basic_car;
        }
        public void setNormal_car(String normal_car){
            this.normal_car = normal_car;
        }
        public String getNormal_car(){
            return this.normal_car;
        }
        public void setLuxurious_car(String luxurious_car){
            this.luxurious_car = luxurious_car;
        }
        public String getLuxurious_car(){
            return this.luxurious_car;
        }
        public void setPool_car(String pool_car){
            this.pool_car = pool_car;
        }
        public String getPool_car(){
            return this.pool_car;
        }
    }

}
