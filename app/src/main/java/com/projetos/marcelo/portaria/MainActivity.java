package com.projetos.marcelo.portaria;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.projetos.marcelo.portaria.model.Parametro;
import com.projetos.marcelo.portaria.model.Trace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
	private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
	private static final int MY_PERMISSIONS_READ_PHONE_STATE = 1;
	private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 2;
	Intent intent;
	ImageButton IbOnOff;
	ImageButton IbCamera;
	ImageButton IbSalvarLoc;
	ImageButton ibAviso;
	ImageButton config;
	Context context;
	private boolean connected = false;
	int milliseconds;
	public String IMEI;
	public String msg;
	public Location org;
	private LocationManager locationManager;
	public double dLatitude, dLongitude;
	LocationListener locationListenerGps;
	TextView textView;
	TextView textView2;
	TextView textView3;
	TextView textView4;
	TextView textView5;
	SQLiteDatabase mydatabase;
	TelephonyManager telephony;
	String mensagem = "";
	MainActivity thisLocal = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();
		SugarContext.init( this );

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS },
						MY_PERMISSIONS_REQUEST_SEND_SMS);
			}
		}

		//teste criar base
		try {
			SugarRecord.deleteAll(Trace.class);
			/*SugarRecord.deleteAll(Parametro.class);
			SugarRecord.deleteAll(Trace.class);
			ParametroDAO parametroDAO = new ParametroDAO();
			parametroDAO.setChave("conexao_ip_arduino","192.168.0.14","81");
			parametroDAO.setChave("ssid_local","Escritorio","");
			parametroDAO.setChave("endereco_local","Rua Cyro Vellozo 56","Prado Velho");
			parametroDAO.setChave("acionar_disparo","false","");
			parametroDAO.setChave("distancia_disparo","150","");*/


		}
		catch(Exception e){
			Toast.makeText(this, "Erro:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}


		intent = new Intent(getApplicationContext(), MediaPlayerService.class);
		intent.setAction(MediaPlayerService.ACTION_PLAY);
		startService(intent);
		thisLocal = this;

		textView =  (TextView) findViewById(R.id.textView);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);
		textView5 = (TextView) findViewById(R.id.textView5);
		textView.setText("");
		textView2.setText("");
		textView3.setText("");
		textView4.setText("");
		textView5.setText("");

		IbOnOff =     (ImageButton) findViewById(R.id.btOnOff);
		IbCamera =    (ImageButton) findViewById(R.id.btCamera);
		IbSalvarLoc = (ImageButton) findViewById(R.id.IbSalvarLoc);
		ibAviso =     (ImageButton) findViewById(R.id.ibaviso);
		config =	  (ImageButton) findViewById(R.id.config);
		IbSalvarLoc.setOnClickListener(this);
		ibAviso.setOnClickListener(this);
		IbOnOff.setOnClickListener(this);
		config.setOnClickListener(this);
	}

	public void delay(int seconds) {
		milliseconds = seconds * 1000;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// imageButton.setBackgroundColor(Color.parseColor("#FF0000"));
						if (msg.length() > 0) {

						}
						msg = "";

						//IbOnOff.setEnabled(true);
					}
				}, milliseconds);
			}
		});
	}



	public class ClientThread implements Runnable {

		public void run() {
			try {
				connected = true;

				InetAddress serverAddr = InetAddress.getByName("192.168.0.14");
				Socket socket = new Socket(serverAddr, 81);

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
							showMessage(msg);
						} catch (IOException e) {
							showMessage("ClientThread[IOException] : "+e.getMessage());
						}

					} catch (Exception e) {
						showMessage("ClientThread : "+e.getMessage());
					}
				}
				socket.close();				
				connected = false;
			} catch (Exception e) {
				showMessage("ClientThread : "+e.getMessage());
				connected = false;
			}
		}
	}

	public void showMessage(String mens) {
		Toast toast = Toast.makeText(context, mens, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
		case MY_PERMISSIONS_REQUEST_SEND_SMS: {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
				} else {
					ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_PHONE_STATE },
							MY_PERMISSIONS_READ_PHONE_STATE);
				}
			}
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				return;

			} else {
				Toast.makeText(getApplicationContext(), "Falhou ao obter permissão para enviar SMS.", Toast.LENGTH_LONG)
						.show();
				return;
			}
		}
		case MY_PERMISSIONS_READ_PHONE_STATE: {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(this,
						Manifest.permission.ACCESS_FINE_LOCATION)) {
				} else {
					ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
							MY_PERMISSIONS_ACCESS_FINE_LOCATION);
				}
			}
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
				if (telephony != null) {
					IMEI = telephony.getDeviceId();
					Toast.makeText(context, IMEI, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Falhou ao obter IMEI.", Toast.LENGTH_LONG).show();
				}
				return;
			} else {
				Toast.makeText(getApplicationContext(), "Falhou ao obter permissão para ler estado do celular.",
						Toast.LENGTH_LONG).show();
				return;
			}

		}
		case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			}
			return;
		}

		}
	}



	@Override
	public void onClick(View v) {

		if (v.equals(ibAviso)){

			String mensmove = "";
			if (mensagem.equals("move040370")) {
				mensagem = "nomove040370";
				mensmove = "NoMove";
			} else {
				mensagem = "move040370";
				mensmove = "Move";
			}
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("041999696921", null, mensagem, null, null);
			showMessage(mensmove);

		}
		else if (v.equals(IbOnOff)){

            Intent intentTelaPrincipal = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(intentTelaPrincipal);
			/*try {

				boolean inv = false;
				CharSequence text = "Bemvindo!";
				int duration = Toast.LENGTH_SHORT;
				// imageButton.setBackgroundColor(Color.parseColor("#00FF00"));
				//IbOnOff.setEnabled(false);
				if (!connected) {
					Thread cThread = new Thread(new ClientThread());
					cThread.start();
				} else
					showMessage("Aguarde...Ainda executando!");
				//delay(3);
			}
			catch (Exception e){
				showMessage(e.getMessage());
			}*/

		}
		else if(v.equals(IbSalvarLoc)){
			finish();
		}
		else if(v.equals(config)){
			try {



				ParametroDAO parametroDAO = new ParametroDAO();
				List<Parametro> parametros = parametroDAO.listar();
				if(parametros != null){
					String mens = "";
					for(Parametro parametro : parametros){
						mens = mens+ parametro.getParametro()+" : "+parametro.getCampo1()+" "+parametro.getCampo2()+"\n";
					}
					showMessage(mens);

					SugarRecord.deleteAll(Parametro.class);
					SugarRecord.deleteAll(Trace.class);
				}
			}
			catch (Exception e){
				showMessage(e.getMessage());
			}
		}
	}

}
