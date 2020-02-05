package com.tmsoft.tm.elitrends.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportIssuesActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private EditText issue, suggestion;
    private ImageView issuesImage, upload, delete;
    private Button save;
    private ProgressDialog progressDialog;

    private int gallaryPick;
    private String getPictureUri, saveCurrentDate, saveCurrentTime;
    private Uri imageUri;

    private String getCurrentUserId;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issues);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = findViewById(R.id.reportIssues_toolbar);
        issue = findViewById(R.id.reportIssues_issues);
        suggestion = findViewById(R.id.reportIssues_suggestion);
        issuesImage = findViewById(R.id.reportIssues_image);
        upload = findViewById(R.id.reportIssues_upload);
        delete = findViewById(R.id.reportIssues_delete);
        save = findViewById(R.id.reportIssues_button);
        progressDialog = new ProgressDialog(this);

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Report Issues");

        delete.setVisibility(View.GONE);

        getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Report Issues");
        databaseReference.keepSynced(true);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallaryPick = 1;
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            issuesImage.setImageResource(R.drawable.report_it);
                            delete.setVisibility(View.INVISIBLE);
                            Toast.makeText(view.getContext(), "Picture one deleted successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Error occurred... please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        issuesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(getPictureUri)){
                    Intent intent = new Intent(ReportIssuesActivity.this, ViewPicture.class);
                    intent.putExtra("imageText", "Issue Picture");
                    intent.putExtra("image", getPictureUri);
                    startActivity(intent);
                } else
                    Toast.makeText(ReportIssuesActivity.this, "Upload Issue Picture", Toast.LENGTH_SHORT).show();
            }
        });
        
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getIssue = issue.getText().toString();
                String getSuggestion = suggestion.getText().toString();
                
                if (TextUtils.isEmpty((getIssue)))
                    Toast.makeText(ReportIssuesActivity.this, "Please describe issue/error", Toast.LENGTH_SHORT).show();
                else
                    saveIssue(getIssue, getSuggestion);
            }
        });
    }

    private void saveIssue(String getIssue, String getSuggestion) {
        progressDialog.setMessage("Reporting Issue...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        getTimeAndDate();
        Map reportMap = new HashMap();
        reportMap.put("Issue", getIssue);
        reportMap.put("Suggestion", getSuggestion);
        reportMap.put("image", getPictureUri);
        reportMap.put("time", saveCurrentTime);
        reportMap.put("date", saveCurrentDate);
        databaseReference.push().setValue(reportMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ReportIssuesActivity.this, "Issue submitted successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(ReportIssuesActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (gallaryPick == 1){
            if(requestCode == gallaryPick && resultCode == RESULT_OK && data != null){
                imageUri = data.getData();
                saveProductPictureOneToFirebaseStorage();
            }
        }
    }

    private void saveProductPictureOneToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic();
                    }
                });
            } catch (Exception ex){
                savePic();
            }
        } else
            savePic();
    }

    private void savePic() {
        getTimeAndDate();
        String saveRandomName = saveCurrentDate + getCurrentUserId + saveCurrentTime;

        StorageReference filePath = storageReference.child("Issues Report Pictures").child(getCurrentUserId)
                .child(imageUri.getLastPathSegment() + saveRandomName + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.report_it).into(issuesImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri).fit()
                                        .placeholder(R.drawable.warning).into(issuesImage);
                            }
                        });
                        Toast.makeText(ReportIssuesActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick = 0;
                        delete.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ReportIssuesActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getTimeAndDate() {
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTimeFormat.format(postTime.getTime());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}