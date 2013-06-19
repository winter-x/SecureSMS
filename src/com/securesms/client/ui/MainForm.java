package com.securesms.client.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.securesms.client.R;
import com.securesms.client.data.ClientAction;

import java.lang.reflect.Field;

public class MainForm extends Activity {

    private static final String PREF = "SecureSMS_PREF";
    private static final String PREF_TELEPHONE = "SecureSMS_Telephone";
    private static final String PREF_PASSWORD = "SecureSMS_Password";
    private static final String PREF_PRIKEY = "SecureSMS_Prikey";
    public static String telephone = "";
    public static String password = "";
    public static String prikey = "";

    private static TextView view_telephone;
    private static TextView view_passwd;
    private static TextView view_prikey;
    private static Intent intent=new Intent();

    private Handler handler =new Handler(){//定义Handler对象
        @Override
        public void handleMessage(Message msg){//当有消息发送出来的时候就执行Handler的这个方法
            super.handleMessage(msg);//处理UI
            startActivity(intent);
            //Toast.makeText(MainForm.this,"获取成功", Toast.LENGTH_SHORT).show();
            //view_prikey.setText(prikey.substring(0,20));
        }
    };


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        restorePrefs();
        view_telephone = (TextView) findViewById(R.id.telephone);
        view_passwd = (TextView) findViewById(R.id.passwd);
        view_prikey = (TextView) findViewById(R.id.prikey);
        if (prikey.equals("")){
            view_prikey.setText("无私钥");
        }
        else {
            view_prikey.setText(prikey.substring(0,50));
        }
        view_telephone.setText(telephone);
        view_passwd.setText(password);

        if (prikey.equals("")) {
            new Thread(){
                @Override
                public void run(){//你要执行的方法
                    while (true){
                        try {
                            wait(1);
                        }
                        catch (Exception e){}
                        if ((!telephone.equals(""))&&(!password.equals(""))){
                            break;
                        }
                    }
                    prikey = ClientAction.getPrikey(telephone, password);
                    //Toast.makeText(MainForm.this,"正在获取私钥", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(0);//执行完毕后给handler发送一个空消息
                }
            }.start();
        }
        else {

            startActivity(intent);
        }

        //ClientAction.getpublickey();
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        SharedPreferences settings = getSharedPreferences(PREF, 0);
        settings.edit()
                .putString(PREF_TELEPHONE, telephone)
                .putString(PREF_PASSWORD, password)
                .putString(PREF_PRIKEY, prikey)
                .commit();
    }


    private void restorePrefs() {
        intent.setClass(MainForm.this,Send.class);
        SharedPreferences settings = getSharedPreferences(PREF, 0);
        telephone = settings.getString(PREF_TELEPHONE, "");
        prikey = settings.getString(PREF_PRIKEY, "");
        if (telephone.equals("")) {//无法获取手机号码
            TelephonyManager phoneMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String pref_telephone = phoneMgr.getLine1Number();
            if (!pref_telephone.equals("")) {
                telephone = pref_telephone;
            } else {
                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);//只允许输入电话号码
                new AlertDialog.Builder(this)
                        .setTitle("请输入手机号码")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                telephone = editText.getText().toString();
                                if (telephone.equals("")) {
                                    Toast.makeText(getApplicationContext(), R.string.telephone_empty, Toast.LENGTH_SHORT).show();
                                    keepDialog(dialog);
                                } else {
                                    distoryDialog(dialog);
                                    getPassword();
                                }
                            }
                        })
                        .show();
            }
        }
        if ((!telephone.equals("")) && password.equals("")) {//能获取手机号但是获取不到密码
            getPassword();
        }
    }

    /**
     * 获取密码对话框
     */
    private void getPassword() {
        SharedPreferences settings = getSharedPreferences(PREF, 0);
        password = settings.getString(PREF_PASSWORD, "");
        if (password.equals("")) {
            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            new AlertDialog.Builder(this)
                    .setTitle("请输入密码")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(editText)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            password = editText.getText().toString();
                            if (password.equals("")) {
                                Toast.makeText(getApplicationContext(), R.string.password_empty, Toast.LENGTH_SHORT).show();
                                keepDialog(dialog);
                            } else {
                                distoryDialog(dialog);
                            }
                        }
                    })
                    .show();
        }
    }

    /**
     * 使对话框不关闭
     *
     * @param dialog
     */
    private void keepDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使对话框关闭
     *
     * @param dialog
     */
    private void distoryDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}