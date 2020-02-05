package com.tmsoft.tm.elitrends.fragment.OrderFragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tmsoft.tm.elitrends.Activity.BuyForMeActivity;
import com.tmsoft.tm.elitrends.Holders.buyForMeClass;
import com.tmsoft.tm.elitrends.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyForMeFragment extends Fragment {


    public BuyForMeFragment() {
        // Required empty public constructor
    }

    private View view;
    private Toolbar myToolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout relativeLayout;
    private ImageView summary;

    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_buy_for_me, container, false);

        myToolbar = (Toolbar) view.findViewById(R.id.fragmentBuyForMe_toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragmentBuyForMe_recycler);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragmentBuyForMe_refresh);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.fragmentBuyForMe_noOrder);
        summary = (ImageView) view.findViewById(R.id.fragmentBuyForMe_summary);

        myToolbar.setTitle("Buy for me");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Buy for me");
        databaseReference.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        showOrders();
        checkOrders();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showOrders();
                checkOrders();
                refreshLayout.setRefreshing(false);
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
        FirebaseRecyclerAdapter<buyForMeClass, buyForMeViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<buyForMeClass, buyForMeViewHolder>(
                        buyForMeClass.class,
                        R.layout.layout_buy_for_me,
                        buyForMeViewHolder.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(final buyForMeViewHolder viewHolder, final buyForMeClass model, int position) {
                        String userId = model.getUserId();
                        final String postKey = getRef(position).getKey();

                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("profilePicture")) {
                                        String profilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                                        viewHolder.setUserProfilePicture(profilePicture);
                                    }
                                    if (dataSnapshot.hasChild("fullName")){
                                        String userName = dataSnapshot.child("fullName").getValue().toString();
                                        viewHolder.setUserName(userName);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.setProductName(model.getProductName());
                        viewHolder.setProductQuantity(model.getProductQuantity());
                        viewHolder.setProductUrgent(model.getProductUrgent());
                        viewHolder.setOrderDate(model.getOrderDate());
                        viewHolder.setOrderTime(model.getOrderTime());
                        viewHolder.setMessage("msg : " + model.getMessage());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(view.getContext(), BuyForMeActivity.class);
                                intent.putExtra("postKey", postKey);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkOrders() {
        Query query = databaseReference.orderByChild("number");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showOrders() {
        Query query = databaseReference.orderByChild("number");
        FirebaseRecyclerAdapter<buyForMeClass, buyForMeViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<buyForMeClass, buyForMeViewHolder>(
                        buyForMeClass.class,
                        R.layout.layout_buy_for_me,
                        buyForMeViewHolder.class,
                        databaseReference
                ) {
            @Override
            protected void populateViewHolder(final buyForMeViewHolder viewHolder, final buyForMeClass model, int position) {
                String userId = model.getUserId();
                final String postKey = getRef(position).getKey();

                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.hasChild("profilePicture")) {
                                String profilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                                viewHolder.setUserProfilePicture(profilePicture);
                            }
                            if (dataSnapshot.hasChild("fullName")){
                                String userName = dataSnapshot.child("fullName").getValue().toString();
                                viewHolder.setUserName(userName);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.setProductName(model.getProductName());
                viewHolder.setProductQuantity(model.getProductQuantity());
                viewHolder.setProductUrgent(model.getProductUrgent());
                viewHolder.setOrderDate(model.getOrderDate());
                viewHolder.setOrderTime(model.getOrderTime());
                viewHolder.setMessage("msg : " + model.getMessage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(view.getContext(), BuyForMeActivity.class);
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class buyForMeViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public buyForMeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserName(String userName) {
            TextView pName = (TextView) mView.findViewById(R.id.layoutBuyForMe_userName);
            pName.setText(userName);
        }

        public void setUserProfilePicture(final String userProfilePicture) {
            final CircleImageView pProfilePicture = (CircleImageView) mView.findViewById(R.id.layoutBuyForMe_profilePicture);
            //loading picture offline
            Picasso
                    .get()
                    .load(userProfilePicture).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning).into(pProfilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso
                            .get()
                            .load(userProfilePicture).fit()
                            .placeholder(R.drawable.warning)
                            .into(pProfilePicture);
                }
            });
        }

        public void setProductName(String productName) {
            TextView pProductName = (TextView) mView.findViewById(R.id.layoutBuyForMe_productName);
            pProductName.setText(productName);
        }

        public void setProductUrgent(String productUrgent) {
            TextView pProductUrgent = (TextView) mView.findViewById(R.id.layoutBuyForMe_productUrgent);
            pProductUrgent.setText(productUrgent);
        }

        public void setProductQuantity(String productQuantity) {
            TextView qty = (TextView) mView.findViewById(R.id.layoutBuyForMe_productQuantity);
            qty.setText(productQuantity);
        }

        public void setOrderDate(String orderDate) {
            TextView date = (TextView) mView.findViewById(R.id.layoutBuyForMe_date);
            date.setText(orderDate);
        }

        public void setOrderTime(String orderTime) {
            TextView time = (TextView) mView.findViewById(R.id.layoutBuyForMe_time);
            time.setText(orderTime);
        }

        public void setMessage(String message) {
            TextView msg = (TextView) mView.findViewById(R.id.layoutBuyForMe_message);
            msg.setText(message);
        }
    }
}
