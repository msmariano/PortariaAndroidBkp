package com.projetos.marcelo.portaria;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

//teste github
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_READ_PHONE_STATE = 1;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 2;
    ImageButton IbOnOff;
    ImageButton IbCamera;
    ImageButton IbSalvarLoc;

    ImageButton ibAviso;
    Context context;
    private boolean connected = false;
    int milliseconds;
    public String IMEI;
    public String msg;
    public Location org;
    boolean bGpsFirst;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        bGpsFirst = false;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }







        /*if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }*/

        //Toast.makeText(context, IMEI, Toast.LENGTH_SHORT).show();
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mydatabase = openOrCreateDatabase("portaria.db", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS TutorialsPoint(Username VARCHAR,Password VARCHAR);");
        mydatabase.execSQL("DROP TABLE Parametros;");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Parametros(longitude NUMERIC,latitude NUMERIC);");
        mydatabase.execSQL("INSERT INTO Parametros VALUES(1234,5678);");




        Cursor c = mydatabase.rawQuery("SELECT * FROM Parametros", null);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView2 = (TextView) findViewById(R.id.textView2);
        c.moveToFirst();
        if (c.getCount() == 0) {
            textView3.setText("No records found");

        } else {
            textView2.setText(c.getString(0));
            textView3.setText(c.getString(1));
        }


        //


        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);


        textView4.setText("teste.....");

        textView.setText(IMEI);


        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_PLAY);
        startService(intent);
        IbOnOff = (ImageButton) findViewById(R.id.btOnOff);
        IbCamera = (ImageButton) findViewById(R.id.btCamera);
        IbSalvarLoc = (ImageButton) findViewById(R.id.IbSalvarLoc);

        ibAviso = (ImageButton) findViewById(R.id.ibaviso);

        IbSalvarLoc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)

            {
                mydatabase.execSQL("UPDATE Parametros SET longitude = " + dLatitude + " , latitude =  " + dLongitude);
            }
        });


        ibAviso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)

            {
                String mensmove = "";
                if (mensagem.equals("move040370")) {
                    mensagem = "nomove040370";
                    mensmove = "NoMove";
                }
                else{
                    mensagem = "move040370";
                    mensmove = "Move";
                }

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("041999696921", null, mensagem, null, null);
                Toast.makeText(getApplicationContext(), mensmove,
                        Toast.LENGTH_LONG).show();


            }
        });

        IbOnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)

            {
                boolean inv = false;
                CharSequence text = "Bemvindo!";
                int duration = Toast.LENGTH_SHORT;
                //imageButton.setBackgroundColor(Color.parseColor("#00FF00"));
                IbOnOff.setEnabled(false);
                if (!connected) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }


                /*new CountDownTimer(3000, 1000) {
                    public void onFinish() {
                        // When timer is finished
                        // Execute your code here
                        imageButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();*/

                delay(3);


            }
        });
        locationListenerGps = new LocationListener() {
            public void onLocationChanged(Location location) {
                float[] dist = new float[1];
                dist[0] = 0;
                if (!bGpsFirst) {
                    bGpsFirst = true;
                    org = new Location("Org");
                    dLatitude = location.getLatitude();
                    dLongitude = location.getLongitude();
                    org.setLongitude(dLongitude);
                    org.setLatitude(dLatitude);

                    textView4.setText("Latitude Org: " + dLatitude);
                    textView5.setText("Longitude Org:" + dLongitude);

                }
                Location.distanceBetween(dLatitude, dLongitude, location.getLatitude(), location.getLongitude(), dist);


                textView.setText("Distancia Origem:" + String.valueOf(org.distanceTo(location)));
                textView2.setText("Latitude: " + Location.convert(location.getLatitude(), Location.PARCELABLE_WRITE_RETURN_VALUE));
                textView3.setText("Longitude:" + Location.convert(location.getLongitude(), Location.PARCELABLE_WRITE_RETURN_VALUE));


                //timer1.cancel();
                //locationResult.gotLocation(location);
                //lm.removeUpdates(this);
                //lm.removeUpdates(locationListenerNetwork);
            }

            public void onProviderDisabled(String provider) {
                Toast toast = Toast.makeText(context, "GPS Desativado!", Toast.LENGTH_SHORT);
                toast.show();

            }

            public void onProviderEnabled(String provider) {
                Toast toast = Toast.makeText(context, "GPS Ativado!", Toast.LENGTH_SHORT);
                toast.show();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListenerGps);


        } catch (Exception e) {
            showMessage(e.getMessage());
        }


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
                        //imageButton.setBackgroundColor(Color.parseColor("#FF0000"));
                        if (msg.length() > 0) {

                            Toast toast = Toast.makeText(context, msg + " " + IMEI, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        msg = "";

                        IbOnOff.setEnabled(true);
                    }
                }, milliseconds);
            }
        });
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName("192.168.0.14");
                Socket socket = new Socket(serverAddr, 81);
                connected = true;
                boolean bEnviado = false;
                //Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                if (connected) {
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
                            Log.e("NETWORK-RECEIVE", "Something goes wrong: IOException", e);
                        }

                    } catch (Exception e) {
                        Toast toast = Toast.makeText(context, e.getMessage(), duration);
                        toast.show();
                    }

                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
                connected = false;
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
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
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_READ_PHONE_STATE);
                    }
                }
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            case MY_PERMISSIONS_READ_PHONE_STATE: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    }
                }
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    if (telephony != null) {
                        IMEI = telephony.getDeviceId();
                        Toast.makeText(context, IMEI, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Falhou ao obter IMEI.", Toast.LENGTH_LONG).show();
                    }
                    return;
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Falhou ao obter permissão para ler estado do celular.", Toast.LENGTH_LONG).show();
                    return;
                }

            }
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListenerGps);

                    Toast toast = Toast.makeText(context, "GPS Requesitado!", Toast.LENGTH_SHORT);
                    toast.show();


                }
                return;
            }

        }
    }


}
