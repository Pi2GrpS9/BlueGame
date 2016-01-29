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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity implements View.OnClickListener {

    EditText name,username,password, confirmpassword, email;
    String nametxt,usernametxt,passwordtxt, confirmpasswordtxt, emailtxt;
    DataBaseHelper helper=new DataBaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button confirmbtn=(Button)findViewById(R.id.Confirmbtn);
        confirmbtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        name=(EditText)findViewById(R.id.nameSU);
        nametxt=name.getText().toString();
        username=(EditText)findViewById(R.id.usernameSU);
        usernametxt=username.getText().toString();
        password=(EditText)findViewById(R.id.passwordSU);
        passwordtxt=password.getText().toString();
        email=(EditText)findViewById(R.id.emailSU);
        emailtxt=email.getText().toString();
        confirmpassword=(EditText)findViewById(R.id.password2SU);
        confirmpasswordtxt=confirmpassword.getText().toString();
        if(v.getId()==R.id.Confirmbtn){
            if(!passwordtxt.equals(confirmpasswordtxt)){
                Toast pass=Toast.makeText(SignUp.this, "Passwords not match", Toast.LENGTH_LONG);
                pass.show();
            }
            else if(passwordtxt==""|| emailtxt=="" || nametxt==""||usernametxt=="" || confirmpasswordtxt=="")
            {
                Toast pass=Toast.makeText(SignUp.this, "Fill completely the form application :)", Toast.LENGTH_LONG);
                pass.show();
            }
            else{
                //insert it to database
                Contacts c=new Contacts();
                c.setEmail(emailtxt);
                c.setName(nametxt);
                c.setPassword(passwordtxt);
                c.setUsername(usernametxt);
                helper.insertContact(c);
                Toast pass=Toast.makeText(SignUp.this, "Your account is correctly added to system. ", Toast.LENGTH_SHORT);
                pass.show();
                Intent i = new Intent(SignUp.this, LoginActivity.class);
                startActivity(i);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

