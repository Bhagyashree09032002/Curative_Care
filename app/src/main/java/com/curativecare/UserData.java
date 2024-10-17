package com.curativecare;

import com.curativecare.UserObject.UserObject;

import java.util.ArrayList;
import java.util.List;


public class UserData {
    public static String id,name,mname,lname,dob,mobile,email="",blood,bp,allergy,problems,choice;
    public static int flag=0;
    public static int amb_status=0;
    public static String relative;
    public static int count=0;
    public static int lt;
    public static int ln;
    public static String area;
    public static String slat="",slon="",dist="";
    public static int checkservice = 0;
    public static String subArea;
    public float rating;
    public static String appointmentDate,appointmentTime;
    public static String doctorMobile;
    public static String request,key;
    public static UserObject uo= new UserObject();
    public static String reportid="";
    public static String status="",speciality="";
    public static Double reqlat=0.0, reqlon=0.0,hlat=0.0, hlon=0.0;
    public static Double dlat=0.0,dlon=0.0;


    public static List<PlaceObject> route = new ArrayList<>();
}