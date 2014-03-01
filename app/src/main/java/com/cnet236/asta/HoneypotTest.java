package com.cnet236.asta;

import java.util.concurrent.locks.Lock;

/**
 * Created by thedestroyer on 01/03/14.
*/
public class HoneypotTest extends Test {
    HoneypotTest(Locker results, Lock resultsLock, Locker testStorage) {
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