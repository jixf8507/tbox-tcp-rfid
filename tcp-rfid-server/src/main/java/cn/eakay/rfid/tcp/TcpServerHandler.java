package cn.eakay.rfid.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.eakay.parking.bean.RfidCarInfo;
import cn.eakay.parking.common.NumberUtils;
import cn.eakay.rfid.spring.SpringAppHelp;
import cn.eakay.rfid.tcp.help.RfidContext;
import cn.eakay.rfid.tools.CRC16;

public class TcpServerHandler extends SimpleChannelInboundHandler<byte[]> {
	private static Logger log = Logger.getLogger(TcpServerHandler.class);

	private static Map<String, RfidContext> rfidDevices = new HashMap<String, RfidContext>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes)
			throws Exception {
		byte cmd = bytes[10];
		System.err.println("tcpServerhandler cmd==" + toInt(cmd));
		// if (bytes.length < 8) {
		// return;
		// }
		
//		byte type = bytes[11];

		byte[] addr = new byte[] { bytes[4], bytes[5], bytes[6], bytes[7],
				bytes[8], bytes[9] };
		String rfidKey = new String(addr);
		addRfidDevices(rfidKey, addr, ctx);
		switch (cmd) {
		case (byte) 0x01:
			// 解析上线报文
			int len = toInt(bytes[13]) / 11;
			List<RfidCarInfo> cardList = new ArrayList<RfidCarInfo>();
			for (int i = 0; i < len; i++) {
				String card = (toInt(bytes[16 + i * 11]) * 256 + toInt(bytes[17 + i * 11]))
						+ "";
				String readTime = "20" + toInt(bytes[19 + i * 11]) + "-"
						+ toInt(bytes[20 + i * 11]) + "-"
						+ toInt(bytes[21 + i * 11]) + " "
						+ toInt(bytes[22 + i * 11]) + ":"
						+ toInt(bytes[23 + i * 11]) + ":"
						+ toInt(bytes[24 + i * 11]);
				log.info(card+"  "+readTime);
				RfidCarInfo rfid = RfidCarInfo.create(card, rfidKey, readTime);
				cardList.add(rfid);
			}
			SpringAppHelp.getRfidRedisDao().batchInsert(cardList);
			reMsg02(addr, ctx);
			return;
		case (byte) 0x03:
			reMsg04(addr, ctx);
			return;
		case (byte) 0x0C:
			reMsg0C(addr, ctx);
//			reMsg08(addr, ctx);
//			reMsg10(addr, ctx);
			return;
		case (byte) 0x13:
//			reMsg08(addr, ctx);
//			reMsg10(addr, ctx);
			return;
		default:
			break;
		}

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

	public void reMsg(String rfidKey, byte[] addr, byte cmd, byte type,
			ChannelHandlerContext ctx) {
		byte[] msg = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, addr[0], addr[1], addr[2], addr[3], addr[4],
				addr[5], cmd, type, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		ctx.writeAndFlush(msg);
	}

	/**
	 * 回复校时
	 * 
	 * @param rfidKey
	 * @param addr
	 * @param ctx
	 */
	public void reMsg0C(byte[] addr, ChannelHandlerContext ctx) {
		byte[] nowBytes = getData();
		byte[] msg = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, addr[0], addr[1], addr[2], addr[3], addr[4],
				addr[5], (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x06,
				nowBytes[0], nowBytes[1], nowBytes[2], nowBytes[3],
				nowBytes[4], nowBytes[5] };
		byte[] cmd = getCRC(msg);
		ctx.writeAndFlush(cmd);
	}
	
	public void reMsg02(byte[] addr, ChannelHandlerContext ctx) {		
		byte[] msg = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, addr[0], addr[1], addr[2], addr[3], addr[4],
				addr[5], (byte) 0x02, (byte) 0x00, (byte) 0x00,(byte) 0x00 };
		byte[] cmd = getCRC(msg);
		ctx.writeAndFlush(cmd);
	}
	
	public void reMsg08(byte[] addr, ChannelHandlerContext ctx) {		
		byte[] msg = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, addr[0], addr[1], addr[2], addr[3], addr[4],
				addr[5], (byte) 0x08, (byte) 0x00, (byte) 0x00,(byte) 0x02, (byte) 0x00,(byte) 0x3C };
		byte[] cmd = getCRC(msg);
		ctx.writeAndFlush(cmd);
	}
	
	public void reMsg04(byte[] addr, ChannelHandlerContext ctx) {		
		byte[] msg = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, addr[0], addr[1], addr[2], addr[3], addr[4],
				addr[5], (byte) 0x04, (byte) 0x00, (byte) 0x00,(byte) 0x00 };
		byte[] cmd = getCRC(msg);
		ctx.writeAndFlush(cmd);
	}
	
	public void reMsg10(byte[] addr, ChannelHandlerContext ctx) {		
		byte[] msg = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, addr[0], addr[1], addr[2], addr[3], addr[4],
				addr[5], (byte) 0x08, (byte) 0x00, (byte) 0x00,(byte) 0x02, (byte) 0x07,(byte) 0x07 };
		byte[] cmd = getCRC(msg);
		ctx.writeAndFlush(cmd);
	}

	private byte[] getCRC(byte[] msg) {
		int crc = CRC16.calcCrc16(msg);
		byte[] cmd = new byte[msg.length + 2];
		for (int i = 0; i < msg.length; i++) {
			cmd[i] = msg[i];
		}
		cmd[msg.length] = (byte) NumberUtils.int2Byte(crc)[2];
		cmd[msg.length + 1] = (byte) NumberUtils.int2Byte(crc)[3];
		return cmd;
	}

	public static void main(String[] args) {
		byte[] card = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0x43, (byte) 0x54, (byte) 0x53,
				(byte) 0x54, (byte) 0x38, (byte) 0x35, (byte) 0x0C,
				(byte) 0x00, (byte) 0x00, (byte) 0x00 };
		int aa = CRC16.calcCrc16(card);

		System.out.println((byte) NumberUtils.int2Byte(aa)[2]);
		System.out.println((byte) NumberUtils.int2Byte(aa)[3]);
		System.out.println("AA++==" + aa);
		// getData();
	}

	public static byte[] getData() {
		byte[] bytes = new byte[6];
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");// 可以方便地修改日期格式
		String dataStr = dateFormat.format(now);
		bytes[0] = (byte) NumberUtils.int2Byte(Integer.parseInt(dataStr
				.substring(0, 2)))[3];
		bytes[1] = (byte) NumberUtils.int2Byte(Integer.parseInt(dataStr
				.substring(2, 4)))[3];
		bytes[2] = (byte) NumberUtils.int2Byte(Integer.parseInt(dataStr
				.substring(4, 6)))[3];
		bytes[3] = (byte) NumberUtils.int2Byte(Integer.parseInt(dataStr
				.substring(6, 8)))[3];
		bytes[4] = (byte) NumberUtils.int2Byte(Integer.parseInt(dataStr
				.substring(8, 10)))[3];
		bytes[5] = (byte) NumberUtils.int2Byte(Integer.parseInt(dataStr
				.substring(10)))[3];
		return bytes;
	}

}