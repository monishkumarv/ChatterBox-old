package com.example.chatterbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfileActivity extends AppCompatActivity {

    public EditText name;
    public String phoneno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        name = findViewById(R.id.name);
        Intent i = getIntent();
        phoneno = i.getStringExtra("PhoneNo");
    }

    public void UpdateName(View v){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("User Data");
        reference.child(phoneno).child("Name").setValue(name.getText().toString());

        Intent intent = new Intent(CreateProfileActivity.this, HomePage.class);
        startActivity(intent);

    }
}
