package com.fakhouri.salim.quest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * Created by salim on 12/18/2015.
 */
public class UserProfile extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int mutedColor = R.attr.colorPrimary;

    private User mUser;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);

        toolbar = (Toolbar)findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // ENSURE USER IS NOT NULL LATERZ
        mUser = MainActivity.myUser;
        collapsingToolbarLayout.setTitle(mUser.getUsername());

        ImageView header = (ImageView) findViewById(R.id.header);

        // get user image
        //String strImage = mUser.getUserImage();
        //Bitmap bitmap = mUser.stringToBitmap(strImage);

        if(MainActivity.userImageStatic != null){
            header.setImageBitmap(MainActivity.userImageStatic);
            // set up palette
            Palette.from(MainActivity.userImageStatic).generate(new Palette.PaletteAsyncListener() {
                @SuppressWarnings("ResourceType")
                @Override
                public void onGenerated(Palette palette) {

                    mutedColor = palette.getMutedColor(R.color.primary_500);
                    collapsingToolbarLayout.setContentScrimColor(mutedColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(R.color.black_trans80);
                }
            });
        }else{
            String strImage = mUser.getUserImage();
            Bitmap bitmap = mUser.stringToBitmap(strImage);
            header.setImageBitmap(bitmap);

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




        recyclerView = (RecyclerView)findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true); // only one view

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new UserAdapter(MainActivity.myUser);
        recyclerView.setAdapter(adapter);


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
