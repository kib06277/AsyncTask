package com.example.asynctask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView msg;
    Button sendTask;
    StringBuffer stringBuffer = new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg = (TextView)findViewById(R.id.message);
        sendTask = (Button)findViewById(R.id.send_task);

        sendTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getTask("a", "b", "c", "d", "e");
            }
        });
    }

    private AsyncTask<String, Integer, String[]> getTask(String... name)
    {
        stringBuffer.delete(0, stringBuffer.length());
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
                        Thread.sleep(1000);
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
                msg.setText(percent + "%");
            }

            @Override
            protected void onPostExecute(String[] s)
            {
                super.onPostExecute(s);
                for(String str : s)
                {
                    stringBuffer.append(str).append("\n");
                    msg.setText(stringBuffer);
                }
            }
        }.execute(name);
    }
}
