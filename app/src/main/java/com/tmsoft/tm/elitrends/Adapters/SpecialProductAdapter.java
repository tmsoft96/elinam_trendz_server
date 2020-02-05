package com.tmsoft.tm.elitrends.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Activity.ViewProductDetailsActivity;
import com.tmsoft.tm.elitrends.Holders.SpecialProductClass;
import com.tmsoft.tm.elitrends.R;

import java.util.List;

public class SpecialProductAdapter extends RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder> {
    private List<SpecialProductClass> specialProductClassList;
    private Context context;


    public SpecialProductAdapter(List<SpecialProductClass> specialProductClassList, Context context){
        this.specialProductClassList = specialProductClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public SpecialProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_all_post_display, parent, false);

        return new SpecialProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SpecialProductViewHolder holder, int position) {
        SpecialProductClass specialProductClass = specialProductClassList.get(position);

        String pProductName = specialProductClass.getProductName();
        String pProductPrice = specialProductClass.getProductPrice();
        String pProductActualPrice = specialProductClass.getProductActualPrice();
        final String pProductPicture = specialProductClass.getProductPicture1();
        final String pProductKey = specialProductClass.getProductKey();
        final String pCategory = specialProductClass.getProductCategory();

        holder.productName.setText(pProductName);
        holder.productPrice.setText(pProductPrice);
        holder.productActualPrice.setText(pProductActualPrice);

        //loading picture offline
        Picasso.get()
                .load(pProductPicture).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(holder.productPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture).fit()
                        .placeholder(R.drawable.warning)
                        .into(holder.productPicture);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clickPostIntent = new Intent(view.getContext(), ViewProductDetailsActivity.class);
                clickPostIntent.putExtra("category", pCategory);
                clickPostIntent.putExtra("postKey", pProductKey);
                context.startActivity(clickPostIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return specialProductClassList.size();
    }

    public class SpecialProductViewHolder extends RecyclerView.ViewHolder{
        public TextView productName, productPrice, productActualPrice;
        public ImageView productPicture;
        public View view;


        public SpecialProductViewHolder(View mView){
            super(mView);
            view = mView;

            productName = mView.findViewById(R.id.specialEvent_productName);
            productPrice = mView.findViewById(R.id.specialEvent_productPrice);
            productActualPrice = mView.findViewById(R.id.specialEvent_productPriceActual);
            productPicture = mView.findViewById(R.id.specialEvent_productImage);

        }
    }
}
