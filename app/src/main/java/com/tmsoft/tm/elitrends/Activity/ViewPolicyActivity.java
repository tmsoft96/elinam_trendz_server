package com.tmsoft.tm.elitrends.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.PolicyTabsAdapter;
import com.tmsoft.tm.elitrends.R;

public class ViewPolicyActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_policy);

        myToolBar = (Toolbar) findViewById(R.id.viewPolicy_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Our Policy");

        //Tabs in main activity
        myViewPager = (ViewPager) findViewById(R.id.viewPolicy_viewPager);
        myViewPager.setAdapter(new PolicyTabsAdapter(getSupportFragmentManager(), this));
        myTabLayout = (TabLayout) findViewById(R.id.viewPolicy_tabs);
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
