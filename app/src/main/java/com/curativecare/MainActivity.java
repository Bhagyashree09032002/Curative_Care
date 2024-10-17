package com.curativecare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button pregister,dregister,medicle;
    static int type = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pregister = (Button) findViewById(R.id.buttonpregister);
        dregister = (Button) findViewById(R.id.buttondregister);




        pregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DonorLogin.class);
                startActivity(intent);
            }
        });
        dregister.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Welcome.class);
                startActivity(intent);
            }
        });

    }


}
