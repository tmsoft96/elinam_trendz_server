package com.tmsoft.tm.elitrends.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.OrderAdapter;
import com.tmsoft.tm.elitrends.R;

public class AllOrdersActivity extends AppCompatActivity {

    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        //Tabs in main activity
        myViewPager = findViewById(R.id.mainOrder_viewPager);
        myViewPager.setAdapter(new OrderAdapter(getSupportFragmentManager(),this));
        myTabLayout = findViewById(R.id.mainOrder_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        Toolbar myToolBar = (Toolbar) findViewById(R.id.mainOrder_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Orders");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
