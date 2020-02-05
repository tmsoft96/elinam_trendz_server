package com.tmsoft.tm.elitrends.fragment;


import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import com.tmsoft.tm.elitrends.R;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.ContactDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.PaymentDetailsFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.PolicyFragment;
import com.tmsoft.tm.elitrends.fragment.SettingFragment.ProductCategoriesCommentFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeSettingFragment extends Fragment {

    private View view;
    private RelativeLayout relativeLayout, contactDetails, paymentDetails, productCategoryDetails,
            deliveryDetails, policyDetails;
    private ImageView back;
    private ViewFlipper viewFlipper;
    private ScrollView scrollView;
    private FragmentManager fragmentManager;

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    public HomeSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_b_home_setting, container, false);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.fragmentMainSetting_relativeLayout);
        contactDetails = (RelativeLayout) view.findViewById(R.id.fragmentMainSetting_contactDetails);
        paymentDetails = (RelativeLayout) view.findViewById(R.id.fragmentMainSetting_paymentDetails);
        productCategoryDetails = (RelativeLayout) view.findViewById(R.id.fragmentMainSetting_productCategoryDetails);
        deliveryDetails = (RelativeLayout) view.findViewById(R.id.fragmentMainSetting_deliveryDetails);
        policyDetails = (RelativeLayout) view.findViewById(R.id.fragmentMainSetting_policyDetails);
        back = (ImageView) view.findViewById(R.id.fragmentMainSetting_back);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.fragmentMainSetting_viewFlipper);
        scrollView = (ScrollView) view.findViewById(R.id.fragmentmainSetting_scrollView);

        fragmentManager = getFragmentManager();

        back.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);

        int[] images = {R.drawable.main_setting_a, R.drawable.main_setting_b, R.drawable.main_setting_c, R.drawable.main_setting_d};
        for (int image : images){
            flipImages(image);
        }

        contactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactDetailsFragment contact = new ContactDetailsFragment();
                setFragment(contact);
            }
        });

        paymentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDetailsFragment payment = new PaymentDetailsFragment();
                setFragment(payment);
            }
        });

        productCategoryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCategoriesCommentFragment categories = new ProductCategoriesCommentFragment();
                setFragment(categories);
            }
        });

        deliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryDetailsFragment delivery = new DeliveryDetailsFragment();
                setFragment(delivery);
            }
        });

        policyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PolicyFragment policy = new PolicyFragment();
                setFragment(policy);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                viewFlipper.setVisibility(View.VISIBLE);
            }
        });

        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("Drag", "Activated");
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());

                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

                //Instantiates the drag shadow builder.
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(back);

                //Starts the drag
                v.startDrag(dragData, myShadow, null, 0);

                return true;
            }
        });

        back.setTag("Drag");
        back.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        Log.i("Drag", "Action is DragEvent.ACTION_DRAG_STARTED");
                        //Do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i("Drag", "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i("Drag", "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.i("Drag", "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i("Drag", "Action is DragEvent.ACTION_DRAG_ENDED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        Log.i("Drag", "x_cord = " + x_cord + " y_cord = " + y_cord);
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        //Do nothing
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.i("Drag", "Action is DragEvent.ACTION_DRAG_DROP");
                        //Do nothing
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        return view;
    }

    private void setFragment(Fragment fragment){
        fragmentManager.beginTransaction().replace(R.id.fragmentMainSetting_relativeLayout, fragment,
                fragment.getTag()).commit();
        back.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        viewFlipper.setVisibility(View.GONE);
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

}
