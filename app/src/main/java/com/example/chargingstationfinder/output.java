package com.example.chargingstationfinder;

import android.widget.Toast;

import androidx.annotation.NonNull;

public class output {

    /* these are place holders for output*/
    private final String LocationName;
    private final double longitude;
    private final double lattitude;
    private final long phoneN;

    public output(String name, double longi, double latt, long pn){
        this.LocationName = name;
        this.longitude = longi;
        this.lattitude = latt;
        this.phoneN = pn;
    }
    public String check(int longi, int latt){
        if(lattitude >= latt){
            if(longitude >= longi){
                toString();
            }
        }
        else if(lattitude <= latt){
            if(longitude <= longi){
                toString();
            }
        }
        else{

        }
        return null;
    }

    public String getLocationName(){return LocationName;}
    public double getLongitude(){return longitude;}
    public double getLattitude(){return lattitude;}
    public long getPhoneN(){return phoneN;}

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "name='" + LocationName + '\'' +
                ", Longitude=" + longitude +
                ", latitude=" + lattitude +
                ", phone Number=" + phoneN +
                '}';
    }
}
