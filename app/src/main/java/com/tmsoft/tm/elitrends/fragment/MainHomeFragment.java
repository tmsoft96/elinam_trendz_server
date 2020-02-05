package com.tmsoft.tm.elitrends.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.Activity.AllOrdersActivity;
import com.tmsoft.tm.elitrends.Activity.AllProductActivity;
import com.tmsoft.tm.elitrends.Activity.ChatMembersActivity;
import com.tmsoft.tm.elitrends.Activity.PublicNotificationActivity;
import com.tmsoft.tm.elitrends.Activity.SettingsActivity;
import com.tmsoft.tm.elitrends.Activity.ViewAdvertisementActivity;
import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.TabsAdapter;
import com.tmsoft.tm.elitrends.Activity.AddActivity;
import com.tmsoft.tm.elitrends.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainHomeFragment extends Fragment {

    private View view;
    private RelativeLayout addProduct, showProduct, orders, notification, showAdvertisement, settings;
    private TextView notificationCounter;
    private ViewFlipper viewFlipper;


    public MainHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_b_main_home, container, false);

        addProduct = (RelativeLayout) view.findViewById(R.id.fragmentMainHome_addProduct);
        showProduct = (RelativeLayout) view.findViewById(R.id.fragmentMainHome_showProduct);
        orders = (RelativeLayout) view.findViewById(R.id.fragmentMainHome_orders);
        notification = (RelativeLayout) view.findViewById(R.id.fragmentMainHome_notification);
        showAdvertisement = (RelativeLayout) view.findViewById(R.id.fragmentMainHome_viewAdvertisement);
        settings = (RelativeLayout) view.findViewById(R.id.fragmentMainHome_settings);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.fragmentMainHome_viewFlipper);
        notificationCounter = (TextView) view.findViewById(R.id.notification_count);

        int[] images = {R.drawable.main_a, R.drawable.main_b, R.drawable.main_c};
        flipImages(images[0]);
        flipImages(images[1]);
        flipImages(images[2]);

        getNotificationCounter();

        notificationCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PublicNotificationActivity.class);
                startActivity(intent);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        showProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AllProductActivity.class);
                startActivity(intent);
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AllOrdersActivity.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PublicNotificationActivity.class);
                startActivity(intent);
            }
        });

        showAdvertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewAdvertisementActivity.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void flipImages(int images){
        final ImageView imageView = new ImageView(view.getContext());
        imageView.setBackgroundResource(images);

        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(8000);//6 sec
        viewFlipper.setAutoStart(true);

        //animation
        viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in);
        viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out);
    }

    private void getNotificationCounter() {
        notificationCounter.setVisibility(View.GONE);
        String getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId);
        dReference.keepSynced(true);
        Query query = dReference.orderByChild("read").startAt("no").endAt("no" + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    long count = dataSnapshot.getChildrenCount();
                    if (count < 1)
                        notificationCounter.setVisibility(View.GONE);
                    else{
                        if (count >= 1000 && count <= 999999){
                            long orgCount = count / 1000;
                            notificationCounter.setText(orgCount + "K");
                            notificationCounter.setVisibility(View.VISIBLE);
                        } else if (count >= 1000000){
                            long orgCount = count / 1000000;
                            notificationCounter.setText(orgCount + "M");
                            notificationCounter.setVisibility(View.VISIBLE);
                        } else {
                            notificationCounter.setText(count + "");
                            notificationCounter.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
