package com.example.chatterbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class asf extends AppCompatActivity {

    private static final int Pick_Photo = 1;
    public DatabaseReference databaseReference;
    public StorageReference storageReference;
    private String myphoneno, picturetype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadimage);

        Intent i = getIntent();
        myphoneno = i.getStringExtra("MyPhoneNo");
        picturetype = i.getStringExtra("Picture_Type");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User Data").child(myphoneno);
        storageReference = FirebaseStorage.getInstance().getReference().child(picturetype);

        Toast.makeText(asf.this,"........",Toast.LENGTH_LONG).show();
        UploadImage();

    }


    public void UploadImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, Pick_Photo);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Pick_Photo && resultCode == RESULT_OK) {

            final Uri uri = data.getData();
            //this is for image file name
            final StorageReference filepath = storageReference.child(myphoneno);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            databaseReference.child(picturetype).setValue(String.valueOf(uri));

                            Intent intent = new Intent(asf.this,ProfileActivity.class);
                            startActivity(intent);

                        }

                    });
                }
            });
        }
    }

}
