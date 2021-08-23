package com.example.bluesky.vocabulary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bluesky on 2018/6/28.
 */

public class MySQLiteHelper extends SQLiteOpenHelper{

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists words( " +   //id,english,chinese,englishToChinesePauseTime,chineseToEnglishPauseTime
                " id integer primary key autoincrement," +  // 单词序号
                " english varchar," +                       //英语单词
                " chinese varchar," +                       //中文意思
                " wordType varchar," +                       //单词词性
                " englishToChinesePauseTime integer," +     //英译中暂停次数
                " chineseToEnglishPauseTime integer) ");    //中译英暂停次数
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
