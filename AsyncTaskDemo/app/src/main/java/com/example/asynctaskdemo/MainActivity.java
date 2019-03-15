package com.example.asynctaskdemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "ASYNC_TASK";
    Button execute , cancel;
    ProgressBar progressBar;
    TextView textView;
    MyTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execute = (Button) findViewById(R.id.execute);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        textView = (TextView) findViewById(R.id.text_view);

        execute.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //注意每次需要 new 一個實例，新建的任務只能執行一次，否則會異常
                mTask = new MyTask();
                mTask.execute("http://www.baidu.com");

                execute.setEnabled(false);
                cancel.setEnabled(true);
            }

        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //OnCancelled 方法啟動取消正在執行的任務
                mTask.cancel(true);
            }
        });
    }

    private class MyTask extends AsyncTask<String, Integer, String>
    {
        //onPreExecute 方法用於在執行背景任務前做一些 UI操作
        @Override
        protected void onPreExecute()
        {
            Log.i(TAG, "onPreExecute() called");
            textView.setText("loading...");
        }

        //doInBackground 內部執行背景任務，不可以在此方法內修改 UI
        @Override
        protected String doInBackground(String... params)
        {
            Log.i(TAG, "doInBackground(Params... params) called");
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    long total = entity.getContentLength();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int count = 0;
                    int length = -1;
                    while ((length = is.read(buf)) != -1)
                    {
                        baos.write(buf, 0, length);
                        count += length;

                        //調用 publishProgress 顯示進度，最後 onProgressUpdate 將被執行
                        publishProgress((int) ((count / (float) total) * 100));
                    }
                    return new String(baos.toByteArray(), "gb2312");
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        //onProgressUpdate 用於更新進度信息
        @Override
        protected void onProgressUpdate(Integer... progresses)
        {
            Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
            progressBar.setProgress(progresses[0]);
            textView.setText("loading..." + progresses[0] + "%");
        }

        //onPostExecute 用於背景作業完畢更新 UI，顯示結果
        @Override
        protected void onPostExecute(String result)
        {
            Log.i(TAG, "onPostExecute(Result result) called");
            textView.setText(result);
            execute.setEnabled(true);
            cancel.setEnabled(false);
        }

        //onCancelled 用於取消執行中的任務並改變其 UI
        @Override
        protected void onCancelled()
        {
            Log.i(TAG, "onCancelled() called");
            textView.setText("cancelled");
            progressBar.setProgress(0);

            execute.setEnabled(true);
            cancel.setEnabled(false);
        }
    }
}
