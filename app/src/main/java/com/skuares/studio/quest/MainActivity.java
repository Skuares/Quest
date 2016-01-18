package com.skuares.studio.quest;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.MapView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

//Social login libraries
import com.facebook.appevents.AppEventsLogger;
import com.skuares.studio.quest.Request.BroadcastRequestActivity;
import com.skuares.studio.quest.Request.FriendRequestActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static ParseObject userObject;
    ParseInstallation parseInstallation;

    public static int badgeCount = 0;
    public static int badgeCountBroadcast = 0;


    private static int REQUEST_CODE = 1;

    private int notificationIconId = 123123;

    Menu menu;

        /* Bad strategy a better approach in retreive user method
        2 lists to get the friend requests
        list<String> to hold all the friend requests identified by 0 and contains the users pointers
        list<User> obtained from list<String>  and passed through the intent to FriendRequestActivity
         */

    public static List<String> usersPointers;
    public static List<String> questNotificationBroadcast;
    Firebase invitesReferenc;

    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;


    NavigationView navigationView;


    android.support.v4.app.FragmentTransaction transaction;
    android.support.v4.app.FragmentManager manager;

    ImageView headerImage;
    TextView headerText;

    private View headerView;

    Firebase friendsReqestReference;

    /* Data from the authenticated user */
    private AuthData mAuthData;

    public static String uid = null;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    public static Firebase ref;
    public static Firebase userRef;
    public static User myUser = null;
    public static Bitmap userImageStatic = null;


    /*
    Google Places Api
     */
    private boolean runOncePlaceGetter = false;
    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

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

        runOncePlaceGetter = true;

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        setTitle("");

        toolbar = mViewPager.getToolbar();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout2);

        //new MyAdapter(getChildFragmentManager(),getContext())
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {


            @Override
            public Fragment getItem(int position) {
                switch (position) {

                    case 0:
                        return new StreamFragment();
                    case 1:
                        return new QOwnStreamFragment();
                    case 2:
                        return new QTookStreamFragment();
                    case 3:
                        return new QJoinStreamFragment();

                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return "Home";
                    case 1:
                        return "Q you own";
                    case 2:
                        return "Q you took";

                    case 3:
                        return "Q you Join";

                }
                return "";
            }
        });


        // NEED ADJUSTMENT BASED ON API
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:

                        return HeaderDesign.fromColorResAndUrl(
                                R.color.bluesh,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");

                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.bluesh,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");

                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.app_name, R.string.app_name);
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        // **********************************************************
        // ******************************************************8888



            /*
            Friend Request list of users pointers
             */

        badgeCount = 0; // referes to friend requests               | we do it this way so badge does not increment every time the app opens
        badgeCountBroadcast = 0; // referes to broadcast requests   |
        usersPointers = new ArrayList<String>();
        questNotificationBroadcast = new ArrayList<String>();


        View logo = findViewById(R.id.logo_white);
        if (logo != null)
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });




        //drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.shitstuff);

        // we need the header view
        headerView = navigationView.inflateHeaderView(R.layout.header_drawer);
        //headerImage = (ImageView)headerView.findViewById(R.id.headerImage);
        //headerText = (TextView) headerView.findViewById(R.id.headerText);


        headerImage = (ImageView) headerView.findViewById(R.id.headerImage);
        headerText = (TextView) headerView.findViewById(R.id.headerText);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // when user clicks close the drawer
                mDrawer.closeDrawers();

                if (item.getItemId() == R.id.edit_profile) {

                    // replace with tab fragment
                    //android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                    //transaction.replace(R.id.containerView, new TabFragment()).commit();

                    // open user profile activity
                    Intent intentProfile = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(intentProfile);

                }

                if (item.getItemId() == R.id.secondItem) {
                       Intent intentMap = new Intent(MainActivity.this,MapActivity.class);
                       startActivity(intentMap);

                }
                if (item.getItemId() == R.id.thirdItem) {

                }


                if (item.getItemId() == R.id.logout) {

                    if (mAuthData != null) {
                            /* logout of Firebase */
                        ref.unauth();
                    } else {

                    }
                }

                // rest of the menu

                return false;
            }
        });


        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();




    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }
    /*
    Get current place of user

    likelihood is from 0 to 1.0

    CONSIDER DOING IT IN A BACKGROUND (done)
     */

    private class UserPlaceBackground extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //  Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.e("placesno","returned");
                return null;
            }

            // restric this to run only once
            // not every time we change activity
            if(runOncePlaceGetter){
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(mGoogleApiClient, null);
                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                        PlaceLikelihood placeLikelihoodHolder = null;
                        float likelihood = (float) 0.0;

                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                            if(placeLikelihood.getLikelihood() > likelihood){
                                // this is good place
                                placeLikelihoodHolder = placeLikelihood;
                                // set the likelihood to the new value
                                likelihood = placeLikelihood.getLikelihood();
                            }



                        }
                        if(placeLikelihoodHolder != null){

                            Log.e("PLACEPICKED",String.valueOf(placeLikelihoodHolder.getPlace().getName()));
                            // get the best place and save it in user firebase and in user Parse
                            // try first getting the first location
                            final Place place = placeLikelihoodHolder.getPlace();

                            final APlace aPlace = new APlace(place.getName().toString(),place.getAddress().toString(),
                                    place.getId(),place.getLatLng().latitude,place.getLatLng().longitude);

                            if(userRef != null){
                                Log.e("placeInsert","doing it now");
                                userRef.child("place").setValue(aPlace);

                                // insert into parse
                                // get the user authorId

                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                String authorId = (String) installation.get("installationAuthorId");
                                // make sure we have a valid data in parse
                                if(authorId != null){
                                    // get the user now
                                    ParseQuery<ParseObject> queryOtherUser = ParseQuery.getQuery("Users");
                                    queryOtherUser.whereEqualTo("authorId", authorId); // find the other user, the one we are viewing
                                    queryOtherUser.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, com.parse.ParseException e) {
                                            if (e == null) {

                                                ParseObject object = objects.get(0); // only one element. the id is unique
                                                // create a ParseGeoPoint
                                                ParseGeoPoint parseGeoPoint = new ParseGeoPoint(aPlace.getPlaceLatitude(), aPlace.getPlaceLongitude());
                                                // insert the coordinates into it

                                                object.put("location", parseGeoPoint);
                                                object.saveInBackground();
                                            } else {
                                                Log.d("score", "Error: " + e.getMessage());
                                            }
                                        }

                                    });


                                }


                            }


                        }

                        likelyPlaces.release();
                    }
                });

                // set it back to false
                runOncePlaceGetter = false;
            }
            Log.e("places","I m in rETURN NULL");
            return null;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        // get the current place
        // this runs every time we navigate between activities
        // needs to be solved
        //Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_SHORT).show();
        UserPlaceBackground userPlaceBackground = new UserPlaceBackground();
        userPlaceBackground.execute();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }

    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {

        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {

            //((MainActivity) getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }


    // Animation
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }





    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_noti, menu);

        this.menu = menu;
            /*

            */
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.item_samplebadge) {
            // make an intent to FriendRequestActivity
            Intent intent = new Intent(MainActivity.this, FriendRequestActivity.class);
            // pass friendsReques.. list along
            Bundle bundle = new Bundle();
            bundle.putSerializable("listOfIds", (Serializable) usersPointers);
            intent.putExtras(bundle);
            startActivity(intent);


            // this should happen based on interaction with the requests
            badgeCount = 0;

            //ActionItemBadge.update(item, MainActivity.badgeCount);
                /*
                // check if zero let it go
                if (badgeCount <= 0) {
                    // hide it
                    ActionItemBadge.hide(item);
                }
                */

            return true;
        }

        if(item.getItemId() == R.id.item_badge_broadcast){
            // make an intent to FriendRequestActivity
            Intent intent = new Intent(MainActivity.this, BroadcastRequestActivity.class);
            // pass friendsReques.. list along
            Bundle bundle = new Bundle();
            bundle.putSerializable("listOfQuestIds", (Serializable) questNotificationBroadcast);
            intent.putExtras(bundle);
            startActivity(intent);


            // this should happen based on interaction with the requests
            badgeCountBroadcast = 0;

        }
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void fetchNotificationsFriendRequest() {

    }

    public void retrieveUser() {
        if (mAuthData != null) {

            // listener for user key
            userRef = ref.child("users").child(mAuthData.getUid());
            uid = mAuthData.getUid();
            userRef.addValueEventListener(new ValueEventListener() {
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
                    if (sharedPreferences.getBoolean("firstTime", true)) {
                        // run once
                        Log.e("Hello", "I AM HERE RUN ONCE");
                        userObject = new ParseObject("Users");
                        //userObject.getObjectId();
                        userObject.put("email", myUser.getEmail());
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
                                if (e == null) {
                                    // success
                                    //Toast.makeText(MainActivity.this,"Installation Parse",Toast.LENGTH_SHORT).show();
                                } else {
                                    // failed
                                    Log.e("ParseError", e.getMessage());
                                }
                            }
                        });


                        // lock the shared
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("firstTime", false);
                        editor.commit();
                    }

                        /*
                        Better approach attach a listener on users/userId/friends by value 0
                        when this triggered
                        get the users pointers
                        get the users from pointers
                        activate icon
                         */
                    // in a method otherwise we would have many listeners

                    // initialize the reference

                    friendsReqestReference = new Firebase("https://quest1.firebaseio.com/users/" + uid + "/friends");
                    Query query = friendsReqestReference.orderByValue().equalTo(0);
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            //Log.e("logmeThis",""+dataSnapshot.getKey());
                            Log.e("RequestMainActivity", "onChildAdded");


                            if (usersPointers.contains(dataSnapshot.getKey())) {
                                // it is already here
                                // do nothing
                                Log.e("checker", "fail data is here");
                            } else {
                                // save this pointer in usersPointers
                                usersPointers.add(dataSnapshot.getKey());
                                Log.e("checker", "success data is nt here");
                                for (String st : usersPointers) {
                                    Log.e("checkerData", "" + st);
                                }

                                // check the value
                                // only increment when it is 0
                                if (dataSnapshot.getValue(Integer.class) == 0) {

                                    badgeCount++;
                                }
                            }

                            // notify user by notification icon


                            // update the icon

                            // add the notification icon programmatically
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_samplebadge), getDrawable(R.drawable.ic_action_person), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);

                            } else {
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_samplebadge), getResources().getDrawable(R.drawable.ic_action_person), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);
                            }





                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            // the user accepts
                            Log.e("RequestMainActivity", "onChildChanged");
                            // decrease the badgeCount and update the notification icon
                            // add the notification icon programmatically
                            //badgeCount--;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_samplebadge), getDrawable(R.drawable.ic_action_person), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getDrawable(R.drawable.ic_stat_bell),badgeCount);
                            } else {
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_samplebadge), getResources().getDrawable(R.drawable.ic_action_person), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);
                                //ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), this.getResources().getDrawable(R.drawable.ic_stat_bell), ActionItemBadge.BadgeStyles.DARK_GREY, MainActivity.badgeCount);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getResources().getDrawable(R.drawable.ic_stat_bell),badgeCount);
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                            }

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                            // get this userId out of usersIds
                            // so when user clicks again on the notification icon does not see this card
                            //Log.e("MainActivityRequest","onChildRemoved ----"+dataSnapshot.getKey());
                            usersPointers.remove(dataSnapshot.getKey());


                            Log.e("RequestMainActivity", "onChildRemoved");
                            // triggered when the user rejects or ignores the request
                            //badgeCount--;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_samplebadge), getDrawable(R.drawable.ic_action_person), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getDrawable(R.drawable.ic_stat_bell),badgeCount);
                            } else {
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_samplebadge), getResources().getDrawable(R.drawable.ic_action_person), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);
                                //ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), this.getResources().getDrawable(R.drawable.ic_stat_bell), ActionItemBadge.BadgeStyles.DARK_GREY, MainActivity.badgeCount);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getResources().getDrawable(R.drawable.ic_stat_bell),badgeCount);
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                            }

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    // similar approach to friend request
                    invitesReferenc = new Firebase("https://quest1.firebaseio.com/users/" + uid + "/invites");
                    Query invitesQuery = invitesReferenc.orderByValue().equalTo(0);
                    invitesQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            // invites contain questIds
                            if (questNotificationBroadcast.contains(dataSnapshot.getKey())) {
                                // it is already here
                                // do nothing
                                Log.e("checker", "fail data is here");
                            } else {
                                // save this questId in the list
                                questNotificationBroadcast.add(dataSnapshot.getKey());
                                Log.e("checker", "success data is nt here");
                                for (String st : questNotificationBroadcast) {
                                    Log.e("checkerData", "" + st);
                                }

                                // check the value
                                // only increment when it is 0
                                if (dataSnapshot.getValue(Integer.class) == 0) {

                                    badgeCountBroadcast++;
                                }
                            }

                            // notify user by notification icon
                            // update the icon
                            // add the notification icon programmatically
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_badge_broadcast), getDrawable(R.drawable.ic_action_signal), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCountBroadcast);

                            } else {
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_badge_broadcast), getResources().getDrawable(R.drawable.ic_action_signal), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCountBroadcast);
                            }



                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_badge_broadcast), getDrawable(R.drawable.ic_action_signal), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCountBroadcast);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getDrawable(R.drawable.ic_stat_bell),badgeCount);
                            } else {
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_badge_broadcast), getResources().getDrawable(R.drawable.ic_action_signal), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCountBroadcast);
                                //ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), this.getResources().getDrawable(R.drawable.ic_stat_bell), ActionItemBadge.BadgeStyles.DARK_GREY, MainActivity.badgeCount);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getResources().getDrawable(R.drawable.ic_stat_bell),badgeCount);
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            // no removal
                            // we need the states to keep track

                            // delete it for now
                            // since we are going to keep the track in the quest itself

                            questNotificationBroadcast.remove(dataSnapshot.getKey());


                            Log.e("RequestMainActivity", "onChildRemoved");
                            // triggered when the user rejects or ignores the request
                            //badgeCount--;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_badge_broadcast), getDrawable(R.drawable.ic_action_signal), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCountBroadcast);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getDrawable(R.drawable.ic_stat_bell),badgeCount);
                            } else {
                                ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.item_badge_broadcast), getResources().getDrawable(R.drawable.ic_action_signal), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCountBroadcast);
                                //ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), this.getResources().getDrawable(R.drawable.ic_stat_bell), ActionItemBadge.BadgeStyles.DARK_GREY, MainActivity.badgeCount);
                                //new ActionItemBadgeAdder().act(MainActivity.this).menu(menu).title("title").itemDetails(0,notificationIconId, 1).showAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS).add(getResources().getDrawable(R.drawable.ic_stat_bell),badgeCount);
                                //ActionItemBadge.update(menu.findItem(R.id.item_samplebadge),badgeCount);
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // set default header view !!!!!!!!!!!!!!!!!!!!!!!


                }
            });

        } else {

            Log.e("auth", "auth is null");
        }

    }


    public List<String> getKeysFromValues(Map<String, Object> map, int value) {
        List<String> list = new ArrayList<String>();

        // iterate over the keys in map
        for (String string : map.keySet()) {
            // check if this key maps to 0 value
            if (map.get(string).equals(0)) {
                // insert into the list
                list.add(string);
            }
        }


        return list;
    }


        /*

        private class CheckFriendRequests extends AsyncTask<Map<String, Object>, Void, Void> {

            Map<String, Object> map;

            @SafeVarargs
            @Override
            protected final Void doInBackground(Map<String, Object>... params) {

                map = params[0];
                usersPointers = getKeysFromValues(map, 0);
                // check if usersPointers has values
                if (usersPointers == null) {
                    return null;
                }else{
                    friendRequestUsers = new ArrayList<User>();

                    // loop through the list
                    // attach listener for one time for each user pointer and store it in list<User> named friendRequestUser
                    // reference
                    Firebase firebase;
                    final int[] checker = {0};
                    for (int i = 0; i < usersPointers.size(); i++) {

                        firebase = new Firebase("https://quest1.firebaseio.com/users/" + usersPointers.get(i));
                        // attach listener for one time


                        final int finalI = i;
                        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // cast it to User and store it
                                User user = dataSnapshot.getValue(User.class);
                                friendRequestUsers.add(user);
                                Log.e("elseHas", "listener is called");
                                checker[0] = finalI;

                                // we need to know when to return
                                // call on last time we loop
                                if(checker[0]+1 == usersPointers.size()){
                                    notificationStuff(friendRequestUsers);
                                }


                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    }

                }

                return null;
            }


        }
        */


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (UserProfile.imageHasChanged) {

            String sImage = myUser.getUserImage();
            // convert string to bitmap and assign it
            Bitmap bitmap = myUser.stringToBitmap(sImage);
            headerImage.setImageBitmap(bitmap);

            // assig it again to static
            userImageStatic = bitmap;
            // set it back to false

            // refresh the stream Fragment

            //Log.e("imagechanged","changed");
        } else {
            //Log.e("imagechanged","not changed");
        }


    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {

            retrieveUser();


            // all good
            //Intent intent = new Intent(MainActivity.this,SignUp.class);
            //startActivity(intent);
            //finish();
        } else {

            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
            finish();
            // user is not authenticated through him out to log in
        }

        // store the auth for later use
        this.mAuthData = authData;
    }


}
