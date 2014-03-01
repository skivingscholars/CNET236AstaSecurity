package com.cnet236.asta;

import java.util.concurrent.locks.Lock;

/**
 * Created by thedestroyer on 01/03/14.
 */
public class SimCardChangeTest extends Test {
    SimCardChangeTest(Locker results, Lock resultsLock, Locker testStorage) {
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