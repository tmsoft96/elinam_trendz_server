package com.tmsoft.tm.elitrends.Adapters.TabsAdapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tmsoft.tm.elitrends.fragment.PolicyFragment.ViewDeliveryPolicyFragment;
import com.tmsoft.tm.elitrends.fragment.PolicyFragment.ViewPaymentPolicyFragment;
import com.tmsoft.tm.elitrends.fragment.PolicyFragment.ViewReturnPolicyFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails.BusTerminalFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails.PersonnelDeliveryFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails.PostDeliveryFragment;

public class PolicyTabsAdapter extends FragmentPagerAdapter {
    private String[] tabTitile = new String[]{"Our Delivery Policy", "Our Payment Policy", "Our Return Policy"};
    Context context;

    public PolicyTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new ViewDeliveryPolicyFragment();

            case 1 :
                return new ViewPaymentPolicyFragment();

            case 2 :
                return new ViewReturnPolicyFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitile[position];
    }
}
