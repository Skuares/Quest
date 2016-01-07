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

import java.util.List;

/**
 * Created by salim on 12/24/2015.
 */
public class QuestOpenedAdapter extends RecyclerView.Adapter<QuestOpenedAdapter.ViewHolder> {

    public static final int QUEST = 0;
    public static final int TODO = 1;

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

            viewQuest.nTakers.setText(""+(int) questCard.getNumberOfTakers()+" Takers");
            viewQuest.nLikes.setText(""+(int) questCard.getNumberOfLikes()+" Likes");
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
