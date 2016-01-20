package com.example.android.bluetoothchat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class choose_game_activity extends Activity implements View.OnClickListener {

    Button blue_choose, wifi_choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game_activity);
        blue_choose=(Button)findViewById(R.id.blue_btn_game);
        wifi_choose=(Button)findViewById(R.id.wifi_btn_game);
        blue_choose.setOnClickListener(this);
        wifi_choose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.blue_btn_game){
            Intent i = new Intent(choose_game_activity.this, MainActivity.class);
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
