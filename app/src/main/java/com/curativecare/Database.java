package com.curativecare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    Context context;

    public Database(Context context) {
        super(context, "curative.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usertype(id integer primary key autoincrement,type integer)");
        db.execSQL("create table user(id integer primary key autoincrement,uid text,pass text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion<=oldVersion){
            return;
        }
        db.execSQL("drop table if exists usertype");
        db.execSQL("drop table if exists user");
    }

    public void saveUserType(int type){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from usertype where type=?",new String[]{""+type});
        if(res.moveToNext()){
            deleteUserType();
        }
        ContentValues values = new ContentValues();
        values.put("type", type);
        db.insert("usertype", null, values);

    }

    public void saveLastUserLogin(String uid, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from user where uid=? and pass=?",new String[]{uid,pass});
        if(res.moveToNext()){
            deleteLastUserLogin();
        }
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("pass", pass);
        db.insert("user", null, values);
    }

    public void deleteUserType(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("usertype","1",null);
        db.delete("user","1",null);
    }

    public void deleteLastUserLogin(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("user","1",null);
    }

    public int getUserType(){
        int type = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from usertype",null);
        if(res.moveToNext()){
            type = res.getInt(1);
        }
        return type;
    }

    public Cursor getLastUserLogin(){
        Cursor res = null;
        SQLiteDatabase db = this.getWritableDatabase();
        res = db.rawQuery("select * from user",null);
        return res;
    }
}
