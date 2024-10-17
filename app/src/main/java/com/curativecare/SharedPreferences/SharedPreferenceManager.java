package com.curativecare.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;



public class SharedPreferenceManager {
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String PREFS_NAME = "carservice";

    public SharedPreferenceManager()
    {
    }

    public SharedPreferenceManager(Context context)
    {
        this.context = context;
    }

    public void connectDB()
    {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void closeDB()
    {
        editor.commit();
    }

    public void setInt(String key, int value)
    {
        editor.putInt(key, value);
    }

    public int getInt(String key)
    {
        return prefs.getInt(key, 0);
    }
    public void setFloat(String key, float value)
    {
        editor.putFloat(key, value);
    }

    public float getFloat(String key)
    {
        return prefs.getFloat(key,0);
    }

    public void setBoolean(String key, boolean value)
    {
        editor.putBoolean(key, value);
    }

    public boolean getBoolean(String key)
    {
        return prefs.getBoolean(key, false);
    }

    public void setString(String key, String value)
    {
        editor.putString(key, value);
    }

    public String getString(String key)
    {
        return prefs.getString(key, "");
    }

    public void setName(String key, String value)
    {
        editor.putString(key, value);
    }

    public String getName(String key)
    {
        return prefs.getString(key, "");
    }
    public void setDepartment(String key, String value)
    {
        editor.putString(key, value);
    }

    public String getDepartment(String key)
    {
        return prefs.getString(key, "");
    }

}
