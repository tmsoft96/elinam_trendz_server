package com.tmsoft.tm.elitrends.Adapters.TabsAdapter.Setting;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tmsoft.tm.elitrends.fragment.SettingFragment.ContactDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails.BusTerminalFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails.PersonnelDeliveryFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails.PostDeliveryFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.PaymentDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.ProductCategoriesCommentFragment;

public class DeliveryTabsAdapter extends FragmentPagerAdapter {
    private String[] tabTitile = new String[]{"Personnel Delivery", "Post Box Delivery", "Bus Terminal Delivery"};
    Context context;

    public DeliveryTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                PersonnelDeliveryFragment personnel = new PersonnelDeliveryFragment();
                return personnel;

            case 1 :
                PostDeliveryFragment post = new PostDeliveryFragment();
                return post;

            case 2 :
                BusTerminalFragment bus = new BusTerminalFragment();
                return bus;

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
