package com.skuares.studio.quest.Request;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.skuares.studio.quest.R;

import org.w3c.dom.Text;

/**
 * Created by salim on 12/27/2015.
 */
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewFriendRequest> {



    public static class ViewFriendRequest extends RecyclerView.ViewHolder{

        CardView cardFriendRequest;
        ImageView senderImage;
        TextView senderName;
        ImageButton accept;
        ImageButton ignore;

        public ViewFriendRequest(View itemView) {
            super(itemView);

            cardFriendRequest = (CardView)itemView.findViewById(R.id.cardRequestFriend);
            senderImage = (ImageView)itemView.findViewById(R.id.senderImage);
            senderName = (TextView)itemView.findViewById(R.id.senderName);
            accept = (ImageButton)itemView.findViewById(R.id.acceptRequest);
            ignore = (ImageButton)itemView.findViewById(R.id.ignoreRequest);



        }
    }


    public FriendRequestAdapter(){

    }

    @Override
    public ViewFriendRequest onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_friend_request,parent,false);

        ViewFriendRequest viewFriendRequest = new ViewFriendRequest(view);

        return viewFriendRequest;
    }

    @Override
    public void onBindViewHolder(ViewFriendRequest holder, int position) {

        // actions and setters

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
