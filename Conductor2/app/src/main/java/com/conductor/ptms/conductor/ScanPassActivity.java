package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanPassActivity extends AppCompatActivity implements View.OnClickListener{
    private IntentIntegrator qrScan;
    private Button scanPass,next;
    private FirebaseAuth mAuth;
    private TextView textViewPassId,textViewCardId;
    private SharedPreferences shared;
    private DatabaseReference myRefPassDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pass);

        setTitle("Scan Card");
        mAuth = FirebaseAuth.getInstance();
        qrScan = new IntentIntegrator(this);
        scanPass = findViewById(R.id.scanPassButton);
        next = findViewById(R.id.passNextButton);
        shared = getSharedPreferences("Bus_Data",Context.MODE_PRIVATE);

        textViewCardId = findViewById(R.id.cardIDofPass);
        textViewPassId = findViewById(R.id.passID);
        myRefPassDetails = FirebaseDatabase.getInstance().getReference("Pass_Details");

        scanPass.setOnClickListener(this);
        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),VerifySrcDestOfPassActivity.class);
                i.putExtra("cardID",textViewCardId.getText().toString());
                i.putExtra("passID",textViewPassId.getText().toString());
//                i.putExtra("cardPhone",textViewPhone.getText().toString());
                startActivity(i);
                finish();
                return;
            }
        });
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews

                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("passID",obj.getString("passID"));
                    editor.commit();
                    final String passID = obj.getString("passID");
                    textViewPassId.setText(passID);
                    next.setVisibility(View.VISIBLE);
                    if(!passID.isEmpty())
                    {
//                        myRefPassDetails.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String cardID = dataSnapshot.child(passID).child("cardId").getValue(String.class);
////                                String Name = dataSnapshot.child(passID).child("Name").getValue(String.class);
//                                textViewCardId.setText(cardID);
////                                textViewPhone.setText(MobNo);
//                                SharedPreferences.Editor ed = shared.edit();
////                                ed.putString("cardName",Name);
////                                ed.putString("cardPhone",MobNo);
//                                ed.commit();                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
