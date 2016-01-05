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
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import customer.bmwallet.com.bangmodwallet.R;

public class RewardArrayAdapter extends ArrayAdapter<PromotionSet> {

    private List<PromotionSet> items;
    private int layoutResourceId;
    private Context context;
    ImageLoader imageLoader;
    Bitmap rewIm;
    DisplayImageOptions options;
    ImageLoaderConfiguration config;

    public RewardArrayAdapter(Context context, int layoutResourceId, List<PromotionSet> items) {
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


        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.reward_list, null);

        }

        PromotionSet p = getItem(position);


        if (p != null) {

            final ImageView rewardImage = (ImageView) v.findViewById(R.id.reward_img);
            TextView rewardName = (TextView) v.findViewById(R.id.reward_name);
            TextView rewardAmount = (TextView) v.findViewById(R.id.reward_amount);
            TextView rewardPrice = (TextView) v.findViewById(R.id.reward_price);

            imageLoader.displayImage("https://apps.bm-wallet.com/store/images/products/"+p.getProductImage(),rewardImage,options);
            rewardName.setText(p.getProductName());
            rewardAmount.setText(p.getAmount()+" ชิ้น");
            rewardPrice.setText("฿ "+p.getPrice());

        }

        return v;

    }

}
