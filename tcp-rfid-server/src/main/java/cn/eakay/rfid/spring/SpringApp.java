package cn.eakay.rfid.spring;

import org.apache.log4j.PropertyConfigurator;

import cn.eakay.rfid.tcp.TcpServer;
import cn.eakay.rfid.tcp.fn.FnTcpServer;

public class SpringApp {

	private int port;
	private int fnport;
	private String log4jPath;

	public void start() {
		System.err.println("================log4jPath============" + log4jPath);
		PropertyConfigurator.configure(log4jPath);
		TcpServer.run(port);
		FnTcpServer.run(fnport);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setLog4jPath(String log4jPath) {
		this.log4jPath = log4jPath;
	}

	public void setFnport(int fnport) {
		this.fnport = fnport;
	}

}
