package com.curativecare;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ViewDonorList extends AppCompatActivity {
    RecyclerView recList;
    ProgressDialog progressDialog;

    String url=IPaddress.ip+"donorsearch.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donor_list);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        UserObject userObject=new UserObject();
      //  UserData.subArea=getIntent().getExtras().getString("subarea");

        createList(userObject);
    }

    private List<UserObject> createList(UserObject userObject) {
        final List<UserObject> list = new ArrayList<UserObject>();
        RequestParams params=new RequestParams();
        //params.put("lat",UserData.lt);
        //params.put("lon",UserData.ln);
        params.put("blood",UserData.blood);
        //params.put("subarea",UserData.subArea);

        progressDialog=new ProgressDialog(ViewDonorList.this);
        progressDialog.setMessage("Fetching Blood Donor's...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
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
                            ci.username = object1.getString("name");
                            ci.address= object1.getString("address");
                            ci.mobile=object1.getString("mobileno");
                            ci.area=object1.getString("area");
                            ci.subArea=object1.getString("subarea");
                            ci.blood=object1.getString("blood");
                            ci.mail = object1.getString("mail");
                            list.add(ci);
                        }
                        ContactAdapter ca = new ContactAdapter(list);
                        recList.setAdapter(ca);

                    }else{
                        Toast.makeText(ViewDonorList.this,object.getString("message"), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ViewDonorList.this,result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(ViewDonorList.this,"Connection ERROR", Toast.LENGTH_LONG).show();
            }
        });
        return list;
    }

}
