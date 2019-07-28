package com.example.android.quakereport;

public class Earthquake {
    private double mMagnitude;
    private String mLocation;
    private long mTimeInMillis;
    private String mUrl;

    public Earthquake(double magnitude,String location,long timeinMillis,String url){
        mMagnitude=magnitude;
        mLocation=location;
        mTimeInMillis=timeinMillis;
        mUrl=url;
    }

    public String getUrl(){
        return mUrl;
    }

    public double getMagnitude(){
        return mMagnitude;
    }

    public String getLocation(){
        return mLocation;
    }

    public long getTimeInMillis(){
        return mTimeInMillis;
    }
}
