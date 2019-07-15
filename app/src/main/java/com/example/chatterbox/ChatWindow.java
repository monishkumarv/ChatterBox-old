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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    private static final String TAG = "ChatWindowLog";
    String friendPhoneNo,newmsg, myPhoneNo;
    ArrayList<Messages> myDisplayMessages = new ArrayList<>();
    ArrayList<Messages> friendDisplayMessages = new ArrayList<>();
    public FirebaseAuth mAuth;
    public FirebaseDatabase mfirebaseDatabase;
    public DatabaseReference mdatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
		
        mAuth = FirebaseAuth.getInstance();
        myPhoneNo = mAuth.getCurrentUser().getPhoneNumber();
        Intent i = getIntent();
        friendPhoneNo = i.getStringExtra("FRIEND_PHONENO");

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference().child("User Data");

        TextView Title = findViewById(R.id.title_phoneNo);
        Title.setText(friendPhoneNo);

        RetrieveMessages(myPhoneNo,friendPhoneNo,myDisplayMessages,true);
        RetrieveMessages(friendPhoneNo,myPhoneNo,friendDisplayMessages,true);

    }

    private void callRecyclerView(ArrayList<Messages> allMessages){
        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);
        RecyclerViewAdapter_Chat adapter = new RecyclerViewAdapter_Chat(this, allMessages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    public void SendMessage(View view){

        EditText msg = findViewById(R.id.enter_message);
        newmsg = msg.getText().toString();
        msg.getText().clear();

        Messages mybuffer = new Messages(true,newmsg);
        Messages frndbuffer = new Messages(false,newmsg);

        RetrieveMessages(friendPhoneNo,myPhoneNo,friendDisplayMessages,false);

        myDisplayMessages.add(mybuffer);
        friendDisplayMessages.add(frndbuffer);

        // Updating Database...
        mdatabaseReference.child(myPhoneNo).child("messages").child(friendPhoneNo).setValue(myDisplayMessages);
        mdatabaseReference.child(friendPhoneNo).child("messages").child(myPhoneNo).setValue(friendDisplayMessages);  //update friend's data
        callRecyclerView(myDisplayMessages);

        // Setting message status as unread for ur friend
        mdatabaseReference.child(friendPhoneNo).child("unreadmessages").child(myPhoneNo).setValue("true");
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
                         callRecyclerView(myDisplayMessages);
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
}
