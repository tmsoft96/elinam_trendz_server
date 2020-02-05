package com.tmsoft.tm.elitrends.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.R;

public class UserSettingsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private LinearLayout relAboutUs, relLogOut, relOurPolicy, relFeedback;
    private Dialog dialog, creditDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference contactReference;
    private String getCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        contactReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");

        myToolBar = (Toolbar) findViewById(R.id.userSettings_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        relAboutUs = (LinearLayout) findViewById(R.id.userSettings_relAboutUs);
        relLogOut = (LinearLayout) findViewById(R.id.userSettings_relLogOut);
        relFeedback = (LinearLayout) findViewById(R.id.userSettings_relFeedback);
        relOurPolicy = (LinearLayout) findViewById(R.id.userSettings_relOurPolicy);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        creditDialog = new Dialog(this, R.style.Theme_CustomDialog);
        
        relAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToAboutUsActivity();
            }
        });

        relLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendUserToLogInActivity();
            }
        });

        relFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSettingsActivity.this, FeedBackActivity.class);
                intent.putExtra("userId", getCurrentUserId);
                startActivity(intent);
            }
        });

        relOurPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewPolicyActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToLogInActivity(){
        Intent intent = new Intent(UserSettingsActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToViewPolicyActivity(){
        Intent intent = new Intent(UserSettingsActivity.this, ViewPolicyActivity.class);
        startActivity(intent);
    }

    private void sendUserToAboutUsActivity(){
        Intent intent = new Intent(UserSettingsActivity.this, AboutUsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
