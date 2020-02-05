package com.tmsoft.tm.elitrends.fragment;


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
import com.tmsoft.tm.elitrends.Activity.ViewUserProfileActivity;
import com.tmsoft.tm.elitrends.Holders.allUserClass;
import com.tmsoft.tm.elitrends.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersDetailsFragment extends Fragment {


    public UsersDetailsFragment() {
        // Required empty public constructor
    }


    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private TextView counter;
    private RelativeLayout noUser;
    private SwipeRefreshLayout refresh;
    private View view;

    private String getCurrentId;
    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users_details, container, false);

        myToolBar = (Toolbar) view.findViewById(R.id.allUser_toolbar);
        myToolBar.setTitle("Users Details");

        getCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        noUser = (RelativeLayout) view.findViewById(R.id.allUser_noUser);
        noUser.setVisibility(View.GONE);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.allUser_refresh);

        checkUser();

        counter = (TextView) view.findViewById(R.id.allUser_counter);

        recyclerView = (RecyclerView) view.findViewById(R.id.allUser_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showAllUserCounter();
                refresh.setRefreshing(false);
            }
        });

        showAllUserCounter();

        return view;
    }

    private void checkUser() {
        noUser = (RelativeLayout) view.findViewById(R.id.allUser_noUser);
        noUser.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num == 0)
                        noUser.setVisibility(View.VISIBLE);
                    else
                        noUser.setVisibility(View.GONE);
                } else
                    noUser.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAllUserCounter() {
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<allUserClass, usersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<allUserClass, usersViewHolder>(

                        allUserClass.class,
                        R.layout.layout_all_registered_users,
                        usersViewHolder.class,
                        databaseReference

                ) {
                    @Override
                    protected void populateViewHolder(usersViewHolder viewHolder, allUserClass model, int position) {
                        final String userId = model.getUserId();

                        viewHolder.setFullName(model.getFullName());
                        viewHolder.setPhoneNumber(model.getPhoneNumber());
                        viewHolder.setProfilePicture(model.getProfilePicture());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), ViewUserProfileActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView profileImage;
        TextView name, phone;

        public usersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            profileImage = mView.findViewById(R.id.chatMember_profilePicture);
            name = mView.findViewById(R.id.chatMember_fullName);
            phone = mView.findViewById(R.id.chatMember_phoneNumber);
        }

        public void setProfilePicture(final String profilePicture) {
            //loading picture offline
            Picasso.get()
                    .load(profilePicture).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_image)
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(profilePicture).fit()
                                    .placeholder(R.drawable.profile_image)
                                    .into(profileImage);
                        }
                    });
        }

        public void setFullName(String fullName) {
            name.setText(fullName);
        }

        public void setPhoneNumber(String phoneNumber) {
            phone.setText(phoneNumber);
        }
    }

}
