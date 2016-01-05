package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.SessionManager;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.datepicker.DatePicker;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.util.Date;


public class AddPromoActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener, NumberPickerDialogFragment.NumberPickerDialogHandler {

    int indi = 0;
    int period;
    TextView startDate, useMemPoint, expDate;
    Button add;
    DateTime d;
    EditText title,description;
    Promotion promotion = new Promotion();
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    JSONObject jObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_promo);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);



        startDate = (TextView)findViewById(R.id.str_date);
        useMemPoint = (TextView) findViewById(R.id.use_point);
        expDate = (TextView) findViewById(R.id.exp_date);

        useMemPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 0;
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(AddPromoActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 1;
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        add = (Button) findViewById(R.id.addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = DateTime.now();
                d = d.plusDays(period);
                promotion.setTitle(title.getText().toString());
                promotion.setDescription(description.getText().toString());
                promotion.setStoreId("1");
                jObj = new JSONObject();
                try {
                    jObj.put("title",promotion.getTitle());
                    jObj.put("description",promotion.getDescription());
                    jObj.put("storeId",promotion.getStoreId());
                    jObj.put("startDate",promotion.getExpDate());
                    jObj.put("usePoint",promotion.getUsePoint());
                    jObj.put("currentDate",now.getYear()+"/"+now.getMonthOfYear()+"/"+now.getDayOfMonth());
                    jObj.put("expDate",d.getYear()+"/"+(d.getMonthOfYear()+1)+"/"+d.getDayOfMonth());
                    jObj.put("strDate",promotion.getStrDate());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url[] = new String[1];
                url[0] = "https://secure.bm-wallet.com/add_promotion.php";
                new JSONPost().execute(url);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_promo, menu);
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

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        String sDate = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
        d = new DateTime(year,monthOfYear,dayOfMonth,0,0);
        promotion.setStrDate(sDate);
        startDate.setText(sDate);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialog calendarDatePickerDialog = (CalendarDatePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialog != null) {
            calendarDatePickerDialog.setOnDateSetListener(this);
        }
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        if(indi == 0){
            promotion.setUsePoint(number+"");
            useMemPoint.setText(number+"");
        }else {
            period = number;
            expDate.setText(number + "");
        }
    }

    private class JSONPost extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];

            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, AddPromoActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Toast.makeText(AddPromoActivity.this, "finish", Toast.LENGTH_LONG).show();

        }

    }
}
