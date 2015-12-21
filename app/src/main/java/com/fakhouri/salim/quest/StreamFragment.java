package com.fakhouri.salim.quest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salim on 12/13/2015.
 */
public class StreamFragment extends Fragment {

    private Firebase ref;
    private Firebase questRef;

    public static List<QuestCard> questCards = null;
    private User mUser;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // attach listener

        // ensure this gets called once
        if(questCards == null){
            questCards = new ArrayList<QuestCard>();
            Firebase questRef = new Firebase("https://quest1.firebaseio.com/Quests");
            // attach listener
            questRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    QuestCard post;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        post = postSnapshot.getValue(QuestCard.class);
                        // add it to the list
                        questCards.add(post);
                    }
                    //
                    Log.e("assigned", "list is ready");
                    adapter = new QuestAdapter(questCards);
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });



        }


        Log.e("assigned", "I am here on create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stream_layout,container,false);
        Log.e("assigned","I am here on create view");

        // set recyle stuff
        recyclerView = (RecyclerView)view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true); // only one view

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);



        if(questCards != null){
            adapter = new QuestAdapter(questCards);
            recyclerView.setAdapter(adapter);
            Log.e("assigned", "adapter gets called and finishing on activity");

        }



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fabStream);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open create quest
                Intent intentQuest = new Intent(getContext(),CreateQuest.class);
                startActivity(intentQuest);
            }
        });


    }


}
