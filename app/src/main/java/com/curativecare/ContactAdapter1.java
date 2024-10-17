package com.curativecare;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.curativecare.UserObject.UserObject;

import java.util.List;


public class ContactAdapter1 extends RecyclerView.Adapter<ContactAdapter1.ContactViewHolder> {

private List<UserObject> contactList;

public ContactAdapter1(List<UserObject> contactList) {
        this.contactList = contactList;
        }

@Override
public int getItemCount() {
        return contactList.size();
        }

@Override
public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {
        final UserObject ci = contactList.get(i);
        contactViewHolder.hospitalName.setText(ci.hospitalName);
        contactViewHolder.doctorName.setText(ci.username);
        contactViewHolder.speciality.setText(ci.specialist);
        contactViewHolder.others.setText(ci.choice);
        contactViewHolder.mobile.setText(ci.mobile);
        contactViewHolder.address.setText(ci.address);
        contactViewHolder.subarea.setText(ci.subArea);
        contactViewHolder.area.setText(ci.area);
        contactViewHolder.experience.setText(ci.experience);
        contactViewHolder.rating.setRating(ci.rating);

    contactViewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                UserData.doctorMobile=ci.mobile;
                UserData.uo = ci;
                Intent intent=new Intent(view.getContext(), SetTimeDate.class);
                intent.putExtra("doctorMobileNumber",ci.mobile);
                view.getContext().startActivity(intent);
            }
        });

        }

@Override
public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
        from(viewGroup.getContext()).
        inflate(R.layout.card_layout, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

public static class ContactViewHolder extends RecyclerView.ViewHolder {

    protected TextView hospitalName;
    protected TextView doctorName;
    protected TextView speciality;
    protected TextView others;
    protected TextView mobile;
    protected TextView address;
    protected TextView subarea;
    protected TextView area;
    protected TextView experience;
    protected RatingBar rating;
    protected Button accept;

    public ContactViewHolder(View v) {
        super(v);
        hospitalName =  (TextView) v.findViewById(R.id.hospitalName);
        doctorName = (TextView)  v.findViewById(R.id.doctorName);
        speciality = (TextView)  v.findViewById(R.id.txtSpeciality);
        others = (TextView)  v.findViewById(R.id.txtOthers);
        mobile = (TextView)  v.findViewById(R.id.txtMobile);
        address= (TextView) v.findViewById(R.id.txtAddress);
        subarea= (TextView) v.findViewById(R.id.txtSubArea);
        area= (TextView) v.findViewById(R.id.txtArea);
        experience = (TextView) v.findViewById(R.id.txtExperience);
        rating= (RatingBar) v.findViewById(R.id.ratingBar1);
        accept= (Button) v.findViewById(R.id.buttonAccept);
    }
}
}
