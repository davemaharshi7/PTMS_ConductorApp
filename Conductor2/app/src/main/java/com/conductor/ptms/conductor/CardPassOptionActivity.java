package com.conductor.ptms.conductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CardPassOptionActivity extends AppCompatActivity {
    Button card,passCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pass_option);

        card = findViewById(R.id.throughCard);
        passCard = findViewById(R.id.throughPass);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),IssueTicketActivity.class);
                startActivity(intent1);
            }
        });
        passCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ADD QR CODE SCANNER
                Intent intent2 = new Intent(getApplicationContext(),ScanPassActivity.class);
                startActivity(intent2);


            }
        });
    }
}
