package com.example.external_asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAsynctask extends AsyncTask<String, Integer, byte[]>
{
    private ImageView iv;
    private Context context; //上下文的對象
    private ProgressDialog dialog; //進度對話框

    /*
     * 把主頁資料接收過來
     */
    public DownloadAsynctask(ImageView iv, Context context)
    {
        super();
        this.iv = iv;
        this.context = context;
    }

    /*
     * 調用當前類別的 execute 方法，準備數據(加載進度條)
     */
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        dialog = new ProgressDialog(context);

        dialog.setIcon(R.drawable.ic_launcher_background);
        dialog.setTitle("正在下載中..");
        dialog.setMessage("請耐心等待");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平進度條
        dialog.setCancelable(false); //關閉返回
        dialog.show();
    }

    /*
     * 背景取得網路的資源
     */
    @Override
    protected byte[] doInBackground(String... params)
    {
        try
        {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            byte[] result = null;
            if (conn.getResponseCode() == 200)
            {
                InputStream is = conn.getInputStream();
                //取得下載的總數量
                int totalSize = conn.getContentLength();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int len = 0;
                //紀錄目前下載進度
                int currentSize = 0;
                while((len = is.read(buffer))!=-1)
                {
                    baos.write(buffer, 0, len);
                    currentSize += len; //更新目前下載進度
                    //通過 publishProgress 來呼叫非同步任務中的 onProgressUpdate
                    publishProgress(totalSize, currentSize); //把數據傳給 onProgressUpdate 進行 UI 更新
                }

                //返回的結果傳給 onPostExecute
                result = baos.toByteArray();
            }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /*
     * 獲得 doInBackground 傳遞過來的參數，進行 UI 更新
     */
    @Override
    protected void onPostExecute(byte[] result)
    {
        super.onPostExecute(result);
        if (result != null)
        {
            //把 doInBackground 傳遞過來的轉成 Bitmap
            Bitmap bm = BitmapFactory.decodeByteArray(result, 0, result.length);
            iv.setImageBitmap(bm);
        }
        else
        {
            Toast.makeText(context, "連線失敗", 0).show();
        }
        dialog.dismiss();
    }

    /*
    * 使用 publishProgress，會觸發 onProgressUpdate 的運行
     */
    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        //更新进度条
        dialog.setMax(values[0]);
        dialog.setProgress(values[1]);
    }
}
