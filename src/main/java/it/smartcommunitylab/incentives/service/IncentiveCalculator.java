/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package it.smartcommunitylab.incentives.service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import it.smartcommunitylab.incentives.model.Delivery;
import it.smartcommunitylab.incentives.model.IncentiveStatus;
import it.smartcommunitylab.incentives.model.Reward;

/**
 * Reward and incentive points model and calculation.
 * Reward triggers:
 * - POSITIVE: N successful deliveries in a row
 * - POSITIVE: threshold on points
 * - NEGATIVE: N failed deliveries in a row
 * - NEGATIVE: threshold on points
 * Reward types:
 * - prize (e.g., discount)
 * - time slot reduction
 * - black list
 * - remove from black list
 * 
 * 
 * @author raman
 *
 */
@Component
public class IncentiveCalculator {

	public static final String LOCATION_TYPE_LOCKER = "locker";
	public static final String LOCATION_TYPE_PICKUP = "pickup";
	public static final String LOCATION_TYPE_HOME = "home";

	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILURE = "failure";
	public static final String STATUS_ABORT = "aborted";
	
	public static final String REWARD_TRIGGER_LEVEL = "levelreward";
	public static final String REWARD_TRIGGER_SEQUENCE = "sequence";
	
	
	public static final Map<String, Integer> rewards = new HashMap<String, Integer>();
	static {
		rewards.put(LOCATION_TYPE_HOME, 10);
		rewards.put(LOCATION_TYPE_PICKUP, 20);
		rewards.put(LOCATION_TYPE_LOCKER, 50);
	}
	
	public static final int REWARD_LEVEL_MULTIPLIER = 100;
	public static final int REWARD_LEVEL_START = 0;
	
	public static final int BLACKLIST_THRESHOLD = -50;
	public static final int BLACKLIST_DURATION_DAYS = 10;
	public static final int BLACKLIST_SEQUENCE_THRESHOLD = 3;
	
	
	public static final int SUCCESS_SEQUENCE_THRESHOLD = 3;
	
	/**
	 * Compute points for a single delivery
	 * @param delivery
	 * @return
	 */
	private int computePoints(Delivery delivery) {
		if (STATUS_FAILURE.equals(delivery.getStatus())) {
			// return sum of geometric progression for failed deliveries
			if (LOCATION_TYPE_HOME.equals(delivery.getLocationType())) {
				int attempts = (delivery.getAttempts() == null ? 1 : delivery.getAttempts());
				int reward = rewards.get(LOCATION_TYPE_HOME);
				return -reward * (1 + attempts) / 2 * attempts;
			}
		} else if (STATUS_SUCCESS.equals(delivery.getStatus())) {
			if (!rewards.containsKey(delivery.getLocationType())) return 0;
			int res = 0;
			// count failed
			if (LOCATION_TYPE_HOME.equals(delivery.getLocationType()) && delivery.getAttempts() != null && delivery.getAttempts() > 1) {
				int attempts = delivery.getAttempts() - 1;
				int reward = rewards.get(LOCATION_TYPE_HOME);
				res = -reward * (1 + attempts) / 2 * attempts;
			}
			return rewards.get(delivery.getLocationType()) + res;
		}
		return 0;
	}
	
	/*
	 * Compute reward for the user status update of a delivery.
	 * Reward is generated only when the user is not blacklisted
	 */
	public List<Reward> updateStatusAndComputeRewards(IncentiveStatus status, Delivery delivery, List<Delivery> previous, List<Reward> rewards) {
		if (status.getPoints() == null) status.setPoints(0);
		int oldPoints = status.getPoints();
		int delta = computePoints(delivery);
		// update points
		status.setPoints(oldPoints + delta);
		
		// cheeck and clean black list status
		if (status.getBlackListEnd() != null && status.getBlackListEnd().isBefore(LocalDateTime.now())) {
			status.setBlackListEnd(null);
			status.setBlackListStart(null);
		}
		
		// failure
		if (delta < 0) {
			// check if entering blacklist for points threshold
			if (status.getBlackListStart() == null && oldPoints > BLACKLIST_THRESHOLD && (oldPoints + delta) <= BLACKLIST_THRESHOLD)  {
				status.setBlackListStart(LocalDateTime.now());
				status.setBlackListEnd(status.getBlackListStart().plusDays(BLACKLIST_DURATION_DAYS));
			// check if entering blackist for sequence 	
			} else if (status.getBlackListStart() == null) {
				int count = 0;
				for (Delivery d : previous) {
					if (d.getStatus().equals(STATUS_SUCCESS)) {
						count = 0;
						break;
					}
					if (d.getStatus().equals(STATUS_FAILURE)) {
						count++;
					}
					if (count == BLACKLIST_SEQUENCE_THRESHOLD - 1) {
						break;
					}
				}
				if (count >= BLACKLIST_SEQUENCE_THRESHOLD - 1) {
					status.setBlackListStart(LocalDateTime.now());
					status.setBlackListEnd(status.getBlackListStart().plusDays(BLACKLIST_DURATION_DAYS));					
				}
			}
			
			return Collections.emptyList();
		}
		
		List<Reward> list = new LinkedList<Reward>();
		
		// passed new level: create a reward
		int passedPointRewards = (oldPoints - REWARD_LEVEL_START) / REWARD_LEVEL_MULTIPLIER;
		int newPointRewards = (delta + oldPoints - REWARD_LEVEL_START) / REWARD_LEVEL_MULTIPLIER;
		if (newPointRewards > passedPointRewards && (delta + oldPoints) > status.getMaxPoints()) {
			Reward reward = new Reward();
			reward.setRecipientId(status.getRecipientId());
			reward.setCreated(LocalDateTime.now());
			reward.setType(REWARD_TRIGGER_LEVEL);
			reward.setText(getText(REWARD_TRIGGER_LEVEL, Collections.singletonMap("points", (delta+oldPoints))));
			list.add(reward);
		}
		// check successful delivery sequence
		if (delivery.getStatus().equals(STATUS_SUCCESS)) {
			Reward old = rewards.stream().filter(r -> r.getType().equals(REWARD_TRIGGER_SEQUENCE)).findFirst().orElse(null);
			int count = 0;
			for (Delivery d : previous) {
				if (old != null && d.getTimeSlotFrom().isBefore(old.getCreated())) break;
				if (d.getStatus().equals(STATUS_FAILURE)) {
					count = 0;
					break;
				}
				if (d.getStatus().equals(STATUS_SUCCESS)) {
					count++;
				}
				if (count == SUCCESS_SEQUENCE_THRESHOLD - 1) {
					break;
				}
			}
			if (count >= SUCCESS_SEQUENCE_THRESHOLD - 1) {
				Reward reward = new Reward();
				reward.setRecipientId(status.getRecipientId());
				reward.setCreated(LocalDateTime.now());
				reward.setType(REWARD_TRIGGER_SEQUENCE);
				reward.setText(getText(REWARD_TRIGGER_SEQUENCE, Collections.singletonMap("sequence", SUCCESS_SEQUENCE_THRESHOLD)));
				list.add(reward);
			}
		}

		status.setLastUpdate(LocalDateTime.now());
		return list;
	}
	
	private String getText(String rewardTrigger, Map<String, Object> params) {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile("rewards/"+rewardTrigger+".mustache");
		StringWriter writer = new StringWriter();
		m.execute(writer, params);
		return writer.toString();
	} 
	
}
