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
import android.widget.Toast;

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
        phone = findViewById(R.id.number);

        Intent i = getIntent();
        phoneno = i.getStringExtra("PhoneNo");
        phone.setText(phoneno);
        setDetails();

    }

    public void ChangeProfilePic(View view){
        Intent intent = new Intent(ProfileActivity.this,UploadImageActivity.class);
        intent.putExtra("MyPhoneNo", phoneno);
        startActivity(intent);
        Toast.makeText(ProfileActivity.this,"Profile Pic Updated",Toast.LENGTH_SHORT).show();

    }

    public void UpdateDetails(View v){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("User Data");
        reference.child(phoneno).child("Name").setValue(name.getText().toString());
        reference.child(phoneno).child("DOB").setValue(dob.getText().toString());
        reference.child(phoneno).child("Email").setValue(email.getText().toString());
        reference.child(phoneno).child("Gender").setValue(gender.getText().toString());

        Intent intent = new Intent(ProfileActivity.this, HomePage.class);
        startActivity(intent);

    }

    public void setDetails() {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(phoneno);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Name").exists())
                { name.setText(dataSnapshot.child("Name").getValue().toString()); }
                if (dataSnapshot.child("DOB").exists())
                { dob.setText(dataSnapshot.child("DOB").getValue().toString()); }
                if (dataSnapshot.child("Email").exists())
                { email.setText(dataSnapshot.child("Email").getValue().toString()); }
                if (dataSnapshot.child("Gender").exists())
                { gender.setText(dataSnapshot.child("Gender").getValue().toString()); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }



}
