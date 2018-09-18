package com.icsseseguridad.locationsecurity.controller;

import android.support.annotation.Nullable;

import com.fxn.utility.Utility;
import com.icsseseguridad.locationsecurity.model.ConfigUtility;
import com.icsseseguridad.locationsecurity.model.Guard;
import com.icsseseguridad.locationsecurity.model.ListAdmin;
import com.icsseseguridad.locationsecurity.model.ListBanner;
import com.icsseseguridad.locationsecurity.model.ListChannel;
import com.icsseseguridad.locationsecurity.model.ListChat;
import com.icsseseguridad.locationsecurity.model.ListChatLine;
import com.icsseseguridad.locationsecurity.model.ListChatWithUnread;
import com.icsseseguridad.locationsecurity.model.ListClerk;
import com.icsseseguridad.locationsecurity.model.ListCompany;
import com.icsseseguridad.locationsecurity.model.ListGuard;
import com.icsseseguridad.locationsecurity.model.ListIncidence;
import com.icsseseguridad.locationsecurity.model.ListRepliesWithUnread;
import com.icsseseguridad.locationsecurity.model.ListReply;
import com.icsseseguridad.locationsecurity.model.ListReport;
import com.icsseseguridad.locationsecurity.model.ListVisit;
import com.icsseseguridad.locationsecurity.model.ListVisitor;
import com.icsseseguridad.locationsecurity.model.ListVisitorVehicle;
import com.icsseseguridad.locationsecurity.model.MultipleResource;
import com.icsseseguridad.locationsecurity.model.User;

import java.util.List;

import okhttp3.RequestBody;
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

    @GET("public/banner")
    Call<ListBanner> getBanners(@Header("APP-TOKEN") String appToken);

    @FormUrlEncoded
    @POST("public/tablet")
    Call<MultipleResource> registered(@Field("imei") String imei);
}
