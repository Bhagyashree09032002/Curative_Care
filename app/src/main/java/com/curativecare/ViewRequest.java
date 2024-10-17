package com.curativecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.curativecare.SharedPreferences.SharedPreferenceManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewRequest extends AppCompatActivity {

    String name,id;
    ListView listView;
    //    private String loginStr = "http://192.168.0.109/accidental_system/compLatLonPolice.php";
    private String viewAmbulanceURL = IPaddress.ip+"viewAmbulanceRequest.php";
    //    private static final String REGISTER_URL = "http://192.168.0.109/accidental_system/police.php";
    //private static final String REGISTER_URL = IPaddress.ip+"police.php";
    private ProgressDialog pDialog;
    ArrayList arrayList;
    private SharedPreferenceManager prefManager;
    String request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        prefManager = new SharedPreferenceManager(ViewRequest.this);

        prefManager.connectDB();
        id= prefManager.getString("id");
        prefManager.closeDB();


        listView = (ListView) findViewById(R.id.ambulanceList);

        viewRequest();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                request = parent.getItemAtPosition(position).toString();

                //UserData.police = police;

                Toast.makeText(ViewRequest.this, "Showing Current Location Of : " + request, Toast.LENGTH_LONG).show();

                Intent i = new Intent(ViewRequest.this, ViewRequestMap.class);
                startActivity(i);
            }
        });
    }

    private void viewRequest() {

        RequestParams params = new RequestParams();

        //params.put("amb_status", UserData.amb_status);

        pDialog = new ProgressDialog(ViewRequest.this);
        pDialog.setMessage("Verifying Details..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(viewAmbulanceURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response = new String(responseBody);
                try {

                    JSONObject object = new JSONObject(response);
                    System.out.println(object);
                    JSONArray array = object.getJSONArray("result");
                    arrayList = new ArrayList();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);

                        String userid = object1.getString("userid");
                        String lat = object1.getString("lat");
                        String lon = object1.getString("lon");
                        String mobile = object1.getString("mobile");

                        String date = object1.getString("date");
                        String time = object1.getString("time");

                        String dlat = object1.getString("dlat");
                        String dlon = object1.getString("dlon");

                        UserData.reqlat = Double.valueOf(lat);
                        UserData.reqlon = Double.valueOf(lon);

                        UserData.dlat = Double.valueOf(dlat);
                        UserData.dlon = Double.valueOf(dlon);

                        arrayList.add("\nUser_ID : "+userid + "\nDate : " + date + " \nTime: " + time+ " \nMobile : " + mobile);
                        //arrayList.add("\nUser_ID : "+userid + "\nUser_Lat : " + lat + " \nUser_Lon : " + lon+ " \nMobile : " + mobile + "\nDest_Lat : " + dlat + " \nDest_Lon : " + dlon);
                        //arrayList.add(name);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ViewRequest.this, android.R.layout.simple_list_item_1, arrayList);
                    // listView.setChoiceMode(listView.CHOICE_MODE_SINGLE);

                    listView.setAdapter(arrayAdapter);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ViewRequest.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(ViewRequest.this, " Error Occured!" ,Toast.LENGTH_LONG).show();
            }

        });
    }


}