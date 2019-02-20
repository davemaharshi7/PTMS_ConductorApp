package com.conductor.ptms.conductor;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestActivity extends AppCompatActivity {

    DatabaseReference databaseFare;
    SharedPreferences shared;
    String dist;
    @Override
    protected void onStart() {
        super.onStart();
        databaseFare.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot d: dataSnapshot.child("4"))
//                {
                    dist = dataSnapshot.child("3").child("dist").getValue(String.class);
                    Log.d("DISTANCE:",dist);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        databaseFare = FirebaseDatabase.getInstance().getReference("Distance").child(Integer.toString(1));

    }
}
