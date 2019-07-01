package com.iskandar.cvwg;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class VibrationsAdapter extends BaseAdapter implements Animation.AnimationListener {

    Context con;
    List<VibrationElement> myLst;

    Animation entryLeft, entryRight;

    public VibrationsAdapter(Context con) {
        this.con = con;
        myLst = VibrationData.getVibrationData().getDataList();

        // animations initialize
        entryLeft = AnimationUtils.loadAnimation(con,R.anim.entry_from_left);
        entryLeft.setAnimationListener(this);
        entryRight = AnimationUtils.loadAnimation(con,R.anim.entry_from_right);
        entryRight.setAnimationListener(this);
    }


    @Override public int getCount() { return myLst.size(); }
    @Override public Object getItem(int position) { return null; }
    @Override public long getItemId(int position) { return 0; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // NO inflation of .xml ... all views of list are Created here // in JAVA code //


        // creating a linear layout which contains a single text view // & adjusting parameters
        LinearLayout linearLayout = new LinearLayout(con);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
         //       ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(0, (int)px, 0, (int)px); // not working ... //

        // following line is a fix , maybe ... for ClassCastException on some devices (e.g. SGS3) //
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.CENTER); // all child views will be centered
        TextView txt = new TextView(con);
        linearLayout.addView(txt); // adding textView to our LinearLayout


        // set textView attributes & values
        txt.setText(myLst.get(position).getName());
        int chosenColor=Color.WHITE; // default, un-used value //
        switch(position%3)
        {
            case 0: chosenColor = Color.RED; break;
            case 1: chosenColor = Color.YELLOW; break;
            case 2: chosenColor = Color.GREEN; break;
        }
        txt.setTextColor(chosenColor);
        txt.setTextSize(24);
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // convert from dip to pixels //
        float dip = 14f;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                con.getResources().getDisplayMetrics());
        txt.setPadding(0,(int)px,0,(int)px);


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { vibrationPlayStop(position); }
        });

        // do NOT use startAnimation >> messy effects // use setAnimation instead ! //
        linearLayout.setAnimation(position%2==0?entryLeft:entryRight);


        return linearLayout;
    }

    private void vibrationPlayStop(int position) {

        // NOTE: using VibrateActivity.vib Vibrator, and not creating another ! //

        VibrationElement tmpVib = myLst.get(position);

        if(tmpVib.isOn())
        {
            // cancel vibration
            VibrateActivity.vib.cancel();
        }
        else
        {
            // in case, starting this vib. INTERRUPTS another one, we need to turn off
            //  all other vibrations' "indicators"(isOn) before starting this one
            myLst.get(0).turnOffAll(myLst);

            // start this vibration
            if (!VibrateActivity.vib.hasVibrator()) {
                Toast.makeText(con, "Your Hardware has NO vibration!", Toast.LENGTH_SHORT).show();
            }
            else {
                VibrateActivity.vib.cancel(); // cancel any ongoing vibration, if exists //
                // start vibrating
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // api >= 26
                        VibrateActivity.vib.vibrate(VibrationEffect
                                .createWaveform(tmpVib.getPattern(),tmpVib.getRepeat()));
                }
                else  { // api < 26
                    VibrateActivity.vib.vibrate(tmpVib.getPattern(),tmpVib.getRepeat());
                }
            }
        }
        // on >> off // off >> on //
        tmpVib.changeOnOffState();
    }


    @Override public void onAnimationStart(Animation animation) {

    }
    @Override public void onAnimationEnd(Animation animation) {

    }
    @Override public void onAnimationRepeat(Animation animation) {

    }
}