package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.astuetz.PagerSlidingTabStrip;
import com.bmwallet.Class.CustomViewPager;
import com.bmwallet.Class.Customer;
import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.LineItem;
import com.bmwallet.Class.Order;
import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.TransactionArrayAdapter;
import com.bmwallet.Class.UpdateableFragment;
import com.bmwallet.Class.Utility;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


//
public class MainPage extends FragmentActivity {


    JSONObject jObj = new JSONObject();
    ListView promoList;
    Promotion promotion;
    Order order;
    SessionManager ss;
    Customer u;
    CustomViewPager viewPager;
    private static final String PREF_NAME = "UserData";
    int periodValue = 0;
    String typeValue = "A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        setTitle("Home");
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter());

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setIndicatorColor(0xff515151);
        tabs.setBackgroundColor(0xffffffff);

        ss = new SessionManager(this);
        try {
            ss.updateTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: setTitle("Home");break;
                    case 1: setTitle("Promotions");break;
                    case 2:setTitle("Previous Transactions");break;
                }
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


       /* mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);
        mSlidingTabLayout.setViewPager(viewPager);*/




    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()!=0) {
            viewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed(); // This will pop the Activity from the stack.
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(getBaseContext(), "You selected Phone", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                ss = new SessionManager(this);
                ss.logoutUser();
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

    public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 3;


        @Override
        public int getItemPosition(Object object) {
            if (object instanceof UpdateableFragment) {
                ((UpdateableFragment) object).update();
            }
            return super.getItemPosition(object);
        }

        public SampleFragmentPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String ret;
            switch(position){
                case 0: ret = "Home"; break;
                case 1: ret = "Promotion"; break;
                case 2: ret = "Transaction"; break;
                default: ret =  "What?"; break;
            }
            return ret;

        }

        @Override
        public Fragment getItem(int position) {
            Fragment page = null;
            switch (position) {
                case 0: page = new HomeFragment(); break;
                case 1: page = new PromoListFragment(); break;
                case 2: page = new TransactionListFragment(); break;

                default:
                    page = new HomeFragment();

                    break;
            }

            return page;
        }
    }

    public void getPromotionOnClick(View v) {

        final ListView promoList = (ListView) findViewById(R.id.promo_list);
        final int position = promoList.getPositionForView((LinearLayout) v.getParent());
        promotion = (Promotion) promoList.getAdapter().getItem(position);
        ss = new SessionManager(this);
        u = ss.getUserDetails();


        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/get_promotion_by_id.php";
        new JSONPost().execute(url);

    }

    public void getPromotionDetailOnClick(View v) {

        final ViewFlipper promoList = (ViewFlipper) findViewById(R.id.promo_flip);
        final int position = promoList.getDisplayedChild();
        ss = new SessionManager(this);
        promotion = ss.getActivePromotion().get(position);
        ss = new SessionManager(this);
        u = ss.getUserDetails();


        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/get_promotion_by_id.php";
        new JSONPost().execute(url);

    }


    private class JSONPost extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progress = new ProgressDialog(MainPage.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            progress.setMessage("Loading...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];
            SharedPreferences prefs = getSharedPreferences(
                    "UserData", Context.MODE_PRIVATE);

            try {
                jObj = new JSONObject();
                jObj.put("promo_id",promotion.getProId());
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId","")));
                jObj.put("userId",u.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, MainPage.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                promotion.setTitle(json.getString("TITLE"));
                promotion.setStoreName(json.getString("STORENAME"));
                promotion.setUsePoint(json.getString("USEMEMBERPOINT"));
                promotion.setExpDate(json.getString("EXPIREDATE"));
                promotion.setDescription(json.getString("DESCRIPTION"));
                promotion.setProIm(json.getString("PHOTO"));
                progress.dismiss();
                Intent intent = new Intent(MainPage.this, PromotionDetailActivity.class);
                intent.putExtra("promo", promotion);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void setFilterOnClick(View v){
        final TextView period = (TextView) findViewById(R.id.trans_period);
        final TextView type = (TextView) findViewById(R.id.trans_type);

        switch (v.getId()){
            case R.id.trans_period:
                final CharSequence[] items = {"Today","1 Week","1 Month","3 Months"};
                final int values1[] = {0,6,29,89};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        period.setText(items[item]);
                        periodValue = values1[item];
                        String url[] = new String[1];
                        url[0] = "https://apps.bm-wallet.com/user/get_history_transaction.php";
                        new GetTrans().execute(url);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.trans_type:
                final CharSequence[] items1 = {"ALL","PAY","TOP UP",};
                final String values2[] = {"A","C","T"};
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainPage.this);
                builder1.setItems(items1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        type.setText(items1[item]);
                        typeValue = values2[item];
                        String url[] = new String[1];
                        url[0] = "https://apps.bm-wallet.com/user/get_history_transaction.php";
                        new GetTrans().execute(url);
                    }
                });
                AlertDialog alert1 = builder1.create();
                alert1.show();
                break;

        }




    }

    private class GetTrans extends AsyncTask<String, Void, JSONArray> {

        ProgressDialog progress = new ProgressDialog(MainPage.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            progress.setMessage("Loading...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url= urls[0];
            SharedPreferences prefs = MainPage.this.getSharedPreferences("UserData", Context.MODE_PRIVATE);

            try {
                jObj = new JSONObject();
                jObj.put("period",periodValue);
                jObj.put("type",typeValue);
                jObj.put("cust_id", ss.getUserDetails().getUserId());
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId", "")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            // Getting JSON from URL
            JSONArray json = JSONParser.postToUrlArr(url, jObj, MainPage.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            ArrayList<Order> orders = new ArrayList<Order>();
            for(int i=0;i<json.length();i++){
                try {
                    JSONObject transObj = json.getJSONObject(i);
                    Order order = new Order();
                    order.setOrderId(transObj.getString("TRANSACTIONID"));
                    order.setSellerId(transObj.getString("SELLERID"));
                    order.setOrderType(transObj.getString("TYPE"));
                    order.setOrderTime(transObj.getString("TIMESTAMP"));
                    order.setTotal(transObj.getString("TOTAL"));
                    order.setStoreName(ss.getStoreName(transObj.getString("SELLERID")));
                    orders.add(order);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            progress.dismiss();
            ss.setTransaction(orders);
            ListView transList = (ListView) findViewById(R.id.trans_list);
            TransactionArrayAdapter adapter = new TransactionArrayAdapter(MainPage.this, R.layout.fragment_transaction_list,orders);
            transList.setAdapter(adapter);
        }
    }

    public void getLineItemOnClick(View v){
        final ListView transList = (ListView) findViewById(R.id.trans_list);
        final int position = transList.getPositionForView((LinearLayout) v.getParent());
        order = (Order) transList.getAdapter().getItem(position);

        if(order.getOrderType().equals("C")) {
            String url[] = new String[1];
            url[0] = "https://apps.bm-wallet.com/user/get_lineItem.php";
            new GetLine().execute(url);
        }
    }

    private class GetLine extends AsyncTask<String, Void, JSONArray> {
        ProgressDialog progress = new ProgressDialog(MainPage.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            progress.setMessage("Loading...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url= urls[0];
            SharedPreferences prefs = MainPage.this.getSharedPreferences("UserData", Context.MODE_PRIVATE);

            try {
                jObj = new JSONObject();
                jObj.put("transaction_id",order.getOrderId());
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId", "")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            // Getting JSON from URL
            JSONArray json = JSONParser.postToUrlArr(url, jObj, MainPage.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            ArrayList<LineItem> lines = new ArrayList<LineItem>();
            for(int i=0;i<json.length();i++) {
                try {
                    JSONObject transObj = json.getJSONObject(i);
                    LineItem line = new LineItem();
                    line.setProdId(transObj.getString("PRODUCTID"));
                    line.setProdName(transObj.getString("NAME"));
                    line.setTotal(transObj.getString("TOTAL"));
                    line.setAmount(transObj.getString("AMOUNT"));
                    line.setPoint(transObj.getString("POINT"));
                    lines.add(line);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                progress.dismiss();
                Intent intent = new Intent(MainPage.this,LineItemDetailActivity.class);
                intent.putExtra("lines",lines);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);


        }
    }
}
