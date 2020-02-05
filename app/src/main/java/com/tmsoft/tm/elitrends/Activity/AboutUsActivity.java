package com.tmsoft.tm.elitrends.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.R;

public class AboutUsActivity extends AppCompatActivity {

    private TextView year, license, contactUs, credits;
    private ImageView facebook, twitter, telegram;
    private Toolbar myToolBar;
    private Dialog dialog;

    private DatabaseReference contactReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        year = (TextView) findViewById(R.id.aboutUs_year);
        license = (TextView) findViewById(R.id.aboutUs_license);
        contactUs = (TextView) findViewById(R.id.aboutUs_contactUs);
        credits = (TextView) findViewById(R.id.aboutUs_credits);
        facebook = (ImageView) findViewById(R.id.aboutUs_facebook);
        telegram = (ImageView) findViewById(R.id.aboutUs_telegram);
        twitter = (ImageView) findViewById(R.id.aboutUs_twitter);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);

        contactReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        contactReference.keepSynced(true);

        myToolBar = (Toolbar) findViewById(R.id.aboutUs_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About Us");

        year.setText("@2018-2019");

        license.setPaintFlags(license.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        contactUs.setPaintFlags(contactUs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        credits.setPaintFlags(credits.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLincenseActivity();
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreditDialog();
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactUsDialog();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri("http://www.facebook.com/elinamtrendz/");
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri("http://www.twitter.com/ELINAM_TRENDZGH?s=80.com");
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri("http://t.me/joinchat/AAAAAFQgF57bHS35hV7E0w");
            }
        });

    }

    private void getUri(String s) {
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showContactUsDialog() {
        final TextView close, phoneNumber, emailAddress, location;

        dialog.setContentView(R.layout.dialog_contact_us);
        close = dialog.findViewById(R.id.contactUs_close);
        phoneNumber = dialog.findViewById(R.id.contactUs_phoneNumber);
        emailAddress = dialog.findViewById(R.id.contactUs_email);
        location = dialog.findViewById(R.id.contactUs_location);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        contactReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("PhoneNumber")){
                        String num = dataSnapshot.child("PhoneNumber").getValue().toString();
                        phoneNumber.setText(num);
                    }
                    if (dataSnapshot.hasChild("EmailAddress")){
                        String email = dataSnapshot.child("EmailAddress").getValue().toString();
                        emailAddress.setText(email);
                    }
                    if (dataSnapshot.hasChild("Location")){
                        String loc = dataSnapshot.child("Location").getValue().toString();
                        location.setText(loc);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.show();
    }

    private void showCreditDialog() {
        TextView close, year;

        dialog.setContentView(R.layout.dialog_credits);
        close = dialog.findViewById(R.id.credits_close);
        year = dialog.findViewById(R.id.credits_year);

        year.setText("@2018 - 2019");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendUserToLincenseActivity() {
        Intent intent = new Intent(AboutUsActivity.this, AppInfoActivity.class);
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
