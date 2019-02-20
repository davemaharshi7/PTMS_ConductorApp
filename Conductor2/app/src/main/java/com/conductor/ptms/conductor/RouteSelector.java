package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;

public class RouteSelector extends AppCompatActivity {

    SharedPreferences shared;
    Spinner route;
    Button submit;
    DatabaseReference databaseRoute,databaseCity;
    FirebaseAuth mAuth;
    HashMap<Integer, String> hash_table_route = new HashMap<>();
    List<String> cities = new ArrayList<String>();


    @Override
    protected void onStart() {
        super.onStart();

        //TODO: To get the list of all cities
        databaseCity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hash_table_route.clear();
                cities.clear();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String city_key = areaSnapshot.getKey();
                    String city_name = areaSnapshot.child("City_Name").getValue(String.class);
//                    Log.i("KEEEEYYY:",city_key);
                    hash_table_route.put(Integer.parseInt(city_key),city_name);

                    cities.add(city_name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseRoute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> paths = new ArrayList<String>();
                List<String> requiredPaths = new ArrayList<String>();
                List<String> requiredNamePaths = new ArrayList<String>();


                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String Path = areaSnapshot.child("Path").getValue(String.class);
                    paths.add(Path);
                }
                for(int j = 0;j<paths.size();j++){


                    String[] arr = paths.get(j).split("#");
                    int len = arr.length;
                    if((Integer.parseInt(arr[0]) == shared.getInt("source_key",0) && (Integer.parseInt(arr[len-1]) == shared.getInt("destination_key",0))))
                    {
                        //requiredPath are paths in number format
                        requiredPaths.add(paths.get(j));
                        StringBuilder s1 = new StringBuilder();

                        for(int i=0;i<len;i++)
                        {
                            s1.append(hash_table_route.get(Integer.parseInt(arr[i]))+"-");

                        }
                        requiredNamePaths.add(s1.toString());

                    }
                }


                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(RouteSelector.this, android.R.layout.simple_spinner_item, requiredNamePaths);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                route.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selector);

        mAuth = FirebaseAuth.getInstance();
        route = (Spinner) findViewById(R.id.routeSpinner);
        submit = (Button) findViewById(R.id.routeSubmit);
        databaseRoute = FirebaseDatabase.getInstance().getReference("Route");
        databaseCity = FirebaseDatabase.getInstance().getReference("City");
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedRoutePath = route.getSelectedItem().toString();
                if (selectedRoutePath == null)
                {
                    printMessage("Sorry No Route Available");
                }
                else {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("selectedPath", selectedRoutePath);
                    editor.commit();
                    Intent i = new Intent(RouteSelector.this, IssueTicketActivity.class);
//                i.putExtra("selectedRoute",selectedRoutePath);
                    startActivity(i);
                    finish(); //Added to make robust situation => as conductor cannot issue different tickets on single Login

                    return;
                }
            }
        });

     }

    private void printMessage(String path) {
        Toast.makeText(getApplicationContext(),path,Toast.LENGTH_SHORT).show();
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
