package com.tmsoft.tm.elitrends.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Holders.autofit;
import com.tmsoft.tm.elitrends.Holders.cartProducts;
import com.tmsoft.tm.elitrends.R;

public class ShowCartWhenClickActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private TextView counter;

    private String cartOrderCounter, getUserId;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart_when_click);

        myToolBar = (Toolbar) findViewById(R.id.showCartWhenClick_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Cart");

        cartOrderCounter = getIntent().getExtras().get("cartOrderCounter").toString();
        getUserId = getIntent().getExtras().get("userId").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Order Confirm")
                .child(getUserId).child(cartOrderCounter);

        autofit noColums = new autofit();
        noColums.autofit(getApplicationContext());

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        recyclerView = (RecyclerView) findViewById(R.id.showCartWhenClick_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoofColums));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));

        counter = (TextView) findViewById(R.id.showCartWhenClick_counter);

        displayAllProducts();
        showCounter();
    }

    private void showCounter() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    long cc = dataSnapshot.getChildrenCount();
                    counter.setText(cc + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayAllProducts() {
        FirebaseRecyclerAdapter<cartProducts,productViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<cartProducts, productViewHolder>(

                        cartProducts.class,
                        R.layout.layout_all_post_display,
                        productViewHolder.class,
                        databaseReference

                ) {
                    @Override
                    protected void populateViewHolder(final productViewHolder viewHolder, cartProducts model, int position) {
                        final String cartPostKey = getRef(position).getKey();

                        viewHolder.setQuantity(model.getQuantity() + "unit(s)");

                        String postKey = model.getPostKey();
                        String category = model.getCategory();
                        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
                        productReference.keepSynced(true);

                        productReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("ProductName")){
                                        String pName = dataSnapshot.child("ProductName").getValue().toString();
                                        viewHolder.setProductName(pName);
                                    }

                                    if (dataSnapshot.hasChild("ProductPicture1")){
                                        String pPicture = dataSnapshot.child("ProductPicture1").getValue().toString();
                                        viewHolder.setProductPicture(pPicture, getApplicationContext());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ShowCartWhenClickActivity.this, ShowCartDetailsActivity.class);
                                intent.putExtra("cartPostKey", cartPostKey);
                                intent.putExtra("cartOrderCounter", cartOrderCounter);
                                intent.putExtra("userId", getUserId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class productViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView discountPercent, discountPrice, pName;
        ImageView priceConceal;
        RelativeLayout relativeLayout;

        public productViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            discountPercent = mView.findViewById(R.id.allPost_productDiscountPercent);
            discountPrice = mView.findViewById(R.id.allPost_productDiscountPrice);
            priceConceal = mView.findViewById(R.id.allPost_priceConceal);
            relativeLayout = mView.findViewById(R.id.allPost_relative);
            pName = mView.findViewById(R.id.allPost_productName);

            int width = relativeLayout.getLayoutParams().width;
            int height = relativeLayout.getLayoutParams().height;
            Log.i("width", width+ "");

            autofit noColums = new autofit();
            noColums.autofit(mView.getContext());

            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(noColums.getLayoutWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT));
            pName.setHeight(noColums.getTextHeight());

            discountPercent.setVisibility(View.GONE);
            discountPrice.setVisibility(View.GONE);
            priceConceal.setVisibility(View.GONE);
        }

        public void setQuantity(String quantity) {
            TextView qty = mView.findViewById(R.id.allPost_productPrice);
            qty.setText(quantity);
        }

        public void setProductPicture(final String pic, final Context context){
            final ImageView pImage = mView.findViewById(R.id.allPost_productImage);
            //loading picture offline
            Picasso.get()
                    .load(pic).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(pImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(pic).fit()
                            .placeholder(R.drawable.warning)
                            .into(pImage);
                }
            });
        }

        public void setProductName(String name){
            pName.setText(name);
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
