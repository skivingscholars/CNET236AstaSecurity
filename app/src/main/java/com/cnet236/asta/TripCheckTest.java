package com.cnet236.asta;

import android.util.Log;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

/**
 * Created by thedestroyer on 28/02/14.
 */
public class TripCheckTest extends Test {
    ArrayList<ArrayList<String>> files = new ArrayList<ArrayList<String>>();

    TripCheckTest(Locker results, Lock resultsLock, Locker testStorage) {
        name = "Integrity Checker";
        resultsFile = results;
        resultsFileLock = resultsLock;
        thisStorage = testStorage;

        populateFiles();
    }

    @Override
    public void updateLocker() {
        String fileData = "";

        for(int i = 0; i < files.size(); i++) {
            fileData += files.get(i).get(0);
            fileData += ":";
            fileData += files.get(i).get(1);
            fileData += "\n";
        }

        thisStorage.setFileData(fileData);
    }

    @Override
    public void updateResults() {
        String[] results;
        String temp = "";

        try {
            resultsFileLock.lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        results = resultsFile.getFileData().split("\n");
        results[0] = name+":"+additional;

        for(String s: results)
            temp += s;

        resultsFile.setFileData(temp);
    }

    @Override
    public void run() {
        checkFiles();
        updateLocker();
        updateResults();
    }

    private void populateFiles() { //files split by newline, name and hash split by :
        String[] lines = thisStorage.getFileData().split("\n");

        for(int file = 0; file < lines.length; file++) {
            String[] fileHash = lines[file].split(":");
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(0, fileHash[0]);
            temp.add(1, fileHash[1]);

            files.add(temp);
        }
    }

    private ArrayList<String> checkFiles() {
        ArrayList<String> filesChanged = new ArrayList<String>();
        String fileChanged;

        try {
            for(ArrayList<String> fileHash: files) {
                fileChanged = checkFile(fileHash);
                if(fileChanged != null) {
                    filesChanged.add(fileHash.get(0));
                    fileHash.set(1, fileChanged); //update files with new hash
                }
            }
        } catch (Exception e) {
            Log.v("TripCheckTest", "error checking integrity of files");
        }

        return filesChanged;
    }

    private String checkFile(ArrayList<String> fileHash) { //checks file, return new hash if changed, null otherwise
        MessageDigest md;
        FileInputStream is;
        DigestInputStream dis;
        int read;
        String newHash;

        try {
            md = MessageDigest.getInstance("MD5");
            is = new FileInputStream(fileHash.get(0));
            dis = new DigestInputStream(is, md);
            read = dis.read();

            while (read != 0) {
                read = dis.read();
            }
        } catch (Exception e) {
            Log.e("TripCheckTest", "error making hash");
            e.printStackTrace();
            return null;
        }

        newHash = byteToString(md.digest());

        if(!newHash.equals(fileHash.get(0)))
            return newHash;

        return null;
    }

    private String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(Integer.toHexString((b & 0xff)));

        return sb.toString();
    }
}
