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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadImageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 111;
    private CircleImageView profilepic;
    private Button savepic;
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String myphoneno;
    private String TAG = "UploadImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        myphoneno = i.getStringExtra("MyPhoneNo");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User Data").child(myphoneno);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pic").child(myphoneno);
        profilepic = findViewById(R.id.profile_pic);
        savepic = findViewById(R.id.save_pic);
        savepic.setVisibility(View.VISIBLE);

        checkFilePermissions();
        showFileChooser();

    }

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            int permissionCheck = UploadImageActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += UploadImageActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
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
                Intent intent = new Intent(UploadImageActivity.this,ProfileActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Uploading url");

            }
        });
    }

}
