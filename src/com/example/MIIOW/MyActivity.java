package com.example.MIIOW;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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

/*
    The main activity of the application. Opened when application starts. This uses a list view to display
    the browser for files stored in the SmartFile account.
 */

public class MyActivity extends Activity {

    ListView directoryListView;
    DirectoryAdapter a; //controls which directory is displayed on the screen
    Stack<String> listPathName = new Stack<String>(); //the path name of the currently displayed directory
    DirectoryObject selectedFile; //file selected by long click

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Login login = new Login(); //async task which logs the user in and populates the screen with the desired directory
        login.execute("/"); //execute the async task


        listPathName.push("/");//keep track of current directory

        //sets up the listview which displays directory contents
        directoryListView = (ListView) findViewById(R.id.directory);
        a = new DirectoryAdapter(getApplicationContext(), R.id.directory, DirectoryObject.getPeek());
        directoryListView.setAdapter(a);

        //What to do when an item is clicked by user
        directoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DirectoryObject file = (DirectoryObject) parent.getItemAtPosition(position);
                //if the selected item is a directory load next screen
                if (file.isDir()) {
                    //if the path has already been cached use that instead of fetching from server
                    if (DirectoryObject.cache.containsKey(file.getPath())) {
                        //get directory from cache
                        ArrayList<DirectoryObject> newDir = DirectoryObject.cache.get(file.getPath());
                        //add directory from cache back onto stack
                        DirectoryObject.dirStack.push(newDir);
                        //keep track of directory name
                        listPathName.push(file.getPath());
                        a.clear(); //clear old screen contents
                        a.addAll(DirectoryObject.getPeek());//load new contents
                    } else {
                        //request server for contents of directory
                        new Login().execute(file.getPath());
                        listPathName.push(file.getPath()); //keep track of directory name
                    }
                    //if selected item is a file allow user to share it
                } else {
                    selectedFile = (DirectoryObject) parent.getItemAtPosition(position);

                    //Show the share menu
                    PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                    popup.inflate(R.menu.share_menu);
                    popup.show();

                    //when a share menu item is selected
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.share:
                                    Intent propagate = new Intent(MyActivity.this, Propagate.class);
                                    propagate.putExtra("onlineFilePath", selectedFile.getPath());
                                    propagate.putExtra("fileName", selectedFile.getName());
                                    //open propagate activity which will propagate the file to selected social media
                                    startActivity(propagate);
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
        if (DirectoryObject.dirStack.size() > 1) { //if we are not at the home directory
            ArrayList<DirectoryObject> oldDir = DirectoryObject.dirStack.pop(); //retrieve old directory
            DirectoryObject.cache.put(listPathName.pop(), oldDir); //cache it
            a.clear(); //clear old contents
            a.addAll(DirectoryObject.getPeek()); //load old directory onto screen
        }
    }

    /*
        Async Task which will log the user in on a separate thread and retrieve the contents of the selected
        directory. Currently the user needs to be logged in again every time a new directory is loaded from
        the server. This isn't optimal and in the future we'd like to be able to separate this functionality.

        @param directory path
        @action loads the contents of the given directory from the SmartFile server onto the screen
     */
    class Login extends AsyncTask<String, Void, String> {
        ProgressDialog p = new ProgressDialog(MyActivity.this);

        protected void onPreExecute() {
            if (UTILITIES.isOnline(MyActivity.this)) {
                //Shows a message while the application retrieves the data
                p = ProgressDialog.show(MyActivity.this, "Please Wait",
                        "Downloading data...", false);
            }
            /* Display a dialog to allow user to retry the connection*/
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                builder.setTitle("Internet connection not found.")
                        .setMessage("Please check your internet connection and try again.");
                //add dialog buttons
                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user clicked retry
                        //try to login again
                        new Login().execute("/");
                        cancel(true); //end the task
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user clicked cancel
                        dialog.dismiss(); //close the dialog
                        MyActivity.this.finish();//close the Activity
                        cancel(true);//end the task
                    }
                });
                //create the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        protected String doInBackground(String... params) {
            String response;
            //set up the api call
            HttpGet http = new HttpGet(UTILITIES.API_URL + "/path/info" + params[0] + "?children=on&format=json");
            HttpClient h = new DefaultHttpClient();
            HttpResponse r;
            try {
                String authInfo = UTILITIES.ACCOUNT_KEY + ":" + UTILITIES.ACCOUNT_SECRET;
                http.setHeader("Authorization", "Basic " + Base64.encodeToString(authInfo.getBytes(), Base64.NO_WRAP));
                r = h.execute(http); //execute the api call
                response = EntityUtils.toString(r.getEntity());
            } catch (Exception e1) {
                return e1.getMessage();
            }
            return response;
        }

        public void onPostExecute(String result) {
            DirectoryObject.dirStack.push(getList(result)); //add loaded directory to stack
            a.clear(); //clear old contents
            a.addAll(DirectoryObject.getPeek()); //load new contents
            p.setMessage("Finished!");
            p.dismiss();
        }

        /*
            Takes in the returned string from the api call and parses it into a list of Directory Objects
            which represent the current directory

            @param the result string from the api call
         */
        public ArrayList<DirectoryObject> getList(String results) {
            ArrayList<DirectoryObject> homeList = new ArrayList<DirectoryObject>();
            try {
                JSONObject data = new JSONObject(results);
                //get the list of files in this directory
                JSONArray jsonFileList = data.getJSONArray("children");
                //parse each file into a Directory Object
                for (int i = 0; i < jsonFileList.length(); i++) {
                    JSONObject fileListing = jsonFileList.getJSONObject(i);
                    String name = fileListing.getString("name");
                    String path = fileListing.getString("path");
                    Boolean isDir = fileListing.getBoolean("isdir");
                    String url = fileListing.getString("url");
                    DirectoryObject file = new DirectoryObject(name, path, isDir, url);
                    homeList.add(file);//add files into a list which represents the directory
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return homeList;
        }
    }


}