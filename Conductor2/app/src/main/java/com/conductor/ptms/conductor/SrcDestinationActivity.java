package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class SrcDestinationActivity extends AppCompatActivity {

    DatabaseReference databaseSource;
    Spinner srcSpinner, destSpinner;
    SharedPreferences shared;
    Button next;
    String source,dest;
    FirebaseAuth mAuth;
    HashMap<String, Integer> hash_table = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
        databaseSource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cities = new ArrayList<String>();
                hash_table.clear();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String city_key = areaSnapshot.getKey();
                    String city_name = areaSnapshot.child("City_Name").getValue(String.class);
//                    Log.i("KEEEEYYY:",city_key);
                    hash_table.put(city_name,Integer.parseInt(city_key));

                    cities.add(city_name);
                }
                Collections.sort(cities);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(SrcDestinationActivity.this, android.R.layout.simple_spinner_item, cities);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                srcSpinner.setAdapter(areasAdapter);
                destSpinner.setAdapter(areasAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_destination);

        databaseSource = FirebaseDatabase.getInstance().getReference("City");
        srcSpinner = (Spinner) findViewById(R.id.srcSpinner);
        destSpinner = (Spinner) findViewById(R.id.destSpinner);
        next = (Button) findViewById(R.id.srcDestNext);
        mAuth = FirebaseAuth.getInstance();

        //For testing Shared Data
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
        printMessage(shared.getString("bus_id","NULL"));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = srcSpinner.getSelectedItem().toString();
                dest = destSpinner.getSelectedItem().toString();
                if(source == null && dest == null)
                {
                    printMessage("Please Wait for cities to Load...");
                }
                else if(!TextUtils.equals(source,dest)){

                    int src_key = hash_table.get(source);
                    int dest_key = hash_table.get(dest);
//                    Log.i("KEEEEY:",Integer.p);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("source",source);
                    editor.putString("destination",dest);
                    editor.putInt("source_key",src_key);
                    editor.putInt("destination_key",dest_key);
                    editor.commit();
                    changeActivity();

                }
                else {
                    printMessage("Source and Destination Cannot be Same!");
                }
            }
        });

    }

    private void printMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

    }

    private void changeActivity() {
        Intent intent = new Intent(SrcDestinationActivity.this,RouteSelector.class);
//        intent.putExtra("src",source);
//        intent.putExtra("dest",dest);
        startActivity(intent);
//        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.logout_menu:
                logout();
                break;
            case R.id.help_menu:
                Toast.makeText(this, "HELP clicked", Toast.LENGTH_SHORT).show();

                break;
        }

        return true;
    }

    private void logout() {
        mAuth.signOut();
        changeToLoginActivity();
    }

    private void changeToLoginActivity() {
        Intent i = new Intent(getApplicationContext(),Login.class);
        startActivity(i);
        finish();
        return;
    }
}
