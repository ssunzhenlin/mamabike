package com.coder520.mamabike;

import com.coder520.mamabike.bike.entity.BikeLocation;
import com.coder520.mamabike.bike.entity.Point;
import com.coder520.mamabike.bike.service.BikeGeoService;
import com.coder520.mamabike.bike.service.BikeService;
import com.coder520.mamabike.common.exception.MaMaBikeException;
import com.coder520.mamabike.common.utils.BaiduPushUtil;
import com.coder520.mamabike.record.service.RideRecordService;
import com.coder520.mamabike.user.entity.UserElement;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MamabikeApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MamabikeApplicationTests {


	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;
	@Autowired
	private BikeGeoService bikeGeoService;
	@Autowired
	@Qualifier("bikeServiceImpl")
	private BikeService bikeService;
	@Autowired
	@Qualifier("rideRecordServiceImpl")
	private RideRecordService rideRecordService;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void restTest() {
//		String result = restTemplate.getForObject("/hello",String.class);
//		Assert.assertTrue("失败",result.equals("ok"));
	}

	@Test
	public void geoTest() throws MaMaBikeException {
//		bikeGeoService.geoNearSphere("bike-position","location",
//				new Point(104.063339, 30.547347),0,50,null,null,10);
//
//		bikeGeoService.geoNear("bike-position",null,new Point(104.063339, 30.547347),10,50);
//		bikeGeoService.rideContrail("ride_contrail","15034158110391291507520");
	}
	@Test
	//28000001
	public void unlockTest () throws MaMaBikeException {
		BikeLocation location = new BikeLocation();
		location.setBikeNumber(28000001l);
		Double[] bikePosition = new Double[]{104.263379,30.347332};
		location.setCoordinates(bikePosition);
//		Query query = Query.query(Criteria.where("bike_no").is(location.getBikeNumber()));
//		Update update = Update.update("status",1)
//				.set("location.coordinates",location.getCoordinates());
//		mongoTemplate.updateFirst(query,update,"bike-position");
//		List<BasicDBObject> list = new ArrayList();
//		BasicDBObject temp = new BasicDBObject("loc",bikePosition);
//		list.add(temp);
//		BasicDBObject obj = new BasicDBObject("record_no","454754754575")
//				.append("bike_no",28000001L)
//				.append("contrail",list);
//		mongoTemplate.insert(obj,"ride_contrail");
//		bikeService.reportLocation(location);

	}
	@Test
	//28000001
	public void rideRecordListTest () throws MaMaBikeException {
//		rideRecordService.listRideRecord(1L,0L);
	}


}
