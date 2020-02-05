package com.tmsoft.tm.elitrends.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Message.AllMessage;
import com.tmsoft.tm.elitrends.R;
import com.tmsoft.tm.elitrends.Urls.InnerNotification;
import com.tmsoft.tm.elitrends.Urls.SendEmail;
import com.tmsoft.tm.elitrends.fragment.OrderFragment.BuyForMeFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BuyForMeActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ImageView productImage, transactionPicture;
    private TextView productName, productDescription, quantityOrdered, urgency,
            userName, userLocation, userTown, userPhoneNumber, userEmailAddress, deliveryType, postBoxDetails,
            paymentType, sendFullName, amountSend, transactionId, senderNetwork, deliveryDate,
            confirmDelivery, productConfirmation, paymentConfirmation, orderDate, totalAmount;
    private EditText deliveryFee, productFee;
    private Button editPayment, confirmOrder, editDeliveryDate, editDelivery;
    private LinearLayout paymentDetails, deliveryDetails;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private AlertDialog orderConfirmAlertDialog, paymentConfirmAlertDialog, deliveryConfirmAlertDialog;
    private Calendar calendar;

    private String orderProductKey;
    private InnerNotification innerNotification;

    private String userId, getCurrentUserId;
    private DatabaseReference databaseReference, userReference, notificationReference, statusReference;

    private String pProfilePicture, pTransactionPicture, pDeliveryDate, pDeliveryFee, pDeliverySuccess,
            pOrderConfirm, pProductFee, pProductImage, pProductName;
    private String zPostRegion, zPostTown, zPostDistrict, zPostNumber;
    private String notifySender1, notifySender2, notifySender3;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_for_me);

        myToolbar = (Toolbar) findViewById(R.id.buyForMe_toolbar);
        productImage = (ImageView) findViewById(R.id.buyForMe_productPicture);
        transactionPicture = (ImageView) findViewById(R.id.buyForMe_transactionPicture);
        productName = (TextView) findViewById(R.id.buyForMe_productName);
        productDescription = (TextView) findViewById(R.id.buyForMe_productDescription);
        quantityOrdered = (TextView) findViewById(R.id.buyForMe_productQtyOrdered);
        urgency = (TextView) findViewById(R.id.buyForMe_urgent);
        userName = (TextView) findViewById(R.id.buyForMe_userFullName);
        userLocation = (TextView) findViewById(R.id.buyForMe_userLocation);
        userTown = (TextView) findViewById(R.id.buyForMe_userTown);
        userPhoneNumber = (TextView) findViewById(R.id.buyForMe_userPhoneNumber);
        userEmailAddress = (TextView) findViewById(R.id.buyForMe_userEmail);
        productName = (TextView) findViewById(R.id.buyForMe_productName);
        deliveryType = (TextView) findViewById(R.id.buyForMe_deliveryType);
        paymentType = (TextView) findViewById(R.id.buyForMe_paymentType);
        sendFullName = (TextView) findViewById(R.id.buyForMe_senderFullName);
        amountSend = (TextView) findViewById(R.id.buyForMe_amountPaid);
        transactionId = (TextView) findViewById(R.id.buyForMe_transactionId);
        senderNetwork = (TextView) findViewById(R.id.buyForMe_senderNetwork);
        deliveryDate = (TextView) findViewById(R.id.buyForMe_deliveryDate);
        postBoxDetails = (TextView) findViewById(R.id.buyForMe_postDelivery);
        deliveryFee = (EditText) findViewById(R.id.buyForMe_deliveryFee);
        productFee = (EditText) findViewById(R.id.buyForMe_productFee);
        editPayment = (Button) findViewById(R.id.buyForMe_editPayment);
        confirmDelivery = (TextView) findViewById(R.id.buyForMe_deliveryConfirm);
        confirmOrder = (Button) findViewById(R.id.buyForMe_confirmProduct);
        editDeliveryDate = (Button) findViewById(R.id.buyForMe_deliveryDateEdit);
        paymentDetails = (LinearLayout) findViewById(R.id.buyForMe_paymentDetails);
        deliveryDetails = (LinearLayout) findViewById(R.id.buyForMe_deliveryDetails);
        productConfirmation = (TextView) findViewById(R.id.buyForMe_productConfirmation);
        orderDate = (TextView) findViewById(R.id.buyForMe_orderDate);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        progressDialog = new ProgressDialog(this);
        paymentConfirmation = (TextView) findViewById(R.id.buyForMe_paymentConfirmation);
        calendar = Calendar.getInstance();
        editDelivery = (Button) findViewById(R.id.buyForMe_editDelivery);
        totalAmount = (TextView) findViewById(R.id.buyForMe_totalAmount);

        orderProductKey = getIntent().getExtras().get("postKey").toString();

        myToolbar = (Toolbar) findViewById(R.id.buyForMe_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buy for me");

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        paymentDetails.setVisibility(View.GONE);
        deliveryDetails.setVisibility(View.GONE);
        postBoxDetails.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Buy for me");
        databaseReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);
        getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        getAllInfo();
        collectAllServerId();

        postBoxDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostDetails();
            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToViewPictureActivity(pProductImage, pProductName);
            }
        });

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToViewPictureActivity(pTransactionPicture, pProductName);
            }
        });

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getDeliveryFee = deliveryFee.getText().toString();
                String getProductFee = productFee.getText().toString();

                if (TextUtils.isEmpty(getDeliveryFee))
                    Toast.makeText(BuyForMeActivity.this, "Please enter delivery fee. If delivery fee is free enter \'0.00\'", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getProductFee))
                    Toast.makeText(BuyForMeActivity.this, "Please enter product fee", Toast.LENGTH_SHORT).show();
                else{
                    double sum = Double.parseDouble(getDeliveryFee) + Double.parseDouble(getProductFee);
                    totalAmount.setText("Total Amount : GHC" + sum);
                    saveFees(getDeliveryFee, getProductFee);
                }
            }
        });

        editPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentConfirmDialog();
            }
        });

        editDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        editDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeliveryConfirmDialog();
            }
        });
     }

    private void saveFees(String getDeliveryFee, String getProductFee) {
        progressDialog.setMessage("Saving fees...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Map feeMap = new HashMap();
        feeMap.put("deliveryFee", getDeliveryFee);
        feeMap.put("productFee", getProductFee);
        feeMap.put("message", "Product confirmed, proceed with payment details");
        databaseReference.child(orderProductKey).updateChildren(feeMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(BuyForMeActivity.this, "Fees saved successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    showConfirmOrderDialog();
                }
            }
        });
    }

    private void showDeliveryConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
        builder.setTitle("Confirm Delivery")
                .setItems(R.array.deliveryConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                saveDelivery("Your order has been delivered successfully \n" +
                                        "Thanks for transacting business with us", "Order Delivered");
                                break;
                            case 1:
                                saveDelivery("Your order has not been delivered yet \n" +
                                        "Please contact us on any information", "Order Not Delivered Yet");
                                break;
                            default:
                                Toast.makeText(BuyForMeActivity.this, "Select delivery detail", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        deliveryConfirmAlertDialog = builder.create();
        deliveryConfirmAlertDialog.show();
    }

    private void saveDelivery(final String msg, final String confirmDetails){
        progressDialog.setMessage("Saving");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        databaseReference.child(orderProductKey).child("deliverySuccess").setValue(confirmDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //sending notification
                        final HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", getCurrentUserId);
                        notificationData.put("title", pProductName);
                        notificationData.put("message", msg);

                        notificationReference.child(userId).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            if (!TextUtils.isEmpty(notifySender1) && !notifySender1.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender1).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender2) && !notifySender2.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3) && !notifySender3.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }
                                            innerNotification = new InnerNotification(
                                                    "order",
                                                    "no",
                                                    getCurrentUserId,
                                                    "BuyForMeActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(orderProductKey);
                                                dRef.child("message").setValue(confirmDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            Toast.makeText(BuyForMeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                            deliveryConfirmAlertDialog.dismiss();
                                                        } else {
                                                            String err = task.getException().getMessage();
                                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                            deliveryConfirmAlertDialog.dismiss();
                                                        }
                                                    }
                                                });
                                            } else {
                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(orderProductKey);
                                                dRef.child("message").setValue(confirmDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            Toast.makeText(BuyForMeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                            deliveryConfirmAlertDialog.dismiss();
                                                        } else {
                                                            String err = task.getException().getMessage();
                                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                            deliveryConfirmAlertDialog.dismiss();
                                                        }
                                                    }
                                                });
                                            }

                                        } else {
                                            String er = task.getException().getMessage();
                                            Toast.makeText(BuyForMeActivity.this, er, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            deliveryConfirmAlertDialog.dismiss();
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999){
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            String dd = day + "/" + (month + 1) + "/" + year;
            saveDeliveryDate(dd);
        }
    };

    private void saveDeliveryDate(final String date) {
        databaseReference.child(orderProductKey).child("deliveryDate").setValue(date)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        final String dd = "Your order delivery date is " + date;

                        //sending notification
                        final HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", getCurrentUserId);
                        notificationData.put("title", pProductName);
                        notificationData.put("message", dd);

                        notificationReference.child(userId).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            if (!TextUtils.isEmpty(notifySender1) && !notifySender1.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender1).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender2) && !notifySender2.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3) && !notifySender3.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }
                                            innerNotification = new InnerNotification(
                                                    "order",
                                                    "no",
                                                    getCurrentUserId,
                                                    "BuyForMeActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    dd,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(orderProductKey);
                                                dRef.child("message").setValue(dd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            Toast.makeText(BuyForMeActivity.this, dd, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            String err = task.getException().getMessage();
                                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(orderProductKey);
                                                dRef.child("message").setValue(dd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            progressDialog.dismiss();
                                                            Toast.makeText(BuyForMeActivity.this, dd, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            String err = task.getException().getMessage();
                                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void showPaymentConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
        builder.setTitle("Confirm Payment")
                .setItems(R.array.paymentConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                savePayment("Please wait for payment confirmation", "Waiting for confirmation");
                                break;
                            case 1:
                                savePayment("Payment Received", "Payment Received");
                                break;
                            case 2:
                                savePayment("Payment not received", "Payment Not Received");
                                break;
                            default:
                                Toast.makeText(BuyForMeActivity.this, "Confirm Payment", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentConfirmAlertDialog.dismiss();
                    }
                });
        paymentConfirmAlertDialog = builder.create();
        paymentConfirmAlertDialog.show();
    }

    private void savePayment(final String msg, final String confirmDetails){
        progressDialog.setMessage("Saving");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        databaseReference.child(orderProductKey).child("paymentConfirm").setValue(confirmDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //sending notification
                        final HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", getCurrentUserId);
                        notificationData.put("title", pProductName);
                        notificationData.put("message", msg);

                        notificationReference.child(userId).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            if (!TextUtils.isEmpty(notifySender1) && !notifySender1.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender1).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender2) && !notifySender2.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3) && !notifySender3.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            innerNotification = new InnerNotification(
                                                    "order",
                                                    "no",
                                                    getCurrentUserId,
                                                    "BuyForMeActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(orderProductKey);
                                                dRef.child(orderProductKey).child("message").setValue(confirmDetails)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(BuyForMeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                                    paymentConfirmAlertDialog.dismiss();
                                                                    if (confirmDetails.equalsIgnoreCase("Payment Received")){
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                                                                        builder.setMessage("Scroll down to access the delivery date")
                                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        deliveryDetails.setVisibility(View.VISIBLE);
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                });
                                                                        AlertDialog alertDialog = builder.create();
                                                                        alertDialog.show();
                                                                    }
                                                                } else {
                                                                    String err = task.getException().getMessage();
                                                                    Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                                    paymentConfirmAlertDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(orderProductKey);
                                                dRef.child(orderProductKey).child("message").setValue(confirmDetails)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(BuyForMeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                                    paymentConfirmAlertDialog.dismiss();
                                                                    if (confirmDetails.equalsIgnoreCase("Payment Received")){
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                                                                        builder.setMessage("Scroll down to access the delivery date")
                                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        deliveryDetails.setVisibility(View.VISIBLE);
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                });
                                                                        AlertDialog alertDialog = builder.create();
                                                                        alertDialog.show();
                                                                    }
                                                                } else {
                                                                    String err = task.getException().getMessage();
                                                                    Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                                    paymentConfirmAlertDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                            }

                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                            paymentConfirmAlertDialog.dismiss();
                                        }
                                    }
                                });
                    }
                });
    }

    private void showConfirmOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
        builder.setTitle("Confirm Order")
                .setItems(R.array.orderConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                saveConfirmation("Please wait for confirmation", "Waiting for confirmation");
                                break;
                            case 1:
                                saveConfirmation("Your order have been confirmed", "Order Confirmed");
                                break;
                            case 2:
                                saveConfirmation("Your order have been Canceled", "Order Canceled");
                                break;
                            default:
                                Toast.makeText(BuyForMeActivity.this, "Select order confirmation detail", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderConfirmAlertDialog.dismiss();
                    }
                });
        orderConfirmAlertDialog = builder.create();
        orderConfirmAlertDialog.show();
    }

    private void saveConfirmation(final String msg, final String confirmDetail){
        progressDialog.setMessage("Saving...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        //sending notification
        final HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", getCurrentUserId);
        notificationData.put("title", pProductName);
        notificationData.put("message", msg);

        databaseReference.child(orderProductKey).child("orderConfirm").setValue(confirmDetail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notificationReference.child(userId).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            try{
                                                statusReference.child("buyForMe").child("pendingOrder").child(orderProductKey).removeValue();
                                                statusReference.child("buyForMe").child("confirmOrder").child(orderProductKey).removeValue();
                                                statusReference.child("buyForMe").child("canceledOrder").child(orderProductKey).removeValue();
                                            } catch (Exception ex){

                                            }
                                            switch (confirmDetail) {
                                                case "Waiting for confirmation":
                                                    statusReference.child("buyForMe").child("pendingOrder").child(orderProductKey).child("item").setValue(orderProductKey);
                                                    break;
                                                case "Order Confirmed":
                                                    statusReference.child("buyForMe").child("confirmOrder").child(orderProductKey).child("item").setValue(orderProductKey);
                                                    break;
                                                case "Order Canceled":
                                                    statusReference.child("buyForMe").child("canceledOrder").child(orderProductKey).child("item").setValue(orderProductKey);
                                                    break;
                                            }
                                            if (!TextUtils.isEmpty(notifySender1) && !notifySender1.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender1).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender2) && !notifySender2.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3) && !notifySender3.equals(getCurrentUserId)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            innerNotification = new InnerNotification(
                                                    "order",
                                                    "no",
                                                    getCurrentUserId,
                                                    "BuyForMeActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                //Toast.makeText(BuyForMeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                orderConfirmAlertDialog.dismiss();
                                                if (confirmDetail.equalsIgnoreCase("Order Confirmed")){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                                                    builder.setMessage("Now wait for the customer to make payment, you can scroll down to access payment")
                                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    paymentDetails.setVisibility(View.VISIBLE);
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    AlertDialog alertDialog = builder.create();
                                                    alertDialog.show();
                                                }

                                                progressDialog.dismiss();
                                                orderConfirmAlertDialog.dismiss();
                                                //Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                Snackbar snackbar = Snackbar.make(myToolbar, msg, Snackbar.LENGTH_LONG)
                                                        .setAction("Send Email", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                SendEmail email = new SendEmail(pUserEmail,
                                                                        "Elinam Trendz",
                                                                        "You order " + pProductName + " have been confirmed",
                                                                        BuyForMeActivity.this, totalAmount);
                                                                email.sendEmail();
                                                            }
                                                        });
                                                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                                snackbar.show();
                                            } else {
                                                orderConfirmAlertDialog.dismiss();
                                                if (confirmDetail.equalsIgnoreCase("Order Confirmed")){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                                                    builder.setMessage("Now wait for the customer to make payment, you can scroll down to access payment")
                                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    paymentDetails.setVisibility(View.VISIBLE);
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    AlertDialog alertDialog = builder.create();
                                                    alertDialog.show();
                                                }

                                                progressDialog.dismiss();
                                                orderConfirmAlertDialog.dismiss();
                                                //Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                Snackbar snackbar = Snackbar.make(myToolbar, msg, Snackbar.LENGTH_LONG)
                                                        .setAction("Send Email", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                SendEmail email = new SendEmail(pUserEmail,
                                                                        "Elinam Trendz",
                                                                        "You order " + pProductName + " have been confirmed",
                                                                        BuyForMeActivity.this, totalAmount);
                                                                email.sendEmail();
                                                            }
                                                        });
                                                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                                snackbar.show();
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                            orderConfirmAlertDialog.dismiss();
                                        }
                                    }
                                });
                    }
                });
    }

    private String[] pUserEmail = new String[1];
    private void getUserPrivateData(){
        userReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profilePicture")) {
                        pProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("fullName")){
                        String pUserName = dataSnapshot.child("fullName").getValue().toString();
                        userName.setText(pUserName);
                    }

                    if (dataSnapshot.hasChild("phoneNumber")){
                        String number = dataSnapshot.child("phoneNumber").getValue().toString();
                        userPhoneNumber.setText(number);
                    }

                    if (dataSnapshot.hasChild("email")){
                        pUserEmail[0] = dataSnapshot.child("email").getValue().toString();
                        userEmailAddress.setText(pUserEmail[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }

    //Show Post Details
    private void  showPostDetails(){
        TextView pRegion, pTown, pDistrict, pPostNum;
        Button cls;

        dialog.setContentView(R.layout.dialog_show_post_delivery_details);
        pRegion = dialog.findViewById(R.id.showPostDelivery_region);
        pTown = dialog.findViewById(R.id.showPostDelivery_town);
        pDistrict = dialog.findViewById(R.id.showPostDelivery_district);
        pPostNum = dialog.findViewById(R.id.showPostDelivery_boxNumber);
        cls = dialog.findViewById(R.id.showPostDelivery_close);

        pRegion.setText(zPostRegion);
        pTown.setText(zPostTown);
        pDistrict.setText(zPostDistrict);
        pPostNum.setText(zPostNumber);

        cls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void collectAllServerId() {
        DatabaseReference notifySender = FirebaseDatabase.getInstance().getReference().child("ServerId");
        notifySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("0")){
                        notifySender1 = dataSnapshot.child("0").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("1")){
                        notifySender2 = dataSnapshot.child("1").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("2")){
                        notifySender3 = dataSnapshot.child("2").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

     private void getAllInfo(){
        databaseReference.child(orderProductKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("orderDate")){
                        String pOrderDate = dataSnapshot.child("orderDate").getValue().toString();
                        orderDate.setText(pOrderDate);

                        if (dataSnapshot.hasChild("orderTime")){
                            String pOrderTime = dataSnapshot.child("orderTime").getValue().toString();
                            orderDate.setText(pOrderDate + " - "+ pOrderTime);
                        }
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        pOrderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                        productConfirmation.setText("Product Confirmation: " + pOrderConfirm);
                    }

                    if (dataSnapshot.hasChild("userId")){
                        userId = dataSnapshot.child("userId").getValue().toString();
                        getUserPrivateData();
                        statusReference = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(userId);
                        statusReference.keepSynced(true);
                    }

                    if (dataSnapshot.hasChild("productName")){
                        pProductName = dataSnapshot.child("productName").getValue().toString();
                        productName.setText(pProductName);
                    }

                    if (dataSnapshot.hasChild("productDetails")){
                        String pProductDetails = dataSnapshot.child("productDetails").getValue().toString();
                        productDescription.setText(pProductDetails);
                    }
                    if (dataSnapshot.hasChild("productUrgent")){
                        String pProductUrgent = dataSnapshot.child("productUrgent").getValue().toString();
                        urgency.setText(pProductUrgent);
                    }
                    if (dataSnapshot.hasChild("productQuantity")){
                        String pProductQuantity = dataSnapshot.child("productQuantity").getValue().toString();
                        quantityOrdered.setText(pProductQuantity);
                    }

                    if (dataSnapshot.hasChild("productImage")){
                        pProductImage = dataSnapshot.child("productImage").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductImage)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductImage)
                                                .placeholder(R.drawable.warning)
                                                .into(productImage);
                                    }

                                });
                    }

                    if (dataSnapshot.hasChild("productFee")){
                        pProductFee = dataSnapshot.child("productFee").getValue().toString();
                        productFee.setText(pProductFee);
                    }

                    //delivery info
                    if (dataSnapshot.hasChild("userLocation")){
                        String pUserLocation = dataSnapshot.child("userLocation").getValue().toString();
                        userLocation.setText(pUserLocation);
                    }

                    if (dataSnapshot.hasChild("userTown")){
                        String pUserTown = dataSnapshot.child("userTown").getValue().toString();
                        userTown.setText(pUserTown);
                    }

                    if (dataSnapshot.hasChild("deliveryType")){
                        String pDeliveryType = dataSnapshot.child("deliveryType").getValue().toString();
                        deliveryType.setText(pDeliveryType);

                        if (pDeliveryType.equals("Post Box Delivery"))
                            postBoxDetails.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("postRegion")){
                        zPostRegion = dataSnapshot.child("postRegion").getValue().toString();
                    }

                    if ((dataSnapshot.hasChild("postTown"))){
                        zPostTown = dataSnapshot.child("postTown").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("postDistrict")){
                        zPostDistrict = dataSnapshot.child("postDistrict").getValue().toString();
                    }

                    if ((dataSnapshot.hasChild("postBoxNumber"))){
                        zPostNumber = dataSnapshot.child("postBoxNumber").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("deliveryDate")){
                        pDeliveryDate = dataSnapshot.child("deliveryDate").getValue().toString();
                        deliveryDate.setText(pDeliveryDate);
                    }

                    if (dataSnapshot.hasChild("deliveryFee")){
                        pDeliveryFee = dataSnapshot.child("deliveryFee").getValue().toString();
                        deliveryFee.setText(pDeliveryFee);
                    }

                    if (!TextUtils.isEmpty(pDeliveryFee) && !TextUtils.isEmpty(pProductFee)){
                        double sum = Double.parseDouble(pDeliveryFee) + Double.parseDouble(pProductFee);
                        totalAmount.setText("Total Amount : GHC" + sum);
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        pDeliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                        confirmDelivery.setText(pDeliverySuccess);
                    }

                    //payment
                    if (dataSnapshot.hasChild("paymentType")){
                        String pPaymentType = dataSnapshot.child("paymentType").getValue().toString();
                        paymentType.setText(pPaymentType);

                        if (pPaymentType.equalsIgnoreCase("Payment On Delivery")){
                            deliveryDetails.setVisibility(View.VISIBLE);
                            editPayment.setVisibility(View.GONE);
                            transactionPicture.setVisibility(View.GONE);
                        }
                    }

                    if (dataSnapshot.hasChild("paymentSenderName")){
                        String pSenderFullName = dataSnapshot.child("paymentSenderName").getValue().toString();
                        sendFullName.setText(pSenderFullName);
                    }

                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                        String pAmountPaid = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                        amountSend.setText(pAmountPaid);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionId")){
                        String pTransactionId = dataSnapshot.child("paymentTransactionId").getValue().toString();
                        transactionId.setText(pTransactionId);
                    }

                    if (dataSnapshot.hasChild("paymentSenderNetwork")){
                        String pSenderNetwork = dataSnapshot.child("paymentSenderNetwork").getValue().toString();
                        senderNetwork.setText(pSenderNetwork);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionPicture")){
                        pTransactionPicture = dataSnapshot.child("paymentTransactionPicture").getValue().toString();
                        //loading picture offline
                        try{
                            Picasso.get()
                                    .load(pTransactionPicture).fit()
                                    .networkPolicy(NetworkPolicy.OFFLINE).into(transactionPicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(pTransactionPicture).fit()
                                            .into(transactionPicture);
                                }
                            });
                            transactionPicture.setVisibility(View.VISIBLE);
                        } catch (Exception ex){
                            transactionPicture.setVisibility(View.GONE);
                            System.out.println(ex.getMessage());
                        }
                    }

                    if (dataSnapshot.hasChild("paymentConfirm")){
                        String pPaymentConfirm = dataSnapshot.child("paymentConfirm").getValue().toString();
                        paymentConfirmation.setText("Payment Confirmation : " + pPaymentConfirm);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buy_for_me, menu);
        MenuItem chatUser = menu.findItem(R.id.buyForMe_chatUser);
        MenuItem callUser = menu.findItem(R.id.buyForMe_callUser);
        MenuItem profilePicture = menu.findItem(R.id.buyForMe_profilePicture);

        chatUser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendUserToChatActivity(userId);
                return true;
            }
        });

        callUser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String pPhoneNumber = userPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(pPhoneNumber))
                    Toast.makeText(BuyForMeActivity.this, "No phone number available", Toast.LENGTH_SHORT).show();
                else
                    callOrderUser(pPhoneNumber);
                return true;
            }
        });

        profilePicture.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (TextUtils.isEmpty(pProfilePicture))
                    Toast.makeText(BuyForMeActivity.this, "No profile picture for this user", Toast.LENGTH_SHORT).show();
                else
                    loadProfilePicture();
                return true;
            }
        });
        return true;
    }

    private void loadProfilePicture(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);

        final ImageView imageView = new ImageView(BuyForMeActivity.this);
        //loading picture offline
        Picasso.get()
                .load(pProfilePicture)
                .fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.profile_image)
                .into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProfilePicture)
                        .fit()
                        .placeholder(R.drawable.profile_image)
                        .into(imageView);
            }
        });
        builder.setView(imageView);
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    //call user
    private void callOrderUser(String pUserPhoneNumber) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + pUserPhoneNumber));
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(phoneIntent);
            Toast.makeText(this, "Calling " + pUserPhoneNumber, Toast.LENGTH_LONG).show();
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "Call failed... Please try again " + pUserPhoneNumber, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToChatActivity(String userId){
        Intent intent = new Intent(BuyForMeActivity.this, ChatActivity.class);
        intent.putExtra("userId",  userId);
        startActivity(intent);
    }

    private void sendUserToViewPictureActivity(String image, String imageText){
        Intent intent = new Intent(BuyForMeActivity.this, ViewPicture.class);
        intent.putExtra("image", image);
        intent.putExtra("imageText", imageText);
        startActivity(intent);
    }
}
