package com.tmsoft.tm.elitrends.fragment.SettingFragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.Setting.DeliveryTabsAdapter;
import com.tmsoft.tm.elitrends.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryDetailsFragment extends Fragment {

    private View view;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    public DeliveryDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_delivery_details, container, false);

        //Tabs in main activity
        myViewPager = view.findViewById(R.id.deliveryDetails_viewPager);
        myViewPager.setAdapter(new DeliveryTabsAdapter(getChildFragmentManager(), view.getContext()));
        myTabLayout = view.findViewById(R.id.deliveryDetails_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        return view;
    }

}
