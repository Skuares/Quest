package com.skuares.studio.quest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salim on 12/19/2015.
 */
public class TodoFragment extends Fragment  {

    ImageButton add;
    ImageButton back;

    EditText addDesc;
    EditText addTime;
    EditText addMoney;

    ToDo toDo;
    ListView listView;


    /*
  * onAttach(Context) is not called on pre API 23 versions of Android and onAttach(Activity) is deprecated
  * Use onAttachToContext instead
  */
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.todo_fragment_layout,container,false);


        add = (ImageButton)v.findViewById(R.id.add);

        addDesc = (EditText)v.findViewById(R.id.addDescription);
        addTime = (EditText)v.findViewById(R.id.addTime);
        addMoney = (EditText)v.findViewById(R.id.addMoney);

        listView = (ListView)v.findViewById(R.id.listView);

        return v;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        //


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                Fragment frag = manager.findFragmentByTag("TodoFragment");
                transaction.remove(frag);
                transaction.commit();
            }
        });

        /*
        // add listener for add
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // FILTER THE VALUES
                // get the values and set up to do object
                String desc = addDesc.getText().toString();
                String time = addTime.getText().toString();
                double money = Double.parseDouble(addMoney.getText().toString());

                toDo = new ToDo(desc,time,money);
                // add it to the list
                CreateQuest.todosList.add(toDo); // WE NEED TO EMPTY THE LIST WHEN THE USER CLICKS POPULATE
                // set the adapter
                listView.setAdapter(new CustomAdapterListView(getContext(),CreateQuest.todosList));

                // empty fields
                addDesc.setText("");
                addTime.setText("");
                addMoney.setText("");


            }
        });
        */


    }



    /*
    public class CustomAdapterListView extends BaseAdapter{

        List<ToDo> list;
        Context context;
        private LayoutInflater inflater=null;

        public CustomAdapterListView(Context context, List<ToDo> list){

            this.context = context;
            this.list = new ArrayList<ToDo>();
            this.list = list;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder
        {
            TextView descText;
            TextView costText;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder;
            View rowView = convertView;

            if(convertView == null){

                rowView = inflater.inflate(R.layout.todo_list_item, null);
                holder = new Holder();
                holder.descText=(TextView) rowView.findViewById(R.id.todoDesc);
                holder.costText=(TextView) rowView.findViewById(R.id.todoCost);
                rowView.setTag(holder);
            }else{
                holder = (Holder)rowView.getTag();


            }

            ToDo toDo = list.get(position);

            holder.descText.setText(toDo.getDesc());
            String mo = String.valueOf(toDo.getMoney());
            String placeholder = mo+"RM"+" /"+toDo.getTime()+"H";
            holder.costText.setText(placeholder);


            return rowView;
        }
    }
    */
}
