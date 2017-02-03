package com.example.songhyunkwan.livego;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;

    private Button mLoingButton;
    private Button mNewacButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){

                    Intent maIntent = new Intent(LoginActivity.this, MainActivity.class);
                    maIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(maIntent);
                    finish();

                }
            }
        };

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        mLoingButton = (Button) findViewById(R.id.loginButton);

        mLoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginConfirm();

            }
        });

        mNewacButton = (Button) findViewById(R.id.registerButton);

        mNewacButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent reIntent = new Intent(LoginActivity.this, NewAccountActivity.class);
                startActivity(reIntent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    private void LoginConfirm() {

        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.setMessage("로그인 중....");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        checkUserExist();

                    }else{

                        Toast.makeText(LoginActivity.this,"로그인 에러..",Toast.LENGTH_LONG).show();

                    }
                }
            });

        }
    }

    private void checkUserExist() {

    final String user_id = mAuth.getCurrentUser().getUid();

    mDatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.hasChild(user_id)){
                mProgress.dismiss();
                Intent maIntent = new Intent(LoginActivity.this, MainActivity.class);
                maIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(maIntent);
                finish();


            }else {

                Toast.makeText(LoginActivity.this,"에러...",Toast.LENGTH_LONG).show();

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

}
}
