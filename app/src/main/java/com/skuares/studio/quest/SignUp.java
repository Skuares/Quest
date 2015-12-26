package com.skuares.studio.quest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;
import com.firebase.ui.auth.core.SocialProvider;

import java.util.Map;

/**
 * Created by salim on 12/17/2015.
 */
public class SignUp extends AppCompatActivity {

    protected Firebase ref;
    protected EditText email,username,firstName,lastName,password,age;
    protected Button signup;
    private LoginButton loginButton;
//    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.signup_fancy_layout);

//        callbackManager = CallbackManager.Factory.create();
        ref = new Firebase (getResources().getString(R.string.firebaseUrl));

        // reference
        email = (EditText)findViewById(R.id.email);
        firstName = (EditText)findViewById(R.id.firstname);
        lastName = (EditText)findViewById(R.id.lastname);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        age = (EditText)findViewById(R.id.age);
        loginButton = (LoginButton)findViewById(R.id.login_button);


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // All providers are optional! Remove any you don't want.
//        setEnabledAuthProvider(SocialProvider.facebook);
////        setEnabledAuthProvider(SocialProvider.twitter);
////        setEnabledAuthProvider(SocialProvider.google);
////        setEnabledAuthProvider(SocialProvider.password);
//    }

    public void Signup(View view) {
        //This method is for firebase login
//        showFirebaseLoginPrompt();

        // get values
        String mEmail = email.getText().toString().toLowerCase();
        String mPassword = password.getText().toString();
        String mUsername = username.getText().toString();
        String mFirstName = firstName.getText().toString();
        String mLastName = lastName.getText().toString();
        String mAge = age.getText().toString();




        if(mEmail.equals("") || mPassword.equals("") || mUsername.equals("")
                || mFirstName.equals("") || mLastName.equals("") || mAge.equals("")){



            // do nothing
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show();


        }else{


            int intAge = Integer.parseInt(mAge);
            if(intAge <= 0){
                Toast.makeText(this, "Correct Your age please", Toast.LENGTH_LONG).show();
                return;
            }
            // first we create the user (aka sign up),, then we log him in
            // to do so we need a ref
            ref.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    Toast.makeText(SignUp.this,"Success.. Welcome To Quest",Toast.LENGTH_LONG).show();
                    Log.e("succes user", "my user id" + stringObjectMap.get("uid"));

                    // here call the log in method
                    authMeToLogin();

                }// end on success

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(SignUp.this,"Firebase Errot"+firebaseError.getMessage(),Toast.LENGTH_LONG).show();
                    //Log.e("error type", String.valueOf(" "+mEmail instanceof String));
                }
            });

        }
    }

    public void authMeToLogin(){
        ref.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                Toast.makeText(SignUp.this,"Authenticated",Toast.LENGTH_LONG).show();
                Intent intentHome = new Intent(SignUp.this, MainActivity.class);
                startActivity(intentHome);
                finish();

                // assign user to firebase database
                // do some checking on values
                String mAge = age.getText().toString();
                int nAge = Integer.parseInt(mAge);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholderuser);

                User myFirebaseUser = new User(firstName.getText().toString(),
                        lastName.getText().toString(),
                        username.getText().toString().trim(),
                        email.getText().toString(),
                        nAge,
                        bitmap
                );
                assignUserToFirebaseDatabase(authData.getUid(),myFirebaseUser);

            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                Toast.makeText(SignUp.this,"Problem with Authentication",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void assignUserToFirebaseDatabase(String uid,User firebaseUser){

        // assign an appropriate listener
        Firebase userKey = ref.child("users").child(uid);
        userKey.setValue(firebaseUser);

    }

    public void signInMethod(View view) {
        Intent intent = new Intent(SignUp.this,SignIn.class);
        startActivity(intent);
        finish();
    }

}
