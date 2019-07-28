package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public final class QueryUtils {

    private QueryUtils() {
    }


    public static List<Earthquake>fetchData(String stringUrl){
        URL url=createUrl(stringUrl);
        String jsonResponse="";
        try {
            jsonResponse=makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"IO Exception occured",e);
        }
        List<Earthquake> earthquakes=extractEarthquakes(jsonResponse);
        Log.i(LOG_TAG,"Test fetchData()");

        return earthquakes;

    }


    private static List<Earthquake> extractEarthquakes(String earthquakeJSON) {
        if (earthquakeJSON==null)
            return null;
        List<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject baseJSONResponse = new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJSONResponse.getJSONArray("features");

            for (int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties_object = currentEarthquake.getJSONObject("properties");
                double magnitude = properties_object.getDouble("mag");
                String location = properties_object.getString("place");
                String url = properties_object.getString("url");
                long time = properties_object.getLong("time");
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                earthquakes.add(earthquake);

            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl); //Since we have to return the url write it outside the try block
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed Url Exception Occured", e);
        }
        return url;

    }

    private static String makeHTTPRequest(URL url) throws IOException {
        InputStream inputStream=null;
        HttpURLConnection urlConnection=null;
        String jsonResponse="";
        if(url==null)
            return jsonResponse;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Status Code: " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"IOException Occured",e);
        }
        finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
            if(inputStream!=null)
                inputStream.close();
        }
        return jsonResponse;
    }

    private static String readFromStream (InputStream inputStream) throws IOException {
        StringBuilder output=new StringBuilder();
        if(inputStream!=null){
        InputStreamReader input =new InputStreamReader(inputStream);
        BufferedReader br=new BufferedReader(input);
        String line=br.readLine();
        while(line!=null){
            output.append(line);
            line=br.readLine();

        }}
        return output.toString();
    }


}


