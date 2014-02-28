package com.cnet236.asta;

import java.util.concurrent.locks.Lock;

/**
 * Created by thedestroyer on 27/02/14.
 */
public abstract class Test implements Runnable {
    int colour;
    String additional = "No additional information available";
    String name;
    Locker thisStorage;
    Locker resultsFile;
    Lock resultsFileLock;

    abstract void updateResults();
    abstract void updateLocker();
    abstract public void run();
}
