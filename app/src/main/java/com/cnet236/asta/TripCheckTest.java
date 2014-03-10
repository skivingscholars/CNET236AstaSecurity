package com.cnet236.asta;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thedestroyer on 28/02/14.
 */
public class TripCheckTest extends Test {
    ArrayList<ArrayList<String>> files = new ArrayList<ArrayList<String>>();

    TripCheckTest(Locker results, ReentrantLock resultsLock, Locker testStorage) {
        name = "Integrity Checker";
        resultsFile = results;
        resultsFileLock = resultsLock;
        thisStorage = testStorage;

        populateFiles();
    }

    TripCheckTest(Parcel source) {
        Log.v("TripCheckTest", "Unparcelling");
        additional = source.readString();
        name = source.readString();
        colour = source.readInt();
        resultsFile = (Locker)source.readValue(getClass().getClassLoader());
        resultsFileLock = (ReentrantLock)source.readSerializable();
        int filesCount = source.readInt();
        Log.v(name, "files size: " + filesCount);

        if(filesCount > 0) {
            files = new ArrayList<ArrayList<String>>();
            for(int i = 0; i < filesCount; i++) {
                ArrayList<String> s = new ArrayList<String>();
                String[] temp = null;
                source.readStringArray(temp);
                Collections.addAll(s, temp);
                files.add(s);
            }
        } else {
            files = new ArrayList<ArrayList<String>>();
        }
        Log.v("name", "Got to the end");
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
        results[0] = name+":"+additional+":"+colour;

        for(String s: results) {
            temp += s;
            temp += "\n";
        }

        for(int i = 0; i < 3-results.length; i++)
            temp+="a\n";

        resultsFile.setFileData(temp);
        resultsFileLock.unlock();
    }

    @Override
    public void run() {
        ArrayList<String> filesChanged;

        filesChanged = checkFiles();
        additional = filesChanged.size() + " files changed.";

        if(filesChanged.size() != 0) {
            additional += "\nThey are:\n";
            for(String s: filesChanged) {
                additional += s;
                additional += "\n";
            }

            if(filesChanged.size() > 4)
                colour = 2;
            else
                colour = 1;
        }
        else {
            colour = 0;
        }

        updateLocker();
        updateResults();

        Log.v("TripCheckTest", colour + " : " + additional);
    }

    private void populateFiles() { //files split by newline, name and hash split by :
        String[] lines = thisStorage.getFileData().split("\n");

        for(int file = 0; file < lines.length; file++) {
            String[] fileHash = lines[file].split(":");

            if(fileHash.length < 2)
                return;

            ArrayList<String> temp = new ArrayList<String>();
            temp.add(fileHash[0]);
            temp.add(fileHash[1]);

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
