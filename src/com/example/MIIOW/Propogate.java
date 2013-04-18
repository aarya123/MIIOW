package com.example.MIIOW;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;

public class Propogate extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.propogate);
    }


    public void print(String path) {
        Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(Uri.parse(path), "application/pdf");
        printIntent.putExtra("title", "title");
        startActivity(printIntent);
    }

    public void sendTexts(ArrayList<String> numbers, String message) {
        UTILITIES.sendSMS(numbers, message);
    }

    class SendEmail extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            if (UTILITIES.isOnline(Propogate.this)) {
                try {
                    GMailSender sender = new GMailSender("avengers.miiow@gmail.com", "kickinass");
                    if (strings.length == 1)
                        sender.sendMail("MIIOW!", strings[0], "avengers.miiow@gmail.com", "anubhaw.arya@gmail.com");
                    else {
                        String emails = UTILITIES.getEmails(Propogate.this);
                        sender.sendMail("MIIOW!", strings[0], emails, "anubhaw.arya@gmail.com");
                    }
                } catch (Exception e) {
                    Log.e("MIIOW", e.getMessage(), e);
                }
            }
            return null;
        }
    }

    class Tweet extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... strings) {
            if (UTILITIES.isOnline(Propogate.this)) {
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
    }
}