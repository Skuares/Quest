package com.skuares.studio.quest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by salim on 12/13/2015.
 */
public class StreamFragment extends Fragment {



    /*
    GoogleApiClient Connection
     */


    // identify the caller
    int onResumeCaller = 100;
    int onCreateCaller = 1;



    private LoadImageFromString loadImageFromString;
    private LoadImageFromString loadImageFromString2;

    public static List<QuestCard> questCards = null;
    private User mUser;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadImageFromString = new LoadImageFromString(getContext());
        loadImageFromString2 = new LoadImageFromString(getContext());
        // attach listener
        //Log.e("onchildadded", "ON CREATE IS CALLED");


        // ensure fetch is called only once ,, otherwise the listener is set multiple times;
        if(questCards == null){

            fetchData(onCreateCaller);
        }

        //Log.e("assigned", "I am here on create");
    }

    private void fetchData(int caller){
        questCards = new ArrayList<QuestCard>();


        final Firebase questRef = new Firebase("https://quest1.firebaseio.com/Quests");
        final Firebase[] usersRef = new Firebase[1];


        final User[] user = new User[1];
        final String[] userImage = new String[1];
        final String[] username = new String[1];
        final QuestCard[] adapterQuest = new QuestCard[1];

        final int[] numberOfLoops = {0};

        // we need an array of quests and an incrementer to keep track of the quests
        // otherwise last item is duplicated
        final int[] increment = {0};
        final List<QuestCard> questCardsHolders = new ArrayList<QuestCard>();
        // attach listener for childs approach
        final int[] i = {0};


        // determine the caller
        if(caller == 1){
            questRef.addChildEventListener(new ChildEventListener() {
                QuestCard fireQuest;

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // gets called each time we add a new quest
                    // and gets called for every item


                    Log.e("onchildadded", "I AM CALLED");

                    fireQuest = dataSnapshot.getValue(QuestCard.class);

                    // track
                    questCardsHolders.add(fireQuest);


                    increment[0]++;

                    // get user image and username
                    String author = fireQuest.getAuthorId();
                    usersRef[0] = new Firebase("https://quest1.firebaseio.com/users/" + author);
                    // attach listener
                    usersRef[0].addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user[0] = dataSnapshot.getValue(User.class);
                            userImage[0] = user[0].getUserImage();
                            username[0] = user[0].getUsername();


                            /*
                            to use descending set i[0] = increment and then start subtracting
                            TO BE CONTINUED LATERZZZ
                             */


                            // use the full data constructor
                            adapterQuest[0] = new QuestCard(questCardsHolders.get(i[0]).getQuestImage(), questCardsHolders.get(i[0]).getQuestTitle(), questCardsHolders.get(i[0]).getAuthorId(), username[0], userImage[0], questCardsHolders.get(i[0]).getQuestDescription(), questCardsHolders.get(i[0]).getQuestCost(), questCardsHolders.get(i[0]).getTodos());
                            // increment i so we the next one next time
                            i[0] = i[0] + 1;
                            //Log.e("onchild",String.valueOf(i[0]));
                            // add it to the list

                            questCards.add(adapterQuest[0]);
                            //Log.e("onchild", "I AM ADDED");

                            // ensure it gets called once
                            if (increment[0] == questCards.size()) {
                                // call the adapter
                                adapter = new RecyclerViewMaterialAdapter(new QuestAdapter(getContext(),questCards,loadImageFromString,loadImageFromString2));
                                recyclerView.setAdapter(adapter);
                                MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
                                //Log.e("CheckerAdalter", "adapter is set2");

                            }


                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });



        /*
        questRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // THIS GETS CALLED EVERY TIME WE ADD A QUEST
                Log.e("IAMCALLED","I AM CALLED");
                //Log.e("loops","DATA"+dataSnapshot.getChildrenCount());
                // set the int
                numberOfLoops[0] = (int)dataSnapshot.getChildrenCount();

                //Log.e("assigned","now on data change");
                QuestCard post;
                // flush the list
                questCards.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    post = postSnapshot.getValue(QuestCard.class);

                        /*
                        strategy: use nested listener to obtain user's image and user's username
                        WHY WE DO THIS
                        if user changes his profile picture or changes his username
                        the data in the quest will be consistent
                        otherwise if we save the user's image and user's name along with the quest
                        the data won't be able to be changed then
                         */
            /*
                    String author = post.getAuthorId();
                    usersRef[0] = new Firebase("https://quest1.firebaseio.com/users/"+author);
                    // attach listener to user's ref
                    final QuestCard finalPost = post;
                    usersRef[0].addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user[0] = dataSnapshot.getValue(User.class);
                            userImage[0] = user[0].getUserImage();
                            username[0] = user[0].getUsername();

                            // use the full data constructor
                            adapterQuest[0] = new QuestCard(finalPost.getQuestImage(), finalPost.getQuestTitle(), finalPost.getAuthorId(), username[0], userImage[0], finalPost.getQuestDescription(), finalPost.getQuestCost(), finalPost.getTodos());
                            // add it to the list

                            questCards.add(adapterQuest[0]);

                            // determine last time we loop

                            int i = numberOfLoops[0];
                            if (questCards.size() == i) {
                                Log.e("assigned", "list is ready");
                                adapter = new QuestAdapter(questCards, loadImageFromString, loadImageFromString2);
                                recyclerView.setAdapter(adapter);

                            }



                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }
                //
                //Log.e("assigned", "list is ready");
                //adapter = new QuestAdapter(questCards,loadImageFromString,loadImageFromString2);
                //recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        */
        }else if (caller == 100){
            // fetch data once
            questRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // THIS GETS CALLED once
                    //Log.e("IAMCALLED","I AM CALLED");
                    //Log.e("loops","DATA"+dataSnapshot.getChildrenCount());
                    // set the int
                    numberOfLoops[0] = (int)dataSnapshot.getChildrenCount();

                    //Log.e("assigned","now on data change");
                    QuestCard post;
                    // flush the list
                    questCards.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        post = postSnapshot.getValue(QuestCard.class);

                        /*
                        strategy: use nested listener to obtain user's image and user's username
                        WHY WE DO THIS
                        if user changes his profile picture or changes his username
                        the data in the quest will be consistent
                        otherwise if we save the user's image and user's name along with the quest
                        the data won't be able to be changed then
                         */

                    String author = post.getAuthorId();
                    usersRef[0] = new Firebase("https://quest1.firebaseio.com/users/"+author);
                    // attach listener to user's ref
                    final QuestCard finalPost = post;
                    usersRef[0].addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user[0] = dataSnapshot.getValue(User.class);
                            userImage[0] = user[0].getUserImage();
                            username[0] = user[0].getUsername();

                            // use the full data constructor
                            adapterQuest[0] = new QuestCard(finalPost.getQuestImage(), finalPost.getQuestTitle(), finalPost.getAuthorId(), username[0], userImage[0], finalPost.getQuestDescription(), finalPost.getQuestCost(), finalPost.getTodos());
                            // add it to the list

                            questCards.add(adapterQuest[0]);

                            // determine last time we loop

                            int i = numberOfLoops[0];
                            if (questCards.size() == i) {

                                adapter = new RecyclerViewMaterialAdapter(new QuestAdapter(getContext(),questCards,loadImageFromString,loadImageFromString2));
                                recyclerView.setAdapter(adapter);
                                MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
                                //Log.e("CheckerAdalter", "adapter is set2");



                            }



                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }
                //
                //Log.e("assigned", "list is ready");
                //adapter = new QuestAdapter(questCards,loadImageFromString,loadImageFromString2);
                //recyclerView.setAdapter(adapter);


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stream_layout,container,false);
        //Log.e("assigned","I am here on create view");



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set recyle stuff
        recyclerView = (RecyclerView)view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true); // only one view

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);





        if(questCards != null){
            //adapter = new QuestAdapter(getContext(),questCards,loadImageFromString,loadImageFromString2);
            //recyclerView.setAdapter(adapter);
            //Log.e("assigned", "adapter gets called and finishing on activity");

            // material design library
            adapter = new RecyclerViewMaterialAdapter(new QuestAdapter(getContext(),questCards,loadImageFromString,loadImageFromString2));
            recyclerView.setAdapter(adapter);
            MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
            Log.e("CheckerAdalter", "adapter is set2");

        }
        //MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);
        Log.e("CheckerAdalter", "register");


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton)getActivity().findViewById(R.id.fabStream);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open create quest

                Intent intentQuest = new Intent(getContext(),CreateQuest.class);
                startActivity(intentQuest);
            }
        });




    }



    @Override
    public void onResume() {
        super.onResume();

        if(UserProfile.imageHasChanged){

            fetchData(onResumeCaller);

            UserProfile.imageHasChanged = false;

        }

    }
}
