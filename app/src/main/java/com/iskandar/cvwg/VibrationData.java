package com.iskandar.cvwg;

import java.util.ArrayList;
import java.util.List;

// use of singleton design pattern , to get an instance of vibration data list //
// same as quotes in notification-random-quotes app done before //

public class VibrationData {

    private List<VibrationElement> vibLst;

    private static VibrationData vibrationData;

    public static VibrationData getVibrationData() {
        if(vibrationData==null) vibrationData=new VibrationData();
        return vibrationData;
    }

    public List<VibrationElement> getDataList()
    {
        return vibLst;
    }

    private VibrationData() {

        // sources for vibrations:-
        // http://www.droidforums.net/threads/post-your-best-custom-vibrate-patterns-for-handcent.20385/
        // http://programmerguru.com/android-tutorial/android-vibrate-example/

        vibLst = new ArrayList<>();

        vibLst.add(new VibrationElement("single & short",new long[]{0,750},-1));
        vibLst.add(new VibrationElement("single & long",new long[]{0,3500},-1));
        vibLst.add(new VibrationElement("S.O.S. (single)",new long[] {
                0, // Start immediately
                200, 200, 200, 200, 200, // S
                500, 500, 200, 500, 200, 500, // O
                500, 200, 200, 200, 200, 200, // s
                1000},-1));
        vibLst.add(new VibrationElement("S.O.S. (repeat)",new long[] {
                0, // Start immediately
                200, 200, 200, 200, 200, // S
                500, 500, 200, 500, 200, 500, // O
                500, 200, 200, 200, 200, 200, // s
                1000},0));
        vibLst.add(new VibrationElement("Final-Fantasy Victory Song",new long[]{
                0,50,100,50,100,50,100,400,
                100,300,100,350,50,200,100,
                100,50,600},-1));
        vibLst.add(new VibrationElement("Unknown", new long[]{
                0,150,50,75,50,75,50,150,50,75,50,75,50,300},-1));
        vibLst.add(new VibrationElement("Shave and a Haircut", new long[]{
                0,100,200,100,100,100,100,100,200,100,500,100,225,100},-1));
        vibLst.add(new VibrationElement("Triangle", new long[] {
                0,200,50,175,50,150,50,125,50,
                100,50,75,50,50,50,75,50,100,50,
                125,50,150,50,157,50,200},-1));
        vibLst.add(new VibrationElement("Star Wars",new long[]{
                0,500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500,
                500,500,110,500,110,450,110,200,110,170,40,600,
                500,500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500,
                500,500,110,500,110,450,110,200,110,170,40,600,500}
                ,-1));
    }
}
