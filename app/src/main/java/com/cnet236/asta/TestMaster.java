package com.cnet236.asta;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cnet236.asta.dummy.TestContent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thedestroyer on 02/03/14.
 */
public class TestMaster{
    private  ArrayList<Test> tests;
    private Locker resultsFile;
    private ReentrantLock resultsFileLock;
    private String password;

    TestMaster(String password, Context context) {
        tests = new ArrayList<Test>();
        resultsFile = new Locker("results", password, context);
        resultsFileLock = new ReentrantLock();
        addTests(password, context);
    }

    TestMaster(byte[] key, Context context) {
        tests = new ArrayList<Test>();
        resultsFile = new Locker("results", key, context);
        resultsFileLock = new ReentrantLock();
        addTests(key, context);
    }

    private void addTests(String p, Context c) {
        tests.add(0, new TripCheckTest(resultsFile, resultsFileLock, new Locker("TripTestFile", p, c)));
        tests.add(1, new HoneypotTest(resultsFile, resultsFileLock, new Locker("HoneypotFile", p, c)));
    } //TODO: remember to add file names to array in newpasswordactivity

    private void addTests(byte[] key, Context c) {
        tests.add(0, new TripCheckTest(resultsFile, resultsFileLock, new Locker("TripTestFile", key, c)));
        tests.add(1, new HoneypotTest(resultsFile, resultsFileLock, new Locker("HoneypotFile", key, c)));
    } //TODO: remember to add file names to array in newpasswordactivity

    public byte[] getKey() {
        return resultsFile.getKey();
    }

    public void runTests() {
        int threadsFinished = 0;
        ArrayList<Thread> treads = new ArrayList<Thread>();

        for(Test t: tests) {
            treads.add(new Thread(t));
            treads.get(treads.size() - 1).start();
        }

        while (threadsFinished != tests.size()) {
            if(!treads.get(threadsFinished).isAlive()) // wait for threads to finish
                threadsFinished += 1;
        }
    }

    public ArrayList<TestContent.TestResult> getResults() {
        ArrayList<TestContent.TestResult> results = new ArrayList<TestContent.TestResult>();
        String resultsFileContent;
        String[] eachResult;

        try {
            resultsFileLock.lockInterruptibly();
            resultsFileContent = resultsFile.getFileData();
            resultsFileLock.unlock();
            eachResult = resultsFileContent.split("\n");

            if(eachResult.length < 1)
                return results;

            for(String s: eachResult) {
                Log.v("TestMaster", "eachresult: " + s);
                String[] nameAdditional = s.split(":");
                int colour = Integer.parseInt(nameAdditional[2]);
                results.add(new TestContent.TestResult(nameAdditional[0], nameAdditional[1], colour));
            }
        } catch (InterruptedException e) {
            Log.v("TestMaster", "getting results interrupted");
        } catch(ArrayIndexOutOfBoundsException e) {
            return results;
        }

        return results;
    }
}
