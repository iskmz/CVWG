package com.iskandar.cvwg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class CameraAlbumAdapter extends BaseAdapter {

    Context contxt;
    List<ImageView> mylst;

    // references to our images // to check stuff out //
    /*
    private Integer[] mThumbIds = {
            R.drawable.icon_about, R.drawable.icon_back,
            R.drawable.icon_gps, R.drawable.icon_camera,
            R.drawable.icon_vibrate, R.drawable.icon_wifi,

    };
    */

    public CameraAlbumAdapter(Context contxt, List<ImageView> listPhotos) {
        this.contxt = contxt;
        this.mylst = listPhotos;
    }


    @Override
    public int getCount() {
        return mylst.size();
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
        // NOTE: had to comment-out all lines related to CONVERT VIEW because
        // they "conflict" with notifyDataSetChanged and make problems in gridView
        // after adding elements to or deleting from the image-view list !

        ImageView imageView;
//        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = mylst.get(position);
            //imageView.setLayoutParams(new GridView.LayoutParams(100,80));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(3, 3, 3, 3);
            imageView.setBackgroundColor(Color.rgb(250,100,0));
//        } else {
//            imageView = (ImageView) convertView;
//        }

        return imageView;
    }
}
