package com.skuares.studio.quest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by salim on 12/13/2015.
 */
public class TabFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int fragNumber = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout,container,false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager(),getContext()));

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    class MyAdapter extends FragmentStatePagerAdapter {


        Context context;
        private int[] images = {


                R.drawable.ic_action_help,
                R.drawable.ic_action_map,
                R.drawable.ic_action_person
        };

        String[] strings = {
                "Stream",
                "Map",
                "Person"
        };

        public MyAdapter(FragmentManager fm, Context context) {
            super(fm);

            this.context = context;
        }



        @Override
        public Fragment getItem(int position) {

            switch (position){

                case 0: return new StreamFragment();
                case 1: return new MapFragment();
                case 2: return new QOwnStreamFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragNumber;
        }

        public CharSequence getPageTitle(int position){

            /*
            switch(position){

                case 0: return "Tab 1";
                case 1: return "Tab 2";
                case 2: return "Tab 3";

            }

            return null;

            */

            // let's add an image

            // Generate title based on item position
            // return tabTitles[position];

            // getDrawable(int i) is deprecated, use getDrawable(int i, Theme theme) for min SDK >=21
            // or ContextCompat.getDrawable(Context context, int id) if you want support for older versions.
            // Drawable image = context.getResources().getDrawable(iconIds[position], context.getTheme());
            // Drawable image = context.getResources().getDrawable(imageResId[position]);


            Drawable image = ContextCompat.getDrawable(context, images[position]);


            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString("   "+strings[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;

        }
    }
}
