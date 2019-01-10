/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ProfileManager;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AccountDetailsActivity";
    private CircleImageView displayImageHolder, editDisplayImage;
    private EditText editTextUserName;
    private static final int CHOOSE_IMAGE = 1;
    private EditText editTextName, editTextSurname, editTextMobileNumber;
    private Toolbar toolbar;
    private Button submitButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String name = "";
    private String surname = "";
    private String username = "";
    private String mobNumber = "";
    private Uri uriProfileImage;
    private String profileImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        toolbar = findViewById(R.id.accountToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Profile");
        toolbar.setTextAlignment(Toolbar.TEXT_ALIGNMENT_CENTER);

        displayImageHolder = findViewById(R.id.accountDisplayPicture);
        editDisplayImage = findViewById(R.id.accountUploadImage);
        editTextName = findViewById(R.id.accountName);
        editTextSurname = findViewById(R.id.accountSurname);
        editTextUserName = findViewById(R.id.accountUserName);
        submitButton = findViewById(R.id.updateProfileConfirm);
        editTextMobileNumber = findViewById(R.id.accountPhoneNumber);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating. Please Wait...");
        progressDialog.setCancelable(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                surname = editTextSurname.getText().toString();
                username = editTextUserName.getText().toString();
                mobNumber = editTextMobileNumber.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(mobNumber) && mobNumber.length() <= 10) {
                    progressDialog.show();
                    saveUserInformation();
                } else {
                    if (TextUtils.isEmpty(name)) {
                        editTextName.setError("Required Field");
                        editTextName.requestFocus();
                    }
                    if (TextUtils.isEmpty(surname)) {
                        editTextSurname.setError("Required Field");
                        editTextSurname.requestFocus();
                    }
                    if (TextUtils.isEmpty(username)) {
                        editTextUserName.setError("Required Field");
                        editTextUserName.requestFocus();
                    }
                    if (TextUtils.isEmpty(mobNumber)) {
                        editTextMobileNumber.setError("Required Field");
                        editTextMobileNumber.requestFocus();
                    }

                    progressDialog.dismiss();
                }
            }
        });

        editDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
            }
        });
    }

    private void saveUserInformation() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if (firebaseUser != null && uriProfileImage != null) {
            databaseReference.child(username).child("Name").setValue(name)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child(username).child("Surname").setValue(surname)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseReference.child(username).child("Username").setValue(username)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            databaseReference.child(username).child("Mobile Number").setValue(mobNumber)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            uploadToImageToStorage();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountDetailsActivity.this, "Adding new Account failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Toast.makeText(this, "Please select an image to upload", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                //get bitmap from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                //display image on imageView using glide
                Glide.with(this).load(bitmap).into(displayImageHolder);
                //uploadToImageToStorage();               //upload the image to FireBase Storage
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToImageToStorage() {
        if (uriProfileImage != null) {
            storageReference = FirebaseStorage.getInstance().getReference("ProfilePics/").child(username)
                    .child(username + System.currentTimeMillis() + ".jpg");

            storageReference.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();
                                    databaseReference.child(username).child("dp link").setValue(profileImageUrl)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(username)
                                                            .setPhotoUri(Uri.parse(profileImageUrl))
                                                            .build();

                                                    firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.dismiss();

                                                            ProfileManager.userDPUrl = profileImageUrl;
                                                            ProfileManager.userName = name;
                                                            ProfileManager.userSurname = surname;
                                                            ProfileManager.userMobile = mobNumber;

                                                            Intent intent = new Intent(AccountDetailsActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountDetailsActivity.this, "Uploading image to server failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });
        } else {
            Toast.makeText(this, "Please select a image to upload", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}
