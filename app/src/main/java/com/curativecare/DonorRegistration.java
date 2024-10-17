package com.curativecare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class DonorRegistration extends AppCompatActivity {
    EditText username,password,conPassword,mobile,hospitalName,address,specialist,mail;
    TextView others,fascilities;
    Button submit;
    String[] hname;
    String url=IPaddress.ip+"donorregistration.php";
    ProgressDialog progressDialog;
    ListView listView;
    String areaName="",subAreaName="",ye;
    Spinner area,subArea,blood;
    StringBuilder choicesString;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);
        username= (EditText) findViewById(R.id.editTextdonorUser);
        password= (EditText) findViewById(R.id.editTextdonorPassword);
        conPassword= (EditText) findViewById(R.id.editTextdonorConfirmPassword);
        mobile= (EditText) findViewById(R.id.editTextdonorMobileNo);
        address= (EditText) findViewById(R.id.editTextdonorAddress);
        submit= (Button) findViewById(R.id.buttondonorSubmit);
        area= (Spinner) findViewById(R.id.spinnerArea);
        subArea= (Spinner) findViewById(R.id.spinnerSubArea);
        blood= (Spinner) findViewById(R.id.spinnerBlood1);
        submit= (Button) findViewById(R.id.buttondonorSubmit);
        mail= (EditText) findViewById(R.id.editTextdonorMailId);

        final String[] areaname={"Select Area","Pune","Mumbai"};
        ArrayAdapter adapter=new ArrayAdapter(DonorRegistration.this,android.R.layout.simple_spinner_item,areaname);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        area.setAdapter(adapter);

        String[] subareaname={"Select Sub-Area","LONAVALA","TALEGON","DEHUROAD","AKURDI","CHINCHWAD","PIMPRI","DAPODI","SHIVAJINAGAR","KATRAJ","NIGDI","BHOSARI","CHAKAN","BANER","HINJEWADI"
                , "HADAPSAR","AAREY MILK COLONY","ANDHERI","ANUSHAKTI NAGAR","BANDRA","BANDRA KURLA COMPLEX","BORIVALI"};
        ArrayAdapter adapter1=new ArrayAdapter(DonorRegistration.this,android.R.layout.simple_spinner_item,subareaname);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        subArea.setAdapter(adapter1);



        String[] year1 = {"Select Blood Group", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        final ArrayAdapter adapter2 = new ArrayAdapter(DonorRegistration.this, android.R.layout.simple_spinner_item, year1);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
        blood.setAdapter(adapter2);

        blood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        ye = "";
                        break;
                    default:
                        ye = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                areaName=adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        subArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subAreaName=adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserData.slat.isEmpty() && !UserData.slon.isEmpty()) {
                    final UserObject userObject = new UserObject();
                    userObject.username = username.getText().toString().trim();
                    userObject.password = password.getText().toString().trim();
                    userObject.mobile = mobile.getText().toString().trim();
                    userObject.address = address.getText().toString().trim();
                    userObject.area = areaName;
                    userObject.mail = mail.getText().toString().trim();
                    userObject.subArea = subAreaName;


                    if (userObject.username.isEmpty() || userObject.password.isEmpty() || userObject.mobile.isEmpty() || userObject.address.isEmpty() || userObject.area.isEmpty() || userObject.subArea.isEmpty() || userObject.mail.isEmpty()) {
                        Toast.makeText(DonorRegistration.this, "Vacant Fields", Toast.LENGTH_SHORT).show();
                    }
                     else {
                        if (Pattern.matches("^([0-9]){10}$", userObject.mobile)) {
                            if (Pattern.matches("^([A-Za-z0-9_\\.\\$])+\\@([A-Za-z0-9_])+\\.+com$", userObject.mail)) {
                                adminRegister(userObject);
                            } else {
                                mail.setError("Invalid email ID!");
                            }
                        } else {
                            mobile.setError("Invalid mobile number!");
                        }
                    }

                }else{
                    Toast.makeText(DonorRegistration.this, "Waiting for gps location! Make sure gps is on and internet connection is provided.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void adminRegister(UserObject userObject) {
        RequestParams params = new RequestParams();
        params.put("name", userObject.username);
        params.put("password", userObject.password);
        params.put("mobile", userObject.mobile);
        params.put("address", userObject.address);
        params.put("area", userObject.area);
        params.put("subarea", userObject.subArea);
        params.put("email", userObject.mail);
        params.put("blood",ye);

        progressDialog = new ProgressDialog(DonorRegistration.this);
        progressDialog.setMessage("Registering...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("success").equals("200")) {
                        Toast.makeText(DonorRegistration.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        //UserData.checkservice = 0;
                        Intent intent = new Intent(DonorRegistration.this, DonorLogin.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(DonorRegistration.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DonorRegistration.this, "JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(DonorRegistration.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
