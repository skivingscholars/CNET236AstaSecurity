package com.cnet236.asta;

import android.os.Parcel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thedestroyer on 01/03/14.
*/
public class HoneypotTest extends Test {
    HoneypotTest(Locker results, ReentrantLock resultsLock, Locker testStorage) {
        name = "Honeypot Checker";
        resultsFile = results;
        resultsFileLock = resultsLock;
        thisStorage = testStorage;
    }

    @Override
    public void updateLocker() {

    }

    @Override
    public void updateResults() {

    }

    @Override
    public void run() {

    }
}