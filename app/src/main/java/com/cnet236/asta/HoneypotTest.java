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
import java.util.ArrayList;
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
                return null;
            }
        }

        return this.thisStorage.getFileData();
    }

    private String getLastAccessed() throws IOException {
        String line, result = "";
        Process process;

        process = Runtime.getRuntime().exec("stat -c %Y "+honeypot);
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = in.readLine()) != null)
            result += line;

        Log.v("HoneypotTest", "Last accessed is: " + result);
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
        //ArrayList<String> newResults = new ArrayList<String>();

        try {
            resultsFileLock.lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        temp = resultsFile.getFileData();
        results = temp.split("\n");
        //Log.v("Honeypot", "-"+temp);
        temp = "";

        for(int i = 0; i < results.length; i++) {
            if(i == 0) {
                temp += results[i] + "\n";
                temp += name+":"+additional+":"+colour+"\n";
                i++;
                continue;
            }

            temp += results[i];
            temp += "\n";
        }

        for(int i = 0; i < 3-results.length; i++)
            temp+="a\n";

        resultsFile.setFileData(temp);
        resultsFileLock.unlock();
    }

    @Override
    public void run() {
        time = getTime();

        try {
            if(!time.equals(getLastAccessed())) {
                time = getLastAccessed();
                colour = 2;
                additional = "File has been changed, consider that your phone has been accessed by a third party";
            } else {

            }
        } catch (NullPointerException e) {
            Log.v("HoneypotTest", "couldn't access sdcard");
            time = "";
            colour = 0;
            additional = "Could not access data.";
        } catch (IOException e) {
            Log.v("HoneypotTest", "IOException getting last accessed");
            colour = 0;
            additional = "Could not access data.";
            time = "";
        }

        updateLocker();
        updateResults();

        Log.v("HoneypotTest", colour + " : " + additional);
    }
}