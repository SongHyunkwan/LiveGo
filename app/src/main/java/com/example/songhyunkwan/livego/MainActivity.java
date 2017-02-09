package com.example.songhyunkwan.livego;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Spinner mBigArea;
    private Spinner mSmallArea;
    private Spinner mAge;
    private Spinner mPurpose;

    private TextView mBigAreaText;
    private TextView mSmallAreaText;
    private TextView mAgeText;
    private TextView mPurText;

    private EditText mStoreName;
    private EditText mCount;

    private Button mSendButton;
    private Button mStateButton;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabaseUsers;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("State");
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Ing");

        mProgress = new ProgressDialog(this);

        mBigArea = (Spinner) findViewById(R.id.bigAreaSpin);
        mSmallArea = (Spinner) findViewById(R.id.smallAreaSpin);
        mAge = (Spinner) findViewById(R.id.ageSpin);
        mPurpose = (Spinner) findViewById(R.id.purposeSpin);

        mBigAreaText = (TextView) findViewById(R.id.bigAreaText);
        mSmallAreaText = (TextView) findViewById(R.id.smallAreaText);
        mAgeText = (TextView) findViewById(R.id.ageText);
        mPurText = (TextView) findViewById(R.id.purText);

        mStoreName = (EditText) findViewById(R.id.storeNameField);
        mCount = (EditText) findViewById(R.id.count);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mStateButton = (Button) findViewById(R.id.dataState);

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent logIntent = new Intent(MainActivity.this, LoginActivity.class);
                    logIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logIntent);
                }
            }
        };

        final String [] bCity = {"단국대학교(죽전)"};
        final String [] sCity = {"정문 앞","꽃매마을"};
        final String [] ageRange = {"20대 초반","20대 중반","20대 후반","30대 초반","30대 중반","30대 후반 이상"};
        final String [] purpose = {"학교 동기모임","끼리끼리","남녀 미팅","단체(교내행사, 개총...)"};

        ArrayAdapter adapter1 = new ArrayAdapter(
                getApplicationContext(), // 현재화면의 제어권자
                android.R.layout.simple_spinner_item,
                bCity); // 데이터
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter adapter2 = new ArrayAdapter(
                getApplicationContext(), // 현재화면의 제어권자
                android.R.layout.simple_spinner_item,
                sCity); // 데이터
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter adapter3 = new ArrayAdapter(
                getApplicationContext(), // 현재화면의 제어권자
                android.R.layout.simple_spinner_item,
                ageRange); // 데이터
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter adapter4 = new ArrayAdapter(
                getApplicationContext(), // 현재화면의 제어권자
                android.R.layout.simple_spinner_item,
                purpose); // 데이터
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        mBigArea.setAdapter(adapter1);
        mBigArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBigAreaText.setText(bCity[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSmallArea.setAdapter(adapter2);
        mSmallArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSmallAreaText.setText(sCity[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAge.setAdapter(adapter3);
        mAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAgeText.setText(ageRange[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPurpose.setAdapter(adapter4);
        mPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPurText.setText(purpose[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendData();

            }
        });

        mStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent stIntent = new Intent(MainActivity.this, StateActivity.class);
                stIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(stIntent);
            }
        });
    }

    private void sendData() {

        String Area1 = mBigAreaText.getText().toString().trim();
        String Area2 = mSmallAreaText.getText().toString().trim();
        String Age = mAgeText.getText().toString().trim();
        String Goal = mPurText.getText().toString().trim();

        String Store = mStoreName.getText().toString().trim();
        String Count = mCount.getText().toString().trim();

        mProgress.setMessage("예약 중...");
        mProgress.show();
        if (!TextUtils.isEmpty(Store) &&!TextUtils.isEmpty(Count)) {
            if (Integer.parseInt(Count) >= 4) {
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference databaseReference = mDatabase.child(user_id);
                DatabaseReference databaseReference2 = mDatabase2.child(user_id);

                databaseReference.child("지역").setValue(Area1);
                databaseReference.child("세부지역").setValue(Area2);
                databaseReference.child("연령층").setValue(Age);
                databaseReference.child("목적").setValue(Goal);
                databaseReference.child("상점이름").setValue(Store);
                databaseReference.child("인원 수").setValue(Count);

                databaseReference2.child("상태").setValue("예약");
                Log.v("TOSS","예약완료");
                Toast.makeText(MainActivity.this, "예약 완료!!",Toast.LENGTH_LONG).show();
            }else {
                Log.v("TOSS","인원수 늘려");
                Toast.makeText(MainActivity.this, "인원수는 4명 이상이여야 합니다",Toast.LENGTH_LONG).show();
            }
        }else{
            Log.v("TOSS","입력 다 안됨");
            Toast.makeText(MainActivity.this, "다 입력하세요!!",Toast.LENGTH_LONG).show();
        }
        mProgress.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkUserExist();
        mAuth.addAuthStateListener(mAuthListner);
    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(user_id)){

                    Intent maIntent = new Intent(MainActivity.this, LoginActivity.class);
                    maIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(maIntent);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
