package com.curativecare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import com.curativecare.SharedPreferences.SharedPreferenceManager;

public class UserHistory extends AppCompatActivity {

    ListView listView;
    ProgressDialog progressDialog;
    TextView textView;
    SharedPreferenceManager sharedPreferenceManager;
    String url=IPaddress.ip+"userhistory.php";
    String addRatingURL = IPaddress.ip+"addrating.php";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        listView= (ListView) findViewById(R.id.listView);
        textView= (TextView) findViewById(R.id.request);

        final ArrayList arrayList=new ArrayList();
        RequestParams params=new RequestParams();
        params.put("mobile",UserData.mobile);
        progressDialog=new ProgressDialog(UserHistory.this);
        progressDialog.setMessage("Fetching User Request...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result=new String(responseBody);
                System.out.println(result);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("success").equals("200")){
                        JSONArray jsonArray=object.getJSONArray("result");
                        System.out.println(jsonArray);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object1=jsonArray.getJSONObject(i);
                            UserObject ci = new UserObject();
                            ci.hospitalName=object1.getString("hospital_name");
                            ci.username = object1.getString("name");
                            ci.mobile = object1.getString("mobile");
                            ci.appointmentDate=object1.getString("date");
                            ci.appointmentTime= object1.getString("time");
                            ci.choice=object1.getString("symptoms");
                            ci.id=object1.getString("id");
                            arrayList.add(ci.hospitalName+"\n"+ci.username+"\n"+"Mobile Number: "+ci.mobile+"\n"+"Symptoms: "+ci.choice+"\n"+"Date: "
                                    +ci.appointmentDate+"\n"+"Time: "+ci.appointmentTime+"\n"+"Request status: "+object1.getString("request_status"));
                        }
                        ArrayAdapter arrayAdapter=new ArrayAdapter(UserHistory.this,android.R.layout.simple_expandable_list_item_1,arrayList){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                // Get the current item from ListView
                                View view = super.getView(position,convertView,parent);

                                // Get the Layout Parameters for ListView Current Item View
                                ViewGroup.LayoutParams params = view.getLayoutParams();

                                // Set the height of the Item View
                                params.height = view.getHeight();
                                view.setLayoutParams(params);

                                return view;
                            }
                        };
                        listView.setAdapter(arrayAdapter);
                    }else{
                        Toast.makeText(UserHistory.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserHistory.this,"JSON ERROR", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(UserHistory.this,"Connection ERROR", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = parent.getItemAtPosition(position).toString().trim();
                String[] temp = string.split("\n");
                String[] t = temp[2].split(" ");
                final String mob = t[2];
                if((temp[6].split(" "))[2].equals("Scanned")){
                    final String date[] = temp[4].split(" ");
                    AlertDialog.Builder alert = new AlertDialog.Builder(UserHistory.this);
                    alert.setTitle("Give rating");
                    LinearLayout ll = new LinearLayout(UserHistory.this);
                    final RatingBar rb = new RatingBar(UserHistory.this);
                    rb.setNumStars(5);
                    rb.setStepSize(0.01f);
                    ll.addView(rb);
                    alert.setView(ll);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            float rating = rb.getRating();
                            saveRating(mob,rating,date[1]);
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
            }
        });
    }

    public void saveRating(String mob, float rating, String date){
        RequestParams params = new RequestParams();
        params.put("rating",rating);
        params.put("mob",mob);
        params.put("umob",UserData.mobile);
        params.put("date",date);

        final ProgressDialog pDialog = ProgressDialog.show(UserHistory.this,"Processing","adding feedback",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(addRatingURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                if(res.equals("200")){
                    Toast.makeText(UserHistory.this, "Rating feedback added!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UserHistory.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(UserHistory.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}