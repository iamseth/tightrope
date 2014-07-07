package strategies;

import models.Server;
import models.ServerPool;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancerStrategy {

    private final String NAME = "RoundRobin";
    private AtomicInteger position = new AtomicInteger(0);

    @Override
    public Server selectServer(final ServerPool serverPool) {
        if (this.position.get() > serverPool.getServers().size() - 1) {
            this.position.set(0);
        }
        return serverPool.getAvailableServers().get(this.position.getAndIncrement());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
