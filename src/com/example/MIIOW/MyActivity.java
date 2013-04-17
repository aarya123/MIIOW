package com.example.MIIOW;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MyActivity extends Activity {

    ListView directoryListView;
    DirectoryAdapter a;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*ArrayList<String> test= GMailSender.getNameEmailDetails(this);
        Toast.makeText(this,""+test.size(),Toast.LENGTH_LONG).show();
        for(int i=0;i<test.size();i++)
            Toast.makeText(this,test.get(i),Toast.LENGTH_LONG).show();*/
        //new Login().execute();
        /*new DirectoryObjects("test","test",true);
        new DirectoryObjects("blah","blah",false);
        directoryListView= (ListView) findViewById(R.id.directory);
        a = new DirectoryAdapter(getApplicationContext(), R.id.directory, DirectoryObjects.dirObj);
        directoryListView.setAdapter(a);
        directoryListView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                    Event goingTo = a.getItem(position);
                    Intent eventInfo = new Intent(Tab1.this, EventInfo.class);
                    eventInfo.putExtra("eventId", goingTo.getId());
                    eventInfo.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Tab1.this.startActivity(eventInfo);
                    new UpdateInterest().execute("" + position);
            }
        });*/
        /*Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(Uri.parse("http://www.dha.com.tr/newpics/news/230620111356175716857.jpg"), "application/pdf");
        printIntent.putExtra("title", "title");
        startActivity(printIntent);*/
        //new SendEmail().execute();
        /*Intent next = new Intent(this, TweetToTwitterActivity.class);
        startActivity(next);*/
    }
    class SendEmail extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            try {
                GMailSender sender = new GMailSender("avengers.miiow@gmail.com", "kickinass");
                sender.sendMail("This is Subject", "This is Body", "avengers.miiow@gmail.com", "anubhaw.arya@gmail.com");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }
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
            return response ;
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