package com.steeve.steeveapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{
    private String LOG_TAG = "Adapter";
    String [] result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public ListAdapter(MainActivity mainActivity, String[] prgmNameList, int[] prgmImages) {
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.tileText);
        holder.img=(ImageView) rowView.findViewById(R.id.tileImage);
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
                Log.v(LOG_TAG, "Position: " + position);
                switch (position) {
                    case 0:
                        Intent pageOneIntent=new Intent(context, DebtActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(pageOneIntent);
                        break;
                    case 1:
                        Intent shoppingIntent = new Intent(context, ShoppingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(shoppingIntent);
                        break;
                }
            }
        });
        return rowView;
    }
} 