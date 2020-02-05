package com.tmsoft.tm.elitrends.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserProfileActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private CircleImageView profilePicture;
    private TextView userName, location, phoneNumber, email;
    private ImageView callUser, chatUser;
    private TextView productOrderNumber, cartProductOrderNumber, feedBackNumber;
    private ImageView productOrderButton, cartProductOrderButton, feedbackButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, productReference, feedbackReference,
            cartOrderReference;
    private String userId, pPhoneNumber, getCurrentUserId;

    private String pProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        myToolBar = (Toolbar) findViewById(R.id.viewAllUser_toolbarView);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        userId = getIntent().getExtras().get("userId").toString();

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.keepSynced(true);
        productReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        productReference.keepSynced(true);
        cartOrderReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
        cartOrderReference.keepSynced(true);
        feedbackReference = FirebaseDatabase.getInstance().getReference().child("Feedback");
        feedbackReference.keepSynced(true);

        profilePicture = (CircleImageView) findViewById(R.id.viewAllUser_profilePicture);
        userName = (TextView) findViewById(R.id.viewAllUser_fullName);
        location = (TextView) findViewById(R.id.viewAllUser_address);
        phoneNumber = (TextView) findViewById(R.id.viewAllUser_phoneNumber);
        email = (TextView) findViewById(R.id.viewAllUser_email);
        callUser = (ImageView) findViewById(R.id.viewAllUser_callUser);
        chatUser = (ImageView) findViewById(R.id.viewAllUser_chatUser);
        productOrderNumber = (TextView) findViewById(R.id.viewAllUser_productOrderNumber);
        cartProductOrderNumber = (TextView) findViewById(R.id.viewAllUser_cartProductOrderNumber);
        feedBackNumber = (TextView) findViewById(R.id.viewAllUser_feedBackNumber);
        productOrderButton = (ImageView) findViewById(R.id.viewAllUser_productOrderButton);
        cartProductOrderButton = (ImageView) findViewById(R.id.viewAllUser_cartProductOrderButton);
        feedbackButton = (ImageView) findViewById(R.id.viewAllUser_feedBackButton);

        showAllDetails();
        showNumberOfOrders();
        showNumberOfFeedbacks();
        showNumberOfCartOrder();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPicture(pProfilePicture);
            }
        });

        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUserNow();
            }
        });

        chatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userId.equals(getCurrentUserId))
                    sendUserToChatActivity();
            }
        });

        productOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = productOrderNumber.getText().toString();
                if (num.equals("0"))
                    Toast.makeText(ViewUserProfileActivity.this, "No Product Order", Toast.LENGTH_SHORT).show();
                else
                    sendUserToAllOrderActivity();
            }
        });

        cartProductOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = cartProductOrderNumber.getText().toString();
                if (num.equals("0"))
                    Toast.makeText(ViewUserProfileActivity.this, "No Cart Product Order", Toast.LENGTH_SHORT).show();
                else
                    sendUserToAllCartOrderActivity();
            }
        });

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = feedBackNumber.getText().toString();
                if (num.equals("0"))
                    Toast.makeText(ViewUserProfileActivity.this, "No Comment", Toast.LENGTH_SHORT).show();
                else
                    sendUserToFeedbackActivity();
            }
        });
    }

    private void showNumberOfCartOrder() {
        cartOrderReference.orderByChild("userId").startAt(userId).endAt(userId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int cartNumber = (int) dataSnapshot.getChildrenCount();
                            cartProductOrderNumber.setText(cartNumber + "");
                        } else {
                            cartProductOrderNumber.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showNumberOfFeedbacks() {
        feedbackReference.orderByChild("userId").startAt(userId).endAt(userId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int feedbackNu = (int) dataSnapshot.getChildrenCount();
                            feedBackNumber.setText(feedbackNu + "");
                        } else {
                            feedBackNumber.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void openPicture(String pProfilePicture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserProfileActivity.this);

        final ImageView imageView = new ImageView(ViewUserProfileActivity.this);
        Picasso.get()
                .load(pProfilePicture).fit()
                .into(imageView);
        builder.setView(imageView);
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void showNumberOfOrders() {
        productReference.orderByChild("userId").startAt(userId).endAt(userId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int getNoOfOrders = (int) dataSnapshot.getChildrenCount();
                            productOrderNumber.setText(getNoOfOrders + "");
                        } else
                            productOrderNumber.setText("0");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void callUserNow() {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + pPhoneNumber));
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(phoneIntent);

            Toast.makeText(this, "Calling " + pPhoneNumber, Toast.LENGTH_LONG).show();
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "Call failed... Please try again " + pPhoneNumber, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllDetails() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("fullName")){
                        String pFullName = dataSnapshot.child("fullName").getValue().toString();
                        userName.setText(pFullName);
                    }
                    if(dataSnapshot.hasChild("phoneNumber")){
                        pPhoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                        phoneNumber.setText(pPhoneNumber);
                    }
                    if(dataSnapshot.hasChild("location")){
                        String pLocation = dataSnapshot.child("location").getValue().toString();
                        location.setText(pLocation);
                    }
                    if(dataSnapshot.hasChild("profilePicture")){
                        pProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                        Picasso.get()
                                .load(pProfilePicture).fit()
                                .placeholder(R.drawable.profile_image).into(profilePicture);
                        //loading picture offline
                        Picasso.get()
                                .load(pProfilePicture).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProfilePicture).fit()
                                        .placeholder(R.drawable.profile_image)
                                        .into(profilePicture);
                            }
                        });
                    }
                    if(dataSnapshot.hasChild("email")){
                        String pEmail = dataSnapshot.child("email").getValue().toString();
                        email.setText(pEmail);
                    } else
                        email.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void sendUserToChatActivity(){
        Intent intent = new Intent(ViewUserProfileActivity.this, ChatActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToAllOrderActivity(){
        Intent intent = new Intent(ViewUserProfileActivity.this, AllUserOrderActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToFeedbackActivity(){
        Intent intent = new Intent(ViewUserProfileActivity.this, FeedBackActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToAllCartOrderActivity(){
        Intent intent = new Intent(ViewUserProfileActivity.this, ShowCartViewOrderActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
