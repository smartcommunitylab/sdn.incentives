package it.smartcommunitylab.incentives;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import it.smartcommunitylab.incentives.model.Delivery;
import it.smartcommunitylab.incentives.model.IncentiveStatus;
import it.smartcommunitylab.incentives.model.Reward;
import it.smartcommunitylab.incentives.service.IncentiveCalculator;

public class IncentivesCalcTests {

	private IncentiveCalculator calc = new IncentiveCalculator();
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testFailedDelivery() {
		// failed delivery
		Delivery delivery = new Delivery();
		delivery.setRecipientId("1");
		delivery.setAttempts(3);
		delivery.setTimeSlotFrom(LocalDateTime.now());
		delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
		delivery.setLocationType(IncentiveCalculator.LOCATION_TYPE_HOME);
		delivery.setStatus(IncentiveCalculator.STATUS_FAILURE);
		
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		status.setMaxPoints(0);
		calc.updateStatusAndComputeRewards(status, delivery, Collections.emptyList(), Collections.emptyList());
		Assert.assertEquals(-60, (int)status.getPoints());
		Assert.assertNotNull(status.getBlackListEnd());
	}
	

	@Test
	public void testSuccessDelivery() {
		// success delivery
		Delivery delivery = new Delivery();
		delivery.setRecipientId("1");
		delivery.setAttempts(3);
		delivery.setTimeSlotFrom(LocalDateTime.now());
		delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
		delivery.setLocationType(IncentiveCalculator.LOCATION_TYPE_HOME);
		delivery.setStatus(IncentiveCalculator.STATUS_SUCCESS);
		
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		status.setMaxPoints(0);
		calc.updateStatusAndComputeRewards(status, delivery, Collections.emptyList(), Collections.emptyList());
		Assert.assertEquals(-20, (int)status.getPoints());
	}

	@Test
	public void testBlacklist() {
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		status.setMaxPoints(0);
		
		List<Delivery> list = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			// success delivery
			Delivery delivery = new Delivery();
			delivery.setRecipientId("1");
			delivery.setAttempts(1);
			delivery.setTimeSlotFrom(LocalDateTime.now());
			delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
			delivery.setLocationType(IncentiveCalculator.LOCATION_TYPE_HOME);
			delivery.setStatus(IncentiveCalculator.STATUS_FAILURE);
			
			calc.updateStatusAndComputeRewards(status, delivery, list, Collections.emptyList());
			list.add(delivery);
			
		}
		Assert.assertEquals(-30, (int)status.getPoints());
		Assert.assertNotNull(status.getBlackListEnd());
	}

	@Test
	public void testReward() {
		IncentiveStatus status = new IncentiveStatus();
		status.setRecipientId("1");
		status.setPoints(0);
		status.setMaxPoints(0);
		
		List<Reward> res = null;
		List<Delivery> list = new LinkedList<>();
		for (int i = 0; i < 2; i++) {
			// success delivery
			Delivery delivery = new Delivery();
			delivery.setRecipientId("1");
			delivery.setAttempts(1);
			delivery.setTimeSlotFrom(LocalDateTime.now());
			delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
			delivery.setLocationType(IncentiveCalculator.LOCATION_TYPE_LOCKER);
			delivery.setStatus(IncentiveCalculator.STATUS_SUCCESS);
			
			res = calc.updateStatusAndComputeRewards(status, delivery, list, Collections.emptyList());
			list.add(delivery);
			
		}
		Assert.assertEquals(100, (int)status.getPoints());
		Assert.assertEquals(res.size(), 1);

		// success delivery
		Delivery delivery = new Delivery();
		delivery.setRecipientId("1");
		delivery.setAttempts(1);
		delivery.setTimeSlotFrom(LocalDateTime.now());
		delivery.setTimeSlotFrom(LocalDateTime.now().plusHours(3));
		delivery.setLocationType(IncentiveCalculator.LOCATION_TYPE_HOME);
		delivery.setStatus(IncentiveCalculator.STATUS_SUCCESS);
		
		res = calc.updateStatusAndComputeRewards(status, delivery, list, Collections.emptyList());
		list.add(delivery);
		Assert.assertEquals(110, (int)status.getPoints());
		Assert.assertEquals(1, res.size());
	}
}
