package com.example.chatterbox;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RecyclerViewAdapter_Friends extends RecyclerView.Adapter<RecyclerViewAdapter_Friends.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapterLog";

    private ArrayList<String> mFriendslist = new ArrayList<>();
    private Context mContext;
    public String name,myPhoneNo;

    public RecyclerViewAdapter_Friends(Context context, ArrayList<String> friendslist, String phoneNo) {
        mFriendslist = friendslist;
        mContext = context;
        myPhoneNo = phoneNo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_friends, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    public String checkunreadstatus;
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called...............");
        setName(mFriendslist.get(position),holder);

        FirebaseDatabase mfirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mdatabaseReference = mfirebaseDatabase.getReference().child("User Data")
                                                                               .child(myPhoneNo)
                                                                               .child("unreadmessages")
                                                                               .child(mFriendslist.get(position));
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    checkunreadstatus = dataSnapshot.getValue().toString();
                    Log.d(TAG, mFriendslist.get(position) + " -status- " + checkunreadstatus );

                    if(checkunreadstatus.equals("true")){
                        Log.d(TAG, "Visible");
                        holder.unreadMessages.setVisibility(View.VISIBLE);
                    }
                    else {
                        Log.d(TAG, "Invisible");
                        holder.unreadMessages.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "error:" + databaseError);

            }
        });



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: clicked on:  " + mFriendslist.get(position));

            mdatabaseReference.setValue("false");
            holder.unreadMessages.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(mContext, ChatWindow.class);
            intent.putExtra("FRIEND_PHONENO", mFriendslist.get(position));
            mContext.startActivity(intent);

        }
        });
    }

    @Override
    public int getItemCount() {
        return mFriendslist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mPhoneNo,unreadMessages;
        RelativeLayout parentLayout;   //layout of each induvidual units of the list

        public ViewHolder(View itemView) {
            super(itemView);
            mPhoneNo = itemView.findViewById(R.id.phone_no);
            unreadMessages = itemView.findViewById(R.id.unread_messages);
            parentLayout = itemView.findViewById(R.id.layout_list_friends);
        }
    }

    public void setName(String phoneNo,ViewHolder holder) {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(phoneNo);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Name").exists())
                {
                    name =dataSnapshot.child("Name").getValue().toString();
                    holder.mPhoneNo.setText(name);    // Updating RecyclerView with ProfileName(instead of phoneNo)

                }else
                    {
                    holder.mPhoneNo.setText(phoneNo);  // If profile is not created already

                }

//                Log.d(TAG,name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }
}

