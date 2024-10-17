package com.curativecare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DonorHomepage extends AppCompatActivity {

    Button request, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_homepage);

        request = findViewById(R.id.btnUserRequest);
        logout = findViewById(R.id.btnLogout);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DonorHomepage.this, ViewUserList.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DonorHomepage.this, DonorLogin.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(DonorHomepage.this, "Please Logout To Exit", Toast.LENGTH_SHORT).show();
    }
}