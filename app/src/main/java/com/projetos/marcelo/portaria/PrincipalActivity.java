package com.projetos.marcelo.portaria;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.projetos.marcelo.portaria.model.Parametro;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText edEnderecoAtual;
    EditText edIpArduino;
    EditText edEnderecoLocal;
    EditText edPortaArduino;
    EditText edImei;
    EditText edUsuario;
    EditText edLatitude;
    EditText edLongitude;
    EditText edDistancia;
    Button btAtualizar;
    CheckBox cbAcionar;
    Localizacao local = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Portaria ");
        edEnderecoAtual = (EditText) findViewById(R.id.edEnderecoAtual);
        edEnderecoLocal = (EditText) findViewById(R.id.edEnderecoLocal);
        edPortaArduino  = (EditText) findViewById(R.id.edPortaArduino);
        edUsuario = (EditText) findViewById(R.id.edUsuario);
        edLatitude = (EditText) findViewById(R.id.edLatitude);
        edLongitude = (EditText) findViewById(R.id.edLongitude);
        edDistancia = (EditText)findViewById(R.id.edDistancia);
        cbAcionar = (CheckBox) findViewById(R.id.cbAcionar);
        edImei = (EditText)findViewById(R.id.edIMEI);

        edIpArduino = (EditText) findViewById(R.id.edIpArduino);
        btAtualizar = (Button)  findViewById(R.id.btAtualizar);

        local = new Localizacao();
        local.inicializar(this);
        edEnderecoAtual.setText(local.getLocalizacaoAtual(this));
        edEnderecoAtual.setEnabled(false);

        ParametroDAO parametroDAO = new ParametroDAO();
        Parametro parametro = parametroDAO.buscarParamento("conexao_ip_arduino");
        //edIpArduino.setError("IP do Arduino");
        if(parametro != null) {
            edIpArduino.setText(parametro.getCampo1());
            edPortaArduino.setText(parametro.getCampo2());
        }

        parametro = parametroDAO.buscarParamento("endereco_local");
        //edIpArduino.setError("IP do Arduino");
        if(parametro != null) {
            edEnderecoLocal.setText(parametro.getCampo1());
            Address address = local.getCoordenadasByEndereco(edEnderecoLocal.getText().toString(),getApplicationContext());
            edLatitude.setText( String.valueOf(address.getLatitude()));
            edLongitude.setText(String.valueOf(address.getLongitude()));
            edLatitude.setEnabled(false);
            edLongitude.setEnabled(false);
        }

        parametro = parametroDAO.buscarParamento("usuario_nome");
        if(parametro != null)
            edUsuario.setText(parametro.getCampo1());

        parametro = parametroDAO.buscarParamento("acionar_disparo");
        if(parametro != null)
            cbAcionar.setChecked(Boolean.parseBoolean(parametro.getCampo1()));


        parametro = parametroDAO.buscarParamento("distancia_disparo");
        if(parametro != null)
            edDistancia.setText(parametro.getCampo1());


        try {
            TelephonyManager telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephony != null) {
                edImei.setText(telephony.getDeviceId());
                edImei.setEnabled(false);
            }
        }
        catch (Exception e){

        }



        btAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ParametroDAO parametroDAO = new ParametroDAO();
                    parametroDAO.setChave("conexao_ip_arduino",edIpArduino.getText().toString(),edPortaArduino.getText().toString());
                    parametroDAO.setChave("usuario_nome",edUsuario.getText().toString(),"");
                    parametroDAO.setChave("endereco_local",edEnderecoLocal.getText().toString(),"");


                    parametroDAO.setChave("distancia_disparo",edDistancia.getText().toString(),"");

                    parametroDAO.setChave("acionar_disparo", String.valueOf(cbAcionar.isChecked())  ,"");

                    Address address = local.getCoordenadasByEndereco(edEnderecoLocal.getText().toString(),getApplicationContext());
                    parametroDAO.setChave("endereco_local_teste", String.valueOf(address.getLatitude())  ,String.valueOf(address.getLongitude()));
                    edLatitude.setText( String.valueOf(address.getLatitude()));
                    edLongitude.setText(String.valueOf(address.getLongitude()));
                    edLatitude.setEnabled(false);
                    edLongitude.setEnabled(false);


                    edIpArduino.setTextColor(Color.BLUE);
                    edPortaArduino.setTextColor(Color.BLUE);
                    edUsuario.setTextColor(Color.BLUE);
                    edEnderecoLocal.setTextColor(Color.BLUE);
                    edLatitude.setTextColor(Color.BLUE);
                    edLongitude.setTextColor(Color.BLUE);
                    edDistancia.setTextColor(Color.BLUE);

                }
                catch(Exception e){
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent irParaTrace = new Intent(PrincipalActivity.this, TraceActivity.class);
            startActivity(irParaTrace);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
