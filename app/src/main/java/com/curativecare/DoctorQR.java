package com.curativecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DoctorQR extends AppCompatActivity {

    Button scanqr, vinfo;
    TextView txqrdata;
    EditText etskey;
    String code,key;
    ProgressDialog pDialog;
    String files[];
    String checkURL = IPaddress.ip+"verifyQR.php";
    //String GetURL = IPaddress.ip+"getKey.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorscan_qrcode);
        scanqr = (Button) findViewById(R.id.btnscanqr);
        vinfo = (Button) findViewById(R.id.btnvinfo);
        txqrdata = (TextView) findViewById(R.id.txtqrdata);
        etskey=(EditText) findViewById(R.id.edtkey);
        //email=(EditText) findViewById(R.id.emailid);

        pDialog = new ProgressDialog(DoctorQR.this);
        pDialog.setMessage("Processing...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

       // email1=email.getText().toString().trim();
        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
                startActivityForResult(intent, 0);
                //getkey();
            }
        });

        vinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key=etskey.getText().toString().trim();
                if(key.equals(IPaddress.skey)){
                    Intent intent=new Intent(DoctorQR.this,UserInfo.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(DoctorQR.this,"Wrong Secret Key", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                code = intent.getStringExtra("SCAN_RESULT");
                IPaddress.code=code;
                details();
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
    public void details(){

        RequestParams params = new RequestParams();
        params.put("code",code);

        pDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(checkURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String success = obj.getString("success");
                    if(success.equals("200")){
                        IPaddress.skey=obj.getString("secretkey");
                        IPaddress.uemail=obj.getString("email");
                        IPaddress.ufname=obj.getString("fname");
                        IPaddress.ulname=obj.getString("lname");
                        IPaddress.ucontactno=obj.getString("contactNo");
                        IPaddress.ubloodgrp=obj.getString("blood");
                        IPaddress.ubp=obj.getString("bp");
                        IPaddress.uallergy=obj.getString("allergy");
                        IPaddress.uproblems=obj.getString("problems");
                        IPaddress.uimagename=obj.getString("imagename");
                        IPaddress.uid=obj.getString("id");
                        txqrdata.setText(code);
                        Toast.makeText(DoctorQR.this,"Secret Key send on registered email id", Toast.LENGTH_SHORT);
                        sendEmail(IPaddress.skey, IPaddress.uemail);
                    }
                    else{
                        IPaddress.skey="";
                        txqrdata.setText("Invalid QRCode");
                        Toast.makeText(DoctorQR.this,"Invalid QRCode", Toast.LENGTH_SHORT);
                    }
                }
                catch (Exception e){
                    Toast.makeText(DoctorQR.this,"JSON Error", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(DoctorQR.this,"Connectivity failed with server!", Toast.LENGTH_SHORT).show();
            }
        });
    }
   /* public void getkey(){

        RequestParams params = new RequestParams();
        params.put("email",email1);

        pDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GetURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String success = obj.getString("success");
                    if(success.equals("200")){
                        IPaddress.skey=obj.getString("secretkey");
                        Toast.makeText(DoctorQR.this,"Secret Key send on registered email id",Toast.LENGTH_SHORT);
                        sendEmail(IPaddress.skey, IPaddress.uemail);
                    }
                    else{
                        txqrdata.setText("Invalid QRCode");
                        Toast.makeText(DoctorQR.this,"Invalid QRCode",Toast.LENGTH_SHORT);
                    }
                }
                catch (Exception e){
                    Toast.makeText(DoctorQR.this,"JSON Error",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(DoctorQR.this,"Connectivity failed with server!",Toast.LENGTH_SHORT).show();
            }
        });
    }
   */ public void sendEmail(final String key, final String mailId) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    SendMailSSL sendMailSSL = new SendMailSSL();
                    //sendMailSSL.EmailSending(mailId, "Reporting mail", message, fl);
                    files = null;
                    sendMailSSL.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mailId, "Verification mail","Verification key "+key,files);
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
