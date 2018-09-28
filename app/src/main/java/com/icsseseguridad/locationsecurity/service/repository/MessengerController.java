package com.icsseseguridad.locationsecurity.service.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.service.event.OnAddUsersToChannelFailure;
import com.icsseseguridad.locationsecurity.service.event.OnAddUsersToChannelSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChannelFailure;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChannelSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChatFailure;
import com.icsseseguridad.locationsecurity.service.event.OnCreateChatSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListAdminFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListAdminSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListChannelFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListChannelSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListChatFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListChatSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListGuardSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListMessageFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListMessageSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadMessagesFailure;
import com.icsseseguridad.locationsecurity.service.event.OnListUnreadMessagesSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSendMessageFailure;
import com.icsseseguridad.locationsecurity.service.event.OnSendMessageSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Channel;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.service.entity.ChatLine;
import com.icsseseguridad.locationsecurity.service.entity.ListAdmin;
import com.icsseseguridad.locationsecurity.service.entity.ListChannel;
import com.icsseseguridad.locationsecurity.service.entity.ListChat;
import com.icsseseguridad.locationsecurity.service.entity.ListChatLine;
import com.icsseseguridad.locationsecurity.service.entity.ListChatWithUnread;
import com.icsseseguridad.locationsecurity.service.entity.ListGuard;
import com.icsseseguridad.locationsecurity.service.entity.MultipleResource;
import com.icsseseguridad.locationsecurity.service.entity.User;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessengerController extends BaseController {

    public void register(String imei, String token, Long guardId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.registerToken(preferences.getToken(),
                imei,
                token,
                guardId);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                Log.e("Controller", "Save Registration Token Failure.");
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    Log.e("Controller", "Save Registration Token Failure: " + response.message());
                    return;
                }
                Log.d("Controller", "Registration Token Saved.");
            }
        });
    }

    public void getGuard() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListGuard> call = apiInterface.getGuards(preferences.getToken());
        call.enqueue(new Callback<ListGuard>() {
            @Override
            public void onFailure(Call<ListGuard> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListGuardFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListGuard> call, Response<ListGuard> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListGuardFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListGuardSuccess(response.body()));
                }
            }
        });
    }

    public void getAdmin() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListAdmin> call = apiInterface.getAdmins(preferences.getToken());
        call.enqueue(new Callback<ListAdmin>() {
            @Override
            public void onFailure(Call<ListAdmin> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListAdminFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListAdmin> call, Response<ListAdmin> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListAdminFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListAdminSuccess(response.body()));
                }
            }
        });
    }

    public void chat(Chat chat) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.createChat(preferences.getToken(),
                chat.user1Id,
                chat.user1Type.name(),
                chat.user1Name,
                chat.user2Id,
                chat.user2Type.name(),
                chat.user2Name);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnCreateChatFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        Log.e("MessengerController", new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnCreateChatFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnCreateChatFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    Log.e("MessengerController", new Gson().toJson(resource));
                    EventBus.getDefault().postSticky(new OnCreateChatFailure(resource.message));
                } else {
                    Chat chatCreated = gson.fromJson(gson.toJson(resource.result), Chat.class);
                    EventBus.getDefault().postSticky(new OnCreateChatSuccess(chatCreated));
                }
            }
        });
    }

    public void getMessages(Long chatId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListChatLine> call = apiInterface.getMessages(preferences.getToken(), chatId);
        call.enqueue(new Callback<ListChatLine>() {
            @Override
            public void onFailure(Call<ListChatLine> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListMessageFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListChatLine> call, Response<ListChatLine> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListMessageFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListMessageSuccess(response.body()));
                }
            }
        });
    }

    public void getChannelMessages(Long chatId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListChatLine> call = apiInterface.getChannelMessages(preferences.getToken(), chatId);
        call.enqueue(new Callback<ListChatLine>() {
            @Override
            public void onFailure(Call<ListChatLine> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListMessageFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListChatLine> call, Response<ListChatLine> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListMessageFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListMessageSuccess(response.body()));
                }
            }
        });
    }

    public void getConversations(Long guardId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListChat> call = apiInterface.getConversations(preferences.getToken(), guardId);
        call.enqueue(new Callback<ListChat>() {
            @Override
            public void onFailure(Call<ListChat> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListChatFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListChat> call, Response<ListChat> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListChatFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListChatSuccess(response.body()));
                }
            }
        });
    }

    public void send(ChatLine message) {
        System.out.println(new Gson().toJson(message));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.sendMessage(preferences.getToken(),
                message.chatId,
                message.channelId,
                message.senderId,
                message.senderType.name(),
                message.senderName,
                message.text);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnSendMessageFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        EventBus.getDefault().postSticky(new OnSendMessageFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnSendMessageFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                System.out.println(new Gson().toJson(resource));
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnSendMessageFailure(resource.message));
                } else {
                    ChatLine chatLine = gson.fromJson(gson.toJson(resource.result), ChatLine.class);
                    EventBus.getDefault().postSticky(new OnSendMessageSuccess(chatLine));
                }
            }
        });
    }

    public void channel(Channel channel) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.createChannel(preferences.getToken(),
                channel.name,
                channel.creatorId,
                channel.creatorType.name(),
                channel.creatorName);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnCreateChannelFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        Log.e("MessengerController", new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnCreateChannelFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnCreateChannelFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    Log.e("MessengerController", new Gson().toJson(resource));
                    EventBus.getDefault().postSticky(new OnCreateChannelFailure(resource.message));
                } else {
                    Channel channelCreated = gson.fromJson(gson.toJson(resource.result), Channel.class);
                    EventBus.getDefault().postSticky(new OnCreateChannelSuccess(channelCreated));
                }
            }
        });
    }

    public void getGroups(Long guardId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListChannel> call = apiInterface.getChannels(preferences.getToken(), guardId);
        call.enqueue(new Callback<ListChannel>() {
            @Override
            public void onFailure(Call<ListChannel> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListChannelFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListChannel> call, Response<ListChannel> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListChannelFailure(response.message()));
                } else {
                    System.out.println(response.toString());
                    EventBus.getDefault().postSticky(new OnListChannelSuccess(response.body()));
                }
            }
        });
    }

    public void addToChannel(List<User> users, Long channelId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.addToChannel(preferences.getToken(),
                channelId,
                users);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnAddUsersToChannelFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    try {
                        MultipleResource resource = gson.fromJson(response.errorBody().string(), MultipleResource.class);
                        Log.e("MessengerController", new Gson().toJson(resource));
                        EventBus.getDefault().postSticky(new OnAddUsersToChannelFailure(resource.message));
                    } catch (IOException e) {
                        EventBus.getDefault().postSticky(new OnAddUsersToChannelFailure(
                                SecurityApp.getAppContext().getString(R.string.error_connection)
                        ));
                    }
                    return;
                }
                MultipleResource resource = response.body();
                if (!resource.response) {
                    EventBus.getDefault().postSticky(new OnAddUsersToChannelFailure(resource.message));
                } else {
                    EventBus.getDefault().postSticky(new OnAddUsersToChannelSuccess(resource));
                }
            }
        });
    }

    public void getUnreadMessages() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ListChatWithUnread> call = apiInterface.getUnreadMessages(preferences.getToken(), preferences.getGuard().id);
        call.enqueue(new Callback<ListChatWithUnread>() {
            @Override
            public void onFailure(Call<ListChatWithUnread> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                EventBus.getDefault().postSticky(new OnListUnreadMessagesFailure(
                        SecurityApp.getAppContext().getString(R.string.error_connection)
                ));
            }
            @Override
            public void onResponse(Call<ListChatWithUnread> call, Response<ListChatWithUnread> response) {
                if (!response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new OnListUnreadMessagesFailure(response.message()));
                } else {
                    EventBus.getDefault().postSticky(new OnListUnreadMessagesSuccess(response.body()));
                }
            }
        });
    }

    public void putChatRead(final Long chatId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<MultipleResource> call = apiInterface.putChatRead(preferences.getToken(), preferences.getGuard().id, chatId);
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                Log.e("Controller", "Failure Make messages read for chat id = " + chatId);
            }
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                if (!response.isSuccessful()) {
                    Log.e("Controller", "Failure Make messages read for chat id = " + chatId);
                    return;
                }
                Log.e("Controller", "Success Make messages read for chat id = " + chatId);
                getUnreadMessages();
            }
        });
    }
}
