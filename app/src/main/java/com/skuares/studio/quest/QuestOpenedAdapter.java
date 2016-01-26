package com.skuares.studio.quest;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 12/24/2015.
 */
public class QuestOpenedAdapter extends RecyclerView.Adapter<QuestOpenedAdapter.ViewHolder> {

    private static int REQUEST_CODE= 100;

    public static final int QUEST = 0;
    public static final int TODO = 1;

    // global reference to textview of likes in order to change it from like method
    public TextView likeTextView;
    public TextView takeTextView;


    // similar to like
    public void take(final String author, final String questKey){
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
                takeTextView.setText("" + (int) questCard.getNumberOfTakers() + " Takers");
                questCard.addTaker(questRef, author);

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


    public void like(final String author, final String questKey) {

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
                //This method will be called once with the results of the transaction.
                Log.e("myvalies","null us here"+currentData.getValue());
                // update the number now of likes with this data
                //like.setValue(currentData.getValue());
                // update the numberOfLikes in questCard object
                questCard.increaseLikes();
                Log.e("likes",""+questCard.getNumberOfLikes());
                // update the values on the UI
                likeTextView.setText("" + (int) questCard.getNumberOfLikes() + " Likes");

                // insert the user into map called usersWhoLiked
                // get the usersWhoLiked map from the quest
                // if it is null, this is the first like then initialize it and call addUserLike

                // no need for above just call the method
                questCard.addUserLike(questRef,author);

            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }




    public static class ViewQuest extends ViewHolder{
        CardView qCard;
        ImageView qImage;

        TextView nLikes;
        TextView nTakers;

        TextView nFollowers;

        RecyclerView rvQPictures;

        public ViewQuest(View view){
            super(view);
            nLikes = (TextView)view.findViewById(R.id.nLikes);
            nTakers = (TextView)view.findViewById(R.id.nTakers);

            qCard = (CardView) view.findViewById(R.id.qCard);
            qCard.setPreventCornerOverlap(false);
            qCard.setCardElevation(0);
            qCard.setMaxCardElevation(0);
            qImage = (ImageView)view.findViewById(R.id.qImage);

            nFollowers = (TextView)view.findViewById(R.id.nFollowers);

            rvQPictures = (RecyclerView) view.findViewById(R.id.rvHoriImages);
        }
    }

    public static class ViewTodos extends ViewHolder{


        CardView cardView;
        ImageButton addPic;
        ImageButton done;
        TextView todosCost;
        TextView todosDesc;
        TextView todosPlace;

        MagicProgressCircle demoMpc;
        AnimTextView demoTv;

        public ViewTodos(View view){
            super(view);

            cardView = (CardView)view.findViewById(R.id.todosCard);

            addPic = (ImageButton)view.findViewById(R.id.addImage);
            done = (ImageButton)view.findViewById(R.id.done);

            todosCost = (TextView)view.findViewById(R.id.todosCost);
            todosDesc = (TextView)view.findViewById(R.id.todosDescription);


            todosPlace = (TextView)view.findViewById(R.id.todosPlace);


            demoMpc = (MagicProgressCircle) view.findViewById(R.id.demo_mpc);
            demoTv = (AnimTextView) view.findViewById(R.id.demo_tv);

        }

    }


    Context context;
    List<ToDo> list;
    QuestCard questCard;
    LoadImageFromString loadImageFromString;
    LoadImageFromPath loadImageFromPath;
    private int[] mDataSetTypes;

    public QuestOpenedAdapter(Context context, List<ToDo> toDos, QuestCard questCard, LoadImageFromString loadImageFromString,int[] mDataSetTypes){

        loadImageFromPath = new LoadImageFromPath(context);
        this.context = context;
        this.list = toDos;
        this.questCard = questCard;
        this.loadImageFromString = loadImageFromString;
        this.mDataSetTypes = mDataSetTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == QUEST){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_quest_card,parent,false);

            return new ViewQuest(v);

        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_todo_card,parent,false);

            return new ViewTodos(v);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // assign the values
        if(holder.getItemViewType() == QUEST){
            final ViewQuest viewQuest = (ViewQuest) holder;
            loadImageFromString.loadBitmapFromString(questCard.getQuestImage(), viewQuest.qImage);

            viewQuest.nTakers.setText("" + (int) questCard.getNumberOfTakers() + " Takers");
            viewQuest.nLikes.setText("" + (int) questCard.getNumberOfLikes() + " Likes");
            viewQuest.nFollowers.setText(""+(int) questCard.getNumberOfFollowers()+" Joiners");

            likeTextView = viewQuest.nLikes;
            takeTextView = viewQuest.nTakers;


            /*
            Recycler view images
             */
            viewQuest.rvQPictures.setHasFixedSize(true);
            viewQuest.rvQPictures
                    .setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            // retrieve the images of this quest from firebase and pass them to adapter
            // attach listener to fetch the images
            Firebase questPicturesRef = new Firebase("https://quest1.firebaseio.com/Pictures");
            //questPicturesRef.orderByKey().equalTo(questCard.getQuestKey());
            questPicturesRef.orderByChild(questCard.getQuestKey());
            questPicturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                QPicture qPicture;
                List<QPicture> qPictures = new ArrayList<QPicture>();
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            qPicture = postSnapshot.child(questCard.getQuestKey()).getValue(QPicture.class);
                            // do some checking
                            if(qPicture != null){
                                qPictures.add(qPicture);
                            }


                        }

                        // another check
                        // check if the list has items in it otherwise hide the rv
                        if(qPictures.size() > 0){
                            // reverse the order
                            Collections.reverse(qPictures);
                            RVQPictureAdapter rvqPictureAdapter = new RVQPictureAdapter(context,qPictures,loadImageFromString);
                            viewQuest.rvQPictures.setAdapter(rvqPictureAdapter);
                        }else{
                            //viewQuest.rvQPictures.setVisibility(View.GONE);
                        }





                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            //RVQPictureAdapter rvqPictureAdapter = new RVQPictureAdapter(context,null,loadImageFromString);
            //viewQuest.rvQPictures.setAdapter(rvqPictureAdapter);
            // if data is null set it to gone
            //viewQuest.rvQPictures.setVisibility(View.GONE);


        }else{

            final ViewTodos viewTodos = (ViewTodos) holder;
            final String cost = ""+list.get(position-1).getMoney()+"RM"+"/"+list.get(position-1).getTime()+"H";
            viewTodos.todosDesc.setText(list.get(position - 1).getDesc());
            viewTodos.todosCost.setText(cost);
            // get the place info
            viewTodos.todosPlace.setText(list.get(position-1).getaPlace().getPlaceAddress());

            viewTodos.addPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "HI", Toast.LENGTH_SHORT).show();
                    // intent to get a picture from user
                    // change picture
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), REQUEST_CODE);


                }
            });
            viewTodos.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check if this user exists in the participants
                    if (list.get(position - 1).getParticipants().containsKey(MainActivity.uid)) {
                        // I have the user
                        // check if this user has already clicked
                        if ((Boolean) list.get(position - 1).getParticipants().get(MainActivity.uid)) {
                            // user has already finished the quest
                            v.setEnabled(false);

                        } else {
                            v.setEnabled(true);
                            // user can click to finish
                            /*
                            WHAT WOULD HAPPEN WHEN HE CLICKS
                            1- CHANGE THE VALUE TO TRUE AND UPDATE FIREBASE
                            2- INCREASE NUMBER OF USERSFINISHEDTHISTODO
                             */
                            // 1
                            list.get(position - 1).updateParticipant(MainActivity.uid,
                                    new Firebase("https://quest1.firebaseio.com/Quests/" + questCard.getQuestKey()),
                                    String.valueOf(position - 1));
                            // 2
                            list.get(position - 1).increaseUsersFinishedThisTodo();

                            Firebase usersFinishedThisTodoRef = new Firebase("https://quest1.firebaseio.com/Quests/" + questCard.getQuestKey());
                            usersFinishedThisTodoRef.child("todos").child(String.valueOf(position - 1)).child("usersFinishedThisTodo").runTransaction(new Transaction.Handler() {
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
                                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                                    // update the ui
                                    int total = list.get(position - 1).getParticipants().size();
                                    int usersFinishedNumber = (int) list.get(position - 1).getUsersFinishedThisTodo();
                                    anim(viewTodos.demoMpc, viewTodos.demoTv, total, usersFinishedNumber);
                                }
                            });
                        }

                    } else {
                        v.setEnabled(false);
                        Toast.makeText(context, "You are not allowed to finsih this quest", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            // progress
            // we need to pass the magicprogress , anim view, and percentage
            // calculate percentage based on number of participants
            int total = list.get(position-1).getParticipants().size();
            int usersFinishedNumber = (int)list.get(position-1).getUsersFinishedThisTodo();
            anim(viewTodos.demoMpc, viewTodos.demoTv, total, usersFinishedNumber);

        }

    }
    private void anim(MagicProgressCircle demoMpc, AnimTextView demoTv, int total, int usersFinishedNumber) {

        int result = (usersFinishedNumber * 100)/total;

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(demoMpc, "percent", 0, result/100f),
                ObjectAnimator.ofInt(demoTv, "score", 0, result)

        );
        set.setDuration(600);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();

    }


    @Override
    public int getItemCount() {
        return list.size()+1;
    }





}
