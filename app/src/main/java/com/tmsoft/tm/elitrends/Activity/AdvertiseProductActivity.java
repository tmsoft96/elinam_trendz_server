package com.tmsoft.tm.elitrends.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

public class AdvertiseProductActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ImageView productPicture1, productPicture2, productPicture3, productVideo, priceImageIndicator;
    private TextView productName, productPrice, productAvaliableQty, dateAndTime, slotNote;
    private EditText productDescription, slotNumber;
    private ImageView editDiscount, editQuantity, editTimeFrame, editEvent, slotAdd, slotMinus, productPic1, productPic2, productPic3;
    private Button advertiseProduct, deleteProduct;
    private Dialog dialog;
    private SwipeRefreshLayout refresh;
    private Calendar calendar;
    private ProgressDialog progressDialog;
    private ViewFlipper productPictureFlipper;

    private  MenuItem menuItem;

    private String postKey, deletion;
    private DatabaseReference databaseReference, advertiseReference;

    private String pProductName, pProductPrice, pProductDescription, pProductCategory, pProductPicture1, pProductPicture2,
            pProductPicture3, pProductVideo, pProductAvaliableQty, pDiscountPercent, pDiscountPrice;

    private int day,month,year, hour, minute;
    private String choosenDate, choosenTime, getEventPackage;
    private boolean LAYOUTSHOW = true;

    private long SUGGESTEDSLOTNUMBER = 0;
    private String AD_DISCOUNTPERCENT, AD_DISCOUNTPRIZE, AD_ISDISCOUNT = "false", AD_TIMEANDDATE, AD_PRODUCTQUANTITYAVAILABLE,
            AD_EVENT, AD_PRODUCTDESCRIPION, AD_SLOTNUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_product);

        myToolbar = (Toolbar) findViewById(R.id.advertiseProduct_toolbar);
        productPicture1 = (ImageView) findViewById(R.id.advertiseProduct_productPicture1);
        productPicture2 = (ImageView) findViewById(R.id.advertiseProduct_productPicture2);
        productPicture3 = (ImageView) findViewById(R.id.advertiseProduct_productPicture3);
        productVideo = (ImageView) findViewById(R.id.advertiseProduct_video);
        priceImageIndicator = (ImageView) findViewById(R.id.advertiseProduct_priceIndicator);
        productName = (TextView) findViewById(R.id.advertiseProduct_productName);
        productPrice = (TextView) findViewById(R.id.advertiseProduct_productPrice);
        productAvaliableQty = (TextView) findViewById(R.id.advertiseProduct_productQuantityAvailable);
        productDescription = (EditText) findViewById(R.id.advertiseProduct_description);
        editDiscount = (ImageView) findViewById(R.id.advertiseProduct_editDiscount);
        editQuantity = (ImageView) findViewById(R.id.advertiseProduct_editQuantity);
        editTimeFrame = (ImageView) findViewById(R.id.advertiseProduct_editTimeFrame);
        editEvent = (ImageView) findViewById(R.id.advertiseProduct_editEvent);
        advertiseProduct = (Button) findViewById(R.id.advertiseProduct_advertise);
        deleteProduct = (Button) findViewById(R.id.advertiseProduct_delete);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        refresh = (SwipeRefreshLayout) findViewById(R.id.advertiseProduct_refresh);
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        slotNote = (TextView) findViewById(R.id.advertiseProduct_slotNumberNote);
        slotNumber = (EditText) findViewById(R.id.advertiseProduct_slotNumber);
        slotAdd = (ImageView) findViewById(R.id.advertiseProduct_slotAdd);
        slotMinus = (ImageView) findViewById(R.id.advertiseProduct_slotMinus);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Advertise Product");

        productPicture2.setVisibility(View.GONE);
        productPicture3.setVisibility(View.GONE);
        productVideo.setVisibility(View.GONE);
        deleteProduct.setVisibility(View.GONE);

        postKey = getIntent().getExtras().get("postKey").toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
        databaseReference.keepSynced(true);
        advertiseReference = FirebaseDatabase.getInstance().getReference().child("Product Advertisement");
        advertiseReference.keepSynced(true);

        try{
            deletion = getIntent().getExtras().get("deletion").toString();
            if (deletion.equalsIgnoreCase("true")){
                deleteProduct.setVisibility(View.VISIBLE);
                showProductAdvertise();
                LAYOUTSHOW = false;
                advertiseProduct.setText("Update Advertisement");
            }
            else{
                deleteProduct.setVisibility(View.GONE);
                displaySelectedProduct();
            }
        } catch (Exception e){
            Log.i("Error", e.getMessage());
            deleteProduct.setVisibility(View.GONE);
            displaySelectedProduct();
        }

        slotNote.setPaintFlags(slotNote.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        productVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdvertiseProductActivity.this, ViewProductVideoActivity.class);
                intent.putExtra("link", pProductVideo);
                startActivity(intent);
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

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displaySelectedProduct();
                refresh.setRefreshing(false);
            }
        });

        editDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceDiscountDialog();
            }
        });

        editQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDialog();
            }
        });

        editTimeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDialog();
            }
        });

        advertiseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = advertiseProduct.getText().toString();
                if (title.equalsIgnoreCase("Update Advertisement"))
                    updateAdvertise();
                else
                    saveAdvertisement();
            }
        });

        slotNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdvertiseProductActivity.this);
                alertDialog.setMessage(R.string.advertiseNote2)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User finish reading
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        getSlotNumber();
        slotNumber.setText(SUGGESTEDSLOTNUMBER + "");

        slotAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getSlot = slotNumber.getText().toString();
                int slot = Integer.parseInt(getSlot);
                ++slot;
                slotNumber.setText(slot+ "");
            }
        });

        slotMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getSlot = slotNumber.getText().toString();
                int slot = Integer.parseInt(getSlot);
                if (slot > 0)
                    --slot;
                slotNumber.setText(slot + "");
            }
        });

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdvertiseProductActivity.this);
                alertDialog.setTitle("Delete Ad");
                alertDialog.setMessage(R.string.deletionNote)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Deleting ad...");
                                progressDialog.setCanceledOnTouchOutside(true);
                                progressDialog.show();

                                advertiseReference.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(AdvertiseProductActivity.this, "Ad deleted successfully...", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(AdvertiseProductActivity.this, err, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User finish reading
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });
    }

    private void updateAdvertise() {
        AD_PRODUCTDESCRIPION = productDescription.getText().toString();
        String getSlot = slotNumber.getText().toString();

        if(!AD_ISDISCOUNT.equals("true"))
            AD_ISDISCOUNT = "false";

        if(TextUtils.isEmpty(AD_PRODUCTDESCRIPION))
            Toast.makeText(AdvertiseProductActivity.this, "Enter product description", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(getSlot))
            Toast.makeText(AdvertiseProductActivity.this, "Enter unique slot number", Toast.LENGTH_SHORT).show();
        else
            updateAdvertisementProduct(getSlot);
    }

    private void saveAdvertisement() {
        AD_PRODUCTDESCRIPION = productDescription.getText().toString();
        String getSlot = slotNumber.getText().toString();

        if(!AD_ISDISCOUNT.equals("true"))
            AD_ISDISCOUNT = "false";

        if(TextUtils.isEmpty(AD_PRODUCTDESCRIPION))
            Toast.makeText(AdvertiseProductActivity.this, "Enter product description", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(getSlot))
            Toast.makeText(AdvertiseProductActivity.this, "Enter unique slot number", Toast.LENGTH_SHORT).show();
        else
            saveAdvertisementProduct(getSlot);
    }

    private void showProductAdvertise() {
        advertiseReference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("advertisementSlot")){
                        AD_SLOTNUMBER = dataSnapshot.child("advertisementSlot").getValue().toString();
                        slotNumber.setText(AD_SLOTNUMBER);
                    }

                    if (dataSnapshot.hasChild("advertiseId")){
                        displaySelectedProduct();
                    }

                    if (dataSnapshot.hasChild("productDescription")){
                        AD_PRODUCTDESCRIPION = dataSnapshot.child("productDescription").getValue().toString();
                        productDescription.setText(AD_PRODUCTDESCRIPION);
                    }

                    if (dataSnapshot.hasChild("productDiscountPercent")){
                        AD_DISCOUNTPERCENT = dataSnapshot.child("productDiscountPercent").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("isProductDiscount")){
                        AD_ISDISCOUNT = dataSnapshot.child("isProductDiscount").getValue().toString();

                        if (AD_ISDISCOUNT.equalsIgnoreCase("true")){
                            if (dataSnapshot.hasChild("productDiscountPrice")){
                                AD_DISCOUNTPRIZE = dataSnapshot.child("productDiscountPrice").getValue().toString();
                                productPrice.setText(AD_DISCOUNTPRIZE);
                                priceImageIndicator.setBackgroundResource(R.drawable.discount);
                            }
                        }


                    }

                    if (dataSnapshot.hasChild("productQuantityAvailable")){
                        AD_PRODUCTQUANTITYAVAILABLE = dataSnapshot.child("productQuantityAvailable").getValue().toString();
                        productAvaliableQty.setText(AD_PRODUCTQUANTITYAVAILABLE);
                    }

                    if (dataSnapshot.hasChild("advertisementDateAndTime")){
                        AD_TIMEANDDATE = dataSnapshot.child("advertisementDateAndTime").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("advertisementEvent")){
                        AD_EVENT = dataSnapshot.child("advertisementEvent").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSlotNumber(){
        advertiseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    SUGGESTEDSLOTNUMBER = dataSnapshot.getChildrenCount() + 1;
                } else {
                    SUGGESTEDSLOTNUMBER = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveAdvertisementProduct(String getSlot) {
        progressDialog.setMessage("Please wait while product advertisement complete...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Map advertiseMap = new HashMap();
        advertiseMap.put("advertisementSlot", getSlot);
        advertiseMap.put("advertiseId", postKey);
        advertiseMap.put("productDescription", AD_PRODUCTDESCRIPION);
        advertiseMap.put("productDiscountPrice", AD_DISCOUNTPRIZE);
        advertiseMap.put("productDiscountPercent", AD_DISCOUNTPERCENT);
        advertiseMap.put("isProductDiscount", AD_ISDISCOUNT);
        advertiseMap.put("productQuantityAvailable", AD_PRODUCTQUANTITYAVAILABLE);
        advertiseMap.put("advertisementDateAndTime", AD_TIMEANDDATE);
        advertiseMap.put("advertisementEvent",AD_EVENT);

        advertiseReference.child(postKey).setValue(advertiseMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdvertiseProductActivity.this, "Product advertisement successfully", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    finish();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(AdvertiseProductActivity.this, err, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void updateAdvertisementProduct(String getSlot) {
        progressDialog.setMessage("Please wait while product advertisement complete...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Map advertiseMap = new HashMap();
        advertiseMap.put("advertisementSlot", getSlot);
        advertiseMap.put("advertiseId", postKey);
        advertiseMap.put("productDescription", AD_PRODUCTDESCRIPION);
        advertiseMap.put("productDiscountPrice", AD_DISCOUNTPRIZE);
        advertiseMap.put("productDiscountPercent", AD_DISCOUNTPERCENT);
        advertiseMap.put("isProductDiscount", AD_ISDISCOUNT);
        advertiseMap.put("productQuantityAvailable", AD_PRODUCTQUANTITYAVAILABLE);
        advertiseMap.put("advertisementDateAndTime", AD_TIMEANDDATE);
        advertiseMap.put("advertisementEvent",AD_EVENT);

        advertiseReference.child(postKey).updateChildren(advertiseMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdvertiseProductActivity.this, "Product advertisement successfully", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    finish();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(AdvertiseProductActivity.this, err, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void showEventDialog() {
        final TextView close, allEvent, addPackage;
        LinearLayout defaultPackages;
        Button done;

        dialog.setContentView(R.layout.dialog_event);
        close = (TextView) dialog.findViewById(R.id.dialogEvent_close);
        allEvent = (TextView) dialog.findViewById(R.id.dialogEvent_events);
        addPackage = (TextView) dialog.findViewById(R.id.dialogEvent_addPackage);
        defaultPackages = (LinearLayout) dialog.findViewById(R.id.dialogEvent_defaultPackages);
        done = (Button) dialog.findViewById(R.id.dialogEvent_done);

        addPackage.setPaintFlags(addPackage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        allEvent.setText(AD_EVENT);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventPackage = allEvent.getText().toString();
                AD_EVENT = getEventPackage;
                LAYOUTSHOW = false;
                if (LAYOUTSHOW)
                    menuItem.setIcon(R.drawable.preview);
                else
                    menuItem.setIcon(R.drawable.preview_pink);
                Toast.makeText(AdvertiseProductActivity.this, "Selected event or occasion is "  +
                        getEventPackage, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdvertiseProductActivity.this);
                builder.setTitle("Customize packages");
                builder.setMessage("Add Event...");

                final EditText editText = new EditText(AdvertiseProductActivity.this);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String getCustomPackage = editText.getText().toString();
                        allEvent.setText(getCustomPackage);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setView(editText);
                Dialog dialogInput = builder.create();
                dialogInput.show();
            }
        });

        defaultPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog subDialog = new Dialog(AdvertiseProductActivity.this, R.style.Theme_CustomDialog);
                LinearLayout christmas, easter, blackMarket, priceReduction, hot, valemtine, mothersDay, fathersDay;
                TextView subClose;

                subDialog.setContentView(R.layout.dialog_special_events);
                christmas = subDialog.findViewById(R.id.dialogSpecial_christmas);
                easter = subDialog.findViewById(R.id.dialogSpecial_easter);
                blackMarket = subDialog.findViewById(R.id.dialogSpecial_blackMarket);
                priceReduction = subDialog.findViewById(R.id.dialogSpecial_priceReduction);
                hot = subDialog.findViewById(R.id.dialogSpecial_hot);
                valemtine = subDialog.findViewById(R.id.dialogSpecial_valentine);
                subClose = subDialog.findViewById(R.id.dialogSpecial_close);
                mothersDay = subDialog.findViewById(R.id.dialogSpecial_mothersDay);
                fathersDay = subDialog.findViewById(R.id.dialogSpecial_fathersDay);

                subClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subDialog.dismiss();
                    }
                });

                christmas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        allEvent.setText("Christmas Packages");
                        subDialog.dismiss();
                    }
                });

                easter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        allEvent.setText("Easter Packages");
                        subDialog.dismiss();
                    }
                });

                blackMarket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        allEvent.setText("Black Market Packages");
                        subDialog.dismiss();
                    }
                });

                priceReduction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        allEvent.setText("Price Reduction Packages");
                        subDialog.dismiss();
                    }
                });

                hot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        allEvent.setText("Hot Packages");
                        subDialog.dismiss();
                    }
                });

                valemtine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        allEvent.setText("Valentine Packages");
                        subDialog.dismiss();
                    }
                });

                mothersDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allEvent.setText("Mother\'s Day Packages");
                        subDialog.dismiss();
                    }
                });

                fathersDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allEvent.setText("Father\'s Day Packages");
                        subDialog.dismiss();
                    }
                });

                subDialog.show();
            }
        });

        dialog.show();
    }

    private void showTimeDialog() {
        final TextView close;
        final ImageView calenderChoose, time;
        Button done;

        dialog.setContentView(R.layout.dialog_timer);
        close = (TextView) dialog.findViewById(R.id.dialogTimer_close);
        dateAndTime = (TextView) dialog.findViewById(R.id.dialogTimer_dateAndTime);
        calenderChoose = (ImageView) dialog.findViewById(R.id.dialogTimer_calender);
        time = (ImageView) dialog.findViewById(R.id.dialogTimer_clock);
        done = (Button) dialog.findViewById(R.id.dialogTimer_done);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dateAndTime.setText(AD_TIMEANDDATE);

        if(!TextUtils.isEmpty(choosenDate) && !TextUtils.isEmpty(choosenTime))
            dateAndTime.setText(choosenDate + " - " + choosenTime);

        calenderChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                showDialog(888);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                showDialog(777);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(choosenDate) && TextUtils.isEmpty(choosenTime))
                    Toast.makeText(AdvertiseProductActivity.this, "Set date and time properly", Toast.LENGTH_SHORT).show();
                else{
                    AD_TIMEANDDATE = choosenDate + " - " + choosenTime;
                    LAYOUTSHOW = false;
                    if (LAYOUTSHOW)
                        menuItem.setIcon(R.drawable.preview);
                    else
                        menuItem.setIcon(R.drawable.preview_pink);
                    Toast.makeText(AdvertiseProductActivity.this, "Product advertisement expiration  time set to " +
                            choosenDate + " - " + choosenTime, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 888){
            return new DatePickerDialog(this, myDateListener, year, month, day);
        } else if (id == 777){
            return  new TimePickerDialog(this, myTimeListener, hour, minute, true);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (minute < 10)
               choosenTime = hourOfDay  + " : 0" + minute;
            else
                choosenTime = hourOfDay + " : " + minute;

            dateAndTime.setText(choosenDate + " - " + choosenTime);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            choosenDate = day + "/" + (month + 1) + "/" + year;
            dateAndTime.setText(choosenDate + " - " + choosenTime);
        }
    };


    //displaying quantity available
    private void showQuantityDialog() {
        TextView close;
        final EditText qty;
        final ImageView qtyAdd, qtyMinus;
        Button add;

        dialog.setContentView(R.layout.dialog_quantity);
        close = dialog.findViewById(R.id.dialogCart_close);
        qty = dialog.findViewById(R.id.dialogCart_quantity);
        qtyAdd = dialog.findViewById(R.id.dialogCart_qtyAdd);
        qtyMinus = dialog.findViewById(R.id.dialogCart_qtySub);
        add = dialog.findViewById(R.id.dialogCart_add);

        qty.setText(pProductAvaliableQty);

        if (TextUtils.isEmpty(pProductAvaliableQty)){
            qtyAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qq = qty.getText().toString();
                    int quantity = Integer.parseInt(qq);
                    ++quantity;
                    qty.setText(quantity + "");
                }
            });

            qtyMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qq = qty.getText().toString();
                    int quantity = Integer.parseInt(qq);
                    --quantity;
                    if (quantity > 0)
                        qty.setText(quantity + "");
                }
            });
        } else {
            int ll = Integer.parseInt(pProductAvaliableQty);
            qtyMinus.setEnabled(false);
            if (ll > 0){
                qtyAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String qq = qty.getText().toString();
                        int quantity = Integer.parseInt(qq);
                        ++quantity;
                        qty.setText(quantity + "");
                        qtyMinus.setEnabled(true);
                    }
                });

                qtyMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String qq = qty.getText().toString();
                        int quantity = Integer.parseInt(qq);
                        --quantity;
                        if (quantity > 0)
                            qty.setText(quantity + "");
                    }
                });
            } else{
                qtyMinus.setEnabled(false);
            }
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qq = qty.getText().toString();

                if (TextUtils.isEmpty(qq))
                    Toast.makeText(AdvertiseProductActivity.this, "Enter available quantity", Toast.LENGTH_SHORT).show();
                else{
                    pProductAvaliableQty = qq;
                    AD_PRODUCTQUANTITYAVAILABLE = qq;
                    productAvaliableQty.setText(qq);
                    LAYOUTSHOW = false;
                    if (LAYOUTSHOW)
                        menuItem.setIcon(R.drawable.preview);
                    else
                        menuItem.setIcon(R.drawable.preview_pink);
                    Toast.makeText(AdvertiseProductActivity.this, "Quantity available now is " + qq, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //displaying price dialog
    private void showPriceDiscountDialog() {
        TextView close, initialPrice;
        final EditText discountPrice, discountPercentage;
        ImageView done, calculate;
        Button removeDiscount;

        dialog.setContentView(R.layout.dialog_discount);
        close = dialog.findViewById(R.id.dialogDiscount_close);
        initialPrice = dialog.findViewById(R.id.dialogDiscount_initialPrice);
        discountPercentage = dialog.findViewById(R.id.dialogDiscount_discountPricePercent);
        discountPrice = dialog.findViewById(R.id.dialogDiscount_discountPrice);
        calculate = dialog.findViewById(R.id.dialogDiscount_calculator);
        done = dialog.findViewById(R.id.dialogDiscount_done);
        removeDiscount = dialog.findViewById(R.id.dialogDiscount_removeDiscount);

        String iniPx = pProductPrice;
        initialPrice.setText(pProductPrice);


        if (!TextUtils.isEmpty(AD_DISCOUNTPERCENT) && !TextUtils.isEmpty(AD_DISCOUNTPRIZE) && AD_ISDISCOUNT.equalsIgnoreCase("true")){
            boolean rr = AD_DISCOUNTPRIZE.startsWith("GHC");
            if (rr){
            String dd = AD_DISCOUNTPRIZE.substring(3);
            discountPrice.setText(dd);
            } else {
                discountPrice.setText(AD_DISCOUNTPRIZE);
            }
            discountPercentage.setText(AD_DISCOUNTPERCENT);
        } else {
            discountPercentage.setText(pDiscountPercent);
            try{
                boolean rr = pDiscountPrice.startsWith("GHC");
                if (rr){
                    String corDix = pDiscountPrice.substring(3);
                    discountPrice.setText(corDix);
                    //Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
                } else
                    discountPrice.setText(pDiscountPrice);
            } catch (NullPointerException e){
                discountPrice.setText(pDiscountPrice);
            }
        }



        final String finalIniPx = iniPx;
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disPx = discountPrice.getText().toString();
                if (!TextUtils.isEmpty(disPx)){
                    try{
                        Double oldPx = Double.parseDouble(finalIniPx.substring(3));
                        Double newPx = Double.parseDouble(disPx);
                        Double increase = oldPx - newPx;
                        Double increaseDivide = increase / oldPx;
                        Double increasePercent = increaseDivide * 100;
                        discountPercentage.setText(Math.round(increasePercent) + "% off");
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
                String getNewPx = discountPrice.getText().toString();
                String getPercent = discountPercentage.getText().toString();

                if (TextUtils.isEmpty(getNewPx) && TextUtils.isEmpty(getPercent))
                    Toast.makeText(view.getContext(), "Check your entries", Toast.LENGTH_SHORT).show();
                else{
                    AD_ISDISCOUNT = "true";
                    AD_DISCOUNTPERCENT = getPercent;
                    AD_DISCOUNTPRIZE = getNewPx;
                    pDiscountPercent = getPercent;
                    pDiscountPrice = getNewPx;

                    productPrice.setText("GHC" + getNewPx);
                    priceImageIndicator.setBackgroundResource(R.drawable.discount);
                    LAYOUTSHOW = false;
                    if (LAYOUTSHOW)
                        menuItem.setIcon(R.drawable.preview);
                    else
                        menuItem.setIcon(R.drawable.preview_pink);
                    Toast.makeText(AdvertiseProductActivity.this, "You added a discount of " + getPercent + "% with a new price GHC" +
                            getNewPx, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });

        removeDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AD_ISDISCOUNT = "false";
                AD_DISCOUNTPERCENT = "";
                AD_DISCOUNTPRIZE = "";
                pDiscountPercent = "";
                pDiscountPrice = "";

                productPrice.setText(pProductPrice);
                priceImageIndicator.setBackgroundResource(R.drawable.original);
                LAYOUTSHOW = false;
                if (LAYOUTSHOW)
                    menuItem.setIcon(R.drawable.preview);
                else
                    menuItem.setIcon(R.drawable.preview_pink);
                Toast.makeText(AdvertiseProductActivity.this, "No discount for this product", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void viewPictureDialog(final String pictureUrl){
        TextView close, title;
        ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pProductName);

        Picasso.get().load(pictureUrl).into(picture);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdvertiseProductActivity.this, ViewPicture.class);
                intent.putExtra("imageText", pProductName);
                intent.putExtra("image", pictureUrl);
                startActivity(intent);
            }
        });

        dialog.show();
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
                        priceImageIndicator.setBackgroundResource(R.drawable.original);
                    }
                    if (dataSnapshot.hasChild("ProductDescription")){
                        pProductDescription = dataSnapshot.child("ProductDescription").getValue().toString();
                        productDescription.setText(pProductDescription);
                    }
                    if (dataSnapshot.hasChild("ProductCategory")){
                        pProductCategory = dataSnapshot.child("ProductCategory").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("ProductVideo")){
                        pProductVideo = dataSnapshot.child("ProductVideo").getValue().toString();
                        if (!pProductVideo.isEmpty())
                            productVideo.setVisibility(View.VISIBLE);
                    }
                    if (dataSnapshot.hasChild("ProductPicture1")){
                        pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture1)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture1, new Callback() {
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
                        Picasso.get()
                                .load(pProductPicture2)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture2, new Callback() {
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
                                        .load(pProductPicture3)
                                        .placeholder(R.drawable.warning).into(productPicture3);
                            }
                        });
                    }

                    if (dataSnapshot.hasChild("ProductDiscount")){
                        String pDiscount = dataSnapshot.child("ProductDiscount").getValue().toString();
                        if (pDiscount.equals("yes")){
                            if (dataSnapshot.hasChild("ProductDiscountPercentage")){
                                pDiscountPercent = dataSnapshot.child("ProductDiscountPercentage").getValue().toString();
                                AD_DISCOUNTPERCENT = pDiscountPercent;
                                if (dataSnapshot.hasChild("ProductDiscountPrice")){
                                    pDiscountPrice = dataSnapshot.child("ProductDiscountPrice").getValue().toString();
                                    AD_DISCOUNTPRIZE = pDiscountPrice;
                                    if (!TextUtils.isEmpty(pDiscountPrice)){
                                        priceImageIndicator.setBackgroundResource(R.drawable.discount);
                                        productPrice.setText(pDiscountPrice);
                                        AD_ISDISCOUNT = "false";
                                    }
                                }
                            }
                        }
                    }

                    if (dataSnapshot.hasChild("ProductQuantity")){
                        pProductAvaliableQty = dataSnapshot.child("ProductQuantity").getValue().toString();
                        productAvaliableQty.setText(pProductAvaliableQty);
                        AD_PRODUCTQUANTITYAVAILABLE = pProductAvaliableQty;
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.advertise_activity, menu);
        menuItem = menu.findItem(R.id.menuAdvertise_preview);

        if (LAYOUTSHOW)
            menuItem.setIcon(R.drawable.preview);
        else
            menuItem.setIcon(R.drawable.preview_pink);

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(LAYOUTSHOW){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdvertiseProductActivity.this);

                    final ImageView imageView = new ImageView(AdvertiseProductActivity.this);
                    imageView.setBackgroundResource(R.drawable.advertise_layout);
                    //zooming photo
                    PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
                    attacher.update();
                    builder.setView(imageView);
                    Dialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                } else {
                    try{
                        showAdvertisementDialog();
                    } catch (Exception e){
                        Log.i("error", e.getMessage());
                    }
                }
                return true;
            }
        });
        return true;
    }

    private void showAdvertisementDialog() {
        TextView slotNumber, discountPercent, initialPrice, discountPrice, productName, close, dateAndTime;
        final ImageView eventPicture, productPicture, preview, next, priceStroke;

        dialog.setContentView(R.layout.dialog_advertise_product);
        slotNumber = (TextView) dialog.findViewById(R.id.dialogAdvertise_advertisementNumber);
        discountPercent = (TextView) dialog.findViewById(R.id.dialogAdvertise_discountPercent);
        initialPrice = (TextView) dialog.findViewById(R.id.dialogAdvertise_initialPrice);
        discountPrice = (TextView) dialog.findViewById(R.id.dialogAdvertise_discountPrice);
        productName = (TextView) dialog.findViewById(R.id.dialogAdvertise_productName);
        close = (TextView) dialog.findViewById(R.id.dialogAdvertise_close);
        dateAndTime = (TextView) dialog.findViewById(R.id.dialogAdvertise_advertisementDateAndTime);
        eventPicture = (ImageView) dialog.findViewById(R.id.dialogAdvertise_eventPicture);
        productPicture = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture);
        preview = (ImageView) dialog.findViewById(R.id.dialogAdvertise_previous);
        next = (ImageView) dialog.findViewById(R.id.dialogAdvertise_next);
        productPic1 = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture1);
        productPic2 = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture2);
        productPic3 = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture3);
        priceStroke = (ImageView) dialog.findViewById(R.id.dialogAdvertise_priceStroke);
        productPictureFlipper = (ViewFlipper) dialog.findViewById(R.id.dialogAdvertise_productPictureFlipper);

        preview.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        priceStroke.setVisibility(View.GONE);
        discountPrice.setVisibility(View.GONE);
        discountPercent.setVisibility(View.GONE);
        productPictureFlipper.setVisibility(View.GONE);
        productPic1.setVisibility(View.VISIBLE);
        productPic2.setVisibility(View.GONE);
        productPic3.setVisibility(View.GONE);

        slotNumber.setText(this.slotNumber.getText().toString());
        discountPercent.setText(AD_DISCOUNTPERCENT);
        initialPrice.setText(pProductPrice);
        discountPrice.setText("GHC" + AD_DISCOUNTPRIZE);
        productName.setText(this.productName.getText().toString());
        dateAndTime.setText("end on : " + AD_TIMEANDDATE);
        //loading picture offline
        Picasso.get()
                .load(pProductPicture1)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(productPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture1)
                        .placeholder(R.drawable.warning).into(productPicture);
            }
        });

        if (AD_ISDISCOUNT.equalsIgnoreCase("true")){
            discountPercent.setVisibility(View.VISIBLE);
            priceStroke.setVisibility(View.VISIBLE);
            discountPrice.setVisibility(View.VISIBLE);
        } else {
            priceStroke.setVisibility(View.GONE);
            discountPrice.setVisibility(View.GONE);
            discountPercent.setVisibility(View.GONE);
        }


        productPic1.setBackgroundResource(R.drawable.check);
        productPic2.setBackgroundResource(R.drawable.uncheck);
        productPic3.setBackgroundResource(R.drawable.uncheck);

        if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && TextUtils.isEmpty(pProductPicture3)){
            productPictureFlipper.setVisibility(View.VISIBLE);
            productPic1.setVisibility(View.VISIBLE);
            productPic2.setVisibility(View.VISIBLE);
            productPicture.setVisibility(View.GONE);

            final String[] images = {pProductPicture1, pProductPicture2};
            for (String image : images){
                flipImages(image);
            }
        } else if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && !TextUtils.isEmpty(pProductPicture3)){
            productPictureFlipper.setVisibility(View.VISIBLE);
            productPic1.setVisibility(View.VISIBLE);
            productPic2.setVisibility(View.VISIBLE);
            productPic3.setVisibility(View.VISIBLE);
            productPicture.setVisibility(View.GONE);

            final String[] images = {pProductPicture1, pProductPicture2, pProductPicture3};
            for (String image : images){
                flipImages(image);
            }
        }

        setSmallImages();

        if (AD_EVENT.equalsIgnoreCase("Christmas Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_christmas);
        else if (AD_EVENT.equalsIgnoreCase("Easter Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_easter);
        else if (AD_EVENT.equalsIgnoreCase("Black Market Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_black_market);
        else if (AD_EVENT.equalsIgnoreCase("Price Reduction Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_price_reduction);
        else if (AD_EVENT.equalsIgnoreCase("Hot Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_hot);
        else if (AD_EVENT.equalsIgnoreCase("Valentine Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_valentine);
        else if (AD_EVENT.equalsIgnoreCase("Mother's Day Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_mothers_day);
        else if (AD_EVENT.equalsIgnoreCase("Father's Day Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_fathers_day);
        else
            eventPicture.setBackgroundResource(R.drawable.packages_customise);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        productPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipOneImage(pProductPicture1);
                productPic1.setBackgroundResource(R.drawable.check);
                productPic2.setBackgroundResource(R.drawable.uncheck);
                productPic3.setBackgroundResource(R.drawable.uncheck);
            }
        });

        productPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipOneImage(pProductPicture2);
                productPic1.setBackgroundResource(R.drawable.uncheck);
                productPic2.setBackgroundResource(R.drawable.check);
                productPic3.setBackgroundResource(R.drawable.uncheck);
            }
        });

        productPic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipOneImage(pProductPicture3);
                productPic1.setBackgroundResource(R.drawable.uncheck);
                productPic2.setBackgroundResource(R.drawable.uncheck);
                productPic3.setBackgroundResource(R.drawable.check);
            }
        });

        dialog.show();
    }

    private void setSmallImages() {
        new Thread(){
            @Override
            public void run() {
                int num = 1;
                while (true){
                    if (!TextUtils.isEmpty(pProductPicture1) && num == 1){
                        try {
                            productPic1.setBackgroundResource(R.drawable.check);
                            productPic2.setBackgroundResource(R.drawable.uncheck);
                            productPic3.setBackgroundResource(R.drawable.uncheck);
                            Thread.sleep(5000);
                            if (!TextUtils.isEmpty(pProductPicture2))
                                num = 2;
                            else
                                num = 1;
                        } catch (Exception e) {
                            Log.i("error" , e.getMessage());
                        }
                    } else if (!TextUtils.isEmpty(pProductPicture2) && num == 2){
                        try {
                            productPic1.setBackgroundResource(R.drawable.uncheck);
                            productPic2.setBackgroundResource(R.drawable.check);
                            productPic3.setBackgroundResource(R.drawable.uncheck);
                            Thread.sleep(5000);
                            if (!TextUtils.isEmpty(pProductPicture3))
                                num = 3;
                            else
                                num = 2;
                        } catch (Exception e) {
                            Log.i("error" , e.getMessage());
                        }
                    } else if (!TextUtils.isEmpty(pProductPicture3) && num == 3){
                        try {
                            productPic1.setBackgroundResource(R.drawable.uncheck);
                            productPic2.setBackgroundResource(R.drawable.uncheck);
                            productPic3.setBackgroundResource(R.drawable.check);
                            Thread.sleep(5000);
                            num = 1;
                        } catch (Exception e) {
                            Log.i("error" , e.getMessage());
                        }
                    } else {
                        productPic1.setBackgroundResource(R.drawable.check);
                        productPic2.setBackgroundResource(R.drawable.uncheck);
                        productPic3.setBackgroundResource(R.drawable.uncheck);
                    }
                }
            }
        }.start();
    }

    /*private void flipThread(final String[] images){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{

                } catch (Exception e){
                    Log.i("errorThread", e.getMessage());
                }
            }
        };

        thread.start();
    }*/

    private void flipImages(final String image){
        final ImageView imageView = new ImageView(AdvertiseProductActivity.this);
        try{
            //loading picture offline
            Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(image).fit()
                                    .placeholder(R.drawable.warning).into(imageView);
                        }
                    });

            productPictureFlipper.addView(imageView);
            productPictureFlipper.setFlipInterval(5000);//5 sec
            productPictureFlipper.setAutoStart(true);

            //animation
            productPictureFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            productPictureFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        } catch (Exception e){
            Log.i("error", e.getMessage());
        }
    }

    private void flipOneImage(final String image){
        productPictureFlipper.removeAllViews();
        final ImageView imageView = new ImageView(AdvertiseProductActivity.this);
        try{
            //loading picture offline
            Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(image).fit()
                                    .placeholder(R.drawable.warning).into(imageView);
                        }
                    });

            productPictureFlipper.addView(imageView);
            productPictureFlipper.setFlipInterval(5000);//5 sec


            if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && TextUtils.isEmpty(pProductPicture3)){
                productPictureFlipper.removeAllViews();
                final String[] images = {pProductPicture1, pProductPicture2};
                for (String img : images){
                    flipImages(img);
                }
            } else if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && !TextUtils.isEmpty(pProductPicture3)){
                productPictureFlipper.removeAllViews();
                final String[] images = {pProductPicture1, pProductPicture2, pProductPicture3};
                for (String img : images){
                    flipImages(img);
                }
            }

            setSmallImages();

            //animation
            productPictureFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            productPictureFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        } catch (Exception e){
            Log.i("error", e.getMessage());
        }
    }
}
