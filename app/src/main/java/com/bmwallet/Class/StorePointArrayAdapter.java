package com.bmwallet.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import customer.bmwallet.com.bangmodwallet.R;

/**
 * Created by Thanatkorn on 9/26/2014.
 */
public class StorePointArrayAdapter extends ArrayAdapter<StorePoint> {
    private List<StorePoint> items;
    private int layoutResourceId;
    private Context context;

    public StorePointArrayAdapter(Context context, int layoutResourceId, List<StorePoint> items) {
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

        StorePoint p = getItem(position);




            TextView storeName = (TextView) v.findViewById(R.id.store_name);
            TextView storePoint = (TextView) v.findViewById(R.id.store_point);

            storeName.setText(p.getStoreName());
            storePoint.setText(p.getPoint());
        return v;

    }
}
