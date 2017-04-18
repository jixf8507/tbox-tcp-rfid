package cn.eakay.rfid.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import cn.eakay.parking.bean.RfidCarInfo;
import cn.eakay.parking.util.JsonUtil;

public class RfidRedisDao {
	private static Logger log = Logger.getLogger(RfidRedisDao.class);

	public StringRedisTemplate stringRedisTemplate;

	public static final String TABLE_NAME = "RfidCarInfo";

	private BoundHashOperations<String, String, String> boundHashOperations;

	public BoundHashOperations<String, String, String> getBoundHashOperations() {
		if (boundHashOperations == null) {
			boundHashOperations = stringRedisTemplate.boundHashOps(TABLE_NAME);
		}
		return boundHashOperations;
	}

	public void insertOrUpdate(RfidCarInfo model) {
		try {
			getBoundHashOperations().put(model.getCarNO(),
					JsonUtil.toJson(model));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public Map<String, String> findAll() {
		try {
			return getBoundHashOperations().entries();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String, String>();
	}

	public void batchInsert(String rfidKey, List<String> cardList) {
		for (String carNO : cardList) {
			RfidCarInfo model = RfidCarInfo.create(carNO, rfidKey);
			insertOrUpdate(model);
		}
	}

	public void batchInsert(List<RfidCarInfo> cardList) {
		for (RfidCarInfo model : cardList) {
			insertOrUpdate(model);
		}
	}

	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

}
