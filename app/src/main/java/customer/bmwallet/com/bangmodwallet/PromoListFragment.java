package customer.bmwallet.com.bangmodwallet;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bmwallet.Class.Promotion;
import com.bmwallet.Class.PromotionArrayAdapter;
import com.bmwallet.Class.SessionManager;
import com.bmwallet.Class.UpdateableFragment;

import java.util.ArrayList;


public class PromoListFragment extends Fragment implements UpdateableFragment {

    private PromotionArrayAdapter adapter;
    private ArrayList<Promotion> arPro;
    SessionManager ss;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);



        ss = new SessionManager(getActivity());
        arPro = ss.getActivePromotion();


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_promo_list, container, false);
        ListView promoList = (ListView) rootView.findViewById(R.id.promo_list);
        adapter = new PromotionArrayAdapter(getActivity(), R.layout.fragment_promo_list,arPro);
        promoList.setAdapter(adapter);
        promoList.setClickable(true);

        return rootView;
    }

    @Override
    public void update() {
        adapter.notifyDataSetChanged();
    }









}
