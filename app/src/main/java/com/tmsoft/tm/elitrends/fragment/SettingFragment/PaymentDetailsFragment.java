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
import com.tmsoft.tm.elitrends.Activity.SettingsActivity;
import com.tmsoft.tm.elitrends.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailsFragment extends Fragment {

    private View view;
    private EditText warningNote, mobileMoneyDetails;
    private Button paymentSaveButton;
    private ProgressDialog progressDialog;

    private DatabaseReference paymentReference;

    public PaymentDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_a_payment_details, container, false);

        warningNote = view.findViewById(R.id.settings_paymentWarningNote);
        mobileMoneyDetails = view.findViewById(R.id.settings_paymentMobileMoneyDetails);
        paymentSaveButton = view.findViewById(R.id.setting_paymentButton);
        progressDialog = new ProgressDialog(view.getContext());

        paymentReference = FirebaseDatabase.getInstance().getReference().child("Payment Details");
        paymentReference.keepSynced(true);

        paymentSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String warning = warningNote.getText().toString();
                String money = mobileMoneyDetails.getText().toString();

                if (TextUtils.isEmpty(warning))
                    Toast.makeText(view.getContext(), "Please enter warning note", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(money))
                    Toast.makeText(view.getContext(), "Please enter Mobile Money Details", Toast.LENGTH_SHORT).show();
                else
                    getPaymentInfo(warning, money);
            }
        });

        showPaymentDetails();

        return view;
    }

    private void showPaymentDetails() {
        paymentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("WarningNote")){
                        String nt = dataSnapshot.child("WarningNote").getValue().toString();
                        warningNote.setText(nt);
                    }
                    if (dataSnapshot.hasChild("MobileMoneyDetails")){
                        String mm = dataSnapshot.child("MobileMoneyDetails").getValue().toString();
                        mobileMoneyDetails.setText(mm);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPaymentInfo(String warning, String money) {
        progressDialog.setMessage("Saving");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        HashMap paymentMap = new HashMap();
        paymentMap.put("WarningNote", warning);
        paymentMap.put("MobileMoneyDetails", money);

        paymentReference.updateChildren(paymentMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(view.getContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(view.getContext(), "Error Occurred...\n" +
                            err + "\nTry Again....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}
