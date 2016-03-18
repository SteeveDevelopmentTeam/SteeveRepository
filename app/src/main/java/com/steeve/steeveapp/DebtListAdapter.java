package com.steeve.steeveapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DebtListAdapter extends BaseAdapter {
    private String LOG_TAG = "DebtListAdapterLOG";
    Context adapterContext;
    String [] usersList;
    String [] receiversList;
    String [] debtAmountsList;
    String [] dateList;
    private static LayoutInflater inflater=null;


    public DebtListAdapter (Context context, String[] dbUsersList, String[] dbReceiversList, String[] dbDebtsAmountList, String [] dbDateList) {
        adapterContext = context;
        usersList = dbUsersList;
        receiversList = dbReceiversList;
        debtAmountsList = dbDebtsAmountList;
        dateList = dbDateList;
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
        TextView timeStampTV;
        RelativeLayout colorLayout1, mainDataContainer, dateContainer;
        ImageView infoButton;
    }

    /*public static CharSequence createDate(String timestamp) {
        Log.v("DebtListAdapter", "Timestamp before conversion:  " + timestamp);
        long ts = Long.parseLong(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(d);
    }*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        Boolean isCredit = false;
        View rowView;
        rowView = inflater.inflate(R.layout.debts_list_item, parent, false);
        holder.userTV = (TextView) rowView.findViewById(R.id.userTextView);
        holder.receiverTV = (TextView) rowView.findViewById(R.id.receiverTextView);
        holder.debtAmountTV = (TextView) rowView.findViewById(R.id.debtAmountTextView);
        holder.colorLayout1 = (RelativeLayout) rowView.findViewById(R.id.colorLayout1);
        holder.euroTV = (TextView) rowView.findViewById(R.id.euroTV);
        holder.userTV.setText(usersList[position]);
        holder.mainDataContainer = (RelativeLayout) rowView.findViewById(R.id.mainDataContainer);
        holder.dateContainer = (RelativeLayout) rowView.findViewById(R.id.dateContainer);
        holder.timeStampTV = (TextView) rowView.findViewById(R.id.timestampTV);
        holder.timeStampTV.setText(formattedDate(dateList[position]).toString());
        holder.infoButton = (ImageView) rowView.findViewById(R.id.transaction_info_button);
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

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mainDataContainer.getVisibility() == View.VISIBLE) {
                    holder.mainDataContainer.setVisibility(View.GONE);
                    holder.dateContainer.setVisibility(View.VISIBLE);
                } else {
                    holder.mainDataContainer.setVisibility(View.VISIBLE);
                    holder.dateContainer.setVisibility(View.GONE);
                }
            }
        });

        Animation animation = AnimationUtils.loadAnimation(adapterContext, R.anim.slide_in_anim);
        animation.setDuration(400);
        rowView.startAnimation(animation);


        return rowView;
    }

    private Date formattedDate(String s)  {
        Locale local = Locale.ITALY;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH:mm:ss", local);
        Date convertedDate = null;
        try {
            convertedDate = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
} 