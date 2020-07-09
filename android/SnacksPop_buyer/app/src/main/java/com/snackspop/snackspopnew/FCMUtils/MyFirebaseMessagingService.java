package com.snackspop.snackspopnew.FCMUtils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.snackspop.snackspopnew.Activity.ChattingActivity;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by suraj on 22/07/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static  int countForNotificationsInGroup = 0 ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        LogCat.e(TAG, "From: " + remoteMessage.getFrom());
        LogCat.e(TAG, "Notification Message Body: " + remoteMessage.getData());
        LogCat.e(TAG, "Notification type: " + remoteMessage.getMessageType());
        LogCat.e(TAG, "Notification type: " + remoteMessage.getMessageId());


        try {
            if (!TextUtils.isEmpty(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, this))) {
                Intent intent = new Intent(this, ChattingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Map<String,String> remoteMessageData = remoteMessage.getData();
                intent.putExtra(AppUtils.EXTRA.IS_FROM_NOTIFICATION, true);
                String notification_title = remoteMessage.getNotification().getTitle();
                String notification_body = remoteMessage.getNotification().getBody();
                int item_id = Integer.parseInt(remoteMessageData.get("item_id"));
                String message = remoteMessageData.get("message");
                String user_info = remoteMessageData.get("user_info");

                JSONObject obj = new JSONObject(user_info);
                String photoID = obj.getString("photo");
                String url ="";
                if (photoID != null && photoID.compareTo("null") != 0)
                {
                    url = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + photoID;
                }

                String name = obj.getString("first_name") + " " + obj.getString("last_name");
                intent.putExtra(AppUtils.EXTRA.chatting_user_id, obj.getInt("id"));
                intent.putExtra(AppUtils.EXTRA.chatting_user_image, url);
                intent.putExtra(AppUtils.EXTRA.chatting_user_name, name);
                intent.putExtra(AppUtils.EXTRA.chatting_item_id, item_id);
                intent.putExtra(AppUtils.EXTRA.chatting_activity_flag, "1");
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                sendNotification(pendingIntent, notification_title, message);

                Intent new_msg_arrived = new Intent(AppUtils.BROADCAST.BR_NEW_MESSAGE_ARRIVED);
                new_msg_arrived.putExtra("user_id" , obj.getInt("id"));
                new_msg_arrived.putExtra("message" , message);
                new_msg_arrived.putExtra("item_id" , item_id);
                sendBroadcast(new_msg_arrived);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        //Calling method to generate notification
//        sendNotification(remoteMessage.getNotification().getBody());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(PendingIntent pendingIntent, String title, String message) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_cir_red)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        countForNotificationsInGroup++;
        notificationManager.notify(countForNotificationsInGroup, notificationBuilder.build());
    }
}