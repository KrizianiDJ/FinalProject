package com.example.krizianidj.capstone1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;

public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG="MYListenerServive";

    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        int id=0;
        String title=" ",body=" ",account,balance,sid;
        if(message.getNotification()!=null)
        {
            title=message.getNotification().getTitle();
            body=message.getNotification().getBody();

        }

        if (message.getData()!=null)
        {
            sid=message.getData().get("id");
            account=message.getData().get("account");
            balance=message.getData().get("balance");
            id=Integer.valueOf(sid);
        }

       // ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 200);
       // toneGen1.startTone(ToneGenerator.TONE_DTMF_8, 100);

        this.sendNotification(new NotificationData(id,title,body));



    }

    private void sendNotification(NotificationData notificationData)
    {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(NotificationData.TEXT, notificationData.getTextMessage());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder = null;
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(notificationData.getTitle())
                    .setContentText(notificationData.getTextMessage())
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.refrigerator)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis());



        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "Não foi possível criar objeto notificationBuilder");
        }
    }



}
