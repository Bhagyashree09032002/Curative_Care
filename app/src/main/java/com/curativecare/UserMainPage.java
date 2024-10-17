package com.curativecare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.curativecare.SharedPreferences.SharedPreferenceManager;

public class UserMainPage extends AppCompatActivity {

    String subAreaName;
    Spinner subArea;
    ListView listView;
    Button submit,history,feedback;
    String dist="";
    ProgressDialog progressDialog;
    SharedPreferenceManager sharedPreferenceManager;
    String[] listContent = {"Flu","Cold","Cough","Stomach Pain","Headache", "Back Pain","Knee Pain","Anxiety","Sleeping Sickness","Joint Pain"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);
        subArea= (Spinner) findViewById(R.id.spinnerArea);
        listView= (ListView) findViewById(R.id.listView);
        submit= (Button) findViewById(R.id.submit);
        history= (Button) findViewById(R.id.history);
        feedback= (Button) findViewById(R.id.feedback);

        if(UserData.name==null){
            Intent i = new Intent(UserMainPage.this,Welcome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }

        String[] subareaname={"-- Select Place --","LONAVALA","TALEGON","DEHUROAD","AKURDI","CHINCHWAD","PIMPRI","DAPODI","SHIVAJINAGAR","NIGDI","BHOSARI","CHAKAN","BANER","HINJEWADI"
                , "HADAPSAR","AAREY MILK COLONY","ANDHERI","ANUSHAKTI NAGAR","BANDRA","BANDRA KURLA COMPLEX","BORIVALI"};
        ArrayAdapter adapter1=new ArrayAdapter(UserMainPage.this,android.R.layout.simple_spinner_item,subareaname);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        subArea.setAdapter(adapter1);

        subArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subAreaName=adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter arrayAdapter=new ArrayAdapter(UserMainPage.this,android.R.layout.simple_list_item_multiple_choice,listContent);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(2, true);
        listView.setAdapter(arrayAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserData.slat.isEmpty() && !UserData.slon.isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UserMainPage.this);
                    alert.setTitle("Enter maximum distance to search doctor");
                    alert.setMessage("Distance in Km");
                    final EditText distu = new EditText(UserMainPage.this);
                    distu.setMaxLines(1);
                    distu.setInputType(InputType.TYPE_CLASS_NUMBER);
                    distu.setGravity(Gravity.CENTER_HORIZONTAL);
                    distu.setWidth(250);
                    LinearLayout ll = new LinearLayout(UserMainPage.this);
                    ll.setGravity(Gravity.CENTER_HORIZONTAL);
                    ll.addView(distu);
                    alert.setView(ll);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final String[] items = {"General", "Orthopedic Surgeon", "Homeopathic", "Dentist", "Cardiologist", "Psychiatrist", "Dermatologist", "Neurologist", "Nephrologist", "Neurologist"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserMainPage.this);
                            builder.setTitle("Select Doctors Speciality")

                                    .setItems(items, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), ""+items[which], Toast.LENGTH_SHORT).show();
                                            UserData.speciality = items[which];
                                            dist = distu.getText().toString().trim();
                                            UserData.dist = dist;

                                            SparseBooleanArray choices = listView.getCheckedItemPositions();
                                            StringBuilder choicesString = new StringBuilder();
                                            for (int i = 0; i < choices.size(); i++) {
                                                if (choices.valueAt(i) == true)
                                                    choicesString.append(listContent[choices.keyAt(i)]).append(",");
                                            }
                                            if (choicesString.toString().isEmpty() || subAreaName.equals("Select Place")) {
                                                Toast.makeText(UserMainPage.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                                            } else {
                                                UserData.subArea = subAreaName;
                                                StringBuilder stringBuilder = new StringBuilder();
                                                sharedPreferenceManager = new SharedPreferenceManager(UserMainPage.this);
                                                sharedPreferenceManager.connectDB();
                                                sharedPreferenceManager.setString("symptoms", choicesString.toString());
                                                sharedPreferenceManager.closeDB();

                                                UserData.choice = stringBuilder.append(",").append(choicesString).toString();
                                                UserData.choice = UserData.choice.replace(",", "%");

                                                Intent intent = new Intent(UserMainPage.this, DoctorList.class);
                                                intent.putExtra("subarea", UserData.subArea);
                                                intent.putExtra("others", UserData.choice);
                                                intent.putExtra("speciality", UserData.speciality);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                            /*builder.setPositiveButton("OK", null);
                            builder.setNegativeButton("CANCEL", null);
                            builder.setNeutralButton("NEUTRAL", null);*/
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.create();
                    alert.show();

                }else{
                    Toast.makeText(UserMainPage.this, "Fetching current location!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserMainPage.this,UserHistory.class);
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IPaddress.uid = UserData.id;
                Intent intent=new Intent(UserMainPage.this,ViewReport.class);
                startActivity(intent);
            }
        });
    }
}
