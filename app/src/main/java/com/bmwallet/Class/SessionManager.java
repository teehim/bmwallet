package com.bmwallet.Class;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import customer.bmwallet.com.bangmodwallet.LoginActivity;
import customer.bmwallet.com.bangmodwallet.MainPage;
import customer.bmwallet.com.bangmodwallet.PromotionDetailActivity;
import customer.bmwallet.com.bangmodwallet.SessionReceiver;

/**
 * Created by Thanatkorn on 8/20/2014.
 */
public class SessionManager {
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;


    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserData";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    ArrayList<Promotion> promotionList;
    JSONObject jObj;
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(JSONObject info){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        schedAlarm();
        JSONObject user = null;
        JSONObject account = null;
        JSONArray points = null;
        JSONArray promo = null;
        JSONArray storeName = null;
        try {
            user = new JSONObject(info.getString("customerjson"));
            account = new JSONObject(info.getString("accountjson"));
            if(!info.getString("accountstorejson").equals("null")) {
                points = new JSONArray(info.getString("accountstorejson"));
            }
            storeName = new JSONArray(info.getString("storeInfo"));
            promo = new JSONArray(info.getString("recentpromotionjson"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(promo==null){
            promo = new JSONArray();
        }

        promotionList = new ArrayList<Promotion>();

        try {
            for(int i=0;i<promo.length();i++){
                Promotion p = new Promotion();
                p.setProId(promo.getJSONObject(i).getString("PROMOTIONID"));
                p.setStoreId(promo.getJSONObject(i).getString("SELLERID"));
                p.setTitle(promo.getJSONObject(i).getString("TITLE"));
                p.setDescription(promo.getJSONObject(i).getString("DESCRIPTION"));
                p.setUsePoint(promo.getJSONObject(i).getString("USEMEMBERPOINT"));
                p.setProIm(promo.getJSONObject(i).getString("PHOTO"));
                p.setStrDate(promo.getJSONObject(i).getString("STARTDATE"));
                p.setExpDate(promo.getJSONObject(i).getString("EXPIREDATE"));
                promotionList.add(p);
            }

            editor.putString("userId", user.getString("CUSTOMERID"));
            editor.putString("firstName", user.getString("FIRSTNAME"));
            editor.putString("lastName", user.getString("LASTNAME"));
            editor.putString("balance", account.getString("BALANCE"));
            editor.putString("accountStat", account.getString("STATUS"));
            editor.putString("imgPath", "https://user.bm-wallet.com/images/users/"+user.getString("IMAGEPATH"));
            if(!info.getString("accountstorejson").equals("null")) {
                for (int i = 0; i < points.length(); i++) {
                    editor.putString("store" + points.getJSONObject(i).getString("SELLERID"), points.getJSONObject(i).getString("MEMBERPOINT"));
                }
            }
            editor.putString("promotion", new Gson().toJson(promotionList));

            for(int i=0;i<storeName.length();i++){
                editor.putString("sn"+storeName.getJSONObject(i).getString("SELLERID"), storeName.getJSONObject(i).getString("STORENAME"));
            }

        } catch (JSONException e) {
            System.out.println("Fail here");
            e.printStackTrace();
        }
        // commit changes
        editor.commit();
    }



    private void schedAlarm() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        /*AlarmManager am = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(_context, 0, new Intent(_context, SessionReceiver.class), 0);

        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000*60*5, pi);*/
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */


    public Customer getUserDetails(){
        Customer customer = new Customer();
        customer.setUserId(pref.getString("userId","0"));
        customer.setPrefix(pref.getString("prefix","Mr."));
        customer.setFirstName(pref.getString("firstName","Firstname"));
        customer.setLastName(pref.getString("lastName","Lastname"));
        customer.setImgPath(pref.getString("imgPath","non"));

        return customer;
    }

    public Account getAccountDetails(){
        Account account = new Account();
        account.setCustomerId(pref.getString("userId", "0"));
        account.setBalance(pref.getString("balance", "0.00"));
        account.setStatus(pref.getString("accountStat", "2"));
        return account;
    }

    public ArrayList<Order> getTransaction(){
        ArrayList<Order> orders = null;
        String transJson = pref.getString("transaction","0");
        if(!transJson.equalsIgnoreCase("0")){
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Order>>(){}.getType();
            orders = (ArrayList<Order>) gson.fromJson(transJson, listType);
        }
        return orders;
    }

    public String getStorePoint(String storeId){
        String storePoint;
        storePoint = pref.getString("store"+storeId,"0");
        return storePoint;
    }

    public String getStoreName(String storeId){
        String storePoint;
        storePoint = pref.getString("sn"+storeId,"Unknown");
        return storePoint;
    }

    public void updateStorePoint(String storeId) throws JSONException {
        jObj = new JSONObject();
        jObj.put("storeID",storeId);
        jObj.put("userID",pref.getString("userId","0"));
        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/update_memberpoint.php";
        new GetPoint().execute(url);
    }

    public void updateAllStorePoint() throws JSONException {
        ArrayList<Promotion> promotion = new ArrayList<Promotion>();
        String proJson = pref.getString("promotion", "0");
        if (!proJson.equalsIgnoreCase("0")) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Promotion>>() {
            }.getType();
            promotion = (ArrayList<Promotion>) gson.fromJson(proJson, listType);

            for (int i = 0; i < promotion.size();i++) {
                jObj = new JSONObject();
                jObj.put("storeID", promotion.get(i).getStoreId());
                jObj.put("userID", pref.getString("userId", "0"));
                String url[] = new String[1];
                url[0] = "https://apps.bm-wallet.com/user/update_memberpoint.php";
                new GetPoint().execute(url);
            }
        }
    }

    public ArrayList<Promotion> getActivePromotion(){
        ArrayList<Promotion> promotion= new ArrayList<Promotion>();
        String proJson = pref.getString("promotion","0");
        if(!proJson.equalsIgnoreCase("0")){
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Promotion>>(){}.getType();
            promotion = (ArrayList<Promotion>) gson.fromJson(proJson, listType);
        }
        return promotion;
    }

    public void updateBalance() throws JSONException {
        jObj = new JSONObject();
        jObj.put("userID",pref.getString("userId","0"));
        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/update_balance.php";
        new GetBalance().execute(url);
    }

    public void updateBalancePromo() throws JSONException {
        jObj = new JSONObject();
        jObj.put("userID",pref.getString("userId","0"));
        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/update_balance.php";
        new GetBalancePromo().execute(url);
    }

    public void updateTransaction() throws JSONException {

        jObj = new JSONObject();
        jObj.put("type", "A");
        jObj.put("period", 0);
        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/user/get_history_transaction.php";
        new GetTrans().execute(url);
    }

    public void setTransaction(ArrayList<Order> orArr){
        editor.putString("transaction", new Gson().toJson(orArr));
        editor.commit();
    }



    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        AccountStorage.SetIsLoggedIn(_context,false);
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    private class GetPoint extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url = urls[0];
            SharedPreferences prefs = _context.getSharedPreferences(
                    "UserData", Context.MODE_PRIVATE);

            try {

                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId", "")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, _context);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                editor.putString("store" + json.getString("SELLERID"), json.getString("MEMBERPOINT"));
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class GetBalance extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progress = new ProgressDialog(_context);

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
            SharedPreferences prefs = _context.getSharedPreferences(
                    "UserData", Context.MODE_PRIVATE);

            try {;
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId","")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, _context);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                editor.putString("balance",json.getString("BALANCE"));
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                updateTransaction();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progress.dismiss();
            Intent intent = new Intent(_context,MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            _context.startActivity(intent);

        }



    }

    private class GetBalancePromo extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progress = new ProgressDialog(_context);



        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];
            SharedPreferences prefs = _context.getSharedPreferences(
                    "UserData", Context.MODE_PRIVATE);

            try {;
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId","")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, _context);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                editor.putString("balance",json.getString("BALANCE"));
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                updateTransaction();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



    }

    private class GetTrans extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url= urls[0];
            SharedPreferences prefs = _context.getSharedPreferences("UserData", Context.MODE_PRIVATE);

            try {
                jObj.put("cust_id", pref.getString("userId", "0"));
                jObj.put("secure", Utility.genSecureKey(prefs.getString("regId", "")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONArray json = JSONParser.postToUrlArr(url, jObj, _context);

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
                    order.setStoreName(getStoreName(transObj.getString("SELLERID")));
                    orders.add(order);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            editor.putString("transaction", new Gson().toJson(orders));
            editor.commit();
        }
    }




}
