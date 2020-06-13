package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;


public class SettingsActivity extends AppCompatActivity {
    Button btnUpdateProfile;
    EditText edtUserName, edtUserStatus;
    ImageView imgProfile;
    TextInputLayout textInputLayoutUserName, textInputLayoutStatus;
    boolean kiemtrathongtin = false;
    String currentUserID;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private static final int GalleryPick = 1;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        RetrieveUserInfo();


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent();
                iGallery.setAction(Intent.ACTION_GET_CONTENT);
                iGallery.setType("image/*");
                startActivityForResult(iGallery, GalleryPick);
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String chuoi = edtUserName.getText().toString();
                    if (chuoi.trim().equals("") || chuoi.equals(null)) {
                        textInputLayoutUserName.setError("Bạn chưa nhập tên người dùng");
                        textInputLayoutUserName.setErrorEnabled(true);
                        kiemtrathongtin = false;
                    } else {
                        textInputLayoutUserName.setError("");
                        textInputLayoutUserName.setErrorEnabled(false);
                        kiemtrathongtin = true;
                    }
                }
            }
        });
        edtUserStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String chuoi = edtUserStatus.getText().toString();
                    if (chuoi.trim().equals("") || chuoi.equals(null)) {
                        textInputLayoutStatus.setError("Bạn chưa nhập giới thiệu");
                        textInputLayoutStatus.setErrorEnabled(true);
                        kiemtrathongtin = false;
                    } else {
                        textInputLayoutStatus.setError("");
                        textInputLayoutStatus.setErrorEnabled(false);
                        kiemtrathongtin = true;
                    }
                }
            }
        });


        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUserName.getText().toString().trim();
                String status = edtUserStatus.getText().toString().trim();

                if (kiemtrathongtin) {
                    HashMap<String, String> profileMap = new HashMap<>();
                    profileMap.put("uid", currentUserID);
                    profileMap.put("name", username);
                    profileMap.put("status", status);

                    mDatabase.child("users").child(currentUserID).setValue(profileMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Error. Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");//This way we link the userId with image. This is the file name of the image stored in firebase database.

                UploadTask uploadTask = filePath.putFile(resultUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Toast.makeText(SettingsActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();

                            if (downloadUri != null) {

                                String downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                mDatabase.child("users").child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (!task.isSuccessful()) {
                                            String error = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
                                        } else {

                                        }
                                    }
                                });
                            }

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }

        }
    }

    private void RetrieveUserInfo() {
        mDatabase.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status") && dataSnapshot.hasChild("image")) {
                    String image = dataSnapshot.child("image").getValue().toString();
                    edtUserName.setText(dataSnapshot.child("name").getValue().toString());
                    edtUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                    Picasso.get().load(image).into(imgProfile);
                } else if (dataSnapshot.child("name").exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")) {
                    edtUserName.setText(dataSnapshot.child("name").getValue().toString());
                    edtUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                } else {
                    edtUserName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please Update your profile name  ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        edtUserName = findViewById(R.id.edtUserName);
        edtUserStatus = findViewById(R.id.edtUserStatus);
        imgProfile = findViewById(R.id.imgProfile);
        textInputLayoutUserName = findViewById(R.id.textInputLayoutUserName);
        textInputLayoutStatus = findViewById(R.id.textInputLayoutStatus);

    }

}
