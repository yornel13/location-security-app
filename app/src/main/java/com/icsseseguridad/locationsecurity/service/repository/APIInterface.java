package com.icsseseguridad.locationsecurity.service.repository;

import com.icsseseguridad.locationsecurity.service.entity.Alert;
import com.icsseseguridad.locationsecurity.service.entity.Channel;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.service.entity.ChatLine;
import com.icsseseguridad.locationsecurity.service.entity.ConfigUtility;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
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
import com.icsseseguridad.locationsecurity.service.entity.Reply;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.entity.User;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("guard/active/1")
    Call<ListGuard> getGuards(@Header("APP-TOKEN") String appToken);

    @GET("admin/active/1")
    Call<ListAdmin> getAdmins(@Header("APP-TOKEN") String appToken);

    @POST("auth/guard")
    Call<MultipleResource> signIn(@Body Map<String, Object> body);

    @POST("auth/admin")
    Call<MultipleResource> signInAdmin(@Body Map<String, Object> body);

    @GET("auth/verify")
    Call<Guard> verifySession(@Header("APP-TOKEN") String appToken);

    @GET("tablet/verify/{imei}")
    Call<MultipleResource> verifyTablet(@Path("imei") String imei);

    @POST("watch/start")
    Call<MultipleResource> initWatch(
            @Header("APP-TOKEN") String appToken,
            @Body Map<String, Object> body);

    @PUT("watch/{watch_id}/end")
    Call<MultipleResource> finishWatch(@Header("APP-TOKEN") String appToken,
           @Path("watch_id") Long watch_id,
           @Body Map<String, Object> body);

    @GET("visitor")
    Call<ListVisitor> getVisitors(@Header("APP-TOKEN") String appToken);

    @POST("visitor/sync")
    Call<MultipleResource> syncVisitor(
            @Header("APP-TOKEN") String appToken,
            @Body Visitor visitor);

    @POST("visitor")
    Call<MultipleResource> addVisitor(
            @Header("APP-TOKEN") String appToken,
            @Field("dni") String dni,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("company") String company,
            @Field("photo") String photo);

    @GET("visitor-vehicle")
    Call<ListVisitorVehicle> getVisitorVehicles(@Header("APP-TOKEN") String appToken);

    @POST("visitor-vehicle")
    Call<MultipleResource> addVehicle(
            @Header("APP-TOKEN") String appToken,
            @Field("plate") String plate,
            @Field("vehicle") String vehicle,
            @Field("model") String model,
            @Field("type") String type,
            @Field("photo") String photo);

    @POST("visitor-vehicle/sync")
    Call<MultipleResource> syncVehicle(
            @Header("APP-TOKEN") String appToken,
            @Body VisitorVehicle vehicle);

    @GET("clerk")
    Call<ListClerk> getClerks(@Header("APP-TOKEN") String appToken);

    @POST("clerk")
    Call<MultipleResource> addClerk(
            @Header("APP-TOKEN") String appToken,
            @Field("dni") String dni,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("address") String address);

    @GET("company")
    Call<ListCompany> getCompanies(@Header("APP-TOKEN") String appToken);

    @GET("visit/active/1")
    Call<ListVisit> getActiveVisits(@Header("APP-TOKEN") String appToken);

    @POST("visit")
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

    @POST("visit/sync")
    Call<MultipleResource> syncVisit(@Header("APP-TOKEN") String appToken,
                                     @Body ControlVisit visit);

    @PUT("visit/{id}")
    Call<MultipleResource> finishVisit(
            @Header("APP-TOKEN") String appToken,
            @Path("id") Long id,
            @Field("comment") String comment);

    @GET("incidence")
    Call<ListIncidence> getIncidences(@Header("APP-TOKEN") String appToken);

    @POST("binnacle")
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

    @POST("binnacle/sync")
    Call<MultipleResource> syncReport(@Header("APP-TOKEN") String appToken,
                                      @Body SpecialReport report);

    @GET("binnacle/resolved/all/guard/{id}")
    Call<ListReport> getGuardReports(@Header("APP-TOKEN") String appToken, @Path("id") Long id);

    @GET("binnacle/{id}/replies")
    Call<ListReply> getReplies(@Header("APP-TOKEN") String appToken, @Path("id") Long reportId);

    @POST("binnacle-reply")
    Call<MultipleResource> postReply(
            @Header("APP-TOKEN") String appToken,
            @Body Reply reply);

    @GET("binnacle-reply/guard/{guard_id}/comment/unread")
    Call<ListRepliesWithUnread> getUnreadReplies(@Header("APP-TOKEN") String appToken , @Path("guard_id") Long guardId);

    @PUT("binnacle-reply/guard/report/{report_id}/read")
    Call<MultipleResource> putReportRead(@Header("APP-TOKEN") String appToken, @Path("report_id") Long reportId);

    @GET("utility/name/TABLET_GPS_UPDATE")
    Call<ConfigUtility> getUpdateGPS(@Header("APP-TOKEN") String appToken);

    @POST("tablet/position")
    Call<MultipleResource> postPosition(
            @Header("APP-TOKEN") String appToken,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("watch_id") Long watch_id,
            @Field("imei") String imei,
            @Field("message") String message,
            @Field("message_time") String messageTime);

    @POST("tablet/position/sync")
    Call<MultipleResource> syncPosition(@Header("APP-TOKEN") String appToken,
            @Body TabletPosition position);

    @POST("messenger/register/tablet")
    Call<MultipleResource> registerToken(
            @Header("APP-TOKEN") String appToken,
            @Body Map<String, Object> body);

    @POST("messenger/chat")
    Call<MultipleResource> createChat(
            @Header("APP-TOKEN") String appToken,
            @Body Chat chat);

    @GET("messenger/conversations/guard/{id}")
    Call<ListChat> getConversations(@Header("APP-TOKEN") String appToken , @Path("id") Long guardId);

    @GET("messenger/conversations/chat/{id}")
    Call<ListChatLine> getMessages(@Header("APP-TOKEN") String appToken , @Path("id") Long chatId);

    @GET("messenger/conversations/channel/{id}")
    Call<ListChatLine> getChannelMessages(@Header("APP-TOKEN") String appToken , @Path("id") Long chatId);

    @GET("messenger/channel/guard/{id}")
    Call<ListChannel> getChannels(@Header("APP-TOKEN") String appToken , @Path("id") Long guardId);

    @POST("messenger/send")
    Call<MultipleResource> sendMessage(
            @Header("APP-TOKEN") String appToken,
            @Body ChatLine chat);

    @POST("messenger/channel")
    Call<MultipleResource> createChannel(
            @Header("APP-TOKEN") String appToken,
            @Body Channel channel);

    @POST("messenger/channel/{channel_id}/add")
    Call<MultipleResource> addToChannel(
            @Header("APP-TOKEN") String appToken,
            @Path("channel_id") Long channelId,
            @Body List<User> body);

    @GET("messenger/conversations/guard/{guard_id}/chat/unread")
    Call<ListChatWithUnread> getUnreadMessages(@Header("APP-TOKEN") String appToken , @Path("guard_id") Long guardId);

    @PUT("messenger/conversations/guard/{guard_id}/chat/{chat_id}/read")
    Call<MultipleResource> putChatRead(@Header("APP-TOKEN") String appToken, @Path("guard_id") Long guardId, @Path("chat_id") Long chatId);

    @POST("alert")
    Call<MultipleResource> sendAlert(
            @Header("APP-TOKEN") String appToken,
            @Body Alert alert);

    @POST("alert/sync")
    Call<MultipleResource> syncAlert(
            @Header("APP-TOKEN") String appToken,
            @Body Alert alert);

    @GET("banner")
    Call<ListBanner> getBanners(@Header("APP-TOKEN") String appToken);

    @POST("tablet")
    Call<MultipleResource> registered(@Header("APP-TOKEN") String appToken,
                                      @Body Map<String, Object> body);

    @GET("vehicle_type")
    Call<ListVehicleType> getVehiclesTypes(@Header("APP-TOKEN") String appToken);
}
