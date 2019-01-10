/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.morningstar.chattr.R;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDetailsActivity extends AppCompatActivity {

    private CircleImageView displayImageHolder, editDisplayImage;
    private EditText editTextUserName;
    private static final int CHOOSE_IMAGE = 1;
    private EditText editTextName, editTextSurname, editTextMobileNumber;
    private Toolbar toolbar;
    private Button submitButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private String name = "";
    private String surname = "";
    private String username = "";
    private String mobNumber = "";
    private Uri uriProfileImage;

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
        progressBar = findViewById(R.id.imageUploadingProgress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                surname = editTextSurname.getText().toString();
                username = editTextUserName.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(mobNumber)) {
                    Toast.makeText(AccountDetailsActivity.this, "Working", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(name))
                        editTextName.setError("Required Field");
                    if (TextUtils.isEmpty(surname))
                        editTextSurname.setError("Required Field");
                    if (TextUtils.isEmpty(username))
                        editTextUserName.setError("Required Field");
                    if (TextUtils.isEmpty(mobNumber))
                        editTextMobileNumber.setError("Required Field");
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
                uploadToImageToStorage();               //upload the image to FireBase Storage
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToImageToStorage() {
        storageReference = FirebaseStorage.getInstance().getReference();

    }
}
