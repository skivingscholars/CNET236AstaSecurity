package com.cnet236.asta;

import android.os.Parcelable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thedestroyer on 27/02/14.
 */
public abstract class Test implements Runnable {
    int colour;
    String additional = "No additional information available";
    String name;
    Locker thisStorage;
    Locker resultsFile;
    ReentrantLock resultsFileLock;

    abstract void updateResults();
    abstract void updateLocker();
    abstract public void run();
}
