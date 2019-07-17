package com.example.chatterbox;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewAdapter_Chat extends RecyclerView.Adapter<RecyclerViewAdapter_Chat.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterChat";

    private ArrayList<Messages> mMessageList = new ArrayList<>();
    private Context mContext;
    private String friendPhoneNo;

    public RecyclerViewAdapter_Chat(Context context, ArrayList<Messages> displaymessages, String phoneno) {
        mMessageList = displaymessages;
        mContext = context;
        friendPhoneNo = phoneno;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_messages, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called.");

        if (mMessageList.get(position).IS_MSG_FROM_YOU) {
            holder.mMessageYou.setText(mMessageList.get(position).message);
            holder.mMessageYou.setVisibility(View.VISIBLE);
            holder.friendprofilepic.setVisibility(View.INVISIBLE);
            holder.mMessageFriend.setVisibility(View.INVISIBLE);
        }else {
            SetProfilePic(friendPhoneNo,holder);
            holder.friendprofilepic.setVisibility(View.VISIBLE);
            holder.mMessageFriend.setText(mMessageList.get(position).message);
            holder.mMessageFriend.setVisibility(View.VISIBLE);
            holder.mMessageYou.setVisibility(View.INVISIBLE);
        }
        Log.d(TAG, "onBindViewHolder: ended.");
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mMessageFriend,mMessageYou;
        CircleImageView friendprofilepic;
        RelativeLayout parentLayout;   //layout of each induvidual units of the list

        public ViewHolder(View itemView) {
            super(itemView);
            mMessageFriend = itemView.findViewById(R.id.disp_msg_friend);
            mMessageYou = itemView.findViewById(R.id.disp_msg_you);
            friendprofilepic = itemView.findViewById(R.id.friend_profile_pic);
            parentLayout = itemView.findViewById(R.id.layout_list_messages);
        }
    }

    public void SetProfilePic(String phoneNo, ViewHolder holder) {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference = mDatabase.getReference().child("User Data").child(phoneNo);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Profile Pic").exists())
                { Glide.with(mContext).asBitmap()
                        .load(dataSnapshot.child("Profile Pic").getValue().toString()).into(holder.friendprofilepic); }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"error:" + databaseError);
            }
        });
    }

}




