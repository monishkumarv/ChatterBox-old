package com.example.chatterbox;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


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
    public Boolean NameExists;
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called");
        setName(mFriendslist.get(position),holder);
        SetProfilePic(mFriendslist.get(position),holder);
        setDateTime(mFriendslist.get(position),holder);
        setLastMsg(mFriendslist.get(position),holder);

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

                    if(checkunreadstatus.equals("true")){
                        Log.d(TAG, "Not Read");
                        holder.previousMessage.setTextColor(Color.parseColor("#2125FF"));
                        holder.date.setTextColor(Color.parseColor("#2125FF"));
                    }
                    else {
                        Log.d(TAG, "Read");
                        holder.previousMessage.setTextColor(Color.parseColor("#808080"));
                        holder.date.setTextColor(Color.parseColor("#808080"));
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
                holder.previousMessage.setTextColor(Color.parseColor("#808080"));
                holder.date.setTextColor(Color.parseColor("#808080"));

                Intent intent = new Intent(mContext, ChatWindow.class);
                if (NameExists){
                    intent.putExtra("Title", holder.mPhoneNo.getText());
                    intent.putExtra("FRIEND_PHONENO", mFriendslist.get(position));

                }else {
                    intent.putExtra("FRIEND_PHONENO", mFriendslist.get(position));
                }
                mContext.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return mFriendslist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mPhoneNo,date,previousMessage;
        CircleImageView profilepic;
        RelativeLayout parentLayout;   //layout of each induvidual units of the list

        public ViewHolder(View itemView) {
            super(itemView);
            mPhoneNo = itemView.findViewById(R.id.phone_no);
            profilepic = itemView.findViewById(R.id.friend_profile_pic);
            previousMessage = itemView.findViewById(R.id.previous_message);
            date = itemView.findViewById(R.id.msg_date);
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
                    NameExists = true;
                    name =dataSnapshot.child("Name").getValue().toString();
                    holder.mPhoneNo.setText(name);    // Updating RecyclerView with ProfileName(instead of phoneNo)

                }else
                {
                    NameExists = true;
                    holder.mPhoneNo.setText(phoneNo);  // If profile is not created already

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

    public void SetProfilePic(String phoneNo,ViewHolder holder) {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(phoneNo);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Profile Pic").exists())
                { Glide.with(mContext).asBitmap()
                        .load(dataSnapshot.child("Profile Pic").getValue().toString()).into(holder.profilepic); }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

    public void setDateTime(String phoneNo,ViewHolder holder){

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data")
                .child(myPhoneNo)
                .child("date_time")
                .child(phoneNo);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String todaysdate = DateFormat.getDateInstance().format(new Date());

                if (todaysdate.equals(dataSnapshot.child("date").getValue().toString())){
                    holder.date.setText(dataSnapshot.child("time").getValue().toString());
                }else {
                    holder.date.setText(dataSnapshot.child("date").getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

    public void setLastMsg(String phoneNo,ViewHolder holder){

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data")
                .child(myPhoneNo)
                .child("lastmessage")
                .child(phoneNo);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    name = dataSnapshot.getValue().toString();
                    holder.previousMessage.setText(name);

                } else {
                    holder.previousMessage.setText(" ");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

}
