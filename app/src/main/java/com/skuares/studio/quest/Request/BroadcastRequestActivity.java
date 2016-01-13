package com.skuares.studio.quest.Request;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.skuares.studio.quest.LoadImageFromString;
import com.skuares.studio.quest.QuestCard;
import com.skuares.studio.quest.R;
import com.skuares.studio.quest.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salim on 1/12/2016.
 */
public class BroadcastRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;


    private ArrayList<String> questIds;
    private ArrayList<String> usersIds;
    private ArrayList<String> broadcastIds;

    private LoadImageFromString loadImageFromString;

    private List<QuestCard> listOfQuests;
    private List<User> listOfUsers;




    Firebase firebaseUser;
    Firebase firebaseQuest;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_request_activity_layout);

        loadImageFromString = new LoadImageFromString(this);
        // reference toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbarRequestBroadcast);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the list of ids

        // set up the recycler view
        recyclerView = (RecyclerView)findViewById(R.id.recycleviewRequestBroadcast);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        broadcastIds = new ArrayList<String>();
        usersIds = new ArrayList<String>();
        questIds = new ArrayList<String>();
        listOfQuests = new ArrayList<QuestCard>();
        listOfUsers = new ArrayList<User>();
        Bundle bundle =  getIntent().getExtras();

        broadcastIds = (ArrayList)bundle.getSerializable("listOfQuestIds");
        // we need to split into 2 lists one for the quest id and one for the broadcaster id
        for(String i : broadcastIds){
            // split and insert
            String[] parts = i.split("----");
            String part1 = parts[0];
            String part2 = parts[1];

            // do insertion
            questIds.add(part1);
            usersIds.add(part2);
            Log.e("logthis",""+part1);
            Log.e("logthis",""+part2);

        }

        if(usersIds != null && questIds != null){
            // retrieve the data

            //fetchDATA( usersIds, questIds);

            Thread thread1 = new Thread(){

                public void run(){

                    // fetch all the quests
                    for(int i = 0; i < questIds.size(); i++){

                        firebaseQuest = new Firebase("https://quest1.firebaseio.com/Quests/"+questIds.get(i));

                        firebaseQuest.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                QuestCard questCard = dataSnapshot.getValue(QuestCard.class);
                                listOfQuests.add(questCard);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }
                }
            };

            Thread thread2 = new Thread(){

              public void run(){


                  for(int i = 0; i < usersIds.size(); i++){

                      firebaseUser = new Firebase("https://quest1.firebaseio.com/users/" + usersIds.get(i));
                      firebaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              User user = dataSnapshot.getValue(User.class);
                              Log.e("logthis",""+dataSnapshot.getKey());
                              listOfUsers.add(user);
                          }

                          @Override
                          public void onCancelled(FirebaseError firebaseError) {

                          }
                      });
                  }
              }
            };


            thread1.start();
            thread2.start();

            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // continue
            // adapter stuff
            adapter = new BroadcastRequestAdapter(BroadcastRequestActivity.this,listOfUsers,listOfQuests,usersIds,questIds,broadcastIds,loadImageFromString);
            recyclerView.setAdapter(adapter);
        }



    }

    public void fetchDATA(List<String> usersIds, List<String> questIds){




    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);


    }
}
