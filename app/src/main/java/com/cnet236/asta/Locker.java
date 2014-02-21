package com.cnet236.asta;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Locker {
    private SecretKey key;
    private final String target;
    private String fileData;
    private final Context current;

    Locker(String fileName, String password, Context context) {
        current = context;
        target = fileName;
        generateKey(password);
        populateFileData();
    }

    private void populateFileData() {
        FileInputStream fileInput = tryFile(target);
        byte[] encBytes = null;

        if(fileInput == null) {
            fileData = "";
            return;
        }

        try {
            int len = fileInput.read(encBytes, 0, 200);
            Log.v("Unlocker", "bytes read: " + len);
            fileInput.close();
        } catch (Exception e) {
            fileData = "";
            return;
        }

        fileData = decodeFile(encBytes);
    }

    private String decodeFile(byte[] bytes) {
        Log.v("Unlocker", "decoding...");
        Cipher cipher;
        String finalString = "";
        byte[] b;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            b = cipher.doFinal(bytes);
            finalString = b.toString();
        } catch (Exception e) {
            Log.e("Unlocker", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        Log.v("Unlocker", "bytes len: " + bytes.length + ", decoded: " + finalString);
        return finalString;
    }

    private void generateKey(String p) {
        final byte[] salt = getSalt();
        final char[] password = p.toCharArray();
        final int iter = 512;
        final int kLen = 256;

        try {
            SecretKeyFactory sKF = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password, salt, iter, kLen);
            key = sKF.generateSecret(keySpec);
        } catch (Exception e) {
            Log.e("Unlocker", "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private byte[] getSalt() {
        String fileName = "notencryptiondata";
        FileInputStream fileInput = tryFile(fileName);
        byte[] salt = new byte[32];

        try {
            if (fileInput.read(salt, 0, 32) < 32)
                throw new IOException("message");
        } catch (Exception e) {
            SecureRandom s = new SecureRandom();
            s.nextBytes(salt);
            try {
                fileInput.close();
            } catch(Exception e1) {
                Log.d("Unlocker", "Couldn't close file, probably wasn't open");
            }
            writeData(salt, fileName) ;
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : salt)
            sb.append(Integer.toHexString((int) (b & 0xff)));

        Log.v("Unlocker", sb.toString());
        return salt;
    }

    private FileInputStream tryFile(String fileName) {
        FileInputStream fi = null;

        try {
            Log.v("Unlocker", "Opening file: "+fileName);
            fi = current.openFileInput(fileName);
        }
        catch (FileNotFoundException e) {
            try {
                Log.d("Unlocker", "making file: " + fileName);
                FileOutputStream fo = current.openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.close();
                fi = current.openFileInput(fileName);
            } catch (FileNotFoundException e1) {
                Log.e("Unlocker", "Couldn't create file: "+fileName);
                e1.printStackTrace();
            } catch (Exception e2) {
                Log.e("Unlocker", "can't into android filesystem.");
            }
            Log.d("Unlocker", "file not found, created replacement.");
        }

        return fi;
    }

    public void setFileData(String data) {
        fileData = data;
        encryptFileData();
    }

    public String getFileData() {
        return fileData;
    }

    public byte[] getKeyHash() {
        MessageDigest thisHash;
        byte[] thisBytes;


        thisBytes = Base64.encode(this.key.getEncoded(), Base64.DEFAULT);

        try {
            thisHash = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Log.v("Unlocker", "no algo");
            return null;
        }
        thisHash.update(thisBytes);

        return thisHash.digest();

    }
    private void encryptFileData() {
        Log.v("Unlocker", "Encrypting fileData: " + target);
        byte[] fileDataBytes;
        Cipher cipher;
        byte[] encBytes;

        try {
            fileDataBytes = fileData.getBytes("UTF-8");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encBytes = cipher.doFinal(fileDataBytes);
            Log.v("Unlocker", "length: " + encBytes.length);
        } catch(Exception e) {
            Log.e("Unlocker", e.getMessage());
            return;
        }

        writeData(encBytes, target);
    }

    private void writeData(byte[] data, String fileName) {
        FileOutputStream output;

        try {
            output = current.openFileOutput(fileName, Context.MODE_PRIVATE);
            output.write(data);
            output.close();
        } catch (Exception e) {
            Log.e("Unlocker", e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean equals(Locker other) {
        StringBuffer thissb = new StringBuffer();
        StringBuffer othersb = new StringBuffer();

        for (byte b : this.getKeyHash())
            thissb.append(Integer.toHexString((int) (b & 0xff)));
        for (byte b : other.getKeyHash())
            othersb.append(Integer.toHexString((int) (b & 0xff)));

        if(Arrays.equals(this.getKeyHash(), other.getKeyHash()))
            return true;

        Log.d("Unlocker", thissb.toString() + " : " + othersb.toString());

        return false;
    }
}
