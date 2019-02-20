package com.conductor.ptms.conductor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {


    private TextView log;
    private EditText email;
    private EditText pass;
    String eemail;
    private Button btn, reset;
    private ProgressBar pgbar;
    private FirebaseAuth mAuth;
    SharedPreferences shared;
    DatabaseReference databaseConductor;

    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        pgbar = (ProgressBar) findViewById(R.id.progressBar2);
        pgbar.setVisibility(View.INVISIBLE);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        btn = (Button) findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
        databaseConductor = FirebaseDatabase.getInstance().getReference("ConductorDetails");

        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
//            editor.putString("key_name", "string value");mAuth.getCurrentUser().getEmail();
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("email",mAuth.getCurrentUser().getEmail());
            editor.commit();
            changeActivity();
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgbar.setVisibility(View.VISIBLE);
                btn.setVisibility(View.INVISIBLE);
                final String log_email = email.getText().toString().trim();
                final String log_pass = pass.getText().toString().trim();
                eemail = log_email;
                if (log_email.isEmpty() || log_pass.isEmpty()) {
                    showMessage("Please Enter All Fields");
                    pgbar.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
                } else {
                    signIn(log_email, log_pass);
                }
            }


        });

    }

    public void signIn(final String log_email, String log_pass) {
        mAuth.signInWithEmailAndPassword(log_email, log_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    pgbar.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);

                    databaseConductor.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Conductor[] arr = new Conductor[10];
                            for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
//                    String conductor_key = areaSnapshot.getKey();
//                    int i = 0;
                                String C_Email = areaSnapshot.child("C_Email").getValue(String.class);
//                                Conductor c = areaSnapshot.getValue(Conductor.class);
//                    arr[i] = c;
//                    i++;
                                if(TextUtils.equals(C_Email,log_email)){
                                    String key = areaSnapshot.getKey();
                                    Log.d("CID:",key);
                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("conductor_id",key);
                                    editor.commit();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    changeActivity();

                } else {
                    showMessage("Login Error Occured : " + task.getException().getMessage());
                    pgbar.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
                }
            }
        });


    }
    public void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

    }

    public void changeActivity() {

        Intent home = new Intent(Login.this,QrCodeScanner.class);
        startActivity(home);
        finish();
        return;

    }
}