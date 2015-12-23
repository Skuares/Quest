package com.fakhouri.salim.quest;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;

import java.util.List;

/**
 * Created by salim on 12/21/2015.
 */
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewH> {


    public static class ViewH extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView questImage;
        TextView questTitle;
        ImageView questUserImage;
        TextView questDescription;
        TextView questUsername;
        TextView questCost;

        // COME BACK FOR BUTTONS LATERZZZ

        public ViewH(View view) {
            super(view);
            cardView = (CardView)view.findViewById(R.id.questCard);
            questImage = (ImageView)view.findViewById(R.id.questImage);
            questTitle = (TextView)view.findViewById(R.id.questTitle);
            questUserImage = (ImageView)view.findViewById(R.id.questUserImage);
            questDescription = (TextView)view.findViewById(R.id.questDescription);
            questUsername = (TextView)view.findViewById(R.id.questUserName);
            questCost = (TextView)view.findViewById(R.id.questCost);


        }
    }


    private List<QuestCard> list;
    private LoadImageFromString loadImageFromString;
    private LoadImageFromString loadImageFromString2;
    String stringImage;
    String userImage;
    public QuestAdapter(List<QuestCard> questCards, LoadImageFromString loadImageFromString,LoadImageFromString loadImageFromString2){

        this.list = questCards;
        this.loadImageFromString = loadImageFromString;
        this.loadImageFromString2 = loadImageFromString2;
    }



    @Override
    public ViewH onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_card_layout,parent,false);
        ViewH h = new ViewH(v);
        return h;
    }

    @Override
    public void onBindViewHolder(ViewH holder, int position) {

        if(list == null){
            return;
        }

        stringImage = list.get(position).getQuestImage();
        // pass it to load
        loadImageFromString.loadBitmapFromString(stringImage, holder.questImage);

        // ABOVE IS MORE EFFICIENT holder.questImage.setImageBitmap(list.get(position).stringToBitmap(list.get(position).getQuestImage()));

        holder.questTitle.setText(list.get(position).getQuestTitle());

        // get them from users .. WE GOT THEM
        userImage = list.get(position).getQuestUserImage();

        loadImageFromString2.loadBitmapFromString(userImage,holder.questUserImage);

        //holder.questUserImage.setImageBitmap(list.get(position).stringToBitmap(list.get(position).getQuestUserImage()));
        holder.questUsername.setText("By "+list.get(position).getQuestUsername());

        holder.questDescription.setText(list.get(position).getQuestDescription());

        holder.questCost.setText(list.get(position).getQuestCost());


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
