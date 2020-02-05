package com.tmsoft.tm.elitrends.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;

import com.tmsoft.tm.elitrends.Adapters.TabsAdapter.Setting.SettingTabsAdapter;
import com.tmsoft.tm.elitrends.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        //Tabs in main activity
        myViewPager = (ViewPager) findViewById(R.id.setting_viewPager);
        myViewPager.setAdapter(new SettingTabsAdapter(getSupportFragmentManager(), this));
        myTabLayout = (TabLayout) findViewById(R.id.setting_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

   /* private void sendUserToMainActivity(){
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }*/
}
