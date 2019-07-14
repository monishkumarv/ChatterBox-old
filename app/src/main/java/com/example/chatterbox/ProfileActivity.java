package com.example.chatterbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    public EditText name;
    public String phoneno;
    private String TAG = "ProfileActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        Intent i = getIntent();
        phoneno = i.getStringExtra("PhoneNo");

        setName();

    }

    public void UpdateName(View v){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("User Data");
        reference.child(phoneno).child("Name").setValue(name.getText().toString());

        Intent intent = new Intent(ProfileActivity.this, HomePage.class);
        startActivity(intent);

    }

    public void setName() {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(phoneno);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Name").exists())
                {
                    name.setText(dataSnapshot.child("Name").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ProfileActivity.this,"Signout Successfull",Toast.LENGTH_LONG).show();
        Intent i = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(i);

    }

}
