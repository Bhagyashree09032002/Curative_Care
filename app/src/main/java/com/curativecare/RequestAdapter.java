package com.curativecare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ContactViewHolder> {


    private List<UserObject> requestList;
    public String msg;
    ProgressDialog progressDialog;
    Context context;

    String requestURL = IPaddress.ip + "request_list1.php";

    public RequestAdapter(List<UserObject> contactList) {

        this.requestList = contactList;

    }

    public RequestAdapter(List<UserObject> requestList, Context context) {
        this.requestList = requestList;
        this.context=context;
    }

    @Override
    public int getItemCount() {

        return requestList.size();
    }

        @Override
        public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {
            final UserObject ci = requestList.get(i);

            contactViewHolder.name.setText(ci.username);
            contactViewHolder.mobile.setText(ci.mobile);
            contactViewHolder.mail.setText(ci.mail);
            contactViewHolder.blood.setText(ci.blood);
            /*contactViewHolder.subarea.setText(ci.subArea);
            contactViewHolder.area.setText(ci.area);*/
            contactViewHolder.cdate.setText(ci.cdate);
            contactViewHolder.ctime.setText(ci.ctime);


            contactViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    UserObject userObject=new UserObject();

                    userObject.mail=ci.mail;
                    userObject.status=1;

                    acceptReject(view,userObject);
                    Toast.makeText(view.getContext(), "Request Accepted Successfully...", Toast.LENGTH_SHORT).show();

                    SmsManager smsManager= SmsManager.getDefault();
                    msg = "Hello, I am "+ ci.username + " available to give you blood, I am helping you in short time.  ";
                    smsManager.sendTextMessage(ci.mobile, null,msg, null, null);
                }
            });

            contactViewHolder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    UserObject userObject=new UserObject();

                    userObject.mail=ci.mail;
                    userObject.status=2;

                    acceptReject(view,userObject);
                    Toast.makeText(view.getContext(), "Request Rejected!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void acceptReject(final View view, final UserObject userObject) {

        String url=IPaddress.ip+"accept_reject1.php";

        RequestParams params=new RequestParams();
        params.put("email",userObject.mail);
        params.put("status",userObject.status);

        final ProgressDialog progressDialog=new ProgressDialog(view.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
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

                        Intent intent=new Intent(view.getContext(),DonorHomepage.class);
                        view.getContext().startActivity(intent);
                        ((Activity)context).finish();

                    }else{
                        Toast.makeText(view.getContext(), "Error occured", Toast.LENGTH_LONG).show();
                        ((Activity)context).finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "JSON Error", Toast.LENGTH_LONG).show();
                    ((Activity)context).finish();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
        from(viewGroup.getContext()).
        inflate(R.layout.request_layout, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView mobile;
        protected TextView address;
        protected TextView subarea;
        protected TextView area,blood;
        protected TextView mail;
        protected TextView cdate,ctime;
        protected Button accept,reject;

        public ContactViewHolder(View v) {
            super(v);
            name=(TextView)  v.findViewById(R.id.username);
            mobile = (TextView)  v.findViewById(R.id.mobile);
            mail= (TextView) v.findViewById(R.id.mail);
            /*subarea= (TextView) v.findViewById(R.id.subarea);
            area= (TextView) v.findViewById(R.id.area);*/
            blood= (TextView) v.findViewById(R.id.blood);
            cdate=(TextView)  v.findViewById(R.id.cdate);
            ctime = (TextView)  v.findViewById(R.id.ctime);
            accept= (Button) v.findViewById(R.id.buttonAccept);
            reject= (Button) v.findViewById(R.id.buttonReject);
        }
    }
}