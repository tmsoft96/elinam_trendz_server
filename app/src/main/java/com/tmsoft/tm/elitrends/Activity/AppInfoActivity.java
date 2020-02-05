package com.tmsoft.tm.elitrends.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.tmsoft.tm.elitrends.R;

public class AppInfoActivity extends AppCompatActivity {

    private RelativeLayout term, license;
    private Toolbar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        term = (RelativeLayout) findViewById(R.id.app_term_and_condition);
        license = (RelativeLayout) findViewById(R.id.app_license);

        myToolBar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("App Info");

        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToTermActivity();
            }
        });

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLicenseActivity();
            }
        });
    }

    private void sendUserToLicenseActivity() {
        Intent intent = new Intent(AppInfoActivity.this, LicenseActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToTermActivity() {
        Intent intent = new Intent(AppInfoActivity.this, AboutAppUsageLicense.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
