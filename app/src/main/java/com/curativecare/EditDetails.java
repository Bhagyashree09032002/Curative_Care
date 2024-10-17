package com.curativecare;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class EditDetails extends AppCompatActivity {

    TextView tfname,tmname,tlname,temail,tdob,tbp,tallergy,thealthissues;
    Button confirm,editfname,editmname,editlname,editemail,editdob,editbp,editallergy,edithealth;
    DatePickerDialog datepicker;
    ProgressDialog pDialog;
    Calendar myCalendar = Calendar.getInstance();
    String fname="",mname="",lname="",email="",dob="",bp="",allergy="",healthissues="";
    String saveUserInfoURL = IPaddress.ip+"edituser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);

        tfname = (TextView) findViewById(R.id.editEditUserFirstName);
        tmname = (TextView) findViewById(R.id.editEditUserMiddleName);
        tlname = (TextView) findViewById(R.id.editEditUserLastName);
        temail = (TextView) findViewById(R.id.editEditEmail);
        tdob = (TextView) findViewById(R.id.editEditDob);
        tbp = (TextView) findViewById(R.id.editEditBP);
        tallergy = (TextView) findViewById(R.id.editEditAllergy);
        thealthissues = (TextView) findViewById(R.id.editEditHealthIssues);
        editfname = (Button) findViewById(R.id.btnEditUserFirstName);
        editmname = (Button) findViewById(R.id.btnEditUserMiddleName);
        editlname = (Button) findViewById(R.id.btnEditUserLastName);
        editemail = (Button) findViewById(R.id.btnEditEmail);
        editdob = (Button) findViewById(R.id.btnEditDob);
        editbp = (Button) findViewById(R.id.btnEditBP);
        editallergy = (Button) findViewById(R.id.btnEditAllergy);
        edithealth = (Button) findViewById(R.id.btnEditHealthIssues);
        confirm = (Button) findViewById(R.id.btnEditUserInfoSave);

        if(UserData.name==null){
            Intent i = new Intent(EditDetails.this,Welcome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }

        pDialog = new ProgressDialog(EditDetails.this);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("processing...");
        pDialog.setCancelable(false);

        final DatePickerDialog.OnDateSetListener dat = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        tfname.setText(UserData.name);
        tmname.setText(UserData.mname);
        tlname.setText(UserData.lname);
        temail.setText(UserData.email);
        tdob.setText(UserData.dob);
        tbp.setText(UserData.bp);
        tallergy.setText(UserData.allergy);
        thealthissues.setText(UserData.problems);

        editfname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tfname.setError(null);
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit first name");
                alert.setMessage("Type new first name");
                final EditText ed = new EditText(EditDetails.this);
                ed.setPadding(8,8,8,8);
                ed.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                ed.setMaxLines(1);
                ed.setMaxEms(10);
                ed.setGravity(View.TEXT_ALIGNMENT_CENTER);
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fname = ed.getText().toString().trim();
                        tfname.setText(fname);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        editmname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmname.setError(null);
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit middle name");
                alert.setMessage("Type new middle name");
                final EditText ed = new EditText(EditDetails.this);
                ed.setPadding(8,8,8,8);
                ed.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                ed.setMaxLines(1);
                ed.setMaxEms(10);
                ed.setGravity(View.TEXT_ALIGNMENT_CENTER);
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mname = ed.getText().toString().trim();
                        tmname.setText(mname);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        editlname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlname.setError(null);
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit last name");
                alert.setMessage("Type new last name");
                final EditText ed = new EditText(EditDetails.this);
                ed.setPadding(8,8,8,8);
                ed.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                ed.setMaxLines(1);
                ed.setMaxEms(10);
                ed.setGravity(View.TEXT_ALIGNMENT_CENTER);
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lname = ed.getText().toString().trim();
                        tlname.setText(lname);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        editemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temail.setError(null);
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit email ID");
                alert.setMessage("Type new email ID");
                final EditText ed = new EditText(EditDetails.this);
                ed.setPadding(8,8,8,8);
                ed.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                ed.setMaxLines(1);
                ed.setGravity(View.TEXT_ALIGNMENT_CENTER);
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        email = ed.getText().toString().trim();
                        temail.setText(email);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        editdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker = new DatePickerDialog(EditDetails.this, dat, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datepicker.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                datepicker.show();
            }
        });

        editbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit blood pressure");
                alert.setMessage("Select blood pressure");
                final Spinner ed = new Spinner(EditDetails.this);
                ed.setPadding(8,8,8,8);
                String[] div1 = {"Select BP Range","80-100","90-110","100-120"};
                ArrayAdapter a1 = new ArrayAdapter(EditDetails.this,android.R.layout.simple_spinner_item, div1);
                ed.setAdapter(a1);
                ed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){
                            case 0: bp="";
                            break;
                            default: bp = parent.getItemAtPosition(position).toString().trim();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tbp.setText(bp);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        editallergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit allergy");
                alert.setMessage("Select allergy");
                final Spinner ed = new Spinner(EditDetails.this);
                ed.setPadding(8,8,8,8);
                String[] div1 = {"Select Allergy","Skin","Medicine","Dust","None"};
                ArrayAdapter a1 = new ArrayAdapter(EditDetails.this,android.R.layout.simple_spinner_item, div1);
                ed.setAdapter(a1);
                ed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){
                            case 0: allergy="";
                                break;
                            default: allergy = parent.getItemAtPosition(position).toString().trim();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tallergy.setText(allergy);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        edithealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditDetails.this);
                alert.setTitle("Edit health issues");
                alert.setMessage("Type health issues");
                final EditText ed = new EditText(EditDetails.this);
                ed.setPadding(8,8,8,8);
                ed.setInputType(InputType.TYPE_CLASS_TEXT);
                ed.setMaxLines(1);
                ed.setMaxEms(30);
                ed.setGravity(View.TEXT_ALIGNMENT_CENTER);
                alert.setView(ed);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        healthissues = ed.getText().toString().trim();
                        thealthissues.setText(healthissues);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create();
                alert.show();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = tfname.getText().toString().trim();
                mname = tmname.getText().toString().trim();
                lname = tlname.getText().toString().trim();
                email = temail.getText().toString().trim();
                dob = tdob.getText().toString().trim();
                bp = tbp.getText().toString().trim();
                allergy = tallergy.getText().toString().trim();
                healthissues = thealthissues.getText().toString().trim();
                if(fname.isEmpty() || mname.isEmpty() || lname.isEmpty() || email.isEmpty() || dob.isEmpty() || bp.isEmpty() || allergy.isEmpty() || healthissues.isEmpty()){
                    Toast.makeText(EditDetails.this, "Enter all required information!", Toast.LENGTH_SHORT).show();
                }else{
                    if(Pattern.matches("^([A-za-z]){1,}$",fname)) {
                        if(Pattern.matches("^([A-za-z]){1,}$",mname)) {
                            if(Pattern.matches("^([A-za-z]){2,}$",lname)) {
                                if (Pattern.matches("^([A-Za-z0-9_\\.\\$])+\\@([A-Za-z0-9_])+\\.+com$", email)) {
                                        editInfo();
                                } else {
                                    temail.setError("Invalid email ID!");
                                    Toast.makeText(EditDetails.this, "Email ID is not valid!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                tlname.setError("Invalid name!");
                                Toast.makeText(EditDetails.this, "Invalid name!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            tmname.setError("Invalid name!");
                            Toast.makeText(EditDetails.this, "Invalid name!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        tfname.setError("Invalid name!");
                        Toast.makeText(EditDetails.this, "Invalid name!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void editInfo(){
        RequestParams params = new RequestParams();
        params.put("fname",fname);
        params.put("mname",mname);
        params.put("lname",lname);
        params.put("email",email);
        params.put("dob",dob);
        params.put("bp",bp);
        params.put("allergy",allergy);
        params.put("healthissue",healthissues);
        params.put("id",UserData.id);
        params.put("mobile",UserData.mobile);

        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(saveUserInfoURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                if(res.equals("200")){
                    Toast.makeText(EditDetails.this, "User profile edited successfully!", Toast.LENGTH_SHORT).show();
                    UserData.name = fname;
                    UserData.mname = mname;
                    UserData.lname = lname;
                    UserData.email = email;
                    UserData.dob = dob;
                    UserData.bp = bp;
                    UserData.allergy = allergy;
                    UserData.problems = healthissues;
                }else{
                    Toast.makeText(EditDetails.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(EditDetails.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tdob.setPaintFlags(tdob.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tdob.setText(sdf.format(myCalendar.getTime()));
        dob = sdf.format(myCalendar.getTime());
    }
}
