package com.curativecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UserHomepage extends AppCompatActivity {

    TextView heading;
    LinearLayout search,add,edit,logout,bloodRequest;
    Button bsearch,badd,bedit,blogout,bambulance;
    Database mydb;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        heading = (TextView) findViewById(R.id.textviewUserhomeHeading);
        search = (LinearLayout) findViewById(R.id.userHomeSearchDoctor);
        add = (LinearLayout) findViewById(R.id.userHomeAddReport);
        edit = (LinearLayout) findViewById(R.id.userHomeEditProfile);
        logout = (LinearLayout) findViewById(R.id.userHomeLogout);
        bloodRequest = (LinearLayout) findViewById(R.id.userBloodRequest);
        bsearch = (Button) findViewById(R.id.btnuserHomeSearchDoctor);
        badd = (Button) findViewById(R.id.btnuserHomeAddReport);
        bedit = (Button) findViewById(R.id.btnuserHomeEditProfile);
        bambulance = (Button) findViewById(R.id.btnRequestAmbulance);
        blogout = (Button) findViewById(R.id.btnuserHomeLogout);

        mydb = new Database(this);

        if(UserData.name==null){
            Intent i = new Intent(UserHomepage.this,Welcome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }

        heading.setText("Hi! "+UserData.name);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this, UserMainPage.class);
                startActivity(i);
            }
        });

        bloodRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this, BloodRequest.class);
                startActivity(i);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this, AddReport.class);
                startActivity(i);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this, EditDetails.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData.checkservice = 0;
                mydb.deleteLastUserLogin();
                Intent i = new Intent(UserHomepage.this,Welcome.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this,UserMainPage.class);
                startActivity(i);
            }
        });

        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPaddress.uid = UserData.id;
                Intent i = new Intent(UserHomepage.this,AddReport.class);
                startActivity(i);
            }
        });

        bedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this,EditDetails.class);
                startActivity(i);
            }
        });

        bambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomepage.this,RequestAmbulance.class);
                startActivity(i);
            }
        });
        

        blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.deleteLastUserLogin();
                Intent i = new Intent(UserHomepage.this,Welcome.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, "To go to login page logout your account!", Toast.LENGTH_SHORT).show();
    }
}
