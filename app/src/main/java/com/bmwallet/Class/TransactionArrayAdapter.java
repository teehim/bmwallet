package com.bmwallet.Class;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import customer.bmwallet.com.bangmodwallet.R;

public class TransactionArrayAdapter extends ArrayAdapter<Order> {

    private List<Order> items;
    private int layoutResourceId;
    private Context context;

    public TransactionArrayAdapter(Context context, int layoutResourceId, List<Order> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.transaction_list, null);

        }

        Order p = getItem(position);


        if (p != null) {

            TextView orderTime = (TextView) v.findViewById(R.id.order_time);
            TextView storeName = (TextView) v.findViewById(R.id.store_name);
            TextView total = (TextView) v.findViewById(R.id.total);


            if(p.getOrderType().equals("T")){
                total.setTextColor(context.getResources().getColor(R.color.before_tap));
                storeName.setText("สหกรณ์");
            }else if(p.getOrderType().equals("C")){
                total.setTextColor(context.getResources().getColor(R.color.after_tap));
                storeName.setText(p.getStoreName());
            }
            orderTime.setText(p.getOrderTime());

            total.setText(p.getTotal());

        }

        return v;

    }

}
