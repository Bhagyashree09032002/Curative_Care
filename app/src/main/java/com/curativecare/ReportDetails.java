package com.curativecare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class ReportDetails extends AppCompatActivity {

    ImageView reportimg;
    TextView name,daten,comment;

    String getReportDetails = IPaddress.ip+"getreportdetails.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        name = (TextView) findViewById(R.id.textReportDetailsHeading);
        daten = (TextView) findViewById(R.id.textReportDetailsDate);
        comment = (TextView) findViewById(R.id.textReportDetailsComment);

        reportimg = (ImageView) findViewById(R.id.imgReportImage);

        getDetails();
    }

    public void getDetails(){
        RequestParams params = new RequestParams();
        params.put("rid",UserData.reportid);

        final ProgressDialog pDialog = ProgressDialog.show(ReportDetails.this,"processing","connecting server...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getReportDetails, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                try{
                    JSONObject o = new JSONObject(res);
                    if(o.getString("success").equals("200")){
                        name.setText(o.getString("reportname"));
                        daten.setText(o.getString("reportdate"));
                        comment.setText(o.getString("comment"));

                        String imagename = o.getString("imagename");
                        String url = IPaddress.ip+"/reports/"+imagename;
                        new GetImage().execute(url);
                    }
                }catch(Exception e){
                    Toast.makeText(ReportDetails.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(ReportDetails.this, "Connectivity failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class GetImage extends AsyncTask<String, Void, Bitmap> {
        //ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading = ProgressDialog.show(CustomAdapter.this, "processing...", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(b);
            //loading.dismiss();
            reportimg.setImageBitmap(b);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String id = params[0];
//                String add = "http://10.0.3.2/Smart_Institute/images/image1.jpg";
            URL url = null;
            Bitmap image1 = null;
            try {
                url = new URL(id);
                image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Log.d("Result", String.valueOf(image1));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image1;
        }

    }
}
