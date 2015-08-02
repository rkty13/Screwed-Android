package com.ohshootimscrewed.screwed;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by eric on 8/2/15.
 */
public class MyLocationListener implements LocationListener{

    Context c;

    public MyLocationListener(Context context){
        c = context;
    }

    @Override
    public void onLocationChanged(Location loc) {
        loc.getLatitude();
        loc.getLongitude();

        String text = "My current location is:  Latitude = " + loc.getLatitude() + "Longitude = " + loc.getLongitude();

        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
        Log.d("asdf", text);
    }

    @Override
    public void onProviderDisabled(String provider){
        Toast.makeText(c, "GPS Disabled", Toast.LENGTH_SHORT).show();
        Log.d("asdf", "GPS Disabled");
    }

    @Override
    public  void onProviderEnabled(String provider){
        Toast.makeText(c, "GPS Enabled", Toast.LENGTH_SHORT).show();
        Log.d("asdf", "GPS Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.d("asdf", "StatusChanged");
    }

 }
