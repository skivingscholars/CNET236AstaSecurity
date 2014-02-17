package com.cnet236.asta;

    import android.content.Context;
    import android.util.Log;

    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.security.SecureRandom;
    import java.security.spec.KeySpec;

    import javax.crypto.Cipher;
    import javax.crypto.SecretKey;
    import javax.crypto.SecretKeyFactory;
    import javax.crypto.spec.PBEKeySpec;

public class Locker {
    private SecretKey key;
    private String target;
    private String fileData;
    private Context current;

    Locker(String fileName, String password, Context context) {
        current = context;
        target = fileName;
        generateKey(password);
        populateFileData();
    }

    private void populateFileData() {
        FileInputStream fileInput = tryFile(target);
        byte[] bytes = null;

        if(fileInput == null) {
            fileData = "";
            return;
        }

        try {
            int len = fileInput.read(bytes, 0, 200);
            Log.v("Unlocker", "bytes read: " + len);
            fileInput.close();
        } catch (Exception e) {
            fileData = "";
            return;
        }

        fileData = decodeFile(bytes);
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
            SecretKeyFactory sKF = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
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
        byte[] salt = new byte[256];

        try {
            fileInput.read(salt, 0, 256);
        } catch (Exception e) {
            SecureRandom s = new SecureRandom();
            s.nextBytes(salt);
            try {
                fileInput.close();
            } catch(Exception e1) {
                Log.d("Unlocker", "Couldn't close file, probably wasn't open");
            }
            writeData(salt, fileName);
        }

        return salt;
    }

    private FileInputStream tryFile(String fileName) {
        FileInputStream fi = null;

        try {
            Log.v("Unlocker", "Opening file");
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

    public void encryptFileData() {
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
}
