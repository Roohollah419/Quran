package com.example.quran.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utility class for testing LiveData.
 * Allows synchronous access to LiveData values in tests.
 */
public class LiveDataTestUtil {

    /**
     * Gets the value from a LiveData object synchronously.
     * This method blocks until the LiveData emits a value or times out after 2 seconds.
     *
     * @param liveData The LiveData to observe
     * @param <T> The type of data held by the LiveData
     * @return The value emitted by the LiveData
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        // Wait for LiveData to emit a value or timeout after 2 seconds
        if (!latch.await(2, TimeUnit.SECONDS)) {
            liveData.removeObserver(observer);
            throw new RuntimeException("LiveData value was never set.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}
