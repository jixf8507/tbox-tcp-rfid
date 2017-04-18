package cn.eakay.rfid.tcp.fn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import org.apache.log4j.Logger;

public class FnMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		byte[] bytes = new byte[in.readableBytes()];
		for (int i = 0; i < in.readableBytes(); i++) {
			Byte b = in.getByte(i);
			bytes[i] = b;
		}
		System.out.println("jxf bytes====" + bytes.length);
		out.add(bytes);
	}

	
}
