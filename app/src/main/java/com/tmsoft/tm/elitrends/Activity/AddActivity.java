package com.tmsoft.tm.elitrends.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.AddTabsAdapter;
import com.tmsoft.tm.elitrends.R;

public class AddActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = (Toolbar) findViewById(R.id.addMain_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Products");

        //Tabs in main activity
        myViewPager = (ViewPager) findViewById(R.id.addMain_viewPager);
        myViewPager.setAdapter(new AddTabsAdapter(getSupportFragmentManager(), this));
        myTabLayout = (TabLayout) findViewById(R.id.addMain_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
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