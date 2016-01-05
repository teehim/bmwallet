package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bmwallet.Class.Account;
import com.bmwallet.Class.AccountStorage;
import com.bmwallet.Class.CustomImage;
import com.bmwallet.Class.Customer;
import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.PromotionSet;
import com.bmwallet.Class.RewardArrayAdapter;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PromotionDetailActivity extends Activity {

    static SessionManager ss;
    TextView title,storeName,usePoint,expDate,description;
    TextView memPoint;
    Promotion promotion;
    ListView rewardList;
    RewardArrayAdapter adapter;
    JSONObject jObj = new JSONObject();
    PromotionSet promoSet;
    Button usePromoBtn;
    String promoStoreId;
    ImageView photo;
    Bitmap proIm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promo_detail);

        promoStoreId = AccountStorage.GetPromoStoreId(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rewardList = (ListView) findViewById(R.id.reward_list);
        title = (TextView) findViewById(R.id.title);
        storeName = (TextView) findViewById(R.id.store_name);
        memPoint = (TextView) findViewById(R.id.mem_point);
        usePoint = (TextView) findViewById(R.id.use_point);
        expDate = (TextView) findViewById(R.id.exp_date);
        description = (TextView) findViewById(R.id.description);
        usePromoBtn = (Button) findViewById(R.id.use_promo);
        photo = (ImageView) findViewById(R.id.image);
        promotion = new Promotion();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).memoryCacheSizePercentage(33).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();


        ss = new SessionManager(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            promotion = (Promotion) extras.getSerializable("promo");
        }
        setTitle(promotion.getTitle());

        imageLoader.loadImage("https://apps.bm-wallet.com/store/images/promotions/"+promotion.getProIm(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                proIm = loadedImage;

                photo.setImageBitmap(proIm);
            }
        });
        title.setText(promotion.getTitle());
        storeName.setText(promotion.getStoreName());
        memPoint.setText("Member Points : "+ss.getStorePoint(promotion.getStoreId()));
        usePoint.setText("Use Member Points : "+promotion.getUsePoint());
        expDate.setText("Expire Date : " + promotion.getExpDate());
        description.setText("Description : " + promotion.getDescription());

        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/get_promotion_detail.php";
        new JSONPost().execute(url);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.promotion_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class JSONPost extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url= urls[0];
            SharedPreferences prefs = getSharedPreferences(
                    "UserData", Context.MODE_PRIVATE);


            try {
                jObj.put("promo_id",promotion.getProId());
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId","")));
                jObj.put("user_id", ss.getUserDetails().getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONArray json = JSONParser.postToUrlArr(url, jObj, PromotionDetailActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            ArrayList<PromotionSet> arReward = new ArrayList<PromotionSet>();
            for(int i=0;i<json.length();i++){
                promoSet = new PromotionSet();
                try {
                    promoSet.setProductImage(json.getJSONObject(i).getString("IMAGEPATH"));
                    promoSet.setPrice(json.getJSONObject(i).getString("PRICE"));
                    promoSet.setProductId(json.getJSONObject(i).getString("PRODUCTID"));
                    promoSet.setAmount(json.getJSONObject(i).getString("AMOUNT"));
                    promoSet.setProductName(json.getJSONObject(i).getString("NAME"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arReward.add(promoSet);
            }

            adapter = new RewardArrayAdapter(PromotionDetailActivity.this, R.layout.promo_detail,arReward);
            rewardList.setAdapter(adapter);

        }

    }

    public void usePromoOnclick(View v){
        if(promoStoreId.equals("0")) {
            usePromoBtn.setBackgroundColor(getResources().getColor(R.color.after_tap));
            usePromoBtn.setText(getResources().getString(R.string.after_tap));
            String promoStorePoint = promotion.getProId()+","+promotion.getStoreId()+","+ss.getStorePoint(promotion.getStoreId());
            System.out.println(promoStorePoint);
            AccountStorage.SetPromoStoreId(PromotionDetailActivity.this,promoStorePoint);
        }else {
            usePromoBtn.setBackgroundColor(getResources().getColor(R.color.before_tap));
            usePromoBtn.setText(getResources().getString(R.string.before_tap));
            AccountStorage.SetPromoStoreId(PromotionDetailActivity.this, "0");
        }
        promoStoreId = AccountStorage.GetPromoStoreId(PromotionDetailActivity.this);
    }

    @Override
    public void onBackPressed() {
        AccountStorage.SetPromoStoreId(PromotionDetailActivity.this, "0");
        super.onBackPressed(); // This will pop the Activity from the stack.
    }

}
