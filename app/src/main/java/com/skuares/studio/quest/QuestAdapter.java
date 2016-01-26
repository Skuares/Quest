package com.skuares.studio.quest;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.List;

/**
 * Created by salim on 12/21/2015.
 */
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewH> {


    public static class ViewH extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView questImage;
        TextView questTitle;
        //ImageView questUserImage;
        TextView questDescription;
        //TextView questUsername;
        //TextView questCost;
        ImageButton likeButton;
        ImageButton takeButton;

        // COME BACK FOR BUTTONS LATERZZZ


        public ViewH(View view) {
            super(view);
            cardView = (CardView)view.findViewById(R.id.questCard);
            questImage = (ImageView)view.findViewById(R.id.questImage);
            questTitle = (TextView)view.findViewById(R.id.questTitle);
            //questUserImage = (ImageView)view.findViewById(R.id.questUserImage);
            questDescription = (TextView)view.findViewById(R.id.questDescription);
            //questUsername = (TextView)view.findViewById(R.id.questUserName);
            //questCost = (TextView)view.findViewById(R.id.questCost);
            likeButton = (ImageButton)view.findViewById(R.id.likeButton);
            takeButton = (ImageButton)view.findViewById(R.id.takeButton);

        }
    }


    private List<QuestCard> list;
    private LoadImageFromString loadImageFromString;
    private LoadImageFromString loadImageFromString2;
    String stringImage;
    String userImage;
    Context context;
    public QuestAdapter(Context context,List<QuestCard> questCards, LoadImageFromString loadImageFromString,LoadImageFromString loadImageFromString2){

        this.list = questCards;
        this.loadImageFromString = loadImageFromString;
        this.loadImageFromString2 = loadImageFromString2;
        this.context = context;
    }



    @Override
    public ViewH onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_card_layout,parent,false);
        ViewH h = new ViewH(v);
        return h;
    }

    @Override
    public void onBindViewHolder(final ViewH holder, final int position) {

        if(list == null){
            return;
        }

        /*
        TRY CHANGING THE COLOR OF THE LIKE ICON
         */
        if(list.get(position).getUsersWhoLiked() == null){

            // do nothing, no body likes it

        }else if (list.get(position).getUsersWhoLiked().get(MainActivity.uid) == null){

            // do nothing, this user has not liked the quest

        }else{

            // already liked, change the color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_hand,context.getTheme());
                holder.likeButton.setImageDrawable(drawable);;
            } else {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_hand);
                holder.likeButton.setImageDrawable(drawable);
            }

        }

        /*
        TRY CHANGING THE COLOR OF TAKE
         */
        // do some checking
        if(MainActivity.uid.equals(list.get(position).getAuthorId())){

            // author case, do not allow him to take it.
            // already liked, change the color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake,context.getTheme());
                holder.takeButton.setImageDrawable(drawable);;
            } else {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake);
                holder.takeButton.setImageDrawable(drawable);
            }

        }
        else if(list.get(position).getTakers() == null){

            // user can take it, do nothing

        }else if (list.get(position).getTakers().get(MainActivity.uid) == null){
            // user can take it, do nothing

        }else{

            // already took it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake,context.getTheme());
                holder.takeButton.setImageDrawable(drawable);;
            } else {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake);
                holder.takeButton.setImageDrawable(drawable);
            }

        }




        stringImage = list.get(position).getQuestImage();
        // pass it to load
        loadImageFromString.loadBitmapFromString(stringImage, holder.questImage);

        // change opacity
        //holder.questImage.setImageAlpha(225);

        holder.questImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when clicked I wanna view the todos list
                // let's try intent
                // WE NEED TO PASS THE WHOLE QUEST
                /*
                STRATEGY FOR PICTURES
                EACH QUEST SHOULD HAVE A FOLDER (ENTRY) OF IMAGES
                AND WE STORE THE KEY OF THIS FOLDER IN QUEST SO WE CAN GET THEM WHEN WE WANT
                 */
                Intent intent = new Intent(context, QuestOpenedActivity.class);
                // WE NO LONGER NEED THIS WE CAN GET TODOS FROM THE QUEST ITSELF .. TO BE CONTINUED
                intent.putExtra("todos", new DataWrapperTodo(list.get(position).getTodos()));
                // let's pass the quest to the activity
                intent.putExtra("quest", list.get(position));
                // pass the quest key

                context.startActivity(intent);
                //Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
            }
        });



        holder.questTitle.setText(list.get(position).getQuestTitle());

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DO SOME CHECKS
                // get the map containing usersWhoLiked
                if (list.get(position).getUsersWhoLiked() == null) {

                    // it means he has not clicked before, and no one has clicked before .. Split to avoid null exception
                    // call like method
                    likeMethod(MainActivity.uid, list.get(position).getQuestKey(), list.get(position));
                    // change the icon
                    // already liked, change the color
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_hand, context.getTheme());
                        holder.likeButton.setImageDrawable(drawable);
                        ;
                    } else {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_hand);
                        holder.likeButton.setImageDrawable(drawable);
                    }

                } else if (list.get(position).getUsersWhoLiked().get(MainActivity.uid) == null) {

                    // call like method
                    likeMethod(MainActivity.uid, list.get(position).getQuestKey(), list.get(position));
                    // change the icon
                    // already liked, change the color
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_hand, context.getTheme());
                        holder.likeButton.setImageDrawable(drawable);
                        ;
                    } else {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_hand);
                        holder.likeButton.setImageDrawable(drawable);
                    }

                } else {

                    // spit out a toast telling the user that is already clicked
                    Toast.makeText(context, "Already liked it", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // do some checking
                if(MainActivity.uid.equals(list.get(position).getAuthorId())){

                    // it is yours, you cant take it
                    Toast.makeText(context, "It is yours", Toast.LENGTH_SHORT).show();
                }
                else if(list.get(position).getTakers() == null){

                    // first time
                    takeMethod(MainActivity.uid, list.get(position).getQuestKey(), list.get(position));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake,context.getTheme());
                        holder.takeButton.setImageDrawable(drawable);;
                    } else {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake);
                        holder.takeButton.setImageDrawable(drawable);
                    }

                }else if (list.get(position).getTakers().get(MainActivity.uid) == null){
                    // first time for this user
                    takeMethod(MainActivity.uid, list.get(position).getQuestKey(), list.get(position));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake,context.getTheme());
                        holder.takeButton.setImageDrawable(drawable);;
                    } else {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_handtake);
                        holder.takeButton.setImageDrawable(drawable);
                    }

                }else{
                     /*
                    BETTER APPROACH CHANGE THE COLOR OF THE ICON
                     */
                    // spit out a toast telling the user that is already clicked
                    Toast.makeText(context, "Already Took it", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*
        // get them from users .. WE GOT THEM
        userImage = list.get(position).getQuestUserImage();
        loadImageFromString2.loadBitmapFromString(userImage, holder.questUserImage);
        // set listener to view the profile
        holder.questUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent to view profile along with the authorid
                // check if the user clicks on his image
                // then take him to his profile
                if(list.get(position).getAuthorId().equals(MainActivity.uid)){
                    // take him to his profile
                    Intent userProfileIntent = new Intent(context,UserProfile.class);
                    context.startActivity(userProfileIntent);
                }else{
                    Intent viewProfile = new Intent(context,ViewProfile.class);
                    viewProfile.putExtra("authorId",list.get(position).getAuthorId());
                    context.startActivity(viewProfile);
                }

            }
        });
        */




        // check if this user has this quest
        /*
        if(MainActivity.myUser.getUsername() != null){
            if(MainActivity.myUser.getUsername().equals(list.get(position).getQuestUsername())){
                holder.questUsername.setText("By you");
            }else{
                holder.questUsername.setText("By " + list.get(position).getQuestUsername());
            }
        }else{
            holder.questUsername.setText("By " + list.get(position).getQuestUsername());
        }
        */

        /*
        holder.questUsername.setText("By " + list.get(position).getQuestUsername());

        // set listener to view the profile
        holder.questUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent to view profile along with the authorid

                // check if the user clicks on his image
                // then take him to his profile
                if(list.get(position).getAuthorId().equals(MainActivity.uid)){
                    // take him to his profile
                    Intent userProfileIntent = new Intent(context,UserProfile.class);
                    context.startActivity(userProfileIntent);
                }else{
                    Intent viewProfile = new Intent(context,ViewProfile.class);
                    viewProfile.putExtra("authorId",list.get(position).getAuthorId());
                    context.startActivity(viewProfile);
                }


            }
        });
        */

        holder.questDescription.setText(list.get(position).getQuestDescription());

       // holder.questCost.setText(list.get(position).getQuestCost());


    }

    public void takeMethod(final String userId, final String questKey, final QuestCard questCard){
        final Firebase takeRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questKey+"/numberOfTakers");
        final Firebase questRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questKey);

        takeRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1.0);

                } else {

                    currentData.setValue((Double) currentData.getValue() + 1.0);

                }

                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
                questCard.increaseTakers();

                questCard.addTaker(questRef, userId);

                // insert this user into participants for every todos we have
                /*
                HOW TO DO IT
                1- loop through the todos we have and call addParticipant on each one
                 */
                for(int i = 0; i < questCard.getTodos().size(); i++){
                    ToDo toDo = questCard.getTodos().get(i);
                    toDo.addParticipant(MainActivity.uid,questRef,String.valueOf(i));
                }



            }
        });

    }

    public void likeMethod(final String userId, String questKey, final QuestCard questCard){
        //increment the number of likes
        //insert the author id in a map called usersWhoLiked
        final Firebase like = new Firebase("https://quest1.firebaseio.com/Quests/"+questKey+"/numberOfLikes");

        // use this to update the data with a map called usersWhoLiked
        final Firebase questRef = new Firebase("https://quest1.firebaseio.com/Quests/"+questKey);

        like.runTransaction(new Transaction.Handler() {
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
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {

                // increase the likes
                questCard.increaseLikes();
                // update map that holds users who liked
                questCard.addUserLike(questRef,userId);



            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null){

            return 0;

        }else{
            return list.size();
        }

    }


}
