package com.skuares.studio.quest;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by salim on 1/23/2016.
 */
public class RVQPictureAdapter extends RecyclerView.Adapter<RVQPictureAdapter.ViewHolder> {


    Context context;
    List<QPicture> qPictures;
    LoadImageFromString loadImageFromString;
    public RVQPictureAdapter(Context context, List<QPicture> qPictures, LoadImageFromString loadImageFromString){

        this.context = context;
        this.qPictures = qPictures;

        this.loadImageFromString = loadImageFromString;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardImages;
        ImageView horiImage;

        public ViewHolder(View itemView) {
            super(itemView);

            cardImages = (CardView)itemView.findViewById(R.id.cardImages);
            horiImage = (ImageView) itemView.findViewById(R.id.horiImage);


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hori_images,parent,false);
        // pass the view to viewHolder and create a view holder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(qPictures != null){

            loadImageFromString.loadBitmapFromString(qPictures.get(position).getStringPicture(),holder.horiImage);
        }
    }

    @Override
    public int getItemCount() {
        if(qPictures != null){
            return qPictures.size();
        }else{
            return 0;
        }

    }


}
