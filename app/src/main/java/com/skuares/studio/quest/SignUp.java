package com.skuares.studio.quest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by salim on 12/17/2015.
 */
public class SignUp extends AppCompatActivity {

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;

    @Bind(R.id.input_fname) EditText _fnameText;
    @Bind(R.id.input_lname) EditText _lnameText;
    @Bind(R.id.input_age) EditText _ageText;

    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;


    ProgressDialog progressDialog;


    protected Firebase ref;
    //protected EditText email,username,firstName,lastName,password,age;
    //protected Button signup;
    //private LoginButton loginButton;
//    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.fancy_sign_up_layout);

        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupMethod();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });


//        callbackManager = CallbackManager.Factory.create();
        ref = new Firebase (getResources().getString(R.string.firebaseUrl));

        // reference
        /*
        email = (EditText)findViewById(R.id.email);
        firstName = (EditText)findViewById(R.id.firstname);
        lastName = (EditText)findViewById(R.id.lastname);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        age = (EditText)findViewById(R.id.age);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        */


    }


    public void signupMethod() {


        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignUp.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String age = _ageText.getText().toString();
        int nAge = Integer.parseInt(age);

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        // first we create the user (aka sign up),, then we log him in
                        // to do so we need a ref
                        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> stringObjectMap) {
                                Toast.makeText(SignUp.this, "Success.. Welcome To Quest", Toast.LENGTH_LONG).show();
                                Log.e("succes user", "my user id" + stringObjectMap.get("uid"));

                                // here call the log in method
                                authMeToLogin();

                            }// end on success

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                Toast.makeText(SignUp.this, "Firebase Errot" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                //Log.e("error type", String.valueOf(" "+mEmail instanceof String));
                            }
                        });


                        //onSignupSuccess();
                        // onSignupFailed();
                        //progressDialog.dismiss();
                    }
                }, 3000);
    }


    /*

    public void Signup(View view) {


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
    */

    public void authMeToLogin(){
        ref.authWithPassword(_emailText.getText().toString(), _passwordText.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                Toast.makeText(SignUp.this, "Authenticated", Toast.LENGTH_LONG).show();

                // call signup success

                //Intent intentHome = new Intent(SignUp.this, MainActivity.class);
                //startActivity(intentHome);
                //finish();

                // assign user to firebase database
                // do some checking on values
                String mAge = _ageText.getText().toString();
                int nAge = Integer.parseInt(mAge);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholderuser);

                User myFirebaseUser = new User(_fnameText.getText().toString(),
                        _lnameText.getText().toString(),
                        _nameText.getText().toString().trim(),
                        _emailText.getText().toString(),
                        nAge,
                        bitmap
                );
                assignUserToFirebaseDatabase(authData.getUid(), myFirebaseUser);

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                Toast.makeText(SignUp.this, "Problem with Authentication", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void assignUserToFirebaseDatabase(String uid,User firebaseUser){

        // assign an appropriate listener
        Firebase userKey = ref.child("users").child(uid);
        userKey.setValue(firebaseUser);
        // call sign up success
        onSignupSuccess();
        progressDialog.dismiss();

    }

    // no need for this
    /*
    public void signInMethod(View view) {
        Intent intent = new Intent(SignUp.this,SignIn.class);
        startActivity(intent);
        finish();
    }
    */





    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        //Intent intentHome = new Intent(SignUp.this, MainActivity.class);
        //startActivity(intentHome);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String age = _ageText.getText().toString();


        if(fname.isEmpty()){
            _fnameText.setError("Cannot be empty");
            valid = false;
        }else{
            _fnameText.setError(null);
        }

        if(lname.isEmpty()){
            _lnameText.setError("Cannot be empty");
            valid = false;
        }else{
            _lnameText.setError(null);
        }

        if(age.isEmpty()){
            _ageText.setError("Cannot be empty");
            valid = false;
        }else{
            // get the number
            int nAge = Integer.parseInt(age);
            if(nAge <= 0){
                _ageText.setError("Cannot be negative or zero");
                valid = false;
            }else{
                _ageText.setError(null);
            }

        }


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }




}
