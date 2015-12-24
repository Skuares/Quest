package com.fakhouri.salim.quest;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
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
    public void onBindViewHolder(ViewH holder, final int position) {

        if(list == null){
            return;
        }

        stringImage = list.get(position).getQuestImage();
        // pass it to load
        loadImageFromString.loadBitmapFromString(stringImage, holder.questImage);

        holder.questImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when clicked I wanna view the todos list
                // let's try intent
                Intent intent = new Intent(context,TodosActivity.class);
                intent.putExtra("todos",new DataWrapperTodo(list.get(position).getTodos()));
                context.startActivity(intent);
                //Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
            }
        });
        // ABOVE IS MORE EFFICIENT holder.questImage.setImageBitmap(list.get(position).stringToBitmap(list.get(position).getQuestImage()));

        holder.questTitle.setText(list.get(position).getQuestTitle());

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


        //holder.questUserImage.setImageBitmap(list.get(position).stringToBitmap(list.get(position).getQuestUserImage()));
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
