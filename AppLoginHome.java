package com.example.jaroga.applogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;

public class AppLoginHome extends AppCompatActivity {

    Button multimedia,realTime,storage;



    public static final String user="names";
    TextView txtUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login_home);



        txtUser=(TextView) findViewById(R.id.textser);
        String user= getIntent().getStringExtra("names");

        txtUser.setText("¡Bienvenid@ "+ user +"!");

        realTime = findViewById(R.id.btnRealTime);
        realTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AppLoginHome.this, RealTime.class);
                startActivity(intent);
            }
        });

        multimedia= (Button)findViewById(R.id.btnMultimedia);
        multimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent anterior = new Intent(AppLoginHome.this, AppLoginMultimedia.class);
                startActivity(anterior);




            }
        });


        storage = (Button) findViewById(R.id.btnStorage);
        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent st = new Intent(AppLoginHome.this, Estorage.class);
                startActivity(st);
            }
        });


    }
}
