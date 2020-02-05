package com.tmsoft.tm.elitrends.fragment.SettingFragment.DeliveryDetails;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDeliveryFragment extends Fragment {

    private View view;
    private EditText postAmount, postNote;
    private Button save;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    public PostDeliveryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_d_post_delivery, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Delivery Details");

        postAmount = view.findViewById(R.id.postBoxDelivery_amount);
        postNote = view.findViewById(R.id.postBoxDelivery_note);
        save = view.findViewById(R.id.postBoxDelivery_saveButton);
        progressDialog = new ProgressDialog(view.getContext());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Saving");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                String amount = postAmount.getText().toString();
                String note = postNote.getText().toString();

                if (TextUtils.isEmpty(amount))
                    amount = null;
                else if (TextUtils.isEmpty(note))
                    note = null;

                Map saveMap = new HashMap();
                saveMap.put("amount", amount);
                saveMap.put("message", note);

                databaseReference.child("postBoxDelivery").updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(view.getContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        showAllInfo();

        return view;
    }

    private void showAllInfo() {
        databaseReference.child("postBoxDelivery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("amount")){
                        String aa = dataSnapshot.child("amount").getValue().toString();
                        postAmount.setText(aa);
                    }

                    if (dataSnapshot.hasChild("message")){
                        String aa = dataSnapshot.child("message").getValue().toString();
                        postNote.setText(aa);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
