package com.example.external_asynctask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
{
    ImageView iv;
    Button button;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView)findViewById(R.id.iv);
        button = (Button)findViewById(R.id.button);

        //下載
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //實作外部 Asynctask
                DownloadAsynctask task = new DownloadAsynctask(iv, MainActivity.this);
                //execute方法执行后，会调用异步任务的doInBackground方法
                task.execute("http://p2.so.qhmsg.com/t01fb3e43c8cd9ee917.jpg");
            }
        });
    }
}
