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
 * Created by Thanatkorn on 9/29/2014.
 */
public class LineItemArrayAdapter extends ArrayAdapter<LineItem> {

private List<LineItem> items;
private int layoutResourceId;
private Context context;

public LineItemArrayAdapter(Context context, int layoutResourceId, List<LineItem> items) {
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
        v = vi.inflate(R.layout.line_item_list, null);

        }

        LineItem p = getItem(position);


        if (p != null) {

        TextView prodName = (TextView) v.findViewById(R.id.prod_name);
        TextView amount = (TextView) v.findViewById(R.id.amount);
        TextView total = (TextView) v.findViewById(R.id.total);



        prodName.setText(p.getProdName());
        amount.setText(p.getAmount());
        total.setText(p.getTotal());

        }

        return v;

        }

        }

