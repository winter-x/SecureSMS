package com.securesms.client.data;

import android.util.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: 许德翔
 * Date: 2013-05-30
 * Time: 10:54
 * To change this template use File | Settings | File Templates.
 */
public class EncryptionHelper {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }


    /**
     * 设置公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public void setPublicKey(String publicKey) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(publicKey.getBytes(), Base64.DEFAULT);
        //keyBytes = (new BASE64Decoder()).decodeBuffer(publicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(keySpec);
    }


    /**
     * 设置私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public void setPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(privateKey.getBytes(), Base64.DEFAULT);
        //keyBytes = (new BASE64Decoder()).decodeBuffer(privateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = keyFactory.generatePrivate(keySpec);
    }

    public EncryptionHelper() {
    }

    /**
     * 用种子初始化密钥
     *
     * @param keyInfo 种子
     * @throws java.security.NoSuchAlgorithmException
     *
     */
    public EncryptionHelper(String keyInfo) throws NoSuchAlgorithmException {
        init(keyInfo);
    }

    /**
     * 用种子初始化密钥
     *
     * @param keyInfo 种子
     * @throws java.security.NoSuchAlgorithmException
     *
     */
    public void init(String keyInfo) throws NoSuchAlgorithmException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = new SecureRandom();
        random.setSeed(keyInfo.getBytes());
        keygen.initialize(1024, random);// 初始加密，长度为1024，必须是大于512才可以的
        KeyPair kp = keygen.generateKeyPair();// 取得密钥对
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    /**
     * 加密字节数组
     *
     * @param plainBytes 明文
     * @return
     * @throws Exception
     */
    public byte[] encryption(byte[] plainBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainBytes);
    }

    /**
     * 解密字节数组
     *
     * @param cipherBytes 密文
     * @return
     * @throws Exception
     */
    public byte[] decryption(byte[] cipherBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherBytes);
    }

    /**
     * 加密字符串
     *
     * @param plainText 明文
     * @return
     * @throws Exception
     */
    public byte[] encryption(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes());
    }

    /**
     * 解密字符串
     *
     * @param cipherText 密文
     * @return
     * @throws Exception
     */
    public byte[] decryption(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(toBytes(cipherText));
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = new String(Base64.encode(keyBytes, Base64.DEFAULT), "UTF-8");
        //String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    public static String encryptBase64(byte[] data){
        String s="";
        try {
            s = new String(Base64.encode(data, Base64.DEFAULT), "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    public static byte[] decryptBase64(String data){
        byte[] s=null;
        try {
            s = Base64.decode(data.getBytes(), Base64.DEFAULT);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    /**
     * MD5加密字节数组
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    /**
     * MD5加密字符串
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptMD5(String data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data.getBytes());
        return toHexString(md5.digest());
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param b 要转换的字节数组
     * @return 十六进制字符串
     */
    public static String toHexString(byte[] b) {
        char[] HEXCHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
            sb.append(HEXCHAR[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转字节数组
     *
     * @param s 十六进制字符串
     * @return 字节数组
     */
    public static final byte[] toBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }
}
