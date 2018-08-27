package com.icsseseguridad.locationsecurity.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.MessengerController;
import com.icsseseguridad.locationsecurity.model.ChatLine;
import com.icsseseguridad.locationsecurity.ui.chat.ChatActivity;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.Const;

import org.json.JSONObject;

import java.util.Map;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    public static final String MESSAGE = "MESSAGE";
    private static final String TAG = "AppFirebaseMsgService";
    public static final int ID_MESSAGE = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, remoteMessage.getData().toString());
        if (remoteMessage.getData() != null) {
            Map<String, String> notifyMap = remoteMessage.getData();
            String type = notifyMap.get("type");
            if (type.equals(MESSAGE)) {
                ChatLine message = gson().fromJson(notifyMap.get("message"), ChatLine.class);
                if (new AppPreferences(getApplicationContext()).getGuard() != null &&
                        new AppPreferences(getApplicationContext()).getGuard().id
                                .equals(message.receiverId)) {
                    sendNotification(message);
                    Intent intent = new Intent(Const.NEW_MESSAGE);
                    intent.putExtra(Const.NEW_MESSAGE, gson().toJson(message));
                    sendBroadcast(intent);
                } else {
                    Log.d(TAG, "Message is to another guard");
                }
            }
        } else {
            Log.d(TAG, "Message Notification is null");
        }
    }

    private void sendNotification(ChatLine message) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat_id", message.chatId);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Mensajes";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_message_black)
                        .setContentTitle("Mensaje Recibido")
                        .setContentText(message.text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Mensajes",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(ID_MESSAGE, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "on new token: " + s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        if (new AppPreferences(getApplicationContext()).getGuard() != null) {
            final String imei = new AppPreferences(getApplicationContext()).getImei();
            final Long guardId = new AppPreferences(getApplicationContext()).getGuard().id;
            new MessengerController().register(imei, token, guardId);
        }
    }

    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }
}
