package com.curativecare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserInfoMedical extends AppCompatActivity {

    TextView txinfo;
    Button btnok;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomedical);
        txinfo=(TextView) findViewById(R.id.txtuserdata);
        btnok=(Button) findViewById(R.id.btnok);
        setdetails();

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }
    public void setdetails(){
        data="Prescription: "+IPaddress.uprescription;
        txinfo.setText(data);
    }
}
