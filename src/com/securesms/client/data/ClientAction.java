package com.securesms.client.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 许德翔
 * Date: 2013-06-01
 * Time: 20:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientAction {
    private static Context context;

    public static String getPrikey(String telephone, String password) {
        String privatekey = "";
        String Url = "http://xdx3445.3322.org:8080/SecureSMS/register.action";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(Url);
        List<NameValuePair> pair = new ArrayList<NameValuePair>();
        try {
            JSONObject postjson = new JSONObject();
            postjson.put("telephone", telephone);
            postjson.put("password", password);
            pair.add(new BasicNameValuePair("info", postjson.toString()));
            post.setEntity(new UrlEncodedFormEntity(pair, HTTP.UTF_8));
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {//成功
                String responseStr = EntityUtils.toString(response.getEntity());//获取JSON字符串
                JSONObject json = new JSONObject(responseStr);//转换为JSON对象
                privatekey = json.getString("prikey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privatekey;
    }

    public static String getpublickey(String telephone){
        String pubkey="";
        String Url = "http://xdx3445.3322.org:8080/SecureSMS/getpubkey.action";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(Url);
        List<NameValuePair> pair = new ArrayList<NameValuePair>();
        try {
            JSONArray post_json = new JSONArray().put(telephone);
            pair.add(new BasicNameValuePair("tellist", post_json.toString()));
            post.setEntity(new UrlEncodedFormEntity(pair, HTTP.UTF_8));
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {//成功
                String responseStr = EntityUtils.toString(response.getEntity());//获取JSON字符串
                JSONArray jsonpubkey=new JSONArray(responseStr);
                pubkey=jsonpubkey.getJSONObject(0).getString(telephone);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubkey;
    }

    public static List getpublickey() {
        List pubkey=null;
        ArrayList<String> mContactsNumber = new ArrayList<String>();
        ContentResolver resolver = context.getContentResolver();         // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(1);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                mContactsNumber.add(phoneNumber);

            }
            phoneCursor.close();
        }
        String Url = "http://xdx3445.3322.org:8080/SecureSMS/getpubkey.action";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(Url);
        List<NameValuePair> pair = new ArrayList<NameValuePair>();
        try {
            JSONArray post_json = new JSONArray(mContactsNumber);
            pair.add(new BasicNameValuePair("tellist", post_json.toString()));
            post.setEntity(new UrlEncodedFormEntity(pair, HTTP.UTF_8));
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {//成功
                String responseStr = EntityUtils.toString(response.getEntity());//获取JSON字符串
                pubkey=getList(responseStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubkey;
    }

    public static Map<String, Object> getMap(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            @SuppressWarnings("unchecked")
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把json 转换为ArrayList 形式
     *
     * @return
     */

    public static List<Map<String, Object>> getList(String jsonString) {
        List<Map<String, Object>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(getMap(jsonObject.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
