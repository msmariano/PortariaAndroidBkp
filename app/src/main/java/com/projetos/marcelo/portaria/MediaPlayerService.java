package com.projetos.marcelo.portaria;

import android.*;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

public class MediaPlayerService extends Service implements LocationListener {
	boolean isAct = false;
	StringBuilder sb = new StringBuilder();
	public static final String ACTION_PLAY = "action_play";
	public static final String ACTION_PAUSE = "action_pause";
	public static final String ACTION_STOP = "action_stop";
	private LocationManager locationManager;
	private MediaPlayer mMediaPlayer;
	private MediaSessionManager mManager;
	private MediaSession mSession;
	private MediaController mController;
	private boolean connected = false;
	private boolean connectedOnline = false;
	NotificationManager manager;
	private Notification.Builder builder;
	private NotificationManager notificationManager;
	LocationListener locationListenerGps;
	String msg = "";
	Socket socket;
	PrintWriter out;
	Thread cThreadOnline;
	public double dLatitude, dLongitude;
	public Location org;
	MediaPlayerService localObj;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void handleIntent(Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;

		String action = intent.getAction();

		if (action.equalsIgnoreCase(ACTION_PLAY)) {
			mController.getTransportControls().play();
		} else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
			mController.getTransportControls().pause();
		} else if (action.equalsIgnoreCase(ACTION_STOP)) {
			mController.getTransportControls().stop();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			String rua = "";
			String numero = "";
			if (addresses.get(0).getThoroughfare() != null)
				rua = addresses.get(0).getThoroughfare();
			if (addresses.get(0).getSubThoroughfare() != null)
				numero = addresses.get(0).getSubThoroughfare();

			if (rua.trim().toLowerCase().equals("rua cyro vellozo") && numero.trim().toLowerCase().equals("56")) {
				updateContext("Em casa[" + rua + " " + numero + "]");

			} else {
				updateContext("Fora de Casa[" + rua + " " + numero + "]");
			}

			try {
				float[] dist = new float[1];
				dist[0] = 0;
				Location.distanceBetween(dLatitude, dLongitude, location.getLatitude(), location.getLongitude(), dist);
				if (dist[0] > 100) {
					builder.setColor(10);
					builder.setContentTitle("Controle Remoto Ativado");
					builder.setSubText("Distancia.:" + String.format("%.0f", dist[0]));
					isAct = true;
					showNotification("Fora de Casa[" + rua + " " + numero + "]","Distancia.:" + String.format("%.0f", dist[0]));
				} else {
					builder.setContentTitle("Controle Remoto Desativado");
					builder.setSubText("Distancia.:" + String.format("%.0f", dist[0]));
					if (isAct) {
						isAct = false;
						cThreadOnline = new Thread(new ClientThreadOnline());
						cThreadOnline.start();
					}

				}
				notificationManager.notify(1, builder.build());
			} catch (Exception e) {

			}

		} catch (IOException e) {
			updateContext("Erro ao localizar : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {

	}

	@Override
	public void onProviderEnabled(String s) {

	}

	@Override
	public void onProviderDisabled(String s) {

	}

	@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
	private Notification.Action generateAction(int icon, String title, String intentAction) {
		Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
		intent.setAction(intentAction);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
		return new Notification.Action.Builder(icon, title, pendingIntent).build();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void buildNotification(Notification.Action action) {
		Notification.MediaStyle style = new Notification.MediaStyle();
		Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
		intent.setAction(ACTION_STOP);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
		builder = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher2).setColor(0)
				.setContentTitle("Controle Remoto").setContentText("").setDeleteIntent(pendingIntent).setStyle(style);
		builder.setTicker("this is ticker text");
		try {
			if (ContextCompat.checkSelfPermission(getApplicationContext(),
					android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				updateContext("GPS Permitido");
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				updateContext("Iniciando GPS!");
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER/* GPS_PROVIDER */, 1000, 1,
						this);
				if (locationManager != null) {
					Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) {
						Geocoder geocoder;
						List<Address> addresses;
						geocoder = new Geocoder(this, Locale.getDefault());
						try {
							addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
							addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
							String rua = "";
							String numero = "";
							if (addresses.get(0).getThoroughfare() != null)
								rua = addresses.get(0).getThoroughfare();
							if (addresses.get(0).getSubThoroughfare() != null)
								numero = addresses.get(0).getSubThoroughfare();
							sb.append(rua + " " + numero);
							updateContext(rua + " " + numero);

							Address address = geocoder.getFromLocationName("rua cyro vellozo 56", 1).get(0);
							dLatitude = address.getLatitude();
							dLongitude = address.getLongitude();

							float[] dist = new float[1];
							dist[0] = 0;
							Location.distanceBetween(dLatitude, dLongitude, location.getLatitude(),
									location.getLongitude(), dist);
							updateContext("Distancia.: " + dist[0]);

						} catch (IOException e) {
							updateContext("Erro ao localizar : " + e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			updateContext(e.getMessage());
		}

		/*
		 * builder.setVibrate(new long[]{100, 250, 100, 500});
		 * builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.
		 * TYPE_NOTIFICATION));
		 */
		builder.addAction(action);
		style.setShowActionsInCompactView(0);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());
	}

	public void updateContext(String mens) {
		try {
			builder.setContentText(mens);
			notificationManager.notify(1, builder.build());
		} catch (Exception e) {

		}
	}

	public class ClientThreadOnline implements Runnable {

		public void run() {
			boolean isRun = true;
			while (!isAct&&isRun) {
				try {
					Thread.sleep(1000);

					try {
						ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
								.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo networkInfo = cm.getActiveNetworkInfo();

						if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
								&& networkInfo.isConnected()) {
							// Wifi is connected
							WifiManager wifiManager = (WifiManager) getApplicationContext()
									.getSystemService(Context.WIFI_SERVICE);
							WifiInfo wifiInfo = wifiManager.getConnectionInfo();
							String ssid = wifiInfo.getSSID();
							if(ssid.equals("Escritorio")){
								showNotification("Acionando portao","");
								Thread cThread = new Thread(new ClientThread());
								cThread.start();
								continue;
							}

						} else {

						}
					} catch (Exception e) {
						isAct = false;
					}

				} catch (Exception e) {
					isAct = false;
				}
			}
		}
	}

	public class ClientThread implements Runnable {

		public void run() {
			try {
				showNotification("Portão","Acionando");
				InetAddress serverAddr = InetAddress.getByName("192.168.0.14");
				Socket socket = new Socket(serverAddr, 81);
				connected = true;
				boolean bEnviado = false;
				int duration = Toast.LENGTH_SHORT;
				if (connected) {
					try {
						PrintWriter out = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
						out.println("act");

						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

						try {
							StringBuilder total = new StringBuilder();
							String line;
							while ((line = in.readLine()) != null) {
								total.append(line);
							}
							msg = total.toString().trim();
							updateContext(msg);
						} catch (IOException e) {
							updateContext("Erro envio comando portão : " + e.getMessage());
							showNotification("Comando do portao[1]",e.getMessage());
						}

					} catch (Exception e) {
						updateContext("Erro envio comando portão : " + e.getMessage());
						showNotification("Comando do portao[2]",e.getMessage());
					}

				}
				socket.close();
				showNotification("Portão","Acionamento finalizado!");
				connected = false;
			} catch (Exception e) {
				showNotification("Comando do portao","Sem conexao!!!");
				connected = false;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mManager == null) {
			initMediaSessions();
		}
		handleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void initMediaSessions() {
		mMediaPlayer = new MediaPlayer();

		mSession = new MediaSession(getApplicationContext(), "simple player session");
		mController = new MediaController(getApplicationContext(), mSession.getSessionToken());

		mSession.setCallback(new MediaSession.Callback() {

			@Override
			public void onPause() {
				super.onPause();
				Thread cThread = new Thread(new ClientThread());
				cThread.start();
			}

			@Override
			public void onPlay() {
				super.onPlay();
				buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PAUSE));
			}

			@Override
			public void onStop() {

			}
		});
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public boolean onUnbind(Intent intent) {
		mSession.release();
		return super.onUnbind(intent);
	}

	private void showNotification(String title,String content) {

		Notification.Builder mBuilder= new Notification.Builder(this)
				.setSmallIcon(R.drawable.aviso)
				.setContentTitle(title)
				.setAutoCancel(true)
				.setContentText(content)
				.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));
		NotificationManager notificationManager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(11, mBuilder.build());

	}
}