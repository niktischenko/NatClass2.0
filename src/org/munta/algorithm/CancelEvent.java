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
        if(isStopPending) {
            isStopPending = false;
            return true;
        } else {
            return isStopPending;
        }
    }
    
    public void waitFlag() {
        try {
            latch.await();
        } catch (InterruptedException ex) {
        }
    }
    
    public void setFlag() {
        latch.countDown();
    }
    
    public void resetFlag() {
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
