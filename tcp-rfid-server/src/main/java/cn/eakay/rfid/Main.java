package cn.eakay.rfid;

import org.apache.log4j.Logger;


import cn.eakay.rfid.spring.SpringAppHelp;

public class Main {
 
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		log.info("main start.....");
		try {
			SpringAppHelp.start();
			log.info("main start  ok.....");
//			RfidRedisDao rfidRedisDao = SpringAppHelp.getRfidRedisDao();
//			while (true) {
//				System.out.println("1111");
//				RfidCarInfo model = RfidCarInfo.create("8814", "12");
//				rfidRedisDao.insertOrUpdate(model);
//				Thread.sleep(10000);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
