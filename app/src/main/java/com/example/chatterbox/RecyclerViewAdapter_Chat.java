package com.example.chatterbox;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerViewAdapter_Chat extends RecyclerView.Adapter<RecyclerViewAdapter_Chat.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterChat";

    private ArrayList<Messages> mMessageList = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_Chat(Context context, ArrayList<Messages> displaymessages) {
        mMessageList = displaymessages;
        mContext = context;
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
            holder.fromYou.setText("You");
            holder.mMessageYou.setText(mMessageList.get(position).message);
            holder.fromFriend.setText(null);
            holder.mMessageFriend.setText(null);
        }else {
            holder.fromFriend.setText("Friend");
            holder.mMessageFriend.setText(mMessageList.get(position).message);
            holder.fromYou.setText(null);
            holder.mMessageYou.setText(null);
        }
        Log.d(TAG, "onBindViewHolder: ended.");
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mMessageFriend,fromFriend,mMessageYou,fromYou;
        RelativeLayout parentLayout;   //layout of each induvidual units of the list

        public ViewHolder(View itemView) {
            super(itemView);
            fromFriend = itemView.findViewById(R.id.fromfriend);
            mMessageFriend = itemView.findViewById(R.id.disp_msg_friend);
            fromYou = itemView.findViewById(R.id.fromyou);
            mMessageYou = itemView.findViewById(R.id.disp_msg_you);
            parentLayout = itemView.findViewById(R.id.layout_list_messages);
        }
    }
}




