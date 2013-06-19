package com.securesms.client.ui;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 许德翔
 * Date: 2013-06-09
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class ConversationList extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getConversationList(){
        String[] projection = new String[] { "thread_id as _id", "thread_id", "msg_count", "snippet",
                "sms.address as address", "sms.date as date" };
        Uri uri = Uri.parse("content://sms/conversations");
        Cursor cursor = managedQuery(uri, projection, null, null, "sms.date desc");
    }

    public List<Map<String, String>> readSmsSessionList() {

        List<Map<String, String>> sList = new ArrayList<Map<String,String>>();
        int ID = 0;
        int MESSAGE_COUNT = 1;
        String[] THREADS_PROJECTION = { "_id", "message_count", "date" };
        Uri MMSSMS_FULL_CONVERSATION_URI = Uri
                .parse("content://mms-sms/conversations");
        Uri CONVERSATION_URI = MMSSMS_FULL_CONVERSATION_URI.buildUpon()
                .appendQueryParameter("simple", "true").build();
        Cursor cursor = getContentResolver().query(CONVERSATION_URI,
                THREADS_PROJECTION, null, null, "date desc");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(ID);
                long msgCount = cursor.getLong(MESSAGE_COUNT);
                Map<String,String> map = new HashMap<String, String>();
                map.put("thread_id", Long.toString(id));
                map.put("msgCount",Long.toString(msgCount));
                //Log.i(TAG, "thread_Id" + id + "  " + msgCount);
                sList.add(map);
            } while (cursor.moveToNext());
        }
        return sList;
    }
}