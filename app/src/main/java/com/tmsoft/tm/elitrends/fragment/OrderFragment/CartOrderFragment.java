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
import com.tmsoft.tm.elitrends.Activity.ShowCartViewOrderActivity;
import com.tmsoft.tm.elitrends.Activity.ShowCartViewOrderDetailsActivity;
import com.tmsoft.tm.elitrends.Holders.cartViewOrder;
import com.tmsoft.tm.elitrends.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartOrderFragment extends Fragment {

    private View view;
    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private RelativeLayout noOrder;
    private SwipeRefreshLayout refresh;
    private ImageView summary;

    private DatabaseReference databaseReference;

    public CartOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart_order, container, false);

        myToolBar = view.findViewById(R.id.fragmentCartOrder_toolbar);
        myToolBar.setTitle("Cart Product Order");

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.fragmentCartOrder_refresh);
        summary = (ImageView) view.findViewById(R.id.fragmentCartOrder_summary);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
        databaseReference.keepSynced(true);

        noOrder = view.findViewById(R.id.fragmentCartOrder_noOrder);
        noOrder.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.fragmentCartOrder_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkOrder();
                showAllProductOrder();
                refresh.setRefreshing(false);
            }
        });

        checkOrder();
        showAllProductOrder();

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
                                showAllProductOrder();
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

    private void checkOrder() {
        //Query orderQuery = databaseReference.orderByChild("number");
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

    private void showAllProductOrderSummary(String value) {
        Query query = databaseReference.orderByChild("orderConfirm").startAt(value).endAt(value + "\uf8ff");
        FirebaseRecyclerAdapter<cartViewOrder, showCartViewOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<cartViewOrder, showCartViewOrderViewHolder>(

                        cartViewOrder.class,
                        R.layout.layout_show_cart_view_order,
                        showCartViewOrderViewHolder.class,
                        query

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
                        viewHolder.setUserProfilePicture(model.getUserProfilePicture(), view.getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), ShowCartViewOrderDetailsActivity.class);
                                intent.putExtra("cartPostKey", cartPostKey);
                                intent.putExtra("userId", getUserId);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showAllProductOrder() {
        FirebaseRecyclerAdapter<cartViewOrder, showCartViewOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<cartViewOrder, showCartViewOrderViewHolder>(

                        cartViewOrder.class,
                        R.layout.layout_show_cart_view_order,
                        showCartViewOrderViewHolder.class,
                        databaseReference

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
                        viewHolder.setUserProfilePicture(model.getUserProfilePicture(), view.getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), ShowCartViewOrderDetailsActivity.class);
                                intent.putExtra("cartPostKey", cartPostKey);
                                intent.putExtra("userId", getUserId);
                                startActivity(intent);
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
                    .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(userProfilePicture).fit()
                            .placeholder(R.drawable.profile_image).into(profilePicture);
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
}
