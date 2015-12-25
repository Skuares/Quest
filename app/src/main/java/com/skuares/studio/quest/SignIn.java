package com.skuares.studio.quest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by salim on 12/17/2015.
 */
public class SignIn extends AppCompatActivity {


    protected EditText emailSign;
    protected EditText passwordSign;

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        ref = new Firebase(getResources().getString(R.string.firebaseUrl));

        emailSign = (EditText) findViewById(R.id.emailSign);
        passwordSign = (EditText)findViewById(R.id.passworSign);


    }

    public void Signin(View view) {

        // get values
        String mEmail = emailSign.getText().toString().toLowerCase();
        String mPassword = passwordSign.getText().toString();

        if(mEmail.equals("") || mPassword.equals("")){

            // do nothing
            Toast.makeText(this, "Require", Toast.LENGTH_LONG).show();


        }else{
            ref.authWithPassword(emailSign.getText().toString(), passwordSign.getText().toString(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                    Toast.makeText(SignIn.this, "Welcome", Toast.LENGTH_LONG).show();
                    Intent intentHome = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                    Toast.makeText(SignIn.this, "Email or Password is incorrect", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
