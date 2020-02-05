package com.tmsoft.tm.elitrends.fragment.SettingFragment;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailsFragment extends Fragment {

    private View view;
    private EditText phoneNumber, emailAddress, location, appLink;
    private Button contactButton;
    private ProgressDialog progressDialog;

    private DatabaseReference contactReference;

    public ContactDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_contact_details, container, false);

        progressDialog = new ProgressDialog(view.getContext());
        phoneNumber = view.findViewById(R.id.settings_phoneNumber);
        emailAddress = view.findViewById(R.id.settings_emailAddress);
        location = view.findViewById(R.id.settings_location);
        appLink = view.findViewById(R.id.settings_appLink);
        contactButton = view.findViewById(R.id.setting_ContactButton);

        contactReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        contactReference.keepSynced(true);

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = phoneNumber.getText().toString();
                String email = emailAddress.getText().toString();
                String loc = location.getText().toString();
                String link = appLink.getText().toString();

                if (TextUtils.isEmpty(num))
                    Toast.makeText(view.getContext(), "Please enter phone number", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(email))
                    Toast.makeText(view.getContext(), "Please enter email address", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(loc))
                    Toast.makeText(view.getContext(), "Please enter location", Toast.LENGTH_SHORT).show();
                else
                    saveContactDetails(num, email, loc, link);
            }
        });

        showContactDetails();

        return view;
    }

    private void showContactDetails() {
        contactReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("PhoneNumber")){
                        String num = dataSnapshot.child("PhoneNumber").getValue().toString();
                        phoneNumber.setText(num);
                    }
                    if (dataSnapshot.hasChild("EmailAddress")){
                        String email = dataSnapshot.child("EmailAddress").getValue().toString();
                        emailAddress.setText(email);
                    }
                    if (dataSnapshot.hasChild("Location")){
                        String loc = dataSnapshot.child("Location").getValue().toString();
                        location.setText(loc);
                    }

                    if (dataSnapshot.hasChild("AppLink")){
                        String link = dataSnapshot.child("AppLink").getValue().toString();
                        appLink.setText(link);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveContactDetails(String num, String email, String loc, String link) {
        progressDialog.setMessage("Saving");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        HashMap contactMap = new HashMap();
        contactMap.put("PhoneNumber", num);
        contactMap.put("EmailAddress", email);
        contactMap.put("Location", loc);
        contactMap.put("AppLink", link);
        contactReference.updateChildren(contactMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(view.getContext(), "Contact save successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else{
                    String err = task.getException().getMessage();
                    Toast.makeText(view.getContext(), "Error Occurred...\n" +
                            err + "\nTry Again....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}
