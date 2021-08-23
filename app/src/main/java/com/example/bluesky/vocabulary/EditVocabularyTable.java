package com.example.bluesky.vocabulary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.bluesky.vocabulary.MainActivity.mySQLiteHelper;

public class EditVocabularyTable extends AppCompatActivity {
    EditText editText1,editText2;
    Spinner spinner;
    Button insertButton,importButton,exportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vocabulary_table);
        editText1=(EditText)findViewById(R.id.editText1);
        editText2=(EditText)findViewById(R.id.editText2);
        spinner=(Spinner)findViewById(R.id.wordTypeSpinner);
        insertButton=(Button)findViewById(R.id.insertWord);
        importButton=(Button)findViewById(R.id.importButton);
        exportButton=(Button)findViewById(R.id.exportButton);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word= String.valueOf(editText1.getText());//生单词
                String chinese= String.valueOf(editText2.getText());//中文意思
                String wordType= (String) spinner.getSelectedItem();
                MainActivity.addWordIntoTable(mySQLiteHelper,word,chinese,wordType);
                Toast toast = Toast.makeText(EditVocabularyTable.this,"Word added successfully!",Toast.LENGTH_SHORT);
                toast.show();
                editText1.setText("");editText2.setText("");
            }
        });
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoregePermissions();
                //new MainActivity().releaseDataBaseToApplicationData2();
                try {
                    new MainActivity().assetsDataToSD();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //copyDbToSDcard();//导出数据库文件
                Toast toast = Toast.makeText(EditVocabularyTable.this,"Data exported successfully!",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoregePermissions();
                if (restoreDatabase())
                {
                    Toast toast = Toast.makeText(EditVocabularyTable.this,"Data imported successfully!",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    //第二种动态获取权限的方法
    public void verifyStoregePermissions()
    {
                if (Build.VERSION.SDK_INT >= 23)//如果安卓版本大于6.0
        {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }


    //有效的能够复制数据库文件的代码——从程序到SD卡
    public  void copyDbToSDcard() {
        // File dataDirectory = Environment.getDataDirectory();
        File databaseFile = EditVocabularyTable.this.getDatabasePath("vocabulary.db");
        System.out.println("databaseFile:-------"+databaseFile);//---/data/user/0/com.example.bluesky.vocabulary/databases/vocabulary.db
        try {
            File f = new File(Environment.getExternalStorageDirectory()
                    + "/" + "vocabulary.db");//这里的参数是数据库的名字
            FileOutputStream fs = new FileOutputStream(f);
            FileInputStream input = new FileInputStream(databaseFile);
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = input.read(buffer, 0, 1024)) > 0) {
                fs.write(buffer, 0, len);
            }
            fs.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //将SD卡中的数据库文件移动到程序中覆盖替换旧数据库——从SD卡到程序
    public boolean restoreDatabase()
    {
        boolean success=false;
        File databaseFile = EditVocabularyTable.this.getDatabasePath("vocabulary.db");//程序中的数据库
        try {
            File f = new File(Environment.getExternalStorageDirectory()
                    + "/" + "vocabulary1.db");//SD卡中数据库的文件对象
            FileOutputStream fs = new FileOutputStream(databaseFile);//输出数据库的目标路径——程序中存放数据库位置
            FileInputStream input = new FileInputStream(f);
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = input.read(buffer, 0, 1024)) > 0) {
                fs.write(buffer, 0, len);
            }
            fs.close();
            input.close();
            success=true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(EditVocabularyTable.this,"请将要导入的数据库文件放置在手机根目录下并命名成vocabulary1.db!",Toast.LENGTH_SHORT);
            toast.show();
        }
        return success;
    }
    //无效的复制数据库文件的代码
    //1.实现复制文件的功能

    //设置目标文件和目标目录
//    public  boolean save() throws IOException {
//        //        一种动态获取权限的方法
//        if (Build.VERSION.SDK_INT >= 23)//如果安卓版本大于6.0
//        {
//            int REQUEST_CODE_CONTACT = 101;
//            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            //验证是否许可权限
//            for (String str : permissions) {
//                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                    //申请权限
//                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
//                }
//            }
//        }
//
//        String dbpath=EditVocabularyTable.this.getDatabasePath("vocabulary.db").getPath();//获取到内部存储的数据库路径
//        String destinationPath=Environment.getExternalStorageDirectory()+ "/"+ "vocabulary.db";//数据库复制目标路径
//        File file=new File(destinationPath);
//        System.out.println("数据库复制路径可读？："+file.canRead());
//        System.out.println("数据库复制路径可写？："+file.canWrite());
//        //InputStream inputStream=new FileInputStream(new File(dbpath));
//        //OutputStream outputStream=new FileOutputStream(new File(destinationPath));
//        System.out.println("Database:-------"+dbpath);
//        System.out.println("FileStorage:-------------"+destinationPath);
//        boolean success=copyFile(dbpath, destinationPath);
//
//
//        return success;
//    }


//    public  boolean copyFile(String source, String dest) {
//        try {
//            File f1 = new File(source);
//            File f2 = new File(dest);
//            InputStream in = new FileInputStream(f1);
//
//            OutputStream out = new FileOutputStream(f2);
//
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0)
//                out.write(buf, 0, len);
//
//            in.close();
//            out.close();
//        } catch (FileNotFoundException ex) {
//            return false;
//        } catch (IOException e) {
//            return false;
//        }
//
//        return true;
//    }


    //2.无效的尝试复制数据库文件的代码
//        public static boolean fileCopy(String oldFilePath,String newFilePath) throws IOException {
//        //如果原文件不存在
//        if(fileExists(oldFilePath) == false){
//            return false;
//        }
//        //获得原文件流
//        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
//        byte[] data = new byte[1024];
//        //输出流
//        FileOutputStream outputStream =new FileOutputStream(new File(newFilePath));
//        //开始处理流
//        while (inputStream.read(data) != -1) {
//            outputStream.write(data);
//        }
//        inputStream.close();
//        outputStream.close();
//        return true;
//    }
//
//    public static boolean fileExists(String filePath) {
//        File file = new File(filePath);
//        return file.exists();
//    }


}
