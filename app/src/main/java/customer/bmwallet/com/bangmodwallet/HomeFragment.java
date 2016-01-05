package customer.bmwallet.com.bangmodwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bmwallet.Class.Account;
import com.bmwallet.Class.CustomImage;
import com.bmwallet.Class.CustomViewFlipper;
import com.bmwallet.Class.Customer;
import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.UpdateableFragment;
import com.bmwallet.Class.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements UpdateableFragment {

    CustomViewFlipper vf;
    float initialX;
    ViewGroup rootView;
    TextView balance;
    SessionManager ss;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    ImageLoaderConfiguration config;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(options).memoryCacheSizePercentage(70).build();

        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();




        rootView = (ViewGroup) inflater.inflate(
                R.layout.home, container, false);




        // new JSONParse().execute(url);


        // ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        // viewPager.setAdapter(new SampleFragmentPagerAdapter());

        TextView firstName = (TextView)rootView.findViewById(R.id.firstname);
        balance = (TextView) rootView.findViewById(R.id.balance);

        ss = new SessionManager(getActivity());
        Customer u = ss.getUserDetails();
        Account a = ss.getAccountDetails();
        final ArrayList<Promotion> promotionList = ss.getActivePromotion();

        firstName.setText(u.getFirstName());
        balance.setText("฿ "+a.getBalance());

        final ImageView proIn = (ImageView) rootView.findViewById(R.id.image);

        imageLoader.displayImage(u.getImgPath(),proIn,options);



        vf = (CustomViewFlipper) rootView.findViewById(R.id.promo_flip);
        vf.setInAnimation(getActivity(), android.R.anim.fade_in);
        vf.setOutAnimation(getActivity(), android.R.anim.fade_out);

        LayoutInflater inflater1 = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View proList;

        if(promotionList == null)
            System.out.println("Null Jaaa");

        for (int i = 0; i < promotionList.size(); i++) {
            if(i!=5) {
                proList = (View) inflater1.inflate(R.layout.promoshow, null);
                Promotion p = promotionList.get(i);
                final ImageView promoImage = (ImageView) proList.findViewById(R.id.promo_image);


                imageLoader.displayImage("https://apps.bm-wallet.com/store/images/promotions/" + p.getProIm(),promoImage,options);


                TextView promoName = (TextView) proList
                        .findViewById(R.id.promo_name);
                TextView store = (TextView) proList.findViewById(R.id.store);
                TextView exp = (TextView) proList.findViewById(R.id.exp_date);
                TextView dl = (TextView) proList.findViewById(R.id.days_left);

                LinearLayout detail = (LinearLayout) proList.findViewById(R.id.detail);
                String[] expDateArr = p.getExpDate().split("-");
                LocalDate expDate = new LocalDate(Integer.parseInt(expDateArr[0]), Integer.parseInt(expDateArr[1]), Integer.parseInt(expDateArr[2]));

                LocalDate now = new LocalDate();
                Period period = new Period(now, expDate, PeriodType.days());
                String dayLeft = "" + period.getDays();


                promoName.setText(p.getTitle());
                store.setText("store" + p.getStoreId());
                exp.setText("days left until \n" + p.getExpDate());
                dl.setText(dayLeft);
                vf.addView(proList, i);

            }else{
                break;
            }

        }

        vf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = arg1.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float finalX = arg1.getX();
                        if (initialX > finalX) {
                            if (vf.getDisplayedChild() == promotionList.size()-1)
                                break;

                            setFlipperIn(1);

                            vf.showNext();
                        } else {
                            if (vf.getDisplayedChild() == 0)
                                break;

                            setFlipperIn(2);
                            vf.showPrevious();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        float finalX1 = arg1.getX();
                        if (initialX > finalX1) {
                            if (vf.getDisplayedChild() == promotionList.size()-1)
                                break;

                            setFlipperIn(1);

                            vf.showNext();
                        } else {
                            if (vf.getDisplayedChild() == 0)
                                break;

                            setFlipperIn(2);
                            vf.showPrevious();
                        }
                        break;
                }
                return true;
            }
        });

        return rootView;

    }

    public void setFlipperIn(int direc) {
        if (direc == 1) {
            vf.setInAnimation(getActivity(), R.anim.in_right);
            vf.setOutAnimation(getActivity(), R.anim.out_left);
        } else {
            vf.setInAnimation(getActivity(), R.anim.in_left);
            vf.setOutAnimation(getActivity(), R.anim.out_right);
        }
    }

    @Override
    public void update() {
        Account a = ss.getAccountDetails();
        balance.setText("฿ "+a.getBalance());
    }

}
