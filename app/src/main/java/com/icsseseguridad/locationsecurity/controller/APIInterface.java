package com.icsseseguridad.locationsecurity.controller;

import android.support.annotation.Nullable;

import com.fxn.utility.Utility;
import com.icsseseguridad.locationsecurity.model.ConfigUtility;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.model.ListClerk;
import com.icsseseguridad.locationsecurity.model.ListCompany;
import com.icsseseguridad.locationsecurity.model.ListGuard;
import com.icsseseguridad.locationsecurity.model.ListIncidence;
import com.icsseseguridad.locationsecurity.model.ListReply;
import com.icsseseguridad.locationsecurity.model.ListReport;
import com.icsseseguridad.locationsecurity.model.ListVisit;
import com.icsseseguridad.locationsecurity.model.ListVisitor;
import com.icsseseguridad.locationsecurity.model.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.model.MultipleResource;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface APIInterface {

    @GET("public/guard")
    Call<ListGuard> getGuards();

    @FormUrlEncoded
    @POST("public/auth/guard")
    Call<MultipleResource> signIn(@Field("dni") String dni, @Field("password") String password);

    @FormUrlEncoded
    @POST("public/watch")
    Call<MultipleResource> initWatch(
            @Header("APP-TOKEN") String appToken,
            @Field("guard_id") Long guardId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Nullable @Field("observation") String observation);

    @FormUrlEncoded
    @PUT("public/watch/{watch_id}")
    Call<MultipleResource> finishWatch(@Header("APP-TOKEN") String appToken,
           @Path("watch_id") Long watchId,
           @Field("latitude") String latitude,
           @Field("longitude") String longitude,
           @Nullable @Field("observation") String observation);

    @GET("public/visitor/active/1")
    Call<ListVisitor> getVisitors(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/visitor")
    Call<MultipleResource> addVisitor(
            @Header("APP-TOKEN") String appToken,
            @Field("dni") String dni,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("company") String company,
            @Field("photo") String photo);

    @GET("public/visitor-vehicle/active/1")
    Call<ListVisitorVehicle> getVisitorVehicles(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/visitor-vehicle")
    Call<MultipleResource> addVehicle(
            @Header("APP-TOKEN") String appToken,
            @Field("plate") String plate,
            @Field("vehicle") String vehicle,
            @Field("model") String model,
            @Field("type") String type,
            @Field("photo") String photo);

    @GET("public/clerk/active/1")
    Call<ListClerk> getClerks(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/clerk")
    Call<MultipleResource> addClerk(
            @Header("APP-TOKEN") String appToken,
            @Field("dni") String dni,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("address") String address);

    @GET("public/company")
    Call<ListCompany> getCompanies(@Header("APP-TOKEN") String appToken);

    @GET("public/visit/active/1")
    Call<ListVisit> getActiveVisits(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/visit")
    Call<MultipleResource> addVisit(
            @Header("APP-TOKEN") String appToken,
            @Field("vehicle_id") Long vehicleId,
            @Field("visitor_id") Long visitorId,
            @Field("visited_id") Long clerkId,
            @Field("guard_id") Long guardId,
            @Field("persons") Integer persons,
            @Field("observation") String materials,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("image_1") String image1,
            @Field("image_2") String image2,
            @Field("image_3") String image3,
            @Field("image_4") String image4,
            @Field("image_5") String image5);

    @PUT("public/visit/{id}")
    Call<MultipleResource> finishVisit(@Header("APP-TOKEN") String appToken, @Path("id") Long id);

    @GET("public/incidence")
    Call<ListIncidence> getIncidences(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/binnacle")
    Call<MultipleResource> addReport(
            @Header("APP-TOKEN") String appToken,
            @Field("watch_id") Long watchId,
            @Field("incidence_id") Long incidenceId,
            @Field("title") String title,
            @Field("observation") String observation,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("image_1") String image1,
            @Field("image_2") String image2,
            @Field("image_3") String image3,
            @Field("image_4") String image4,
            @Field("image_5") String image5);

    @GET("public/binnacle/guard/{id}")
    Call<ListReport> getGuardReports(@Header("APP-TOKEN") String appToken, @Path("id") Long id);

    @GET("public/binnacle/{id}/replies")
    Call<ListReply> getReplies(@Header("APP-TOKEN") String appToken, @Path("id") Long reportId);

    @FormUrlEncoded
    @POST("public/binnacle")
    Call<MultipleResource> addReply(
            @Header("APP-TOKEN") String appToken,
            @Field("report_id") Long reportId,
            @Field("guard_id") Long guardId,
            @Field("text") String text);

    @FormUrlEncoded
    @POST("public/binnacle-reply")
    Call<MultipleResource> postReply(
            @Header("APP-TOKEN") String appToken,
            @Field("report_id") Long reportId,
            @Field("guard_id") Long guardId,
            @Field("user_name") String userName,
            @Field("text") String text);

    @GET("public/utility/name/TABLET_GPS_UPDATE")
    Call<ConfigUtility> getUpdateGPS(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/tablet")
    Call<MultipleResource> postPosition(
            @Header("APP-TOKEN") String appToken,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("watch_id") Long watch_id,
            @Field("imei") String imei,
            @Field("message") String message,
            @Field("message_time") String messageTime);

}
