package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader <List<Earthquake>>{

    private String murl;
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    public EarthquakeLoader(Context context, String url) {
        super(context);
        murl=url;
    }

    protected void OnStartLoading(){
        Log.i(LOG_TAG,"Test : Inside onStartLoading()");
        forceLoad();    //triggers loadIn Background()
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if(murl==null)
            return null;



        List<Earthquake> result=QueryUtils.fetchData(murl);
        Log.i(LOG_TAG,"Test: loadinBackground()");
        return result;

    }
}
