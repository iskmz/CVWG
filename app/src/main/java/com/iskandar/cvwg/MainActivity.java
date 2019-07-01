package com.iskandar.cvwg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

import io.fabric.sdk.android.Fabric;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {


    Context context;
    ImageButton btnAbout,btnQuit, btnCamera,btnVibrate,btnWifi,btnGPS;
    TextView txtCamera, txtVibrate, txtWifi, txtGPS;
    Animation entryLeft, entryRight, entryTop, entryBottom;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        setContentView(R.layout.activity_main);

        setPointers();
        setListeners();
        showWelcomeBar(3000,5000); // once, at startup only //
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnimations();
    }

    public void onClickCamera(View v) {
        startActivity(new Intent(context,CameraActivity.class));
    }
    public void onClickVibrate(View v) {
        startActivity(new Intent(context,VibrateActivity.class));
    }
    public void onClickWifi(View v) {
        startActivity(new Intent(context,WifiActivity.class));
    }
    public void onClickGPS(View v) {
        startActivity(new Intent(context,GPSActivity.class));
    }

    private void setListeners() {

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // animation listeners
        // first one was done separately, to call Snack-bar after it ends , ONCE !
        // .. & only once !
        entryLeft.setAnimationListener(this);
        entryRight.setAnimationListener(this);
        entryTop.setAnimationListener(this);
        entryBottom.setAnimationListener(this);
        // startAnimations(); // moved to onStart
    }

    private void showWelcomeBar(final int offset_mSec, final int duration_mSec) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(offset_mSec); // to wait for animations to finish //
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(R.id.layoutMainAct), "Welcome to C.V.W.G.", duration_mSec)
                                .setAction("About!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAboutDialog();
                                    }
                                })
                                .setActionTextColor(Color.rgb(250, 100, 00))
                                .show();
                    }
                });
            }
        }).start();
    }

    private void showAboutDialog() {
        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle("C.V.W.G.")
                .setMessage("\n\tC amera"+
                        "\n\tV ibrate"+
                        "\n\tW i-Fi"+
                        "\n\tG PS"+
                        "\n\n\tbrought to you by \n\n\t\t Iskandar Mazzawi \u00a9")
                .setIcon(R.drawable.icon_about)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void startAnimations() {

        // from left
        btnAbout.startAnimation(entryLeft);
        // from right
        btnQuit.startAnimation(entryRight);
        // from top
        btnCamera.startAnimation(entryTop);
        btnVibrate.startAnimation(entryTop);
        txtCamera.startAnimation(entryTop);
        txtVibrate.startAnimation(entryTop);
        // from bottom
        btnWifi.startAnimation(entryBottom);
        btnGPS.startAnimation(entryBottom);
        txtWifi.startAnimation(entryBottom);
        txtGPS.startAnimation(entryBottom);

    }

    private void setPointers() {
        this.context=this;
        firstTime=true;

        // buttons
        btnAbout = findViewById(R.id.btnAbout);
        btnQuit = findViewById(R.id.btnQuit);
        btnCamera = findViewById(R.id.btnCamera);
        btnVibrate = findViewById(R.id.btnVibrate);
        btnWifi = findViewById(R.id.btnWifi);
        btnGPS = findViewById(R.id.btnGPS);

        // animations
        entryLeft = AnimationUtils.loadAnimation(context,R.anim.entry_from_left);
        entryRight = AnimationUtils.loadAnimation(context,R.anim.entry_from_right);
        entryTop = AnimationUtils.loadAnimation(context,R.anim.entry_from_top);
        entryBottom = AnimationUtils.loadAnimation(context,R.anim.entry_from_bottom);

        // text views
        txtCamera = findViewById(R.id.txtCamera);
        txtVibrate = findViewById(R.id.txtVibrate);
        txtWifi = findViewById(R.id.txtWiFi);
        txtGPS = findViewById(R.id.txtGPS);
    }


    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
