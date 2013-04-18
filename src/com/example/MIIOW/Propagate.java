package com.example.MIIOW;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import java.util.ArrayList;
import java.util.List;

public class Propagate extends Activity {
    CheckBox emailCB, tweetCB, textCB, printCB, allEmailCB, allTextCB;
    View submit;
    EditText emailAddy, phoneNum;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.propagate);
        emailAddy = (EditText) findViewById(R.id.emailAddy);
        phoneNum = (EditText) findViewById(R.id.phoneNum);
        emailCB = (CheckBox) findViewById(R.id.emailCB);
        tweetCB = (CheckBox) findViewById(R.id.tweetCB);
        textCB = (CheckBox) findViewById(R.id.textCB);
        printCB = (CheckBox) findViewById(R.id.printCB);
        allEmailCB = (CheckBox) findViewById(R.id.allEmailCB);
        allTextCB = (CheckBox) findViewById(R.id.allTextCB);
        submit = findViewById(R.id.submit);
        emailCB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (emailCB.isChecked()){
                    allEmailCB.setVisibility(View.VISIBLE);
                    emailAddy.setVisibility(View.VISIBLE);
                }
                else {
                    allEmailCB.setVisibility(View.INVISIBLE);
                    allEmailCB.setChecked(false);
                    emailAddy.setVisibility(View.GONE);
                    emailAddy.setText("");
                }
            }
        });
        allEmailCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allEmailCB.isChecked())
                {
                    emailAddy.setVisibility(View.GONE);
                    emailAddy.setText("");
                }
                else
                {
                    emailAddy.setVisibility(View.VISIBLE);
                }
            }
        });
        textCB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (textCB.isChecked()){
                    allTextCB.setVisibility(View.VISIBLE);
                    phoneNum.setVisibility(View.VISIBLE);
                }
                else {
                    allTextCB.setVisibility(View.INVISIBLE);
                    allTextCB.setChecked(false);
                    phoneNum.setVisibility(View.GONE);
                    phoneNum.setText("");
                }
            }
        });
        allTextCB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(allTextCB.isChecked())
                {
                    phoneNum.setVisibility(View.GONE);
                    phoneNum.setText("");
                }
                else
                {
                    phoneNum.setVisibility(View.VISIBLE);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GetExchangeLink().execute("/Resume.pdf");
            }
        });

    }


    public void print(String path) {
        Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(Uri.parse(path), "application/pdf");
        printIntent.putExtra("MIIOW", "MIIOW");
        startActivity(printIntent);
    }

    public void sendTexts(ArrayList<String> numbers, String message) {
        UTILITIES.sendSMS(numbers, message);
    }

    class SendEmail extends AsyncTask<String, Void, String> {
        ProgressDialog p;

        protected void onPreExecute() {
            p = ProgressDialog.show(Propagate.this, "Please Wait",
                    "Emailing the file...", false);
        }
        protected String doInBackground(String... strings) {
            if (UTILITIES.isOnline(Propagate.this)) {
                try {
                    GMailSender sender = new GMailSender("avengers.miiow@gmail.com", "kickinass");
                    Log.d("MIIOW",strings[0]);
                    if (!strings[1].equals("asdfasdf"))
                        sender.sendMail("MIIOW!", strings[0], "avengers.miiow@gmail.com", strings[1]);
                    else {
                        String emails = UTILITIES.getEmails(Propagate.this);
                        sender.sendMail("MIIOW!", strings[0], "avengers.miiow@gmail.com", emails);
                    }
                } catch (Exception e) {
                    Log.e("MIIOW", e.getMessage(), e);
                }
            }
            return null;
        }

        public void onPostExecute(String result) {
            p.setMessage("Finished!");
            p.dismiss();
        }
    }

    class Tweet extends AsyncTask<String, Void, String> {
        ProgressDialog p;

        protected void onPreExecute() {
            p = ProgressDialog.show(Propagate.this, "Please Wait",
                    "Tweeting the file...", false);
        }
        protected String doInBackground(String... strings) {
            if (UTILITIES.isOnline(Propagate.this)) {
                OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(UTILITIES.TWITTER_CONSUMER_KEY).apiSecret(UTILITIES.TWITTER_CONSUMER_SECRET).build();
                Token accessToken = new Token(UTILITIES.TWITTER_ACCESS_TOKEN, UTILITIES.TWITTER_ACCESS_TOKEN_SECRET);
                OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/statuses/update.json");
                request.addBodyParameter("status", strings[0]);
                service.signRequest(accessToken, request);
                Response response = request.send();
                return response.getBody();
            }
            return null;
        }

        public void onPostExecute(String result) {
            p.setMessage("Finished!");
            p.dismiss();
        }
    }

    class GetExchangeLink extends AsyncTask<String, Void, String> {
        ProgressDialog p;

        protected void onPreExecute() {
            p = ProgressDialog.show(Propagate.this, "Please Wait",
                    "Propagating the file...", false);
        }

        protected String doInBackground(String... params) {
            if (UTILITIES.isOnline(Propagate.this)) {
                try {
                    String response;
                    HttpPost post = new HttpPost(UTILITIES.API_URL + "/path/exchange/");
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("path",params[0]));
                    nameValuePairs.add(new BasicNameValuePair("mode","r"));
                    nameValuePairs.add(new BasicNameValuePair("expires","9999999999999999999999999999999"));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse r;
                    String authInfo = UTILITIES.ACCOUNT_KEY + ":" + UTILITIES.ACCOUNT_SECRET;
                    post.setHeader("Authorization", "Basic " + Base64.encodeToString(authInfo.getBytes(), Base64.NO_WRAP));
                    r = client.execute(post);
                    response = EntityUtils.toString(r.getEntity());
                    return response;
                } catch (Exception e) {
                    Log.d("MIIOW", e.getMessage());
                }

            }
            return null;
        }

        public void onPostExecute(String result) {
            try {
                JSONObject jsonURL= new JSONObject(result);
                String link=jsonURL.getString("url");
                p.setMessage("Finished!");
                p.dismiss();
                Log.d("MIIOW",""+link);
                if(emailCB.isChecked()){
                    if(allEmailCB.isChecked())
                        new SendEmail().execute(link,"asdfasdf");
                    else
                        new SendEmail().execute(link,emailAddy.getText().toString());
                }
                if(tweetCB.isChecked())
                    new Tweet().execute(link);
                if(textCB.isChecked())
                {
                    if(allTextCB.isChecked())
                        sendTexts(UTILITIES.getPhoneNumbers(Propagate.this),link);
                    else
                    {
                        ArrayList<String> number= new ArrayList<String>();
                        number.add(phoneNum.getText().toString());
                        sendTexts(number,link);
                    }
                }
                if(printCB.isChecked())
                {
                    print("path");
                }
            } catch (JSONException e) {
                Log.d("MIIOW",e.getMessage());
            }
        }

    }
}