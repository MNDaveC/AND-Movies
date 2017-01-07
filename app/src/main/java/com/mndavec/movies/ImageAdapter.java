package com.mndavec.movies;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private Movie[] posters;

    public ImageAdapter(Context c, Movie[] posters) {
        context = c;
        this.posters = posters;
    }

    public int getCount() {
        return posters.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(context);
        }
        String url = posters[position].getPosterURL(); //getItem(position);
        Picasso.with(context).load(url).into(view);
        return view;
    }
}