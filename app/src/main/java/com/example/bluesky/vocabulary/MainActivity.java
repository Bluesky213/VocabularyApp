package com.example.bluesky.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//Version: 1.0.0
//Date: 2018/07/18 0:22
public class MainActivity extends AppCompatActivity {
    //Version2:
    //模式:
    //      1.随机顺序
    //      2.插入顺序升序
    //      3.插入顺序倒序
    //      4.难度值降序-----英译中难度值降序


    //点开程序
    //1.点击运行按钮
        //--如果在记单词，rememberStatus=1,暂停记单词的状态，并且pauseTime+1                               Finished
        //--如果没有在记单词，并且是第一次记单词，rememberStatus状态=0，程序开始执行                           Finished
        //     没有在记单词，rememberStatus状态=0，但是是处于暂停状态，那么点击按钮开始继续记单词                Finished
        //--长按按钮，回到初始显示状态，数据不清除

    //实现数据库能够导出，电脑和手机上都能编辑                                                              Finished

    //尝试怎么在TextView上面加Button，然后点击显示单词的textView的时候暂停并更新pauseTime                     Finished
    //尝试用sharedPreference来存储用户数据，然后实现按照不同的方式来复习单词的行为

    //开发中译英模式，功能设计与上面的趋同                                                                 Finished
    //数据库修改，更新成englishToChinese_pauseTime记录英译中和chineseToEnglish_pauseTime记录中译英的情况     Finished

    //添加 用户能够添加和删除单词库的功能(最好新建一个表一样的页面)，然后在这个页面上可以导出单词表文件               -------

    //UI:
    //1.记单词时遇到了不会的点击暂停时textView的颜色会改变，再点继续时颜色会变回来                               Finished
    //2.新增进度条，记单词时显示当前记单词占总单词量的进度                                                   -----线程4、5应该按照排序来显示进度而不是id
    //3.让所有的单词都能一行显示出来————根据单词的长度textview自适应                                         -------
    //4.新增点击播放声音或者textview有特效。提升用户体验
    //5.去掉状态栏                                                                                    Finished--可改进
    //6.去掉标题栏
    //7.将Button放到右上角                                                                            Finished--可改进

    //Bugs:
    //1.出现了双击textView时 启动了两个线程同时控制textView导致显示不正常的问题                               -------可用监听按键时间来实现,间隔小于0.7就不执行
    //2.按下home键后程序还在后台运行自动播放单词
    //3.点开app的时候直接先点textView1会报错                                                             Finished

    //改进：
    //1.添加能够按照单词类型来复习的功能                                                                  -------
    //2.添加分别 按照两种难度值来复习的功能                                                                Finished
    //3.设置中添加 设置单词显示颜色和点击后显示颜色
    //4.设置中添加控制单词显示间隔时间                                                                    Finished
    //5.让程序在第一次启动时用自带的文件夹中的数据库而不是创建数据库
    public static int runningStatus=0;//展示单词的状态
    public static int rememberMode=1;//记单词的模式 1.英译中 2.中译英 3.中英双显
    public static int displayTime=1500;
    public static String rememberingWord=null;
    public static long startMillimeter=0;
    public static long endMillimeter;
    public static boolean chineseTextViewStatus=true;
    public static boolean englishTextViewStatus=true;

    Button startButton,jump,test;
    TextView textView1,textView2;
    Spinner changeRememberMode;
    ProgressBar progressBar;
    Switch englishDisplaySwitch,chineseDisplaySwitch;
    public RangeSelectionView rangeSelectionView;
    public static MySQLiteHelper mySQLiteHelper;
    Handler handler=new Handler();
    public static int numberOfWords;

    public static int progressNumStart;
    public static int progressNumEnd;
    public int realTime_progressNumStart;
    public int realTime_progressNumEnd;

    //更新进度条
    Thread updateProgressBarThread=new Thread(){
        int progress;
        public void run()
        {
//           progressNumStart=rangeSelectionView.getNumStart();
//           progressNumEnd  =rangeSelectionView.getNumEnd();
//           System.out.println("progressNumStart:"+progressNumStart+" progressNumEnd:"+progressNumEnd);
            hideBottomUIMenu();
            switch (rememberMode)
            {
                case 1:
                    progress=searchIdByEnglishWord(mySQLiteHelper,rememberingWord)*1000/numberOfWords;//总单词数
                    progressBar.setProgress(progress);//设置进度
                    System.out.println("---------------progress:"+progress);
                    break;
                case 2:
                    progress=searchIdByEnglishWord(mySQLiteHelper,rememberingWord)*1000/numberOfWords;//总单词数
                    progressBar.setProgress(progress);//设置进度

                    break;
                case 3:
                    progress=searchIdByEnglishWord(mySQLiteHelper,rememberingWord)*1000/numberOfWords;//总单词数
                    progressBar.setProgress(progress);//设置进度

                    break;
                case 4://英译中模式
                    progress=searchIdByEnglishWord(mySQLiteHelper,rememberingWord)*1000/numberOfWords;//总单词数
                    progressBar.setProgress(progress);//设置进度

                    break;
                case 5://中译英模式
                    progress=searchIdByEnglishWord(mySQLiteHelper,rememberingWord)*1000/numberOfWords;//总单词数
                    progressBar.setProgress(progress);//设置进度

                    break;
            }
        }
    };








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //setContentView(R.layout.titlebar);
        setContentView(R.layout.activity_main);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        startButton=(Button)findViewById(R.id.startButton);
        //测试按钮
        //test=(Button)findViewById(R.id.test);
        //jump=(Button)findViewById(R.id.jump);

        hideThread.start();
        textView1=(TextView)findViewById(R.id.textView1);
        textView2=(TextView)findViewById(R.id.textView2);
        changeRememberMode=(Spinner)findViewById(R.id.changeRememberMode);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        englishDisplaySwitch=(Switch)findViewById(R.id.switch1);
        chineseDisplaySwitch=(Switch)findViewById(R.id.switch2);
        rangeSelectionView=(RangeSelectionView)findViewById(R.id.rangeSelectionView);


        //progressBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.blue));

        //第一次启动进入程序，将准备好的.db数据库文件替换到运行文件夹中database目录下面然后OK
//        File appDatabaseFile= MainActivity.this.getDatabasePath("vocabulary.db");//程序中的数据库存放地
//        if(appDatabaseFile.exists())
//        {
//        }
//        else
//        {
//            releaseDataBaseToApplicationData();
//        }
        //SQLiteDatabase database=SQLiteDatabase.openOrCreateDatabase(appDatabaseFile,null);
        //mySQLiteHelper=new MySQLiteHelper(this,"vocabulary",null,3);


        //创建数据库
        mySQLiteHelper=new MySQLiteHelper(this,"vocabulary.db",null,3);

        if (searchNumberOfWords(mySQLiteHelper)==0)//第一次启动软件数据库中没有数据就插入几条初始数据
            insertBaseData(mySQLiteHelper);//插入初始数据

        numberOfWords=searchNumberOfWords(mySQLiteHelper);//数据库中的总单词数
        rangeSelectionView.endNum=numberOfWords;//范围选择进度的Max值设成单词数
        System.out.println("---------------------Number of words in main:"+numberOfWords);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(runningStatus==1)
                {
                    //如果是在记单词，点击时什么也不做
                    //textView1.setTextColor(getResources().getColor(R.color.red));
                    Toast.makeText(MainActivity.this, "已经在记单词中！", Toast.LENGTH_SHORT).show();
                }
                else if(runningStatus==0)//暂停状态
                {
                    endMillimeter=System.currentTimeMillis();
                    if(endMillimeter-startMillimeter>1000)//若果一次双击事件的事件大于1s
                    {

                        textView1.setTextColor(getResources().getColor(R.color.blue));
                        runningStatus = 1;//切换状态值
                        numberOfWords = searchNumberOfWords(mySQLiteHelper);//开始记单词时就查询总单词量，根据总单词数量来进行循环
                        System.out.println("----------------:" + numberOfWords);//上面都是正常的等于5
                        //用hanlder将runnable线程Post出去
                        switch (rememberMode) {
                            case 1:
                                handler.post(threadMode1);
                                break;
                            case 2:
                                handler.post(threadMode2);
                                break;
                            case 3:
                                handler.post(threadMode3);
                                break;
                            case 4:
                                handler.post(threadMode4);
                                break;
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"请不要过快切换开始/暂停状态！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {//点击中文单词的时候暂停，并且陌生度+1
            @Override
            public void onClick(View v) {
                System.out.println("点击了textView1--------------rememberingWord:"+rememberingWord);
                switch (runningStatus)
                {
                    case 1:
                        startMillimeter=System.currentTimeMillis();//点下瞬间记录当前时间————一次双击事件开始时间 开始——暂停
                            runningStatus=0;
                            textView1.setTextColor(getResources().getColor(R.color.red));
                            switch (rememberMode)
                            {
                                //这里的handler.post可以去掉
                                case 1:addEnglishToChinesePauseTime(mySQLiteHelper);break;
                                case 2:handler.post(threadMode2);addEnglishToChinesePauseTime(mySQLiteHelper);break;//post与否效果都一样
                                case 3:handler.post(threadMode3);addEnglishToChinesePauseTime(mySQLiteHelper);break;
                                case 4:handler.post(threadMode4);addEnglishToChinesePauseTime(mySQLiteHelper);break;
                            }
                            //System.out.println("运行了情况1");
                            break;


                    case 0:
                        endMillimeter=System.currentTimeMillis();//点下瞬间记录当前时间————一次双击事件结束时间 暂停——开始
                        if(endMillimeter-startMillimeter>1000)//若果一次双击事件的事件大于1s
                        {
                            textView1.setTextColor(getResources().getColor(R.color.blue));
                            if(rememberingWord!=null&&searchIdByEnglishWord(mySQLiteHelper,rememberingWord)<numberOfWords)
                            {
                                runningStatus=1;
                                switch (rememberMode)
                                {
                                    case 1:handler.post(threadMode1);break;
                                    case 2:handler.post(threadMode2);break;
                                    case 3:handler.post(threadMode3);break;
                                    case 4:handler.post(threadMode4);break;
                                }
                                //System.out.println("运行了情况2");
                            }
                            break;
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"请不要过快切换开始/暂停状态！",Toast.LENGTH_SHORT).show();
                        }
                }


            }

        });

        englishDisplaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    englishTextViewStatus=true;
                    textView1.setVisibility(View.VISIBLE);
                }
                if(!isChecked)
                {
                    englishTextViewStatus=false;
                    textView1.setVisibility(View.INVISIBLE);
                }
            }
        });

        chineseDisplaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    chineseTextViewStatus=true;
                    textView2.setVisibility(View.VISIBLE);
                }
                else
                {
                    chineseTextViewStatus=false;
                    textView2.setVisibility(View.INVISIBLE);
                }
            }
        });

        changeRememberMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rememberMode=changeRememberMode.getSelectedItemPosition()+1;//选中第一个值为0
                runningStatus=0;

                System.out.println("changeSpinner:-------------------"+changeRememberMode.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //rangeSelectionView.setOnTouchListener();
    }
    //创建菜单
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE,1,Menu.NONE,"+");
        menu.add(Menu.NONE,2,Menu.NONE,"Settings");
        menu.add(Menu.NONE,3,Menu.NONE,"SettingsActivity");
        menu.add(Menu.NONE,4,Menu.NONE,"单词表");
        //menu.add(Menu.NONE,2,Menu.NONE,"编辑");
        //menu.add(Menu.NONE,3,Menu.NONE,"查看信息");
        //menu.add(Menu.NONE,4,Menu.NONE,"删除");
        //menu.add(Menu.NONE,5,Menu.NONE,"查询");
        //menu.add(Menu.NONE,6,Menu.NONE,"导入到手机电话簿");
        //menu.add(Menu.NONE,7,Menu.NONE,"退出");
        return super.onCreateOptionsMenu(menu);
    }
    //监听菜单选择
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId())
        {
            case 1:Intent intent1=new Intent(MainActivity.this,EditVocabularyTable.class);
                startActivity(intent1);break;
            case 2:Intent intent2=new Intent(MainActivity.this,Settings.class);
                startActivity(intent2);break;
            case 3:Intent intent3=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent3);break;
            case 4:Intent intent4=new Intent(MainActivity.this,vocabularyTable.class);
                startActivity(intent4);break;

        }
        return super.onOptionsItemSelected(menuItem);
    }


    //插入几个初始单词如果没有的话
    public void insertBaseData(MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        db.delete("words",null,null);
        db.execSQL("insert into words(english,id,chinese,wordType,englishToChinesePauseTime,chineseToEnglishPauseTime) values('Success',null,'成功','v',0,0)  ");//插入单词、中文意思、暂停次数
        db.execSQL("insert into words(english,id,chinese,wordType,englishToChinesePauseTime,chineseToEnglishPauseTime) values('SONY'   ,null,'索尼','n',0,0)  ");//插入单词、中文意思、暂停次数
        db.execSQL("insert into words(english,id,chinese,wordType,englishToChinesePauseTime,chineseToEnglishPauseTime) values('Google' ,null,'谷歌','n',0,0)  ");//插入单词、中文意思、暂停次数
        db.execSQL("insert into words(english,id,chinese,wordType,englishToChinesePauseTime,chineseToEnglishPauseTime) values('Huawei' ,null,'华为','n',0,0)  ");//插入单词、中文意思、暂停次数
        db.close();
    }

    //从数据库寻找单词
    public String searchEnglishWord(MySQLiteHelper mySQLiteHelper,int i){
        System.out.println("the value of i:"+i);//可以获取到i的值为1
        String english="";
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from words where id='"+i+"'",null);//查询序号为i的单词
        if(cursor.moveToFirst())
        {
            english=cursor.getString(i);//利用游标查询单词
        }
        cursor.close();
        db.close();
        return english;
    }

    //通过Id获取表格的单词，第i行第二列,getString(1)
    public String searchEnglishWordById(MySQLiteHelper mySQLiteHelper,int i){
        System.out.println("---------searchEnglishWord()中i的值："+i);
        String english;
        Cursor cursor;
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        cursor=db.rawQuery("select * from words where id='"+i+"'",null);//查询所有行内容,此时游标指向第0行数据
        cursor.moveToFirst();//游标指向第一行数据
        //System.out.println("-------------searchId:"+cursor.getInt(0));
        //System.out.println("-------------searchEnglishWord2:"+cursor.getString(1));
        english=cursor.getString(1);//获取第一列数据english字段
        cursor.close();
        db.close();
        return english;
    }
    //通过Id获取表格的单词，第i行第二列,getString(1)
    public String searchEnglishWordByIdWithRange(MySQLiteHelper mySQLiteHelper,int i){
        //System.out.println("---------searchEnglishWord()中i的值："+i);
        String english;
        Cursor cursor;
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();

        cursor=db.rawQuery("select * from words where id between "+i+" and "+rangeSelectionView.getNumEnd()+" order by id asc",null);//查询所有行内容,此时游标指向第0行数据
        cursor.moveToFirst();//游标指向第一行数据
        //System.out.println("-------------searchId:"+cursor.getInt(0));
        //System.out.println("-------------searchEnglishWord2:"+cursor.getString(1));
        english=cursor.getString(1);//获取第一列数据english字段
        cursor.close();
        db.close();
        return english;
    }

    public String searchFormulaById(MySQLiteHelper mySQLiteHelper,int i){
        System.out.println("---------searchEnglishWord()中i的值："+i);
        String english;

        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from words where wordType='formula' order by id desc",null);//查询所有行内容,此时游标指向第0行数据
        cursor.move(i);//游标指向第一行数据
        //System.out.println("-------------searchId:"+cursor.getInt(0));
        //System.out.println("-------------searchEnglishWord2:"+cursor.getString(1));
        english=cursor.getString(1);//获取第一列数据english字段
        cursor.close();
        db.close();
        return english;
    }
    //返回公式的总数
    public Integer searchFormulaNumber(MySQLiteHelper mySQLiteHelper){
        int number;
        Cursor cursor;
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        cursor=db.rawQuery("select count(*) from words where wordType='formula'",null);//查询所有行内容,此时游标指向第0行数据
        cursor.moveToNext();//游标指向第一行数据
        //System.out.println("-------------searchId:"+cursor.getInt(0));
        //System.out.println("-------------searchEnglishWord2:"+cursor.getString(1));
        number=cursor.getInt(0);//获取第一列数据english字段
        cursor.close();
        db.close();
        return number;
    }
    //通过英文单词获取对应的Id,第i行第一列，getInt(3)
    public Integer searchIdByEnglishWord(MySQLiteHelper mySQLiteHelper,String english)
    {
        int id;
        Cursor cursor;
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        cursor=db.rawQuery("select * from words where english='"+english+"'",null);//db.rawQuery(String,null)需要弄清楚
        cursor.moveToNext();
        id=cursor.getInt(0);
        return id;
    }

    //通过Id获取英文的中文意思，第i行第三列,getString(2)
    public String searchChineseById(MySQLiteHelper mySQLiteHelper,int i){
        String chinese;
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor;
        cursor=db.rawQuery("select * from words ",null);//查询所有行内容,此时游标指向第0行数据
        cursor.move(i);//游标指向第一行数据
        System.out.println("-------------searchEnglishWord2:"+cursor.getString(2));
        chinese=cursor.getString(2);//获取第一列数据
        cursor.close();
        db.close();
        return chinese;
    }
    //通过现在正在记的单词搜索中文
    public String searchChineseByEnglish(MySQLiteHelper mySQLiteHelper,String rememberingWord)
    {
        String chinese;
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor;
        cursor=db.rawQuery("select chinese from words where english='"+rememberingWord+"'",null);//查询所有行内容,此时游标指向第0行数据
        cursor.moveToFirst();//游标指向第一行数据
        chinese=cursor.getString(0);//获取第一列数据
        cursor.close();
        db.close();
        return chinese;
    }

    //返回数据库总单词数
    public int searchNumberOfWords(MySQLiteHelper mySQLiteHelper){
        String numOfWords="";
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from words",null);
        if(cursor.moveToFirst())
        {
            numOfWords=cursor.getString(0);
        }
        cursor.close();
        db.close();
        return Integer.valueOf(numOfWords);
    }

    public static void addWordIntoTable(MySQLiteHelper mySQLiteHelper,String word,String chinese,String wordType){
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        db.execSQL("insert into words (english,id,chinese,wordType,englishToChinesePauseTime,chineseToEnglishPauseTime)" +
                "values ('"+word+"',null,'"+chinese+"','"+wordType+"',0,0)");
        db.close();
    }

    //英译中记单词模式 难度值+1
    public static void addEnglishToChinesePauseTime(MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        db.execSQL("update words set englishToChinesePauseTime=englishToChinesePauseTime+1 where english='"+rememberingWord+"'  " );
        db.close();
    }


    //中译英记单词模式 难度值+1
    public static void addChineseToEnglishPauseTime(MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        db.execSQL("update words set chineseToEnglishPauseTime=chineseToEnglishPauseTime+1 where english='"+rememberingWord+"'  ");
        db.close();
    }
    //按照英译中难度值降序来查询单词
    public static String searchWordOrderByEnglishToChinesePauseTime(MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select english,EnglishToChinesePauseTime from words order by EnglishToChinesePauseTime desc",null);
        String english=cursor.getString(0);
        cursor.close();
        db.close();
        return english;
    }
    //按照中译英难度值降序来查询单词
    public static String searchWordOrderByChineseToEnglishPauseTime(MySQLiteHelper mySQLiteHelper,int i){
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select english,ChineseToEnglishPauseTime from words order by ChineseToEnglishPauseTime desc",null);
        cursor.move(i);
        String english=cursor.getString(0);

        cursor.close();
        db.close();
        return english;
    }

    //将开发apk中assets文件夹中自带的数据库替换程序中的数据库
    public  void releaseDataBaseToApplicationData() {
        // File dataDirectory = Environment.getDataDirectory();
        File databaseFile = MainActivity.this.getDatabasePath("vocabulary.db");//程序中的数据库存放地
        try {
            //File f = new File(Environment.getExternalStorageDirectory()
              //      + "/" + "vocabulary.db");//这里的参数是数据库的名字
            File f=new File(String.valueOf(MainActivity.this.getAssets().open("vocabulary0820_444.db")));//assets中的数据库文件
            FileInputStream input = new FileInputStream(f);
            FileOutputStream output = new FileOutputStream(databaseFile);
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = input.read(buffer, 0, 1024)) > 0) {
                output.write(buffer, 0, len);
            }
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void releaseDataBaseToApplicationData2() {
        // File dataDirectory = Environment.getDataDirectory();
        //File databaseFile = new File(Environment.getExternalStorageDirectory()
        //        + "/" + "vocabulary.db");//程序中的数据库存放地
        try {
            //File f = new File(Environment.getExternalStorageDirectory()
            //      + "/" + "vocabulary.db");//这里的参数是数据库的名字
            //File assetsFile=new File(String.valueOf();//assets中的数据库文件
            InputStream input =MainActivity.this.getAssets().open("vocabulary0820_444.db");
            OutputStream output = new FileOutputStream(new File(Environment.getExternalStorageDirectory()
                    + "/" + "vocabulary.db"));
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = input.read(buffer, 0, 1024)) > 0) {
                output.write(buffer, 0, len);
            }
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void assetsDataToSD() throws IOException
    {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory()
                + "/" + "vocabulary.db");
        Context context=MainActivity.this;
        //System.out.println("R.id-------------"+getResources().openRawResource(com.example.bluesky.vocabulary.R.raw.vocabulary0820_4444));
        myInput = getResources().openRawResource(com.example.bluesky.vocabulary.R.raw.vocabulary0820_4444);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    Thread hideThread=new Thread() {
        public void run()
        {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                View v = getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    };

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideBottomUIMenu();
        }
    }


    //(效率改进思路)返回数据库查询到的数据游标对象Cursor，目的是想通过 一次性通过Cursor获得所有的数据，然后再按需取来用而大量减少对数据库的操作
    public Cursor searchEnglishWord(MySQLiteHelper mySQLiteHelper){
        String english="";
        Cursor cursor;
        System.out.println("the value of i:");//可以获取到i的值为1
        SQLiteDatabase db=mySQLiteHelper.getWritableDatabase();
        cursor=db.rawQuery("select * from words ",null);//查询所有行内容
        cursor.moveToFirst();
        System.out.println("--------------searchEnglishWord:"+cursor.getString(0));
        cursor.close();
        db.close();
        return cursor;
    }



    //----------------------------线程代码-------------------------------------------

    //(算法改进)通过thread2来调用thread进行 thread的wait()和notify操作来暂停和继续，而不是像现在的思路：如果是在记单词就状态变=0，然后textView不继续更替显示
    //如果不是在记单词就状态变为1,然后继续post线程
    //随机英译中模式

    Thread threadMode1=new Thread(){
        int i;
        public void run(){
            i=(int)(Math.random()*numberOfWords)+1;//设置一个随机的单词id
            if(rememberMode==1)
            {
                if(runningStatus==1)
                {
                    rememberingWord=searchEnglishWordById(mySQLiteHelper,i);
                    textView1.setText(rememberingWord);
                    textView2.setText(searchChineseByEnglish(mySQLiteHelper,rememberingWord));
                    System.out.println("显示第"+i+"个单词："+rememberingWord);//输出正在显示的单词信息
                    handler.postDelayed(this,displayTime);
                }
                if(runningStatus==0)
                {

                }
                System.out.println("--------Thread一轮结束i的值是："+i);
                System.out.println("--------Thread一轮结束runningStatus-----Run():"+runningStatus);
            }
            handler.post(updateProgressBarThread);
        }
    };

    //插入顺序升序
    Thread threadMode2=new Thread(){
        int i=progressNumStart;//设置一个循环的次数  //每次i都被赋值选择的下限所以单词不变?
        public void run(){
            //实时更新，放在进度条方法里面不
            progressNumStart=rangeSelectionView.getNumStart();
            progressNumEnd  =rangeSelectionView.getNumEnd();
            System.out.println("NumStart:"+rangeSelectionView.getNumStart()+" i:"+i);
            if(rememberMode==2)
            {
                if(runningStatus==1)
                {
                    rememberingWord=searchEnglishWordByIdWithRange(mySQLiteHelper,i);   //似乎i没有起作用并且按照id查询是以10增长来算的1、11、21、31、41...
                    //textView1和textView2都显示但是根据switch来区分
                    textView1.setText(rememberingWord);
                    textView2.setText(searchChineseByEnglish(mySQLiteHelper,rememberingWord));

                    System.out.println("显示第"+i+"个单词："+rememberingWord);//输出正在显示的单词信息
                    i++;

                    if(i<numberOfWords&runningStatus==1)
                    {
                        handler.postDelayed(this,displayTime);//每个单词停顿2s
                        System.out.println("------------运行状态："+runningStatus);
                    }
                    if(i==progressNumEnd&runningStatus==1)
                    {
                        handler.postDelayed(this,displayTime);//最后一次post让i在i++后变成numberOfWords+1
                        System.out.println("------------运行状态："+runningStatus);
                    }
                    if(i==progressNumEnd+1&&runningStatus==1)//如果展示完成
                    {
                        runningStatus=0;//暂停记单词状态
                        i=1;            //一轮结束从头再来
                        System.out.println("------------运行状态："+runningStatus);
                    }
                }
                if(runningStatus==0)
                {

                }
                System.out.println("--------Thread一轮结束i的值是："+i);
                System.out.println("--------Thread一轮结束runningStatus-----Run():"+runningStatus);
            }
            handler.post(updateProgressBarThread);
        }
    };


    //插入顺序降序
    Thread threadMode3=new Thread(){
        int i=0;
        public void run(){
            System.out.println("------------The value of numberofwords is-------:"+numberOfWords);//----------------5
            System.out.println("The value of i is:"+i);//-----------------0
            System.out.println("The value of numberOfWords-i is:"+String.valueOf(numberOfWords-i));//-----------------0
            //System.out.println("The value of i is:"+temp_i);//-----------------0
            if(rememberMode==3)
            {
                if(runningStatus==1)
                {
                    rememberingWord=searchEnglishWordById(mySQLiteHelper,numberOfWords-i);
                    //textView1和textView2都显示但是根据switch来区分
                    textView1.setText(rememberingWord);
                    textView2.setText(searchChineseByEnglish(mySQLiteHelper,rememberingWord));

                    System.out.println("显示第"+i+"个单词："+rememberingWord);//输出正在显示的单词信息
                    i++;

                    if(numberOfWords-i<numberOfWords&runningStatus==1)
                    {
                        handler.postDelayed(this,displayTime);//每个单词停顿2s
                        System.out.println("------------运行状态："+runningStatus);
                    }
                    if(numberOfWords-i==numberOfWords&runningStatus==1)
                    {
                        handler.postDelayed(this,displayTime);//最后一次post让i在i++后变成numberOfWords+1
                        System.out.println("------------运行状态："+runningStatus);
                    }
                    if(numberOfWords-i==0&&runningStatus==1)//如果展示完成
                    {
                        runningStatus=0;//暂停记单词状态
                        i=0;            //一轮结束从头再来
                        System.out.println("------------运行状态："+runningStatus);
                    }
                }
                if(runningStatus==0)
                {

                }
                System.out.println("--------Thread一轮结束i的值是："+i);
                System.out.println("--------Thread一轮结束runningStatus-----Run():"+runningStatus);
            }
            handler.post(updateProgressBarThread);
        }
    };

    //英译中难度值降序模式
    Thread threadMode4=new Thread(){
        int i=1;//设置一个循环的次数
        public void run(){
            if(rememberMode==4)
            {
                if(runningStatus==1)
                {
                    rememberingWord=searchWordOrderByEnglishToChinesePauseTime(mySQLiteHelper);
                    textView1.setText(rememberingWord);
                    textView2.setText(searchChineseByEnglish(mySQLiteHelper,rememberingWord));
                    System.out.println("显示第"+i+"个单词："+rememberingWord);//输出正在显示的单词信息
                    i++;

                    if(i<numberOfWords&runningStatus==1)
                    {
                        handler.postDelayed(this,displayTime);//每个单词停顿2s
                        System.out.println("------------运行状态："+runningStatus);
                    }
                    if(i==numberOfWords&runningStatus==1)
                    {
                        handler.postDelayed(this,displayTime);//最后一次post让i在i++后变成numberOfWords+1
                        System.out.println("------------运行状态："+runningStatus);
                    }
                    if(i==numberOfWords+1&&runningStatus==1)//如果展示完成
                    {
                        runningStatus=0;//暂停记单词状态
                        i=1;            //一轮结束从头再来
                        System.out.println("------------运行状态："+runningStatus);
                    }
                }
                if(runningStatus==0)
                {

                }
                System.out.println("--------Thread一轮结束i的值是："+i);
                System.out.println("--------Thread一轮结束runningStatus-----Run():"+runningStatus);
            }
            handler.post(updateProgressBarThread);
        }
    };

}

