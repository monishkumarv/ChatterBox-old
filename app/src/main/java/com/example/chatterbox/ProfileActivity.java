package com.example.chatterbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Activity {

    public EditText name,dob,phone,email,gender;
    public String phoneno;
    CircleImageView profilepic;
    ImageView coverpic;
    private String TAG = "ProfileActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        email = findViewById(R.id.email_id);
        gender = findViewById(R.id.male_female);
        profilepic = findViewById(R.id.profile_pic);
        coverpic = findViewById(R.id.cover_pic);
        phone = findViewById(R.id.number);

        Intent i = getIntent();
        phoneno = i.getStringExtra("PhoneNo");
        phone.setText(phoneno);
        setDetails();


    }

    public void ChangeDP(View view){
        Intent intent = new Intent(ProfileActivity.this,UploadImageActivity.class);
        intent.putExtra("MyPhoneNo", phoneno);
        intent.putExtra("Picture_Type", "Profile Pic");
        startActivity(intent);
    }

    public void ChangeCover(View view) {
        Intent intent = new Intent(ProfileActivity.this,UploadImageActivity.class);
        intent.putExtra("MyPhoneNo", phoneno);
        intent.putExtra("Picture_Type", "Cover Pic");
        startActivity(intent);
    }

    public void UpdateDetails(View view){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("User Data");
        reference.child(phoneno).child("Name").setValue(name.getText().toString());
        reference.child(phoneno).child("DOB").setValue(dob.getText().toString());
        reference.child(phoneno).child("Email").setValue(email.getText().toString());
        reference.child(phoneno).child("Gender").setValue(gender.getText().toString());

        Toast.makeText(ProfileActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();

        coverpic.setFocusable(false);
        profilepic.setFocusable(false);
        name.setFocusable(false);
        dob.setFocusable(false);
        email.setFocusable(false);
        gender.setFocusable(false);
    }

    public void setDetails() {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(phoneno);

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
                // Picture Setting
                if (dataSnapshot.child("Profile Pic").exists())
                { Glide.with(ProfileActivity.this).asBitmap()
                        .load(dataSnapshot.child("Profile Pic").getValue().toString()).into(profilepic); }
                if (dataSnapshot.child("Cover Pic").exists())
                { Glide.with(ProfileActivity.this).asBitmap()
                        .load(dataSnapshot.child("Cover Pic").getValue().toString()).into(coverpic); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

    public void Edit(View view) {
        coverpic.setFocusable(true);
        profilepic.setFocusable(true);
        name.setFocusable(true);
        dob.setFocusable(true);
        email.setFocusable(true);
        gender.setFocusable(true);
    }

    public void BackButton(View view) {
        Intent intent = new Intent(ProfileActivity.this, HomePage.class);
        startActivity(intent);
    }
}
