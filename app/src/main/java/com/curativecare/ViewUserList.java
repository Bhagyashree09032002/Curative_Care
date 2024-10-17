package com.curativecare;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
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

public class ViewUserList extends AppCompatActivity {

    RecyclerView recList;
    ProgressDialog progressDialog;

    ListView list;
    ArrayList arrayList;
    String requestURL = IPaddress.ip+"request_list1.php";
    String username="",mobile="",email="",blood="",area="",subarea="",cdate="",ctime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_list);

        recList = (RecyclerView) findViewById(R.id.requestListCard);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        UserObject userObject=new UserObject();
        //  UserData.subArea=getIntent().getExtras().getString("subarea");

        createList(userObject);

        /*list = (ListView) findViewById(R.id.listRequest);
        arrayList=new ArrayList();

        userRequest();*/
    }

    private List<UserObject> createList(UserObject userObject) {

        final List<UserObject> list = new ArrayList<UserObject>();

        RequestParams params=new RequestParams();
        params.put("mobile",UserData.mobile);

        progressDialog=new ProgressDialog(ViewUserList.this);
        progressDialog.setMessage("Fetching User Request's...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(requestURL, params, new AsyncHttpResponseHandler() {
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
                            ci.username = object1.getString("username");
                            ci.mail= object1.getString("email");
                            ci.mobile=object1.getString("mobile");
                            ci.blood=object1.getString("blood");
                            ci.cdate=object1.getString("cdate");
                            ci.ctime=object1.getString("ctime");

                            list.add(ci);
                        }
                        RequestAdapter ra = new RequestAdapter(list);
                        recList.setAdapter(ra);

                    }else{
                        Toast.makeText(ViewUserList.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ViewUserList.this,result, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(ViewUserList.this,"Connection ERROR", Toast.LENGTH_SHORT).show();
            }
        });
        return list;
    }

}