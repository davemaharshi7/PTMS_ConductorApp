package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogoutAndAgainIssueActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private Button logout,issueAnother;
    DatabaseReference databaseHistory,databaseFareCollected;
    SharedPreferences shared;
    int final_fare,total_tickets;
    String formatedDate,bus_id,route_id,source,destination,cid,status;

    @Override
    protected void onStart() {
        super.onStart();
        databaseFareCollected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    FareStatusClass fc = areaSnapshot.getValue(FareStatusClass.class);
                    if(TextUtils.equals(fc.getDate(),formatedDate) && TextUtils.equals(fc.getC_ID(),cid) && TextUtils.equals(fc.getBus_ID(),bus_id))
                    {
                        status = fc.getIs_Collected();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_and_again_issue);

        mAuth = FirebaseAuth.getInstance();
        logout = (Button) findViewById(R.id.logout);
        issueAnother = (Button) findViewById(R.id.IssueAnother);
        databaseHistory = FirebaseDatabase.getInstance().getReference().child("History");
        databaseFareCollected = FirebaseDatabase.getInstance().getReference().child("Fare_Collected_Status");

        //getting details
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        formatedDate = frmt.format(c);
        cid = shared.getString("conductor_id","Error");
        bus_id = shared.getString("bus_id","Error");
        route_id = shared.getString("selectedPath","ERROR");
        source = shared.getString("src_ticket","ERROR");
        destination = shared.getString("dest_ticket","ERROR");
        total_tickets = shared.getInt("Total_tickets",0);
        final_fare = shared.getInt("final_fare",0);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                HistoryClass historyClass = new HistoryClass(bus_id,formatedDate,route_id,Integer.toString(final_fare),Integer.toString(total_tickets),status,destination,source);
                String id = databaseHistory.push().getKey();
                databaseHistory.child(cid).child(id).setValue(historyClass);
                Toast.makeText(getApplicationContext(),"History uploaded",Toast.LENGTH_SHORT).show();
                logout();

            }
        });
        issueAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogoutAndAgainIssueActivity.this,IssueTicketActivity.class);
                startActivity(intent);
                return;
            }
        });

    }
    public void logout() {
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
