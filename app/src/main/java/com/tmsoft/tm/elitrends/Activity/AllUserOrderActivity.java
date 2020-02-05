package com.tmsoft.tm.elitrends.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.tmsoft.tm.elitrends.Holders.productOrderList;
import com.tmsoft.tm.elitrends.R;

public class AllUserOrderActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private RelativeLayout noOrder;

    private DatabaseReference databaseReference;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_order);

        myToolBar = (Toolbar) findViewById(R.id.allOrder_toolbarView);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolBar.setTitle("User Product Orders");

        userId = getIntent().getExtras().get("userId").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        databaseReference.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.allOrder_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        Query orderQuery = databaseReference.orderByChild("userId")
                .startAt(userId).endAt(userId + "\uf8ff");

        noOrder = (RelativeLayout) findViewById(R.id.allOrder_noOrder);
        noOrder.setVisibility(View.GONE);

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

        FirebaseRecyclerAdapter<productOrderList, productOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<productOrderList, productOrderViewHolder>(
                        productOrderList.class,
                        R.layout.layout_product_order,
                        productOrderViewHolder.class,
                        orderQuery
                ){
                    @Override
                    protected void populateViewHolder(productOrderViewHolder viewHolder, productOrderList model, int position) {
                        final String productOrderKey = getRef(position).getKey();
                        final String userId = model.getUserId();

                        viewHolder.setOrderConfirm(model.getOrderConfirm());
                        viewHolder.setProductName(model.getProductName());
                        viewHolder.setProductPicture1(model.getProductPicture1(), getApplicationContext());
                        viewHolder.setProductPrice(model.getProductPrice());
                        viewHolder.setUserFullName(model.getUserFullName());
                        viewHolder.setDeliverySuccess(model.getDeliverySuccess());
                        viewHolder.setOrderDate(model.getOrderDate());
                        viewHolder.setOrderTime(model.getOrderTime());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AllUserOrderActivity.this, ViewProductOrderDetailsActivity.class);
                                intent.putExtra("productOrderKey", productOrderKey);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class productOrderViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public productOrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProductPicture1(final String productPicture1, final Context context) {
            final ImageView pProductImage = mView.findViewById(R.id.viewProductOrder_productPicture);
            //loading picture offline
            Picasso
                    .get()
                    .load(productPicture1)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(pProductImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(productPicture1).fit()
                            .placeholder(R.drawable.warning)
                            .into(pProductImage);
                }
            });
        }

        public void setProductName(String productName) {
            TextView pProductName = mView.findViewById(R.id.viewProductOrder_productName);
            pProductName.setText(productName);
        }

        public void setProductPrice(String productPrice) {
            TextView pProductPrize = mView.findViewById(R.id.viewProductOrder_productPrice);
            pProductPrize.setText(productPrice);
        }

        public void setUserFullName(String userFullName) {
            TextView pUserFullName = mView.findViewById(R.id.viewProductOrder_fullName);
            pUserFullName.setText(userFullName);
        }

        public void setOrderConfirm(String orderConfirm) {
            TextView pOrderConfirm = mView.findViewById(R.id.viewProductOrder_confirmMessage);
            pOrderConfirm.setText(orderConfirm);
        }

        public void setDeliverySuccess(String deliverySuccess) {
            TextView pDeliverySuccess = mView.findViewById(R.id.viewProductOrder_deliveryMessage);
            pDeliverySuccess.setText(deliverySuccess);
        }

        public void setOrderDate(String orderDate) {
            TextView pOrderDate = mView.findViewById(R.id.viewProductOrder_date);
            pOrderDate.setText(orderDate);
        }

        public void setOrderTime(String orderTime) {
            TextView pOrderTime = mView.findViewById(R.id.viewProductOrder_time);
            pOrderTime.setText(orderTime);
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
}