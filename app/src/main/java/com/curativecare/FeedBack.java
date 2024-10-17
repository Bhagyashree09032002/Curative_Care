package com.curativecare;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.curativecare.SharedPreferences.SharedPreferenceManager;

public class FeedBack extends AppCompatActivity {

    RecyclerView recList;
    ProgressDialog progressDialog;
    SharedPreferenceManager sharedPreferenceManager;
    Context context=FeedBack.this;
    String url=IPaddress.ip+"feedback.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        UserObject userObject=new UserObject();
        createList(userObject);
    }
    private List<UserObject> createList(UserObject userObject) {
        final List<UserObject> list = new ArrayList<UserObject>();
        RequestParams params=new RequestParams();
        params.put("mobile",UserData.mobile);
        progressDialog=new ProgressDialog(FeedBack.this);
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
                            ci.username = object1.getString("name");
                            ci.hospitalName=object1.getString("hospital_name");
                            ci.mobile=object1.getString("mobile");
                            ci.area=object1.getString("area");
                            ci.subArea=object1.getString("subarea");
                            ci.rating= Float.parseFloat(object1.getString("rating"));
                            list.add(ci);
                        }
                        FeedBackAdapter ca = new FeedBackAdapter(list,context);
                        recList.setAdapter(ca);


                    }else{
                        Toast.makeText(FeedBack.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FeedBack.this,"JSON ERROR", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(FeedBack.this,"Connection ERROR", Toast.LENGTH_SHORT).show();
            }
        });
        return list;
    }

}
