package com.example.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadProfilePicActivity extends AppCompatActivity {

    public EditText name,dob,phone,email,gender,bio;
    private CircleImageView profilepic;
    private Button savepic,isEditable;
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference databaseReference,mdatabaseReference;
    private String myphoneno;
    private static final int PICK_IMAGE_REQUEST = 111;

    private String TAG = "UploadProfilePicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        phone = findViewById(R.id.number);
        email = findViewById(R.id.email_id);
        gender = findViewById(R.id.male_female);
        profilepic = findViewById(R.id.profile_pic);
        bio = findViewById(R.id.bio_et);
        savepic = findViewById(R.id.save_pic);
        isEditable = findViewById(R.id.is_editable);

        Intent i = getIntent();
        myphoneno = i.getStringExtra("MyPhoneNo");


        databaseReference = FirebaseDatabase.getInstance().getReference().child("User Data").child(myphoneno);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pic").child(myphoneno);


        profilepic.setEnabled(false);
        savepic.setVisibility(View.VISIBLE);
        isEditable.setVisibility(View.INVISIBLE);

        checkFilePermissions();
        setDetails();
        showFileChooser();
    }

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            int permissionCheck = UploadProfilePicActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += UploadProfilePicActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{ }
    }


    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilepic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void UploadImage(View view) {

        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            GetDownloadurl();
                            Log.d(TAG,"Getting url");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        else {
            //if there is not any file
            Toast.makeText(getApplicationContext(), "Please choose a Picture", Toast.LENGTH_LONG).show();
        }
        savepic.setVisibility(View.INVISIBLE);
    }


    public void GetDownloadurl(){

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Log.d(TAG,"Uploading url");
                databaseReference.child("Profile Pic").setValue(String.valueOf(downloadUrl));
                Intent intent = new Intent(UploadProfilePicActivity.this,ProfileActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Uploading url");

            }
        });
    }


    public void setDetails() {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(myphoneno);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Biodata setting
                if (dataSnapshot.child("Name").exists())
                { name.setText(dataSnapshot.child("Name").getValue().toString()); }
                if (dataSnapshot.child("DOB").exists())
                { dob.setText(dataSnapshot.child("DOB").getValue().toString()); }
                if (dataSnapshot.child("Email").exists())
                { email.setText(dataSnapshot.child("Email").getValue().toString()); }
                if (dataSnapshot.child("Gender").exists())
                { gender.setText(dataSnapshot.child("Gender").getValue().toString()); }
                if (dataSnapshot.child("Bio").exists())
                { bio.setText(dataSnapshot.child("Bio").getValue().toString()); }

                // Profile Picture Setting
                if (dataSnapshot.child("Profile Pic").exists())
                { Glide.with(UploadProfilePicActivity.this).asBitmap()
                        .load(dataSnapshot.child("Profile Pic").getValue().toString()).into(profilepic); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("User Data"); // Used for setting Online status
        mdatabaseReference.child(myphoneno).child("OnlineStatus").setValue("true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("User Data"); // Used for setting Online status
        mdatabaseReference.child(myphoneno).child("OnlineStatus").setValue("false");

    }


}
