package cn.eakay.rfid.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import org.apache.log4j.Logger;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

	private static Logger log = Logger.getLogger(MessageDecoder.class);

	private byte[] oldBytes;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		byte[] bytes = new byte[in.readableBytes() + 1];
		for (int i = 0; i < in.readableBytes(); i++) {
			Byte b = in.getByte(i);
			bytes[i] = b;
		}
		System.out.println("jxf bytes====" + bytes.length);
		// bytes[in.readableBytes()] = 0x24;

		// if (isComplete(bytes)) {
		// out.add(bytes);
		// clear();
		// return;
		// }

		merge(bytes);

		while (oldBytes.length > 16) {
			if (isHead()) {
				int len = toInt(oldBytes[13]);
				if (oldBytes.length < len + 16) {
					return;
				} else {
					byte[] comBytes = new byte[len + 16];
					for (int i = 0; i < comBytes.length; i++) {
						comBytes[i] = oldBytes[i];
					}
					out.add(comBytes);
					subBytes(len + 16);
				}
			} else {
				subBytes(1);
			}
		}

		// if (oldBytes.length > bytes.length && isComplete(oldBytes)) {
		// out.add(oldBytes);
		// clear();
		// return;
		// }
		// 残留报文超过最大长度时清除残留报文
		// if (!isHead(oldBytes)) {
		//
		// clear();
		// }
	}

	private boolean isHead() {
		// if()
		return oldBytes[0] == (byte) 0XFF && oldBytes[1] == (byte) 0XFF
				&& oldBytes[2] == (byte) 0XFF && oldBytes[3] == (byte) 0XFF
				&& oldBytes[4] != (byte) 0XFF;
	}

	private boolean startFF() {
		// if()
		return oldBytes[0] == (byte) 0XFF;
	}

	/**
	 * 合并报文
	 * 
	 * @param bytes
	 * @param old
	 * @return
	 */
	private void merge(byte[] bytes) {
		if (oldBytes == null) {
			oldBytes = new byte[0];
		}
		byte[] newBytes = new byte[oldBytes.length + bytes.length];
		for (int i = 0; i < oldBytes.length; i++) {
			newBytes[i] = oldBytes[i];
		}
		for (int i = 0; i < bytes.length; i++) {
			newBytes[oldBytes.length + i] = bytes[i];
		}
		oldBytes = newBytes;
	}

	private void subBytes(int index) {
		if (index >= oldBytes.length) {
			oldBytes = new byte[0];
			return;
		}
		byte[] newBytes = new byte[oldBytes.length - index];
		for (int i = 0; i < oldBytes.length - index; i++) {
			newBytes[i] = oldBytes[i + index];
		}
		oldBytes = newBytes;
	}

	private void clear() {
		oldBytes = null;
	}

	/**
	 * 是否完整报文
	 * 
	 * @param bytes
	 * @return
	 */
	public boolean isComplete(byte[] bytes) {
		if (bytes.length < 10) {
			return false;
		}
		// if (!isHead(bytes)) {
		// return false;
		// }
		byte bNo = bytes[10];// 报文编号
		log.info(bytes.length + "  " + bNo);
		switch (bNo) {
		case (byte) 0X01:
			// if (bytes.length == 11) {
			// return true;
			// }
			// return false;
			return true;
		case (byte) 0X02:
			return true;
		case (byte) 0X0c:
			if (bytes.length == 16) {
				return true;
			}
			return false;
		default:
			return false;
		}

	}

	public static int toInt(byte b) {
		return (b & 0xFF);
	}

	// private boolean checkHead() {
	// for( ){
	//
	// }
	//
	// }
}
