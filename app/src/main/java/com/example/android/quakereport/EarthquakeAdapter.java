package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private static final String SEPARATOR= "of";

    private int getMagnitudeColor(double mag){
        int magColor;
        int magFloor=(int)Math.floor(mag);
        switch(magFloor) {
            case 0:
            case 1:
                magColor = R.color.magnitude1;
                break;
            case 2:
                magColor = R.color.magnitude2;
                break;
            case 3:
                magColor = R.color.magnitude3;
                break;
            case 4:
                magColor = R.color.magnitude4;
                break;
            case 5:
                magColor = R.color.magnitude5;
                break;
            case 6:
                magColor = R.color.magnitude6;
                break;
            case 7:
                magColor = R.color.magnitude7;
                break;
            case 8:
                magColor = R.color.magnitude8;
                break;
            case 9:
                magColor = R.color.magnitude9;
                break;
            default:
                magColor = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(),magColor);

    }


    //overwrite the ArrayAdapter's constructor
    public EarthquakeAdapter(Context context, List<Earthquake>earthquakes){
        super(context,0,earthquakes);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView=convertView;
        //in case of recycled views
        if(listItemView==null){
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item,parent,false);
        }
        Earthquake currentEarthquake=getItem(position);

        //for creating new views
        TextView magnitudeView=(TextView) listItemView.findViewById(R.id.magnitude);
        DecimalFormat formatter=new DecimalFormat("0.0");
        String magnitude=formatter.format(currentEarthquake.getMagnitude());
        magnitudeView.setText(magnitude);

        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);




        String originalLocation=currentEarthquake.getLocation();
        String primaryLocation;
        String locationOffset;

        if(originalLocation.contains(SEPARATOR)){
            String[] parts=originalLocation.split(SEPARATOR);
            locationOffset=parts[0]+"of";
            primaryLocation=parts[1];
        }
        else{
            primaryLocation=originalLocation;
            locationOffset=getContext().getString(R.string.near_the);

        }
        TextView offsetView=(TextView) listItemView.findViewById(R.id.location_offset);
        offsetView.setText(locationOffset);

        TextView primaryLocationView=(TextView) listItemView.findViewById(R.id.location);
        primaryLocationView.setText(primaryLocation);

        Date dateObject=new Date(currentEarthquake.getTimeInMillis());
        TextView dateView=(TextView) listItemView.findViewById(R.id.date);
        String FormattedDate= formatDate(dateObject);
        dateView.setText(FormattedDate);

        TextView timeView=(TextView) listItemView.findViewById(R.id.time);
        String formattedTime=formatTime(dateObject);
        timeView.setText(formattedTime);

        return listItemView;
    }

}
