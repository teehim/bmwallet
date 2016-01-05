package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bmwallet.Class.AccountStorage;
import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class AfterTapActivity extends Activity {
    int type;
    SessionManager ss;
    String promoStorePoint="";
    JSONObject jObj;
    Promotion promotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_tap);

        ss = new SessionManager(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            type =  extras.getInt("transType");
            if(type==1){
                promoStorePoint = extras.getString("promoStorePoint");
            }
        }
        TextView okBtn = (TextView) findViewById(R.id.ok_btn_id);
        TextView message = (TextView) findViewById(R.id.message);
        if(!AccountStorage.isLoggedIn(this)){
            message.setText("Please Login First");
        }else if(type==1){
            message.setText("Use Promotion Successful");
        }
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                v.setBackgroundColor(Color.GRAY); // Choose whichever color
                if(!AccountStorage.isLoggedIn(AfterTapActivity.this)){
                    Intent intent = new Intent(AfterTapActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                if(type==0) {
                    try {
                        ss.updateBalance();
                        ss.updateAllStorePoint();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else if(type==1){
                    String out[]=promoStorePoint.split(",");
                    promotion = new Promotion();
                    promotion.setProId(out[0]);
                    promotion.setStoreId(out[1]);
                    try {
                        ss.updateStorePoint(out[1]);
                        ss.updateBalancePromo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AccountStorage.SetPromoStoreId(AfterTapActivity.this, "0");
                    String url[] = new String[1];
                    url[0] = "https://apps.bm-wallet.com/user/get_promotion_by_id.php";
                    new JSONPost().execute(url);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.after_tap, menu);
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

    private class JSONPost extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progress = new ProgressDialog(AfterTapActivity.this);

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

            jObj = new JSONObject();
            try {
                jObj.put("promo_id",promotion.getProId());
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId", "")));
                jObj.put("userId",ss.getUserDetails().getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, AfterTapActivity.this);

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
                progress.dismiss();
                Intent intent = new Intent(AfterTapActivity.this, PromotionDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("promo", promotion);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



    }
}
