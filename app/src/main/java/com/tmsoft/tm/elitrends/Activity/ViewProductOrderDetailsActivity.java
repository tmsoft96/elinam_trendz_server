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

public class ViewProductOrderDetailsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private TextView orderTime, orderDate, productOrderId, productName, productPrice, qtyOrder;
    private TextView orderConfirm, userFullName, userTown, deliveryLocation, userPhoneNumber, userEmailAddress;
    private TextView deliveryType, postShowDetails, totalAmount, paymentType, senderNetwork;
    private TextView senderFullName, amountPaid, transactionId, deliveryDate, deliveryFee, deliveryConfirm;
    private ImageView productImage, transactionPicture;
    private Dialog dialog;
    private CircleImageView userProfilePicture;
    private Button chatUser, callUser, editOrder, editDelivery, editPayment, deliveryDateEdit;
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private AlertDialog orderConfirmAlertDialog, paymentConfirmAlertDialog, deliveryConfirmAlertDialog;

    private int year, month, day;
    private InnerNotification innerNotification;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference, notificationReference, statusReference;

    private String orderProductKey, pProductName, pProductPicture, pUserProfilePicture, pTransactionPicture, pUserFullName;
    private String pOrderConfirm, pDeliveryDate, pDeliveryFee, pDeliverySuccess, pUserPhoneNumber;
    private String userId;
    private String zPostRegion, zPostTown, zPostDistrict, zPostNumber;
    private String notifySender1, notifySender2, notifySender3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_order_details);

        myToolBar = (Toolbar) findViewById(R.id.viewProductOrderDetails_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Ordered");

        orderProductKey = getIntent().getExtras().get("productOrderKey").toString();

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        databaseReference.keepSynced(true);

        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        orderDate = (TextView) findViewById(R.id.viewProductOrderDetails_orderDate);
        orderTime = (TextView) findViewById(R.id.viewProductOrderDetails_orderTime);
        productOrderId = (TextView) findViewById(R.id.viewProductOrderDetails_productId);
        productName = (TextView) findViewById(R.id.viewProductOrderDetails_productName);
        productPrice = (TextView) findViewById(R.id.viewProductOrderDetails_productPrice);
        qtyOrder = (TextView) findViewById(R.id.viewProductOrderDetails_quantity);
        orderConfirm = (TextView) findViewById(R.id.viewProductOrderDetails_orderConfirm);
        userFullName = (TextView) findViewById(R.id.viewProductOrderDetails_userFullName);
        deliveryLocation = (TextView) findViewById(R.id.viewProductOrderDetails_userLocation);
        userPhoneNumber = (TextView) findViewById(R.id.viewProductOrderDetails_userPhoneNumber);
        userEmailAddress = (TextView) findViewById(R.id.viewProductOrderDetails_userEmail);
        senderFullName = (TextView) findViewById(R.id.viewProductOrderDetails_senderFullName);
        amountPaid = (TextView) findViewById(R.id.viewProductOrderDetails_amountPaid);
        transactionId = (TextView) findViewById(R.id.viewProductOrderDetails_transactionId);
        senderNetwork = (TextView) findViewById(R.id.viewProductOrderDetails_senderNetwork);
        deliveryDate = (TextView) findViewById(R.id.viewProductOrderDetails_deliveryDate);
        deliveryFee = (TextView) findViewById(R.id.viewProductOrderDetails_deliveryFee);
        deliveryConfirm = (TextView) findViewById(R.id.viewProductOrderDetails_deliveryConfirm);
        productImage = (ImageView) findViewById(R.id.viewProductOrderDetails_productPicture);
        transactionPicture = (ImageView) findViewById(R.id.viewProductOrderDetails_transactionPicture);
        userProfilePicture = (CircleImageView) findViewById(R.id.viewProductOrderDetails_userProfilePicture);
        deliveryDateEdit = (Button) findViewById(R.id.viewProductOrderDetails_deliveryDateEdit);
        callUser = (Button) findViewById(R.id.viewProductOrderDetails_callUser);
        chatUser = (Button) findViewById(R.id.viewProductOrderDetails_chatUser);
        editOrder = (Button) findViewById(R.id.viewProductOrderDetails_editOrder);
        editDelivery = (Button) findViewById(R.id.viewProductOrderDetails_editDelivery);
        progressDialog = new ProgressDialog(this);
        userTown = (TextView) findViewById(R.id.viewProductOrderDetails_userTown);
        deliveryType = (TextView) findViewById(R.id.viewProductOrderDetails_deliveryType);
        postShowDetails = (TextView) findViewById(R.id.viewProductOrderDetails_postDelivery);
        totalAmount = (TextView) findViewById(R.id.viewProductOrderDetails_totalAmount);
        paymentType = (TextView) findViewById(R.id.viewProductOrderDetails_paymentType);
        editPayment = (Button) findViewById(R.id.viewProductOrderDetails_editPayment);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        getAllInformation(orderProductKey);
        postShowDetails.setVisibility(View.GONE);
        transactionPicture.setVisibility(View.GONE);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductName, pProductPicture, "", "");
            }
        });

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPic(pProductName, pTransactionPicture);
            }
        });

        userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pUserFullName, pUserProfilePicture, pUserFullName, pUserProfilePicture);
            }
        });

        deliveryDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOrderUser(pUserPhoneNumber);
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

        chatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToChatActivity(userId);
            }
        });

        collectAllServerId();

        postShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPostDetails();
            }
        });
    }

    private void showConfirmOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductOrderDetailsActivity.this);
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
                                Toast.makeText(ViewProductOrderDetailsActivity.this, "Select order confirmation detail", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductOrderDetailsActivity.this);
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
                                Toast.makeText(ViewProductOrderDetailsActivity.this, "Select delivery detail", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        deliveryConfirmAlertDialog = builder.create();
        deliveryConfirmAlertDialog.show();
    }

    private void showPaymentConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductOrderDetailsActivity.this);
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
                                Toast.makeText(ViewProductOrderDetailsActivity.this, "Confirm Payment", Toast.LENGTH_SHORT).show();
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
                                                statusReference.child("singleOrder").child("pendingOrder").child(orderProductKey).removeValue();
                                                statusReference.child("singleOrder").child("confirmOrder").child(orderProductKey).removeValue();
                                                statusReference.child("singleOrder").child("canceledOrder").child(orderProductKey).removeValue();
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
                                                    "ViewProductOrderActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                orderConfirmAlertDialog.dismiss();
                                                //Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                Snackbar snackbar = Snackbar.make(myToolBar, msg, Snackbar.LENGTH_LONG)
                                                        .setAction("Send Email", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                try{
                                                                    SendEmail email = new SendEmail(pUserEmail,
                                                                            "Elinam Trendz",
                                                                            "You order " + pProductName + " have been confirmed",
                                                                            ViewProductOrderDetailsActivity.this, totalAmount);
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
                                                orderConfirmAlertDialog.dismiss();
                                                //Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                Snackbar snackbar = Snackbar.make(myToolBar, msg, Snackbar.LENGTH_LONG)
                                                        .setAction("Send Email", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                try{
                                                                    SendEmail email = new SendEmail(pUserEmail,
                                                                            "Elinam Trendz",
                                                                            "You order " + pProductName + " have been confirmed",
                                                                            ViewProductOrderDetailsActivity.this, totalAmount);
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
                                            Toast.makeText(ViewProductOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
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
                                                    "ViewProductOrderActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                deliveryConfirmAlertDialog.dismiss();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                deliveryConfirmAlertDialog.dismiss();
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(ViewProductOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                                            deliveryConfirmAlertDialog.dismiss();
                                        }
                                    }
                                });
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
                                                    "ViewProductOrderActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    msg,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                paymentConfirmAlertDialog.dismiss();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ViewProductOrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                paymentConfirmAlertDialog.dismiss();
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(ViewProductOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void viewPic(String pProductName, String pTransactionPicture) {
        Intent intent = new Intent(ViewProductOrderDetailsActivity.this, ViewPicture.class);
        intent.putExtra("imageText", pProductName);
        intent.putExtra("image", pTransactionPicture);
        startActivity(intent);
    }

    private String[] pUserEmail = new String[1];
    private void getAllInformation(String orderProductKey) {
        databaseReference.child(orderProductKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("userId")){
                        userId = dataSnapshot.child("userId").getValue().toString();
                        statusReference = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(userId);
                        statusReference.keepSynced(true);
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

                    if (dataSnapshot.hasChild("productName")){
                        pProductName = dataSnapshot.child("productName").getValue().toString();
                        productName.setText(pProductName);
                    }

                    if (dataSnapshot.hasChild("productPrice")){
                        String pProductPrice = dataSnapshot.child("productPrice").getValue().toString();
                        productPrice.setText(pProductPrice);
                    }

                    if (dataSnapshot.hasChild("productQuantity")){
                        String pProductQty = dataSnapshot.child("productQuantity").getValue().toString();
                        qtyOrder.setText(pProductQty);
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        pOrderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                        orderConfirm.setText(pOrderConfirm);
                    }

                    if (dataSnapshot.hasChild("userFullName")){
                        pUserFullName = dataSnapshot.child("userFullName").getValue().toString();
                        userFullName.setText(pUserFullName);
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

                    if (dataSnapshot.hasChild("paymentSenderName")){
                        String pSenderFullName = dataSnapshot.child("paymentSenderName").getValue().toString();
                        senderFullName.setText(pSenderFullName);
                    }

                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                        String pAmountPaid = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                        amountPaid.setText(pAmountPaid);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionId")){
                        String pTransactionId = dataSnapshot.child("paymentTransactionId").getValue().toString();
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
                        pDeliveryFee = dataSnapshot.child("deliveryFee").getValue().toString();
                        deliveryFee.setText(pDeliveryFee);
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        pDeliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                        deliveryConfirm.setText(pDeliverySuccess);
                    }

                    if (dataSnapshot.hasChild("productPicture1")){
                        pProductPicture = dataSnapshot.child("productPicture1").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture).fit()
                                        .placeholder(R.drawable.warning).into(productImage);
                            }
                        });
                    }

                    if (dataSnapshot.hasChild("userProfilePicture")){
                        pUserProfilePicture = dataSnapshot.child("userProfilePicture").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pUserProfilePicture).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image)
                                .into(userProfilePicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pUserProfilePicture).fit()
                                                .placeholder(R.drawable.profile_image).into(userProfilePicture);
                                    }
                                });
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

                    if (dataSnapshot.hasChild("userTown")){
                        String pUserTown = dataSnapshot.child("userTown").getValue().toString();
                        userTown.setText(pUserTown);
                    }

                    if (dataSnapshot.hasChild("deliveryType")){
                        String pDeliveryType = dataSnapshot.child("deliveryType").getValue().toString();
                        deliveryType.setText(pDeliveryType);

                        if (pDeliveryType.equals("Post Box Delivery"))
                            postShowDetails.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("totalAmount")){
                        String pTotalAmount = dataSnapshot.child("totalAmount").getValue().toString();
                        totalAmount.setText(pTotalAmount);
                    }

                    if (dataSnapshot.hasChild("paymentType")){
                        String pPaymentType = dataSnapshot.child("paymentType").getValue().toString();
                        paymentType.setText(pPaymentType);
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

    private void viewPictureDialog(String pProductName, final String pProductPicture, final String pUserFullName, final String pUserProfilePicture){
        TextView close, title;
        final ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pProductName);

        Picasso.get().load(pProductPicture).into(picture);
        //loading picture offline
        Picasso.get()
                .load(pProductPicture).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image).into(picture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture).fit()
                        .placeholder(R.drawable.no_image)
                        .into(picture);
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pUserFullName.equals("") && !pUserProfilePicture.equals(""))
                    viewPic(pUserFullName, pUserProfilePicture);
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



    private void editDeliveryDate(final String date) {
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
                                                    "ViewProductOrderActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    dd,
                                                    pProductName,
                                                    userId,
                                                    orderProductKey
                                            );
                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                Toast.makeText(ViewProductOrderDetailsActivity.this, dd, Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ViewProductOrderDetailsActivity.this, dd, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(ViewProductOrderDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
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

    private void sendUserToChatActivity(String userId){
        Intent intent = new Intent(ViewProductOrderDetailsActivity.this, ChatActivity.class);
        intent.putExtra("userId",  userId);
        startActivity(intent);
    }
}
