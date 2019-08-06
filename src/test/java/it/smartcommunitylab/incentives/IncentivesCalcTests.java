package it.smartcommunitylab.incentives;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import it.smartcommunitylab.incentives.model.Delivery;
import it.smartcommunitylab.incentives.model.IncentiveModel;
import it.smartcommunitylab.incentives.model.IncentiveStatus;
import it.smartcommunitylab.incentives.model.Reward;
import it.smartcommunitylab.incentives.service.IncentiveCalculator;

public class IncentivesCalcTests {

	private IncentiveCalculator calc = new IncentiveCalculator();
	private IncentiveModel model = null;
	
	@Before
	public void init() {
		Yaml yaml = new Yaml(new Constructor(IncentiveModel.class));
		InputStream inputStream = this.getClass()
		 .getClassLoader()
		 .getResourceAsStream("incentives.yml");
		model = yaml.load(inputStream);
	}
	
	@Test
	public void testFailedDelivery() {
		// failed delivery
		Delivery delivery = new Delivery();
		delivery.setRecipientId("1");
		delivery.setAttempts(3);
		delivery.setTimeSlotFrom(LocalDateTime.now());
		delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
		delivery.setLocationType("home");
		delivery.setStatus(IncentiveCalculator.STATUS_FAILURE);
		
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		calc.updateStatusAndComputeRewards(model, status, delivery, Collections.emptyList(), Collections.emptyList());
		Assert.assertEquals(0, (int)status.getPoints());
		Assert.assertEquals(85, (int)status.getReliabilityIndex());
		Assert.assertNull(status.getBlackListEnd());
	}
	

	@Test
	public void testSuccessDelivery() {
		// success delivery
		Delivery delivery = new Delivery();
		delivery.setRecipientId("1");
		delivery.setAttempts(3);
		delivery.setTimeSlotFrom(LocalDateTime.now());
		delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
		delivery.setLocationType("home");
		delivery.setStatus(IncentiveCalculator.STATUS_SUCCESS);
		
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		calc.updateStatusAndComputeRewards(model, status, delivery, Collections.emptyList(), Collections.emptyList());
		Assert.assertEquals(10, (int)status.getPoints());
	}

	@Test
	public void testBlacklist() {
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		
		List<Delivery> list = new LinkedList<>();
		for (int i = 0; i < 5; i++) {
			// success delivery
			Delivery delivery = new Delivery();
			delivery.setRecipientId("1");
			delivery.setAttempts(1);
			delivery.setTimeSlotFrom(LocalDateTime.now());
			delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
			delivery.setLocationType("home");
			delivery.setStatus(IncentiveCalculator.STATUS_FAILURE);
			
			calc.updateStatusAndComputeRewards(model, status, delivery, list, Collections.emptyList());
			list.add(delivery);
			
		}
		Assert.assertEquals(0, (int)status.getPoints());
		Assert.assertEquals(75, (int)status.getReliabilityIndex());
		Assert.assertNotNull(status.getBlackListEnd());
	}

	@Test
	public void testReward() {
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		
		List<Reward> res = null;
		List<Delivery> list = new LinkedList<>();
		for (int i = 0; i < 2; i++) {
			// success delivery
			Delivery delivery = new Delivery();
			delivery.setRecipientId("1");
			delivery.setAttempts(1);
			delivery.setTimeSlotFrom(LocalDateTime.now());
			delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
			delivery.setLocationType("locker");
			delivery.setStatus(IncentiveCalculator.STATUS_SUCCESS);
			
			res = calc.updateStatusAndComputeRewards(model, status, delivery, list, Collections.emptyList());
			list.add(delivery);
			
		}
		Assert.assertEquals(50, (int)status.getPoints());
		Assert.assertEquals(1, res.size());

		// success delivery
		Delivery delivery = new Delivery();
		delivery.setRecipientId("1");
		delivery.setAttempts(1);
		delivery.setTimeSlotFrom(LocalDateTime.now());
		delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
		delivery.setLocationType("home");
		delivery.setStatus(IncentiveCalculator.STATUS_SUCCESS);
		
		res = calc.updateStatusAndComputeRewards(model, status, delivery, list, Collections.emptyList());
		list.add(delivery);
		Assert.assertEquals(65, (int)status.getPoints());
		Assert.assertEquals(2, res.size());
	}
}
