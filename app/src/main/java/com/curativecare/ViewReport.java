package com.curativecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ViewReport extends AppCompatActivity {

    ListView list;
    String rlist[];
    String viewReportsURL = IPaddress.ip+"viewallreports.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        list = (ListView) findViewById(R.id.listAllReports);

        getAllReports();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select = parent.getItemAtPosition(position).toString();
                String[] temp = select.split(" ");
                UserData.reportid = temp[0];
                Intent i = new Intent(ViewReport.this,ReportDetails.class);
                startActivity(i);
            }
        });
    }

    public void getAllReports(){
        RequestParams params = new RequestParams();
        params.put("uid",IPaddress.uid);

        final ProgressDialog pDialog = ProgressDialog.show(ViewReport.this,"Processing","fetching details...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(viewReportsURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                try{
                    JSONObject o = new JSONObject(res);
                    if(o.getString("success").equals("200")){
                        JSONArray a = o.getJSONArray("result");
                        rlist = new String[a.length()];
                        for(int i=0;i<a.length();i++){
                            rlist[i] = a.getString(i);
                        }
                        ArrayAdapter a1 = new ArrayAdapter(ViewReport.this,R.layout.custom_spinner,rlist);
                        list.setAdapter(a1);
                    }else{
                        Toast.makeText(ViewReport.this, o.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(ViewReport.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(ViewReport.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
