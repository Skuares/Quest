package com.skuares.studio.quest;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.parse.FindCallback;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 12/23/2015.
 */
public class QuestOpenedActivity extends AppCompatActivity implements OnMenuItemClickListener {


    private static int REQUEST_CODE= 100;
    LoadImageFromPath loadImageFromPath;
    /*
    Define the states about the broadcast
     */

    private int noReactionState = 0;
    private int comingState = 1;
    private int maybeState = 2;
    private int noState = 3;


    private Toolbar toolbar;
    private List<ToDo> list;
    private DataWrapperTodo dataWrapperTodo;

    private RecyclerView recyclerView;
    private QuestOpenedAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private QuestCard questCard;
    LoadImageFromString loadImageFromString;

    ImageView uImage;
    TextView uText;
    TextView qTitle;
    TextView qSubTitle;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;


    public static final int QUEST = 0;
    public static final int TODO = 1;
    private int mDatasetTypes[] = {QUEST, TODO};

    private String authorId = null;
    private String questKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_opened_activity);

        loadImageFromPath = new LoadImageFromPath(this);
        // initialize the communicattor
        //communicator = new Communicator();

        toolbar = (Toolbar) findViewById(R.id.toolbarTodos);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get the questCard
        questCard = (QuestCard) getIntent().getSerializableExtra("quest");
        // get the authorId
        authorId = questCard.getAuthorId();
        questKey = questCard.getQuestKey();


        fragmentManager = getSupportFragmentManager();
        initMenuFragment();

        loadImageFromString = new LoadImageFromString(this);

        // retreive data
        dataWrapperTodo = (DataWrapperTodo) getIntent().getSerializableExtra("todos");
        list = dataWrapperTodo.getToDos();



        uImage = (ImageView)findViewById(R.id.uImage);
        uText = (TextView)findViewById(R.id.uText);
        qTitle = (TextView)findViewById(R.id.qTitle);
        qSubTitle = (TextView)findViewById(R.id.qSubTitle);

        // get user info from the questCard
        loadImageFromString.loadBitmapFromString(questCard.getQuestUserImage(),uImage);
        uText.setText(questCard.getQuestUsername());
        qTitle.setText(questCard.getQuestTitle());
        qSubTitle.setText(questCard.getQuestDescription());

        // set recyle stuff
        recyclerView = (RecyclerView) findViewById(R.id.recycleviewTodos);
        recyclerView.setHasFixedSize(true); // only one view

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up adapter
        adapter = new QuestOpenedAdapter(this,list,questCard,loadImageFromString,mDatasetTypes);
        recyclerView.setAdapter(adapter);


    }



    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));


        menuParams.setMenuObjects(getMenuObjectsAuthor());


        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);

    }



    private List<MenuObject> getMenuObjectsAuthor(){

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);


        MenuObject like = new MenuObject("Like");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);



        MenuObject addFav = new MenuObject("Take Quest");
        addFav.setResource(R.drawable.ic_action_handtake);

        MenuObject mapObject = new MenuObject("View Map");
        mapObject.setResource(R.drawable.ic_action_pinmap);

        // special for author
        MenuObject broadcast = new MenuObject("Broadcast");
        broadcast.setResource(R.drawable.ic_action_signal);

        menuObjects.add(close);
        //menuObjects.add(send);
        menuObjects.add(like);
        //menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(mapObject);
        menuObjects.add(broadcast);
        //menuObjects.add(block);
        return menuObjects;

    }
    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        //MenuObject send = new MenuObject("Send message");
        //send.setResource(R.drawable.icn_1);

        MenuObject like = new MenuObject("Like");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);

        //MenuObject addFr = new MenuObject("Add to friends");
        //BitmapDrawable bd = new BitmapDrawable(getResources(),
          //      BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));
        //addFr.setDrawable(bd);

        // should be take quest
        MenuObject addFav = new MenuObject("Take Quest");
        addFav.setResource(R.drawable.ic_action_handtake);

        MenuObject mapObject = new MenuObject("View Map");
        mapObject.setResource(R.drawable.ic_action_pinmap);
        //MenuObject block = new MenuObject("Block user");
        //block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        //menuObjects.add(send);
        menuObjects.add(like);
        //menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(mapObject);
        //menuObjects.add(block);
        return menuObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.quest_open_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){

            finish();
            return true;
        }

        if(item.getItemId() == R.id.context_menu){

            if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        if(position == 1){

            if(adapter != null){
                // restrict the use
                // if this user has already liked do not allow him to click

                // get the map containing usersWhoLiked
                if(questCard.getUsersWhoLiked() == null){

                    // it means he has not clicked before, and no one has clicked before .. Split to avoid null exception
                    adapter.like(MainActivity.uid,questKey);
                }else if (questCard.getUsersWhoLiked().get(MainActivity.uid) == null){

                    adapter.like(MainActivity.uid, questKey);

                }else{
                    /*
                    BETTER APPROACH CHANGE THE COLOR OF THE ICON
                     */
                    // spit out a toast telling the user that is already clicked
                    Toast.makeText(this, "Already liked it", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(position == 2){

            // take the quest
            if(adapter != null){
                if(MainActivity.uid.equals(authorId)){

                    // it is yours, you cant take it
                    Toast.makeText(this, "It is yours", Toast.LENGTH_SHORT).show();
                }
                else if(questCard.getTakers() == null){

                    // first time
                    adapter.take(MainActivity.uid, questKey);
                }else if (questCard.getTakers().get(MainActivity.uid) == null){
                    // first time for this user
                    adapter.take(MainActivity.uid,questKey);
                }else{
                     /*
                    BETTER APPROACH CHANGE THE COLOR OF THE ICON
                     */
                    // spit out a toast telling the user that is already clicked
                    Toast.makeText(this, "Already Took it", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(position == 4){// broadcast case

            // check if this user is the author if he is let him broadcast
            // if not, check if he is a taker and let him broadcast
            // if not taker nor authod tell him he must take the quest to broadcast

            if(MainActivity.uid.equals(authorId)){
                // this user is the author
                Toast.makeText(this, "Author can broadcast", Toast.LENGTH_SHORT).show();
                // get the friends array from parse

                // and let the user broadcast

                final ParseQuery<ParseObject> queryThisUser = ParseQuery.getQuery("Users");
                queryThisUser.whereEqualTo("authorId", MainActivity.uid);
                queryThisUser.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {

                            ParseObject object = objects.get(0);


                            // the notification
                            ParseQuery pushQuery = ParseInstallation.getQuery();
                            pushQuery.whereContainedIn("installationAuthorId",object.getList("friends") );


                            // set up the data in Quest and User in firebase
                            /*
                            Strategy:
                            2 firebase references ,1- for the quest to add the joiners,2- for users and then we loop on it for every
                            user

                             */

                            // get the users potential joiners
                            List<String> potentialJoiners = object.getList("friends");
                            potentialJoiners.remove(0);// get rid of the empty entry in parse

                            // add restriction
                            // do not broadcast to users who has already join the quest
                            for(String string: potentialJoiners){
                                // if this user is already in the joiners map
                                /*
                                Special case: first broadcast
                                no joiners at all so this would be null

                                 */
                                if(questCard.getJoiners() != null){
                                    if(questCard.getJoiners().containsKey(string)){
                                        // check now if he/she has accepted the request
                                        int result = (int)questCard.getJoiners().get(string);
                                        if(result == 1){
                                            potentialJoiners.remove(string);
                                        }
                                    }

                                }

                            }


                            Firebase questRefJoiners = new Firebase("https://quest1.firebaseio.com/Quests/"+questKey+"/joiners");
                            Firebase generalUserRef;


                            // convert the list to map
                            Map<String,Object> map = new HashMap<String,Object>();
                            for (String i : potentialJoiners){
                                // insert into the map
                                map.put(i,noReactionState);
                                // get the user and insert into it

                                generalUserRef = new Firebase("https://quest1.firebaseio.com/users/"+i+"/invites");
                                Map<String ,Object> inviteMap = new HashMap<String, Object>();
                                String combine = questKey+"----"+authorId;
                                inviteMap.put(combine,noReactionState);
                                generalUserRef.updateChildren(inviteMap);


                            }
                            // insert into the quest
                            questRefJoiners.updateChildren(map);

                            // we need to insert the invites into every user we have in the list


                            // Send push notification to query
                            ParsePush push = new ParsePush();
                            push.setQuery(pushQuery); // Set our Installation query
                            push.setMessage("You have an invitation from "+object.get("username"));
                            push.sendInBackground();


                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }

                });

            }else if(questCard.getTakers() == null) {
                // no takers at all
                Toast.makeText(this, "Please take the quest so you can broadcast", Toast.LENGTH_SHORT).show();

            }else if (questCard.getTakers().get(MainActivity.uid).equals(MainActivity.uid)){
                // this user is a taker
                Toast.makeText(this, "Taker can broadcast", Toast.LENGTH_SHORT).show();


                ParseQuery<ParseObject> queryTaker = ParseQuery.getQuery("Users");
                queryTaker.whereEqualTo("authorId", MainActivity.uid);
                queryTaker.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {

                            ParseObject objectTaker = objects.get(0);


                            // the notification
                            ParseQuery pushQueryTaker = ParseInstallation.getQuery();
                            pushQueryTaker.whereContainedIn("installationAuthorId", objectTaker.getList("friends"));


                            // set up the data in Quest and User in firebase
                            /*
                            Strategy:
                            2 firebase references ,1- for the quest to add the joiners,2- for users and then we loop on it for every
                            user

                             */

                            // get the users potential joiners
                            List<String> potentialJoinersTaker = objectTaker.getList("friends");
                            potentialJoinersTaker.remove(0);// get rid of the empty entry in parse

                            // remove the author (case: the author might be this taker broadcaster friend, so it
                            // doeesn't make sense to broadcast to him)
                            if(potentialJoinersTaker.contains(authorId)){
                                potentialJoinersTaker.remove(authorId);
                            }

                            // add restriction
                            // do not broadcast to users who has already join the quest
                            for(String string: potentialJoinersTaker){
                                // if this user is already in the joiners map
                                if(questCard.getJoiners().containsKey(string)){
                                    // check now if he/she has accepted the request
                                    int result = (int)questCard.getJoiners().get(string);
                                    if(result == 1){
                                        potentialJoinersTaker.remove(string);
                                    }
                                }

                            }


                            Firebase questRefJoinersTaker = new Firebase("https://quest1.firebaseio.com/Quests/" + questKey + "/joiners");
                            Firebase generalUserRefTaker;


                            // convert the list to map
                            Map<String, Object> mapTaker = new HashMap<String, Object>();
                            for (String i : potentialJoinersTaker) {
                                // insert into the map
                                mapTaker.put(i, noReactionState);
                                // get the user and insert into it
                                generalUserRefTaker = new Firebase("https://quest1.firebaseio.com/users/" + i + "/invites");
                                Map<String, Object> inviteMap = new HashMap<String, Object>();
                                String combine = questKey + "----" + MainActivity.uid;
                                inviteMap.put(combine, noReactionState);
                                generalUserRefTaker.updateChildren(inviteMap);


                            }
                            // insert into the quest
                            questRefJoinersTaker.updateChildren(mapTaker);

                            // we need to insert the invites into every user we have in the list


                            // Send push notification to query
                            ParsePush pushTaker = new ParsePush();
                            pushTaker.setQuery(pushQueryTaker); // Set our Installation query
                            pushTaker.setMessage("You have an invitation from " + objectTaker.get("username"));
                            pushTaker.sendInBackground();


                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }

                });



            }else{
                // not taker nor author
                Toast.makeText(this, "Please take the quest so you can broadcast", Toast.LENGTH_SHORT).show();
            }

        }
    }



    private class MakeQPictureAndSave extends AsyncTask<Object,Void,Boolean>{

        private String filePath;
        private String ownerId;
        private String questKey1;


        @Override
        protected Boolean doInBackground(Object... params) {

            filePath = (String)params[0];
            ownerId = (String)params[1];
            questKey1 = (String)params[2];

            Bitmap bitmap = LoadImageFromPath.decodeSampledBitmapFromPath(filePath, 100, 100);
            // create QPicture object
            String stringImage = QPicture.bitmapToString(bitmap);
            QPicture qPicture = new QPicture(ownerId,stringImage);
            // save the qPicture
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(questKey1,qPicture);

            Firebase qRef = new Firebase("https://quest1.firebaseio.com/");
            Firebase pictureRef = qRef.child("Pictures");
            Firebase newPictureRef = pictureRef.push();
            newPictureRef.setValue(map);


            return true;
        }


        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if(bool){
                adapter.notifyDataSetChanged();
            }

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

                if (Build.VERSION.SDK_INT >= 19) {

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Uri selectedImage = data.getData();


                    InputStream input;
                    Bitmap bitmap;
                    String picturePath = getPath(QuestOpenedActivity.this, selectedImage);
                    // save the image to firebase
                    MakeQPictureAndSave makeQPictureAndSave = new MakeQPictureAndSave();
                    makeQPictureAndSave.execute(picturePath,MainActivity.uid,questKey);

                } else {

                    DoTheJobInBackgroud hello = new DoTheJobInBackgroud();
                    hello.execute(data);
                }


            }


        } catch (Exception e) {
            Toast.makeText(QuestOpenedActivity.this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }



    private class DoTheJobInBackgroud extends AsyncTask<Intent, Void, String> {

        Intent data;

        @Override
        protected String doInBackground(Intent... params) {
            // get the image from data
            data = params[0];
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            //Log.d("hello", "Android Version is 18 below");
            Cursor cursor = QuestOpenedActivity.this.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String picturePath = cursor.getString(columnIndex);

                cursor.close();

                return picturePath;
            }

            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                // save the image to firebase
                MakeQPictureAndSave makeQPictureAndSave = new MakeQPictureAndSave();
                makeQPictureAndSave.execute(s,MainActivity.uid,questKey);

            } else {
                Toast.makeText(QuestOpenedActivity.this, "path is wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }



    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && com.skuares.studio.quest.DocumentsContract.isDocumentUri(uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = com.skuares.studio.quest.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = com.skuares.studio.quest.DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = com.skuares.studio.quest.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }




}
