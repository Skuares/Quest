package com.skuares.studio.quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewProfile extends AppCompatActivity {

    private ImageView header;

    private String author;
    private Firebase authorPath;

    private Integer userState = 0;
    private Map<String,Object> userMapFriends;

    private Map<String,Object> receiverMapFriends;
    /*
    Receiver should go into the receive state (0)
    Sender should go into the pending state (1)
    if receiver accepts both go into accepted state (2)
    if receiver ignores goes
        delete the state from the receiver and the sender
     */
    private int request = 0; // receiver state
    private int pending = 1;
    private int accepted = 2;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int mutedColor = R.attr.colorPrimary;

    private LoadImageFromString loadImageFromString;

    private User mUser; // the receiver
    private User currentUser; // the sender (the user that is doing the action)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private com.github.clans.fab.FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile_layout);
        toolbar = (Toolbar)findViewById(R.id.anim_toolbarView);
        setSupportActionBar(toolbar);

        userMapFriends = new HashMap<String, Object>();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadImageFromString = new LoadImageFromString(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarView);
        header = (ImageView) findViewById(R.id.headerView);
        floatingActionButton = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.floatingPictureView);


        currentUser = MainActivity.myUser;
        /*
         1- Receive user id via intent and find him
         2- Apply data to header and name
         3- apply the rest via the recycle
         4- use UserAdapter ,, it just displays data
         */

        // 1- receive the intent
        author = getIntent().getStringExtra("authorId");
        if(author != null){
            // get the user data
            // get the path
            authorPath = new Firebase(getResources().getString(R.string.firebaseUrl)+"users/"+author);
            // attach listener
            authorPath.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // get user
                    mUser = dataSnapshot.getValue(User.class);
                    // get image,username and set them to header and collapse title
                    String strImage = mUser.getUserImage();
                    //Log.e("nulls",""+strImage);
                    loadImageFromString.loadBitmapFromString(strImage, header);

                    // set up pallete
                    PaletteBackground paletteBackground = new PaletteBackground();
                    paletteBackground.execute(strImage);

                    collapsingToolbarLayout.setTitle(mUser.getUsername());
                    // pass the user to adapter

                    recyclerView = (RecyclerView) findViewById(R.id.scrollableviewView);
                    recyclerView.setHasFixedSize(true); // only one view

                    layoutManager = new LinearLayoutManager(ViewProfile.this);
                    recyclerView.setLayoutManager(layoutManager);

                    adapter = new UserAdapter(mUser);
                    recyclerView.setAdapter(adapter);

                    // check the state of this user
                    // get the map of values and index author in it to see of this user that I am checking has a record
                    userMapFriends = MainActivity.myUser.getFriends();
                    Log.e("ViewProfile",String.valueOf(userMapFriends));
                    // check to see if this user in the userMap friends

                    // first check the userMapFriends
                    if(userMapFriends == null){
                        // user has no friends
                        // his data on firebase does not contain friends field

                        // do nothing for now
                        userState = null;
                    }else{
                        // he has friends
                        userState = (Integer) userMapFriends.get(author); // the author id of this particular user
                        if (userState == null) {
                            // this user is not a friend of your
                            // and have not sent him before
                            // do not change the icon
                            Log.e("UserState", String.valueOf(userState));

                        } else if (userState == pending) {
                            Log.e("UserState", String.valueOf(userState));
                            // sent but no acceptance
                            // change the icon
                            floatingActionButton.setImageResource(R.drawable.ic_action_time);


                        } else if (userState == accepted) {
                            // this user is your friend
                            // change icon
                            Log.e("UserState", String.valueOf(userState));
                            floatingActionButton.setImageResource(R.drawable.ic_action_accept);
                        }else if(userState == request){
                            floatingActionButton.setImageResource(R.drawable.ic_action_time);
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }


            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("userStateHello","floating is clicked");
                    /*
                    check the state of the user
                     */
                    if(userState == null){
                        // change the icon
                        if(author != null && mUser != null){
                            Log.e("ViewProfileUserState","checking mUser status");
                            floatingActionButton.setImageResource(R.drawable.ic_action_time);
                            // use parse to send a push request an add friend

                            // Create our Installation query
                            ParseQuery pushQuery = ParseInstallation.getQuery();
                            //pushQuery.whereEqualTo("channels", "Giants"); // Set the channel
                            pushQuery.whereEqualTo("installationAuthorId", author);

                            // Send push notification to query
                            ParsePush push = new ParsePush();
                            push.setQuery(pushQuery);
                            push.setMessage(MainActivity.myUser.getUsername() + " Sent you a friend request");
                            push.sendInBackground();

                            // update the hashmap of this user's friends // author is the friend,,
                            // establish the userMapFriends
                            if(userMapFriends == null){
                                userMapFriends = new HashMap<String, Object>();
                            }
                            userMapFriends.put(author, pending);
                            // save to currentUser firebase
                            currentUser.addFriend(author, pending, MainActivity.userRef);
                            // change user state to 1,, pending state
                            userState = pending;

                            /*
                            what should happen in the receiver side
                            1- receiver's friends should contain this request as 0 (means request state)
                             */

                            // todo this
                            // 1- get the user friends (mUser)
                            // 2- insert the state to be request
                            // * receiverMapFriends might be null. first request possibility
                            receiverMapFriends = mUser.getFriends();
                            if(receiverMapFriends == null){
                                // initialize it
                                receiverMapFriends = new HashMap<String, Object>();

                            }
                            // insert data sender Id(the current user who is doing the action) with request state 0 into the receiver friends
                            receiverMapFriends.put(MainActivity.uid,request);
                            // save to receiver firebase (firebase -> users -> userId ) then from add Friend (-> friends)
                            Firebase receiverRef = MainActivity.ref.child("users").child(author);
                            mUser.addFriend(MainActivity.uid,request,receiverRef);
                        }

                    }else if(userState == pending){
                        // pending state
                        // do not change the icon
                        // let user know that his request has   been sent
                        Snackbar.make(v, "A Request Has Already Been Sent", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else if(userState == accepted){
                        // you are friened with this user
                        // onclick , pop up dialog
                        // ask if you want to unfriend this user
                        // if so
                        // delete this user(author) from the hashmap
                        // and delete this user (current) from author's hashmap

                        new MaterialDialog.Builder(ViewProfile.this)
                                .title("Unfriend")
                                .content("Do you want to unfriend " + mUser.getUsername() + " ?")
                                .positiveText("Unfriend")
                                .negativeText("Cancel")
                                .theme(Theme.DARK)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                        // unfriend the user
                                        // currentUser has the other user id
                                        Map<String, Object> currentUserMap = new HashMap<String, Object>();
                                        currentUserMap.put(author, null);

                                        Map<String, Object> unfriendedMap = new HashMap<String, Object>();
                                        unfriendedMap.put(MainActivity.uid, null);

                                        // get references to sender/receiver
                                        Firebase firebaseUnfriendedRef = MainActivity.ref.child("users").child(author).child("friends");
                                        Firebase firebaseThisUserRef = MainActivity.userRef.child("friends");

                                        firebaseUnfriendedRef.updateChildren(unfriendedMap);
                                        firebaseThisUserRef.updateChildren(currentUserMap);

                                        // change the icon
                                        floatingActionButton.setImageResource(R.drawable.ic_action_add_person);
                                        // change the state
                                        userState = null;

                                        // parse stuff
                                        // delete from user array of friends


                                        // reciver (this user)

                                        ParseQuery<ParseObject> queryThisUser = ParseQuery.getQuery("Users");
                                        queryThisUser.whereEqualTo("authorId", MainActivity.uid); // find this user
                                        queryThisUser.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                                                if (e == null) {

                                                    ParseObject object = objects.get(0); // only one element. the id is unique
                                                    List<String> strings = new ArrayList<String>();
                                                    strings.add(author);
                                                    object.removeAll("friends", strings);
                                                    object.saveInBackground();
                                                } else {
                                                    Log.d("score", "Error: " + e.getMessage());
                                                }
                                            }

                                        });

                                        ParseQuery<ParseObject> queryOtherUser = ParseQuery.getQuery("Users");
                                        queryOtherUser.whereEqualTo("authorId", author); // find the other user, the one we are viewing
                                        queryOtherUser.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                                                if (e == null) {

                                                    ParseObject object = objects.get(0); // only one element. the id is unique
                                                    List<String> strings = new ArrayList<String>();
                                                    strings.add(MainActivity.uid);
                                                    object.removeAll("friends", strings);
                                                    object.saveInBackground();
                                                } else {
                                                    Log.d("score", "Error: " + e.getMessage());
                                                }
                                            }

                                        });


                                    }
                                })
                                .show();


                    }else if(userState == request){
                        // this state represents the case
                        // that I have received a request from this user
                        // so it has to be time icon same as pending
                        floatingActionButton.setImageResource(R.drawable.ic_action_time);
                        Snackbar.make(v, "This user sent you a friend request", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            });

    }


    // set up the pallete image in background
    private class PaletteBackground extends AsyncTask<String,Void,Bitmap>{

        String strImageHolder;

        @Override
        protected Bitmap doInBackground(String... params) {
            strImageHolder = params[0];
            Bitmap bitmap;

            // decode the string
            try {
                byte[] bytes = Base64.decode(strImageHolder, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                return bitmap;
            }catch (Exception e){

                Log.e("ErrorImage", e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // do palette work
            // set up palette
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @SuppressWarnings("ResourceType")
                @Override
                public void onGenerated(Palette palette) {

                    mutedColor = palette.getMutedColor(R.color.primary_500);
                    collapsingToolbarLayout.setContentScrimColor(mutedColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(R.color.black_trans80);
                }
            });
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
