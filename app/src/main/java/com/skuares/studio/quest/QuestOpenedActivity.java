package com.skuares.studio.quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 12/23/2015.
 */
public class QuestOpenedActivity extends AppCompatActivity implements OnMenuItemClickListener {



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
                // pass it to a dailog
                // and let the user broadcast

                ParseQuery<ParseObject> queryThisUser = ParseQuery.getQuery("Users");
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
                                inviteMap.put(questKey,noReactionState);
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

            }else{
                // not taker nor author
                Toast.makeText(this, "Please take the quest so you can broadcast", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
