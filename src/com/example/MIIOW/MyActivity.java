package com.example.MIIOW;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
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

import java.util.ArrayList;
import java.util.Stack;

//TODO: Figure out connectivity problem, crashes if 4g isn't turned on
public class MyActivity extends Activity {

    ListView directoryListView;
    DirectoryAdapter a;
    Stack<String> listPathName = new Stack<String>(); //the path name of the currently displayed directory

    @Override
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
        Login login = new Login();
        login.execute("/");
        listPathName.push("/");

        directoryListView = (ListView) findViewById(R.id.directory);
        a = new DirectoryAdapter(getApplicationContext(), R.id.directory, DirectoryObject.getPeek());
        directoryListView.setAdapter(a);
        directoryListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DirectoryObject file = (DirectoryObject) parent.getItemAtPosition(position);
                if (file.isDir()) {
                    //if the path has already been cached use that instead
                    if (DirectoryObject.cache.containsKey(file.getPath())) {
                        ArrayList<DirectoryObject> newDir = DirectoryObject.cache.get(file.getPath());
                        DirectoryObject.dirStack.push(newDir);
                        listPathName.push(file.getPath());
                        a.clear();
                        a.addAll(DirectoryObject.getPeek());
                    } else {
                        new Login().execute(file.getPath()); //request server for contents of file
                        listPathName.push(file.getPath());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Not a directory.", Toast.LENGTH_SHORT).show();
                }
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
        });
        directoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //show a dialog to share
                final PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.inflate(R.menu.share_menu);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                //send to Anubhaw's shit
                                popup.dismiss();
                                //include progress dialog?
                                return true;
                            case R.id.cancel:
                                popup.dismiss();
                                return true;
                        }
                        return false;
                    }
                });
                //share
                return true;
            }
        });
    }

    public void onBackPressed() {
        if (DirectoryObject.dirStack.size() > 1) {
            ArrayList<DirectoryObject> oldDir = DirectoryObject.dirStack.pop();
            DirectoryObject.cache.put(listPathName.pop(), oldDir);
            a.clear();
            a.addAll(DirectoryObject.getPeek());
        }
    }

    class Login extends AsyncTask<String, Void, String> {
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

        protected void onPreExecute() {
            p = ProgressDialog.show(MyActivity.this, "Please Wait",
                    "Downloading data...", false);
        }

        protected String doInBackground(String... params) {
            //String homeDir = "/";
            String response;
            HttpGet http = new HttpGet(UTILITIES.API_URL + "/path/info" + params[0] + "?children=on&format=json");
            Log.i("VALUES", "Path: " + params[0]);
            String string = UTILITIES.API_URL + "/path/info" + params[0] + "?children=on&format=json";
            Log.i("VALUES", "Request String: " + string);
            HttpClient h = new DefaultHttpClient();
            HttpResponse r;
            try {
                String authInfo = UTILITIES.ACCOUNT_KEY + ":" + UTILITIES.ACCOUNT_SECRET;
                http.setHeader("Authorization", "Basic " + Base64.encodeToString(authInfo.getBytes(), Base64.NO_WRAP));
                r = h.execute(http);
                response = EntityUtils.toString(r.getEntity());
            } catch (Exception e1) {
                p = ProgressDialog.show(MyActivity.this, "Please Wait",
                        "Searching for connection...", false);
                //Toast.makeText(getApplicationContext(), "Failed to get data from server.", Toast.LENGTH_SHORT).show();
                return e1.getMessage();
            }
            //Log.i("VALUES", "Response: "+response.toString());
            return response;
            //return getFilesFromPath(homeDir);
        }

        public void onPostExecute(String result) {
            //DirectoryObject.dirObj=getList(result);
            DirectoryObject.dirStack.push(getList(result));
            a.clear();
            a.addAll(DirectoryObject.getPeek());
            p.setMessage("Finished!");
            p.dismiss();
        }

        /*
        public String getFilesFromPath(String path){
            String response;
            HttpGet http = new HttpGet(UTILITIES.API_URL+"/path/info" +path+ "?children=on&format=json");
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
                return e1.getMessage();
            }
            return response ;
        }*/
        public ArrayList<DirectoryObject> getList(String results) {
            ArrayList<DirectoryObject> homeList = new ArrayList<DirectoryObject>();
            //ArrayList<DirectoryObject> objList=null;
            try {
                Log.i("VALUES", "Inside try.");
                Log.i("VALUES", "Results: " + results.toString());
                JSONObject data = new JSONObject(results);
                Log.i("VALUES", "data from path " + data.toString(5));
                //Toast.makeText(getApplicationContext(),"Thru data.",Toast.LENGTH_SHORT).show();
                JSONArray jsonFileList = data.getJSONArray("children");
                //Log.i("VALUES", "children are "+jsonFileList.toString(5));
                //Toast.makeText(getApplicationContext(),"Thru jsonFileList.",Toast.LENGTH_SHORT).show();
                for (int i = 0; i < jsonFileList.length(); i++) {
                    JSONObject fileListing = jsonFileList.getJSONObject(i);
                    //Log.i("VALUES", "Child " +i+ " is "+jsonFileList.toString(5));
                    //Toast.makeText(getApplicationContext(),"Thru fileListing.",Toast.LENGTH_SHORT).show();
                    String name = fileListing.getString("name");
                    //Toast.makeText(getApplicationContext(),"Thru name.",Toast.LENGTH_SHORT).show();
                    String path = fileListing.getString("path");
                    //Toast.makeText(getApplicationContext(),"Thru path.",Toast.LENGTH_SHORT).show();
                    Boolean isDir = fileListing.getBoolean("isdir");
                    //Toast.makeText(getApplicationContext(),"Thru isDir.",Toast.LENGTH_SHORT).show();
                    //recursive mess
                  /*  if(isDir==false){
                        objList=null;
                    }
                    else{
                        objList = getList(getFilesFromPath(path)); //hopefully it will use right path
                    }*/
                    DirectoryObject file = new DirectoryObject(name, path, isDir);
                    homeList.add(file);
                    //JSONObject tempJsonObject = (JSONObject) jsonFileList.get(i);
                    //Toast.makeText(MyActivity.this,tempJsonObject.toString(),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("EMPTY ARRAY ERROR", "JSON not being correctly read from getList()");
            }
            return homeList;
        }
    }
}