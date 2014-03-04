package com.cnet236.asta;

import android.os.Parcel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thedestroyer on 01/03/14.
 */
public class SimCardChangeTest extends Test {
    SimCardChangeTest(Locker results, ReentrantLock resultsLock, Locker testStorage) {
        name = "Sim Card Change";
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