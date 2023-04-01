package com.hduy.app_lay_so.Controller;

import java.io.UnsupportedEncodingException;

public class Util
{
    public static byte[] strTobytes(String str){
        byte[] b=null,data=null;
        try {
            b = str.getBytes("utf-8");
            data=new String(b,"utf-8").getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
    /**
     * byte[] merger
     * */
    public static   byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
    public static byte[] strTobytes(String str ,String charset){
        byte[] b=null,data=null;
        try {
            b = str.getBytes("utf-16");
            data=new String(b,"utf-16").getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
}