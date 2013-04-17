package com.example.MIIOW;

import android.os.AsyncTask;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

public class TweetToTwitterActivity extends Activity {

    private static final String ACCESS_TOKEN = "1348177470-n4VCuo43YD8NFZTO2AgviBzm95Dgd4xom5IY4LS";
    private static final String ACCESS_TOKEN_SECRET = "PJ6FKAXHZGedaFhsLozF4bcMqm4RKozJoBx6iRFDayI";
    private static final String CONSUMER_KEY = "UJaAlQAjwN19yATvN5t3A";
    private static final String CONSUMER_SECRET = "wxn6QvE44Mja9gB6nL7G02HN816196lRZVsk5uebcws";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweeter);
        tweetMessage(null);
    }

    public void tweetMessage(View v) {
        new Tweet().execute();
    }

    class Tweet extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... strings) {
            OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
            Token accessToken = new Token(ACCESS_TOKEN,ACCESS_TOKEN_SECRET);
            // Now let's go and ask for a protected resource!
            OAuthRequest request = new OAuthRequest(Verb.POST,"https://api.twitter.com/1.1/statuses/update.json");
            request.addBodyParameter("status", strings[0]);
            service.signRequest(accessToken, request);
            Response response = request.send();
            return response.getBody();
        }

        public void onPostExecute(String result) {
            Toast.makeText(TweetToTwitterActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}