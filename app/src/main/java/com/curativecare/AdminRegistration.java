package com.curativecare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
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

public class AdminRegistration extends AppCompatActivity {
    EditText username,password,conPassword,mobile,hospitalName,address,specialist,mail;
    TextView others;
    Button submit;
    String url=IPaddress.ip+"adminregistration.php";
    ProgressDialog progressDialog;
    ListView listView;
    String areaName="",subAreaName="",experience="";
    Spinner area,subArea,exper;
    String[] exp = {"Select your experience of work","0-1 years","1-2 years","2-3 years","3-4 years","4-5 years","5-6 years","6-7 years","7-8 years","8-9 years","9-10 years","10-11 years","11-12 years","12-13 years","13-14 years","14-15 years","15-16 years","16-17 years","17-18 years","18-19 years","19-20 years","20+ years"};
    StringBuilder choicesString;
    String[] listContent = {"Flu","Cold","Cough","Stomach Pain","Headache", "Back Pain","Knee Pain","Anxiety","sleeping sickness","joint pain"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        username= (EditText) findViewById(R.id.editTextAdminUser);
        password= (EditText) findViewById(R.id.editTextAdminPassword);
        conPassword= (EditText) findViewById(R.id.editTextAdminConfirmPassword);
        mobile= (EditText) findViewById(R.id.editTextAdminMobileNo);
        hospitalName= (EditText) findViewById(R.id.editTextAdminShopName);
        address= (EditText) findViewById(R.id.editTextAdminAddress);
        exper = (Spinner) findViewById(R.id.spinnerDoctorExperience);
        specialist= (EditText) findViewById(R.id.editTextSpecialist);
        others= (TextView) findViewById(R.id.editTextOthers);
        submit= (Button) findViewById(R.id.buttonAdminSubmit);
        area= (Spinner) findViewById(R.id.spinnerArea);
        subArea= (Spinner) findViewById(R.id.spinnerSubArea);
        submit= (Button) findViewById(R.id.buttonAdminSubmit);
        mail= (EditText) findViewById(R.id.editTextAdminMailId);

        final String[] areaname={"Select Area","Pune","Mumbai","Nashik","Beed"};
        ArrayAdapter adapter=new ArrayAdapter(AdminRegistration.this,android.R.layout.simple_spinner_item,areaname);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        area.setAdapter(adapter);

        String[] subareaname={"Select Sub-Area","LONAVALA","TALEGON","DEHUROAD","AKURDI","CHINCHWAD","PIMPRI","DAPODI","SHIVAJINAGAR","NIGDI","BHOSARI","CHAKAN","BANER","HINJEWADI"
                , "HADAPSAR","AAREY MILK COLONY","ANDHERI","ANUSHAKTI NAGAR","BANDRA","BANDRA KURLA COMPLEX","BORIVALI"};
        ArrayAdapter adapter1=new ArrayAdapter(AdminRegistration.this,android.R.layout.simple_spinner_item,subareaname);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        subArea.setAdapter(adapter1);

        ArrayAdapter a2 = new ArrayAdapter(AdminRegistration.this,android.R.layout.simple_spinner_item,exp);
        exper.setAdapter(a2);

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

        exper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 : experience = "";
                        break;
                    default: experience = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    userObject.hospitalName = hospitalName.getText().toString().trim();
                    userObject.address = address.getText().toString().trim();
                    userObject.specialist = specialist.getText().toString();
                    userObject.area = areaName;
                    userObject.mail = mail.getText().toString().trim();
                    userObject.subArea = subAreaName;
                    userObject.choice = others.getText().toString().trim();
                    if (userObject.username.isEmpty() || userObject.password.isEmpty() || userObject.mobile.isEmpty() || userObject.hospitalName.isEmpty() || experience.isEmpty()
                            || userObject.address.isEmpty() || userObject.area.isEmpty() || userObject.subArea.isEmpty() || userObject.specialist.isEmpty() || userObject.mail.isEmpty()) {
                        Toast.makeText(AdminRegistration.this, "Vacant Fields", Toast.LENGTH_SHORT).show();
                    }/*else if(userObject.area.equals("Select Area")|| userObject.subArea.equals("Select Sub-Area")){
                    Toast.makeText(AdminRegistration.this,"Select Valid Area/Sub-Area",Toast.LENGTH_SHORT).show();
                }else if(!(userObject.mobile.length()==10)){
                    Toast.makeText(AdminRegistration.this,"Invalid Mobile Number",Toast.LENGTH_SHORT).show();
                }else if(!(conPassword.getText().toString().trim().equals(userObject.password))){
                    Toast.makeText(AdminRegistration.this,"Password Not Matching",Toast.LENGTH_SHORT).show();
                }*/ else if (userObject.choice.isEmpty()) {
                        userObject.choice = " ";
                        adminRegister(userObject);

                    } else {
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
                    Toast.makeText(AdminRegistration.this, "Waiting for gps location! Make sure gps is on and internet connection is provided.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(AdminRegistration.this);
                dialog.setContentView(R.layout.listview);
                dialog.setTitle("Select");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                listView= (ListView) dialog.findViewById(R.id.listView);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setItemChecked(2, true);
                Button button= (Button) dialog.findViewById(R.id.listViewSubmit);
                ArrayAdapter<String> adapter= new ArrayAdapter<String>(AdminRegistration.this,android.R.layout.simple_list_item_multiple_choice, listContent);
                listView.setAdapter(adapter);
                dialog.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        SparseBooleanArray choices = listView.getCheckedItemPositions();
                        StringBuilder choicesString = new StringBuilder();
                        for (int i = 0; i < choices.size(); i++)
                        {
                            if(choices.valueAt(i) == true)
                                choicesString.append(listContent[choices.keyAt(i)]).append(",");
                        }
                        Toast.makeText(AdminRegistration.this, choicesString, Toast.LENGTH_SHORT).show();
                        others.setText(choicesString);
                    }
                });
            }
        });
    }

    private void adminRegister(UserObject userObject) {
        RequestParams params=new RequestParams();
        params.put("name",userObject.username);
        params.put("password",userObject.password);
        params.put("mobile",userObject.mobile);
        params.put("hospitalname",userObject.hospitalName);
        params.put("address",userObject.address);
        params.put("specialist",userObject.specialist);
        params.put("others",userObject.choice);
        params.put("experience",experience);
        params.put("area",userObject.area);
        params.put("subarea",userObject.subArea);
        params.put("email",userObject.mail);
        params.put("lat",UserData.slat);
        params.put("lon",UserData.slon);
        progressDialog=new ProgressDialog(AdminRegistration.this);
        progressDialog.setMessage("Registering...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result=new String(responseBody);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("success").equals("200")){
                        Toast.makeText(AdminRegistration.this,"Successfully Registered", Toast.LENGTH_SHORT).show();
                        UserData.checkservice=0;
                        Intent intent=new Intent(AdminRegistration.this,AdminLogin.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AdminRegistration.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminRegistration.this,"JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(AdminRegistration.this,"Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
