package cn.eakay.rfid.tcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TCPServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("logging", new LoggingHandler(LogLevel.INFO));
//		pipeline.addLast(new DelimiterBasedFrameDecoder(30, Unpooled
//				.copiedBuffer("$".getBytes())));
		pipeline.addLast("decoder", new MessageDecoder());
		// pipeline.addLast("test",new TestDecode());
		pipeline.addLast("encoder", new ByteArrayEncoder());
		pipeline.addLast("TCPClientHandler", new TcpServerHandler());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}
}
