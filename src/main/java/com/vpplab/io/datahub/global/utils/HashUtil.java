package com.vpplab.io.datahub.global.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class HashUtil {

    public static String getSalt() {
        Random random = new Random();
        byte[] salt = new byte[10];

        random.nextBytes(salt);

        StringBuffer sb = new StringBuffer();

        for(int i=0; i<salt.length; i++) {
            sb.append(String.format("%02x", salt[i]));
        }

        return sb.toString();
    }

    public static String getEncrypt(String pwd) {

        String result = "";
        byte[] bytes = pwd.getBytes();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);

            byte[] b = md.digest();

            StringBuffer sb = new StringBuffer();

            for(int i=0; i<b.length; i++) {
                sb.append(Integer.toString((b[i] & 0xFF) + 256, 16).substring(1));
            }

            result = sb.toString();

        } catch (Exception e) {
        }

        return result;
    }
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//      String  sha256pwsd =   HashUtil.getEncrypt("1234", "donghae");
//      System.out.println(sha256pwsd);

        String a = HashUtil.getEncrypt("vpplab0127!");
        System.out.println(a);
    }
}

//71b6c1d53832f789a7f2435a7c629245fa3761ad8487775ebf4957330213a706
