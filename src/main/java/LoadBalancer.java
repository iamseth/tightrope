import models.Statistics;
import models.Server;
import models.ServerPool;
import network.FrontendHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import strategies.LoadBalancerStrategy;
import strategies.RoundRobinStrategy;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class LoadBalancer {

    private static final Logger log = LoggerFactory.getLogger(FrontendHandler.class);

    private Channel acceptor;
    private ChannelGroup allChannels;
    private ServerBootstrap bootstrap;
    private ServerPool serverPool;
    private int port;
    private Statistics statistics;

    public LoadBalancer(ServerPool serverPool, int port) {
        this.serverPool = serverPool;
        this.port = port;
        this.statistics = new Statistics();
    }

    public synchronized void start() {
        final Executor bossPool = Executors.newCachedThreadPool();
        final Executor workerPool = Executors.newCachedThreadPool();
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(bossPool, workerPool));
        final ClientSocketChannelFactory clientSocketChannelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);
        bootstrap.setOption("child.tcpNoDelay", true);
        allChannels = new DefaultChannelGroup("handler");

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new FrontendHandler(allChannels, clientSocketChannelFactory, serverPool, statistics));
            }
        });

        log.info("Starting on port {}", port);
        acceptor = bootstrap.bind(new InetSocketAddress(port));

        if (acceptor.isBound()) {
            log.info("Server started successfully");
        }
    }

    public synchronized void stop() {
        this.allChannels.close().awaitUninterruptibly();
        this.acceptor.close().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();
    }

    public Statistics getStatistics() {
        return this.statistics;
    }

    public static void main(String[] args) throws InterruptedException {
        final LoadBalancerStrategy loadBalancerStrategy = new RoundRobinStrategy();
        final ServerPool serverPool = new ServerPool(loadBalancerStrategy);
        serverPool.addServer(new Server("10.0.0.100", 8080));
        serverPool.addServer(new Server("10.0.0.201", 8080));
        final LoadBalancer loadBalancer = new LoadBalancer(serverPool, 9000);
        loadBalancer.start();
        Thread shutdownHook = new Thread() {
            @Override
            public void run() {
                loadBalancer.stop();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);


        while(true) {
            Statistics status = loadBalancer.getStatistics();
            log.info("{} connections and {} bytes transfered so far", status.getFrontendConnections(), status.getBytesIn());
            sleep(2000);
        }

    }
}
