package com.bmwallet.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Thanatkorn on 9/12/2014.
 */
public class AccountStorage {
    private static final String PREF_ACCOUNT_NUMBER = "account_number";
    private static final String PREF_IS_LOGGED_IN = "is_logged_in";
    private static final String PREF_PROMO_STORE_ID = "promo_store_id";
    private static final String DEFAULT_ACCOUNT_NUMBER = "00000000";
    private static final String PREF_ACCOUNT_BALANCE = "account_balance";
    private static final String DEFAULT_ACCOUNT_BALANCE = "0.00";
    private static final String TAG = "AccountStorage";
    private static String sAccount = null;
    private static boolean isLoggedIn = false;
    private static String promoStoreId = null;
    private static final Object sAccountLock = new Object();
    private static String accountBalance = null;

    public static void SetAccount(Context c, String s) {
        synchronized(sAccountLock) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_ACCOUNT_NUMBER, s).commit();
            sAccount = s;
        }
    }

    public static String GetAccount(Context c) {
        synchronized (sAccountLock) {
            if (sAccount == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String account = prefs.getString(PREF_ACCOUNT_NUMBER, DEFAULT_ACCOUNT_NUMBER);
                sAccount = account;
            }
            return sAccount;
        }
    }

    public static void SetIsLoggedIn(Context c, Boolean b){
        synchronized(sAccountLock) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putBoolean(PREF_IS_LOGGED_IN, b).commit();
            isLoggedIn = b;
        }
    }

    public static Boolean isLoggedIn(Context c){
        synchronized (sAccountLock) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            Boolean bIsLoggedIn = prefs.getBoolean(PREF_IS_LOGGED_IN, false);
            isLoggedIn = bIsLoggedIn;

            return isLoggedIn;
        }
    }

    public static void setAccountBalance(Context c, String b){
        synchronized (sAccountLock){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_ACCOUNT_BALANCE, b).commit();
            accountBalance = b;
        }
    }

    public static String GetAccountBalance(Context c) {
        synchronized (sAccountLock) {
            if (accountBalance == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String balance = prefs.getString(PREF_ACCOUNT_BALANCE, DEFAULT_ACCOUNT_BALANCE);
                accountBalance = balance;
            }
            return accountBalance;
        }
    }

    public static void SetPromoStoreId(Context c, String s) {
        synchronized(sAccountLock) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_PROMO_STORE_ID, s).commit();
            promoStoreId = s;
        }
    }

    public static String GetPromoStoreId(Context c) {
        synchronized (sAccountLock) {
             SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
             String storeId = prefs.getString(PREF_PROMO_STORE_ID, "0");
             promoStoreId = storeId;
            return promoStoreId;
        }
    }
}
