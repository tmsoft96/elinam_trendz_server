package com.tmsoft.tm.elitrends.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Holders.favoriteProducts;
import com.tmsoft.tm.elitrends.R;

public class FavoriteActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String getCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorites");
        databaseReference.keepSynced(true);

        int mNoofColums = autofit(getApplicationContext());

        myToolBar = (Toolbar) findViewById(R.id.favorite_toolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Cart");

        recyclerView = (RecyclerView) findViewById(R.id.favorite_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(mNoofColums, StaggeredGridLayoutManager.VERTICAL));

        displayAllFavoriteProduct();
    }

    private int autofit(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 140);
        return noOfColumns;
    }

    private void displayAllFavoriteProduct() {
        Query favQuery = databaseReference.orderByChild("UserId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");

        FirebaseRecyclerAdapter<favoriteProducts, favoriteProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<favoriteProducts, favoriteProductViewHolder>(

                        favoriteProducts.class,
                        R.layout.layout_all_post_display,
                        favoriteProductViewHolder.class,
                        favQuery

                ) {
                    @Override
                    protected void populateViewHolder(favoriteProductViewHolder viewHolder, favoriteProducts model, int position) {
                        final String postKey = getRef(position).getKey();

                        viewHolder.setpProductName(model.getpProductName());
                        viewHolder.setpProductPicture1(model.getpProductPicture1(), getApplicationContext());
                        viewHolder.setpProductPrice(model.getpProductPrice());

                        //setting onClick Listener to the recycle view
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(FavoriteActivity.this, ViewFavoriteDetailActivity.class);
                                clickPostIntent.putExtra("postKey", postKey);
                                startActivity(clickPostIntent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class favoriteProductViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public favoriteProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setpProductName(String pProductName) {
            TextView productName = mView.findViewById(R.id.allPost_productName);
            String postss = pProductName;

            try{
                if (postss.length() >= 25){
                    String ePost = postss.substring(0,25);
                    String ps = ePost + "***";

                    productName.setText(ps);
                } else{
                    productName.setText(postss);
                }
            } catch (Exception ex){

            }
        }

        public void setpProductPrice(String pProductPrice) {
            TextView productPrice = mView.findViewById(R.id.allPost_productPrice);
            productPrice.setText(pProductPrice);
        }

        public void setpProductPicture1(final String pProductPicture1, final Context context) {
            final ImageView pProductPicture = mView.findViewById(R.id.allPost_productImage);
            //loading picture offline
            Picasso.get()
                    .load(pProductPicture1).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(pProductPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(pProductPicture1).fit()
                            .placeholder(R.drawable.warning)
                            .into(pProductPicture);
                }
            });
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
