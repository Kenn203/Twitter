 package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;

 public class ComposeActivity extends AppCompatActivity {

     public static final String TAG = "ComposeActivity";
     public static final int MAX_TWEET_LENGTH = 140;

    EditText etCompose;
    Button btnTweet;
    MenuItem miActionProgressItem;

    TwitterClient client;

     @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         miActionProgressItem = menu.findItem(R.id.miActionProgress);
         return super.onPrepareOptionsMenu(menu);
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.progress_bar,menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miActionProgressItem.setVisible(true);
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry tweet is empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says " + tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            miActionProgressItem.setVisible(false);
                            finish();
                        }catch (JSONException e){
                            e.printStackTrace();
                            miActionProgressItem.setVisible(false);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}