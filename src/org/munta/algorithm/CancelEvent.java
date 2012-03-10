package org.munta.algorithm;

import java.util.concurrent.CountDownLatch;

public final class CancelEvent {
    
    private Boolean isStopPending;
    private CountDownLatch latch;
    
    private CancelEvent() {
        isStopPending = false;
        latch = new CountDownLatch(0);
    }
    
    public void setStopPending() {
        isStopPending = true;
    }
    
    public Boolean getStopPendingReset() {
        return isStopPending;
    }
    
    public void waitFlag() {
        try {
            latch.await();
        } catch (InterruptedException ex) {
        }
    }
    
    public void setFlag() {
        isStopPending = false;
        latch.countDown();
    }
    
    public void resetFlag() {
        isStopPending = false;
        latch = new CountDownLatch(1);
    }
    
    private static CancelEvent instance = null;
    public static CancelEvent getInstance() {
        if(instance == null) {
            instance = new CancelEvent();
        }
        return instance;
    }
}
