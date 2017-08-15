package com.projetos.marcelo.portaria;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.projetos.marcelo.portaria.model.Parametro;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by msmariano on 15/08/2017.
 */

public class Localizacao  implements LocationListener {

    Location location = null;

    public boolean inicializar(Context context){
        try {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    return true;
                }
            }
        }
        catch (Exception e){

        }
        return false;
    }

    public Address  getCoordenadasByEndereco(String endereco,Context context){

        Geocoder geocoder;
        Address address = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            address = geocoder.getFromLocationName(endereco, 1).get(0);
        }
        catch (Exception e){

        }
        return  address;
    }


    public String getLocalizacaoAtual(Context context){

        try {
            if (location != null) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String rua = "";
                    String numero = "";
                    if (addresses.get(0).getThoroughfare() != null)
                        rua = addresses.get(0).getThoroughfare();
                    if (addresses.get(0).getSubThoroughfare() != null)
                        numero = addresses.get(0).getSubThoroughfare();
                    return rua + " " + numero;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
        }
        return "";
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
