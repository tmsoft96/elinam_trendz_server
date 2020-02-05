package com.tmsoft.tm.elitrends.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rerlanggas.lib.ExceptionHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.R;
import com.tmsoft.tm.elitrends.fragment.HomeSettingFragment;
import com.tmsoft.tm.elitrends.fragment.MainHomeFragment;
import com.tmsoft.tm.elitrends.fragment.UsersDetailsFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 0;
    private FragmentManager fragmentManager;
    private Dialog dialog;
    private Context context;
    private View notificationBadge;
    private Toolbar myToolBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView profilePicture;
    private ImageView facebook, twitter, telegram, search, editProfile;
    private TextView details;

    private DatabaseReference myIdRef;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, contactReference, onlineReference, tokenRef, agreementReference;
    private String getCurrentUserId;
    private String notifySender1 = "empty", notifySender2 = "empty", notifySender3 = "empty";

    private String uProfilePicture;
    private long counter = 0;

    private View view;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //catching error
        ExceptionHandler.init(this, MainActivity.class);

        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.context = this;

        /*//order notification
        Intent orderAlarm = new Intent(this.context, OrderNotificationReceiver.class);
        Boolean orderAlarmRunning = (PendingIntent.getBroadcast(this.context, 0, orderAlarm, PendingIntent.FLAG_NO_CREATE) != null);
        if (orderAlarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, orderAlarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 10000, pendingIntent);
        }

        //chat notification
        Intent chatAlarm = new Intent(this.context, ChatNotificationReceiver.class);
        Boolean chatAlarmRunning = (PendingIntent.getBroadcast(this.context, 0, chatAlarm, PendingIntent.FLAG_NO_CREATE) != null);
        if (chatAlarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, chatAlarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 15000, pendingIntent);
        }*/

        //Public Notification
        /*Intent publicAlarm = new Intent(this.context, PublicNotificationReceiver.class);
        Boolean publicAlarmRunning = (PendingIntent.getBroadcast(this.context, 0, publicAlarm, PendingIntent.FLAG_NO_CREATE) != null);
        if (publicAlarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, publicAlarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 15000, pendingIntent);
        }*/

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        contactReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        contactReference.keepSynced(true);
        myIdRef = FirebaseDatabase.getInstance().getReference().child("ServerId");
        myIdRef.keepSynced(true);
        onlineReference = FirebaseDatabase.getInstance().getReference().child("OnlineUser");
        onlineReference.keepSynced(true);
        tokenRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken").child(getCurrentUserId);
        tokenRef.keepSynced(true);
        agreementReference = FirebaseDatabase.getInstance().getReference().child("AgreementTerms").child(getCurrentUserId);
        agreementReference.keepSynced(true);

        dialog = new Dialog(this, R.style.Theme_CustomDialog);

        search = (ImageView) findViewById(R.id.main_search);
        myToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        navigationView = (NavigationView) findViewById(R.id.main_slideNavigation);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_slide_header);
        profilePicture = (CircleImageView) navView.findViewById(R.id.nav_profilePicture);
        facebook = (ImageView) navView.findViewById(R.id.nav_facebook);
        twitter = (ImageView) navView.findViewById(R.id.nav_twitter);
        telegram = (ImageView) navView.findViewById(R.id.nav_telegram);
        details = (TextView) navView.findViewById(R.id.nav_details);
        editProfile = (ImageView) navView.findViewById(R.id.nav_editProfile);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        closeKeyBoard();
        checkIfAuthorized();
        checkForManifestPermission();
        checkTermsAndAgreement();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                MenuSelected(menuItem);
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToSearchActivity();
            }
        });

    }

    private void checkForManifestPermission() {
        ArrayList<String> arrPerm = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.CALL_PHONE);
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.RECORD_AUDIO);
        }

        if(!arrPerm.isEmpty()){
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        new Thread(){
            @Override
            public void run() {

            }
        }.start();


        int actionPerform = event.getAction();
        switch (actionPerform){
            case MotionEvent.ACTION_DOWN :
                myToolBar.setVisibility(View.VISIBLE);
                Log.i("action", "down");
                break;
            case MotionEvent.ACTION_UP :
                myToolBar.setVisibility(View.GONE);
                Log.i("action", "down");
                break;
        }

        return true;
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initializeSocialMedia() {
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

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUri(String s) {
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void MenuSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_usersFeedback :
                Intent intent = new Intent(MainActivity.this, FeedBackActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_viewPolicies :
                Intent intent2 = new Intent(MainActivity.this, ViewPolicyActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_aboutUs :
                Intent intent1 = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent1);
                break;

            case R.id.nav_shareClient :
                shareApp();
                break;

            case R.id.nav_reportIssues :
                Intent intent3 = new Intent(MainActivity.this, ReportIssuesActivity.class);
                startActivity(intent3);
                break;

            case R.id.nave_logOut :
                mAuth.signOut();
                sendUserToLogInActivity();
                break;

            case R.id.nav_chat :
                Intent intent4 = new Intent(MainActivity.this, ChatMembersActivity.class);
                startActivity(intent4);
                break;
        }
    }

    private void shareApp() {
        DatabaseReference deReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        deReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("AppLink")){
                        String link = dataSnapshot.child("AppLink").getValue().toString();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String shareBody = link;
                        String shareSub = "Elinam Trendz";
                        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(intent, "Share using ..."));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkTermsAndAgreement() {
        agreementReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("terms")){
                        String terms = dataSnapshot.child("terms").getValue().toString();
                        if (terms.equalsIgnoreCase("yes")){
                            Log.i("termsAgreement", "yes");
                            showDetails();
                            saveAllServerId();
                            initializeSocialMedia();

                            String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                            tokenRef.child("device_token").setValue(DeviceToken);
                        }
                        else{
                            Log.i("termsAgreement", "no");
                            showTermsAndAgreementDialog();
                        }
                    }
                } else {
                    showTermsAndAgreementDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showTermsAndAgreementDialog() {
        Button accept;
        TextView text;

        dialog.setContentView(R.layout.dialog_term_and_agreement);
        accept = dialog.findViewById(R.id.dialogTerms_accept);
        text = dialog.findViewById(R.id.dialogTerms_text);

        try {
            InputStream stream = getAssets().open("terms.txt");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String tContents = new String(buffer);
            text.setText(tContents);
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agreementReference.child("terms").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void saveAllServerId() {
        DatabaseReference serverRef = FirebaseDatabase.getInstance().getReference().child("AllServerId");
        serverRef.keepSynced(true);

        serverRef.child(getCurrentUserId).setValue(getCurrentUserId);
    }

    private void checkIfAuthorized(){
        final DatabaseReference notifySender = FirebaseDatabase.getInstance().getReference().child("ServerId");
        notifySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("0")){
                        notifySender1 = dataSnapshot.child("0").getValue().toString();
                        saveOnlineUser();

                    } else {
                        counter = dataSnapshot.getChildrenCount();
                        if (counter == 0 && notifySender1.equals("empty")){
                            myIdRef.child("0").setValue(getCurrentUserId);
                        }
                    }

                    if (dataSnapshot.hasChild("1")){
                        notifySender2 = dataSnapshot.child("1").getValue().toString();
                        saveOnlineUser();
                    } else {
                        counter = dataSnapshot.getChildrenCount();
                        if (counter == 1 && notifySender2.equals("empty") && !notifySender1.equals(getCurrentUserId)){
                            myIdRef.child("1").setValue(getCurrentUserId);
                        }
                    }

                    if (dataSnapshot.hasChild("2")){
                        notifySender3 = dataSnapshot.child("2").getValue().toString();
                        saveOnlineUser();
                    } else {
                        counter = dataSnapshot.getChildrenCount();
                        if (counter == 2 && notifySender3.equals("empty") && !notifySender1.equals(getCurrentUserId)
                                && !notifySender2.equals(getCurrentUserId)){
                            myIdRef.child("2").setValue(getCurrentUserId);
                        }
                    }
                } else {
                    counter = dataSnapshot.getChildrenCount();
                    if (counter == 0 && notifySender1.equals("empty")){
                        myIdRef.child("0").setValue(getCurrentUserId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveOnlineUser() {
        onlineReference.child("serverStatus").setValue("offline")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            String err = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            showCloseDialog();
        }

        return false;
    }


    //setting listener to the button navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    MainHomeFragment mainHomeFragment = new MainHomeFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_relativeLayout, mainHomeFragment,
                            mainHomeFragment.getTag()).commit();
                    myToolBar.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_orders:
                    UsersDetailsFragment usersDetailsFragment = new UsersDetailsFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_relativeLayout, usersDetailsFragment,
                            usersDetailsFragment.getTag()).commit();
                    myToolBar.setVisibility(View.GONE);
                    search.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_setting:
                    HomeSettingFragment homeSettingFragment = new HomeSettingFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_relativeLayout, homeSettingFragment,
                            homeSettingFragment.getTag()).commit();
                    myToolBar.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currrentUser = mAuth.getCurrentUser();
        if (currrentUser == null){
            sendUserToLogInActivity();
        } else {
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserID)){
                    sendUserToProfileActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showDetails() {
        databaseReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profilePicture")){
                        uProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                        //loading picture offline
                        Picasso.get().load(uProfilePicture).fit().networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(uProfilePicture).fit().placeholder(R.drawable.profile_image)
                                        .into(profilePicture);
                            }
                        });
                    }

                    if (dataSnapshot.hasChild("fullName")){
                        String name = dataSnapshot.child("fullName").getValue().toString();

                        if(dataSnapshot.hasChild("phoneNumber")){
                            String pPhoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                            details.setText(name + "\n" + pPhoneNumber);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void showCloseDialog() {
        TextView msg;
        Button yes, no;

        dialog.setContentView(R.layout.dialog_question);
        msg = dialog.findViewById(R.id.dialogQuestion_message);
        yes = dialog.findViewById(R.id.dialogQuestion_yes);
        no = dialog.findViewById(R.id.dialogQuestion_no);

        String message = "Do you want to exit ?";

        msg.setText(message);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_icon, menu);
        MenuItem menuItem = menu.findItem(R.id.mainToolbar_search);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendUserToSearchActivity();
                return true;
            }
        });
        return true;
    }*/


    private void sendUserToSearchActivity(){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void sendUserToProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLogInActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLogInDialogActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.putExtra("close", "show");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
