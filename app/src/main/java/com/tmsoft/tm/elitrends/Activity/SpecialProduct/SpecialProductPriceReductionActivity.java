package com.tmsoft.tm.elitrends.Activity.SpecialProduct;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tmsoft.tm.elitrends.Adapters.SpecialProductAdapter;
import com.tmsoft.tm.elitrends.Holders.SpecialProductClass;
import com.tmsoft.tm.elitrends.R;

import java.util.ArrayList;
import java.util.List;

public class SpecialProductPriceReductionActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;

    private final List<SpecialProductClass> specialProductClassList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private SpecialProductAdapter specialProductAdapter;

    private DatabaseReference onlineReference, databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_product_price_reduction);

        myToolBar = (Toolbar) findViewById(R.id.priceReduction_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.p_price_reduction);
        getSupportActionBar().setTitle("Price Reduction Packages");

        onlineReference = FirebaseDatabase.getInstance().getReference().child("OnlineUser");
        onlineReference.keepSynced(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child("Price Reduction Packages");
        databaseReference.keepSynced(true);

        int mNoofColums = autofit(getApplicationContext());

        specialProductAdapter = new SpecialProductAdapter(specialProductClassList, this);

        recyclerView = (RecyclerView) findViewById(R.id.priceReduction_recycler);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), mNoofColums);
        gridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(specialProductAdapter);

        showItems();
    }

    private void showItems() {
        specialProductClassList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SpecialProductClass specialProductClass = dataSnapshot.getValue(SpecialProductClass.class);
                specialProductClassList.add(specialProductClass);
                specialProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int autofit(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 140);
        return noOfColumns;
    }

    private void saveOnlineUser(String status) {
        onlineReference.child("serverStatus").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(SpecialProductPriceReductionActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            saveOnlineUser("offline");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
