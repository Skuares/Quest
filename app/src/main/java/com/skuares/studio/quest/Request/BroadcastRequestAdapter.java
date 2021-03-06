package com.skuares.studio.quest.Request;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.skuares.studio.quest.LoadImageFromString;
import com.skuares.studio.quest.MainActivity;
import com.skuares.studio.quest.Quest;
import com.skuares.studio.quest.QuestCard;
import com.skuares.studio.quest.R;
import com.skuares.studio.quest.ToDo;
import com.skuares.studio.quest.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 1/14/2016.
 */
public class BroadcastRequestAdapter extends RecyclerView.Adapter<BroadcastRequestAdapter.ViewBroadcast> {


    // define the states
    private int noReactionState = 0;
    private int comingState = 1;
    private int maybeState = 2;
    private int noState = 3;

    private Firebase questRef;

    Context context;
    List<User> users;
    List<QuestCard> questCards;
    List<String> usersIds;
    List<String> questsIds;
    List<String> broadcastIds;

    LoadImageFromString loadImageFromString;


    public BroadcastRequestAdapter(Context context,List<User> users, List<QuestCard> questCards, List<String> usersIds,
                                   List<String> questsIds,List<String> broadcastIds ,LoadImageFromString loadImageFromString){

        this.context = context;
        this.users = users;
        this.questCards = questCards;
        this.usersIds = usersIds;
        this.questsIds = questsIds;
        this.broadcastIds = broadcastIds;
        this.loadImageFromString = loadImageFromString;
    }


    @Override
    public ViewBroadcast onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.broadcast_request,parent,false);

        ViewBroadcast viewBroadcast = new ViewBroadcast(view);

        return viewBroadcast;
    }

    @Override
    public void onBindViewHolder(final ViewBroadcast holder, final int position) {

        if(questCards == null || users == null){
            return;
        }else{
            if(users.size() > 0 && questCards.size() > 0){// prevent outofbound exception , happends when user fastly opens the activity
                holder.senderNameB.setText(users.get(position).getUsername());
                loadImageFromString.loadBitmapFromString(users.get(position).getUserImage(), holder.senderImageB);

                // set up the listners for buttons
                holder.comingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // change the state in the user who presses in his data (invites)
                        if (MainActivity.myUser != null) {
                            MainActivity.myUser.updateInviteState(broadcastIds.get(position),comingState,MainActivity.userRef);
                        }

                        // change the state under the quest itself
                        // get the quest ref
                        questRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questsIds.get(position)+"/joiners");
                        // set up the map
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put(MainActivity.uid,comingState);
                        questRef.updateChildren(map);

                        // update the numberOfFollowers
                        Firebase follower = new Firebase("https://quest1.firebaseio.com/Quests/"+questsIds.get(position)+"/numberOfFollowers");
                        follower.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                if(currentData.getValue() == null) {
                                    currentData.setValue(1.0);

                                }   else  {

                                    currentData.setValue((Double) currentData.getValue() + 1.0);

                                }

                                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                        // insert this user into participants of every todos
                         /*
                HOW TO DO IT
                1- loop through the todos we have and call addParticipant on each one
                 */
                        for(int i = 0; i < questCards.get(position).getTodos().size(); i++){
                            ToDo toDo = questCards.get(position).getTodos().get(i);
                            toDo.addParticipant(MainActivity.uid,questRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questsIds.get(position)),
                                    String.valueOf(i));
                        }

                        // remove the buttons and tell the user that he is a joiner of this quest
                        holder.comingButton.setVisibility(View.GONE);
                        holder.maybeButton.setVisibility(View.GONE);
                        holder.noButton.setVisibility(View.GONE);
                        holder.senderNameB.setVisibility(View.GONE);
                        holder.requestDescriptionB.setText("You are now a joiner of this quest");

                    }
                });


                holder.maybeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // change the state in the user who presses in his data (invites)
                        // change the state under the quest itself
                        // remove the buttons
                        // change the state in the user who presses in his data (invites)
                        if (MainActivity.myUser != null) {
                            MainActivity.myUser.updateInviteState(broadcastIds.get(position),maybeState,MainActivity.userRef);
                        }

                        // change the state under the quest itself
                        // get the quest ref
                        questRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questsIds.get(position)+"/joiners");
                        // set up the map
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put(MainActivity.uid,maybeState);
                        questRef.updateChildren(map);

                        // remove the buttons and tell the user that he is a joiner of this quest
                        holder.comingButton.setVisibility(View.GONE);
                        holder.maybeButton.setVisibility(View.GONE);
                        holder.noButton.setVisibility(View.GONE);
                        holder.senderNameB.setVisibility(View.GONE);
                        holder.requestDescriptionB.setText("We hope you make it");
                    }
                });

                holder.noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // change the state in the user who presses in his data (invites)
                        // change the state under the quest itself
                        // remove the buttons

                        // change the state in the user who presses in his data (invites)
                        if (MainActivity.myUser != null) {
                            MainActivity.myUser.updateInviteState(broadcastIds.get(position),noState,MainActivity.userRef);
                        }

                        // change the state under the quest itself
                        // get the quest ref
                        questRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questsIds.get(position)+"/joiners");
                        // set up the map
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put(MainActivity.uid,noState);
                        questRef.updateChildren(map);
                        // remove the buttons and tell the user that he is a joiner of this quest
                        holder.comingButton.setVisibility(View.GONE);
                        holder.maybeButton.setVisibility(View.GONE);
                        holder.noButton.setVisibility(View.GONE);
                        holder.senderNameB.setVisibility(View.GONE);
                        holder.requestDescriptionB.setText("No problem");
                    }
                });


            }

        }
    }

    @Override
    public int getItemCount() {
        return questCards.size();
    }

    public static class ViewBroadcast extends RecyclerView.ViewHolder{

        CardView cardBroadcast;
        ImageView senderImageB;
        TextView senderNameB;
        Button noButton;
        Button maybeButton;
        Button comingButton;

        TextView requestDescriptionB;

        public ViewBroadcast(View itemView) {
            super(itemView);

            cardBroadcast = (CardView)itemView.findViewById(R.id.cardBroadcast);
            senderImageB = (ImageView)itemView.findViewById(R.id.senderImageB);
            senderNameB = (TextView)itemView.findViewById(R.id.senderNameB);
            noButton = (Button)itemView.findViewById(R.id.noButton);
            maybeButton = (Button)itemView.findViewById(R.id.maybeButton);
            comingButton = (Button)itemView.findViewById(R.id.comingButton);
            requestDescriptionB = (TextView)itemView.findViewById(R.id.requestDescriptionB);

        }
    }



}
