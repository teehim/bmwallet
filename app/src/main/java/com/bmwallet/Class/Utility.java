package com.bmwallet.Class;

import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by Thanatkorn on 8/20/2014.
 */
public class Utility {

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String genSecureKey(String key){
        DateTime date = DateTime.now();
        System.out.println(key);
        int index = date.getDayOfMonth()+date.getMonthOfYear();
        String secKey = md5(key.substring(index,index+8));
        return secKey;
    }
}
