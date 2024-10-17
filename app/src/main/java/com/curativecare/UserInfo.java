package com.curativecare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.curativecare.UserObject.UserObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UserInfo extends AppCompatActivity
{

    TextView txinfo;
    Button btnok,viewreports,addreport;
    String data,etpres,SecretKey="",Encrpted="",qrimage="";
    ImageView addpic;
    EditText etspres;
    String[] files;
    ListView listView;
    OutputStream out=null;
    File path,dir,fl;
    String message="",cdate="";
    String url=IPaddress.ip+"updatepres.php";
    String geturl=IPaddress.ip+"getQRdata.php";
    ProgressDialog progressDialog;
    String[] listContent = {"Brufen 3","Citizin 6","Crosin 3","Aceclofenac 6","Acetaminophen 3", "Tramadol Hydrochloride 6","Asprin 3","Chlorzoxazone 6","Diclofenac 3","Paracetamol 6"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        txinfo=(TextView) findViewById(R.id.txtuserdata);
        btnok=(Button) findViewById(R.id.btnok);
        addpic = (ImageView) findViewById(R.id.imgphoto);
        etspres=(EditText) findViewById(R.id.edtpres);
        listView= (ListView) findViewById(R.id.listView);
        viewreports = (Button) findViewById(R.id.btnViewAllReports);
        addreport = (Button) findViewById(R.id.docAddReport);
        setdetails();
        getdetails();

        etspres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(UserInfo.this);
                dialog.setContentView(R.layout.listview1);
                dialog.setTitle("Select");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                listView= (ListView) dialog.findViewById(R.id.listView1);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setItemChecked(2, true);
                Button button= (Button) dialog.findViewById(R.id.listViewSubmit1);
                ArrayAdapter<String> adapter= new ArrayAdapter<String>(UserInfo.this,android.R.layout.simple_list_item_multiple_choice, listContent);
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
                        Toast.makeText(UserInfo.this, choicesString, Toast.LENGTH_SHORT).show();
                        etspres.setText(choicesString);
                    }
                });
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etpres=etspres.getText().toString().trim();
                AESEncrypt a = new AESEncrypt();
                PasswordAthenticationProtocol RN = new PasswordAthenticationProtocol();
                SecretKey = RN.generateRandomString();
                Encrpted= null;
                try {
                    Encrpted = a.encrypt(IPaddress.uemail, SecretKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("---Encrpted---"+Encrpted);

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(Encrpted, BarcodeFormat.QR_CODE,140,140);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    //imageView.setImageBitmap(bitmap);
                    qrimage = getStringImage(bitmap);
                    path = Environment.getExternalStorageDirectory();
                    dir = new File(path + "/healthcare_qr_code/");
                    dir.mkdirs();
                    fl = new File(dir, DoctorData.mobile+cdate+ ".JPEG");

                    out = new FileOutputStream(fl);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    message = "Your qr code for prescription of Dr."+DoctorData.name+" for date "+cdate;
                    //sendMail(message,IPaddress.uemail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updatepres();
                /*Intent intent=new Intent(UserInfo.this,DoctorQR.class);
                startActivity(intent);*/
            }
        });

        viewreports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserInfo.this,ViewReport.class);
                startActivity(i);
            }
        });

        addreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserInfo.this,AddReport.class);
                startActivity(i);
            }
        });
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void getdetails() {
        RequestParams params=new RequestParams();
        params.put("email",IPaddress.uemail);
        progressDialog=new ProgressDialog(UserInfo.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Get Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final ArrayList arrayList=new ArrayList();

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post(geturl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result=new String(responseBody);
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("success").equals("200")){
                        JSONArray jsonArray=object.getJSONArray("result");
                        System.out.println(jsonArray);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object1=jsonArray.getJSONObject(i);
                            UserObject ci = new UserObject();
                            ci.symptoms = object1.getString("symptoms");
                            ci.prescription = object1.getString("prescription");
                            arrayList.add("Symptoms: "+ci.symptoms+"\n"+"Prescription: "+ci.prescription+"\n");
                        }
                        ArrayAdapter arrayAdapter=new ArrayAdapter(UserInfo.this,android.R.layout.simple_expandable_list_item_1,arrayList){
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
                        Toast.makeText(UserInfo.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserInfo.this,"JSON Error", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    private void updatepres() {
        RequestParams params=new RequestParams();
        params.put("pres",etpres);
        params.put("email",IPaddress.uemail);
        params.put("seckey",SecretKey);
        params.put("encrypted",Encrpted);
        params.put("qrimage",qrimage);
        progressDialog=new ProgressDialog(UserInfo.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Update QR Code...");
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
                        sendMail("You have successfully prescribe.<br> Your Details.<br>"+

                                "Full Name : "+IPaddress.ufname+ " "+IPaddress.ulname +"<br>"+
                                "Email : "+ IPaddress.uemail + " "+"<br>"+
                                "QR Code : "+ IPaddress.code + " "+"<br>"+
                                "SecretKey :"+ SecretKey,IPaddress.uemail);
                        /*sendEmail("You have successfully Prescribe.<br> Your Details.<br>"+

                                "Full Name : "+IPaddress.ufname+ " "+IPaddress.ulname +"<br>"+
                                "Email : "+ IPaddress.uemail + " "+"<br>"+
                                "QR Code : "+ IPaddress.code + " "+"<br>"+
                                "SecretKey :"+ IPaddress.skey,IPaddress.uemail);*/

                        Toast.makeText(UserInfo.this,"Update Successfully", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(UserInfo.this,object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserInfo.this,"JSON Error", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void sendEmail(final String key, final String mailId) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            SendMailSSL sendMailSSL = new SendMailSSL();
                            //sendMailSSL.EmailSending(mailId, "Prescription mail", key);
                            files = null;
                            sendMailSSL.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mailId, "Prescription mail","Prescription mil "+key,files);
                            //Your code goes here
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    public void sendMail(final String message, final String mailId){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    SendMailSSL sendMailSSL = new SendMailSSL();
                    //sendMailSSL.EmailSending(mailId, "Reporting mail", message, fl);
                    path = Environment.getExternalStorageDirectory();
                    dir = new File(path + "/healthcare_qr_code/");
                    files = new String[]{path+"/healthcare_qr_code/"+DoctorData.mobile+cdate+ ".JPEG"};
                    sendMailSSL.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mailId, "Doctor prescription of Dr."+DoctorData.name,message,files);
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        finish();
    }
    public void setdetails(){
        data="User Id: "+IPaddress.uid+"\nFirst Name: "+IPaddress.ufname+"\nLast Name: "+IPaddress.ulname+"\nContact Number: "+IPaddress.ucontactno+"\nBlood Group: "+IPaddress.ubloodgrp+"\nBlood Pressure: "+IPaddress.ubp+"\nAllergy: "+IPaddress.uallergy+"\n Any Problems: "+IPaddress.uproblems;
        txinfo.setText(data);
        String path = IPaddress.ip+"/images/"+IPaddress.uimagename;
        new GetImage().execute(path);
    }
    class GetImage extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(UserInfo.this, "processing...", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(b);
            loading.dismiss();
            addpic.setImageBitmap(b);
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
