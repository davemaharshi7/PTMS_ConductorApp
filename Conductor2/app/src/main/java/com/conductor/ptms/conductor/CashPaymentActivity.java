package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CashPaymentActivity extends AppCompatActivity {

    private Button finalCashButton;
    private TextView actualTicketCost;
    private TextView TotalTicketCost;
    private String fareCollectedKey;
    private SharedPreferences shared;
    private DatabaseReference databaseTicket,databaseFareCollected;
    private int CASH_PAYMENT = 0;
    private int CARD_PAYMENT = 1;
    private int final_fare,lastFare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);

        finalCashButton = findViewById(R.id.cashConfirmButton);
        actualTicketCost = findViewById(R.id.ActualTicketCost);
        TotalTicketCost = findViewById(R.id.FinalCashTicketAmount);
        databaseTicket = FirebaseDatabase.getInstance().getReference("Ticket_Log");
        SharedPreferences.Editor editor = shared.edit();
        databaseFareCollected = FirebaseDatabase.getInstance().getReference("Fare_Collected_Status");
        final_fare = shared.getInt("Ticket_FARE",0);
        fareCollectedKey= shared.getString("FareCollectedKey","0");
        Log.d("FARE CHECK",fareCollectedKey);

        actualTicketCost.setText(""+final_fare+" INR");
        final_fare += 30;
        TotalTicketCost.setText(""+final_fare+" INR");


        finalCashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logTicket();
                Intent i = new Intent(getApplicationContext(),LogoutAndAgainIssueActivity.class);
                startActivity(i);
                finish();
                return;
            }

            private void logTicket() {
                String key = databaseTicket.push().getKey();
                String conductor_id = shared.getString("conductor_id","ERROR");
                String bus_id = shared.getString("bus_id","Error");
                String source = shared.getString("src_ticket","ERROR");
                String destination = shared.getString("dest_ticket","ERROR");
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
                String formatedDate = frmt.format(c);
                int totalNumberOfIssued = shared.getInt("totalNumberOfIssued",0);

                final_fare = shared.getInt("Ticket_FARE",0);
                final_fare += 30;

                TicketLog ticketLog = new TicketLog(source,destination,formatedDate,""+totalNumberOfIssued,
                        ""+final_fare,CASH_PAYMENT);
                databaseTicket.child(bus_id).child(conductor_id).child(key).setValue(ticketLog);

                //TODO:changed

                int lastUpdatedFare = lastFare + final_fare;
                Log.d("LastUpdatedFare",""+lastUpdatedFare);
                databaseFareCollected.child(fareCollectedKey).child("totalFare").setValue(lastUpdatedFare);
                Log.d("LastUpdatedFare",""+lastUpdatedFare + " Saved");


                printMessage("Ticket Logged Successfully");

            }
        });
    }

    private void printMessage(String ticket_logged_successfully) {
        Toast.makeText(getApplicationContext(),ticket_logged_successfully,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseFareCollected.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lastFare = dataSnapshot.child(fareCollectedKey).child("totalFare").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
