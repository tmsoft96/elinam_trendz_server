package com.tmsoft.tm.elitrends.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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


public class ViewFavoriteDetailActivity extends AppCompatActivity {

    private ImageView productPicture1, productPicture2, productPicture3;
    private TextView productName, productPrice, productDescription;
    private Dialog dialog;
    private ImageButton delete;
    private Toolbar myToolBar;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String postKey;
    private String pProductName, pProductPrice, pProductDescription, pProductPicture1, pProductPicture2, pProductPicture3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite_detail);

        myToolBar = (Toolbar) findViewById(R.id.viewFavorite_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        productPicture1 = (ImageView) findViewById(R.id.viewFavorite_productPicture1);
        productPicture2 = (ImageView) findViewById(R.id.viewFavorite_productPicture2);
        productPicture3 = (ImageView) findViewById(R.id.viewFavorite_productPicture3);
        productName = (TextView) findViewById(R.id.viewFavorite_productName);
        productPrice = (TextView) findViewById(R.id.viewFavorite_productPrice);
        productDescription = (TextView) findViewById(R.id.viewFavorite_productDescription);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        delete = (ImageButton) findViewById(R.id.viewFavorite_delete);

        postKey = getIntent().getExtras().get("postKey").toString();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorites").child(postKey);
        databaseReference.keepSynced(true);

        productPicture2.setVisibility(View.INVISIBLE);
        productPicture3.setVisibility(View.INVISIBLE);

        displaySelectedProduct();

        productPicture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture1);
            }
        });

        productPicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture2);
            }
        });

        productPicture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture3);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrentProduct();
            }
        });
    }

    private void displaySelectedProduct() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("pProductName")){
                        pProductName = dataSnapshot.child("pProductName").getValue().toString();
                        productName.setText(pProductName);
                    }
                    if (dataSnapshot.hasChild("pProductPrice")){
                        pProductPrice = dataSnapshot.child("pProductPrice").getValue().toString();
                        productPrice.setText(pProductPrice);
                    }
                    if (dataSnapshot.hasChild("pProductDescription")){
                        pProductDescription = dataSnapshot.child("pProductDescription").getValue().toString();
                        productDescription.setText(pProductDescription);
                    }
                    if (dataSnapshot.hasChild("pProductPicture1")){
                        pProductPicture1 = dataSnapshot.child("pProductPicture1").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture1).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(pProductPicture1).fit()
                                        .placeholder(R.drawable.warning).into(productPicture1);
                            }
                        });
                    }
                    if (dataSnapshot.hasChild("pProductPicture2")){
                        pProductPicture2 = dataSnapshot.child("pProductPicture2").getValue().toString();
                        productPicture2.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture2).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture2, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture2).fit()
                                        .placeholder(R.drawable.warning).into(productPicture2);
                            }
                        });
                    }
                    if (dataSnapshot.hasChild("pProductPicture3")){
                        pProductPicture3 = dataSnapshot.child("pProductPicture3").getValue().toString();
                        productPicture3.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture3).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture3, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture3).fit()
                                        .placeholder(R.drawable.warning).into(productPicture3);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void viewPictureDialog(final String pictureUrl){
        TextView close, title;
        final ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pProductName);

        //loading picture offline
        Picasso.get()
                .load(pictureUrl).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(picture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pictureUrl).fit()
                        .placeholder(R.drawable.warning).into(picture);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFavoriteDetailActivity.this, ViewPicture.class);
                intent.putExtra("imageText", pProductName);
                intent.putExtra("image", pictureUrl);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void deleteCurrentProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewFavoriteDetailActivity.this);
        builder.setTitle("Confirm Product");

        final TextView text = new TextView(ViewFavoriteDetailActivity.this);
        text.setText("  Are you sure you want to remove this product from your list");
        text.setTextColor(Color.WHITE);
        text.setTextSize(16f);
        builder.setView(text);

        //two button
        //Update button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.removeValue();
                finish();
                Toast.makeText(ViewFavoriteDetailActivity.this, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        //cancel button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);

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
