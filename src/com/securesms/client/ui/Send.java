package com.securesms.client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.securesms.client.R;
import com.securesms.client.data.ClientAction;
import com.securesms.client.data.EncryptionHelper;

/**
 * Created with IntelliJ IDEA.
 * User: 许德翔
 * Date: 2013-06-13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
public class Send extends Activity {

    private static EditText txtMessage;
    private static Button btnsend;
    private static String ciphertxt;
    private static String plain;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        action();
    }

    private Handler handler = new Handler() {//定义Handler对象
        @Override
        public void handleMessage(Message msg) {//当有消息发送出来的时候就执行Handler的这个方法
            super.handleMessage(msg);//处理UI
            Toast.makeText(Send.this, "加密成功", Toast.LENGTH_SHORT).show();
            txtMessage.setText(ciphertxt);
            //view_prikey.setText(prikey.substring(0,20));
        }
    };

    private void action() {
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        btnsend = (Button) findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            EncryptionHelper encryption = new EncryptionHelper();
                            encryption.setPublicKey(ClientAction.getpublickey(MainForm.telephone));
                            byte[] temp=encryption.encryption(txtMessage.getText().toString());
                            ciphertxt=EncryptionHelper.encryptBase64(temp);
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });



        final Handler decryption=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);    //To change body of overridden methods use File | Settings | File Templates.
                Toast.makeText(Send.this, "解密成功", Toast.LENGTH_SHORT).show();
                txtMessage.setText(plain);
            }
        };

        Button btntemp=(Button)findViewById(R.id.btntemp);
        btntemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            EncryptionHelper encryption = new EncryptionHelper();
                            encryption.setPrivateKey(MainForm.prikey);
                            plain=new String(encryption.decryption(EncryptionHelper.decryptBase64(txtMessage.getText().toString())));
                            decryption.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

    }



}
