package com.iskandar.cvwg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GPSActivity extends AppCompatActivity implements Animation.AnimationListener, LocationListener {

    Context context;
    Animation entryLeft, entryTop, entryTopOffset1, entryTopOffset2, entryTopOffset3;

    ImageView icon1, icon2, icon3;
    TextView gpsTitle, txtGPSMsg;
    ImageButton btnBack;

    //variables for location
    LocationManager locationManager;
    List<Location> locationsList; // M
    BaseAdapter myAdapter; // C
    ListView lstView; // V

    //the code to recognize a specific permission request
    private final int LOCATION_REQUEST_CODE = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        setPointers();
        setAnimations();
        checkForPermission();
    }

    private void checkForPermission() {
        List<String> permissionList = new ArrayList<>(); //this will hold all the permissions we want to ask for

        //contextCompat.checkSelfPermission check if we have permmission for given component - 
        // will return PERMISSION_GRANTED or PERMISSION_DENIED

        //if the ACCESS_COARSE_LOCATION permission isn't granted then we add it to the list of permission to ask for
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        //if the ACCESS_FINE_LOCATION permission isn't granted then we add it to the list of permission to ask for
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), LOCATION_REQUEST_CODE);
        } else {
            setLocationManager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            //this checks the specific permission explicitly

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "cant get GPS data without permission!", Toast.LENGTH_SHORT).show();
                return;
            }
            setLocationManager();
        }

    }

    @SuppressLint("MissingPermission")
    private void setLocationManager() {
        //if the list is empty, then we already have all the permissions we need
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
        //we specify the provider(String), the minimum time (int - milliseconds),
        // minimum distance (int-meters), and the listener for the updated (LocationManager)
    }


    @Override
    protected void onStart() {
        super.onStart();
        startAnimations();
    }

    private void startAnimations() {

        btnBack.startAnimation(entryLeft);
        txtGPSMsg.startAnimation(entryLeft);
        gpsTitle.startAnimation(entryTop);
        icon1.startAnimation(entryTopOffset1);
        icon2.startAnimation(entryTopOffset2);
        icon3.startAnimation(entryTopOffset3);
    }

    private void setPointers() {
        this.context = this;

        //getting the location manager (an object that we can deal with locations) from the system services
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationsList = new ArrayList<>();  // DATA // M.
        lstView = findViewById(R.id.lstLocations); // View // V.
        myAdapter = new GPSAdapter();  // Adapter // C.
        lstView.setAdapter(myAdapter);  // M.V.C. //

        icon1 = findViewById(R.id.iconGPS1);
        icon2 = findViewById(R.id.iconGPS2);
        icon3 = findViewById(R.id.iconGPS3);
        gpsTitle = findViewById(R.id.txtGPSTitle);
        txtGPSMsg = findViewById(R.id.txtGPSListMsg);
        btnBack = findViewById(R.id.btnBackInGPS);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAnimations() {

        entryLeft = AnimationUtils.loadAnimation(context, R.anim.entry_from_left);
        entryTop = AnimationUtils.loadAnimation(context, R.anim.entry_from_top);
        entryTopOffset1 = AnimationUtils.loadAnimation(context, R.anim.entry_from_top_offset1);
        entryTopOffset2 = AnimationUtils.loadAnimation(context, R.anim.entry_from_top_offset2);
        entryTopOffset3 = AnimationUtils.loadAnimation(context, R.anim.entry_from_top_offset3);

        entryLeft.setAnimationListener(this);
        entryTop.setAnimationListener(this);
        entryTopOffset1.setAnimationListener(this);
        entryTopOffset2.setAnimationListener(this);
        entryTopOffset3.setAnimationListener(this);
    }


    @Override public void onAnimationStart(Animation animation) { }
    @Override public void onAnimationEnd(Animation animation) { }
    @Override public void onAnimationRepeat(Animation animation) { }

    @Override
    public void onLocationChanged(Location location) {
        //when location is changed
        locationsList.add(location);
        myAdapter.notifyDataSetChanged();
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {
        String statusStr = "\nstatus: ";
        switch(status)
        {
            case LocationProvider.AVAILABLE: statusStr+="AVAILABLE"; break;
            case LocationProvider.OUT_OF_SERVICE: statusStr+="OUT-OF-SERVICE"; break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE: statusStr+="TEMPORARILY UNAVAILABLE"; break;
        }
        Toast.makeText(context, provider.toUpperCase() + statusStr, Toast.LENGTH_SHORT).show();
    }
    @Override public void onProviderEnabled(String provider) {
        Toast.makeText(context, provider.toUpperCase() + " Enabled", Toast.LENGTH_SHORT).show();
    }
    @Override public void onProviderDisabled(String provider) {
        Toast.makeText(context, provider.toUpperCase() + " Disabled", Toast.LENGTH_SHORT).show();
    }

    // inner adapter class // no need to open a new file for this one //
    class GPSAdapter extends BaseAdapter {
        @Override public int getCount() {return locationsList.size(); }
        @Override public Object getItem(int position) { return null; }
        @Override public long getItemId(int position) { return 0; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setTextSize(16);
            txt.setPadding(0, 10, 0, 10);
            Location tmpLoc = locationsList.get(position);
            String tmpStr = "#"+(position+1)+" @ "+convertTime(tmpLoc.getTime())+
                    "\n - - - - - - - - - - - - - -"+
                    "\nlatitude: " + fixedDecimals(tmpLoc.getLatitude(),5) +
                    "\nlongitude: " + fixedDecimals(tmpLoc.getLongitude(),5) +
                    (tmpLoc.hasAltitude()?"\naltitude: "+ fixedDecimals(tmpLoc.getAltitude(),2)+" meters":"")+
                    (tmpLoc.hasBearing()?"\nbearing: "+ fixedDecimals(tmpLoc.getBearing(),2)+" degrees":"")+
                    (tmpLoc.hasSpeed()?"\nspeed: "+ fixedDecimals(tmpLoc.getSpeed(),2)+" m/sec":"");

            if (position % 2 == 0) {
                txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                txt.setTextColor(Color.WHITE);

            } else {
                txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                txt.setTextColor(Color.GRAY);
            }

            txt.setText(tmpStr);

            return txt;
        }

        private String fixedDecimals(double number, int decimals) {
            String decimalStr="";
            for(int i=0; i<decimals;i+=1) decimalStr+="#";
            DecimalFormat df = new DecimalFormat("#."+decimalStr);
            return df.format(number);
        }

        private String convertTime(long time) {
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(date);
        }
    }
}