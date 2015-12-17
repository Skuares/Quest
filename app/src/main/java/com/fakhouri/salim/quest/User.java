package com.fakhouri.salim.quest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.firebase.client.Firebase;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by salim on 12/16/2015.
 */
public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String email;

    private int age;
    private String userImage;

    public User(){}



    // constructor
    public User(String firstName, String lastName, String username, String email, int age, Bitmap localBitmap){

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.age = age;

        // if this contsructor is used, use placeholder image
        //this.userImage = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.placeholderuser);
        this.userImage = bitmapToString(localBitmap);
    }


    // dummy constructor

    public User(@JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName,
                @JsonProperty("username") String username,
                @JsonProperty("email") String email,
                @JsonProperty("age") int age,
                @JsonProperty("userImage") String bitmapString){
        // for firebase
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.age = age;

        // if this contsructor is used, use placeholder image
        //this.userImage = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.placeholderuser);
        this.userImage = bitmapString;
    }


    // constructor with image ...... NOT DONE YET
    /*
    public User(String firstName,String lastName, String username, String email, int age,String userImage){

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.age = age;

        this.userImage = userImage;


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


    public void setUserImage(Bitmap bitmapUserImage, Firebase userRef) {

        // call bitmapToString
        if(bitmapUserImage != null){
            String stringUserImage = bitmapToString(bitmapUserImage);
            this.userImage = stringUserImage;

            // save to firebase
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("userImage",userImage);
            userRef.updateChildren(map);
        }else{
            Log.e("UserError","bitmap is null");
        }

    }
}
