/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.bluetoothchat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.ResultCallback;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
        ,BluetoothChatFragment.OnGameInteractionListener {
    Dialog dialog;
    public static final String TAG = "MainActivity";
    public String username="";
    Button sign_out_button,start_game;
    TextView score_txt;
    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    private GoogleApiClient mGoogleApiClient;
    private BluetoothChatFragment mChatFragment;
    String score;
    DataBaseHelper helper=new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("Username");




        Typeface tf=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        sign_out_button=(Button)findViewById(R.id.sign_out_button);
        sign_out_button.setTypeface(tf);
        sign_out_button.setOnClickListener(this);

        score_txt=(TextView)findViewById(R.id.score_txt);
        score_txt.setTypeface(tf);
        //find his score
        score=helper.returnScore(username);
        score_txt.setText(username + ",your score: " + score);
        //
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mChatFragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, mChatFragment);
            transaction.commit();
        }
        sign_out_button=(Button)findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        start_game=(Button)findViewById(R.id.start_button);
        start_game.setOnClickListener(this);
    }
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.sign_out_button:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Intent it = new Intent(MainActivity.this, LoginActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(it);
                                finish();

                            }
                        });
                break;
            case R.id.start_button:
                //Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show();
                mChatFragment.startNewGame(1);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Create a chain of targets that will receive log data */
    //@Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        
    }

    public void onGameWon(int selectedCase) {
        // TODO Game finished
        Log.d("Main // WINNER = ", "" + selectedCase);
    }
    @Override
    public void onBackPressed() {
        Intent it = new Intent(MainActivity.this, choose_game_activity.class);
        it.putExtra("Username",username);
        startActivity(it);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent it = new Intent(MainActivity.this, choose_game_activity.class);
            it.putExtra("Username",username);
            startActivity(it);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
