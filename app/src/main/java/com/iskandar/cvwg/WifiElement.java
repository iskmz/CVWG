package com.iskandar.cvwg;

public class WifiElement {


    String ssid;  // network name
    String bssid; // MAC address
    String capabilities; // authentication , encryption ... etc
    int signalLevel; // signal strength (absolute level, i.e. POST-conversion)

    public static final int MAX_SIGNAL_LEVEL = 4; // range:  0,1,2,3 //

    public WifiElement(String ssid, String bssid, String capabilities, int signalLevel) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.signalLevel = signalLevel;
    }


    public String getSSID() {
        return ssid;
    }

    public String getBSSID() {
        return bssid.toUpperCase();
    }

    public String getCapabilities() { // after analysis // and editing
        return capabilities.replace("[","").replace("]","\n");
    }

    public int getSignalLevel() {
        return signalLevel;
    }
}
