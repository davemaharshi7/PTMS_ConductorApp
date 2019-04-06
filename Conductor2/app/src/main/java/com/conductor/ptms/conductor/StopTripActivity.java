package com.conductor.ptms.conductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StopTripActivity extends AppCompatActivity {

    private Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_trip);

        stop = findViewById(R.id.stopTripButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : completely stop Location sharing
            Intent i = new Intent(getApplicationContext(),LocationService.class);
            stopService(i);
            Intent a = new Intent(getApplicationContext(),SrcDestinationActivity.class);
            startActivity(a);
            finish();
            }
        });
    }
}
