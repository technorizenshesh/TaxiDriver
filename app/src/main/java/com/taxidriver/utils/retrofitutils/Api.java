package com.taxidriver.utils.retrofitutils;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("change_password")
    Call<ResponseBody> changePass(@FieldMap Map<String, String> params);

    @POST("get_faq")
    Call<ResponseBody> getAllFAQInformation();

    @FormUrlEncoded
    @POST("insert_chat")
    Call<ResponseBody> insertChatApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_chat")
    Call<ResponseBody> getConversationApiCAll(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_card_data")
    Call<ResponseBody> addCardApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_emergency")
    Call<ResponseBody> deleteEmergencyContactApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("wallet_transfer")
    Call<ResponseBody> walletTransferApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("emergency_number")
    Call<ResponseBody> addContactApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_emergency")
    Call<ResponseBody> getAllEmerContacts(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_contact_us")
    Call<ResponseBody> contactUsApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_my_transaction")
    Call<ResponseBody> getTransactionApiCall(@FieldMap Map<String, String> params);

    @POST("car_list")
    Call<ResponseBody> getCarList();

    @FormUrlEncoded
    @POST("add_wallet")
    Call<ResponseBody> addWalletAmount(@FieldMap Map<String, String> params);

    @POST("get_route")
    Call<ResponseBody> getRoutes();

    @FormUrlEncoded
    @POST("get_review_driver")
    Call<ResponseBody> getUserReviews(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_schedule_booking")
    Call<ResponseBody> getScheduleBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_details")
    Call<ResponseBody> getCurrentBookingDetails(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_user_history")
    Call<ResponseBody> getHistoryApiCall(@FieldMap Map<String, String> params);

    @POST("get_make")
    Call<ResponseBody> getCarMakeList();

    @POST("get_donation")
    Call<ResponseBody> gteDonationApiCall();

    @FormUrlEncoded
    @POST("get_model")
    Call<ResponseBody> getCarModelCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_lat_lon")
    Call<Map<String, String>> updateLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgotPass(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_car_detail")
    Call<ResponseBody> getVehicleListApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("vehicle_online")
    Call<ResponseBody> updateOnlineVehicle(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_car_detail")
    Call<ResponseBody> deleteCarApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_history")
    Call<ResponseBody> getTaxiHistory(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_online_status")
    Call<ResponseBody> updateOnOffApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_profile")
    Call<ResponseBody> getProfileCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("check_valid_login")
    Call<ResponseBody> checkLoginValidCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("available_car_driver")
    Call<ResponseBody> getAvailableDrivers(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_available_driver")
    Call<ResponseBody> getAvailableCarDriversHome(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_car_type_list")
    Call<ResponseBody> getCarTypeListApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_dev_order")
    Call<ResponseBody> getDevOrdersApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("social_login")
    Call<ResponseBody> socialLogin(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_bank_account")
    Call<ResponseBody> addBankAccount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentTaxiBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("booking_request")
    Call<ResponseBody> bookingRequestApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("offer_pool_request")
    Call<ResponseBody> offerPoolRequestApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_pool_request")
    Call<ResponseBody> getUserRequestsApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("accept_and_cancel_pool_request")
    Call<ResponseBody> acceptRejectPoolApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_offer_pool_request")
    Call<ResponseBody> getOfferedPoolApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCallTaxi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("earning_data")
    Call<ResponseBody> getAllEarningsApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("waiting_status")
    Call<ResponseBody> waitingTimeAPiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("place_order")
    Call<ResponseBody> placeDevOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signUpApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("available_car_driver")
    Call<ResponseBody> getAvailableCarCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> updateOrderStatusApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_count_cart")
    Call<ResponseBody> getCartCountApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_lat_lon")
    Call<ResponseBody> getLatLonDriver(@FieldMap Map<String, String> params);

    @Multipart
    @POST("add_document")
    Call<ResponseBody> addDriverDocumentApiCall(@Part("user_id") RequestBody user_id,
                                                @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2);

    @Multipart
    @POST("signup")
    Call<ResponseBody> signUpDriverCallApi(@Part("first_name") RequestBody first_name,
                                           @Part("last_name") RequestBody last_name,
                                           @Part("email") RequestBody email,
                                           @Part("mobile") RequestBody mobile,
                                           @Part("city") RequestBody city,
                                           @Part("address") RequestBody address,
                                           @Part("register_id") RequestBody register_id,
                                           @Part("lat") RequestBody lat,
                                           @Part("lon") RequestBody lon,
                                           @Part("password") RequestBody password,
                                           @Part("type") RequestBody type,
                                           @Part("step") RequestBody step,
                                           @Part("user_name") RequestBody username,
                                           @Part("workplace") RequestBody workplace,
                                           @Part("work_lat") RequestBody work_lat,
                                           @Part("work_lon") RequestBody work_lon,
                                           @Part MultipartBody.Part file1);

    @Multipart
    @POST("update_doc")
    Call<ResponseBody> updateLisenceApiCall(@Part("expiry_driving_lisence_date") RequestBody expiry_driving_lisence_date,
                                           @Part("user_id") RequestBody user_id,
                                           @Part MultipartBody.Part file1);

    @Multipart
    @POST("update_pan_doc")
    Call<ResponseBody> updatePanApiCall(@Part("expiry_pan_date") RequestBody expiry_driving_lisence_date,
                                           @Part("user_id") RequestBody user_id,
                                           @Part MultipartBody.Part file1);

    @Multipart
    @POST("update_profile")
    Call<ResponseBody> updateDriverCallApi(@Part("user_id") RequestBody user_id,
                                           @Part("first_name") RequestBody first_name,
                                           @Part("last_name") RequestBody last_name,
                                           @Part("mobile") RequestBody mobile,
                                           @Part("email") RequestBody email,
                                           @Part("address") RequestBody address,
                                           @Part("lat") RequestBody lat,
                                           @Part("lon") RequestBody lon,
                                           @Part("workplace") RequestBody workplace,
                                           @Part("work_lon") RequestBody work_lon,
                                           @Part("work_lat") RequestBody work_lat,
                                           @Part("city") RequestBody city,
                                           @Part MultipartBody.Part file1);

    @Multipart
    @POST("update_security_image")
    Call<ResponseBody> updateSecurityEssentials(@Part("user_id") RequestBody user_id,
                                        @Part MultipartBody.Part file1);

    @Multipart
    @POST("add_vehicle")
    Call<ResponseBody> addDriverVehicle(@Part("user_id") RequestBody user_id,
                                        @Part("brand") RequestBody car_brand,
                                        @Part("car_model") RequestBody car_model,
                                        @Part("car_number") RequestBody carNumber,
                                        @Part("year_of_manufacture") RequestBody year_of_manufacture,
                                        @Part("basic_car") RequestBody basic_car,
                                        @Part("normal_car") RequestBody normal_car,
                                        @Part("luxurious_car") RequestBody luxurious_car,
                                        @Part("pool_car") RequestBody pool_car,
                                        @Part MultipartBody.Part file1);

    @Multipart
    @POST("update_vehicle")
    Call<ResponseBody> editDriverVehicle(@Part("user_id") RequestBody user_id,
                                         @Part("brand") RequestBody car_brand,
                                         @Part("car_model") RequestBody car_model,
                                         @Part("car_number") RequestBody carNumber,
                                         @Part("year_of_manufacture") RequestBody year_of_manufacture,
                                         @Part("basic_car") RequestBody basic_car,
                                         @Part("normal_car") RequestBody normal_car,
                                         @Part("luxurious_car") RequestBody luxurious_car,
                                         @Part("pool_car") RequestBody pool_car,
                                         @Part("vehicle_id") RequestBody vehicle_id,
                                         @Part MultipartBody.Part file1);

}


