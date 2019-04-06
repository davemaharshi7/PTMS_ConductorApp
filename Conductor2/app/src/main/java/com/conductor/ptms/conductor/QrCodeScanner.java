package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QrCodeScanner extends AppCompatActivity implements View.OnClickListener {

    private Button buttonScan,btnNext;
    private TextView textData;
    private IntentIntegrator qrScan;
    SharedPreferences shared;
    private FirebaseAuth mAuth;
    private String prevBusID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        shared = getSharedPreferences("Bus_Data",Context.MODE_PRIVATE); // get the set of Preferences labeled "A"
        String BUSID = shared.getString("bus_id","NOT");
        Log.i("FIRST",BUSID);
//        if(!BUSID.equals("NOT"))
//        {
//            Log.i("FIRST",BUSID+" INSIDE");
//
//            Intent i = new Intent(getApplicationContext(), SrcDestinationActivity.class);
//            startActivity(i);
//            finish();
//        }


        mAuth = FirebaseAuth.getInstance();
        buttonScan = (Button) findViewById(R.id.qrScanner);
        textData = (TextView) findViewById(R.id.qrData);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setVisibility(View.INVISIBLE);

        qrScan = new IntentIntegrator(this);
        buttonScan.setOnClickListener(this);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QrCodeScanner.this,SrcDestinationActivity.class);
                startActivity(i);
//                finish();
                return;
            }
        });

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
                    editor.putString("bus_id",obj.getString("busid"));
                    editor.commit();
                    prevBusID = obj.getString("busid");
                    textData.setText(obj.getString("busid"));
                    btnNext.setVisibility(View.VISIBLE);
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
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("bus_id", prevBusID);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        String bus_id = savedInstanceState.getString("bus_id");
//        Intent i = new Intent(getApplicationContext(), SrcDestinationActivity.class);
//        startActivity(i);
//        finish();
//    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
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

    private void logout() {
        mAuth.signOut();
        changeToLoginActivity();
    }

    private void changeToLoginActivity() {
        Intent i = new Intent(QrCodeScanner.this,Login.class);
        startActivity(i);
        finish();
        return;
    }
}
