package customer.bmwallet.com.bangmodwallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bmwallet.Class.Account;
import com.bmwallet.Class.JSONParser;
import com.bmwallet.Class.Order;
import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.PromotionArrayAdapter;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.TransactionArrayAdapter;
import com.bmwallet.Class.UpdateableFragment;
import com.bmwallet.Class.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TransactionListFragment extends Fragment implements UpdateableFragment {

    private TransactionArrayAdapter adapter;
    private ArrayList<Order> arOr;
    SessionManager ss;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);

        ss = new SessionManager(getActivity());
        arOr = ss.getTransaction();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_transaction_list, container, false);
        ListView transList = (ListView) rootView.findViewById(R.id.trans_list);
        adapter = new TransactionArrayAdapter(getActivity(), R.layout.fragment_transaction_list,arOr);
        transList.setAdapter(adapter);
        transList.setClickable(true);

        return rootView;
    }

    @Override
    public void update() {
        arOr = ss.getTransaction();
        adapter.notifyDataSetChanged();
    }



}
