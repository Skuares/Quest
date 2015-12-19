package com.fakhouri.salim.quest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.fakhouri.salim.quest.UserProfile.getPath;

/**
 * Created by salim on 12/19/2015.
 */
public class CreateQuest extends AppCompatActivity {

    private static int REQUEST_CODE= 1;

    private Toolbar toolbar;
    Bitmap questImage = null;
    ImageView questImageAdd;

    TextView questUsernameAdd; // dynamic
    ImageView questUserImageAdd; // dynamic

    EditText questTitleAdd;
    EditText questDescriptionAdd;

    private User userQuest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_quest_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // reference
        questImageAdd = (ImageView)findViewById(R.id.questImageAdd);
        questUserImageAdd = (ImageView)findViewById(R.id.questUserImageAdd);
        questUsernameAdd = (TextView)findViewById(R.id.questUserNameAdd);
        questDescriptionAdd = (EditText)findViewById(R.id.questDescriptionAdd);
        questTitleAdd = (EditText)findViewById(R.id.questTitleAdd);

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
