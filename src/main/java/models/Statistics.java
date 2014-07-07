package models;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {


    private AtomicLong bytesIn;
    private AtomicInteger frontendConnections;

    public Statistics() {
        this.bytesIn = new AtomicLong(0);
        this.frontendConnections = new AtomicInteger(0);
    }

    public long getBytesIn() {
        return bytesIn.get();
    }

    public void addBytes(long bytes) {
        this.bytesIn.addAndGet(bytes);
    }

    public int getFrontendConnections() {
        return frontendConnections.get();
    }

    public void addFrontendConnection() {
        this.frontendConnections.getAndIncrement();
    }

    public void removeFrontendConnection() {
        this.frontendConnections.getAndDecrement();
    }

}
