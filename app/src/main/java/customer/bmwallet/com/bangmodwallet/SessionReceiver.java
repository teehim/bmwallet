package customer.bmwallet.com.bangmodwallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bmwallet.Class.AccountStorage;

public class SessionReceiver extends BroadcastReceiver {

    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserData";

    public SessionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        AccountStorage.SetIsLoggedIn(context,false);
        AccountStorage.SetAccount(context,"00000000");

        Intent i = new Intent(context,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        System.out.println("Delete Session");
    }
}