package com.cnet236.asta;

import java.util.concurrent.locks.Lock;

/**
 * Created by thedestroyer on 28/02/14.
 */
public class TripCheckTest extends Test {
    TripCheckTest(Locker results, Lock resultsLock, Locker testStorage) {
        name = "Integrity Checker";
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
