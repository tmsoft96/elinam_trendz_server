package com.tmsoft.tm.elitrends.fragment.HomeFragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.Holders.autofit;
import com.tmsoft.tm.elitrends.R;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.BulkPurchaseFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.CarCareFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.GlobalTrendingFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.MadeInGhanaFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.ManufacturesFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.NowAvailableFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.SlightlyUsedProductsFragment;
import com.tmsoft.tm.elitrends.fragment.InnerFragment.YourLocalMarketFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    private View view;
    private LinearLayout shopNowAvailable, shopMadeInGhana, shopManufactures, shopBulkPurchase,
            shopYourLocalMarket, shopSlightlyUsed, shopCarCare, shopGlobalTrending;
    private RelativeLayout relativeLayout;
    private ImageView back;

    private FragmentManager fragmentManager;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_categories, container, false);

        ImageView nowAvailable = (ImageView) view.findViewById(R.id.cat_nowAvailable);
        ImageView madeInGhana = (ImageView) view.findViewById(R.id.cat_madeInGhana);
        ImageView manufacture = (ImageView) view.findViewById(R.id.cat_manufactures);
        ImageView bulkPurchase = (ImageView) view.findViewById(R.id.cat_bulkPurchase);
        ImageView yourLocalMarket = (ImageView) view.findViewById(R.id.cat_yourLocalMarket);
        ImageView slightlyUsed = (ImageView) view.findViewById(R.id.cat_slightlyUsedProduct);
        ImageView carCare = (ImageView) view.findViewById(R.id.cat_carCare);
        ImageView globalTrending = (ImageView) view.findViewById(R.id.cat_globalTrending);

        shopNowAvailable = (LinearLayout) view.findViewById(R.id.cat_pageNowAvailable);
        shopMadeInGhana = (LinearLayout) view.findViewById(R.id.cat_pageMadeInGhana);
        shopManufactures = (LinearLayout) view.findViewById(R.id.cat_pageManufactures);
        shopBulkPurchase = (LinearLayout) view.findViewById(R.id.cat_pageBulkPurchase);
        shopYourLocalMarket = (LinearLayout) view.findViewById(R.id.cat_pageYourLocalMarket);
        shopSlightlyUsed = (LinearLayout) view.findViewById(R.id.cat_pageSlightlyUsed);
        shopCarCare = (LinearLayout) view.findViewById(R.id.cat_pageCarCare);
        shopGlobalTrending = (LinearLayout) view.findViewById(R.id.cat_pageGlobalTrending);

        autofit noColums = new autofit();
        noColums.autofit(view.getContext());

        int width = shopNowAvailable.getLayoutParams().width;
        int height = shopNowAvailable.getLayoutParams().height;
        Log.i("width", width+ "");

        shopNowAvailable.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopMadeInGhana.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopManufactures.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopBulkPurchase.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopYourLocalMarket.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopSlightlyUsed.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopCarCare.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));
        shopGlobalTrending.setLayoutParams(new LinearLayout.LayoutParams(noColums.getLayoutWidth(), height));

        relativeLayout = (RelativeLayout) view.findViewById(R.id.category_relative);
        back = (ImageView) view.findViewById(R.id.categories_back);

        relativeLayout.setVisibility(View.GONE);

        fragmentManager = getFragmentManager();

        back.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });

        shopNowAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                NowAvailableFragment nowAvailableFragment = new NowAvailableFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, nowAvailableFragment,
                        nowAvailableFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopMadeInGhana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                MadeInGhanaFragment madeInGhanaFragment = new MadeInGhanaFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, madeInGhanaFragment,
                        madeInGhanaFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopManufactures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                ManufacturesFragment manufacturesFragment = new ManufacturesFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, manufacturesFragment,
                        manufacturesFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopBulkPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                BulkPurchaseFragment bulkPurchaseFragment = new BulkPurchaseFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, bulkPurchaseFragment,
                        bulkPurchaseFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopYourLocalMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                YourLocalMarketFragment yourLocalMarketFragment = new YourLocalMarketFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, yourLocalMarketFragment,
                        yourLocalMarketFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopSlightlyUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                SlightlyUsedProductsFragment slightlyUsedProductsFragment = new SlightlyUsedProductsFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, slightlyUsedProductsFragment,
                        slightlyUsedProductsFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopCarCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                CarCareFragment carCareFragment = new CarCareFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, carCareFragment,
                        carCareFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });

        shopGlobalTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                GlobalTrendingFragment globalTrendingFragment = new GlobalTrendingFragment();
                fragmentManager.beginTransaction().replace(R.id.category_relative, globalTrendingFragment,
                        globalTrendingFragment.getTag()).commit();
                back.setVisibility(View.VISIBLE);
            }
        });


        nowAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Now Available");
            }
        });

        madeInGhana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Made In Ghana");
            }
        });

        manufacture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Manufacturers");
            }
        });

        bulkPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Order For Me");
            }
        });

        yourLocalMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Livestock");
            }
        });

        slightlyUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Slightly Used Product");
            }
        });

        carCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Auto Parts");
            }
        });

        globalTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDetail("Global Trending");
            }
        });

        return view;
    }

    private void showCategoryDetail(final String name){
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category Details");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(name)){
                        String gg = dataSnapshot.child(name).getValue().toString();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                        alertDialog.setMessage(gg)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //User finish reading
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    } else
                        Toast.makeText(view.getContext(), "Error occurred...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
