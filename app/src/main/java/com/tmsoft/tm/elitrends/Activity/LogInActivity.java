package com.tmsoft.tm.elitrends.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tmsoft.tm.elitrends.R;

public class LogInActivity extends AppCompatActivity {

    private EditText email, password;
    private Button logIn;
    private TextView forgetPassword, signUp;
    private ImageView gmail;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    private String DeviceToken, online_user_id, close;
    private Boolean isValidate;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private GoogleApiClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        email = (EditText) findViewById(R.id.txt_emailLogIn);
        password = (EditText) findViewById(R.id.txt_passwordLogIn);
        logIn = (Button) findViewById(R.id.btn_logIn);
        forgetPassword = (TextView) findViewById(R.id.loginForgetPassword);
        signUp = (TextView) findViewById(R.id.logInRegister);
        gmail = (ImageView) findViewById(R.id.logInGmail);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);

        signUp.setPaintFlags(signUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken");

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInUser();
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
                        Toast.makeText(LogInActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
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

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToSignUpActivity();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToForgetPasswordActivity();
            }
        });

        try{
            close = getIntent().getExtras().get("close").toString();

            //showClose(close);
        } catch (Exception ex){

        }
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
                                            Log.d(TAG, "signInWithCredential:success");
                                            sendUserToMainActivity();
                                            progressDialog.dismiss();
                                        }
                                    });
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            sendUserToLogInActivity();
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(LogInActivity.this, "Failed to sign in... try again \n" + errorMessage, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void LogInUser() {
        String getEmail = email.getText().toString();
        String getPassword = password.getText().toString();

        if (TextUtils.isEmpty(getEmail))
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(getPassword))
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        else{
            progressDialog.setTitle("Account Log In");
            progressDialog.setMessage("Please wait patiently while we are setting things up");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(getEmail,getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        online_user_id = mAuth.getCurrentUser().getUid();
                        DeviceToken = FirebaseInstanceId.getInstance().getToken();
                        databaseReference.child(online_user_id).child("device_token").setValue(DeviceToken)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        validateUserEmailAddress();
                                        progressDialog.dismiss();
                                        Toast.makeText(LogInActivity.this, "Log In successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(LogInActivity.this, errorMessage + " Try Again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
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

    private void sendUserToLogInActivity() {
        Intent intent = new Intent (LogInActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }


    private void sendUserToSignUpActivity() {
        Intent intent = new Intent (LogInActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            sendUserToMainActivity();
        }
    }

    private void validateUserEmailAddress(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        isValidate = currentUser.isEmailVerified();

        if (isValidate){
            sendUserToMainActivity();
        } else {
            showDialogOk();
            Toast.makeText(this, "Please verify your account first...", Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }


    private void sendUserToMainActivity() {
        Intent intent = new Intent (LogInActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void sendUserToForgetPasswordActivity(){
        Intent intent = new Intent(LogInActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
