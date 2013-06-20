package com.securesms.client.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.securesms.client.R;
import com.securesms.client.data.ClientAction;
import com.securesms.client.data.EncryptionHelper;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: 许德翔
 * Date: 2013-06-13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
public class Send extends Activity {

    private static EditText txtMessage;
    private static EditText txtPhone;
    private static Button btnsend;
    private static String ciphertxt;
    //private static String plain;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        action();
    }

    private Handler handler = new Handler() {//定义Handler对象
        @Override
        public void handleMessage(Message msg) {//当有消息发送出来的时候就执行Handler的这个方法
            super.handleMessage(msg);//处理UI
            //Toast.makeText(Send.this, "发送成功", Toast.LENGTH_SHORT).show();
            txtMessage.setText(ciphertxt);
            //view_prikey.setText(prikey.substring(0,20));
        }
    };

    private void action() {
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtPhone=(EditText)findViewById(R.id.txtPhone);
        btnsend = (Button) findViewById(R.id.btnsend);

        String action="com.securesms.client.receiver";
        sendReceiver receiver=new sendReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(action);
        registerReceiver(receiver,filter);
        Intent intent = new Intent(action);
        final PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            EncryptionHelper encryption = new EncryptionHelper();
                            encryption.setPublicKey(ClientAction.getpublickey(txtPhone.getText().toString()));//设置接收方公钥
                            byte[] temp=encryption.encryption(txtMessage.getText().toString());//加密短信
                            ciphertxt=EncryptionHelper.encryptBase64(temp);//对加密内容进行编码

                            SmsManager smsManager = SmsManager.getDefault();
                            ArrayList<String> sendArray = smsManager.divideMessage(ciphertxt);//拆分短信
                            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                            for (int i = 0; i < sendArray.size(); i++){
                                sentIntents.add(pi);
                            }
                            smsManager.sendMultipartTextMessage(txtPhone.getText().toString(), null, sendArray, sentIntents, null);
//                            List<String> divideContents = smsManager.divideMessage(ciphertxt);
//                            for (String text : divideContents) {
//                                smsManager.sendTextMessage(txtPhone.getText().toString(), null, text, pi, null);
//                            }
                            handler.sendEmptyMessage(0);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Send.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.start();
            }
        });



//        final Handler decryption=new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);    //To change body of overridden methods use File | Settings | File Templates.
//                Toast.makeText(Send.this, "解密成功", Toast.LENGTH_SHORT).show();
//                txtMessage.setText(plain);
//            }
//        };
//
//        Button btntemp=(Button)findViewById(R.id.btntemp);
//        btntemp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(){
//                    @Override
//                    public void run() {
//                        try {
//                            EncryptionHelper encryption = new EncryptionHelper();
//                            encryption.setPrivateKey(MainForm.prikey);
//                            plain=new String(encryption.decryption(EncryptionHelper.decryptBase64(txtMessage.getText().toString())));
//                            decryption.sendEmptyMessage(0);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
//        });

    }

    class sendReceiver extends BroadcastReceiver {
        //写个接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int resultCode = getResultCode();
            if(resultCode==Activity.RESULT_OK){
                Toast.makeText(Send.this, "发送成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Send.this, "发送失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
