package com.projetos.marcelo.portaria;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MediaPlayerService extends Service {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";


    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    private boolean connected = false;
    private boolean connectedOnline = false;
    String msg ="";
    Socket socket;
    PrintWriter out;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            mController.getTransportControls().play();
        }
        else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            mController.getTransportControls().pause();
        }
        else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            mController.getTransportControls().stop();
        }
    }




    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.drawable.ic_launcher2)
                .setColor(0)
                .setContentTitle( "ControleRemoto" )
                .setContentText( "" )
                .setDeleteIntent(pendingIntent)
                .setStyle(style);
        builder.setTicker("this is ticker text");


        Thread cThreadOnline = new Thread(new ClientThreadOnline());
        cThreadOnline.start();

        /*builder.setVibrate(new long[]{100, 250, 100, 500});
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));*/
        builder.addAction(action);
        style.setShowActionsInCompactView(0);


        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify(1, builder.build());
    }
    public class ClientThreadOnline implements Runnable {
        private String line;

        public void run()
        {
              while(true){
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                    }
                    //showNotification();


                   Log.i("SERVICE", "Service running");

                }

        }
    }

    private void showNotification() {


        Notification myNotication;

        Bundle yesBundle = new Bundle();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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


        builder.build();


        myNotication = builder.getNotification();
        manager.notify(11, myNotication);
    }

    public class ClientThread implements Runnable
    {

        public void run()
        {
            try
            {
                InetAddress serverAddr = InetAddress.getByName("192.168.0.14");
                Socket socket = new Socket(serverAddr,81);
                connected = true;
                boolean bEnviado = false;
                //Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                if (connected)
                {
                    try {
                        PrintWriter out = new PrintWriter(
                                new BufferedWriter(new OutputStreamWriter(
                                        socket.getOutputStream())), true);
                        out.println("act");

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));

                        try {
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                total.append(line);
                            }
                            msg = total.toString().trim();
                            //showToastInThread(MainActivity.this,msg);
                            //Log.d("ClientActivity", msg);


                        } catch (IOException e) {
                            //Toast toast = Toast.makeText(context, e.getMessage(), duration);
                            //toast.show();
                            //e.printStackTrace();
                            Log.e("NETWORK-RECEIVE", "Something goes wrong: IOException",e);
                        }

                    }
                    catch (Exception e)
                    {

                    }

                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
                connected = false;
            }
            catch (Exception e)
            {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( mManager == null ) {
            initMediaSessions();
        }
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSessions() {
        mMediaPlayer = new MediaPlayer();

        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController =new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback() {


                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     Log.e("MediaPlayerService", "onPause");
                                     //buildNotification(generateAction(android.R.drawable.ic_media_play, "Pause", ACTION_PAUSE));


                                         Thread cThread = new Thread(new ClientThread());
                                         cThread.start();




                                 }
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     Log.e("MediaPlayerService", "onPlay");

                                     buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PAUSE));






                                    /* new Thread(new Runnable() {
                                         @Override
                                         public void run() {

                                             //Your logic that service will perform will be placed here
                                             //In this example we are just looping and waits for 1000 milliseconds in each loop.
                                             while(true){
                                                 try {
                                                     Thread.sleep(1000);
                                                 } catch (Exception e) {
                                                 }
                                                 Log.e("MediaPlayerService", "Executando");
                                             }
                                         }
                                     }).start();*/


                                 }

                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     Log.e("MediaPlayerService", "onStop");
                                     //Stop media player here
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel(1);
                                     Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                                     stopService(intent);
                                 }
                             }
        );
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onUnbind(Intent intent) {
        mSession.release();
        return super.onUnbind(intent);
    }
}