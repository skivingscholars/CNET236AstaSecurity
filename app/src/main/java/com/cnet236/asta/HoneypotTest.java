package com.cnet236.asta;

import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thedestroyer on 01/03/14.
*/
public class HoneypotTest extends Test {
    private String honeypot;
    private String time;

    HoneypotTest(Locker results, ReentrantLock resultsLock, Locker testStorage) {
        name = "Honeypot Checker";
        resultsFile = results;
        resultsFileLock = resultsLock;
        thisStorage = testStorage;
        honeypot = Environment.getExternalStorageDirectory().getAbsolutePath()+"/passwords.txt";
        Log.v("Honeypot", "file is: " + honeypot);
        //hash = getHash();
    }

    private String getTime() {
        if(this.thisStorage.getFileData().equals("")) {
            try {
                new File(Environment.getExternalStorageDirectory(), honeypot).createNewFile();
                this.thisStorage.setFileData(getLastAccessed());
            } catch (IOException e) {
                Log.v("Honeypot", "IOException...");
                e.printStackTrace();
            }
        }

        return this.thisStorage.getFileData();
    }

    private String getLastAccessed() {
        String line, result = "";
        try{
            Process process;
            process = Runtime.getRuntime().exec("stat -c %Y "+honeypot);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((line = in.readLine()) != null)
                result += line;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void updateLocker() {
        this.thisStorage.setFileData(time);
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
        results[1] = name+":"+additional+":"+colour;

        for(String s: results)
            temp += s;

        resultsFile.setFileData(temp);
        resultsFileLock.unlock();
    }

    @Override
    public void run() {
        time = getTime();
        if(!time.equals(getLastAccessed())) {
            time = getLastAccessed();
            colour = 2;
            additional = "File has been changed, consider that your phone has been access by a third party";
        }
    }
}