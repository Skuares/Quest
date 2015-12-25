package com.skuares.studio.quest;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by salim on 12/18/2015.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // reference what do you have in the layout
        CardView cardViewUserInfo;
        CardView cardViewDescription;

        TextView firstNameValue;
        TextView lastNameValue;
        TextView usernameValue;
        TextView ageValue;
        TextView emailValue;

        TextView descriptionValue;


        public ViewHolder(View view) {
            super(view);

            cardViewUserInfo = (CardView)view.findViewById(R.id.userInfo);
            cardViewDescription = (CardView)view.findViewById(R.id.descCard);

            firstNameValue = (TextView)view.findViewById(R.id.firstnameCardValue);
            lastNameValue = (TextView)view.findViewById(R.id.lastnameCardValue);
            usernameValue = (TextView)view.findViewById(R.id.usernameCardValue);
            ageValue = (TextView)view.findViewById(R.id.ageCardValue);
            emailValue = (TextView)view.findViewById(R.id.emailCardValue);

            descriptionValue = (TextView)view.findViewById(R.id.descValue);

        }
    }

    private User myUser;
    // data is only one user

    public UserAdapter(User user) {
        myUser = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cards_profile,parent,false);
        // pass the view to viewHolder and create a view holder
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // specify the values
        if(myUser != null){
            holder.firstNameValue.setText(myUser.getFirstName());
            holder.lastNameValue.setText(myUser.getLastName());
            holder.usernameValue.setText(myUser.getUsername());
            String sAge = String.valueOf(myUser.getAge());
            holder.ageValue.setText(sAge);
            holder.emailValue.setText(myUser.getEmail());
            holder.descriptionValue.setText(myUser.getDescription());
        }

    }

    @Override
    public int getItemCount() {
        return 1;
    }


}
