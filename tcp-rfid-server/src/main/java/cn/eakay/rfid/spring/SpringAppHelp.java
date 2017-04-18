package cn.eakay.rfid.spring;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;

public class SpringAppHelp {

	private static final String configLocation = "spring-redis.xml";

	private static ApplicationContext context;

	public static void start() {
		context = new ClassPathXmlApplicationContext(configLocation);
	}

	public static ApplicationContext getApplicationContext() {
		if (context == null) {
			start();
		}
		return context;
	}

	public static RfidRedisDao getRfidRedisDao() {
		return (RfidRedisDao) getApplicationContext().getBean(
				"rfidRedisDao");
	}
//
//	public static ParkingHartDataProducer getParkingHartDataProducer() {
//		return (ParkingHartDataProducer) getApplicationContext().getBean(
//				"parkingHartDataProducer");
//	}
}
