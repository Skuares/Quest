package com.skuares.studio.quest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by salim on 12/17/2015.
 */
public class SignIn extends AppCompatActivity {

    @Bind(R.id.input_email) EditText  _emailText;
    @Bind(R.id.input_password) EditText  _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    private static final int REQUEST_SIGNUP = 0;

    //protected EditText emailSign;
    //protected EditText passwordSign;

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fancy_sign_in_layout);

        ButterKnife.bind(this);

        ref = new Firebase(getResources().getString(R.string.firebaseUrl));

        //emailSign = (EditText) findViewById(R.id.emailSign);
        //passwordSign = (EditText)findViewById(R.id.passworSign);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Signin();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }



    public void Signin() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignIn.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();



        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed

                        ref.authWithPassword(_emailText.getText().toString(), _passwordText.getText().toString(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {

                                onLoginSuccess();

                                //System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                                //Toast.makeText(SignIn.this, "Welcome", Toast.LENGTH_LONG).show();

                                //Intent intentHome = new Intent(SignIn.this, MainActivity.class);
                                //startActivity(intentHome);
                                //finish();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // there was an error
                                onLoginFailed();

                                Toast.makeText(SignIn.this, "Email or Password is incorrect", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });


                    }
                }, 2000);


        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                Intent intentHome = new Intent(SignIn.this, MainActivity.class);
                startActivity(intentHome);

                this.finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Intent intentHome = new Intent(SignIn.this, MainActivity.class);
        startActivity(intentHome);
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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
