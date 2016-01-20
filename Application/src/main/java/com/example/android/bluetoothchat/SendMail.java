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
