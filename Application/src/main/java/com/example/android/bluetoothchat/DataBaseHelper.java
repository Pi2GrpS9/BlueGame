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

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.widget.Toast;

/**
 * Created by alireza on 06/11/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="contacts.db";
    private static final String TABLE_NAME="contacts";
    private static final String COLUMN_ID="id";
    private static final String COLUMN_NAME="name";
    private static final String COLUMN_EMAIL="email";
    private static final String COLUMN_USERNAME="username";
    private static final String COLUMN_PASS="password";
    private static final String COLUMN_SCORE="score";
    SQLiteDatabase db;
    private static final String TABLE_CREATE="create table contacts(id integer primary key not null,"+
            "name text not null, email text not null, username text not null, password text not null,score text )";

    public DataBaseHelper(Context context){
        super(context , DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db=db;
    }

    public void insertContact(Contacts c){
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        String query="select * from contacts";
        Cursor cursor=db.rawQuery(query,null);
        int count=cursor.getCount();
        values.put(COLUMN_ID, count);
        values.put(COLUMN_NAME,c.getName());
        values.put(COLUMN_EMAIL, c.getEmail());
        values.put(COLUMN_PASS, c.getPassword());
        values.put(COLUMN_USERNAME, c.getUsername());
        values.put(COLUMN_SCORE, c.getScore());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int existanceOfContacts(String email){
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        String query="SELECT COUNT(*) FROM CONTACTS WHERE email = '"+email+"'";
        Cursor cursor=db.rawQuery(query, null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        db.close();
        /*if (count==0)
            return false;
        else{
            return true;
        }*/
        return count;
    }
    public void updateScore(String username,String score ){
        db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", score);
        db.update(TABLE_NAME, values,  "username ='"+username+"'", null);
    }

    public String searchPass(String uname){
        db=this.getReadableDatabase();
        String query="select username, password from "+TABLE_NAME;
        Cursor cursor =db.rawQuery(query,null);
        String a, b;
        b="not found";
        if(cursor.moveToFirst()){
            do{
                a=cursor.getString(0);
                if(a.equals(uname)){
                    b=cursor.getString(1);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        return b;
    }
    public String passForget(String email){
        db=this.getReadableDatabase();
        String query="select password from "+TABLE_NAME + " where email = '"+email+"'";
        Cursor cursor =db.rawQuery(query,null);
        String  b;
        b="not found";
        if(cursor.moveToFirst()){
            do{
                b=cursor.getString(0);
                break;
            }
            while(cursor.moveToNext());
        }
        return b;
    }
    public String returnScore(String username){
        db=this.getReadableDatabase();
        String query="select score from "+TABLE_NAME + " where username = '"+username+"'";
        Cursor cursor =db.rawQuery(query,null);
        String  b;
        b="0";
        if(cursor.moveToFirst()){
            do{
                b=cursor.getString(0);
                break;
            }
            while(cursor.moveToNext());
        }
        return b;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
}
