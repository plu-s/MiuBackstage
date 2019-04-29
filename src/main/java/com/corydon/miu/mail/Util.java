package com.corydon.miu.mail;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Util {
    /**
     * 以指定编码来读取文件。
     * @param fileName 文件名
     * @param encoding 文本编码，默认utf-8
     * @return 文件的内容
     */
    public static String readTextFile(String fileName,String encoding){
        if(encoding==null){
            encoding="UTF-8";
        }
        File file=new File(fileName);
        Long length=file.length();
        byte[] content=new byte[length.intValue()];
        try{
            FileInputStream is=new FileInputStream(file);
            is.read(content);
            is.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            return new String(content,encoding);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用AES加密内容
     * @param content
     * @param password
     * @return
     */
    public static byte[] encryptByAES(String content,String password){
        byte[] result=null;
        try{
            KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
            keyGenerator.init(128,new SecureRandom(password.getBytes()));
            SecretKey rawKey=keyGenerator.generateKey();
            byte[] rawKeyByte=rawKey.getEncoded();
            SecretKeySpec key=new SecretKeySpec(rawKeyByte,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,key);
            result=cipher.doFinal(content.getBytes("utf-8"));
        }catch(NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | UnsupportedEncodingException
                | BadPaddingException
                | IllegalBlockSizeException e){
            e.printStackTrace();
        }
        return result;
    }
    public static String decryptByAES(byte[] content,String password){
        String result=null;
        try{
            byte[] b;
            KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
            keyGenerator.init(128,new SecureRandom(password.getBytes()));
            SecretKey rawKey=keyGenerator.generateKey();
            byte[] rawKeyByte=rawKey.getEncoded();
            SecretKeySpec key=new SecretKeySpec(rawKeyByte,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,key);
            b=cipher.doFinal(content);
            result=new String(b,"utf-8");
        }catch(NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | UnsupportedEncodingException
                | BadPaddingException
                | IllegalBlockSizeException e){
            e.printStackTrace();
        }
        return result;
    }
    public static String byte2hexStr(byte[] buf){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<buf.length;i++){
            String hex=Integer.toHexString(buf[i]&0xFF);
            if(hex.length()==1){
                hex="0"+hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    public static byte[] hexStr2Byte(String hexStr){
        if(hexStr.length()<1){
            return null;
        }
        byte[] result=new byte[hexStr.length() / 2];
        for(int i=0;i<hexStr.length()/2;i++){
            int high=Integer.parseInt(hexStr.substring(2*i,2*i+1),16);
            int low=Integer.parseInt(hexStr.substring(2*i+1,2*i+2),16);
            result[i]=(byte)(high*16+low);
        }
        return result;
    }
}
