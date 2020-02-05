package com.tmsoft.tm.elitrends.Adapters.TabsAdapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tmsoft.tm.elitrends.fragment.Add.AddShopProductFragment;
import com.tmsoft.tm.elitrends.fragment.Add.SingleProductFragment;
import com.tmsoft.tm.elitrends.fragment.OrderFragment.CartOrderFragment;
import com.tmsoft.tm.elitrends.fragment.OrderFragment.OrderFragment;

public class AddTabsAdapter extends FragmentPagerAdapter {
    private String[] tabTitle = new String[]{"Your Products", "Shop Products"};
    Context context;

    public AddTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                SingleProductFragment single = new SingleProductFragment();
                return single;

            case 1 :
                AddShopProductFragment shop = new AddShopProductFragment();
                return shop;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
