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

import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.curativecare.SharedPreferences.SharedPreferenceManager;

public class AdminLogin extends AppCompatActivity {

    EditText username,password;
    TextView changeuprofile;
    Button login,register;
    ProgressDialog progressDialog;
    String url=IPaddress.ip+"adminlogin1.php";
    Database mydb;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        mydb = new Database(this);

        username= (EditText) findViewById(R.id.editTextAdminUser);
        password= (EditText) findViewById(R.id.editTextAdminPass);
        login= (Button) findViewById(R.id.buttonAdminLogin);
        register= (Button) findViewById(R.id.buttonAdminRegister);
        changeuprofile = (TextView) findViewById(R.id.doctorChnageUserProfile);
        sharedPreferenceManager=new SharedPreferenceManager(AdminLogin.this);

        checkUserLogin();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(AdminLogin.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AdminLogin.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
                }else{
                    UserData.checkservice =1;
                    Intent in = new Intent(AdminLogin.this,LocService.class);
                    startService(in);
                    Intent intent = new Intent(AdminLogin.this, AdminRegistration.class);
                    startActivity(intent);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(AdminLogin.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AdminLogin.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    UserObject userObject = new UserObject();
                    userObject.username = username.getText().toString().trim();
                    userObject.password = password.getText().toString().trim();
                    if (userObject.username.isEmpty() || userObject.password.isEmpty()) {
                        Toast.makeText(AdminLogin.this, "Please Enter Username/Password", Toast.LENGTH_SHORT).show();
                    } else {
                        login(userObject);
                    }
                }
            }
        });

        changeuprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.deleteUserType();
                Intent i = new Intent(AdminLogin.this,Mainact.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });

    }

    private void login(final UserObject userObject) {
        RequestParams params=new RequestParams();
        params.put("name",userObject.username);
        params.put("password",userObject.password);
        progressDialog=new ProgressDialog(AdminLogin.this);
        progressDialog.setMessage("Verifying Details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result=new String(responseBody);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("success").equals("200")){
                        sharedPreferenceManager.connectDB();
                        sharedPreferenceManager.setString("name",object.getString("name"));
                        sharedPreferenceManager.setString("id",object.getString("id"));
                        sharedPreferenceManager.setString("mobile",object.getString("mobile"));
                        sharedPreferenceManager.setString("hospitalname",object.getString("hospitalname"));
                        sharedPreferenceManager.closeDB();
                        DoctorData.name=object.getString("name");
                        DoctorData.id=object.getString("id");
                        DoctorData.mobile=object.getString("mobile");
                        DoctorData.hospitalname=object.getString("hospitalname");
                        mydb.saveLastUserLogin(userObject.username,userObject.password);
                        Intent intent=new Intent(AdminLogin.this,AdminMainPage.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AdminLogin.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminLogin.this,"JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AdminLogin.this,"Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkUserLogin(){
        Cursor res = mydb.getLastUserLogin();
        if(res.moveToNext()){
            UserObject userObject = new UserObject();
            userObject.username = res.getString(1);
            userObject.password = res.getString(2);
            login(userObject);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1: if(grantResults.length>1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                UserObject userObject = new UserObject();
                userObject.username = username.getText().toString().trim();
                userObject.password = password.getText().toString().trim();
                if (userObject.username.isEmpty() || userObject.password.isEmpty()) {
                    Toast.makeText(AdminLogin.this, "Please Enter Username/Password", Toast.LENGTH_SHORT).show();
                } else {
                    login(userObject);
                }
            }
                break;

            case 2: if(grantResults.length>1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                UserData.checkservice =1;
                Intent in = new Intent(AdminLogin.this,LocService.class);
                startService(in);
                Intent intent = new Intent(AdminLogin.this, AdminRegistration.class);
                startActivity(intent);
            }
            break;
        }
    }
}
