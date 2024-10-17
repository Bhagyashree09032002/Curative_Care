package com.curativecare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.curativecare.SharedPreferences.SharedPreferenceManager;

public class Medical extends AppCompatActivity {

    EditText username,password;
    Button login,register;
    TextView changeuprofile;
    ProgressDialog progressDialog;
    String url=IPaddress.ip+"medlogin.php";
    String username1="",password1="";
    Database mydb;
    SharedPreferenceManager sharedPreferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

        username= (EditText) findViewById(R.id.editTextLogUser);
        password= (EditText) findViewById(R.id.editTextLogPass);
        login= (Button) findViewById(R.id.buttonLogin);
        changeuprofile = (TextView) findViewById(R.id.medicalChnageUserProfile);
        sharedPreferenceManager=new SharedPreferenceManager(Medical.this);

        mydb = new Database(this);

        checkUserLogin();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(ActivityCompat.checkSelfPermission(Medical.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Medical.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    username1 = username.getText().toString().trim();
                    password1 = password.getText().toString().trim();
                    if (username1.isEmpty() || password1.isEmpty()) {
                        Toast.makeText(Medical.this, "Please Enter Username / Password", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(Medical.this,Mainact.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });

    }

    private void login() {
        RequestParams params=new RequestParams();
        params.put("name",username1);
        params.put("password",password1);
        progressDialog=new ProgressDialog(Medical.this);
        progressDialog.setMessage("Verifying Details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient client=new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
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
                        Intent intent=new Intent(Medical.this,ScanQRCode.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(Medical.this,obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Medical.this,"JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(Medical.this,"Error Occurred", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Medical.this, "Please Enter Username/Password", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
                break;
        }
    }
}
