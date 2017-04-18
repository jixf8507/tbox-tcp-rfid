package cn.eakay.rfid.tcp.help;

import io.netty.channel.ChannelHandlerContext;

public class RfidContext {

	private String deviceKey;
	private byte[] deviceKeyBytes;
	private ChannelHandlerContext context;
	
	
	private RfidContext(String deviceKey, byte[] deviceKeyBytes,
			ChannelHandlerContext context) {
		super();
		this.deviceKey = deviceKey;
		this.deviceKeyBytes = deviceKeyBytes;
		this.context = context;
	}
	
	public static RfidContext create(String deviceKey, byte[] deviceKeyBytes,
			ChannelHandlerContext context){
		return new RfidContext(deviceKey, deviceKeyBytes, context) ;
	}
	
	public String getDeviceKey() {
		return deviceKey;
	}
	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}
	public byte[] getDeviceKeyBytes() {
		return deviceKeyBytes;
	}
	public void setDeviceKeyBytes(byte[] deviceKeyBytes) {
		this.deviceKeyBytes = deviceKeyBytes;
	}
	public ChannelHandlerContext getContext() {
		return context;
	}
	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}
	
	public static void main(String args[]){
		String arr = "ff ff ff ff 43 54 53 54 38 35 0c 00 00 00 1a 83 " ;
		byte[] bs = new byte[]{(byte)0x43,(byte)0x54,(byte)0x53,(byte)0x54,(byte)0x38,(byte)0x35} ;
		System.out.println(new String(bs));
	}
	
	

}
