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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TicketConfirmation extends AppCompatActivity {

    private static final int PRICE_PER_KM = 3;
    private static final int PRICE_PER_KM_CHILD = PRICE_PER_KM/2;
    TextView busid,routeid,srcc,dest,cid,no_tickets,fare;
    Button confirm;
    String src_key,dest_key;
    SharedPreferences shared;
    String dist,source,destination;
    FirebaseAuth mAuth;
    public int final_fare;
    int no_tickets_str,no_childs;
    TicketLog ticketLog;
    DatabaseReference databaseTicket;
    DatabaseReference databaseFare;
    String formatedDate;
    @Override
    protected void onStart() {
        super.onStart();
        databaseFare.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dist = dataSnapshot.child(dest_key).child("dist").getValue(String.class);
//                Log.d("DISTANCE::",dist);

//                dist = dataSnapshot.child(Integer.toString(dest_key)).getValue(String.class);
                final_fare = Integer.parseInt(dist)*PRICE_PER_KM*no_tickets_str;
                final_fare += Integer.parseInt(dist)*PRICE_PER_KM_CHILD*no_childs;
//                Log.d("DISTANCE _FARE",""+final_fare);
                fare.setText(" "+final_fare);
                ticketLog = new TicketLog(source,destination,formatedDate,""+no_tickets_str,""+final_fare);

                confirm.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_confirmation);

        mAuth = FirebaseAuth.getInstance();
        //Initialization And Setting Shared variable
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
        busid = (TextView) findViewById(R.id.Bus_id);
        routeid = (TextView) findViewById(R.id.Route_id);
        srcc = (TextView) findViewById(R.id.src_text);
        dest = (TextView) findViewById(R.id.dest_text);
        cid = (TextView) findViewById(R.id.conductor_text);
        no_tickets = (TextView) findViewById(R.id.noOfTickets);
        fare = (TextView) findViewById(R.id.fare_text);
        confirm = (Button) findViewById(R.id.confirm_fare);
        confirm.setVisibility(View.INVISIBLE);

        //Setting variables form shared variable
        final String bus_id = shared.getString("bus_id","Error");
        String route_id = shared.getString("selectedPath","ERROR");
        source = shared.getString("src_ticket","ERROR");
        destination = shared.getString("dest_ticket","ERROR");
        no_tickets_str = shared.getInt("no_of_tickets",0);
        no_childs = shared.getInt("no_of_childs",0);
        final String conductor_id = shared.getString("conductor_id","ERROR");
        cid.setText(conductor_id);


        src_key = Integer.toString(shared.getInt("src_key_ticket",1));
        dest_key = Integer.toString(shared.getInt("dest_key_ticket",1));
        databaseFare = FirebaseDatabase.getInstance().getReference("Distance").child(src_key);
        databaseTicket = FirebaseDatabase.getInstance().getReference("Ticket_Log");
        Log.d("DISTANCE - destination",dest_key);
        Log.d("DISTANCE - source",src_key);

        //Setting text view elements
        busid.setText(bus_id);
        routeid.setText(route_id);
        srcc.setText(source);
        dest.setText(destination);
        no_tickets.setText(Integer.toString(no_tickets_str));

        //getting current date to string
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        formatedDate = frmt.format(c);


        ticketLog = new TicketLog(source,destination,formatedDate,""+no_tickets_str,""+final_fare);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = shared.edit();
//                editor.putInt("Total_tickets",no_tickets_str);
                int t = shared.getInt("Total_tickets",0);
                t = t + no_tickets_str + no_childs;
                editor.putInt("Total_tickets",t);
                int f = shared.getInt("Total_Fare",0);
                f = f + final_fare;
                editor.putInt("final_fare",f);
                editor.commit();
                addTicketToDatabase(bus_id, conductor_id, ticketLog);
                Intent i = new Intent(TicketConfirmation.this,LogoutAndAgainIssueActivity.class);
                startActivity(i);
                return;

            }
        });
    }

    private void addTicketToDatabase(String bus_id,String conductor_id,TicketLog t) {
        String key = databaseTicket.push().getKey();
        databaseTicket.child(bus_id).child(conductor_id).child(key).setValue(t);
        printMessage("Ticket Logged Successfully");

    }

    private void printMessage(String ticket_logged) {
        Toast.makeText(getApplicationContext(),ticket_logged,Toast.LENGTH_SHORT).show();
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
