package com.iskandar.cvwg;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.Toast;

public class VibrateActivity extends AppCompatActivity implements Animation.AnimationListener {


    Context context;
    public static Vibrator vib; // public & static in order to use it also from within adapter class
    Animation titleSeeSaw,entryLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrate);

        // set pointers // and also set listView's Adapter // data is inside adapter-class //
        this.context = this;
        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        ((ListView)findViewById(R.id.lstVibes)).setAdapter(new VibrationsAdapter(context));

        // animateListItems();


        // define & load animations // don't start yet!
        initializeAnimations();
        // check hardware // initial check //
        vibrateCheckAtStart();

        // set listeners
        setListeners();

    }

    private void animateListItems() {
        // animate ALL of list view elements to load from right side of screen

        // NOT USED at the moment ... instead, used animations inside of list-view's adapter //

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                                                context, R.anim.listview_anim_right);
        ((ListView)findViewById(R.id.lstVibes)).setLayoutAnimation(controller);

    }

    private void setListeners() {
        (findViewById(R.id.btnBackInVibrate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
        (findViewById(R.id.txtVibTitle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startAnimations(true); }
        });
        (findViewById(R.id.imgWaveStart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startAnimations(true); }
        });
        (findViewById(R.id.imgWaveEnd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startAnimations(true); }
        });
    }

    private void initializeAnimations() {
        titleSeeSaw = AnimationUtils.loadAnimation(context,R.anim.see_saw);
        titleSeeSaw.setAnimationListener(this);
        entryLeft = AnimationUtils.loadAnimation(context,R.anim.entry_from_left);
        entryLeft.setAnimationListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnimations(false);
    }

    private void startAnimations(boolean titleOnly) {

        (findViewById(R.id.txtVibTitle)).startAnimation(titleSeeSaw);
        (findViewById(R.id.imgWaveStart)).startAnimation(titleSeeSaw);
        (findViewById(R.id.imgWaveEnd)).startAnimation(titleSeeSaw);
        if(!titleOnly) {
            (findViewById(R.id.btnBackInVibrate)).startAnimation(entryLeft);
        }
    }

    private void vibrateCheckAtStart() {

        // a method to run at start of activity to check hardware

        // if hardware has no vibrator >> toast message
        // if api level >=26  && with amplitudeControl >> vibrate 2 sec with amp = 100
        // if api level >=26  WITH NO amplitudeControl >> vibrate 5 sec with default amp.
        // if api level < 26  >>> vibrate pattern below , once

        if (!vib.hasVibrator())
        {
            Toast.makeText(context, "Your Hardware has NO vibration!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // for api level 26 and above // use VibrationEffect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(vib.hasAmplitudeControl()) { // another needed check
                    vib.vibrate(VibrationEffect.createOneShot(2000,100));
                } else {
                    vib.vibrate(VibrationEffect.createOneShot(5000,VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
            else  // api < 26
            {
                vib.vibrate(new long[]{100,200,100,200,100,200,100,200,100},-1);
            }

        }
    }

    @Override public void onAnimationStart(Animation animation) {

    }
    @Override public void onAnimationEnd(Animation animation) {

    }
    @Override public void onAnimationRepeat(Animation animation) {

    }
}



