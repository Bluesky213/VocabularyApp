package com.example.bluesky.vocabulary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.bluesky.vocabulary.MainActivity.displayTime;

/**
 * Created by Bluesky on 2018/7/23.
 */

public class Settings extends AppCompatActivity {
    TextView textView1, textView2;
    EditText editText1;
    Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        editText1 = (EditText) findViewById(R.id.editText1);
        button1=(Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    float num=Float.valueOf(String.valueOf(editText1.getText()));
                    displayTime= (int) (num*1000);
                    Toast.makeText(Settings.this,"修改成功",Toast.LENGTH_SHORT).show();
                    System.out.println("----------num:"+num);
                    System.out.println("----------displayTime:"+displayTime);
                }catch (Exception e)
                {
//                if(String.valueOf(num)==null){
                    Toast.makeText(Settings.this,"请输入数字",Toast.LENGTH_SHORT).show();
//                }
                }

            }
        });
    }
}