package com.example.bluesky.vocabulary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by Bluesky on 2018/9/3.
 */

public class MySQLiteHelper3 extends MySQLiteHelper {

    public MySQLiteHelper3(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        File appDatabaseFile= new MainActivity().getDatabasePath("vocabulary.db");//程序中的数据库存放地

        db=SQLiteDatabase.openOrCreateDatabase(appDatabaseFile,null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
