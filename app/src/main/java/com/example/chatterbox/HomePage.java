package com.example.chatterbox;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePageLog";
    public FirebaseAuth mAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;

    public String myPhoneNo,friendPhoneNo;
    private ArrayList<String> friendslist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Action Bar
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.ic_launcher_foreground);
        ab.setDisplayUseLogoEnabled(false);
        ab.setLogo(R.drawable.ic_message_24dp);
        ab.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        myPhoneNo = mAuth.getCurrentUser().getPhoneNumber();

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference().child("User Data");

        RetrieveFriends();
        callRecyclerView();
    }


    public void callRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.recycler_view_friends);
        RecyclerViewAdapter_Friends adapter = new RecyclerViewAdapter_Friends(this, this.friendslist,myPhoneNo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    public void AddFriend (View view){


        String currentDate = DateFormat.getDateInstance().format(new Date());
        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        EditText new_friend = findViewById(R.id.friend_phoneNo);
        friendPhoneNo = new_friend.getText().toString();


        if (myPhoneNo.equals(friendPhoneNo) ){
            Toast.makeText(HomePage.this,"Try Again",Toast.LENGTH_SHORT).show();
            new_friend.setText("+91");
            return;
        }
        if (myPhoneNo.length() != 13){
            Toast.makeText(HomePage.this,"Try Again",Toast.LENGTH_SHORT).show();
            new_friend.setText("+91");
            return;
        }

        if (!checkFriend()){
            // updating Friends List
            friendslist.add(friendPhoneNo);
            mdatabaseReference.child(myPhoneNo).child("friends").setValue(friendslist);

            // Updating Date and Time of last encounter
            mdatabaseReference.child(myPhoneNo).child("date_time").child(friendPhoneNo).child("date").setValue(currentDate);
            mdatabaseReference.child(myPhoneNo).child("date_time").child(friendPhoneNo).child("time").setValue(currentTime);
            mdatabaseReference.child(friendPhoneNo).child("date_time").child(myPhoneNo).child("date").setValue(currentDate);
            mdatabaseReference.child(friendPhoneNo).child("date_time").child(myPhoneNo).child("time").setValue(currentTime);

            //
//            mdatabaseReference.child(myPhoneNo).child("Typing Status").child(friendPhoneNo).setValue("false");

            callRecyclerView();

        } new_friend.setText("+91");

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

    public boolean checkFriend(){

        for (int i = 0; i <friendslist.size() ; i++) {
            if (friendslist.get(i).equals(friendPhoneNo)){
                Toast.makeText(HomePage.this,"You are already friends",Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }



    /** Action Bar...**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mf = getMenuInflater();
        mf.inflate(R.menu.actionbar_homepage,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.sign_out:
                signOut();break;

            case R.id.my_profile:
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.putExtra("PhoneNo", myPhoneNo);
                intent.putExtra("Editable", "true");
                intent.putExtra("NewUser", "false");
                startActivity(intent);break;

            case R.id.refresh_page:
                Toast.makeText(getApplicationContext(),"Refreshing",Toast.LENGTH_SHORT).show();
                callRecyclerView();
                break;

            default:
                return super.onOptionsItemSelected(item);



        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder a_builder = new AlertDialog.Builder(HomePage.this);
        // a_builder(variable) is created for AlertBuilder datatype (analogy)
        a_builder.setMessage("Your mates are going to miss u :(")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();                        // Close the 'Dialogue box' and resumes the 'App'
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Are u sure?");
        alert.show();

    }
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(HomePage.this,"Signout Successfull",Toast.LENGTH_LONG).show();
        Intent i = new Intent(HomePage.this,MainActivity.class);
        startActivity(i);

    }

}
