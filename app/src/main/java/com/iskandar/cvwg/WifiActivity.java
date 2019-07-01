package com.iskandar.cvwg;

import android.Manifest;
import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WifiActivity extends AppCompatActivity implements Animation.AnimationListener {


    ListView lstView;
    Context context;
    final int ACCESS_LOCATION = 23; // needed for Android 6 & above (API >= 23) //
    WifiManager wifiMgr; // a manager for all our wifi requests

    Animation entryLeft,entryRight,entryTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        setPointer();
        initializeAnimations();
        getWifiPermission(); // later: adjust permissions, as did in CAMERA // with boolean and result and all ... //
        scanWifi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnimations();
    }

    private void startAnimations() {
        (findViewById(R.id.btnBackInWiFi)).startAnimation(entryLeft);
        (findViewById(R.id.btnRefreshInWiFi)).startAnimation(entryRight);
        (findViewById(R.id.txtWiFiTitle)).startAnimation(entryTop);
    }

    private void initializeAnimations() {

        entryLeft = AnimationUtils.loadAnimation(context,R.anim.entry_from_left);
        entryRight = AnimationUtils.loadAnimation(context,R.anim.entry_from_right);
        entryTop = AnimationUtils.loadAnimation(context,R.anim.entry_from_top);

        entryLeft.setAnimationListener(this);
        entryRight.setAnimationListener(this);
        entryTop.setAnimationListener(this);
    }

    private void setPointer() {
        this.context=this;
        this.lstView=findViewById(R.id.lstNetworks);

        (findViewById(R.id.btnRefreshInWiFi)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { scanWifi(); }
        });
        (findViewById(R.id.btnBackInWiFi)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void getWifiPermission() {
        // NEEDS further work // same as in camera: onPermissionResult ... etc ...
        // and with boolean hasPermission = ?? //


            ActivityCompat.requestPermissions(WifiActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                ACCESS_LOCATION);
    }


    private void scanWifi() {
        //wifi example -> pointer to system service WIFI
        wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled())
        {
            txtMsgCustom("list of nearby WiFi networks ...",16,Color.WHITE,Gravity.START);

            getWifiList();
            lstView.setVisibility(View.VISIBLE);
        }
        else
        {
            txtErrorMsg("WiFi Disabled!",32,Color.RED);
            lstView.setVisibility(View.INVISIBLE);
        }
    }

    private void getWifiList() {

        // setting up a receiver for WifiScanResults, when scan in complete
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                List<ScanResult> wifiList = wifiMgr.getScanResults();
                List<WifiElement> wifiElements = new ArrayList<>();
                for (ScanResult item : wifiList) {
                    wifiElements.add(new WifiElement(item.SSID, item.BSSID, item.capabilities,
                            WifiManager.calculateSignalLevel(item.level, WifiElement.MAX_SIGNAL_LEVEL)));
                }
                WifiAdapter myAdapter = new WifiAdapter(context,wifiElements);
                lstView.setAdapter(myAdapter);

                // time-stamp
                ((TextView)findViewById(R.id.txtTimeScan)).setText(getTimeStamp());
                (findViewById(R.id.txtTimeScan)).setVisibility(View.VISIBLE);
            }
        };
        // an intent to check if wifi scan was completed and results are available
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // register receiver defined above with the intentFilter defined above
        registerReceiver(wifiScanReceiver, intentFilter);


        // in api >= 23 , location needs to be turned ON for scan to work
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) { checkLocationStatus(); }


        if (!wifiMgr.startScan()) {
            // scan failure handling
            txtErrorMsg("Scan FAILED!",32,Color.BLUE);

        }
    }

    private String getTimeStamp() {

        return (new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").
                format(Calendar.getInstance().getTime()));

    }

    private void checkLocationStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}

        if(!gps_enabled && !network_enabled) {

            txtErrorMsg("GPS is OFF!",32,Color.GREEN);


            new AlertDialog.Builder(context)
                    .setTitle("GPS is turned OFF!")
                    .setMessage("\nGPS is needed to scan for WiFi on THIS device!" +
                            "\n\nDo you want to enable it?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("NO!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void txtMsgCustom(String msg,int txtSize,int color,int gravity) {
        ((TextView)findViewById(R.id.txtWifiListMsg)).setText(msg);
        ((TextView)findViewById(R.id.txtWifiListMsg)).setTextSize(txtSize);
        ((TextView)findViewById(R.id.txtWifiListMsg)).setGravity(gravity);
        ((TextView)findViewById(R.id.txtWifiListMsg)).setTextColor(color);
    }

    private void txtErrorMsg(String msg,int txtSize,int color) {
        txtMsgCustom(msg,txtSize,color,Gravity.CENTER);
        (findViewById(R.id.txtTimeScan)).setVisibility(View.INVISIBLE);
    }

    @Override public void onAnimationStart(Animation animation) { }
    @Override public void onAnimationEnd(Animation animation) { }
    @Override public void onAnimationRepeat(Animation animation) { }
}