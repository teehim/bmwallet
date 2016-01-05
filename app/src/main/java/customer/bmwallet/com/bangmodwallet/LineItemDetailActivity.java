package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.bmwallet.Class.LineItem;
import com.bmwallet.Class.LineItemArrayAdapter;

import java.util.ArrayList;


public class LineItemDetailActivity extends Activity {

    TextView productName,amount,total;
    LineItemArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_item_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Line Items");

        productName = (TextView) findViewById(R.id.prod_name);
        amount = (TextView) findViewById(R.id.amount);
        total = (TextView) findViewById(R.id.total);

        Bundle extras = getIntent().getExtras();
        ArrayList<LineItem> lines = (ArrayList<LineItem>) extras.getSerializable("lines");

        ListView lineList = (ListView) findViewById(R.id.line_item_list);
        adapter = new LineItemArrayAdapter(this,R.layout.line_item_list,lines);
        lineList.setAdapter(adapter);
        lineList.setClickable(true);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.line_item_detail, menu);
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
}
