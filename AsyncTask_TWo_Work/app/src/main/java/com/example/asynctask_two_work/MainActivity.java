package com.example.asynctask_two_work;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
{
    private Button sendTask;
    private TextView msg, msg2;
    private StringBuffer stringBuffer, stringBuffer2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData()
    {
        stringBuffer = new StringBuffer();
        stringBuffer2 = new StringBuffer();
    }

    private void initView()
    {
        sendTask = (Button) findViewById(R.id.send_task);
        msg = (TextView) findViewById(R.id.message);
        msg2 = (TextView) findViewById(R.id.message_2);
        sendTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getTask(1, "a", "b", "c", "d", "e");
                getTask(2, "f", "g", "h", "i", "j");
            }
        });
    }
    private AsyncTask<String, Integer, String[]> getTask(final int index, String... name){
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer2.delete(0, stringBuffer2.length());
        return new AsyncTask<String, Integer, String[]>()
        {
            private int count;
            @Override
            protected String[] doInBackground(String... params)
            {
                for(String s : params)
                {
                    try
                    {
                        int rand = (int)(Math.random()*3 + 1);
                        Thread.sleep(rand * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    publishProgress(count, params.length);
                    count++;
                }
                return params;
            }

            @Override
            protected void onProgressUpdate(Integer... values)
            {
                super.onProgressUpdate(values);
                int percent = (int)(100 / values[1] * count);
                if(index == 1)
                {
                    msg.setText(percent + "%");
                }
                else
                {
                    msg2.setText(percent + "%");
                }
            }

            @Override
            protected void onPostExecute(String[] s)
            {
                super.onPostExecute(s);
                for(String str : s)
                {
                    if(index == 1)
                    {
                        stringBuffer.append(str).append("\n");
                        msg.setText(stringBuffer);
                    }
                    else
                    {
                        stringBuffer2.append(str).append("\n");
                        msg2.setText(stringBuffer2);
                    }
                }
            }
        }.executeOnExecutor(Executors.newCachedThreadPool(), name);
    }
}
