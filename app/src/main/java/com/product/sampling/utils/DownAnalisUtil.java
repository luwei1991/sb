package com.product.sampling.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.product.sampling.Constants;
import com.product.sampling.MainApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 下载附件类
 * Created by 陆伟 on 2020/5/20.
 * Copyright (c) 2020 . All rights reserved.
 */


public class DownAnalisUtil {
    /**
     * 下载docx
     */
    public static void downLoadDocx(String id,String endWidth){
        File fileDir = new File(Constants.PDF_PATH);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        new DownLoadDocx().execute(Constants.BASE_URL+"base/tBaFile/downFile?id=" + id,Constants.PDF_PATH+"/"+id + endWidth);
    }



   static class DownLoadDocx extends AsyncTask<String,String,String> {

        @Override
        protected String  doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(f_url[1]);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());

            }
            return f_url[1];
        }


        @Override
        protected void onPostExecute(String s) {
            FileShareUtil.openFile(MainApplication.INSTANCE,s);
        }
    }
}
