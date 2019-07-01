package com.iskandar.cvwg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class WifiAdapter extends BaseAdapter {

    Context context;
    List<WifiElement> wifiElements;

    public WifiAdapter(Context context, List<WifiElement> wifiElements) {
        this.context = context;
        this.wifiElements = wifiElements;
    }

    @Override
    public int getCount() {
        return wifiElements.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // set current parent view and child-views
        View view = LayoutInflater.from(context).inflate(R.layout.item_wifi,null);
        TextView ssid = view.findViewById(R.id.itemWifiSSID);
        TextView bssid = view.findViewById(R.id.itemWifiBSSID);
        TextView capabilities = view.findViewById(R.id.itemWifiCapabilities);
        ImageView imgSignal = view.findViewById(R.id.itemWifiLevelImg);

        // get current wifi element (data item)
        WifiElement tmp = wifiElements.get(position);

        // connect data to view
        ssid.setText(tmp.getSSID());
        bssid.setText(tmp.getBSSID());
        capabilities.setText(tmp.getCapabilities());
        switch (tmp.getSignalLevel()) // 0,1,2,3 //
        {
            case 0: imgSignal.setImageResource(R.drawable.wifi_level_0); break;
            case 1: imgSignal.setImageResource(R.drawable.wifi_level_1); break;
            case 2: imgSignal.setImageResource(R.drawable.wifi_level_2); break;
            case 3: imgSignal.setImageResource(R.drawable.wifi_level_3); break;
        }

        return view;
    }
}
