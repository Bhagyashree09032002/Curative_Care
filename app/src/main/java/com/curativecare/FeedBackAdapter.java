package com.curativecare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
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
import com.curativecare.SharedPreferences.SharedPreferenceManager;


public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ContactViewHolder> {

    String url=IPaddress.ip+"user_rating.php";
    private List<UserObject> contactList;
    Context context;
    public FeedBackAdapter(List<UserObject> contactList, Context context) {
        this.contactList = contactList;
        this.context=context;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(final FeedBackAdapter.ContactViewHolder contactViewHolder, int i) {
        final UserObject ci = contactList.get(i);
        contactViewHolder.hospitalName.setText(ci.hospitalName);
        contactViewHolder.doctorName.setText(ci.username);
        contactViewHolder.mobile.setText(ci.mobile);
        contactViewHolder.subarea.setText(ci.subArea);
        contactViewHolder.area.setText(ci.area);

        contactViewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Dialog dialog=new Dialog(view.getContext());
                dialog.setTitle("Rate");
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.rating);
                final RatingBar ratingBar = (RatingBar)dialog.findViewById(R.id.ratingBar1);
                Button submit= (Button) dialog.findViewById(R.id.submit);
                dialog.show();

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                                                boolean fromUser) {
                        if(ratingBar.getRating()==0.0){
                            Toast.makeText(view.getContext(), "Please select correct rating", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                ratingBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        float touchPositionX=motionEvent.getX();
                        float width=ratingBar.getWidth();
                        float starsf = (touchPositionX / width) * 5.0f;
                        int stars = (int)starsf + 1;
                        ratingBar.setRating(stars);
                        return true;
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        dialog.dismiss();
                        if(ratingBar.getRating()==0.0){
                            Toast.makeText(view.getContext(), "Please select correct rating", Toast.LENGTH_SHORT).show();
                        }else{
                            float val=(ratingBar.getRating()+ci.rating)/2;
                            Toast.makeText(view.getContext(), ""+val, Toast.LENGTH_SHORT).show();
                            SharedPreferenceManager sharedPreferenceManager=new SharedPreferenceManager(view.getContext());
                            sharedPreferenceManager.connectDB();
                            String userMob=sharedPreferenceManager.getString("mobile");
                            sharedPreferenceManager.closeDB();
                            //String url="http://192.168.0.130/disease_prediction/user_rating.php";
                            RequestParams params=new RequestParams();
                            params.put("rating",val);
                            params.put("admin_mobile",ci.mobile);
                            params.put("user_mobile",userMob);
                            final ProgressDialog progressDialog=new ProgressDialog(view.getContext());
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            AsyncHttpClient asyncHttpClient1=new AsyncHttpClient();
                            asyncHttpClient1.get(url, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    progressDialog.dismiss();
                                    String result=new String(responseBody);
                                    System.out.println(result);
                                    try {
                                        JSONObject object=new JSONObject(result);
                                        if(object.getString("success").equals("200")){
                                            Toast.makeText(view.getContext(), "Thank you", Toast.LENGTH_SHORT).show();
                                            ((Activity)context).finish();
                                        }else{
                                            Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            ((Activity)context).finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        System.out.println(e);
                                        Toast.makeText(view.getContext(), "JSON Error", Toast.LENGTH_SHORT).show();
                                        ((Activity)context).finish();
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(view.getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                    ((Activity)context).finish();
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    @Override
    public FeedBackAdapter.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.feedback_card_layout, viewGroup, false);

        return new FeedBackAdapter.ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView hospitalName;
        protected TextView doctorName;
        protected TextView mobile;
        protected TextView subarea;
        protected TextView area;
        protected Button accept;

        public ContactViewHolder(View v) {
            super(v);
            hospitalName =  (TextView) v.findViewById(R.id.hospitalName);
            doctorName = (TextView)  v.findViewById(R.id.doctorName);
            mobile = (TextView)  v.findViewById(R.id.txtMobile);
            subarea= (TextView) v.findViewById(R.id.txtSubArea);
            area= (TextView) v.findViewById(R.id.txtArea);
            accept= (Button) v.findViewById(R.id.buttonAccept);
        }
    }
}
