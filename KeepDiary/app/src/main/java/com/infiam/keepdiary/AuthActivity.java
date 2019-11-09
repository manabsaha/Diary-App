package com.infiam.keepdiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private Button authBtn;
    private FirebaseAuth mAuth;
    private String email,password;
    private TextInputLayout getEmail,getPassword;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        authBtn = findViewById(R.id.submitAuth);
        getEmail = findViewById(R.id.email);
        getPassword = findViewById(R.id.password);

        final String state = getIntent().getStringExtra("state");

        if(state.equals("new")){
            getPassword.setHint("New Password");
            authBtn.setText("Register");
        }
        else{
            getPassword.setHint("Password");
            authBtn.setText("Login");
        }

        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**Get email and password from Input Fields*/
                email = getEmail.getEditText().getText().toString();
                password = getPassword.getEditText().getText().toString();

                if(email.length()>0 && password.length()>=8){
                    /**Login or Register User*/
                    if(state.equals("new")){
                        registerUser();
                    }
                    else if(state.equals("ex")){
                        loginUser();
                    }
                }
                else{
                    if(email.length()==0) {
                        getEmail.setError("Field can't be blank");
                        getEmail.requestFocus();
                    }
                    else {
                        getPassword.setError("Minimum 8 characters");
                        getPassword.requestFocus();
                    }
                }

            }
        });

    }

    private void loginUser() {
        showProgress("Logging user");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            gotoHome();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void registerUser() {
        showProgress("Registering User");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            gotoHome();
                            Toast.makeText(getApplicationContext(), "Account Created",Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void gotoHome() {
        Intent welcome = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(welcome);
        finish();
    }

    private void showProgress(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent auth = new Intent(getApplicationContext(),WelcomeActivity.class);
        startActivity(auth);
        finish();
    }
}
