package com.skuares.studio.quest.Request;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.skuares.studio.quest.LoadImageFromString;
import com.skuares.studio.quest.MainActivity;
import com.skuares.studio.quest.R;
import com.skuares.studio.quest.User;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        TextView requestDescription;

        public ViewFriendRequest(View itemView) {
            super(itemView);

            cardFriendRequest = (CardView)itemView.findViewById(R.id.cardRequestFriend);
            senderImage = (ImageView)itemView.findViewById(R.id.senderImage);
            senderName = (TextView)itemView.findViewById(R.id.senderName);
            accept = (ImageButton)itemView.findViewById(R.id.acceptRequest);
            ignore = (ImageButton)itemView.findViewById(R.id.ignoreRequest);
            requestDescription = (TextView)itemView.findViewById(R.id.requestDescription);


        }
    }

    int acceptState = 2;
    int requestState = 0;
    int pendingState = 1;

    List<User> users;
    Context context;
    List<String> usersIds;
    private LoadImageFromString loadImageFromString;
    public FriendRequestAdapter(Context context,List<User> users,LoadImageFromString loadImageFromString,List<String> usersIds){
        this.users = users;
        this.context = context;
        this.loadImageFromString = loadImageFromString;
        this.usersIds = usersIds;
    }

    @Override
    public ViewFriendRequest onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_friend_request,parent,false);

        ViewFriendRequest viewFriendRequest = new ViewFriendRequest(view);

        return viewFriendRequest;
    }

    @Override
    public void onBindViewHolder(final ViewFriendRequest holder, final int position) {

        // actions and setters
        if(users == null){
            return;
        }

        holder.senderName.setText(users.get(position).getUsername());
        loadImageFromString.loadBitmapFromString(users.get(position).getUserImage(), holder.senderImage);
        // attach listener on buttons
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change the state to accept
                // in this user (receiver)
                // in the sender

                // change in this user (receiver)
                // get the user from main activity and update the friends hashmap and save
                /*
                fst parameter is the sender id
                snd parameter is the state
                3rd parameter is the receiver reference
                 */
                MainActivity.myUser.addFriend(usersIds.get(position),acceptState,MainActivity.userRef);
                // update the sender
                Firebase firebaseSenderRef = MainActivity.ref.child("users").child(usersIds.get(position));

                /*
                Use the main activity user to access the method
                it does not matter because the method only access the database

                fst parameter is the receiver id
                snd parameter is the state
                third is the firebase reference for the sender
                 */
                MainActivity.myUser.addFriend(MainActivity.uid, acceptState, firebaseSenderRef);

                // change the UI
                holder.ignore.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.senderName.setVisibility(View.GONE);
                holder.requestDescription.setText("You are now a friend with " + users.get(position).getUsername());

                // insert into Parse
                // this user's array of friends
                // the sender array

                // reciver (this user)

                ParseQuery<ParseObject> queryReceiver = ParseQuery.getQuery("Users");
                queryReceiver.whereEqualTo("authorId", MainActivity.uid); // find the sender ,, we should pass the sender id
                queryReceiver.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {

                            ParseObject object = objects.get(0); // only one element. the id is unique
                            object.addUnique("friends",usersIds.get(position));// senderId
                            object.saveInBackground();
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }

                });




                // sender parse stuff
                ParseQuery<ParseObject> querySender = ParseQuery.getQuery("Users");
                querySender.whereEqualTo("authorId", usersIds.get(position)); // find the sender ,, we should pass the sender id
                querySender.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {

                            ParseObject object = objects.get(0); // only one element. the id is unique
                            object.addUnique("friends",MainActivity.uid);
                            object.saveInBackground();
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }

                });




            }
        });

        holder.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the value of the userId to null for receiver and sender
                // in this user (receiver) which means the main activity user
                // and in the sender user

                // make a map with null value and its key should be sender/receiver ids
                // begin with this user
                Map<String,Object> mapReceiver = new HashMap<String, Object>();
                mapReceiver.put(usersIds.get(position), null);

                Map<String,Object> mapSender = new HashMap<String, Object>();
                mapSender.put(MainActivity.uid, null);

                // get references to sender/receiver
                Firebase firebaseSenderRef = MainActivity.ref.child("users").child(usersIds.get(position)).child("friends");
                Firebase firebaseThisUserRef = MainActivity.userRef.child("friends");


                firebaseSenderRef.updateChildren(mapSender);
                firebaseThisUserRef.updateChildren(mapReceiver);

                // change the UI
                holder.ignore.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.senderName.setVisibility(View.GONE);
                holder.requestDescription.setText("You have refused to be a friend with " + users.get(position).getUsername());




            }
        });
    }

    @Override
    public int getItemCount() {

        if(users == null){
            return 0;
        }
        return users.size();

    }
}
