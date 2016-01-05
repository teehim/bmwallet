package com.bmwallet.Class;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import customer.bmwallet.com.bangmodwallet.R;

public class PromotionArrayAdapter extends ArrayAdapter<Promotion> {

		private List<Promotion> items;
		private int layoutResourceId;
		private Context context;
        SessionManager ss;
        DisplayImageOptions options;
        ImageLoaderConfiguration config;
        ImageLoader imageLoader;


    public PromotionArrayAdapter(Context context, int layoutResourceId, List<Promotion> items) {
			super(context, layoutResourceId, items);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.items = items;
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
            config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(options).memoryCacheSizePercentage(70).build();
            ImageLoader.getInstance().init(config);
            imageLoader = ImageLoader.getInstance();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		    View v = convertView;


            ss = new SessionManager(context);


		    if (v == null) {

		        LayoutInflater vi;
		        vi = LayoutInflater.from(getContext());
		        v = vi.inflate(R.layout.promo_list, null);

		    }

		    Promotion p = getItem(position);


		    if (p != null) {

		        final ImageView img = (ImageView) v.findViewById(R.id.image);
		        TextView title = (TextView) v.findViewById(R.id.title);
                TextView storeName = (TextView) v.findViewById(R.id.store_name);
                TextView useMem = (TextView) v.findViewById(R.id.use_mem);
                TextView expDate = (TextView) v.findViewById(R.id.exp_date);
                TextView storePoint = (TextView) v.findViewById(R.id.spoint);

                imageLoader.displayImage("https://apps.bm-wallet.com/store/images/promotions/" + p.getProIm(),img,options);
                title.setText(p.getTitle());
                storePoint.setText(ss.getStorePoint(p.getStoreId()));
                storeName.setText(ss.getStoreName(p.getStoreId()));
		        useMem.setText("Use: "+p.getUsePoint()+" Point(s)");
                expDate.setText("Expire Date: "+p.getExpDate());

		    }

		    return v;

		}
		
	}
