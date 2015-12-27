package com.skuares.studio.quest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.SaveCallback;

//Social login libraries
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {


    ParseObject userObject;
    ParseInstallation parseInstallation;

    public static int badgeCount = 1;

    private static int REQUEST_CODE= 1;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    android.support.v4.app.FragmentTransaction transaction;
    android.support.v4.app.FragmentManager manager;

    ImageView headerImage;
    TextView headerText;

    private View headerView;



    /* Data from the authenticated user */
    private AuthData mAuthData;

    public static String uid = null;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    public static Firebase ref;
    public static Firebase userRef;
    public static User myUser = null;
    public static Bitmap userImageStatic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // check user
        ref = new Firebase(getResources().getString(R.string.firebaseUrl));
        // authinticate user if logged in send him to home
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {

                mAuthData = authData;

                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        ref.addAuthStateListener(mAuthStateListener);

        // **********************************************************
        // ******************************************************8888

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView = (NavigationView)findViewById(R.id.shitstuff);

        // we need the header view
        headerView = navigationView.inflateHeaderView(R.layout.header_drawer);
        //headerImage = (ImageView)headerView.findViewById(R.id.headerImage);
        //headerText = (TextView) headerView.findViewById(R.id.headerText);


        headerImage = (ImageView) headerView.findViewById(R.id.headerImage);
        headerText = (TextView) headerView.findViewById(R.id.headerText);



        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.containerView,new TabFragment()).commit();

        /**
         * Setup click events on the Navigation View Items.
         */

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // when user clicks close the drawer
                drawerLayout.closeDrawers();

                if (item.getItemId() == R.id.edit_profile) {

                    // replace with tab fragment
                    //android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                    //transaction.replace(R.id.containerView, new TabFragment()).commit();

                    // open user profile activity
                    Intent intentProfile = new Intent(MainActivity.this,UserProfile.class);
                    startActivity(intentProfile);

                }

                if (item.getItemId() == R.id.secondItem) {
                    android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.containerView, new NothingFragment());
                    transaction.addToBackStack("other");
                    transaction.commit();


                }

                if(item.getItemId() == R.id.logout){

                    if (mAuthData != null) {
                        /* logout of Firebase */
                        ref.unauth();
                    }else{

                    }
                }

                // rest of the menu

                return false;
            }
        });


        // setup drawer toggle
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();




        /*
        Parse Stuff
        Should be in retrieve user , cause we need the data
         */



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //Log.e("MenuInf", "Menu");

        //you can add some logic (hide it if the count == 0)
        if (MainActivity.badgeCount > 0) {

            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), this.getDrawable(R.drawable.ic_stat_bell), ActionItemBadge.BadgeStyles.DARK_GREY, MainActivity.badgeCount);
            } else {
                ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), this.getResources().getDrawable(R.drawable.ic_stat_bell), ActionItemBadge.BadgeStyles.DARK_GREY, MainActivity.badgeCount);
            }


        } else {
            ActionItemBadge.hide(menu.findItem(R.id.item_samplebadge));
        }
        return true;

        /*
         //If you want to add your ActionItem programmatically you can do this too. You do the following:
        new ActionItemBadgeAdder().act(this).menu(menu).title(R.string.sample_2).itemDetails(0, SAMPLE2_ID, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(bigStyle, 1);
        return true;
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.item_samplebadge) {
            // make an intent to RequestActivity

            Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
            MainActivity.badgeCount--;

            ActionItemBadge.update(item, MainActivity.badgeCount);
            // check if zero let it go
            if(badgeCount <= 0){
                // hide it
                ActionItemBadge.hide(item);
            }
            return true;
        }
        if(item.getItemId() == android.R.id.home){

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }


    public void retrieveUser(){
        if(mAuthData != null){

            // listener for user key
            userRef = ref.child("users").child(mAuthData.getUid());
            uid = mAuthData.getUid();
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // convert snapshot to user class
                    myUser = dataSnapshot.getValue(User.class);

                    String sImage = myUser.getUserImage();
                    // convert string to bitmap and assign it
                    Bitmap bitmap = myUser.stringToBitmap(sImage);

                    userImageStatic = bitmap;
                    // header image WE STILL NEED TO MAKE IT CHANGEABLE
                    headerImage.setImageBitmap(bitmap);

                    // change the name
                    headerText.setText(myUser.getUsername());

                    /*
                    1- Put parse stuff to create the user class
                    2- set up the Parse Installation .. add the author id to it
                    so it can be unique
                     */

                    /*
                    Ensure this gets called once
                    not every time user opens the account

                     */
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    if(sharedPreferences.getBoolean("firstTime",true)){
                        // run once
                        Log.e("Hello","I AM HERE RUN ONCE");
                        userObject = new ParseObject("Users");
                        //userObject.getObjectId();
                        userObject.put("email",myUser.getEmail());
                        userObject.put("username", myUser.getUsername());
                        userObject.put("authorId", mAuthData.getUid());
                        // list of user's friends
                        userObject.addUnique("friends", "");// empty frield to match firebase :)
                        userObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    // success
                                    //Toast.makeText(MainActivity.this, "Saved To Parse", Toast.LENGTH_SHORT).show();
                                } else {
                                    // failed
                                    Log.e("ParseError", e.getMessage());
                                }

                            }
                        });

                        // get the current installation
                        parseInstallation = ParseInstallation.getCurrentInstallation();
                        // subscribe to System
                        parseInstallation.addUnique("channels", "System");

                        // add author id to parse installation
                        parseInstallation.put("installationAuthorId", mAuthData.getUid());
                        // make relation with userObject
                        parseInstallation.put("users", userObject);
                        parseInstallation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    // success
                                    //Toast.makeText(MainActivity.this,"Installation Parse",Toast.LENGTH_SHORT).show();
                                }else{
                                    // failed
                                    Log.e("ParseError", e.getMessage());
                                }
                            }
                        });



                        // lock the shared
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("firstTime",false);
                        editor.commit();
                    }



                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // set default header view !!!!!!!!!!!!!!!!!!!!!!!


                }
            });

        }else{

            Log.e("auth", "auth is null");
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // check image
        Log.e("HEYTHERE","HERE I AM");


        if(UserProfile.imageHasChanged){

            String sImage = myUser.getUserImage();
            // convert string to bitmap and assign it
            Bitmap bitmap = myUser.stringToBitmap(sImage);
            headerImage.setImageBitmap(bitmap);

            // assig it again to static
            userImageStatic = bitmap;
            // set it back to false

            // refresh the stream Fragment

            //Log.e("imagechanged","changed");
        }else{
            //Log.e("imagechanged","not changed");
        }


    }

    private void setAuthenticatedUser(AuthData authData) {
        if(authData != null){

            retrieveUser();


            // all good
            //Intent intent = new Intent(MainActivity.this,SignUp.class);
            //startActivity(intent);
            //finish();
        }else{

            Intent intent = new Intent(MainActivity.this,SignUp.class);
            startActivity(intent);
            finish();
            // user is not authenticated through him out to log in
        }

        // store the auth for later use
        this.mAuthData = authData;
    }



}
