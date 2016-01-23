package com.skuares.studio.quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.firebase.client.Firebase;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 12/19/2015.
 */
public class QuestCard implements Serializable{


    private String questImage;
    private String questTitle;
    private String authorId;

    // used when retrieving user image and user username
    private String questUsername; // generated dynamically
    private String questUserImage; // generated dynamically... receive it as a string it is okay

    private String questDescription;

    // user id as a key in the map, value boolean!!!
    private Map<String,Object> takers;
    // user id as a key in the map, value is boolean
    private Map<String,Object> joiners;

    private String questCost;

    // tracker for likes, takers, and followers
    private double numberOfLikes;
    private double numberOfTakers;
    private double numberOfFollowers;
    private List<ToDo> todos;

    private String questKey;

    private Map<String,Object> usersWhoLiked;


    // another constructor to use when we get the user's image and user's username (for stream)
    public  QuestCard(String questImage, String questTitle,String authorId,
                      String questUsername,
                      String questUserImage,
                      String questDescription, String questCost,List<ToDo> todos,

                      String questKey,
                      Map<String,Object> usersWhoLiked,
                      Map<String, Object> takers,
                      double numberOfLikes,
                      double numberOfTakers,
                      double numberOfFollowers,
                      Map<String,Object> joiners){

        this.questImage = questImage;
        this.questTitle = questTitle;
        this.authorId = authorId;


        // must get it from users reference in firebase
        this.questUsername = questUsername;
        this.questUserImage = questUserImage;


        this.questDescription = questDescription;
        this.questCost = questCost;

        this.numberOfLikes = 0;
        this.numberOfTakers = 0;
        this.numberOfFollowers = 0;


        this.joiners = joiners;


        this.questKey = questKey;
        this.usersWhoLiked = usersWhoLiked;


        this.takers = takers;

        this.numberOfLikes = numberOfLikes;
        this.numberOfTakers = numberOfTakers;
        this.numberOfFollowers = numberOfFollowers;

        this.todos = new ArrayList<ToDo>();
        this.todos = todos;

    }


    public QuestCard(){}

    // to save the quest
    public QuestCard(Bitmap questImage, String questTitle,String authorId,
                     String questDescription, String questCost,List<ToDo> todos,String questKey){

        this.questImage = bitmapToString(questImage);
        this.questTitle = questTitle;
        this.authorId = authorId;

        // must get it from users reference in firebase
        //this.questUsername = questUsername;
        //this.questUserImage = questUserImage;

        this.questDescription = questDescription;
        this.questCost = questCost;

        this.numberOfLikes = 0;
        this.numberOfTakers = 0;
        this.numberOfFollowers = 0;

        // initialize the maps
        takers = new HashMap<String,Object>();
        joiners = new HashMap<String, Object>();

        usersWhoLiked = new HashMap<String, Object>();

        this.todos = new ArrayList<ToDo>();
        this.todos = todos;
        this.questKey = questKey;
    }

    public QuestCard(@JsonProperty("questImage") String questImage,
                     @JsonProperty("questTitle") String questTitle,
                     @JsonProperty("authorId") String authorId,

                     @JsonProperty("questDescription") String questDescription,
                     @JsonProperty("questCode") String questCost,
                     @JsonProperty("numberOfLikes") double numberOfLikes,
                     @JsonProperty("numberOfTakers") double numberOfTakers,
                     @JsonProperty("numberOfFollowers") double numberOfFollowers,

                     @JsonProperty("todos") List<ToDo> todos,
                     @JsonProperty("questKey") String questKey,
                     @JsonProperty("usersWhoLiked") Map<String,Object> usersWhoLiked,
                     @JsonProperty("takers") Map<String, Object> takers,
                     @JsonProperty("joiners") Map<String,Object> joiners){


        this.questImage = questImage;
        this.questTitle = questTitle;
        this.authorId = authorId;
       // this.questUsername = questUsername;
        //this.questUserImage = questUserImage;
        this.questDescription = questDescription;
        this.questCost = questCost;

        this.numberOfLikes = numberOfLikes;
        this.numberOfTakers = numberOfTakers;
        this.numberOfFollowers = numberOfFollowers;

        // initialize the maps
        this.takers = takers;


        this.questKey = questKey;

        this.usersWhoLiked = usersWhoLiked;

        this.todos = todos;

        this.joiners = joiners;
    }

    public Map<String, Object> getUsersWhoLiked() {
        return usersWhoLiked;
    }

    public Map<String, Object> getTakers() {
        return takers;
    }

    public void addUserLike(Firebase questRef, String authorId){
        if(questRef != null && authorId != null){
            // update the usersWhoLiked
            if(this.usersWhoLiked != null){
                this.usersWhoLiked.put(authorId,true);
            }else{
                // it is null
                // initialize it
                // add a value to it
                this.usersWhoLiked = new HashMap<String, Object>();
                this.usersWhoLiked.put(authorId,true);
            }



            // save to firebase
            Map<String,Object> map = new HashMap<String, Object>();
            map.put(authorId,true);
            questRef.child("usersWhoLiked").updateChildren(map);
        }
    }

    public void addTaker(Firebase questRef, String authorId){
        if(questRef != null && authorId != null){
            if(this.takers != null){
                this.takers.put(authorId,authorId);
            }else{
                this.takers = new HashMap<String, Object>();
                this.takers.put(authorId, authorId);
            }

            // save to firebase
            Map<String,Object> map = new HashMap<String, Object>();
            map.put(authorId,authorId);
            questRef.child("takers").updateChildren(map);

        }
    }



    public String getQuestKey() {
        return questKey;
    }

    public List<ToDo> getTodos() {
        return todos;
    }

    public Map<String, Object> getJoiners() {
        return joiners;
    }

    public void setQuestImage(Bitmap bitmap,Firebase questRef){
        if(bitmap != null && questRef != null){

            // convert bitmap to string
            String stringImage = bitmapToString(bitmap);
            this.questImage = stringImage;
            // save it
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("questImage",stringImage);
            questRef.updateChildren(map);
        }
    }

    // ONLY FOR ADAPTER
    public String getQuestUsername() {
        return questUsername;
    }
    // ONLY FOR ADAPTER
    public String getQuestUserImage() {

        return questUserImage;
    }

    public void setQuestTitle(String title, Firebase questRef){

        if(title != null && questRef != null){
            this.questTitle = title;
            // save it
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("questTitle",title);
            questRef.updateChildren(map);
        }
    }

    /*


    author id cannot be changed
    quest username cannot be changed
    quest user image cannot be changed
     */

    public void setQuestDescription(String description, Firebase questRef){

        if(description != null && questRef != null){

            this.questDescription = description;
            // save it
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("questDescription",description);
            questRef.updateChildren(map);
        }

    }

    public void setQuestCost(String cost, Firebase questRef){

        if(cost != null && questRef != null){

            this.questCost = cost;
            // save it
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("questCost",cost);
            questRef.updateChildren(map);
        }
    }





    // working with numbers ... WE SHOULD USE TRANSACTION LATERZZZZ
    public void increaseLikes(){
        numberOfLikes++;
    }


    public void increaseTakers(){
        numberOfTakers++;
    }

    public String getQuestImage() {
        return questImage;
    }

    public String getQuestTitle() {
        return questTitle;
    }

    public String getAuthorId() {
        return authorId;
    }

    //public String getQuestUsername() {
      //  return questUsername;
    //}

    //public String getQuestUserImage() {
      //  return questUserImage;
    //}

    public String getQuestDescription() {
        return questDescription;
    }

    public String getQuestCost() {
        return questCost;
    }

    public double getNumberOfLikes() {
        return numberOfLikes;
    }

    public double getNumberOfTakers() {
        return numberOfTakers;
    }

    public double getNumberOfFollowers() {
        return numberOfFollowers;
    }

    private String bitmapToString(Bitmap bitmap){


        // output stream to write for when using compress
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        bitmap.recycle();
        // convert the output stream to byte array
        byte[] bytes = outputStream.toByteArray();
        // convert byte[] to string
        String stringImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        return stringImage;
    }

    public Bitmap stringToBitmap(String stringImage){

        Bitmap bitmap = null;

        // decode the string
        try {
            byte[] bytes = Base64.decode(stringImage,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }catch (Exception e){

            Log.e("ErrorImage", e.getMessage());
            return null;
        }

    }




}
