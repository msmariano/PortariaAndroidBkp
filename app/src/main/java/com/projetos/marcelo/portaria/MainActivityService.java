package com.projetos.marcelo.portaria;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Marcelo on 31/05/2016.
 */

public class MainActivityService extends Service {
    private static final String TAG = "HelloService";
    private static final String ACTION_VIEW ="Atender";
    NotificationManager manager;
    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }
    public class Atender
    {
        Atender()
        {
            Log.i(TAG, "teste");
            Context context = getApplicationContext();
            final Toast ola = Toast.makeText(context,"Ola", Toast.LENGTH_SHORT);
        }


    }




    private void showNotification() {


        Notification myNotication;

        Bundle yesBundle = new Bundle();

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent("com.example.marcelo.controleremoto");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);



        Notification.Builder builder = new Notification.Builder(this);
        //builder.addAction(0, "Atender", pendingIntentAtender);


        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("Portaria");
        builder.setContentText("Tocando campainha.Por favor atenda!");

        yesBundle.putInt("userAnswer", 1);//This is the value I want to pass
        intent.putExtras(yesBundle);
        intent.setAction("call_method");
        PendingIntent pendingIntentAtender = PendingIntent.getBroadcast(this, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(0, "Atender", pendingIntentAtender);

        builder.setSmallIcon(R.drawable.chat1);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("Atenção...");   //API level 16
        builder.setNumber(100);
        builder.setVibrate(new long[]{100, 250, 100, 500});
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));




        BroadcastReceiver call_method = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action_name = intent.getAction();
                if (action_name.equals("call_method")) {
                    // call your method here and do what ever you want.
                    Log.i(TAG, "testedddd");
                }
            };
        };
        registerReceiver(call_method, new IntentFilter("call_method"));







        builder.build();


        myNotication = builder.getNotification();
        manager.notify(11, myNotication);







    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                /*for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        Log.i(TAG, "Service running");
                    }
                }*/
                while(isRunning) {
                    try {
                        Thread.sleep(10000);
                        showNotification();
                    } catch (Exception e) {
                    }
                    manager.cancel(11);
                }


                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }
}

