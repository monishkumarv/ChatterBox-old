package com.example.chatterbox;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePageLog";
    public FirebaseAuth mAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;

    String myPhoneNo;
    private ArrayList<String> friendslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        myPhoneNo = mAuth.getCurrentUser().getPhoneNumber();

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference().child("User Data");

        RetrieveFriends();
        callRecyclerView();
    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(HomePage.this,"Signout Successfull",Toast.LENGTH_LONG).show();
        Intent i = new Intent(HomePage.this,MainActivity.class);
        startActivity(i);

    }

    private void callRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_friends);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, this.friendslist);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void AddFriend (View view){


        EditText new_friend = findViewById(R.id.friend_phoneNo);

        friendslist.add(new_friend.getText().toString());
        new_friend.setText("+91");
        mdatabaseReference.child(myPhoneNo).child("friends").setValue(friendslist);

        callRecyclerView();

    }

    public void RetrieveFriends(){
//        Toast.makeText(HomePage.this,"Searching 4ur friends...",Toast.LENGTH_LONG).show();
        Log.d(TAG,"Retrieving process started");

        DatabaseReference newReference = mdatabaseReference.child(myPhoneNo).child("friends");

        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                friendslist.clear();
                for (DataSnapshot frienditem: dataSnapshot.getChildren()){
                    Log.d(TAG,"Retrieving in process");

                    try {
                        String buffer = frienditem.getValue(String.class);  // error in this line
                        friendslist.add(buffer);
                    }catch (Exception e){
                        Log.d(TAG,e.toString());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "error:" + databaseError);

            }
        });

    }

}
