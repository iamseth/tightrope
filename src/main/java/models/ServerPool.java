package models;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategies.LoadBalancerStrategy;

import java.util.ArrayList;
import java.util.List;

public class ServerPool {

    private static final Logger log = LoggerFactory.getLogger(ServerPool.class);

    private List<Server> servers;
    private LoadBalancerStrategy loadBalancerStrategy;

    public ServerPool(LoadBalancerStrategy loadBalancerStrategy) {
        this.loadBalancerStrategy = loadBalancerStrategy;
        this.servers = new ArrayList<Server>();

        log.info("Initializing server pool with {} strategy", this.loadBalancerStrategy.getName());
    }

    public Server selectServer() {
        return this.loadBalancerStrategy.selectServer(this);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void addServer(Server server) {
        this.servers.add(server);
    }

    public List<Server> getAvailableServers() {
        ArrayList<Server> availableServers = new ArrayList<Server>();
        for (Server server: this.servers) {
            if (server.isAvailable().get()) {
                availableServers.add(server);
            }
        }
        log.debug("{} available servers in pool", availableServers.size());
        return availableServers;
    }
}
