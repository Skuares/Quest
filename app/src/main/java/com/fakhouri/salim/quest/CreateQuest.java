package com.fakhouri.salim.quest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.fakhouri.salim.quest.UserProfile.getPath;

/**
 * Created by salim on 12/19/2015.
 */
public class CreateQuest extends AppCompatActivity {


    public static List<ToDo> todosList;
    private static int REQUEST_CODE= 1;

    private Toolbar toolbar;
    Bitmap questImage = null;
    ImageView questImageAdd;

    Button publish;

    private Firebase ref;
    private Firebase questRef;
    private Firebase newQuestRef;

    TextView questUsernameAdd; // dynamic
    ImageView questUserImageAdd; // dynamic

    EditText questTitleAdd;
    EditText questDescriptionAdd;
    Button addTodos;

    private User userQuest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_quest_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ref = new Firebase(getResources().getString(R.string.firebaseUrl));

        questRef = ref.child("Quests");
        newQuestRef = questRef.push();

        // initialize the list
        todosList = new ArrayList<ToDo>();
        // reference
        questImageAdd = (ImageView)findViewById(R.id.questImageAdd);
        questUserImageAdd = (ImageView)findViewById(R.id.questUserImageAdd);
        questUsernameAdd = (TextView)findViewById(R.id.questUserNameAdd);
        questDescriptionAdd = (EditText)findViewById(R.id.questDescriptionAdd);
        questTitleAdd = (EditText)findViewById(R.id.questTitleAdd);
        publish = (Button)findViewById(R.id.publish);

        addTodos = (Button)findViewById(R.id.addTodos);
        addTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // populate the fragment
                Fragment fragment = new TodoFragment();
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.relative, fragment, "TodoFragment");
                transaction.addToBackStack("todo");
                transaction.commit();
                //Toast.makeText(CreateQuest.this,"clicked",Toast.LENGTH_LONG).show();

            }
        });
        // get user
        if(MainActivity.myUser != null){
            userQuest = MainActivity.myUser;

            // get user image and get username

            questUserImageAdd.setImageBitmap(MainActivity.userImageStatic);
            questUsernameAdd.setText("By " + userQuest.getUsername());

            // now set listener to questImageAdd
            questImageAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // make an intent and add picture
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    CreateQuest.this.startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), REQUEST_CODE);
                }
            });

            publish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get info
                    String questDesc = questDescriptionAdd.getText().toString();
                    String questTitle = questTitleAdd.getText().toString();
                    String userImage = userQuest.getUserImage();
                    String username = userQuest.getUsername();

                    // check list
                    if(todosList == null || todosList.size() == 0){
                        Toast.makeText(CreateQuest.this,"You must add a todos",Toast.LENGTH_LONG).show();
                        return;
                    }

                    // check image
                    if(questImage == null ){
                        Toast.makeText(CreateQuest.this,"You must choose a cover photo",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(questDesc.equals("")){
                        Toast.makeText(CreateQuest.this,"You must fill the description",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(questTitle.equals("")){
                        Toast.makeText(CreateQuest.this,"You must type a title",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(MainActivity.uid == null){
                        Toast.makeText(CreateQuest.this,"You must authenticate",Toast.LENGTH_LONG).show();
                        return;
                    }

                    // we need to meausre the cost
                    double sumMoney = 0;
                    int hours = 0;
                    // loop through the list
                    for(int i = 0; i < todosList.size(); i++){

                        // get the money
                        sumMoney += todosList.get(i).getMoney();
                        // get the hours
                        // convert to int
                        int convert = Integer.parseInt(todosList.get(i).getTime());
                        hours += convert;
                    }

                    String cost = ""+sumMoney+"RM"+" /"+hours+" H";
                    // setup quest,, ADJUST THE CLASS FIRST BY ADDING LIST OF TODOS TO THE CLASS
                    QuestCard questCard = new QuestCard(questImage,questTitle,MainActivity.uid,username,userImage,questDesc,cost,todosList);
                    // SAVE TO FIREBASE
                    newQuestRef.setValue(questCard);


                    // add the list of todos to the questObject
                    newQuestRef.child("todos").setValue(todosList);

                    // DO WE NEED TO SET UP THE FOLLOWERS MAP AND TRAKERS MAP FROM NOW!!!!
                    // YES WE DO BECAUSE I EXPECT TO RECEIVE THEM IN JSON PROPERTY

                    // DID NOT WORK OUT IT HAS TO HAVE SOME DATA
                    /*
                    // it is a map let try making it empty
                    Map<String,Object> takersMap = new HashMap<String, Object>();
                    // empty for now
                    newQuestRef.child("takers").setValue(takersMap);

                    // let's set up the same for followers
                    Map<String,Object> followersMap = new HashMap<String, Object>();
                    // empty for now
                    newQuestRef.child("followers").setValue(takersMap);
                    */

                    // end the task and give feedback
                    Toast.makeText(CreateQuest.this,"Success",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });



        }else{
            // not allowed
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

                if (Build.VERSION.SDK_INT >= 19) {

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Uri selectedImage = data.getData();


                    InputStream input;
                    Bitmap bitmap;
                    String picturePath = getPath(CreateQuest.this, selectedImage);
                    try {
                        input = CreateQuest.this.getContentResolver().openInputStream(
                                selectedImage);
                        bitmap = BitmapFactory.decodeStream(input);
                        questImage = bitmap;
                        questImageAdd.setImageBitmap(bitmap);



                    } catch (FileNotFoundException e1) {
                        Toast.makeText(CreateQuest.this, e1.getMessage(), Toast.LENGTH_LONG).show();

                    }
                } else {

                    DoInBackgroud hello = new DoInBackgroud();
                    hello.execute(data);
                }


            }


        } catch (Exception e) {
            Toast.makeText(CreateQuest.this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private class DoInBackgroud extends AsyncTask<Intent, Void, String> {

        Intent data;

        @Override
        protected String doInBackground(Intent... params) {
            // get the image from data
            data = params[0];
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            //Log.d("hello", "Android Version is 18 below");
            Cursor cursor = CreateQuest.this.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String picturePath = cursor.getString(columnIndex);

                cursor.close();

                return picturePath;
            }

            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(s);
                questImage = bitmap;
                questImageAdd.setImageBitmap(bitmap);
                //saveImageToFirebase(s);


            } else {
                Toast.makeText(CreateQuest.this, "path is wrong", Toast.LENGTH_SHORT).show();
            }
        }

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
