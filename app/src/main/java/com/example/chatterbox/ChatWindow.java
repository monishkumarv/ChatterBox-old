package com.example.chatterbox;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatWindow extends AppCompatActivity {

    private static final String TAG = "ChatWindowLog";
    public String friendPhoneNo,newmsg, myPhoneNo,name;
    public EditText msg;
    public ArrayList<Messages> myDisplayMessages = new ArrayList<>();
    public ArrayList<Messages> friendDisplayMessages = new ArrayList<>();
    public FirebaseAuth mAuth;
    public FirebaseDatabase mfirebaseDatabase;
    public DatabaseReference mdatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        Log.d(TAG,"......................................................");

        mAuth = FirebaseAuth.getInstance();
        myPhoneNo = mAuth.getCurrentUser().getPhoneNumber();
        Intent i = getIntent();
        friendPhoneNo = i.getStringExtra("FRIEND_PHONENO");
        name = i.getStringExtra("Title");

        // Action Bar (Title Bar)
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.ic_launcher_foreground);
        ab.setDisplayUseLogoEnabled(false);
        setTitle(friendPhoneNo);
        if (name!=null){setTitle(name);}


        // Getting Firebase Reference
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference().child("User Data");

        // Getting Old Messages
        RetrieveMessages(myPhoneNo,friendPhoneNo,myDisplayMessages,true);
        RetrieveMessages(friendPhoneNo,myPhoneNo,friendDisplayMessages,true);

        msg = findViewById(R.id.enter_message);

        // Send TYPE STATUE...
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Shows in your friend's database...Whether you are Typing
                if (s.length() != 0){
                    mdatabaseReference.child(friendPhoneNo).child("Typing Status").child(myPhoneNo).setValue("true");
                }else {
                    mdatabaseReference.child(friendPhoneNo).child("Typing Status").child(myPhoneNo).setValue("false");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Checking and Updating TYPE STATUS...
        mdatabaseReference.child(myPhoneNo).child("Typing Status").child(friendPhoneNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if (dataSnapshot.getValue().toString().equals("true")){
                        Log.d(TAG,"Typing...");

                        callRecyclerView(myDisplayMessages,true);

                    }else {
                        RetrieveMessages(myPhoneNo,friendPhoneNo,myDisplayMessages,true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void callRecyclerView(ArrayList<Messages> allMessages, Boolean isTyping){

        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);
        LinearLayoutManager mlayoutmanager = new LinearLayoutManager(this);
        RecyclerViewAdapter_Chat adapter = new RecyclerViewAdapter_Chat(this, allMessages,myPhoneNo,friendPhoneNo,isTyping);
        mlayoutmanager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mlayoutmanager);
        recyclerView.setAdapter(adapter);

    }


    public void SendMessage(View view){

        newmsg = msg.getText().toString();
        msg.getText().clear();

        Messages mybuffer = new Messages(true,newmsg,"unread");
        Messages frndbuffer = new Messages(false,newmsg,"unread");

        RetrieveMessages(friendPhoneNo,myPhoneNo,friendDisplayMessages,false);

        myDisplayMessages.add(mybuffer);
        friendDisplayMessages.add(frndbuffer);

        // Updating Database...
        mdatabaseReference.child(myPhoneNo).child("messages").child(friendPhoneNo).setValue(myDisplayMessages);
        mdatabaseReference.child(friendPhoneNo).child("messages").child(myPhoneNo).setValue(friendDisplayMessages);  //update friend's data

        // Setting message status as read/unread for ur friend
        mdatabaseReference.child(friendPhoneNo).child("unreadmessages").child(myPhoneNo).setValue("true");

        // Date & Time of Last Message
        String currentDate = DateFormat.getDateInstance().format(new Date());
        String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        mdatabaseReference.child(myPhoneNo).child("date_time").child(friendPhoneNo).child("date").setValue(currentDate);
        mdatabaseReference.child(myPhoneNo).child("date_time").child(friendPhoneNo).child("time").setValue(currentTime);
        mdatabaseReference.child(friendPhoneNo).child("date_time").child(myPhoneNo).child("date").setValue(currentDate);
        mdatabaseReference.child(friendPhoneNo).child("date_time").child(myPhoneNo).child("time").setValue(currentTime);

        // Update Last Message
        mdatabaseReference.child(myPhoneNo).child("lastmessage").child(friendPhoneNo).setValue(mybuffer.message);
        mdatabaseReference.child(friendPhoneNo).child("lastmessage").child(myPhoneNo).setValue(mybuffer.message);

        callRecyclerView(myDisplayMessages,false);


    }

    public void RetrieveMessages(String parentNumber, String childNumber, final ArrayList<Messages> messagelist, final Boolean CALL_RECYCLERVIEW){
        Log.d(TAG,"Retrieving process started");

        DatabaseReference newReference = mdatabaseReference.child(parentNumber).child("messages").child(childNumber);

        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messagelist.clear();
                for (DataSnapshot msgitem: dataSnapshot.getChildren()){
                    Log.d(TAG,"Retrieving in process");

                    Messages temp = msgitem.getValue(Messages.class);  // error in this line

                    Log.d(TAG,temp.message);
                    messagelist.add(temp);

                    if(CALL_RECYCLERVIEW){
                         callRecyclerView(myDisplayMessages,false);
                        Log.d(TAG,"RecyclerView updated");

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "error:" + databaseError);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mf = getMenuInflater();
        mf.inflate(R.menu.actionbar_chatwindow,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

           case R.id.view_profile:
               Intent intent = new Intent(ChatWindow.this, ProfileActivity.class);
               intent.putExtra("PhoneNo", friendPhoneNo);
               intent.putExtra("Editable", "false");
               intent.putExtra("NewUser", "false");
               startActivity(intent);break;

            default:
                return super.onOptionsItemSelected(item);



        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
