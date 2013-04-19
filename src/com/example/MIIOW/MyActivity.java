package com.example.MIIOW;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Stack;

public class MyActivity extends Activity {

    ListView directoryListView;
    DirectoryAdapter a;
    Stack<String> listPathName = new Stack<String>(); //the path name of the currently displayed directory
    DirectoryObject selectedFile; //not the best way to do this

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Login login = new Login();
        login.execute("/");
        listPathName.push("/");
        directoryListView = (ListView) findViewById(R.id.directory);
        a = new DirectoryAdapter(getApplicationContext(), R.id.directory, DirectoryObject.getPeek());
        directoryListView.setAdapter(a);
        directoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    selectedFile = (DirectoryObject) parent.getItemAtPosition(position);
                    PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                    popup.inflate(R.menu.share_menu);
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.share:
                                    //send to Anubhaw's shit - send to intent called Propagate
                                    Intent propagate = new Intent(MyActivity.this, Propagate.class);
                                    propagate.putExtra("onlineFilePath", selectedFile.getPath());
                                    propagate.putExtra("fileName", selectedFile.getName());
                                    startActivity(propagate);
                                    //include progress dialog?
                                    return true;
                                case R.id.cancel:
                                    return true;
                            }
                            return false;
                        }
                    });
                }
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
        ProgressDialog p;

        protected void onPreExecute() {
            p = ProgressDialog.show(MyActivity.this, "Please Wait",
                    "Downloading data...", false);
        }

        protected String doInBackground(String... params) {
            //String homeDir = "/";
            String response;
            HttpGet http = new HttpGet(UTILITIES.API_URL + "/path/info" + params[0] + "?children=on&format=json");
            HttpClient h = new DefaultHttpClient();
            HttpResponse r;
            try {
                String authInfo = UTILITIES.ACCOUNT_KEY + ":" + UTILITIES.ACCOUNT_SECRET;
                http.setHeader("Authorization", "Basic " + Base64.encodeToString(authInfo.getBytes(), Base64.NO_WRAP));
                r = h.execute(http);
                response = EntityUtils.toString(r.getEntity());
            } catch (Exception e1) {
                //figure this out...attempt to reconnect
                p = ProgressDialog.show(MyActivity.this, "Please Wait",
                        "Searching for connection...", false);
                //Toast.makeText(getApplicationContext(), "Failed to get data from server.", Toast.LENGTH_SHORT).show();
                return e1.getMessage();
            }
            return response;
        }

        public void onPostExecute(String result) {
            //DirectoryObject.dirObj=getList(result);
            DirectoryObject.dirStack.push(getList(result));
            a.clear();
            a.addAll(DirectoryObject.getPeek());
            p.setMessage("Finished!");
            p.dismiss();
        }

        public ArrayList<DirectoryObject> getList(String results) {
            ArrayList<DirectoryObject> homeList = new ArrayList<DirectoryObject>();
            try {
                Log.i("VALUES", "Inside try.");
                Log.i("VALUES", "Results: " + results);
                JSONObject data = new JSONObject(results);
                Log.i("VALUES", "data from path " + data.toString(5));
                JSONArray jsonFileList = data.getJSONArray("children");
                for (int i = 0; i < jsonFileList.length(); i++) {
                    JSONObject fileListing = jsonFileList.getJSONObject(i);
                    String name = fileListing.getString("name");
                    String path = fileListing.getString("path");
                    Boolean isDir = fileListing.getBoolean("isdir");
                    String url = fileListing.getString("url");
                    DirectoryObject file = new DirectoryObject(name, path, isDir, url);
                    homeList.add(file);
                }
            } catch (JSONException e) {
                Log.e("EMPTY ARRAY ERROR", "JSON not being correctly read from getList()");
            }
            return homeList;
        }
    }


}