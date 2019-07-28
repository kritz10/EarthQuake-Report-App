/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LauncherActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <List<Earthquake>>{

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private EarthquakeAdapter adapter; // it cant be final since it has to be used to update the views

    private static final int LOADER_ID =1;//so as to make sure it doesnt get modified
    private TextView mEmptyStateTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"Test : Inside onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);



        /*EarthquakeAsyncTask object=new EarthquakeAsyncTask();
        object.execute(USGS_REQUEST_URL);
        */

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView=(TextView) findViewById(R.id.empty_list);
        earthquakeListView.setEmptyView(mEmptyStateTextView);



        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentEarthquake = adapter.getItem(i);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(intent);
            }
        });

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected==true) {
            LoaderManager loaderManager = getLoaderManager();
            Log.i(LOG_TAG, "Test :Inside initLoader()");


            loaderManager.initLoader(LOADER_ID, null, this).forceLoad();
        }
        else{
            View loading=(View)findViewById(R.id.loading);
            loading.setVisibility(View.GONE);

            earthquakeListView.setEmptyView(mEmptyStateTextView);
            mEmptyStateTextView.setText(R.string.internet);
        }


    }

    @Override
    public Loader <List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude=sharedPrefs.getString(getString(R.string.settings_min_magnitude_key),getString(R.string.settings_min_magnitude_default));

        String orderBy =sharedPrefs.getString(getString(R.string.settings_order_by_key),getString(R.string.settings_order_by_default));
        Uri uri=Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder=uri.buildUpon();
        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("limit","10");
        uriBuilder.appendQueryParameter("minmag",minMagnitude);
        uriBuilder.appendQueryParameter("orderby",orderBy);

        Log.i(LOG_TAG,"Test:Inside onCreateLoader() "+uriBuilder.toString());
        return new EarthquakeLoader(this,uriBuilder.toString());

    }


    public void onLoadFinished(Loader <List<Earthquake>> loader, List<Earthquake> earthquake) {
        View loading=(View)findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        adapter.clear();
        //This will trigger the ListView to update.
        if (earthquake != null && !earthquake.isEmpty()) {
            Log.i(LOG_TAG,"Test: Inside OnLoadFinished()");
            adapter.addAll(earthquake);
        }

            mEmptyStateTextView.setText(R.string.no_earthquakes);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i(LOG_TAG,"Test:Inside onLoaderReset()");
        adapter.clear();
    }

    /*private class EarthquakeAsyncTask extends AsyncTask<String,Void, List<Earthquake>>{
        protected List<Earthquake> doInBackground(String...url){
            if(url.length<1||url[0]==null)
                return null;

            List <Earthquake>result=QueryUtils.fetchData(url[0]);
            return result;


        }
        protected void onPostExecute(List<Earthquake> data){
            //Clear the adapter of previous earthquake data
            adapter.clear();
            //This will trigger the ListView to update.
            if(data!=null &&!data.isEmpty()){
                adapter.addAll(data);
            }
        }*/

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.action_settings){
            Intent i=new Intent(this,SettingActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
