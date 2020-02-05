package com.tmsoft.tm.elitrends.fragment.SettingFragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
public class PolicyFragment extends Fragment {

    private View view;
    private EditText delivery, returns;
    private Button save;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    public PolicyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_policy, container, false);

        delivery = view.findViewById(R.id.policy_deliveryNote);
        returns = view.findViewById(R.id.policy_returnNote);
        save = view.findViewById(R.id.policy_saveButton);
        progressDialog = new ProgressDialog(view.getContext());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Policy Details");
        databaseReference.keepSynced(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String del = delivery.getText().toString();
                String ret = returns.getText().toString();

                saveAll(del, ret);
            }
        });

        displayAllInfo();

        return view;
    }

    private void displayAllInfo() {
        databaseReference.child("policy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("deliveryPolicy")){
                        String del = dataSnapshot.child("deliveryPolicy").getValue().toString();
                        delivery.setText(del);
                    }

                    if (dataSnapshot.hasChild("returnPolicy")){
                        String ret = dataSnapshot.child("returnPolicy").getValue().toString();
                        returns.setText(ret);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveAll(String del, String ret) {
        progressDialog.setMessage("saving ...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Map saveMap = new HashMap();
        saveMap.put("deliveryPolicy", del);
        saveMap.put("returnPolicy", ret);

        databaseReference.child("policy").updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(view.getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}
