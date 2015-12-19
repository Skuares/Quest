package com.fakhouri.salim.quest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

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
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }



    public void retrieveUser(){
        if(mAuthData != null){

            // listener for user key
            userRef = ref.child("users").child(mAuthData.getUid());
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
    protected void onRestart() {
        super.onRestart();
        // check image
        if(UserProfile.imageHasChanged){

            String sImage = myUser.getUserImage();
            // convert string to bitmap and assign it
            Bitmap bitmap = myUser.stringToBitmap(sImage);
            headerImage.setImageBitmap(bitmap);

            // assig it again to static
            userImageStatic = bitmap;
            // set it back to false
            UserProfile.imageHasChanged = false;
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
