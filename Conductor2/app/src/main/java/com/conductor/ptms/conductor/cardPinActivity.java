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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class cardPinActivity extends AppCompatActivity {
    private DatabaseReference myRefCard,databaseTicket,databaseFareCollected;
    private String cardId,cardName,cardPhone,cardPin;
    private EditText inputPin;
    private Integer cardBalance;
    private TextView textViewCardId,textViewUserName,textViewPhone;
    private SharedPreferences shared;
    private int ticketFare;
    private int lastFare;
    private String fareCollectedKey;
    private Button checkPin;
    private int CASH_PAYMENT = 0;
    private int CARD_PAYMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pin);

        cardId = getIntent().getStringExtra("cardID");
        cardName = getIntent().getStringExtra("cardName");
        cardPhone = getIntent().getStringExtra("cardPhone");

        myRefCard = FirebaseDatabase.getInstance().getReference("Card_Details");
        inputPin = findViewById(R.id.inputPin);
        checkPin = findViewById(R.id.confirmPinButton);
        textViewCardId = findViewById(R.id.scannedCardId2);
        textViewUserName = findViewById(R.id.userNameCard2);
        textViewPhone = findViewById(R.id.userPhoneCard2);

        setTitle("Verify PIN Code");
        databaseTicket = FirebaseDatabase.getInstance().getReference("Ticket_Log");



        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
        ticketFare = shared.getInt("Ticket_FARE",0);
        Toast.makeText(getApplicationContext(),""+ticketFare,Toast.LENGTH_SHORT).show();
        textViewCardId.setText(cardId);
        textViewPhone.setText(cardPhone);
        textViewUserName.setText(cardName);
        fareCollectedKey= shared.getString("FareCollectedKey","0");
        Log.d("FARE CHECK",fareCollectedKey);
        printMessage("key is "+fareCollectedKey);


        checkPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPin.getText().toString().isEmpty())
                {
                    inputPin.setError("Please Enter your Pin");
                    inputPin.requestFocus();
                    return;
                }
                if(inputPin.getText().toString().length() != 4)
                {
                    inputPin.setError("Pin Must be of 4 digits only");
                    inputPin.requestFocus();
                    return;
                }
                String pin = inputPin.getText().toString();
                String encrptyString = encryptAlgo(pin);
                Log.d("ENCRYPT",encrptyString);
                if(cardPin.equals(encrptyString)){
//                    TODO: deductMoneyFromAccount
                    deductMoneyFromAccount();
                }
                else {
                    Toast.makeText(getApplicationContext(),"PIN INVALID",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void deductMoneyFromAccount() {
        int difference = cardBalance - ticketFare;
        if(difference >= 0){
            myRefCard.child(cardId).child("Balance").setValue(difference);




            updateTicketLog();
            Intent i = new Intent(getApplicationContext(),LogoutAndAgainIssueActivity.class);
            startActivity(i);
            finish();
            return;
        }else {
            //TODO: HANDLE CONDITION WHEN USER DOESNOT HAVE SUFFICENT BALANCE
            Toast.makeText(getApplicationContext(),"Insufficient Balance!",Toast.LENGTH_SHORT)
                    .show();
            Intent insufficientBalance = new Intent(getApplicationContext(),CashPaymentActivity
                    .class);
            startActivity(insufficientBalance);

            return;
        }

    }

    private void updateTicketLog() {
        SharedPreferences.Editor editor = shared.edit();
//                editor.putInt("Total_tickets",no_tickets_str);
        String key = databaseTicket.push().getKey();
        String conductor_id = shared.getString("conductor_id","ERROR");
        String bus_id = shared.getString("bus_id","Error");
        String source = shared.getString("src_ticket","ERROR");
        String destination = shared.getString("dest_ticket","ERROR");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDate = frmt.format(c);
        int totalNumberOfIssued = shared.getInt("totalNumberOfIssued",0);

        int final_fare = shared.getInt("Ticket_FARE",0);

        TicketLog ticketLog = new TicketLog(source,destination,formatedDate,""+totalNumberOfIssued,
                ""+final_fare,CARD_PAYMENT);

        databaseTicket.child(bus_id).child(conductor_id).child(key).setValue(ticketLog);


        printMessage("Ticket Logged Successfully");
        //addTicketToDatabase(bus_id, conductor_id, ticketLog);

    }

    private void printMessage(String ticket_logged_successfully) {
        Toast.makeText(getApplicationContext(),ticket_logged_successfully,Toast.LENGTH_SHORT).show();
    }

    private String encryptAlgo(String pin) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(pin.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        myRefCard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardBalance = dataSnapshot.child(cardId).child("Balance").getValue(Integer.class);
                cardPin = dataSnapshot.child(cardId).child("pin").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
