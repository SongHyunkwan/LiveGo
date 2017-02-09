package com.example.songhyunkwan.livego;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class StateActivity extends AppCompatActivity {

    private TextView mStateText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mStateText = (TextView) findViewById(R.id.stateText);
        mRef = new Firebase("https://livego-3f70b.firebaseio.com/Ing");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Ing");
        mAuth = FirebaseAuth.getInstance();
        final String userId = mAuth.getCurrentUser().getUid();
        Log.v("E_VALUE", "에러시점");
        mRef.child(userId).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                Log.v("E_VALUE", "dataSnapshot.getValue() = " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                    Map<String, String> map = dataSnapshot.getValue(Map.class);
                    String tt = map.get("상태");
                    Log.v("E_VALUE", "ㅆ" + tt);
                    mStateText.setText("현재 상태 : " + tt);
                }else{
                    mStateText.setText("현재 상태 : 예약현황이 없습니다" );
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("E_VALUE", "firebaseError" + firebaseError);
            }
        });
    }
}
