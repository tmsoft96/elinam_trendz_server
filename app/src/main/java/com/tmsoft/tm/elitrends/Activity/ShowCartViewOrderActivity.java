package com.tmsoft.tm.elitrends.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Holders.cartViewOrder;
import com.tmsoft.tm.elitrends.R;

public class ShowCartViewOrderActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private RelativeLayout noOrder;

    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_show_cart_view_order);

        myToolBar = (Toolbar) findViewById(R.id.showCartViewOrder_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart Product Order");

        try{
            userId = getIntent().getExtras().get("userId").toString();
        } catch (Exception ex){

        }

        noOrder = (RelativeLayout) findViewById(R.id.showCartViewOrder_noOrder);
        noOrder.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
        databaseReference.keepSynced(true);
        recyclerView = (RecyclerView) findViewById(R.id.showCartViewOrder_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkOrder();
        showAllProductOrder();
    }

    private void checkOrder() {
        Query orderQuery;
        if (TextUtils.isEmpty(userId)){
            orderQuery = databaseReference.orderByChild("number");
            //Toast.makeText(this, "number", Toast.LENGTH_SHORT).show();
        } else {
            orderQuery = databaseReference.orderByChild("userId").startAt(userId).endAt(userId + "\uf8ff");
            //Toast.makeText(this, "userId", Toast.LENGTH_SHORT).show();
        }

        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num == 0)
                        noOrder.setVisibility(View.VISIBLE);
                    else
                        noOrder.setVisibility(View.GONE);
                } else
                    noOrder.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAllProductOrder() {
        Query orderQuery;
        if (TextUtils.isEmpty(userId)){
            orderQuery = databaseReference.orderByChild("number");
            //Toast.makeText(this, "number", Toast.LENGTH_SHORT).show();
        } else {
            orderQuery = databaseReference.orderByChild("userId").startAt(userId).endAt(userId + "\uf8ff");
            //Toast.makeText(this, "userId", Toast.LENGTH_SHORT).show();
        }

        FirebaseRecyclerAdapter<cartViewOrder, showCartViewOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<cartViewOrder, showCartViewOrderViewHolder>(

                        cartViewOrder.class,
                        R.layout.layout_show_cart_view_order,
                        showCartViewOrderViewHolder.class,
                        orderQuery

                ) {
                    @Override
                    protected void populateViewHolder(showCartViewOrderViewHolder viewHolder, cartViewOrder model, int position) {
                        final String cartPostKey = getRef(position).getKey();
                        final String getUserId = model.getUserId();

                        viewHolder.setOrderDate(model.getOrderDate());
                        viewHolder.setOrderTime(model.getOrderTime());
                        viewHolder.setOrderConfirm(model.getOrderConfirm());
                        viewHolder.setPaymentAmountPaid(model.getPaymentAmountPaid());
                        viewHolder.setPaymentConfirm(model.getPaymentConfirm());
                        viewHolder.setDeliverySuccess(model.getDeliverySuccess());
                        viewHolder.setUserFullName(model.getUserFullName());
                        viewHolder.setUserProfilePicture(model.getUserProfilePicture(), getApplicationContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ShowCartViewOrderActivity.this, ShowCartViewOrderDetailsActivity.class);
                                intent.putExtra("cartPostKey", cartPostKey);
                                intent.putExtra("userId", getUserId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class showCartViewOrderViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public showCartViewOrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setOrderDate(String orderDate) {
            TextView date = mView.findViewById(R.id.layoutShowCartViewOrder_date);
            date.setText(orderDate);
        }

        public void setOrderTime(String orderTime) {
            TextView time = mView.findViewById(R.id.layoutShowCartViewOrder_time);
            time.setText(orderTime);
        }

        public void setUserFullName(String userFullName) {
            TextView userName = mView.findViewById(R.id.layoutShowCartViewOrder_userName);
            userName.setText(userFullName);
        }

        public void setUserProfilePicture(final String userProfilePicture, final Context context) {
            final ImageView profilePicture = mView.findViewById(R.id.layoutShowCartViewOrder_profilePicture);
            //loading picture offline
            Picasso.get()
                    .load(userProfilePicture).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_image)
                    .into(profilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(userProfilePicture).fit()
                            .placeholder(R.drawable.profile_image)
                            .into(profilePicture);
                }
            });
        }

        public void setOrderConfirm(String orderConfirm) {
            TextView orderStatus = mView.findViewById(R.id.layoutShowCartViewOrder_orderStatus);
            orderStatus.setText(orderConfirm);
        }

        public void setPaymentAmountPaid(String paymentAmountPaid) {
            TextView amountPaid = mView.findViewById(R.id.layoutShowCartViewOrder_amountPaid);
            amountPaid.setText(paymentAmountPaid);
        }

        public void setPaymentConfirm(String paymentConfirm) {
            TextView paymentStatus = mView.findViewById(R.id.layoutShowCartViewOrder_paymentStatus);
            paymentStatus.setText(paymentConfirm);
        }

        public void setDeliverySuccess(String deliverySuccess) {
            TextView deliveryStatus = mView.findViewById(R.id.layoutShowCartViewOrder_deliveryStatus);
            deliveryStatus.setText(deliverySuccess);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
