package com.curativecare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.samira.bloodbank.SharedPreferences.SharedPreferenceManager;
import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DonorLogin extends AppCompatActivity {

    EditText username,password;
    TextView changeuprofile;
    Button login,register;
    ProgressDialog progressDialog;
    String url=IPaddress.ip+"adminlogin.php";

    //SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_login);


        username= (EditText) findViewById(R.id.editTextdonorUser);
        password= (EditText) findViewById(R.id.editTextPass);
        login= (Button) findViewById(R.id.buttondonorLogin);
        register= (Button) findViewById(R.id.buttondonorRegister);
        //changeuprofile = (TextView) findViewById(R.id.ChnageUserProfile);
        //sharedPreferenceManager=new SharedPreferenceManager(DonorLogin.this);

       // checkUserLogin();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonorLogin.this, DonorRegistration.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserObject userObject = new UserObject();
                userObject.username = username.getText().toString().trim();
                userObject.password = password.getText().toString().trim();
                if (userObject.username.isEmpty() || userObject.password.isEmpty()) {
                    Toast.makeText(DonorLogin.this, "Vacant Fields", Toast.LENGTH_SHORT).show();
                } else {
                    login(userObject);
                }
            }
        });

       /* changeuprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DonorLogin.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });*/

    }

    private void login(final UserObject userObject) {
        RequestParams params = new RequestParams();
        params.put("name", userObject.username);
        params.put("password", userObject.password);
        progressDialog = new ProgressDialog(DonorLogin.this);
        progressDialog.setMessage("Verifying Details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                System.out.print(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String success = obj.getString("success");

                    if(success.equals("200")){

                        progressDialog.dismiss();
                        Toast.makeText(DonorLogin.this, "Donor Login Successfully...", Toast.LENGTH_LONG).show();

                        UserData.id = obj.getString("id");
                        UserData.name = obj.getString("name");
                        UserData.mobile = obj.getString("mobile");
                        //UserData.blood = obj.getString("blood");
                        //UserData.checkservice = 1;
                        /*Intent in = new Intent(DonorLogin.this,LocService.class);
                        startService(in);*/
                        Intent intent=new Intent(DonorLogin.this,DonorHomepage.class);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(DonorLogin.this,message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(DonorLogin.this,"JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
            }
        });
    }



}
