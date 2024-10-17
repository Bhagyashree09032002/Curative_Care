package com.curativecare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;


public class Mainact extends AppCompatActivity {
    LinearLayout pregister,dregister,medicle,ambulance,bloodBank;
    static int type = 0;
    Database mydb;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainact);

        mydb = new Database(this);
        pregister = (LinearLayout) findViewById(R.id.buttonpregister);
        dregister = (LinearLayout) findViewById(R.id.buttondregister);
        medicle = (LinearLayout) findViewById(R.id.buttonmedicle);

        ambulance = (LinearLayout) findViewById(R.id.buttonAmbulanceLogin);
        bloodBank = (LinearLayout) findViewById(R.id.buttonblood);

        checkUserType();

        pregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                mydb.saveUserType(type);
                Intent intent = new Intent(Mainact.this, Welcome.class);
                startActivity(intent);
            }
        });
        dregister.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                type = 2;
                mydb.saveUserType(type);
                Intent intent = new Intent(Mainact.this, AdminLogin.class);
                startActivity(intent);
            }
        });
        medicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 3;
                mydb.saveUserType(type);
                Intent intent = new Intent(Mainact.this, Medical.class);
                startActivity(intent);
            }
        });
        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 4;
                mydb.saveUserType(type);
                Intent intent = new Intent(Mainact.this, AmbulanceLogin.class);
                startActivity(intent);
            }
        });

        bloodBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 5;
                mydb.saveUserType(type);
                Intent intent = new Intent(Mainact.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void checkUserType(){
        int type = mydb.getUserType();
        if(type==1){
            Intent intent = new Intent(Mainact.this, Welcome.class);
            startActivity(intent);
        }else if(type==2){
            Intent intent = new Intent(Mainact.this, AdminLogin.class);
            startActivity(intent);
        }else if(type==3){
            Intent intent = new Intent(Mainact.this, Medical.class);
            startActivity(intent);
        }else if(type==4){
            Intent intent = new Intent(Mainact.this, AmbulanceLogin.class);
            startActivity(intent);
        }else if(type==5){
            Intent intent = new Intent(Mainact.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
