package com.skuares.studio.quest;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 12/24/2015.
 */
public class QuestOpenedAdapter extends RecyclerView.Adapter<QuestOpenedAdapter.ViewHolder> {

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

        public ViewQuest(View view){
            super(view);
            nLikes = (TextView)view.findViewById(R.id.nLikes);
            nTakers = (TextView)view.findViewById(R.id.nTakers);

            qCard = (CardView) view.findViewById(R.id.qCard);
            qCard.setPreventCornerOverlap(false);
            qCard.setCardElevation(0);
            qCard.setMaxCardElevation(0);
            qImage = (ImageView)view.findViewById(R.id.qImage);
        }
    }

    public static class ViewTodos extends ViewHolder{


        CardView cardView;
        ImageButton addPic;
        ImageButton viewPic;
        TextView todosCost;
        TextView todosDesc;
        CheckBox checkBox;



        public ViewTodos(View view){
            super(view);

            cardView = (CardView)view.findViewById(R.id.todosCard);
            addPic = (ImageButton)view.findViewById(R.id.addPic);
            viewPic = (ImageButton)view.findViewById(R.id.viewPic);
            todosCost = (TextView)view.findViewById(R.id.todosCost);
            todosDesc = (TextView)view.findViewById(R.id.todosDescription);
            checkBox = (CheckBox)view.findViewById(R.id.todosCheckbox);



        }

    }


    Context context;
    List<ToDo> list;
    QuestCard questCard;
    LoadImageFromString loadImageFromString;
    private int[] mDataSetTypes;

    public QuestOpenedAdapter(Context context, List<ToDo> toDos, QuestCard questCard, LoadImageFromString loadImageFromString,int[] mDataSetTypes){
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
            Log.e("hello", "return1");
            return new ViewQuest(v);

        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_todo_card,parent,false);
            Log.e("hello", "return111");
            return new ViewTodos(v);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // assign the values
        if(holder.getItemViewType() == QUEST){
            ViewQuest viewQuest = (ViewQuest) holder;
            loadImageFromString.loadBitmapFromString(questCard.getQuestImage(), viewQuest.qImage);

            viewQuest.nTakers.setText("" + (int) questCard.getNumberOfTakers() + " Takers");
            viewQuest.nLikes.setText(""+(int) questCard.getNumberOfLikes()+" Likes");

            likeTextView = viewQuest.nLikes;
            takeTextView = viewQuest.nTakers;
        }else{

            ViewTodos viewTodos = (ViewTodos) holder;
            String cost = ""+list.get(position-1).getMoney()+"RM"+"/"+list.get(position-1).getTime()+"H";
            viewTodos.todosDesc.setText(list.get(position-1).getDesc());
            viewTodos.todosCost.setText(cost);
        }

    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

}
