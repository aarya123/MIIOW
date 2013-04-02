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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    "Downloading data...", false);
        }

        protected String doInBackground(String... params)
        {
            String response;
            HttpGet http = new HttpGet(UTILITIES.API_URL+"/path/info?children=on&format=json");
            HttpClient h = new DefaultHttpClient();
            HttpResponse r;
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
            try {
                JSONObject data=new JSONObject(result);
                JSONArray jsonFileList=data.getJSONArray("children");
                Toast.makeText(MyActivity.this,jsonFileList.toString(),Toast.LENGTH_LONG).show();
                for(int i=0;i<jsonFileList.length();i++)
                {
                    JSONObject tempJsonObject = (JSONObject) jsonFileList.get(i);
                    Toast.makeText(MyActivity.this,tempJsonObject.toString(),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.d("ERROR",e.getMessage());
            }
            p.setMessage("Finished!");
            p.dismiss();
            Toast.makeText(MyActivity.this,result,Toast.LENGTH_LONG).show();
        }
    }
}