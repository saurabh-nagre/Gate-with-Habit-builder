package com.master.gate_habitbuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


public class SigninActivity extends AppCompatActivity {

    TextView forgotpasswordTV;
    EditText emailET,passwordET;
    Button signupbutton,signinbutton;
    GridLayout layout ;
    SignInButton googlebutton;
    private FirebaseAuth mAuth;
    ProgressBar mprogressBar;
    String memail,mpassword;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        forgotpasswordTV = findViewById(R.id.forgotpasswordTextView);
        signupbutton = findViewById(R.id.createaccountButton);
        signinbutton = findViewById(R.id.signinButton);
        layout = findViewById(R.id.authGridLayout);
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        googlebutton = findViewById(R.id.googlesigninButton);
        mprogressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();

        if(currentuser!=null){
            switchActivity(currentuser);
            finishAffinity();
        }

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()<8 || s.toString().contains(" ")){
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                }
                else
                {
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().contains(" ")){
                    Toast.makeText(SigninActivity.this,"Password Shouldn't Contain any spaces",Toast.LENGTH_LONG).show();
                }
            }
        });

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signupbutton.setOnClickListener(v-> signup());

        signinbutton.setOnClickListener(v->signin());

        forgotpasswordTV.setOnClickListener(v-> forgotPassword());

        googlebutton.setOnClickListener(v->googlesignIn());
    }

    void signup(){
        memail = emailET.getText().toString();
        mpassword = passwordET.getText().toString();

        emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);

        if(memail.isEmpty() ) {
            Toast.makeText(SigninActivity.this, "Enter Email to Create Account", Toast.LENGTH_SHORT).show();
            emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0);
        }
        else if(mpassword.length()<8)
            passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        else{
            mprogressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(memail,mpassword).addOnCompleteListener(SigninActivity.this, task -> {

                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null) {
                        switchActivity(user);
                    }
                    else {
                        Toast.makeText(SigninActivity.this,"Something went Wrong",Toast.LENGTH_SHORT).show();
                        recreate();
                    }

                }
                else{
                    mprogressBar.setVisibility(View.GONE);
                    Toast.makeText(SigninActivity.this,"Please check your Email",Toast.LENGTH_SHORT).show();

                    emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                    try {
                        throw Objects.requireNonNull(task.getException());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("SignupException","Exception in creating Account");
                    }

                }

            });
        }


    }

    void signin(){
        memail = emailET.getText().toString();
        mpassword = passwordET.getText().toString();

        emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);

        if(memail.isEmpty()){
            Toast.makeText(SigninActivity.this,"Enter Email to Login or Use Google Sign in",Toast.LENGTH_SHORT).show();
            emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        }
        else if(mpassword.length()<8){
            passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        }
        else{
            mprogressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(memail,mpassword).addOnCompleteListener(SigninActivity.this, task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null) {
                        switchActivity(user);
                    }
                    else{
                        mprogressBar.setVisibility(View.GONE);
                        Toast.makeText(SigninActivity.this,"Something went Wrong",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    mprogressBar.setVisibility(View.GONE);
                    passwordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                    emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                    Toast.makeText(SigninActivity.this,"Check Entered Credentials",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void forgotPassword(){
        memail =  emailET.getText().toString();
        if(memail.isEmpty()){
            Toast.makeText(SigninActivity.this,"Enter Email to Reset Password",Toast.LENGTH_LONG).show();
            emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        }
        else{
            mAuth.sendPasswordResetEmail(memail).addOnCompleteListener(this,task -> {
                if(task.isSuccessful()){
                    Toast.makeText(SigninActivity.this,"Password Reset link sent to "+memail,Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(SigninActivity.this,"Check Your Email",Toast.LENGTH_SHORT).show();
                    emailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                }
            });
        }


    }
    void switchActivity(FirebaseUser user){

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("email",user.getEmail());
        startActivity(intent);
        finishAffinity();
    }

    private void googlesignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mprogressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                mprogressBar.setVisibility(View.GONE);
                Toast.makeText(SigninActivity.this,"Google Sign in Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    mprogressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user!=null)
                            switchActivity(user);
                        else{
                            Toast.makeText(SigninActivity.this,"Google Sign in Failed",Toast.LENGTH_SHORT).show();
                        }

                    } else {
                       Toast.makeText(SigninActivity.this,"Google Sign in Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
