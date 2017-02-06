package com.example.songhyunkwan.livego;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
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

    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

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
    }

    private void LoginConfirm() {

        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        final String emailText = mEmailField.getText().toString().trim();
        String passText = mPasswordField.getText().toString().trim();

        //Log.v("EmailCheck","is it Email ? " + isValidEmail(emailText));
        if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passText)) { //빈칸 없애기
            //email형식 체크
            if (isValidEmail(emailText)) {
                mProgress.setMessage("로그인 중...");
                mProgress.show();
                //서버로 전송
                mAuth.signInWithEmailAndPassword(emailText, passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            checkUserExist();
                            mProgress.dismiss();
                        }else{
                            Log.v("Login","login 에러 : " + task);
                            checkEmailExist(emailText);
                            mProgress.dismiss();

                        }
                    }
                });
                //ID,PW 체크 확인 후 뷰전환

            } else {
                Toast.makeText(this, "Email형식에 맞지 않습니다.", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "Email과 비밀번호를 전부 입력해주세요", Toast.LENGTH_LONG).show();
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
    public final static boolean isValidEmail(CharSequence target) { //Email입력됬는지 체크하는 부분
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void checkEmailExist(String email) {

        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if(task.isSuccessful()){
                    ///////// getProviders() will return size 1. if email ID is available.
                    //Log.v("TASK","TAST = "+task.getResult().getProviders().size());
                    if (task.getResult().getProviders().size() == 0){   //이메일 중복 X => 사용가능

                        EmailCheckSucess();

                    }else {                                             //이메일 중복 O => 사용불가

                        EmailCheckFail();

                    }

                }
            }
        });

    }
    private void EmailCheckSucess() {   //이메일 사용 가능
        Toast.makeText(this,"등록되지 않은 Email입니다.",Toast.LENGTH_LONG).show();
    }

    private void EmailCheckFail() {     //이메일 사용 불가
        Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
    }

}
