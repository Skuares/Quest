package com.fakhouri.salim.quest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;


public class ViewProfile extends AppCompatActivity {

    private ImageView header;

    private String author;
    private Firebase authorPath;

    private Integer userState = 0;
    private Map<String,Object> userMapFriends;


    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int mutedColor = R.attr.colorPrimary;

    private LoadImageFromString loadImageFromString;

    private User mUser;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadImageFromString = new LoadImageFromString(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarView);
        header = (ImageView) findViewById(R.id.headerView);
        floatingActionButton = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.floatingPictureView);

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

                    recyclerView = (RecyclerView)findViewById(R.id.scrollableviewView);
                    recyclerView.setHasFixedSize(true); // only one view

                    layoutManager = new LinearLayoutManager(ViewProfile.this);
                    recyclerView.setLayoutManager(layoutManager);

                    adapter = new UserAdapter(mUser);
                    recyclerView.setAdapter(adapter);

                    // check the state of this user
                    userMapFriends = mUser.getFriends();
                    // check to see if this user in the userMap friends

                    userState = (Integer)userMapFriends.get(author); // the author id of this particular user
                    if(userState == null){
                        // this user is not a friend of your
                        // and have not sent him before
                        // do not change the icon
                        Log.e("UserState",String.valueOf(userState));

                    }else if(userState == 1){
                        // sent but no acceptance
                        // change the icon
                        Log.e("UserState",String.valueOf(userState));
                    }else if(userState == 2){
                        // this user is your friend
                        // change icon
                        Log.e("UserState",String.valueOf(userState));

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        if(mUser != null){
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("userStateHello","floating is clicked");
                    /*
                    check the state of the user
                     */
                    if(userState == null){
                        // change the icon
                        floatingActionButton.setImageResource(R.drawable.ic_action_time);
                        // use parse to send a push request an add friend

                        // update the hashmap of this user's friends
                    }else if(userState == 1){
                        // pending state
                        // do not change the icon
                        // let user know that his request has   been sent
                        Snackbar.make(v, "A Request Has Already Been Sent", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else if(userState == 2){
                        // you are friened with this user
                        // onclick , pop up dialog
                        // ask if you want to unfriend this user
                        // if so
                        // delete this user(author) from the hashmap
                        // and delete this user (current) from author's hashmap
                        //Snackbar.make(v, "A Request Has Already Been Sent", Snackbar.LENGTH_LONG)
                          //      .setAction("Action", null).show();
                    }

                }
            });
        }





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
