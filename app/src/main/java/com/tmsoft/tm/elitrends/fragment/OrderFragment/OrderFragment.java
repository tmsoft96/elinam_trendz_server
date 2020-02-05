package com.tmsoft.tm.elitrends.fragment.OrderFragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tmsoft.tm.elitrends.Activity.ViewProductOrderDetailsActivity;
import com.tmsoft.tm.elitrends.Holders.productOrderList;
import com.tmsoft.tm.elitrends.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private View view;
    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private RelativeLayout noOrder;
    private SwipeRefreshLayout refresh;
    private ImageView summary;

    private DatabaseReference databaseReference;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order, container, false);

        myToolBar = view.findViewById(R.id.viewProductOrder_toolbar);
        myToolBar.setTitle("Product Order");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        databaseReference.keepSynced(true);

        noOrder = (RelativeLayout) view.findViewById(R.id.fragmentViewProductOrder_noOrder);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.fragmentViewProductOrder_refresh);
        summary = (ImageView) view.findViewById(R.id.fragmentViewProductOrder_summary);

        noOrder.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.viewProductOrder_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        checkProduct();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkProduct();
                refresh.setRefreshing(false);
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Sorting...");
                builder.setItems(R.array.orderConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                showAllProductOrderSummary("Waiting for confirmation");
                                break;
                            case 1:
                                showAllProductOrderSummary("Order Confirmed");
                                break;
                            case 2:
                                showAllProductOrderSummary("Order Canceled");
                                break;
                            default:
                                showOrders();
                        }
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void showAllProductOrderSummary(String value) {
        Query query = databaseReference.orderByChild("orderConfirm").startAt(value).endAt(value + "\uf8ff");
        FirebaseRecyclerAdapter<productOrderList, productOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<productOrderList, productOrderViewHolder>(
                        productOrderList.class,
                        R.layout.layout_product_order,
                        productOrderViewHolder.class,
                        query
                ){
                    @Override
                    protected void populateViewHolder(productOrderViewHolder viewHolder, productOrderList model, int position) {
                        final String productOrderKey = getRef(position).getKey();
                        final String userId = model.getUserId();

                        viewHolder.setOrderConfirm(model.getOrderConfirm());
                        viewHolder.setProductName(model.getProductName());
                        viewHolder.setProductPicture1(model.getProductPicture1(), getContext());
                        viewHolder.setProductPrice(model.getProductPrice());
                        viewHolder.setUserFullName(model.getUserFullName());
                        viewHolder.setDeliverySuccess(model.getDeliverySuccess());
                        viewHolder.setOrderDate(model.getOrderDate());
                        viewHolder.setOrderTime(model.getOrderTime());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ViewProductOrderDetailsActivity.class);
                                intent.putExtra("productOrderKey", productOrderKey);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkProduct() {
        databaseReference.addValueEventListener(new ValueEventListener() {
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

    @Override
    public void onStart() {
        super.onStart();
        showOrders();
    }

    private void showOrders() {
        //Query query = databaseReference.orderByChild("number");
        FirebaseRecyclerAdapter<productOrderList, productOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<productOrderList, productOrderViewHolder>(
                        productOrderList.class,
                        R.layout.layout_product_order,
                        productOrderViewHolder.class,
                        databaseReference
                ){
                    @Override
                    protected void populateViewHolder(productOrderViewHolder viewHolder, productOrderList model, int position) {
                        final String productOrderKey = getRef(position).getKey();
                        final String userId = model.getUserId();

                        viewHolder.setOrderConfirm(model.getOrderConfirm());
                        viewHolder.setProductName(model.getProductName());
                        viewHolder.setProductPicture1(model.getProductPicture1(), getContext());
                        viewHolder.setProductPrice(model.getProductPrice());
                        viewHolder.setUserFullName(model.getUserFullName());
                        viewHolder.setDeliverySuccess(model.getDeliverySuccess());
                        viewHolder.setOrderDate(model.getOrderDate());
                        viewHolder.setOrderTime(model.getOrderTime());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ViewProductOrderDetailsActivity.class);
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
            Picasso.get()
                    .load(productPicture1).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning).into(pProductImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(productPicture1).fit()
                            .placeholder(R.drawable.warning).into(pProductImage);
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

}
