package com.example.chatterbox;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Activity {

    public EditText name,dob,phone,email,gender,bio;
    public String phoneno,isEditable,isNewUser;
    CircleImageView profilepic;
    private String TAG = "ProfileActivityLog";
    Button iseditable,submitprofile;
    TextView createprofileTv;

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
        createprofileTv = findViewById(R.id.create_profile_tv);
        iseditable = findViewById(R.id.is_editable);
        submitprofile = findViewById(R.id.submit_profile);

        Intent i = getIntent();
        phoneno = i.getStringExtra("PhoneNo");
        isEditable = i.getStringExtra("Editable");
        isNewUser = i.getStringExtra("NewUser");

        phone.setText(phoneno);
        setDetails();

        if (isNewUser.equals("true")){
            createprofileTv.setVisibility(View.VISIBLE);
        }else {
            createprofileTv.setVisibility(View.INVISIBLE);
        }

        if (isEditable.equals("true")){                // View Your Profile
            profilepic.setEnabled(true);
            iseditable.setVisibility(View.VISIBLE);
        }else {                                        // View Friend's Profile
            profilepic.setEnabled(false);
            iseditable.setVisibility(View.INVISIBLE);
        }


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
                if (dataSnapshot.child("Bio").exists())
                { bio.setText(dataSnapshot.child("Bio").getValue().toString()); }

                // Profile Picture Setting
                if (dataSnapshot.child("Profile Pic").exists())
                { Glide.with(ProfileActivity.this).asBitmap()
                        .load(dataSnapshot.child("Profile Pic").getValue().toString()).into(profilepic); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

    public void ChangeDP(View view){

         Intent intent = new Intent(ProfileActivity.this, UploadImageActivity.class);
         intent.putExtra("MyPhoneNo", phoneno);
         intent.putExtra("Picture_Type", "Profile Pic");
         startActivity(intent);
    }

    public void UpdateDetails(View view){

        if (isEditable.equals("true")) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference().child("User Data");
            reference.child(phoneno).child("Name").setValue(name.getText().toString());
            reference.child(phoneno).child("DOB").setValue(dob.getText().toString());
            reference.child(phoneno).child("Email").setValue(email.getText().toString());
            reference.child(phoneno).child("Gender").setValue(gender.getText().toString());
            reference.child(phoneno).child("Bio").setValue(bio.getText().toString());

            Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

            name.setFocusable(false);
            dob.setFocusable(false);
            email.setFocusable(false);
            gender.setFocusable(false);
            bio.setFocusable(false);
            submitprofile.setVisibility(View.INVISIBLE);

            if (isNewUser.equals("true")){
                Intent intent = new Intent(ProfileActivity.this, HomePage.class);
                startActivity(intent);
            }

        }
    }

    public void Edit(View view) {

        name.setFocusableInTouchMode(true);
        dob.setFocusableInTouchMode(true);
        email.setFocusableInTouchMode(true);
        gender.setFocusableInTouchMode(true);
        bio.setFocusableInTouchMode(true);
        submitprofile.setVisibility(View.VISIBLE);
    }

    public void BackButton(View view) {
        Intent intent = new Intent(ProfileActivity.this, HomePage.class);
        startActivity(intent);
    }
}
