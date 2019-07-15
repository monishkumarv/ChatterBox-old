package com.example.chatterbox;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        ActionBar ab = getSupportActionBar();               // Creating an object in ActionBar
        ab.setLogo(R.drawable.ic_launcher_foreground);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        myPhoneNo = mAuth.getCurrentUser().getPhoneNumber();

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference().child("User Data");


        RetrieveFriends();
        callRecyclerView();
    }


    private void callRecyclerView(){
        Toast.makeText(HomePage.this, "calling recyclerview...", Toast.LENGTH_SHORT).show();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_friends);
        RecyclerViewAdapter_Friends adapter = new RecyclerViewAdapter_Friends(this, this.friendslist,myPhoneNo);
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


    /** Action Bar...**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mf = getMenuInflater();
        mf.inflate(R.menu.actionbar_mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.my_profile:
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.putExtra("PhoneNo", myPhoneNo);
                startActivity(intent);

            case R.id.settings_id:
                Toast.makeText(getApplicationContext(),"Settings icon is selected",Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);



        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();

    }
}
