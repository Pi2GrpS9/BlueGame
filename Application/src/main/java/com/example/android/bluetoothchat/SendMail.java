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

import android.os.AsyncTask;
import com.example.android.common.logger.Log;

/**
 * Created by alireza on 29/11/15.
 */
public class SendMail extends AsyncTask<String, Integer, Void> {
    @Override
    protected Void doInBackground(String... params) {
        Mail m = new Mail("ifootprintandroid@gmail.com", "alitedi88");
        String[] toArr = {params[0].toString()};
        m.setTo(toArr);
        m.setFrom("datefootprint@gmail.com");
        m.setSubject("Password recovery for BLUEGAME application");
        m.setBody("Hello, here is your password for your BLUEGAME application: "+params[1].toString());

        try {
            //m.addAttachment("/sdcard/filelocation");

            if(m.send()) {
                Log.d("sent", "sent");
            } else {
                Log.d("not sent","not sent");
            }
        } catch(Exception e) {
           Log.d("MailApp", "Could not send email", e);
        }
        return null;
    }

    protected void onPreExecute() {

    }
}
