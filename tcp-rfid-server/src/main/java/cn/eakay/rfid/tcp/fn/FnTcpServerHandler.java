package cn.eakay.rfid.tcp.fn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.eakay.parking.bean.RfidCarInfo;
import cn.eakay.rfid.spring.SpringAppHelp;
import cn.eakay.rfid.tcp.help.RfidContext;

public class FnTcpServerHandler extends SimpleChannelInboundHandler<byte[]> {
	private static Logger log = Logger.getLogger(FnTcpServerHandler.class);

	private static Map<String, RfidContext> rfidDevices = new HashMap<String, RfidContext>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes)
			throws Exception {

		// if (bytes.length != 20) {
		// return;
		// }

		byte[] addr = new byte[] { bytes[0], bytes[1], bytes[2], bytes[3] };
		String rfidKey = "fn";
		addRfidDevices(rfidKey, addr, ctx);
		int len = toInt(bytes[10]);
		List<RfidCarInfo> cardList = new ArrayList<RfidCarInfo>();
		for (int i = 0; i < len; i++) {
			int card = toInt(bytes[11 + i * 8]) * 256 * 256
					+ toInt(bytes[12 + i * 8]) * 256 + toInt(bytes[13 + i * 8]);
			log.info(card + "  ");
			RfidCarInfo rfid = RfidCarInfo.create(card + "", rfidKey);
			cardList.add(rfid);
		}
		SpringAppHelp.getRfidRedisDao().batchInsert(cardList);
	}

	public static int toInt(byte b) {
		return (b & 0xFF);
	}

	private static void addRfidDevices(String rfidKey, byte[] addr,
			ChannelHandlerContext ctx) {
		// if(rfidDevices.get(rfidKey)==null){
		rfidDevices.put(rfidKey, RfidContext.create(rfidKey, addr, ctx));
		// }
	}

	// public static void main(String args[]) {
	// byte[] addr = new byte[] { 0x02, 0x03, 0x04, 0x05, 0x00, 0x14, 0x00,
	// 0x58, 0x41, 0x0C, 0x01, 0x04, (byte) 0xB4, 0x38, 0x00, 0x18,
	// 0x7F, 0x0D, 0x31, (byte) 0x8D };
	// String rfidKey = new String(addr);
	// int a = toInt(addr[11]) * 256 * 256 + toInt(addr[12]) * 256
	// + toInt(addr[13]);
	// System.out.println(a);
	// }

}