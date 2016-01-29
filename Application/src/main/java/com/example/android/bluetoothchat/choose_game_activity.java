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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class choose_game_activity extends Activity implements View.OnClickListener {

    Button blue_choose, wifi_choose;
    public String username="";
    public TextView txt_two, txt_multi,title;
    ImageView im1,im2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game_activity);
        Typeface tf=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        Typeface tf2=Typeface.createFromAsset(getAssets(),"fonts/abel.ttf");
        im1=(ImageView) findViewById(R.id.wifi_btn_game);
        Glide.with(this)
                .load(R.drawable.connect_four)
                .crossFade()
                .into(im1);
        im1.setOnClickListener(this);
        im2=(ImageView) findViewById(R.id.blue_btn_game);
        Glide.with(this)
                .load(R.drawable.tic)
                .crossFade()
                .into(im2);
        im2.setOnClickListener(this);
        username = getIntent().getStringExtra("Username");
        txt_two=(TextView)findViewById(R.id.txt_two_player);
        txt_two.setText("TIC TAC TOE:\nShort time game\nThis game is based for two players and for a limit time");
        txt_two.setGravity(Gravity.CENTER);
        txt_multi=(TextView)findViewById(R.id.txt_multi_player);
        txt_multi.setText("Connect four:\nLong time game\n This game is based for multi-players and it takes time for playing");
        txt_multi.setGravity(Gravity.CENTER);
        txt_multi.setTypeface(tf2); txt_two.setTypeface(tf2);
        //blue_choose=(Button)findViewById(R.id.blue_btn_game);
       // wifi_choose=(Button)findViewById(R.id.wifi_btn_game);
       // blue_choose.setOnClickListener(this);
       // wifi_choose.setOnClickListener(this);
        title= (TextView)findViewById(R.id.textView4);

        title.setTypeface(tf);
        title.setText("Welcome "+username+"\nPlease choose your game: ");
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.blue_btn_game){
            Intent i = new Intent(choose_game_activity.this, MainActivity.class);
            i.putExtra("Username",username);
            startActivity(i);
        }
        else{
            Toast pass=Toast.makeText(choose_game_activity.this, "UNDER MAINTENANCE", Toast.LENGTH_LONG);
            pass.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_game_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
