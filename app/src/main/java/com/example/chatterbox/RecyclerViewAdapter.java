package com.example.chatterbox;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mFriendslist = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> friendslist) {
        mFriendslist = friendslist;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_friends, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called.");
        holder.mPhoneNo.setText(mFriendslist.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on:  " + mFriendslist.get(position));
                Toast.makeText(mContext, mFriendslist.get(position), Toast.LENGTH_SHORT).show();

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

        TextView mPhoneNo;
        RelativeLayout parentLayout;   //layout of each induvidual units of the list

        public ViewHolder(View itemView) {
            super(itemView);
            mPhoneNo = itemView.findViewById(R.id.email_id);
            parentLayout = itemView.findViewById(R.id.layout_list_friends);
        }
    }
}

