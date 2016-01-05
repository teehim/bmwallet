package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bmwallet.Class.AccountStorage;
import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.Utility;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class LoginActivity extends Activity {

    EditText userText, passText;
    Button loginBtn;
    String username,password;
    JSONObject jObj = new JSONObject();
    GoogleCloudMessaging gcm;
    Context context;
    String regId;
    SessionManager ss;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    static final String TAG = "Login Activity";
    private static final String PREF_NAME = "UserData";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        ss = new SessionManager(this);

        userText = (EditText)findViewById(R.id.username);
        passText = (EditText)findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.loginBtn);

        context = getApplicationContext();


        if(AccountStorage.isLoggedIn(this)){
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);
            finish();
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                if (TextUtils.isEmpty(regId)) {
                    regId = registerGCM();
                    Log.d("RegisterActivity", "GCM RegId: " + regId);

                } else {
                }

                if ((!TextUtils.isEmpty(userText.getText().toString()) && !TextUtils.isEmpty(passText.getText().toString()))) {

                    try {
                        username = userText.getText().toString().trim();
                        password = passText.getText().toString().trim();
                        if (username != null && password != null) {
                            jObj = new JSONObject();
                            jObj.put("username", username);
                            jObj.put("password", password);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url[] = new String[1];
                    url[0] = "https://apps.bm-wallet.com/user/user_login.php";
                    new JSONPost().execute(url);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please Insert Username and Password.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }


        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

        ProgressDialog progress = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            progress.setMessage("Logging In...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];
            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, LoginActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json.getString("status").equalsIgnoreCase("1")) {
                    SessionManager ss = new SessionManager(LoginActivity.this);
                    ss.createLoginSession(json);
                    AccountStorage.SetAccount(LoginActivity.this, username);
                    AccountStorage.SetIsLoggedIn(LoginActivity.this, true);
                    progress.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainPage.class);
                    startActivity(intent);
                    finish();

                }else{
                    progress.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Wrong Username or Password.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private class SendRegId extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url= urls[0];

            JSONObject regJObj = new JSONObject();

            try {
                regJObj.put("gcmRegID",regId);
                regJObj.put("userID",userText.getText().toString().trim());
                regJObj.put("brand",Build.MANUFACTURER);
                regJObj.put("model",Build.MODEL);
                regJObj.put("api",Build.VERSION.RELEASE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONParser.postToUrlVoid(url, regJObj, LoginActivity.this);


            return null;
        }


    }

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity","registerGCM - successfully registered with GCM server - regId: " + regId);
        } else {
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity","I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void,String,String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "+ regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                String url[] = new String[1];
                url[0] = Config.APP_SERVER_URL;
                new SendRegId().execute(url);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

}

