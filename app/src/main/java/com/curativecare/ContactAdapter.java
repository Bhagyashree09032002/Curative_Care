package com.curativecare;

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

import com.curativecare.UserObject.UserObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<UserObject> contactList;
    public String msg;
    Context context;

    String requestURL = IPaddress.ip + "blood_request.php";

    public ContactAdapter(List<UserObject> contactList) {

        this.contactList = contactList;
    }

    public ContactAdapter(List<UserObject> contactList, Context context) {
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

            contactViewHolder.Name.setText(ci.username);
            contactViewHolder.mobile.setText(ci.mobile);
            contactViewHolder.address.setText(ci.address);
            contactViewHolder.subarea.setText(ci.subArea);
            contactViewHolder.area.setText(ci.area);
            contactViewHolder.blood.setText(ci.blood);


            contactViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //UserData.dmobile=ci.mobile;
                    /*SmsManager smsManager=SmsManager.getDefault();
                    msg=""+UserData.name+" Needs Blood Immedietly, Please Call Him and Save The Life in Danger.";
                    smsManager.sendTextMessage(UserData.mobile, null,msg, null, null);*/

                    UserObject userObject=new UserObject();
                    userObject.mobile=ci.mobile;
                    userObject.mail=UserData.email;
                    userObject.name = UserData.name + " " + UserData.mname + " " + UserData.lname;
                    userObject.blood = UserData.blood;

                    sendRequest(view,userObject);

                }
            });
        }

    private void sendRequest(final View view, final UserObject userObject) {
        RequestParams params = new RequestParams();
        params.put("name", userObject.name);
        params.put("mail", userObject.mail);
        params.put("mobile", userObject.mobile);
        params.put("blood", userObject.blood);

        final ProgressDialog progressDialog=new ProgressDialog(view.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(requestURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("success").equals("200")) {

                        Toast.makeText(view.getContext(), object.getString("message"), Toast.LENGTH_LONG).show();

                        Intent intent=new Intent(view.getContext(), UserHomepage.class);
                        view.getContext().startActivity(intent);

                    }else{
                        Toast.makeText(view.getContext(), object.getString("message"), Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "JSON Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(view.getContext(), "Connectivity Failed With Server", Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
        from(viewGroup.getContext()).
        inflate(R.layout.card_layout1, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView Name;
        protected TextView mobile;
        protected TextView address;
        protected TextView subarea;
        protected TextView area,blood;
        protected Button accept;

        public ContactViewHolder(View v) {
            super(v);
            Name=(TextView)  v.findViewById(R.id.uName);
            mobile = (TextView)  v.findViewById(R.id.uno);
            address= (TextView) v.findViewById(R.id.txtaddress1);
            subarea= (TextView) v.findViewById(R.id.txtsubarea1);
            area= (TextView) v.findViewById(R.id.txtarea1);
            blood= (TextView) v.findViewById(R.id.txtblood1);
            accept= (Button) v.findViewById(R.id.buttonAccept);
        }
    }
}