package com.curativecare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import com.curativecare.SharedPreferences.SharedPreferenceManager;
import com.curativecare.UserObject.UserObject;


public class RequestAdapter1 extends RecyclerView.Adapter<RequestAdapter1.ContactViewHolder> {

    private List<UserObject> contactList;
    Context context;
    String[] files;

    public RequestAdapter1(List<UserObject> contactList, Context context) {
        this.contactList = contactList;
        this.context=context;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {
        final UserObject ci = contactList.get(i);
        contactViewHolder.hospitalName.setText(ci.username);
        contactViewHolder.mobile.setText(ci.mobile);
        contactViewHolder.date.setText(ci.appointmentDate);
        contactViewHolder.time.setText(ci.appointmentTime);
        contactViewHolder.symptoms.setText(ci.choice);
        System.out.println(ci.mail);

        contactViewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                UserObject userObject=new UserObject();
                userObject.id=ci.id;
                userObject.request="Accepted";
                userObject.mail=ci.mail;
                userObject.appointmentTime=ci.appointmentTime;
                userObject.appointmentDate=ci.appointmentDate;
                acceptReject(view,userObject);
            }
        });

        contactViewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserObject userObject=new UserObject();
                userObject.id=ci.id;
                userObject.mail=ci.mail;
                userObject.appointmentTime=ci.appointmentTime;
                userObject.appointmentDate=ci.appointmentDate;
                userObject.request="Rejected";
                acceptReject(view,userObject);
            }
        });

    }

    private void acceptReject(final View view, final UserObject userObject) {

        String url=IPaddress.ip+"accept_reject.php";
        RequestParams params=new RequestParams();
        params.put("id",userObject.id);
        params.put("request",userObject.request);
        final ProgressDialog progressDialog=new ProgressDialog(view.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending notification");
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
                        Toast.makeText(view.getContext(), "Successfully sent notification", Toast.LENGTH_SHORT).show();

                        //for sending mail to user
                        SharedPreferenceManager sharedPreferenceManager=new SharedPreferenceManager(view.getContext());
                        sharedPreferenceManager.connectDB();
                        final String hospitalname=sharedPreferenceManager.getString("hospitalname");
                        sharedPreferenceManager.closeDB();
                        sendEmail("The Appointment request you made to "+ hospitalname+" on "+userObject.appointmentDate+" at "+userObject.appointmentTime
                                        +" has been "+userObject.request,"Your request has been "+userObject.request,userObject.mail);

                        Intent intent=new Intent(view.getContext(),PendingRequest.class);
                        view.getContext().startActivity(intent);
                        ((Activity)context).finish();

                    }else{
                        Toast.makeText(view.getContext(), "Error occured", Toast.LENGTH_SHORT).show();
                        ((Activity)context).finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "JSON Error", Toast.LENGTH_SHORT).show();
                    ((Activity)context).finish();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
            }
        });
    }
    public void sendEmail(final String key, final String sub, final String mailId) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    SendMailSSL sendMailSSL = new SendMailSSL();
                    //sendMailSSL.EmailSending(mailId, sub, key);
                    files = null;
                    sendMailSSL.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mailId, sub,key,files);
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_request_layout, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView hospitalName;
        protected TextView date;
        protected TextView time;
        protected TextView mobile;
        protected TextView symptoms;
        protected Button accept,reject;

        public ContactViewHolder(View v) {
            super(v);
            hospitalName =  (TextView) v.findViewById(R.id.hospitalName);
            mobile = (TextView)  v.findViewById(R.id.txtMobile);
            date= (TextView) v.findViewById(R.id.txtDate);
            time= (TextView) v.findViewById(R.id.txtTime);
            symptoms= (TextView) v.findViewById(R.id.txtSymptoms);
            accept= (Button) v.findViewById(R.id.buttonAccept);
            reject= (Button) v.findViewById(R.id.buttonReject);
        }
    }


}
