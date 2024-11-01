package com.example.fittarget;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FitTargetDatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME =  "fit_target_database";
    private static int DB_VERSION = 1;
    FitTargetDatabaseHelper(Context context ){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public  void onCreate(SQLiteDatabase db){
    updateMyDatabase(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    updateMyDatabase(db,oldVersion,newVersion);
    }

    public  void  insertUser(SQLiteDatabase db,String name, String email, String password, int age, int weight,
                             int height, String gender, String weightMeasurementPreference,
                             String weightControl,
                             int weightTarget,
                             int periodTarget
                             )
    {
        ContentValues userValues = new ContentValues();
        userValues.put("NAME",name);
        userValues.put("GENDER",gender);
        userValues.put("EMAIL",email);
        userValues.put("PASSWORD",password);
        userValues.put("AGE",age);
        userValues.put("WEIGHT",weight);
        userValues.put("HEIGHT",height);
        userValues.put("WEIGHT_MEASUREMENT_PREFERENCE",weightMeasurementPreference);
        userValues.put("WEIGHT_CONTROL",weightControl);
        userValues.put("WEIGHT_TARGET",weightTarget);
        userValues.put("PERIOD_TARGET",periodTarget);
        db.insert("USER_INFO",null,userValues);

    }

    public void  updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion < 1){
            db.execSQL("CREATE TABLE USER_INFO (USER_ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "NAME TEXT," +
                    "GENDER TEXT," +
                    "EMAIL TEXT," +
                    "PASSWORD TEXT," +
                    "AGE INTEGER," +
                    "WEIGHT INTEGER," +
                    "HEIGHT INTEGER," +
                    "WEIGHT_MEASUREMENT_PREFERENCE TEXT," +
                    "WEIGHT_CONTROL TEXT," +
                    "WEIGHT_TARGET INTEGER," +
                    "PERIOD_TARGET INTEGER)"
            );

        }
    }


}
