package com.tmsoft.tm.elitrends.Adapters.TabsAdapter.Setting;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tmsoft.tm.elitrends.fragment.SettingFragment.ContactDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.PaymentDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.PolicyFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.ProductCategoriesCommentFragment;

public class SettingTabsAdapter extends FragmentPagerAdapter {
    private String[] tabTitile = new String[]{"Contact Details", "Payment Details", "Product Category Details",
            "Delivery Details", "Policy Details"};
    Context context;

    public SettingTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                ContactDetailsFragment contact = new ContactDetailsFragment();
                return contact;

            case 1 :
                PaymentDetailsFragment payment = new PaymentDetailsFragment();
                return payment;

            case 2 :
                ProductCategoriesCommentFragment categories = new ProductCategoriesCommentFragment();
                return categories;

            case 3 :
                DeliveryDetailsFragment delivery = new DeliveryDetailsFragment();
                return delivery;

            case 4:
                PolicyFragment policy = new PolicyFragment();
                return policy;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitile[position];
    }
}
