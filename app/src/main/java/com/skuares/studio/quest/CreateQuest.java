package com.skuares.studio.quest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.skuares.studio.quest.UserProfile.getPath;

/**
 * Created by salim on 12/19/2015.
 */
public class CreateQuest extends AppCompatActivity{


    public static List<ToDo> todosList;
    private static int REQUEST_CODE= 1;

    private Toolbar toolbar;
    Bitmap questImage = null;
    ImageView questImageAdd;

    Button publish;

    private RelativeLayout relativeLayout;

    // boolean to check if user has set an image
    boolean userChooseImage = false;

    LoadImageFromPath loadImageFromPath;

    /*
    Todos Dialg layout
     */
    protected GoogleApiClient mGoogleApiClient;
    ToDo toDo;
    EditText addMoneyDialog,addTimeDialog,addDescriptionDialog;
    ImageButton addDialog;
    ListView listViewDialog;
    PlaceAutocompleteFragment autocompleteFragment;
    MaterialDialog dialog;


    private String pathString = null;

    private Firebase ref;
    private Firebase questRef;
    private Firebase newQuestRef;

    TextView questUsernameAdd; // dynamic
    ImageView questUserImageAdd; // dynamic

    EditText questTitleAdd;
    EditText questDescriptionAdd;


    private User userQuest;

    String questKey;

    String cost = "";
    String questDesc = "";
    String questTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_quest_layout);

       // buildGoogleApiClient();

        relativeLayout = (RelativeLayout)findViewById(R.id.appGone);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ref = new Firebase(getResources().getString(R.string.firebaseUrl));

        questRef = ref.child("Quests");
        newQuestRef = questRef.push();
        questKey = newQuestRef.getKey();


        loadImageFromPath = new LoadImageFromPath(this);
        // initialize the list
        todosList = new ArrayList<ToDo>();
        // reference
        questImageAdd = (ImageView)findViewById(R.id.questImageAdd);
        questUserImageAdd = (ImageView)findViewById(R.id.questUserImageAdd);
        questUsernameAdd = (TextView)findViewById(R.id.questUserNameAdd);
        questDescriptionAdd = (EditText)findViewById(R.id.questDescriptionAdd);
        questTitleAdd = (EditText)findViewById(R.id.questTitleAdd);
        publish = (Button)findViewById(R.id.publish);



        // get user
        if(MainActivity.myUser != null){
            userQuest = MainActivity.myUser;

            // get user image and get usernamse

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
                    questDesc = questDescriptionAdd.getText().toString();
                    questTitle = questTitleAdd.getText().toString();

                    // check list
                    if(todosList == null || todosList.size() == 0){
                        Toast.makeText(CreateQuest.this,"You must add a todos",Toast.LENGTH_LONG).show();
                        return;
                    }

                    // check image

                    if(userChooseImage == false ){
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

                    cost = ""+sumMoney+"RM"+" /"+hours+" H";

                    // setup quest
                    GetCompressedImageThenSave getCompressedImageThenSave = new GetCompressedImageThenSave();
                    getCompressedImageThenSave.execute(pathString,100,100);


                    /*
                    newQuestRef.setValue(questCard);
                    // add the list of todos to the questObject
                    newQuestRef.child("todos").setValue(todosList);
                    */



                    // end the task and give feedback
                    Toast.makeText(CreateQuest.this,"Success",Toast.LENGTH_SHORT).show();
                    // make an intent first
                    Intent intent = new Intent(CreateQuest.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                    startActivity(intent);

                }
            });



        }else{
            // not allowed
        }

    }

    /*

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connection true",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed",Toast.LENGTH_SHORT).show();

    }
    */


    /*
    @Override
    protected void onStart() {
        super.onStart();

        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            Log.v("Google API","Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    */




    class GetCompressedImageThenSave extends AsyncTask<Object,Void,Bitmap>{

        private String path;
        private int reqW = 100;
        private int reqH = 100;


        @Override
        protected Bitmap doInBackground(Object... params) {
            path = (String)params[0];
            reqW = (int) params[1];
            reqH = (int) params[2];

            Bitmap bitmap = LoadImageFromPath.decodeSampledBitmapFromPath(path,reqW,reqH);


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            questImage = bitmap;

            // now call save quest from here


            QuestCard questCard = new QuestCard(questImage,questTitle,MainActivity.uid,questDesc,cost,todosList,questKey);
            // SAVE TO FIREBASE
            SaveQuest saveQuest = new SaveQuest();
            saveQuest.execute(questCard,newQuestRef);

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
                    loadImageFromPath.loadBitmap(picturePath, questImageAdd);
                    userChooseImage = true;
                    pathString = picturePath;

                    /*
                    try {
                        input = CreateQuest.this.getContentResolver().openInputStream(
                                selectedImage);
                        bitmap = BitmapFactory.decodeStream(input);
                        questImage = bitmap;
                        questImageAdd.setImageBitmap(bitmap);



                    } catch (FileNotFoundException e1) {
                        Toast.makeText(CreateQuest.this, e1.getMessage(), Toast.LENGTH_LONG).show();

                    }
                    */
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

    private class SaveQuest extends AsyncTask<Object,Void,Void>{

        private QuestCard questCard = null;
        private Firebase firebaseQuest = null;

        @Override
        protected Void doInBackground(Object... params) {
            questCard = (QuestCard)params[0];
            firebaseQuest = (Firebase)params[1];

            firebaseQuest.setValue(questCard);
            // add todos
            firebaseQuest.child("todos").setValue(todosList);


            return null;
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

                loadImageFromPath.loadBitmap(s,questImageAdd);

                userChooseImage = true;
                pathString = s;

                /*
                Bitmap bitmap = BitmapFactory.decodeFile(s);
                questImage = bitmap;
                questImageAdd.setImageBitmap(bitmap);
                */



            } else {
                Toast.makeText(CreateQuest.this, "path is wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){

            finish();
            return true;
        }

        if(item.getItemId() == R.id.action_todos){
            // do todos stuff
            // create a custum dialog
            boolean wrapInScrollView = false;
            dialog = new MaterialDialog.Builder(this)
                    .title("Add Todo")
                    .customView(R.layout.todos_dialog_input, wrapInScrollView)
                    .positiveText("Done")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            // do stuff
                            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                        }
                    })
                    .build();

            // do referencing and todos stuff
            if(dialog != null){

                final APlace[] aPlace = {null};
                autocompleteFragment = (PlaceAutocompleteFragment)

                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        //  Get info about the selected place.
                        aPlace[0] = new APlace(place.getName().toString(),place.getAddress().toString(),
                                place.getId(),place.getLatLng().latitude,place.getLatLng().longitude);

                        //Toast.makeText(CreateQuest.this,"GOT IT Place: +"+  place.getName(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Status status) {
                        //  Handle the error.
                        Toast.makeText(CreateQuest.this,"GOT IT Error: +"+ status,Toast.LENGTH_SHORT).show();
                        Log.e("GOT IT ERROR", "An error occurred: " + status);
                    }
                });


                addDescriptionDialog = (EditText) dialog.getCustomView().findViewById(R.id.addDescriptionDialog);
                addTimeDialog = (EditText) dialog.getCustomView().findViewById(R.id.addTimeDialog);
                addMoneyDialog = (EditText) dialog.getCustomView().findViewById(R.id.addMoneyDialog);
                addDialog = (ImageButton)dialog.getCustomView().findViewById(R.id.addDialog);
                listViewDialog = (ListView)dialog.getCustomView().findViewById(R.id.listViewDialog);


                addDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get all the values
                        String desc = addDescriptionDialog.getText().toString();
                        String time = addTimeDialog.getText().toString();
                        String money = addMoneyDialog.getText().toString();

                        if(desc.equals("") || time.equals("") || money.equals("")){
                            Toast.makeText(CreateQuest.this,"Please Fill In The Fields",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double moneys = Double.parseDouble(money);
                        // create a to--do object
                        APlace aPlace1 = aPlace[0];
                        if(aPlace1 != null){
                            toDo = new ToDo(desc,time,moneys,aPlace1);
                            // add it to the list
                            todosList.add(toDo);
                            // call adapter
                            listViewDialog.setAdapter(new CustomAdapterListView(CreateQuest.this,todosList));

                            // empty fields
                            addDescriptionDialog.setText("");
                            addTimeDialog.setText("");
                            addMoneyDialog.setText("");
                        }else{
                            Toast.makeText(CreateQuest.this,"You Must Choose a Place",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }

            dialog.show();



            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private class CustomAdapterListView extends BaseAdapter {

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
            TextView placeText;
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
                holder.placeText = (TextView)rowView.findViewById(R.id.todoPlace);
                rowView.setTag(holder);
            }else{
                holder = (Holder)rowView.getTag();
            }

            ToDo toDo = list.get(position);

            holder.descText.setText(toDo.getDesc());
            String mo = String.valueOf(toDo.getMoney());
            String placeholder = mo+"RM"+" /"+toDo.getTime()+"H";
            APlace aPlace = toDo.getaPlace();
            holder.placeText.setText(aPlace.getPlaceAddress());
            holder.costText.setText(placeholder);
            return rowView;
        }
    }

}
