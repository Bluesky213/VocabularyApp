package com.example.bluesky.vocabulary;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import static com.example.bluesky.vocabulary.MainActivity.mySQLiteHelper;

public class vocabularyTable extends AppCompatActivity {
    SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_table);
        lv=(ListView)findViewById(R.id.lv);
        updateListView();
    }
    public void updateListView(){
        Cursor cr = db.rawQuery("select * from words order by id desc",null);
        //一共6列分别获取列名作为表头
        String id = cr.getColumnName(0);
        String english = cr.getColumnName(1);
        String chinese = cr.getColumnName(2);
        String wordType= cr.getColumnName(3);
        String englishToChinesePauseTime=cr.getColumnName(4);
        String chineseToEnglishPauseTime=cr.getColumnName(5);
        String[] ColumnNames = { id, english, chinese};
                //,wordType,englishToChinesePauseTime,chineseToEnglishPauseTime };
        ListAdapter adapter = new MySimpleCursorAdapter(vocabularyTable.this,
                R.layout.activity_vocabulary_table, cr, ColumnNames, new int[] { R.id.id,
                R.id.name, R.id.age });

        lv.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        onDestroy();
        Log.i("message", "数据库连接销毁");
        super.onPause();
    }

    @Override
    protected void onDestroy() {// 关闭数据库
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
