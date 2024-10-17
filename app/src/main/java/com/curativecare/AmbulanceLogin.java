package com.curativecare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.curativecare.SharedPreferences.SharedPreferenceManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AmbulanceLogin extends AppCompatActivity {

    EditText username,password;
    Button login,register;
    TextView changeuprofile;
    ProgressDialog progressDialog;
    String AmbulanceURL=IPaddress.ip+"driverlogin.php";
    String username1="", password1="";
    Database mydb;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_login);


        username= (EditText) findViewById(R.id.editUser);
        password= (EditText) findViewById(R.id.editPass);
        login= (Button) findViewById(R.id.buttonLogin);
        changeuprofile = (TextView) findViewById(R.id.bloodChnageUserProfile);
        sharedPreferenceManager=new SharedPreferenceManager(AmbulanceLogin.this);

        mydb = new Database(this);

        checkUserLogin();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(ActivityCompat.checkSelfPermission(AmbulanceLogin.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AmbulanceLogin.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    username1 = username.getText().toString().trim();
                    password1 = password.getText().toString().trim();
                    if (username1.isEmpty() || password1.isEmpty()) {
                        Toast.makeText(AmbulanceLogin.this, "Please Enter Username / Password", Toast.LENGTH_SHORT).show();
                    } else {
                        login();
                    }
                }
            }
        });

        changeuprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.deleteUserType();
                Intent i = new Intent(AmbulanceLogin.this,Mainact.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });

    }

    private void login() {
        RequestParams params=new RequestParams();
        params.put("mobile",username1);
        params.put("password",password1);

        params.put("curlat", UserData.slat);
        params.put("curlon", UserData.slon);

        progressDialog=new ProgressDialog(AmbulanceLogin.this);
        progressDialog.setMessage("Verifying Details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient client=new AsyncHttpClient();
        client.post(AmbulanceURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();

                String response = new String(responseBody);
                System.out.print(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String success = obj.getString("success");

                    if(obj.getString("success").equals("200")){
                        mydb.saveLastUserLogin(username1,password1);
                        Intent intent=new Intent(AmbulanceLogin.this, ViewRequest.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AmbulanceLogin.this,obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AmbulanceLogin.this,"JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(AmbulanceLogin.this,"Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void checkUserLogin(){
        Cursor res = mydb.getLastUserLogin();
        if(res.moveToNext()){
            username1 = res.getString(1);
            password1 = res.getString(2);
            login();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1: if(grantResults.length>1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                username1 = username.getText().toString().trim();
                password1 = password.getText().toString().trim();
                if (username1.isEmpty() || password1.isEmpty()) {
                    Toast.makeText(AmbulanceLogin.this, "Please Enter Username/Password", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
                break;
        }
    }

}
