package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.StorePoint;
import com.bmwallet.Class.StorePointArrayAdapter;

import java.util.ArrayList;


public class StorePointFragment extends Fragment {
    private StorePointArrayAdapter adapter;
    private ArrayList<StorePoint> arOr;
    SessionManager ss;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);



        ss = new SessionManager(getActivity());


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_transaction_list, container, false);
        ListView promoList = (ListView) rootView.findViewById(R.id.trans_list);
        adapter = new StorePointArrayAdapter(getActivity(), R.layout.fragment_transaction_list,arOr);
        promoList.setAdapter(adapter);
        promoList.setClickable(true);

        return rootView;
    }

}
