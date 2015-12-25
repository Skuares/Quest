package com.skuares.studio.quest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

/**
 * Created by salim on 12/23/2015.
 */
public class TodosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<ToDo> list;
    private DataWrapperTodo dataWrapperTodo;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todos_activity_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbarTodos);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // retreive data
        dataWrapperTodo = (DataWrapperTodo) getIntent().getSerializableExtra("todos");
        list = dataWrapperTodo.getToDos();
        //Log.e("hellooo", String.valueOf(list.get(0)));



        // set recyle stuff
        recyclerView = (RecyclerView) findViewById(R.id.recycleviewTodos);
        recyclerView.setHasFixedSize(true); // only one view

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up adapter
        adapter = new TodosAdapter(this,list);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
