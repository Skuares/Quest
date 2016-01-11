package com.skuares.studio.quest;

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
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 1/10/2016.
 */
public class QTookStreamFragment extends Fragment {


    /*
   a map to help us identifying which quest has been changed
    */
    Map<String,QuestCard> map;

    private LoadImageFromString loadImageFromString;
    private LoadImageFromString loadImageFromString2;

    private List<QuestCard> questCards = null;
    private User mUser;

    RecyclerView rvQTookStream;
    RecyclerView.LayoutManager manager;
    RecyclerView.Adapter adapter;

    /*
    FOR NOW USE THE STREAM ADAPTER(QUEST ADAPTER)
    TILL WE DESIGN 2 LAYOUT FOR AUTHOR QUEST AND TAKER QUEST
     */



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        map = new HashMap<String,QuestCard>();

        loadImageFromString = new LoadImageFromString(getContext());
        loadImageFromString2 = new LoadImageFromString(getContext());
        // attach listener
        //Log.e("onchildadded", "ON CREATE IS CALLED");


        // ensure fetch is called only once ,, otherwise the listener is set multiple times;
        if(questCards == null){

            fetchTokenQuest();
        }

    }

    public void fetchTokenQuest(){

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


        // construct the query    questRef.orderByChild("authorId").equalTo(MainActivity.uid);     questRef.orderByChild("takers/"+MainActivity.uid).equalTo(MainActivity.uid);
        Query query = questRef.orderByChild("takers/"+MainActivity.uid).equalTo(MainActivity.uid);
        query.addChildEventListener(new ChildEventListener() {

            QuestCard fireQuest;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                        adapterQuest[0] = new QuestCard(questCardsHolders.get(i[0]).getQuestImage(), questCardsHolders.get(i[0]).getQuestTitle(), questCardsHolders.get(i[0]).getAuthorId(), username[0], userImage[0], questCardsHolders.get(i[0]).getQuestDescription(), questCardsHolders.get(i[0]).getQuestCost(), questCardsHolders.get(i[0]).getTodos(),questCardsHolders.get(i[0]).getQuestKey(),questCardsHolders.get(i[0]).getUsersWhoLiked(),questCardsHolders.get(i[0]).getTakers(),questCardsHolders.get(i[0]).getNumberOfLikes(),questCardsHolders.get(i[0]).getNumberOfTakers(),questCardsHolders.get(i[0]).getJoiners());
                        // increment i so we the next one next time
                        i[0] = i[0] + 1;
                        //Log.e("onchild",String.valueOf(i[0]));

                        // add it to the map
                        map.put(adapterQuest[0].getQuestKey(),adapterQuest[0]);

                        // add it to the list
                        questCards.add(adapterQuest[0]);
                        //Log.e("onchild", "I AM ADDED");

                        // ensure it gets called once
                        if (increment[0] == questCards.size()) {
                            // call the adapter
                            adapter = new RecyclerViewMaterialAdapter(new QuestAdapter(getContext(),questCards,loadImageFromString,loadImageFromString2));
                            rvQTookStream.setAdapter(adapter);
                            MaterialViewPagerHelper.registerRecyclerView(getActivity(), rvQTookStream, null);
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

                // update the data of the quest

                // HOW TO DO IT
                // convert to quest card
                QuestCard updatedQuestCard = dataSnapshot.getValue(QuestCard.class);
                // get the keyy and index it into the map
                String key = updatedQuestCard.getQuestKey();
                Log.e("indexes", "" + key);
                QuestCard oldQuest = map.get(key);
                Log.e("indexes",""+oldQuest.getQuestKey());
                if(oldQuest == null){
                    // not of interest to this user
                }else{
                    // get the index of the oldQuest
                    int index = questCards.indexOf(oldQuest);
                    // use this index to update the quest
                    Log.e("indexes",""+index);
                    Log.e("indexes", "" + questCards.size());
                    if(index >= 0){// because of the firebase issue of calling ondatachanged many times
                        questCards.set(index,updatedQuestCard);
                        // notify the adapter
                        adapter.notifyDataSetChanged();
                    }

                }

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
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.q_took_stream_layout,container,false);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvQTookStream = (RecyclerView)view.findViewById(R.id.rvQTook);
        rvQTookStream.setHasFixedSize(true); // only one view

        manager = new LinearLayoutManager(getContext());
        rvQTookStream.setLayoutManager(manager);

        if(questCards != null){

            // material design library
            adapter = new RecyclerViewMaterialAdapter(new QuestAdapter(getContext(),questCards,loadImageFromString,loadImageFromString2));
            rvQTookStream.setAdapter(adapter);
            MaterialViewPagerHelper.registerRecyclerView(getActivity(), rvQTookStream, null);
        }

    }




}
