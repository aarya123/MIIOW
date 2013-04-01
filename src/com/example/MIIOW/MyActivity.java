package com.example.MIIOW;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class MyActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new Login().execute();
    }

    class Login extends AsyncTask<String, Void, String>
    {
        ProgressDialog p;

        protected void onPreExecute()
        {
            p = ProgressDialog.show(MyActivity.this, "Please Wait",
                    "Downloading data!", false);
        }

        protected String doInBackground(String... params)
        {
            String response = null;
            HttpGet http = new HttpGet(UTILITIES.API_URL+"/path/info?children=on&format=json");
            HttpClient h = new DefaultHttpClient();
            HttpResponse r = null;
            try
            {
                String authInfo = UTILITIES.ACCOUNT_KEY+":"+UTILITIES.ACCOUNT_SECRET;
                http.setHeader("Authorization", "Basic " + Base64.encodeToString(authInfo.getBytes(), Base64.NO_WRAP));
                r = h.execute(http);
                response = EntityUtils.toString(r.getEntity());
            } catch (Exception e1)
            {
                Log.d("ERROR", e1.getMessage());
                return e1.getMessage();
            }
            return response;
        }

        public void onPostExecute(String result)
        {
            p.setMessage("Finished!");
            p.dismiss();
            Log.d("Result",result);
            Toast.makeText(MyActivity.this,result,Toast.LENGTH_LONG).show();

        }
    }
}