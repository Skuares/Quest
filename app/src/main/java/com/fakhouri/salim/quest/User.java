package com.fakhouri.salim.quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.firebase.client.Firebase;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salim on 12/16/2015.
 */
public class User {


    private final String quester = "I am a Quester";
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String description;
    private int age;
    private String userImage;

    // have to add them dynamically
    /*
    - Null Not Friend
    1- Pending state (wait for response)
    2- Friend
     */
    private Map<String,Object> friends;


    public User(){}



    // constructor
    public User(String firstName, String lastName, String username, String email, int age, Bitmap localBitmap){

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.age = age;
        this.description = quester;
        // if this contsructor is used, use placeholder image
        //this.userImage = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.placeholderuser);
        this.userImage = bitmapToString(localBitmap);
        // when user is registered he has no friends
        this.friends = new HashMap<String,Object>();
        this.friends.put("empty", 0); // HAS TO HAVE FIRST FIELD AS EMPTY OTHERWISE IT WON'T BE CREATED
    }


    // dummy constructor NO LONGER IN USE , WE HAD TO ADD FRIEND FIELD
    /*
    public User(@JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName,
                @JsonProperty("username") String username,
                @JsonProperty("email") String email,
                @JsonProperty("age") int age,
                @JsonProperty("description") String description,
                @JsonProperty("userImage") String bitmapString){
        // for firebase
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.age = age;
        this.description = description;
        // if this contsructor is used, use placeholder image
        //this.userImage = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.placeholderuser);
        this.userImage = bitmapString;
    }
    */

    // another firebase constructor to receive friends
    public User(@JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName,
                @JsonProperty("username") String username,
                @JsonProperty("email") String email,
                @JsonProperty("age") int age,
                @JsonProperty("description") String description,
                @JsonProperty("userImage") String bitmapString,
                @JsonProperty("friends") Map<String,Object> friends){
        // for firebase
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.age = age;
        this.description = description;
        // if this contsructor is used, use placeholder image
        //this.userImage = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.placeholderuser);
        this.userImage = bitmapString;
        this.friends = friends;
    }

    public void addFriend(String id, int state,Firebase userRef) {

        if(id != null && (state == 1 || state == 2 || state == 3)){
            this.friends.put(id,state);
            userRef.updateChildren(this.friends);
        }

    }

    public Map<String, Object> getFriends() {
        return friends;
    }

    /* NO LONGER USERFUL.. IT HAS TO BE A HASHMAP , THE ADD FREIND HAS 3 STATES
    public void addFriends(String friendsUid,Firebase userRef) {
        friends.add(friendsUid);
        // save to firebase
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("friends",friends);

        userRef.updateChildren(map);
    }
    */

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
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;
        }catch (Exception e){

            Log.e("ErrorImage", e.getMessage());
            return null;
        }

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description, Firebase userRef) {
        if(description != null && userRef != null){

            this.description = description;
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("description",description);
            userRef.updateChildren(map);
        }

    }


    // create getters

    public String getFirstName(){

        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getUsername(){
        return username;
    }

    public int getAge() {
        return age;
    }

    public String getUserImage() {

        return userImage;
    }


    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName, Firebase userRef) {
        if(firstName != null){
            this.firstName = firstName;
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("firstName",firstName);
            userRef.updateChildren(map);

        }else{
            //
            Log.e("UserError","first name is null");
        }

    }

    public void setUsername(String username, Firebase userRef) {
        if(username != null){
            this.username = username;
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("username",username);
            userRef.updateChildren(map);
        }else{
            Log.e("UserError","user name is null");
        }

    }

    public void setLastName(String lastName, Firebase userRef) {
        if(lastName != null){

            this.lastName = lastName;
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("lastName",lastName);
            userRef.updateChildren(map);
        }else{
            Log.e("UserError","last name is null");
        }

    }




    public void setEmail(String email, Firebase userRef) {
        if(email != null){
            this.email = email;
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("email",email);
            userRef.updateChildren(map);
        }else{
            Log.e("UserError","email name is null");
        }

    }


    public void setAge(int age, Firebase userRef) {
        if(age > 0){
            this.age = age;
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("age",age);
            userRef.updateChildren(map);
        }else{
            Log.e("UserError","age value is not right");
        }

    }

    class SaveImageInBackground extends AsyncTask<Object,Void,String>{

        private String data;
        private Firebase firebase;
        @Override
        protected String doInBackground(Object... params) {

            data = (String)params[0];
            firebase = (Firebase)params[1];
            // compress it
            Bitmap bitmap = LoadImageFromPath.decodeSampledBitmapFromPath(data, 100, 100);
            // convert to string
            String stringUserImage = bitmapToString(bitmap);

            // save
            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("userImage",stringUserImage);
            firebase.updateChildren(map);

            return stringUserImage;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // set the value
            userImage = s;

        }
    }

    public void setUserImage(String filePath, Firebase userRef) {

        // call bitmapToString
        if(filePath != null && userRef != null){

            // call the async
            SaveImageInBackground  saveImageInBackground = new SaveImageInBackground();
            saveImageInBackground.execute(filePath,userRef);


        }else{
            Log.e("UserError","bitmap is null");
        }

    }


}
