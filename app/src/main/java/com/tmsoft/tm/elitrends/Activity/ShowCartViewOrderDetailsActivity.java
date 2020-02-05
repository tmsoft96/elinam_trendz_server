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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowCartViewOrderDetailsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private TextView orderTime, orderDate, productOrderId;
    private TextView orderConfirm, userFullName, deliveryLocation, userPhoneNumber, userEmailAddress, userTown;
    private TextView senderFullName, amountPaid, transactionId, senderNetwork, deliveryDate, deliveryFee, deliveryConfirm;
    private TextView paymentType, deliveryType, totalAmount, postDeliveryButton;
    private ImageView transactionPicture;
    private CircleImageView userProfilePicture;
    private LinearLayout linearLayout;
    private Dialog dialog;
    private Button chatUser, callUser, editOrder, editDelivery, editPayment, deliveryDateEdit;
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private AlertDialog orderConfirmAlertDialog, paymentConfirmAlertDialog, deliveryConfirmAlertDialog;

    private int year, month, day;

    private DatabaseReference databaseReference, notificationReference, statusReference;
    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private InnerNotification innerNotification;

    private String orderProductKey, pTransactionPicture, pTransactionId, cartOrderCounter, pUserProfilePicture, pUserPhoneNumber;
    private String getUserId, pUserFullName, pDeliveryDate;
    private String zPostRegion, zPostTown, zPostDistrict, zPostNumber;
    private String notifySender1, notifySender2, notifySender3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart_view_order_details);

        myToolBar = (Toolbar) findViewById(R.id.showCartViewOrderDetails_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart Product Ordered");

        orderDate = (TextView) findViewById(R.id.showCartViewOrderDetails_date);
        orderTime = (TextView) findViewById(R.id.showCartViewOrderDetails_time);
        productOrderId = (TextView) findViewById(R.id.showCartViewOrderDetails_productId);
        orderConfirm = (TextView) findViewById(R.id.showCartViewOrderDetails_orderConfirm);
        userFullName = (TextView) findViewById(R.id.showCartViewOrderDetails_userFullName);
        deliveryLocation = (TextView) findViewById(R.id.showCartViewOrderDetails_userLocation);
        userPhoneNumber = (TextView) findViewById(R.id.showCartViewOrderDetails_userPhoneNumber);
        userEmailAddress = (TextView) findViewById(R.id.showCartViewOrderDetails_userEmail);
        senderFullName = (TextView) findViewById(R.id.showCartViewOrderDetails_senderFullName);
        amountPaid = (TextView) findViewById(R.id.showCartViewOrderDetails_amountPaid);
        transactionId = (TextView) findViewById(R.id.showCartViewOrderDetails_transactionId);
        senderNetwork = (TextView) findViewById(R.id.showCartViewOrderDetails_senderNetwork);
        deliveryDate = (TextView) findViewById(R.id.showCartViewOrderDetails_deliveryDate);
        deliveryFee = (TextView) findViewById(R.id.showCartViewOrderDetails_deliveryFee);
        deliveryConfirm = (TextView) findViewById(R.id.showCartViewOrderDetails_deliveryConfirm);
        userTown = (TextView) findViewById(R.id.showCartViewOrderDetails_userTown);
        paymentType = (TextView) findViewById(R.id.showCartViewOrderDetails_paymentType);
        deliveryType = (TextView) findViewById(R.id.showCartViewOrderDetails_deliveryType);
        totalAmount = (TextView) findViewById(R.id.showCartViewOrderDetails_totalAmount);
        transactionPicture = (ImageView) findViewById(R.id.showCartViewOrderDetails_transactionPicture);
        linearLayout = (LinearLayout) findViewById(R.id.showCartViewOrderDetails_showAllProduct);
        userProfilePicture = (CircleImageView) findViewById(R.id.showCartViewOrderDetails_userProfilePicture);
        postDeliveryButton = (TextView) findViewById(R.id.showCartViewOrderDetails_postDelivery);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        callUser = (Button) findViewById(R.id.showCartViewOrderDetails_callUser);
        chatUser = (Button) findViewById(R.id.showCartViewOrderDetails_chatUser);
        editOrder = (Button) findViewById(R.id.showCartViewOrderDetails_editOrder);
        editPayment = (Button) findViewById(R.id.showCartViewOrderDetails_editPayment);
        editDelivery = (Button) findViewById(R.id.showCartViewOrderDetails_editDelivery);
        deliveryDateEdit = (Button) findViewById(R.id.showCartViewOrderDetails_editDeliveryDate);
        progressDialog = new ProgressDialog(this);
        calendar = Calendar.getInstance();

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        orderProductKey = getIntent().getExtras().get("cartPostKey").toString();
        getUserId = getIntent().getExtras().get("userId").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
        databaseReference.keepSynced(true);

        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        statusReference = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(getUserId);
        statusReference.keepSynced(true);

        transactionPicture.setVisibility(View.GONE);
        postDeliveryButton.setVisibility(View.GONE);

        getAllInformation(orderProductKey);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewPicActivity(pTransactionId, pTransactionPicture);
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewCartProductActivity();
            }
        });

        userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewPicActivity(pUserFullName, pUserProfilePicture);
            }
        });

        postDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPostDetails();
            }
        });

        editOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmOrderDialog();
            }
        });

        editDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeliveryConfirmDialog();
            }
        });

        editPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaymentConfirmDialog();
            }
        });

        deliveryDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        chatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToChatActivity(getUserId);
            }
        });

        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOrderUser(pUserPhoneNumber);
            }
        });

        collectAllServerId();
    }

    private void showConfirmOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowCartViewOrderDetailsActivity.this);
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
                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, "Select order confirmation detail", Toast.LENGTH_SHORT).show();
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

    private void showDeliveryConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowCartViewOrderDetailsActivity.this);
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
                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, "Select delivery detail", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        deliveryConfirmAlertDialog = builder.create();
        deliveryConfirmAlertDialog.show();
    }

    private void showPaymentConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowCartViewOrderDetailsActivity.this);
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
                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, "Confirm Payment", Toast.LENGTH_SHORT).show();
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


    private void saveConfirmation(final String msg, final String confirmDetail){
        progressDialog.setMessage("Saving...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        //sending notification
        final HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", getCurrentUserId);
        notificationData.put("title", "Cart Order");
        notificationData.put("message", msg);

        databaseReference.child(orderProductKey).child("orderConfirm").setValue(confirmDetail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notificationReference.child(getUserId).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            try{
                                                statusReference.child("cartOrder").child("pendingOrder").child(orderProductKey).removeValue();
                                                statusReference.child("cartOrder").child("confirmOrder").child(orderProductKey).removeValue();
                                                statusReference.child("cartOrder").child("canceledOrder").child(orderProductKey).removeValue();
                                            } catch (Exception ex){

                                            }
                                            switch (confirmDetail) {
                                                case "Waiting for confirmation":
                                                    statusReference.child("singleOrder").child("pendingOrder").child(orderProductKey).child("item").setValue(orderProductKey);
                                                    break;
                                                case "Order Confirmed":
                                                    statusReference.child("singleOrder").child("confirmOrder").child(orderProductKey).child("item").setValue(orderProductKey);
                                                    break;
                                                case "Order Canceled":
                                                    statusReference.child("singleOrder").child("canceledOrder").child(orderProductKey).child("item").setValue(orderProductKey);
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
                                                    "ShowCartOrderedDetailsActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    "Cart Order",
                                                    getUserId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                orderConfirmAlertDialog.dismiss();
                                                Snackbar snackbar = Snackbar.make(myToolBar, msg, Snackbar.LENGTH_LONG)
                                                        .setAction("Send Email", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                try{
                                                                    SendEmail email = new SendEmail(pUserEmail,
                                                                            "Elinam Trendz",
                                                                            msg,
                                                                            ShowCartViewOrderDetailsActivity.this, totalAmount);
                                                                    email.sendEmail();
                                                                } catch (Exception e){
                                                                    Log.i("error", e.getMessage());
                                                                }
                                                            }
                                                        });
                                                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                                snackbar.show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                orderConfirmAlertDialog.dismiss();
                                                Snackbar snackbar = Snackbar.make(myToolBar, msg, Snackbar.LENGTH_LONG)
                                                        .setAction("Send Email", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                try{
                                                                    SendEmail email = new SendEmail(pUserEmail,
                                                                            "Elinam Trendz",
                                                                            msg,
                                                                            ShowCartViewOrderDetailsActivity.this, totalAmount);
                                                                    email.sendEmail();
                                                                } catch (Exception e){
                                                                    Log.i("error", e.getMessage());
                                                                }
                                                            }
                                                        });
                                                snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                                snackbar.show();
                                            }

                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(ShowCartViewOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                                            orderConfirmAlertDialog.dismiss();
                                        }
                                    }
                                });
                    }
                });
    }

    private void saveDelivery(final String msg, String confirmDetails){
        progressDialog.setMessage("Saving");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        databaseReference.child(orderProductKey).child("deliverySuccess").setValue(confirmDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //sending notification
                            final HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("from", getCurrentUserId);
                            notificationData.put("title", "Cart Order");
                            notificationData.put("message", msg);

                            notificationReference.child(getUserId).push().setValue(notificationData)
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

                                                if (!TextUtils.isEmpty(notifySender2) && !notifySender3.equals(getCurrentUserId)){
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
                                                        "ShowCartOrderedDetailsActivity",
                                                        ServerValue.TIMESTAMP.toString(),
                                                        msg,
                                                        "Cart Order",
                                                        getUserId,
                                                        orderProductKey
                                                );
                                                boolean deter = innerNotification.onSaveAll();
                                                if (deter){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ShowCartViewOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                    deliveryConfirmAlertDialog.dismiss();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ShowCartViewOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                    deliveryConfirmAlertDialog.dismiss();
                                                }

                                            } else {
                                                String er = task.getException().getMessage();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, er, Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                deliveryConfirmAlertDialog.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(ShowCartViewOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void savePayment(final String msg, String confirmDetails){
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
                        notificationData.put("title", "Cart Order");
                        notificationData.put("message", msg);

                        notificationReference.child(getUserId).push().setValue(notificationData)
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
                                                    "ShowCartOrderedDetailsActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    "Cart Order",
                                                    getUserId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                paymentConfirmAlertDialog.dismiss();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                paymentConfirmAlertDialog.dismiss();
                                            }
                                        } else {
                                            String er = task.getException().getMessage();
                                            Toast.makeText(ShowCartViewOrderDetailsActivity.this, er, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            paymentConfirmAlertDialog.dismiss();
                                        }
                                    }
                                });
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

    private String[] pUserEmail = new String[1];
    private void getAllInformation(String orderProductKey) {
        databaseReference.child(orderProductKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("cartOrderCounter")){
                        cartOrderCounter = dataSnapshot.child("cartOrderCounter").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("userProfilePicture")){
                        pUserProfilePicture = dataSnapshot.child("userProfilePicture").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pUserProfilePicture).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image)
                                .into(userProfilePicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pUserProfilePicture).fit()
                                                .placeholder(R.drawable.profile_image)
                                                .into(userProfilePicture);
                                    }
                                });
                    }

                    if (dataSnapshot.hasChild("orderDate")){
                        String pOrderDate = dataSnapshot.child("orderDate").getValue().toString();
                        orderDate.setText(pOrderDate);
                    }

                    if (dataSnapshot.hasChild("orderTime")){
                        String pOrderTime = dataSnapshot.child("orderTime").getValue().toString();
                        orderTime.setText(pOrderTime);
                    }

                    if (dataSnapshot.hasChild("productId")){
                        String pProductId = dataSnapshot.child("productId").getValue().toString();
                        productOrderId.setText(pProductId);
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        String pOrderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                        orderConfirm.setText(pOrderConfirm);
                    }

                    if (dataSnapshot.hasChild("userFullName")){
                        pUserFullName = dataSnapshot.child("userFullName").getValue().toString();
                        userFullName.setText(pUserFullName);
                    }

                    if (dataSnapshot.hasChild("userTown")){
                        String pUserTown = dataSnapshot.child("userTown").getValue().toString();
                        userTown.setText(pUserTown);
                    }

                    if (dataSnapshot.hasChild("userLocation")){
                        String pUserLocation = dataSnapshot.child("userLocation").getValue().toString();
                        deliveryLocation.setText(pUserLocation);
                    }

                    if (dataSnapshot.hasChild("userPhoneNumber")){
                        pUserPhoneNumber = dataSnapshot.child("userPhoneNumber").getValue().toString();
                        userPhoneNumber.setText(pUserPhoneNumber);
                    }

                    if (dataSnapshot.hasChild("userEmail")){
                        pUserEmail[0] = dataSnapshot.child("userEmail").getValue().toString();
                        userEmailAddress.setText(pUserEmail[0]);
                    }

                    if (dataSnapshot.hasChild("paymentType")){
                        String pPaymentType = dataSnapshot.child("paymentType").getValue().toString();
                        paymentType.setText(pPaymentType);
                    }

                    if (dataSnapshot.hasChild("paymentSenderName")){
                        String pSenderFullName = dataSnapshot.child("paymentSenderName").getValue().toString();
                        senderFullName.setText(pSenderFullName);
                    }

                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                        String pAmountPaid = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                        amountPaid.setText(pAmountPaid);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionId")){
                        pTransactionId = dataSnapshot.child("paymentTransactionId").getValue().toString();
                        transactionId.setText(pTransactionId);
                    }

                    if (dataSnapshot.hasChild("paymentSenderNetwork")){
                        String pSenderNetwork = dataSnapshot.child("paymentSenderNetwork").getValue().toString();
                        senderNetwork.setText(pSenderNetwork);
                    }

                    if (dataSnapshot.hasChild("deliveryDate")){
                        pDeliveryDate = dataSnapshot.child("deliveryDate").getValue().toString();
                        deliveryDate.setText(pDeliveryDate);
                    }

                    if (dataSnapshot.hasChild("deliveryFee")){
                        String pDeliveryFee = dataSnapshot.child("deliveryFee").getValue().toString();
                        deliveryFee.setText(pDeliveryFee);
                    }

                    if (dataSnapshot.hasChild("deliveryType")){
                        String pDeliveryType = dataSnapshot.child("deliveryType").getValue().toString();
                        deliveryType.setText(pDeliveryType);

                        if (pDeliveryType.equals("Post Box Delivery"))
                            postDeliveryButton.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        String pDeliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                        deliveryConfirm.setText(pDeliverySuccess);
                    }

                    if (dataSnapshot.hasChild("totalAmount")){
                        String pTotalAmount = dataSnapshot.child("totalAmount").getValue().toString();
                        totalAmount.setText(pTotalAmount);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionPicture")){
                        pTransactionPicture = dataSnapshot.child("paymentTransactionPicture").getValue().toString();
                        if (!TextUtils.isEmpty(pTransactionPicture)){
                            transactionPicture.setVisibility(View.VISIBLE);
                            //loading picture offline
                            Picasso.get()
                                    .load(pTransactionPicture).fit()
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.no_image)
                                    .into(transactionPicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(pTransactionPicture).fit()
                                            .placeholder(R.drawable.no_image)
                                            .into(transactionPicture);
                                }
                            });
                        }
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            editDeliveryDate(dd);
        }
    };

    private void editDeliveryDate(final String date) {
        databaseReference.child(orderProductKey).child("deliveryDate").setValue(date)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        final String dd = "Your order delivery date is " + date;

                        //sending notification
                        final HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", getCurrentUserId);
                        notificationData.put("title", "Cart Order");
                        notificationData.put("message", dd);

                        notificationReference.child(getUserId).push().setValue(notificationData)
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
                                                    "ShowCartOrderedDetailsActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    dd,
                                                    "Cart Order",
                                                    getUserId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, dd, Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ShowCartViewOrderDetailsActivity.this, dd, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(ShowCartViewOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
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

    private void sendUserToViewPicActivity(String picName, String picUrl){
        Intent intent = new Intent(ShowCartViewOrderDetailsActivity.this, ViewPicture.class);
        intent.putExtra("imageText", picName);
        intent.putExtra("image", picUrl);
        startActivity(intent);
    }

    private void sendUserToViewCartProductActivity(){
        Intent intent = new Intent(ShowCartViewOrderDetailsActivity.this, ShowCartWhenClickActivity.class);
        intent.putExtra("cartOrderCounter", cartOrderCounter);
        intent.putExtra("userId", getUserId);
        startActivity(intent);
    }

    private void sendUserToChatActivity(String userId){
        Intent intent = new Intent(ShowCartViewOrderDetailsActivity.this, ChatActivity.class);
        intent.putExtra("userId",  userId);
        startActivity(intent);
    }
}
