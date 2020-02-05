package com.tmsoft.tm.elitrends.fragment.Add;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.tmsoft.tm.elitrends.Activity.MainActivity;
import com.tmsoft.tm.elitrends.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleProductFragment extends Fragment {

    private Toolbar myToolBar;
    private EditText productName, productPrice, productDescription, productQuantityAvailable, productVideo;
    private Spinner productCategory;
    private ImageView productImage1, productImage2, productImage3;
    private ImageView productUpload1, productUpload2, productUpload3;
    private ImageView productDelete1, productDelete2, productDelete3;
    private Button button;
    private ProgressDialog progressDialog;
    private View view;
    private TextView addPriceDiscount;
    private Dialog dialog;
    private LinearLayout limitedQty;
    private ImageView qtyAdd, qtySub;
    private EditText productLimitedQty;

    private TextToSpeech phoneSpeak;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private int gallaryPick1, gallaryPick2, gallaryPick3;
    private Uri imageUri1, imageUri2, imageUri3;
    private String saveCurrentDate1, saveCurrentTime1, saveRandomName1;
    private String saveCurrentDate2, saveCurrentTime2, saveRandomName2;
    private String saveCurrentDate3, saveCurrentTime3, saveRandomName3;
    private String getPictureUri1, getPictureUri2, getPictureUri3;

    private int numQty = 0;

    String getDiscount = "no", getDiscountPrice, getDiscountPercent,
            tempDiscountPercent = null, tempDiscountPrice = null;

    Bitmap thumb_bitmap = null;


    public SingleProductFragment() {
        // Required empty public constructor
    }


    @Override
    public void onPause() {
        if(phoneSpeak != null){
            phoneSpeak.stop();
            phoneSpeak.shutdown();
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_q_single_product, container, false);

        productName = view.findViewById(R.id.add_productName);
        productPrice = view.findViewById(R.id.add_productPrice);
        productDescription = view.findViewById(R.id.add_productDescription);
        productCategory = view.findViewById(R.id.add_productCategory);
        productVideo = view.findViewById(R.id.add_productVideo);
        productImage1 = view.findViewById(R.id.add_productPicture1);
        productImage2 = view.findViewById(R.id.add_productPicture2);
        productImage3 = view.findViewById(R.id.add_productPicture3);
        productUpload1 = view.findViewById(R.id.add_productUpload1);
        productUpload2 = view.findViewById(R.id.add_productUpload2);
        productUpload3 = view.findViewById(R.id.add_productUpload3);
        productDelete1 = view.findViewById(R.id.add_productDelete1);
        productDelete2 = view.findViewById(R.id.add_productDelete2);
        productDelete3 = view.findViewById(R.id.add_productDelete3);
        addPriceDiscount = view.findViewById(R.id.add_priceDiscount);
        button = view.findViewById(R.id.add_buttonProduct);
        progressDialog = new ProgressDialog(view.getContext());
        productQuantityAvailable = view.findViewById(R.id.add_productQuantityAvailable);
        dialog = new Dialog(view.getContext(), R.style.Theme_CustomDialog);
        limitedQty = view.findViewById(R.id.add_limitedQty);
        qtyAdd = view.findViewById(R.id.add_qtyAdd);
        qtySub = view.findViewById(R.id.add_qtySub);
        productLimitedQty = view.findViewById(R.id.add_productLimitedQty);

        //Phone speak out
        phoneSpeak = new TextToSpeech(view.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    phoneSpeak.setLanguage(Locale.UK);
                }
            }
        });



        myToolBar = view.findViewById(R.id.addSingle_navToolbar);
        myToolBar.setTitle("Add your products");

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.keepSynced(true);

        productDelete1.setVisibility(View.INVISIBLE);
        productDelete2.setVisibility(View.INVISIBLE);
        productDelete3.setVisibility(View.INVISIBLE);

        addPriceDiscount.setPaintFlags(addPriceDiscount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        limitedQty.setVisibility(View.GONE);

        limitedQtyWorks();

        productUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallaryPick1 = 1;
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick1);
            }
        });

        productUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPictureUri1 == null)
                    Toast.makeText(view.getContext(), "Please upload picture to first box", Toast.LENGTH_SHORT).show();
                else {
                    gallaryPick2 = 1;
                    Intent gallaryIntent = new Intent();
                    gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    gallaryIntent.setType("image/*");
                    startActivityForResult(gallaryIntent, gallaryPick2);
                }
            }
        });

        productUpload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPictureUri1 == null)
                    Toast.makeText(view.getContext(), "Please upload picture to first box", Toast.LENGTH_SHORT).show();
                else if (getPictureUri2 == null)
                    Toast.makeText(view.getContext(), "Please upload picture to second box", Toast.LENGTH_SHORT).show();
                else{
                    gallaryPick3 = 1;
                    Intent gallaryIntent = new Intent();
                    gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    gallaryIntent.setType("image/*");
                    startActivityForResult(gallaryIntent, gallaryPick3);
                }
            }
        });

        productDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri1)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri1);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            productImage1.setImageResource(R.drawable.product);
                            productDelete1.setVisibility(View.INVISIBLE);
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

        productDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri2)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri2);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            productImage2.setImageResource(R.drawable.product);
                            productDelete2.setVisibility(View.INVISIBLE);
                            Toast.makeText(view.getContext(), "Picture two deleted successfully", Toast.LENGTH_SHORT).show();
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

        productDelete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog.setMessage("Deleting....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri3)){
                    StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri3);
                    delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            productImage3.setImageResource(R.drawable.product);
                            productDelete3.setVisibility(View.INVISIBLE);
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

        addPriceDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = productPrice.getText().toString();
                if (TextUtils.isEmpty(price))
                    Toast.makeText(view.getContext(), "Enter original price first", Toast.LENGTH_SHORT).show();
                else
                    showPriceDiscountDialog();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pName = productName.getText().toString();
                String pPrice = productPrice.getText().toString();
                String pDescription = productDescription.getText().toString();
                String pCategory = productCategory.getSelectedItem().toString();
                String pQuantityAvailable = productQuantityAvailable.getText().toString();
                String pVideo = productVideo.getText().toString();
                String pLimited = productLimitedQty.getText().toString();

                int oo = Integer.parseInt(pLimited);


                if(TextUtils.isEmpty(pName))
                    Toast.makeText(view.getContext(), "Enter your product name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pPrice))
                    Toast.makeText(view.getContext(), "Enter your product price", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pDescription))
                    Toast.makeText(view.getContext(), "Enter your product description", Toast.LENGTH_SHORT).show();
                else if (pCategory.equalsIgnoreCase("Select Category"))
                    Toast.makeText(view.getContext(), "Select product category", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pLimited) && pCategory.equalsIgnoreCase("Bulk Purchase"))
                    Toast.makeText(view.getContext(), "Enter minimum quantity", Toast.LENGTH_SHORT).show();
                else if (oo < 1)
                    Toast.makeText(view.getContext(), "Check your minimum quantity", Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(getPictureUri1))
                    Toast.makeText(view.getContext(), "Upload a minimum of one product picture", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pQuantityAvailable))
                    Toast.makeText(view.getContext(), "Enter the product quantity", Toast.LENGTH_SHORT).show();
                else{
                    if (pCategory.equalsIgnoreCase("Bulk Purchase")){
                        if (getDiscount.equals("no"))
                            addProduct(pName, pPrice, pDescription, pCategory, pVideo, getPictureUri1, getPictureUri2, getPictureUri3, pQuantityAvailable,
                                    "no", "", "", pLimited);
                        else if (getDiscount.equals("yes"))
                            addProduct(pName, pPrice, pDescription, pCategory, pVideo, getPictureUri1, getPictureUri2, getPictureUri3, pQuantityAvailable,
                                    "yes", getDiscountPercent, getDiscountPrice, pLimited);
                    } else {
                        if (getDiscount.equals("no"))
                            addProduct(pName, pPrice, pDescription, pCategory, pVideo, getPictureUri1, getPictureUri2, getPictureUri3, pQuantityAvailable,
                                    "no", "", "", null);
                        else if (getDiscount.equals("yes"))
                            addProduct(pName, pPrice, pDescription, pCategory, pVideo, getPictureUri1, getPictureUri2, getPictureUri3, pQuantityAvailable,
                                    "yes", getDiscountPercent, getDiscountPrice, null);
                    }
                }
            }
        });

        return view;
    }

    private void limitedQtyWorks() {
        productCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String zz = productCategory.getSelectedItem().toString();
                if ("Bulk Purchase".equals(zz)) {
                    limitedQty.setVisibility(View.VISIBLE);
                } else {
                    limitedQty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        qtySub.setEnabled(false);

        productLimitedQty.setText("1");

        if(numQty >= 0){
            qtyAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    numQty = Integer.parseInt(productLimitedQty.getText().toString());
                    numQty = numQty + 1;
                    productLimitedQty.setText(numQty + "");
                    qtySub.setEnabled(true);
                }
            });

            qtySub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    numQty = Integer.parseInt(productLimitedQty.getText().toString());
                    if (numQty == 0){
                        qtySub.setEnabled(false);
                    } else {
                        numQty = Integer.parseInt(productLimitedQty.getText().toString());
                        numQty = numQty - 1;
                        productLimitedQty.setText(numQty + "");
                    }
                }
            });
        } else
            qtySub.setEnabled(false);

    }

    private void showPriceDiscountDialog() {
        TextView close, initialPrice;
        final EditText discountPrice, discountPercentage;
        ImageView done, calculate;

        dialog.setContentView(R.layout.dialog_discount);
        close = dialog.findViewById(R.id.dialogDiscount_close);
        initialPrice = dialog.findViewById(R.id.dialogDiscount_initialPrice);
        discountPercentage = dialog.findViewById(R.id.dialogDiscount_discountPricePercent);
        discountPrice = dialog.findViewById(R.id.dialogDiscount_discountPrice);
        calculate = dialog.findViewById(R.id.dialogDiscount_calculator);
        done = dialog.findViewById(R.id.dialogDiscount_done);

        final String iniPx = productPrice.getText().toString();

        initialPrice.setText(iniPx);

        if (!TextUtils.isEmpty(tempDiscountPercent))
            discountPercentage.setText(tempDiscountPercent);

        if (!TextUtils.isEmpty(tempDiscountPrice))
            discountPrice.setText(tempDiscountPrice);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disPx = discountPrice.getText().toString();
                if (!TextUtils.isEmpty(disPx)){
                    try{
                        Double oldPx = Double.parseDouble(iniPx);
                        Double newPx = Double.parseDouble(disPx);
                        Double increase = oldPx - newPx;
                        Double increaseDivide = increase / oldPx;
                        Double increasePercent = increaseDivide * 100;
                        discountPercentage.setText(Math.round(increasePercent) + "% off");
                    } catch (Exception ex){
                        Toast.makeText(view.getContext(), "Check price entry", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(view.getContext(), "Enter new price", Toast.LENGTH_SHORT).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getNewPx = discountPrice.getText().toString();
                String getPerce = discountPercentage.getText().toString();

                if (TextUtils.isEmpty(getNewPx) && TextUtils.isEmpty(getPerce))
                    Toast.makeText(view.getContext(), "Check your entries", Toast.LENGTH_SHORT).show();
                else{
                    getDiscount = "yes";
                    getDiscountPercent = getPerce;
                    getDiscountPrice = getNewPx;
                    dialog.dismiss();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addProduct(String pName, String pPrice, String pDescription, final String pCategory,
                            final String pVideo, String getPictureUri1, String getPictureUri2,
                            String getPictureUri3, String pProductQuantity, String pDiscount,
                            String pDiscountPercentage, String pDiscountPrice, String pLimitedQty) {
        progressDialog.setTitle("Adding");
        progressDialog.setMessage("Please wait patiently while your product is added");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        String searchName = pName.toLowerCase();

        String pp = "GHC" + pPrice;
        String pdp = "GHC" + pDiscountPrice;
        String pRaw = null;

        if (pDiscount.equals("no")){
            pRaw = pPrice;
        } else if (pDiscount.equals("yes")){
            pRaw = pDiscountPrice;
        }

        DatabaseReference dRef = databaseReference.push();

        String productKey = dRef.getKey();

        final HashMap<String, String> productMap = new HashMap<>();
        productMap.put("UserID", getCurrentUserId);
        productMap.put("ProductKey", productKey);
        productMap.put("ProductName", pName);
        productMap.put("ProductSearchName", searchName);
        productMap.put("ProductPrice", pp);
        productMap.put("ProductPriceRaw", pRaw);
        productMap.put("ProductDescription", pDescription);
        productMap.put("ProductCategory", pCategory);
        productMap.put("ProductLimitedQty", pLimitedQty);
        productMap.put("ProductVideo", pVideo);
        productMap.put("ProductDiscount", pDiscount);
        productMap.put("ProductDiscountPercentage", pDiscountPercentage);
        productMap.put("ProductDiscountPrice", pdp);
        productMap.put("ProductPicture1", getPictureUri1);
        productMap.put("ProductPicture2", getPictureUri2);
        productMap.put("ProductPicture3", getPictureUri3);
        productMap.put("ProductDate", saveCurrentDate1);
        productMap.put("ProductTime", saveCurrentTime1);
        productMap.put("ProductQuantity", pProductQuantity);

        dRef.setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendUserToMainActivity();
                    Toast.makeText(view.getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                    String speak = "Product added successfully";
                    phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                    progressDialog.dismiss();
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(view.getContext(), "Failed to add product \n" + errorMessage + "\n Please try again .....", Toast.LENGTH_SHORT).show();
                    String speak = "Failed to add product, please try again";
                    phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                    progressDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (gallaryPick1 == 1){
            if(requestCode == gallaryPick1 && resultCode == RESULT_OK && data != null){
                imageUri1 = data.getData();
                saveProductPictureOneToFirebaseStorage();
            }
        } else if (gallaryPick2 == 1){
            if (requestCode == gallaryPick2 && resultCode == RESULT_OK && data != null){
                imageUri2 = data.getData();
                saveProductPictureTwoToFirebaseStorage();
            }
        } else if (gallaryPick3 == 1){
            if (requestCode == gallaryPick3 && resultCode == RESULT_OK && data != null){
                imageUri3 = data.getData();
                saveProductPictureThreeToFirebaseStorage();
            }
        }
    }

    private void saveProductPictureOneToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri1)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri1);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic1();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic1();
                    }
                });
            } catch (Exception ex){
                savePic1();
            }
        } else
            savePic1();
    }

    private void savePic1() {
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate1 = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime1 = currentTimeFormat.format(postTime.getTime());

        saveRandomName1 = saveCurrentDate1 + getCurrentUserId + saveCurrentTime1;

        StorageReference filePath = storageReference.child("Product Pictures").child(getCurrentUserId)
                .child(imageUri1.getLastPathSegment() + saveRandomName1 + ".jpg");
        filePath.putFile(imageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri1 = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri1).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage1, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri1).fit()
                                        .placeholder(R.drawable.warning).into(productImage1);
                            }
                        });
                        Toast.makeText(view.getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick1 = 0;
                        productDelete1.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(view.getContext(), "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveProductPictureTwoToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri2)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri2);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic2();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic2();
                    }
                });
            } catch (Exception ex){
                savePic2();
            }
        } else
            savePic2();
    }

    private void savePic2(){
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate2 = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime2 = currentTimeFormat.format(postTime.getTime());

        saveRandomName2 = saveCurrentDate2 + getCurrentUserId + saveCurrentTime2;

        StorageReference filePath = storageReference.child("Product Pictures").child(getCurrentUserId)
                .child(imageUri2.getLastPathSegment() + saveRandomName2 + ".jpg");
        filePath.putFile(imageUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri2 = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri2).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage2, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri2).fit()
                                        .placeholder(R.drawable.warning).into(productImage2);
                            }
                        });
                        Toast.makeText(view.getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick2 = 0;
                        productDelete2.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(view.getContext(), "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveProductPictureThreeToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri3)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri3);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic3();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic3();
                    }
                });
            } catch (Exception ex){
                savePic3();
            }
        } else
            savePic3();
    }

    private void savePic3(){
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate3 = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime3 = currentTimeFormat.format(postTime.getTime());

        saveRandomName3 = saveCurrentDate3 + getCurrentUserId + saveCurrentTime3;

        StorageReference filePath = storageReference.child("Product Pictures").child(getCurrentUserId)
                .child(imageUri3.getLastPathSegment() + saveRandomName3 + ".jpg");
        filePath.putFile(imageUri3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri3 = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri3).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productImage3, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri3).fit()
                                        .placeholder(R.drawable.warning).into(productImage3);
                            }
                        });
                        Toast.makeText(view.getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick3 = 0;
                        productDelete3.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(view.getContext(), "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    private void sendUserToMainActivity(){
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
