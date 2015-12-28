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
import com.skuares.studio.quest.R;
import com.skuares.studio.quest.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salim on 12/27/2015.
 */
public class RequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;

    private LoadImageFromString loadImageFromString;
    private List<User> friendsRequestUsers;
    private ArrayList usersIds;

    Firebase firebase;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity_layout);

        loadImageFromString = new LoadImageFromString(this);
        // reference toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbarRequest);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the list of ids

        // set up the recycler view
        recyclerView = (RecyclerView)findViewById(R.id.recycleviewRequest);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        usersIds = new ArrayList<String>();
        friendsRequestUsers = new ArrayList<User>();
        Bundle bundle =  getIntent().getExtras();

        usersIds = (ArrayList)bundle.getSerializable("listOfIds");
        if(usersIds != null){
            // use this list to get the users info
            for (int i = 0; i < usersIds.size(); i++) {

                firebase = new Firebase("https://quest1.firebaseio.com/users/" + usersIds.get(i));
                // attach listener for one time

                final int finalI = i;
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // cast it to User and store it
                        User user = dataSnapshot.getValue(User.class);
                        friendsRequestUsers.add(user);

                        // check to call adapter
                        if((finalI+1) == usersIds.size()){
                            // call adapter
                            adapter = new FriendRequestAdapter(RequestActivity.this,friendsRequestUsers,loadImageFromString,usersIds);
                            recyclerView.setAdapter(adapter);
                        }


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            }
        }else{
            Log.e("request","null");
        }

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
