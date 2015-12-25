package com.skuares.studio.quest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by salim on 12/18/2015.
 */
public class EditProfile extends AppCompatActivity {

    Button doChangeFirst,doChangeLast,doChangeAge,doChangeUsername,doChangeDescription;
    EditText changeFirst,changeLast,changeAge,changeUsername,changeDescription;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profile_layout);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        changeFirst = (EditText)findViewById(R.id.changeFirst);// firstname for now
        doChangeFirst = (Button)findViewById(R.id.doChangeFirst);
        doChangeFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check user
                if (MainActivity.myUser != null) {
                    // get the value of the editText
                    String value = changeFirst.getText().toString();
                    if(value.equals("")){
                        Toast.makeText(EditProfile.this,"Cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MainActivity.myUser.setFirstName(value, MainActivity.userRef);
                    Toast.makeText(EditProfile.this, "first name has been changed", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("change ", "error user is null");
                }
            }
        });

        changeLast = (EditText)findViewById(R.id.changeLast);
        changeUsername = (EditText)findViewById(R.id.changeUsername);
        changeAge = (EditText)findViewById(R.id.changeAge);
        changeDescription = (EditText)findViewById(R.id.changeDescription);

        doChangeLast = (Button)findViewById(R.id.doChangeLast);
        doChangeUsername = (Button)findViewById(R.id.doChangeUsername);
        doChangeAge = (Button)findViewById(R.id.doChangeAge);
        doChangeDescription = (Button)findViewById(R.id.doChangeDescription);

        doChangeDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.myUser != null){
                    // get the value of the editText
                    String value = changeDescription.getText().toString();
                    if(value.equals("")){
                        Toast.makeText(EditProfile.this,"Cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MainActivity.myUser.setDescription(value, MainActivity.userRef);
                    Toast.makeText(EditProfile.this,"Description has been changed",Toast.LENGTH_LONG).show();
                }else{
                    Log.e("change ","error user is null");
                }
            }
        });

        doChangeLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.myUser != null){
                    // get the value of the editText
                    String value = changeLast.getText().toString();
                    if(value.equals("")){
                        Toast.makeText(EditProfile.this,"Cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MainActivity.myUser.setLastName(value, MainActivity.userRef);
                    Toast.makeText(EditProfile.this,"last name has been changed",Toast.LENGTH_LONG).show();
                }else{
                    Log.e("change ","error user is null");
                }
            }
        });

        doChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.myUser != null){
                    // get the value of the editText
                    String value = changeUsername.getText().toString().trim();
                    if(value.equals("")){
                        Toast.makeText(EditProfile.this,"Cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MainActivity.myUser.setUsername(value, MainActivity.userRef);
                    Toast.makeText(EditProfile.this,"Username has been changed",Toast.LENGTH_LONG).show();
                }else{
                    Log.e("change ","error user is null");
                }
            }
        });

        doChangeAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.myUser != null){
                    // get the value of the editText
                    String value = changeAge.getText().toString();
                    if(value.equals("")){
                        Toast.makeText(EditProfile.this,"Cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int myAge = Integer.parseInt(value);
                    MainActivity.myUser.setAge(myAge, MainActivity.userRef);
                    Toast.makeText(EditProfile.this,"Age has been changed",Toast.LENGTH_LONG).show();
                }else{
                    Log.e("change ","error user is null");
                }
            }
        });

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
