package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class IssueTicketActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseSource;
    SharedPreferences shared;
    Spinner src,dest;
    EditText tickets,childs;
    Button btnSubmit;
    String path;
    HashMap<String, Integer> hash_table = new HashMap<>();
    HashMap<Integer, String> hash_table_key = new HashMap<>();



    @Override
    protected void onStart() {
        super.onStart();

        databaseSource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> cities = new ArrayList<String>();
                final List<String> inPathCities = new ArrayList<String>();

                hash_table.clear();
                hash_table_key.clear();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String city_key = areaSnapshot.getKey();
                    String city_name = areaSnapshot.child("City_Name").getValue(String.class);
//                    Log.i("KEEEEYYY:",city_key);
                    hash_table.put(city_name,Integer.parseInt(city_key));
                    hash_table_key.put(Integer.parseInt(city_key),city_name);

                    cities.add(city_name);
                }
                path = shared.getString("selectedPath","NULL");
                String[] arr= path.split("-");
                for(int i=0;i<arr.length;i++)
                {
                    inPathCities.add(hash_table_key.get(hash_table.get(arr[i])));
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(IssueTicketActivity.this, android.R.layout.simple_spinner_item, inPathCities);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                src.setAdapter(areasAdapter);
                dest.setAdapter(areasAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_ticket);

        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
//        printMessage(shared.getString("selectedPath","NULL"));
        src = (Spinner) findViewById(R.id.issueScrSpinner);
        dest = (Spinner) findViewById(R.id.issueDestSpinner);
        tickets = (EditText) findViewById(R.id.no_of_tickets);
        btnSubmit = (Button) findViewById(R.id.btn_Issue);
        databaseSource = FirebaseDatabase.getInstance().getReference("City");
        childs = (EditText) findViewById(R.id.no_childs);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String source = src.getSelectedItem().toString();
                    String destination = dest.getSelectedItem().toString();
                    int no_of_tickets;
                    int no_of_childs;

                    if(tickets.getText().toString().isEmpty())
                    {
                        tickets.setError("Please Enter Number of Tickets");
                        tickets.requestFocus();
                        return;
                    }
                    else {
                        no_of_tickets = Integer.parseInt(tickets.getText().toString());
                    }
                    if(childs.getText().toString().isEmpty())
                    {
//
                        childs.setError("Please Enter number of Child");
                        childs.requestFocus();
                        return;
                    }else {
                       no_of_childs = Integer.parseInt(childs.getText().toString());
                    }

                    if (no_of_tickets > 0 && no_of_tickets <= 10 && no_of_childs>=0 &&
                            no_of_childs<=10 && !TextUtils.equals(source, destination)) {
                        Intent i = new Intent(IssueTicketActivity.this, TicketConfirmation.class);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putInt("no_of_tickets", no_of_tickets);
                        editor.putInt("no_of_childs", no_of_childs);

                        editor.putString("src_ticket", source);
                        editor.putString("dest_ticket", destination);
                        editor.putInt("src_key_ticket", hash_table.get(source));
                        editor.putInt("dest_key_ticket", hash_table.get(destination));
//                        int t = shared.getInt("Total_tickets",0);
//                        t = t + no_of_tickets;
//                        editor.putInt("Total_tickets",t);
                        editor.commit();
                        startActivity(i);
                        return;
                    } else {
                        printMessage("Please check Entered Details such as source and destination" +
                                " and number of tickets");
                    }
                }


        });

    }

    private void printMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

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
            case R.id.stop_trip:
                Intent i = new Intent(getApplicationContext(),StopTripActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }

    public void logout() {
        mAuth.signOut();
        changeToLoginActivity();
    }

    public void changeToLoginActivity() {
        Intent i = new Intent(getApplicationContext(),Login.class);
        startActivity(i);
        finish();
        return;
    }
}
