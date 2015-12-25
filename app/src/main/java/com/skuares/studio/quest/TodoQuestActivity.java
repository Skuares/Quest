package com.skuares.studio.quest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Created by salim on 12/25/2015.
 */
public class TodoQuestActivity extends AppCompatActivity {

    ImageButton add;
    ImageButton back;

    EditText addDesc;
    EditText addTime;
    EditText addMoney;

    ToDo toDo;
    ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_fragment_layout);

        add = (ImageButton)findViewById(R.id.add);
        back = (ImageButton)findViewById(R.id.back);

        addDesc = (EditText)findViewById(R.id.addDescription);
        addTime = (EditText)findViewById(R.id.addTime);
        addMoney = (EditText)findViewById(R.id.addMoney);

        listView = (ListView)findViewById(R.id.listView);


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
                listView.setAdapter(new CustomAdapterListView(TodoQuestActivity.this,CreateQuest.todosList));

                // empty fields
                addDesc.setText("");
                addTime.setText("");
                addMoney.setText("");


            }
        });



    }


    public class CustomAdapterListView extends BaseAdapter {

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
}


