package com.fakhouri.salim.quest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by salim on 12/24/2015.
 */
public class ViewProfile extends AppCompatActivity {

    private ImageView header;

    private String author;
    private Firebase authorPath;



    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int mutedColor = R.attr.colorPrimary;

    private LoadImageFromString loadImageFromString;

    private User mUser;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton floatingActionButton;

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
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingPicture);

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


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        // set up the floating action
        if(mUser != null){
            // do it here

        }


    }


    // set up the pallete image in background
    private class PaletteBackground extends AsyncTask<String,Void,Bitmap>{

        String strImageHolder;

        @Override
        protected Bitmap doInBackground(String... params) {
            strImageHolder = params[0];
            Bitmap bitmap = null;

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
