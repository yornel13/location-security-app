package com.icsseseguridad.locationsecurity.service.repository;

import android.support.annotation.Nullable;

import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;
import com.icsseseguridad.locationsecurity.service.entity.Guard;
import com.icsseseguridad.locationsecurity.service.entity.ListAdmin;
import com.icsseseguridad.locationsecurity.service.entity.ListBanner;
import com.icsseseguridad.locationsecurity.service.entity.ListChannel;
import com.icsseseguridad.locationsecurity.service.entity.ListChat;
import com.icsseseguridad.locationsecurity.service.entity.ListChatLine;
import com.icsseseguridad.locationsecurity.service.entity.ListChatWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.ListClerk;
import com.icsseseguridad.locationsecurity.service.entity.ListCompany;
import com.icsseseguridad.locationsecurity.service.entity.ListGuard;
import com.icsseseguridad.locationsecurity.service.entity.ListIncidence;
import com.icsseseguridad.locationsecurity.service.entity.ListRepliesWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.ListReply;
import com.icsseseguridad.locationsecurity.service.entity.ListReport;
import com.icsseseguridad.locationsecurity.service.entity.ListVehicleType;
import com.icsseseguridad.locationsecurity.service.entity.ListVisit;
import com.icsseseguridad.locationsecurity.service.entity.ListVisitor;
import com.icsseseguridad.locationsecurity.service.entity.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.User;

import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("public/guard/active/1")
    Call<ListGuard> getGuards(@Header("APP-TOKEN") String appToken);

    @GET("public/admin/active/1")
    Call<ListAdmin> getAdmins(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/auth/guard")
    Call<MultipleResource> signIn(@Field("dni") String dni, @Field("password") String password);

    @FormUrlEncoded
    @POST("public/auth/admin")
    Call<MultipleResource> signInAdmin(@Field("dni") String dni, @Field("password") String password);

    @GET("public/auth/verify")
    Call<Guard> verifySession(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/watch/start")
    Call<MultipleResource> initWatch(
            @Header("APP-TOKEN") String appToken,
            @Field("guard_id") Long guardId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("tablet_imei") String imei);

    @FormUrlEncoded
    @PUT("public/watch/{watch_id}/end")
    Call<MultipleResource> finishWatch(@Header("APP-TOKEN") String appToken,
           @Path("watch_id") Long watch_id,
           @Field("f_latitude") String latitude,
           @Field("f_longitude") String longitude,
           @Nullable @Field("observation") String observation);

    @GET("public/visitor")
    Call<ListVisitor> getVisitors(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/visitor/sync")
    Call<MultipleResource> syncVisitor(
            @Header("APP-TOKEN") String appToken,
            @Field("dni") String dni,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("company") String company,
            @Field("photo") String photo,
            @Field("create_date") Timestamp createDate,
            @Field("update_date") Timestamp updateDate,
            @Field("active") int active);

    @FormUrlEncoded
    @POST("public/visitor")
    Call<MultipleResource> addVisitor(
            @Header("APP-TOKEN") String appToken,
            @Field("dni") String dni,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("company") String company,
            @Field("photo") String photo);

    @GET("public/visitor-vehicle")
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

    @FormUrlEncoded
    @POST("public/visitor-vehicle/sync")
    Call<MultipleResource> syncVehicle(
            @Header("APP-TOKEN") String appToken,
            @Field("plate") String plate,
            @Field("vehicle") String vehicle,
            @Field("model") String model,
            @Field("type") String type,
            @Field("photo") String photo,
            @Field("create_date") Timestamp createDate,
            @Field("update_date") Timestamp updateDate,
            @Field("active") int active);

    @GET("public/clerk")
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
    Call<MultipleResource> addVisit(@Header("APP-TOKEN") String appToken,
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

    @FormUrlEncoded
    @POST("public/visit/sync")
    Call<MultipleResource> syncVisit(@Header("APP-TOKEN") String appToken,
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
            @Field("image_5") String image5,
            @Field("create_date") Timestamp createDate,
            @Field("finish_date") Timestamp finishDate,
            @Field("comment") String comment,
            @Field("guard_out_id") Long guardOutId,
            @Field("f_latitude") String fLatitude,
            @Field("f_longitude") String fLongitude,
            @Field("sync_id") String syncId,
            @Field("status") int status);

    @FormUrlEncoded
    @PUT("public/visit/{id}")
    Call<MultipleResource> finishVisit(
            @Header("APP-TOKEN") String appToken,
            @Path("id") Long id,
            @Field("comment") String comment);

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

    @FormUrlEncoded
    @POST("public/binnacle/sync")
    Call<MultipleResource> syncReport(@Header("APP-TOKEN") String appToken,
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
            @Field("image_5") String image5,
            @Field("create_date") Timestamp createDate,
            @Field("update_date") Timestamp finishDate,
            @Field("sync_id") String syncId,
            @Field("status") int status,
            @Field("resolved") int resolved);

    @GET("public/binnacle/resolved/all/guard/{id}")
    Call<ListReport> getGuardReports(@Header("APP-TOKEN") String appToken, @Path("id") Long id);

    @GET("public/binnacle/{id}/replies")
    Call<ListReply> getReplies(@Header("APP-TOKEN") String appToken, @Path("id") Long reportId);

    @FormUrlEncoded
    @POST("public/binnacle-reply")
    Call<MultipleResource> postReply(
            @Header("APP-TOKEN") String appToken,
            @Field("report_id") Long reportId,
            @Field("guard_id") Long guardId,
            @Field("user_name") String userName,
            @Field("text") String text);

    @GET("public/binnacle-reply/guard/{guard_id}/comment/unread")
    Call<ListRepliesWithUnread> getUnreadReplies(@Header("APP-TOKEN") String appToken , @Path("guard_id") Long guardId);

    @PUT("public/binnacle-reply/guard/report/{report_id}/read")
    Call<MultipleResource> putReportRead(@Header("APP-TOKEN") String appToken, @Path("report_id") Long reportId);

    @GET("public/utility/name/TABLET_GPS_UPDATE")
    Call<ConfigUtility> getUpdateGPS(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/tablet/position")
    Call<MultipleResource> postPosition(
            @Header("APP-TOKEN") String appToken,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("watch_id") Long watch_id,
            @Field("imei") String imei,
            @Field("message") String message,
            @Field("message_time") String messageTime);

    @FormUrlEncoded
    @POST("public/tablet/position/sync")
    Call<MultipleResource> syncPosition(@Header("APP-TOKEN") String appToken,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("generated_time") Timestamp generatedTime,
            @Field("message_time") Timestamp messageTime,
            @Field("watch_id") Long watch_id,
            @Field("imei") String imei,
            @Field("message") String message,
            @Field("alert_message") String alertMessage,
            @Field("is_exception") int isException);

    @FormUrlEncoded
    @POST("public/messenger/register/tablet")
    Call<MultipleResource> registerToken(
            @Header("APP-TOKEN") String appToken,
            @Field("imei") String imei,
            @Field("registration_id") String registrationId,
            @Field("guard_id") Long guard_id);

    @FormUrlEncoded
    @POST("public/messenger/chat")
    Call<MultipleResource> createChat(
            @Header("APP-TOKEN") String appToken,
            @Field("user_1_id") Long user1Id,
            @Field("user_1_type") String user1Type,
            @Field("user_1_name") String user1Name,
            @Field("user_2_id") Long user2Id,
            @Field("user_2_type") String user2Type,
            @Field("user_2_name") String user2Name);

    @GET("public/messenger/conversations/guard/{id}")
    Call<ListChat> getConversations(@Header("APP-TOKEN") String appToken , @Path("id") Long guardId);

    @GET("public/messenger/conversations/chat/{id}")
    Call<ListChatLine> getMessages(@Header("APP-TOKEN") String appToken , @Path("id") Long chatId);

    @GET("public/messenger/conversations/channel/{id}")
    Call<ListChatLine> getChannelMessages(@Header("APP-TOKEN") String appToken , @Path("id") Long chatId);

    @GET("public/messenger/channel/guard/{id}")
    Call<ListChannel> getChannels(@Header("APP-TOKEN") String appToken , @Path("id") Long guardId);

    @FormUrlEncoded
    @POST("public/messenger/send")
    Call<MultipleResource> sendMessage(
            @Header("APP-TOKEN") String appToken,
            @Field("chat_id") Long chatId,
            @Field("channel_id") Long channelId,
            @Field("sender_id") Long senderId,
            @Field("sender_type") String senderType,
            @Field("sender_name") String senderName,
            @Field("text") String text);

    @FormUrlEncoded
    @POST("public/messenger/channel")
    Call<MultipleResource> createChannel(
            @Header("APP-TOKEN") String appToken,
            @Field("name") String name,
            @Field("creator_id") Long creatorId,
            @Field("creator_type") String creatorType,
            @Field("creator_name") String creatorName);

    @POST("public/messenger/channel/{channel_id}/add")
    Call<MultipleResource> addToChannel(
            @Header("APP-TOKEN") String appToken,
            @Path("channel_id") Long channelId,
            @Body List<User> body);

    @GET("public/messenger/conversations/guard/{guard_id}/chat/unread")
    Call<ListChatWithUnread> getUnreadMessages(@Header("APP-TOKEN") String appToken , @Path("guard_id") Long guardId);

    @PUT("public/messenger/conversations/guard/{guard_id}/chat/{chat_id}/read")
    Call<MultipleResource> putChatRead(@Header("APP-TOKEN") String appToken, @Path("guard_id") Long guardId, @Path("chat_id") Long chatId);

    @FormUrlEncoded
    @POST("public/alert")
    Call<MultipleResource> sendAlert(
            @Field("guard_id") Long guardId,
            @Field("cause") String cause,
            @Field("type") String type,
            @Field("message") String message,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("public/alert/sync")
    Call<MultipleResource> syncAlert(
            @Field("guard_id") Long guardId,
            @Field("cause") String cause,
            @Field("type") String type,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("message") String message,
            @Field("create_date") Timestamp createDate,
            @Field("update_date") Timestamp updateDate,
            @Field("status") int status);

    @GET("public/banner")
    Call<ListBanner> getBanners(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/tablet")
    Call<MultipleResource> registered(@Field("imei") String imei);

    @GET("public/vehicle_type")
    Call<ListVehicleType> getVehiclesTypes(@Header("APP-TOKEN") String appToken);
}
