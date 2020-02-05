package com.tmsoft.tm.elitrends.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ViewProductDetailsActivity extends AppCompatActivity {

    private ImageView productPicture1, productPicture2, productPicture3, viewVideo;
    private TextView productName, productPrice, productDescription, productQuantityAvailable, limitedQty;
    private Button addToFavorite, comment, advertiseProduct;
    private ProgressDialog progressDialog;
    private Dialog dialog, dialogDiscount;
    private ImageButton edit, delete;
    private Toolbar myToolBar;
    private SwipeRefreshLayout refresh;
    private RelativeLayout discountRelative;
    private TextView discountPercent, discountPrice, category;
    private ImageView priceConceal, productDelete1, productDelete2, productDelete3, productImage1, productImage2, productImage3;

    private String getUserCurrentId;
    private DatabaseReference databaseReference, favoriteDatabaseReference;
    private StorageReference storageReference;

    private String postKey;
    private String pProductName, pProductPrice, pProductDescription, pProductPicture1,
            pProductPicture2, pProductPicture3, pProductQuantityAvailable, pViewVideo;
    private String saveCurrentDate, saveCurrentTime, pProductCategory, pProductVideo, pDiscount, pDiscountPercent,
            pDiscountPrice, pLimitedQty;

    private int gallaryPick1, gallaryPick2, gallaryPick3, numQty = 0;
    private Uri imageUri1, imageUri2, imageUri3;
    private String getPictureUri1, getPictureUri2, getPictureUri3;
    private String saveCurrentDate1, saveCurrentTime1, saveRandomName1;
    private String saveCurrentDate2, saveCurrentTime2, saveRandomName2;
    private String saveCurrentDate3, saveCurrentTime3, saveRandomName3;

    String getDiscount = "no", getDiscountPrice, getDiscountPercent,
            tempDiscountPrice = null, tempDiscountPercent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = (Toolbar) findViewById(R.id.viewProduct_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details...");

        productPicture1 = (ImageView) findViewById(R.id.viewProduct_productPicture1);
        productPicture2 = (ImageView) findViewById(R.id.viewProduct_productPicture2);
        productPicture3 = (ImageView) findViewById(R.id.viewProduct_productPicture3);
        productName = (TextView) findViewById(R.id.viewProduct_productName);
        productPrice = (TextView) findViewById(R.id.viewProduct_productPrice);
        productDescription = (TextView) findViewById(R.id.viewProduct_productDescription);
        productQuantityAvailable = (TextView) findViewById(R.id.viewProduct_productQtyOrdered);
        addToFavorite = (Button) findViewById(R.id.viewProduct_addToList);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        dialogDiscount = new Dialog(this, R.style.Theme_CustomDialog);
        delete = (ImageButton) findViewById(R.id.viewProduct_delete);
        edit = (ImageButton) findViewById(R.id.viewProduct_edit);
        comment = (Button) findViewById(R.id.viewProduct_comment);
        viewVideo = (ImageView) findViewById(R.id.viewProduct_video);
        advertiseProduct = (Button) findViewById(R.id.viewProduct_specialEvents);
        discountRelative = (RelativeLayout) findViewById(R.id.viewProduct_discountRelative);
        discountPercent = (TextView) findViewById(R.id.viewProduct_discountPercent);
        discountPrice = (TextView) findViewById(R.id.viewProduct_discountPrice);
        priceConceal = (ImageView) findViewById(R.id.viewProduct_priceConceal);
        limitedQty = (TextView) findViewById(R.id.viewProduct_limitedQty);
        refresh = (SwipeRefreshLayout) findViewById(R.id.viewProduct_refresh);
        category = (TextView) findViewById(R.id.viewProduct_productCategory);

        postKey = getIntent().getExtras().get("postKey").toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getUserCurrentId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
        databaseReference.keepSynced(true);

        favoriteDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Favorites");
        favoriteDatabaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();

        productPicture2.setVisibility(View.GONE);
        productPicture3.setVisibility(View.GONE);
        viewVideo.setVisibility(View.GONE);
        discountRelative.setVisibility(View.GONE);
        priceConceal.setVisibility(View.GONE);
        discountPrice.setVisibility(View.GONE);
        limitedQty.setVisibility(View.GONE);

        displaySelectedProduct();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displaySelectedProduct();
                refresh.setRefreshing(false);
            }
        });

        addToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFavorite();
            }
        });


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
        
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToCommentActivity();
            }
        });

        viewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProductDetailsActivity.this, ViewProductVideoActivity.class);
                intent.putExtra("link", pViewVideo);
                startActivity(intent);
            }
        });

        advertiseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProductDetailsActivity.this, AdvertiseProductActivity.class);
                intent.putExtra("postKey", postKey);
                startActivity(intent);
            }
        });
    }


    private void saveFavorite() {
        progressDialog.setTitle("Adding to Favorite List");
        progressDialog.setMessage("Please wait patiently while your product is added");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTimeFormat.format(postTime.getTime());

        HashMap favoriteMap = new HashMap();
        favoriteMap.put("UserId", getUserCurrentId);
        favoriteMap.put("pProductName", pProductName);
        favoriteMap.put("pProductPrice", pProductPrice);
        favoriteMap.put("pProductDescription", pProductDescription);
        favoriteMap.put("pProductPicture1", pProductPicture1);
        favoriteMap.put("pProductPicture2", pProductPicture2);
        favoriteMap.put("pProductPicture3", pProductPicture3);


        favoriteDatabaseReference.child(saveCurrentDate + getUserCurrentId + saveCurrentTime)
                .updateChildren(favoriteMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(ViewProductDetailsActivity.this, "Product added to favorite list successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(ViewProductDetailsActivity.this, "Failed to add product \n" + errorMessage + "\n Please try again .....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }



    private void displaySelectedProduct() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("ProductName")){
                        pProductName = dataSnapshot.child("ProductName").getValue().toString();
                        productName.setText(pProductName);
                    }
                    if (dataSnapshot.hasChild("ProductPrice")){
                        pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                        productPrice.setText(pProductPrice);
                    }
                    if (dataSnapshot.hasChild("ProductDescription")){
                        pProductDescription = dataSnapshot.child("ProductDescription").getValue().toString();
                        productDescription.setText(pProductDescription);
                    }
                    if (dataSnapshot.hasChild("ProductCategory")){
                        pProductCategory = dataSnapshot.child("ProductCategory").getValue().toString();
                        category.setText(pProductCategory);
                    }
                    if (dataSnapshot.hasChild("ProductVideo")){
                        pProductVideo = dataSnapshot.child("ProductVideo").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("ProductPicture1")){
                        pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                        getPictureUri1 = pProductPicture1;
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture1)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productPicture1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture1)
                                        .placeholder(R.drawable.warning).into(productPicture1);
                            }
                        });
                    }
                    if (dataSnapshot.hasChild("ProductPicture2")){
                        pProductPicture2 = dataSnapshot.child("ProductPicture2").getValue().toString();
                        productPicture2.setVisibility(View.VISIBLE);
                        getPictureUri2 = pProductPicture2;
                        Picasso.get()
                                .load(pProductPicture2)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productPicture2, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture2)
                                        .placeholder(R.drawable.warning).into(productPicture2);
                            }
                        });
                    }
                    if (dataSnapshot.hasChild("ProductPicture3")){
                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                        productPicture3.setVisibility(View.VISIBLE);
                        getPictureUri3 = pProductPicture3;
                        Picasso.get()
                                .load(pProductPicture3)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productPicture3, new Callback() {
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
                    if (dataSnapshot.hasChild("ProductQuantity")){
                        pProductQuantityAvailable = dataSnapshot.child("ProductQuantity").getValue().toString();
                        productQuantityAvailable.setText(pProductQuantityAvailable);
                    }

                    if (dataSnapshot.hasChild("ProductVideo")){
                        pViewVideo = dataSnapshot.child("ProductVideo").getValue().toString();
                        if (!pViewVideo.isEmpty())
                            viewVideo.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("ProductDiscount")){
                        pDiscount = dataSnapshot.child("ProductDiscount").getValue().toString();
                        if (pDiscount.equals("yes")){
                            if (dataSnapshot.hasChild("ProductDiscountPercentage")){
                                pDiscountPercent = dataSnapshot.child("ProductDiscountPercentage").getValue().toString();
                                if(!TextUtils.isEmpty(pDiscountPercent)){
                                    discountPercent.setText(pDiscountPercent);
                                    discountRelative.setVisibility(View.VISIBLE);
                                    discountPercent.setVisibility(View.VISIBLE);
                                }
                            }

                            if (dataSnapshot.hasChild("ProductDiscountPrice")){
                                pDiscountPrice = dataSnapshot.child("ProductDiscountPrice").getValue().toString();
                                if(!TextUtils.isEmpty(pDiscountPrice)){
                                    discountPrice.setText(pDiscountPrice);
                                    discountPrice.setVisibility(View.VISIBLE);
                                    productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                }
                            }
                        }
                    }

                    if (dataSnapshot.hasChild("ProductLimitedQty")){
                        pLimitedQty = dataSnapshot.child("ProductLimitedQty").getValue().toString();
                        if (!TextUtils.isEmpty(pLimitedQty)){
                            String qq = "Minimum quantity order is " + pLimitedQty;
                            limitedQty.setText(qq);
                            limitedQty.setVisibility(View.VISIBLE);
                        }
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

        //Picasso.get().load(pictureUrl).into(picture);

        //loading picture offline
        Picasso.get()
                .load(pictureUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning)
                .into(picture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pictureUrl).fit()
                        .placeholder(R.drawable.warning)
                        .into(picture);
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
                Intent intent = new Intent(ViewProductDetailsActivity.this, ViewPicture.class);
                intent.putExtra("imageText", pProductName);
                intent.putExtra("image", pictureUrl);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void deleteCurrentProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductDetailsActivity.this);
        builder.setTitle("Confirm Product");
        builder.setMessage("Are you sure you want to delete this product");

        //two button
        //Update button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Deleting product ...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ViewProductDetailsActivity.this, "Deleted completely", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        } else {
                            String ex = task.getException().getMessage();
                            Toast.makeText(ViewProductDetailsActivity.this, ex + " try again...", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
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
    }

    private void showEditDialog() {
        final EditText productName, productPrice, productDescription, productQuantityAvailable, productVideo, limitedQty;
        final Spinner productCategory;
        final LinearLayout layoutLimitedQty;
        final ImageView productUpload1, productUpload2, productUpload3, add, minus;
        Button button;
        TextView close, discount;

        dialog.setContentView(R.layout.dialog_edit_product_layout);
        productName = dialog.findViewById(R.id.edit_productName);
        productPrice = dialog.findViewById(R.id.edit_productPrice);
        productDescription = dialog.findViewById(R.id.edit_productDescription);
        productCategory = dialog.findViewById(R.id.edit_productCategory);
        productVideo = dialog.findViewById(R.id.edit_productVideo);
        productImage1 = dialog.findViewById(R.id.edit_productPicture1);
        productImage2 = dialog.findViewById(R.id.edit_productPicture2);
        productImage3 = dialog.findViewById(R.id.edit_productPicture3);
        productUpload1 = dialog.findViewById(R.id.edit_productUpload1);
        productUpload2 = dialog.findViewById(R.id.edit_productUpload2);
        productUpload3 = dialog.findViewById(R.id.edit_productUpload3);
        productDelete1 = dialog.findViewById(R.id.edit_productDelete1);
        productDelete2 = dialog.findViewById(R.id.edit_productDelete2);
        productDelete3 = dialog.findViewById(R.id.edit_productDelete3);
        productQuantityAvailable = dialog.findViewById(R.id.edit_productQuantityAvailable);
        button = dialog.findViewById(R.id.edit_buttonProduct);
        close = dialog.findViewById(R.id.editProduct_close);
        discount = dialog.findViewById(R.id.edit_priceDiscount);
        limitedQty = dialog.findViewById(R.id.edit_productLimitedQty);
        layoutLimitedQty = dialog.findViewById(R.id.edit_limitedQty);
        add = dialog.findViewById(R.id.edit_qtyAdd);
        minus = dialog.findViewById(R.id.edit_qtySub);

        layoutLimitedQty.setVisibility(View.GONE);
        minus.setEnabled(false);
        productDelete1.setVisibility(View.INVISIBLE);
        productDelete2.setVisibility(View.INVISIBLE);
        productDelete3.setVisibility(View.INVISIBLE);

        discount.setPaintFlags(discount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = productPrice.getText().toString();
                if (TextUtils.isEmpty(price))
                    Toast.makeText(view.getContext(), "Enter original price first", Toast.LENGTH_SHORT).show();
                else
                    showPriceDiscountDialog();
            }
        });

        productName.setText(pProductName);
        productPrice.setText(pProductPrice);
        productDescription.setText(pProductDescription);
        productCategory.setSelection(((ArrayAdapter) productCategory.getAdapter()).getPosition(pProductCategory));
        limitedQty.setText(pLimitedQty);

        if (pProductCategory.equalsIgnoreCase("Made In Ghana") || pProductCategory.equalsIgnoreCase("Manufactures"))
            productCategory.setVisibility(View.GONE);
        else
            productCategory.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(limitedQty.getText().toString()))
            numQty = Integer.parseInt(limitedQty.getText().toString());

        if(numQty > 0)
            minus.setEnabled(true);
        else
            minus.setEnabled(false);

        //loading picture 1
        Picasso.get()
                .load(pProductPicture1).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image).into(productImage1, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture1).fit()
                        .placeholder(R.drawable.no_image).into(productImage1);
            }
        });

        //loading picture 2
        Picasso.get()
                .load(pProductPicture2)
                .fit().networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image).into(productImage2, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture2).fit()
                        .placeholder(R.drawable.no_image).into(productImage2);
            }
        });

        //loading picture 3
        Picasso.get()
                .load(pProductPicture3).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image).into(productImage3, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture3).fit()
                        .placeholder(R.drawable.no_image).into(productImage3);
            }
        });

        productQuantityAvailable.setText(pProductQuantityAvailable);

        productVideo.setText(pProductVideo);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numQty = numQty + 1;
                limitedQty.setText(numQty + "");
                minus.setEnabled(true);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numQty == 0){
                    minus.setEnabled(false);
                } else {
                    numQty = Integer.parseInt(limitedQty.getText().toString());
                    numQty = numQty - 1;
                    limitedQty.setText(numQty + "");
                }
            }
        });

        productDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri1)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri1);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            productImage1.setImageResource(R.drawable.product);
                            productDelete1.setVisibility(View.INVISIBLE);
                            Toast.makeText(view.getContext(), "Picture one deleted successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Error occurred... please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        productDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri2)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri2);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            productImage2.setImageResource(R.drawable.product);
                            productDelete2.setVisibility(View.INVISIBLE);
                            Toast.makeText(view.getContext(), "Picture one deleted successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Error occurred... please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        productDelete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri3)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri3);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            productImage3.setImageResource(R.drawable.product);
                            productDelete3.setVisibility(View.INVISIBLE);
                            Toast.makeText(view.getContext(), "Picture one deleted successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Error occurred... please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        productUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallaryPick1 = 1;
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick1);
            }
        });

        productUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallaryPick2 = 1;
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick2);
            }
        });

        productUpload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallaryPick3 = 1;
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick3);
            }
        });

        productCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String zz = productCategory.getSelectedItem().toString();
                switch (zz){
                    case "Bulk Purchase":
                        layoutLimitedQty.setVisibility(View.VISIBLE);
                        break;
                    default:
                        layoutLimitedQty.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pName = productName.getText().toString();
                String pPrice = productPrice.getText().toString();
                String pDescription = productDescription.getText().toString();
                String pCategory = productCategory.getSelectedItem().toString();
                String pVideo = productVideo.getText().toString();
                String pQuantityAvailable = productQuantityAvailable.getText().toString();
                String pLQty = limitedQty.getText().toString();

                if(TextUtils.isEmpty(pName))
                    Toast.makeText(ViewProductDetailsActivity.this, "Please enter your product name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pPrice))
                    Toast.makeText(ViewProductDetailsActivity.this, "Please enter your product price", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pDescription))
                    Toast.makeText(ViewProductDetailsActivity.this, "Please enter your product description", Toast.LENGTH_SHORT).show();
                else if (pCategory.equalsIgnoreCase("Select Category"))
                    Toast.makeText(ViewProductDetailsActivity.this, "Please select product category", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pQuantityAvailable))
                    Toast.makeText(ViewProductDetailsActivity.this, "Please enter the product quantity", Toast.LENGTH_SHORT).show();
                else{
                    if (!pCategory.equalsIgnoreCase("Bulk Purchase"))
                        pLQty = "";
                    editProduct(pName, pPrice, pDescription, pCategory, pVideo, getPictureUri1, getPictureUri2,
                            getPictureUri3, pQuantityAvailable, pLQty);
                }

            }
        });

        dialog.show();
    }

    private void showPriceDiscountDialog() {
        TextView close, initialPrice;
        final EditText discountPriceD, discountPercentageD;
        ImageView done, calculate;
        Button removeDiscount;

        dialogDiscount.setContentView(R.layout.dialog_discount);
        close = dialogDiscount.findViewById(R.id.dialogDiscount_close);
        initialPrice = dialogDiscount.findViewById(R.id.dialogDiscount_initialPrice);
        discountPercentageD = dialogDiscount.findViewById(R.id.dialogDiscount_discountPricePercent);
        discountPriceD = dialogDiscount.findViewById(R.id.dialogDiscount_discountPrice);
        calculate = dialogDiscount.findViewById(R.id.dialogDiscount_calculator);
        done = dialogDiscount.findViewById(R.id.dialogDiscount_done);
        removeDiscount = dialogDiscount.findViewById(R.id.dialogDiscount_removeDiscount);

        try {
            boolean rr = pDiscountPrice.startsWith("GHC");
            if (rr){
                String disPxx = pDiscountPrice.substring(3);
                discountPriceD.setText(disPxx);
            } else {
                discountPriceD.setText(pDiscountPrice);
            }
        } catch (NullPointerException e){
            Log.i("error", e.getMessage());
            discountPriceD.setText(pDiscountPrice);
        }

        discountPercentageD.setText(pDiscountPercent);

        initialPrice.setText(pProductPrice);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iniPx = pProductPrice.substring(3);
                String disPx = discountPriceD.getText().toString();
                if (!TextUtils.isEmpty(disPx)){
                    try{
                        Double oldPx = Double.parseDouble(iniPx);
                        Double newPx = Double.parseDouble(disPx);
                        Double increase = oldPx - newPx;
                        Double increaseDivide = increase / oldPx;
                        Double increasePercent = increaseDivide * 100;
                        discountPercentageD.setText(Math.round(increasePercent) + "% off");
                    } catch (Exception ex){
                        Toast.makeText(view.getContext(), "Check price entry", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(view.getContext(), "Enter new price", Toast.LENGTH_SHORT).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getNewPx = discountPriceD.getText().toString();
                String getPercent = discountPercentageD.getText().toString();

                if (TextUtils.isEmpty(getNewPx) && TextUtils.isEmpty(getPercent))
                    Toast.makeText(view.getContext(), "Check your entries", Toast.LENGTH_SHORT).show();
                else{
                    pDiscount = "yes";
                    pDiscountPercent = getPercent;
                    pDiscountPrice = "GHC" + getNewPx;
                    dialogDiscount.dismiss();
                    Toast.makeText(ViewProductDetailsActivity.this, "You added a discount of " + getPercent + "% with a new price " +
                            getNewPx, Toast.LENGTH_LONG).show();
                }
            }
        });

        removeDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDiscount = "no";
                pDiscountPercent = "";
                pDiscountPrice = "";
                dialogDiscount.dismiss();
                Toast.makeText(ViewProductDetailsActivity.this, "No discount for this product", Toast.LENGTH_LONG).show();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDiscount.dismiss();
            }
        });

        dialogDiscount.show();
    }

    private void editProduct(String pName, String pPrice, String pDescription, String pCategory,
                             final String pVideo, String getPictureUri1, String getPictureUri2,
                             String getPictureUri3, String pProductQuantity, String pLimitedQtyy) {
        progressDialog.setTitle("Updating Product");
        progressDialog.setMessage("Please wait patiently while your product is updated");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        String searchName = pName.toLowerCase();

        HashMap productMap = new HashMap();
        productMap.put("ProductName", pName);
        productMap.put("ProductPrice", pPrice);
        productMap.put("ProductDescription", pDescription);
        productMap.put("ProductSearchName", searchName);
        productMap.put("ProductCategory", pCategory);
        productMap.put("ProductVideo", pVideo);
        productMap.put("ProductDiscount", pDiscount);
        productMap.put("ProductDiscountPercentage", pDiscountPercent);
        productMap.put("ProductDiscountPrice", pDiscountPrice);
        productMap.put("ProductPicture1", getPictureUri1);
        productMap.put("ProductPicture2", getPictureUri2);
        productMap.put("ProductPicture3", getPictureUri3);
        productMap.put("ProductDate", saveCurrentDate1);
        productMap.put("ProductTime", saveCurrentTime1);
        productMap.put("ProductQuantity", pProductQuantity);
        productMap.put("ProductLimitedQty", pLimitedQtyy);

        databaseReference.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        displaySelectedProduct();
                        Toast.makeText(ViewProductDetailsActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(ViewProductDetailsActivity.this, "Failed to updated product \n" + errorMessage + "\n Please try again .....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (gallaryPick1 == 1){
            if(requestCode == gallaryPick1 && resultCode == RESULT_OK && data != null){
                imageUri1 = data.getData();
                saveProductPictureOneToFirebaseStorage();
            }
        } else if (gallaryPick2 == 1){
            if (requestCode == gallaryPick2 && resultCode == RESULT_OK && data != null){
                imageUri2 = data.getData();
                saveProductPictureTwoToFirebaseStorage();
            }
        } else if (gallaryPick3 == 1){
            if (requestCode == gallaryPick3 && resultCode == RESULT_OK && data != null){
                imageUri3 = data.getData();
                saveProductPictureThreeToFirebaseStorage();
            }
        }
    }

    private void saveProductPictureOneToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri1)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri1);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic1();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic1();
                    }
                });
            } catch (Exception ex){
                savePic1();
            }
        } else
            savePic1();
    }

    private void savePic1() {
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate1 = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime1 = currentTimeFormat.format(postTime.getTime());

        saveRandomName1 = saveCurrentDate1 + getUserCurrentId + saveCurrentTime1;

        StorageReference filePath = storageReference.child("Product Pictures").child(getUserCurrentId)
                .child(imageUri1.getLastPathSegment() + saveRandomName1 + ".jpg");
        filePath.putFile(imageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri1 = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri1).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri1).fit()
                                        .placeholder(R.drawable.warning)
                                        .into(productImage1);
                            }
                        });
                        Toast.makeText(ViewProductDetailsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick1 = 0;
                        productDelete1.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ViewProductDetailsActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveProductPictureTwoToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri2)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri2);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic2();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic2();
                    }
                });
            } catch (Exception ex){
                savePic2();
            }
        } else
            savePic2();
    }

    private void savePic2(){
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate2 = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime2 = currentTimeFormat.format(postTime.getTime());

        saveRandomName2 = saveCurrentDate2 + getUserCurrentId + saveCurrentTime2;

        StorageReference filePath = storageReference.child("Product Pictures").child(getUserCurrentId)
                .child(imageUri2.getLastPathSegment() + saveRandomName2 + ".jpg");
        filePath.putFile(imageUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri2 = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri2).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage2, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri2).fit()
                                        .placeholder(R.drawable.warning).into(productImage2);
                            }
                        });
                        Toast.makeText(ViewProductDetailsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick2 = 0;
                        productDelete2.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ViewProductDetailsActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveProductPictureThreeToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri3)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri3);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic3();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic3();
                    }
                });
            } catch (Exception ex){
                savePic3();
            }
        } else
            savePic3();
    }

    private void savePic3(){
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate3 = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime3 = currentTimeFormat.format(postTime.getTime());

        saveRandomName3 = saveCurrentDate3 + getUserCurrentId + saveCurrentTime3;

        StorageReference filePath = storageReference.child("Product Pictures").child(getUserCurrentId)
                .child(imageUri3.getLastPathSegment() + saveRandomName3 + ".jpg");
        filePath.putFile(imageUri3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri3 = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri3).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage3, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri3).fit()
                                        .placeholder(R.drawable.warning).into(productImage3);
                            }
                        });
                        Toast.makeText(ViewProductDetailsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick3 = 0;
                        productDelete3.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ViewProductDetailsActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
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

    private void sendUserToCommentActivity(){
        Intent intent = new Intent(ViewProductDetailsActivity.this, CommentActivity.class);
        intent.putExtra("postKey", postKey);
        startActivity(intent);
    }
}
