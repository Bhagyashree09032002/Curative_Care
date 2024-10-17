package com.curativecare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SplashScreen extends AppCompatActivity {

    EditText ip;
    Button submit;
    public static String ip1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ip= (EditText) findViewById(R.id.editIPAddress);
        submit= (Button) findViewById(R.id.buttonSubmit);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Mainact.class));
                finish();
            }
        }, 3000);*/
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(SplashScreen.this, LocService.class);
                startService(in);

                ip1=ip.getText().toString().trim();
                Intent intent=new Intent(SplashScreen.this, Mainact.class);
                startActivity(intent);
            }
        });
    }
}
