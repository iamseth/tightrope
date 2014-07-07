package models;

import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private final String hostname;
    private final int port;
    private AtomicBoolean available;

    public Server(final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
        this.available = new AtomicBoolean(true);
    }


    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public AtomicBoolean isAvailable() {
        return available;
    }

    public void setAvailable(AtomicBoolean available) {
        this.available = available;
    }
}
