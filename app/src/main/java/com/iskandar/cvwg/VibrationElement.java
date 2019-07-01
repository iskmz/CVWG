package com.iskandar.cvwg;

import java.util.List;

public class VibrationElement {


    private String name;
    private boolean isOn;
    private long[] pattern;
    private int repeat;

    public VibrationElement(String name, long[] pattern, int repeat) {
        this.name = name;
        this.pattern = pattern;
        this.repeat = repeat; // -1: NO repeat, 0: repeat from start , n>0: from n-th index //
        this.isOn=false; // at construction, always off //
    }

    public String getName() {
        return name;
    }

    public long[] getPattern() {
        return pattern;
    }

    public int getRepeat() {
        return repeat;
    }

    public boolean isOn() {
        return isOn;
    }

    public void changeOnOffState() {
        this.isOn = !this.isOn;
    }

    public void turnOffAll(List<VibrationElement> lst) {
        for(VibrationElement element: lst) element.isOn=false;
    }
}
