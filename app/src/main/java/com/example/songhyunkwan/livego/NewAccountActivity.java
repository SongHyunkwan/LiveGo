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

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewAccountActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmField;

    private Button mRegisterButton;

    private Firebase mRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;


    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mConfirmField = (EditText) findViewById(R.id.confirmField);

        mRegisterButton = (Button) findViewById(R.id.registerButton);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAccount();

            }
        });

    }

    private void CreateAccount() {      //계정 만들기
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();
        String passwordCheck = mConfirmField.getText().toString().trim();
        Log.v("test","password.length() = " + password.length());
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&!TextUtils.isEmpty(passwordCheck)) {

            checkEmailExist(email);

            if (TextUtils.equals(password,passwordCheck) && password.length() > 5 && password.length() < 21) {

                mProgress.setMessage("계정 생성 중...");
                mProgress.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//정상적으로 만들어 졌을때

                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference databaseReference = mDatabaseReference.child(user_id);

                            databaseReference.child("Email").setValue(email);

                            mProgress.dismiss();

                            Intent intent = new Intent(NewAccountActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }else {
                            Log.v("task","task = " + task);

                        }

                    }
                });
                Toast.makeText(this, "회원가입 완료",Toast.LENGTH_LONG);
                mProgress.dismiss();
                //Toast.makeText(this, "계정 생성 완료 생성됨", Toast.LENGTH_LONG).show();

            }else if(password.length() <=5 || password.length() > 20){      //비밀번호 글자 수 확인

                Toast.makeText(this, "비밀번호를 6 ~ 20자 이내로 입력해주세요.",Toast.LENGTH_LONG).show();

            }else {

                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();

            }

        }else{                                   // 로그인 안될시 여러 요인들 체크

            if (TextUtils.isEmpty(email)){

                Toast.makeText(this, "Email을 입력해 주세요",Toast.LENGTH_LONG).show();

            }else if (TextUtils.isEmpty(password)){

                Toast.makeText(this, "비밀번호를 입력해 주세요.",Toast.LENGTH_LONG).show();

            }else if (TextUtils.isEmpty(passwordCheck)){

                Toast.makeText(this, "비밀번호 확인을 입력해 주세요.",Toast.LENGTH_LONG).show();

            }
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

    }

    private void EmailCheckFail() {     //이메일 사용 불가
        Toast.makeText(this,"등록되있는 Email입니다",Toast.LENGTH_LONG).show();
    }

}
