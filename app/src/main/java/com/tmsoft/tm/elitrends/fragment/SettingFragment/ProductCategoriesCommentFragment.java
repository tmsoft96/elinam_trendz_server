package com.tmsoft.tm.elitrends.fragment.SettingFragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductCategoriesCommentFragment extends Fragment {

    private View view;
    private EditText buyForMe, madeInGhana, nowAvailable, orderForMe, slighlyUsed, autopart, manufacture, livestock, globalTrending;
    private Button save;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    public ProductCategoriesCommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_product_categories_comment, container, false);

        buyForMe = view.findViewById(R.id.settings_buyForMe);
        madeInGhana = view.findViewById(R.id.settings_madeInGhanaComment);
        nowAvailable = view.findViewById(R.id.settings_nowAvailableComment);
        orderForMe = view.findViewById(R.id.settings_orderForMeComment);
        slighlyUsed = view.findViewById(R.id.settings_slightlyUsedProductsComment);
        autopart = view.findViewById(R.id.settings_autopartComment);
        manufacture = view.findViewById(R.id.settings_manufactureComment);
        livestock = view.findViewById(R.id.settings_livestockComment);
        globalTrending = view.findViewById(R.id.settings_globalTrendingComment);
        save = view.findViewById(R.id.setting_categoryButton);
        progressDialog = new ProgressDialog(view.getContext());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category Details");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buy = buyForMe.getText().toString();
                String made = madeInGhana.getText().toString();
                String now = nowAvailable.getText().toString();
                String order = orderForMe.getText().toString();
                String slightly = slighlyUsed.getText().toString();
                String auto = autopart.getText().toString();
                String manu = manufacture.getText().toString();
                String live = livestock.getText().toString();
                String global = globalTrending.getText().toString();

                saveCategory(buy, made, now, order, slightly, auto, manu, live, global);
            }
        });

        showDetails();

        return view;
    }

    private void showDetails() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Buy For Me")){
                        String gg = dataSnapshot.child("Buy For Me").getValue().toString();
                        buyForMe.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Made In Ghana")){
                        String gg = dataSnapshot.child("Made In Ghana").getValue().toString();
                        madeInGhana.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Now Available")){
                        String gg = dataSnapshot.child("Now Available").getValue().toString();
                        nowAvailable.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Order For Me")){
                        String gg = dataSnapshot.child("Order For Me").getValue().toString();
                        orderForMe.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Slightly Used Product")){
                        String gg = dataSnapshot.child("Slightly Used Product").getValue().toString();
                        slighlyUsed.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Auto Parts")){
                        String gg = dataSnapshot.child("Auto Parts").getValue().toString();
                        autopart.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Manufacturers")){
                        String gg = dataSnapshot.child("Manufacturers").getValue().toString();
                        manufacture.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Livestock")){
                        String gg = dataSnapshot.child("Livestock").getValue().toString();
                        livestock.setText(gg);
                    }

                    if (dataSnapshot.hasChild("Global Trending")){
                        String gg = dataSnapshot.child("Global Trending").getValue().toString();
                        globalTrending.setText(gg);
                    }
                } else
                    Toast.makeText(view.getContext(), "Please add category details", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveCategory(String buy, String made, String now, String order, String slightly, String auto, String manu, String live, String global) {
        progressDialog.setMessage("Saving Details");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Map saveMap = new HashMap();
        saveMap.put("Buy For Me", buy);
        saveMap.put("Made In Ghana", made);
        saveMap.put("Now Available", now);
        saveMap.put("Order For Me", order);
        saveMap.put("Slightly Used Product", slightly);
        saveMap.put("Auto Parts", auto);
        saveMap.put("Manufacturers", manu);
        saveMap.put("Livestock", live);
        saveMap.put("Global Trending", global);
        databaseReference.updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(view.getContext(), "Save Successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(view.getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}
