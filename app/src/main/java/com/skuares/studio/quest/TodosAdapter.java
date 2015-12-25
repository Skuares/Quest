package com.skuares.studio.quest;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by salim on 12/24/2015.
 */
public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.ViewTodos> {


    public static class ViewTodos extends RecyclerView.ViewHolder{

        CardView cardView;
        ImageButton addPic;
        ImageButton viewPic;
        TextView todosCost;
        TextView todosDesc;
        CheckBox checkBox;

        public ViewTodos(View view){
            super(view);

            cardView = (CardView)view.findViewById(R.id.todosCard);
            addPic = (ImageButton)view.findViewById(R.id.addPic);
            viewPic = (ImageButton)view.findViewById(R.id.viewPic);
            todosCost = (TextView)view.findViewById(R.id.todosCost);
            todosDesc = (TextView)view.findViewById(R.id.todosDescription);
            checkBox = (CheckBox)view.findViewById(R.id.todosCheckbox);
        }

    }


    Context context;
    List<ToDo> list;
    public TodosAdapter(Context context, List<ToDo> toDos){
        this.context = context;
        this.list = toDos;
    }

    @Override
    public ViewTodos onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todos_card_layout,parent,false);

        ViewTodos viewTodos = new ViewTodos(view);


        return viewTodos;
    }

    @Override
    public void onBindViewHolder(ViewTodos holder, int position) {
        // assign the values
        String cost = ""+list.get(position).getMoney()+"RM"+"/"+list.get(position).getTime()+"H";
        holder.todosDesc.setText(list.get(position).getDesc());
        holder.todosCost.setText(cost);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
