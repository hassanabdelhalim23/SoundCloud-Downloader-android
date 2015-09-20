package com.scd.sounddown.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomAdapter extends BaseAdapter {

    String[] titles;

    Context context;

    DownloadingList ma;

    String[] ImageList;

    String[] DownloadLinks;

    private static LayoutInflater inflater = null;

    public CustomAdapter(DownloadingList mainActivity, String[] titleList, String[] imageList, String[] downloadLinks) {
        // TODO Auto-generated constructor stub
        titles = titleList;
        context = mainActivity;
        ma = mainActivity;
        ImageList = imageList;
        DownloadLinks = downloadLinks;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override


    public int getCount() {
        // TODO Auto-generated method stub
        return titles.length;
    }


    @Override


    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override


    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    public class Holder


    {
        ImageView trackImage;
        TextView title;
        TextView length;
        Button down;
    }


    @Override


    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.tracks_list, null);
//        holder.trackImage = (ImageView) rowView.findViewById(R.id.trackThumb);
        holder.title = (TextView) rowView.findViewById(R.id.title);
//        holder.length = (TextView) rowView.findViewById(R.id.length);
        holder.down = (Button) rowView.findViewById(R.id.download);
        holder.title.setText(titles[position]);
        holder.down.setTag(DownloadLinks[position]);
        holder.down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               ma.download(holder.down.getTag().toString());
            }
        });

//        holder.img.setImageResource(ImageList[position]);
//        rowView.setOnClickListener(new OnClickListener() {
//
//            @Override
//
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                Toast.makeText(context, "You Clicked " + titles[position], Toast.LENGTH_LONG).show();
//            }
//
//
//        });
        return rowView;
    }
}

