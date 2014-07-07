package strategies;


import models.Server;
import models.ServerPool;

public interface LoadBalancerStrategy {
    Server selectServer(ServerPool serverPool);
    String getName();
}
