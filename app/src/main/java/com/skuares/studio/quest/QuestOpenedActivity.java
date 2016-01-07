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

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salim on 12/23/2015.
 */
public class QuestOpenedActivity extends AppCompatActivity implements OnMenuItemClickListener {

    private Toolbar toolbar;
    private List<ToDo> list;
    private DataWrapperTodo dataWrapperTodo;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private QuestCard questCard;
    LoadImageFromString loadImageFromString;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    public static final int QUEST = 0;
    public static final int TODO = 1;
    private int mDatasetTypes[] = {QUEST, TODO};

    private String authorId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_opened_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbarTodos);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // get the questCard
        questCard = (QuestCard) getIntent().getSerializableExtra("quest");
        // get the authorId
        authorId = questCard.getAuthorId();


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

        if(authorId.equals(MainActivity.uid)){
            // this is the author

            menuParams.setMenuObjects(getMenuObjectsAuthor());
        }else{
            // not the author
            // do not show broadcast option

            menuParams.setMenuObjects(getMenuObjects());
        }

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
    }
}
