package com.tmsoft.tm.elitrends.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tmsoft.tm.elitrends.R;

import java.io.IOException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button btn_signUp;
    private ImageView gmail;
    private TextView logIn;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    private String DeviceToken, online_user_id;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, agreementReference;
    private GoogleApiClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        email = (EditText) findViewById(R.id.register_Email);
        password = (EditText) findViewById(R.id.register_Password);
        confirmPassword = (EditText) findViewById(R.id.register_ConfirmPassword);
        btn_signUp = (Button) findViewById(R.id.register_SignUpButton);
        gmail = (ImageView) findViewById(R.id.register_Gmail);
        logIn = (TextView) findViewById(R.id.register_logIn);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken");
        agreementReference = FirebaseDatabase.getInstance().getReference().child("AgreementTerms");

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUserToFirebase();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(RegisterActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLogInActivity();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {

            progressDialog.setTitle("Log In with Google Account");
            progressDialog.setMessage("Please wait patiently while we connect to your account");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait while we are getting your account set up", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Occurred. Can't connect to your Gmail right now \n Try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            online_user_id = mAuth.getCurrentUser().getUid();
                            DeviceToken = FirebaseInstanceId.getInstance().getToken();
                            databaseReference.child(online_user_id).child("device_token").setValue(DeviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showTermsAndAgreementDialog2(online_user_id);
                                        }
                                    });
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            sendUserToRegisterActivity();
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Failed to sign in... try again \n" + errorMessage, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }



    private void signUpUserToFirebase() {
        String getEmail = email.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(getEmail))
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(getPassword))
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(getConfirmPassword))
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        else if (!getPassword.equals(getConfirmPassword))
            Toast.makeText(this, "Please password do not match", Toast.LENGTH_SHORT).show();
        else{
            progressDialog.setTitle("Account Sign Up");
            progressDialog.setMessage("Please wait patiently while we are setting things up");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        online_user_id = mAuth.getCurrentUser().getUid();
                        DeviceToken = FirebaseInstanceId.getInstance().getToken();
                        databaseReference.child(online_user_id).child("device_token").setValue(DeviceToken)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        checkTermsAndAgreement(online_user_id);
                                    }
                                });

                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, errorMessage + "\n Please try again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void sendVerificationEmail(){
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        showDialogOk();
                        Toast.makeText(RegisterActivity.this, "Registration Successfully, Please check and verify your account", Toast.LENGTH_LONG).show();
                        sendUserToLogInActivity();
                        mAuth.signOut();
                    } else {
                        String err = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    private void checkTermsAndAgreement(final String userId) {
        agreementReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("terms")){
                        String terms = dataSnapshot.child("terms").getValue().toString();
                        if (terms.equalsIgnoreCase("yes")){
                            sendVerificationEmail();
                            Log.i("termsAgreement", "yes");
                            progressDialog.dismiss();
                        }
                        else{
                            Log.i("termsAgreement", "no");
                            showTermsAndAgreementDialog(userId);
                        }
                    }
                } else {
                    showTermsAndAgreementDialog(userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showTermsAndAgreementDialog(final String userId) {
        Button accept;
        TextView text;

        dialog.setContentView(R.layout.dialog_term_and_agreement);
        accept = dialog.findViewById(R.id.dialogTerms_accept);
        text = dialog.findViewById(R.id.dialogTerms_text);

        try {
            InputStream stream = getAssets().open("terms.txt");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String tContents = new String(buffer);
            text.setText(tContents);
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agreementReference.child(userId).child("terms").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            sendVerificationEmail();
                            progressDialog.dismiss();
                            dialog.dismiss();
                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void showTermsAndAgreementDialog2(final String userId) {
        Button accept;
        TextView text;

        dialog.setContentView(R.layout.dialog_term_and_agreement);
        accept = dialog.findViewById(R.id.dialogTerms_accept);
        text = dialog.findViewById(R.id.dialogTerms_text);

        try {
            InputStream stream = getAssets().open("terms.txt");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String tContents = new String(buffer);
            text.setText(tContents);
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agreementReference.child(userId).child("terms").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            progressDialog.dismiss();
                            sendUserToMainActivity();
                            Toast.makeText(RegisterActivity.this, "Sign In successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void showDialogOk() {
        final Button ok;

        dialog.setContentView(R.layout.dialog_validate);
        ok = dialog.findViewById(R.id.validate_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLogInActivity() {
        Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent intent = new Intent (RegisterActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
