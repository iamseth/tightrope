package network;


import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BackendHandler extends SimpleChannelHandler {

    private static final Logger log = LoggerFactory.getLogger(BackendHandler.class);
    private final Channel frontendChannel;

    public BackendHandler(Channel frontendChannel) {
        this.frontendChannel = frontendChannel;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        this.frontendChannel.write(e.getMessage());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (this.frontendChannel.isConnected()) {
            log.debug("closing channel from backend");
            this.frontendChannel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = e.getChannel();
        if (channel.isConnected()) {
            log.debug("Exception caught connecting to {}. {}", channel.getRemoteAddress(), e.getCause());
            channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }


}
