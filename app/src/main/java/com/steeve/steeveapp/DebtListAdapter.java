package com.steeve.steeveapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DebtListAdapter extends BaseAdapter {
    private String LOG_TAG = "DebtListAdapterLOG";
    Context adapterContext;
    String [] usersList;
    String [] receiversList;
    String [] debtAmountsList;
    private static LayoutInflater inflater=null;
    private Animation animation;


    public DebtListAdapter (Context context, String[] dbUsersList, String[] dbReceiversList, String[] dbDebtsAmountList) {
        adapterContext = context;
        usersList = dbUsersList;
        receiversList = dbReceiversList;
        debtAmountsList = dbDebtsAmountList;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return usersList.length;
    }

    @Override
    public Object getItem(int position) {
        return getCount() - position;
    }

    @Override
    public long getItemId(int position) {
        return getCount() - position;
    }

    public class Holder
    {
        TextView userTV;
        TextView debtAmountTV;
        TextView receiverTV;
        TextView euroTV;
        RelativeLayout colorLayout1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        Boolean isCredit = false;
        View rowView;
        rowView = inflater.inflate(R.layout.debts_list_item, parent, false);
        holder.userTV = (TextView) rowView.findViewById(R.id.userTextView);
        holder.receiverTV = (TextView) rowView.findViewById(R.id.receiverTextView);
        holder.debtAmountTV = (TextView) rowView.findViewById(R.id.debtAmountTextView);
        holder.colorLayout1 = (RelativeLayout) rowView.findViewById(R.id.colorLayout1);
        holder.euroTV = (TextView) rowView.findViewById(R.id.euroTV);
        holder.userTV.setText(usersList[position]);
        if (Float.parseFloat(debtAmountsList[position])<0) { isCredit = true;  }
        holder.debtAmountTV.setText(Float.toString(Math.abs(Float.parseFloat(debtAmountsList[position]))));
        holder.receiverTV.setText(receiversList[position]);

        if (!isCredit) { // Deve avvenire dopo popolamento debtAmountTV!
            holder.colorLayout1.setBackgroundResource(android.R.color.holo_red_light);
            holder.userTV.setTextColor(Color.parseColor("#FF0000"));
            holder.receiverTV.setTextColor(Color.parseColor("#00ff00"));
            holder.euroTV.setTextColor(Color.parseColor("#ff0000"));
            holder.debtAmountTV.setTextColor(Color.parseColor("#ff0000"));
        } else {
            holder.userTV.setTextColor(Color.parseColor("#00ff00"));
            holder.receiverTV.setTextColor(Color.parseColor("#ff0000"));
            holder.colorLayout1.setBackgroundResource(android.R.color.holo_green_light);
            holder.euroTV.setTextColor(Color.parseColor("#00ff00"));
            holder.debtAmountTV.setTextColor(Color.parseColor("#00ff00"));
        }
        animation = AnimationUtils.loadAnimation(adapterContext, R.anim.slide_in_anim);
        animation.setDuration(400);
        rowView.startAnimation(animation);

        return rowView;
    }
} 