package com.example.chatterbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
