package com.tmsoft.tm.elitrends.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.TabsAdapter;
import com.tmsoft.tm.elitrends.R;

public class AllProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        //Tabs in main activity
        ViewPager myViewPager = findViewById(R.id.main_viewPager);
        myViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(),this));
        TabLayout myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        Toolbar myToolBar = (Toolbar) findViewById(R.id.allProduct_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Show Product");
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
