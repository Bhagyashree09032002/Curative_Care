package com.curativecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BloodRequest extends AppCompatActivity {

    //private String sendRequestURL = IPaddress.ip+"ambulanceRequest.php";
    //private String searchHospitalURL = IPaddress.ip+"searchHospital.php";
    Button request;
    Spinner spinner;

    private ProgressDialog pDialog;
    ArrayList arrayList;

    String selected="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);

        request = findViewById(R.id.reqBlood);
        spinner = findViewById(R.id.spinnerBlood);

        String[] blood = {"Select Blood Group", "O+", "O-", "A+", "A-","B+","B-","AB+","AB-"};
        final ArrayAdapter adapter1 = new ArrayAdapter(BloodRequest.this, android.R.layout.simple_spinner_item, blood);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0 : selected = "";
                        break;
                    default:
                        selected = adapterView.getItemAtPosition(i).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(BloodRequest.this, "Please Select Blood Group To Serarch The Donor!", Toast.LENGTH_SHORT).show();
            }
        });


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selected.isEmpty()){
                    Toast.makeText(BloodRequest.this, "Please Select Blood Group To Serarch The Donor!", Toast.LENGTH_SHORT).show();
                }else{
                    //functionSend();
                    UserData.blood = selected;
                    Intent intent=new Intent(BloodRequest.this, ViewDonorList.class);
                    startActivity(intent);
                }
            }
        });

    }

    /*private void search() {

        RequestParams params = new RequestParams();

        pDialog = new ProgressDialog(BloodRequest.this);
        pDialog.setMessage("Verifying Details..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(searchHospitalURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response = new String(responseBody);
                try {

                    JSONObject object = new JSONObject(response);
                    System.out.println(object);
                    JSONArray array = object.getJSONArray("result");
                    arrayList = new ArrayList();

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object1 = array.getJSONObject(i);

                        String hospital = object1.getString("hospital");
                        String lat = object1.getString("lat");
                        String lon = object1.getString("lon");

                        UserData.hlat = Double.valueOf(lat);
                        UserData.hlon = Double.valueOf(lon);

                        arrayList.add(hospital);
                    }
                    ArrayAdapter aa = new ArrayAdapter(BloodRequest.this, android.R.layout.simple_spinner_item, arrayList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner.setAdapter(aa);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BloodRequest.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(BloodRequest.this, " Error Occured!" ,Toast.LENGTH_LONG).show();
            }

        });
    }

    private void functionSend(){

        RequestParams params = new RequestParams();

        params.put("id",UserData.id);
        params.put("lat",UserData.slat);
        params.put("lon",UserData.slon);
        params.put("mobile",UserData.mobile);
        params.put("status","pending");
        params.put("hospital", selected);
        params.put("firstaid", firstAid);
        UserData.status = "pending";

        final ProgressDialog pDialog = ProgressDialog.show(BloodRequest.this,"Processing","Please Wait...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(sendRequestURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);

                if(res.equals("200")){

                    Toast.makeText(BloodRequest.this, "Ambulance Request Sent!", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(BloodRequest.this, UserHomepage.class);
                    startActivity(intent);

                    //UserData.amb_status = 1;

                }else{
                    Toast.makeText(BloodRequest.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(BloodRequest.this, "Connectivity Failed! Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
