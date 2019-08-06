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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import it.smartcommunitylab.incentives.model.Delivery;
import it.smartcommunitylab.incentives.model.IncentiveModel;
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

//	public static final String LOCATION_TYPE_LOCKER = "locker";
//	public static final String LOCATION_TYPE_PICKUP = "pickup";
//	public static final String LOCATION_TYPE_HOME = "home";

	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILURE = "failure";
	public static final String STATUS_ABORT = "aborted";
	
//	public static final String REWARD_TRIGGER_LEVEL = "levelreward";
//	public static final String REWARD_TRIGGER_SEQUENCE = "sequence";
	
	
//	public static final Map<String, Integer> rewards = new HashMap<String, Integer>();
//	static {
//		rewards.put(LOCATION_TYPE_HOME, 10);
//		rewards.put(LOCATION_TYPE_PICKUP, 20);
//		rewards.put(LOCATION_TYPE_LOCKER, 50);
//	}
	
//	public static final int REWARD_LEVEL_MULTIPLIER = 100;
//	public static final int REWARD_LEVEL_START = 0;
	
//	public static final int BLACKLIST_THRESHOLD = -50;
//	public static final int BLACKLIST_DURATION_DAYS = 10;
//	public static final int BLACKLIST_SEQUENCE_THRESHOLD = 3;
	
	
//	public static final int SUCCESS_SEQUENCE_THRESHOLD = 3;
	public static final Integer START_RELIEABILITY_INDEX = 100;
	
	/**
	 * Compute points for a single delivery
	 * @param delivery
	 * @return
	 */
	private int computeReliabilityIndexInc(IncentiveModel model, Delivery delivery) {
		if (STATUS_FAILURE.equals(delivery.getStatus())) {
			int penalty = Math.abs(model.getLocationPenalties().getOrDefault(delivery.getLocationType(), 0));
			if (penalty > 0) {
				int attempts = (delivery.getAttempts() == null ? 1 : delivery.getAttempts());
				return -penalty * attempts;
			} 
		} else if (STATUS_SUCCESS.equals(delivery.getStatus())) {
			int inc = Math.abs(model.getLocationRewards().getOrDefault(delivery.getLocationType(), 0));
			int penalty = Math.abs(model.getLocationPenalties().getOrDefault(delivery.getLocationType(), 0));
			if (inc > 0) {
				int attempts = delivery.getAttempts() - 1;
				return -attempts * penalty + inc; 
			}
		}
		return 0;
	}
	
	/*
	 * Compute reward for the user status update of a delivery.
	 * Reward is generated only when the user is not blacklisted
	 */
	public List<Reward> updateStatusAndComputeRewards(IncentiveModel model, IncentiveStatus status, String action, String state, List<Reward> rewards) {
		int indexDelta = computeActionRewardsInc(model, action, state);
		int pointDelta = computeActionPointsInc(model, status, action, state);
	
		List<Reward> list = updateStatusAndComputeRewards(model, status, indexDelta, pointDelta, Collections.emptyList(), rewards);
		return list;
	}

	
	/*
	 * Compute reward for the user status update of a delivery.
	 * Reward is generated only when the user is not blacklisted
	 */
	public List<Reward> updateStatusAndComputeRewards(IncentiveModel model, IncentiveStatus status, Delivery delivery, List<Delivery> previous, List<Reward> rewards) {
		int indexDelta = computeReliabilityIndexInc(model, delivery);
		int pointDelta = computePointsInc(model, status, delivery);

		List<Reward> list = updateStatusAndComputeRewards(model, status, indexDelta, pointDelta, previous, rewards);
		
		if (delivery.getStatus().equals(STATUS_SUCCESS)) {
			if (model.getSequenceRewards() != null) {
				for (String sequenceReward: model.getSequenceRewards()) {
					int threshold = model.getSequenceRewardMultipliers().getOrDefault(sequenceReward, 0);
					if (threshold == 0) continue;
					
					// check successful delivery sequence
					Reward old = rewards.stream().filter(r -> r.getType().equals(sequenceReward)).findFirst().orElse(null);
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
						if (count == threshold - 1) {
							break;
						}
					}
					if (count >= threshold - 1) {
						Reward reward = new Reward();
						reward.setRecipientId(status.getRecipientId());
						reward.setCreated(LocalDateTime.now());
						reward.setType(sequenceReward);
						reward.setText(model.getSequenceRewardTexts().get(sequenceReward));
						list.add(reward);
					}
					
				}
			}
		}
		return list;
	}
	
	/*
	 * Compute reward for the user status update of a delivery.
	 * Reward is generated only when the user is not blacklisted
	 */
	public List<Reward> updateStatusAndComputeRewards(IncentiveModel model, IncentiveStatus status, int indexDelta, int pointDelta, List<Delivery> previous, List<Reward> rewards) {

		int oldIndex = status.getReliabilityIndex();
		int oldPoints = status.getPoints();

		// update points
		status.setPoints(oldPoints + pointDelta);
		status.setReliabilityIndex(oldIndex + indexDelta);
		
		// cheeck and clean black list status
		if (status.getBlackListEnd() != null && status.getBlackListEnd().isBefore(LocalDateTime.now())) {
			status.setBlackListEnd(null);
			status.setBlackListStart(null);
		}
		
		// failure
		if (indexDelta < 0) {
			if (model.getBlacklistDuration() != null && model.getBlacklistThreshold() != null) {
				// check if entering blacklist for points threshold
				
				if (status.getBlackListStart() == null && oldIndex > model.getBlacklistThreshold() && (oldIndex + indexDelta) <= model.getBlacklistThreshold())  {
					status.setBlackListStart(LocalDateTime.now());
					status.setBlackListEnd(status.getBlackListStart().plusDays(model.getBlacklistDuration()));
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
						if (count == model.getBlacklistSequenceThreshold() - 1) {
							break;
						}
					}
					if (count >= model.getBlacklistSequenceThreshold() - 1) {
						status.setBlackListStart(LocalDateTime.now());
						status.setBlackListEnd(status.getBlackListStart().plusDays(model.getBlacklistDuration()));					
					}
				}
			}
			return new LinkedList<>();
		}
		
		List<Reward> list = new LinkedList<Reward>();
		
		// passed new level: create a reward
		if (model.getPointRewards() != null) {
			for (String pointReward: model.getPointRewards()) {
				int multiplier = model.getPointRewardMultipliers().getOrDefault(pointReward, 0);
				if (multiplier == 0) continue;
				
				int passedPointRewards = oldPoints / multiplier;
				int newPointRewards = (pointDelta + oldPoints) / multiplier;
				if (newPointRewards > passedPointRewards) {
					Reward reward = new Reward();
					reward.setRecipientId(status.getRecipientId());
					reward.setCreated(LocalDateTime.now());
					reward.setType(pointReward);
					reward.setText(model.getPointRewardTexts().get(pointReward));
					list.add(reward);
				}
			}
		}

		status.setLastUpdate(LocalDateTime.now());
		return list;
	}
	
	/**
	 * @param model
	 * @param delivery
	 * @return
	 */
	private int computePointsInc(IncentiveModel model, IncentiveStatus status, Delivery delivery) {
		if (STATUS_SUCCESS.equals(delivery.getStatus())) {
			int inc = Math.abs((Integer)evaluateExpression(model.getLocationPoints().getOrDefault(delivery.getLocationType(), "0"), status));
			if (inc > 0) {
				return inc;
			}
		}
		return 0;
	}

	/**
	 * @param model
	 * @param delivery
	 * @return
	 */
	private int computeActionPointsInc(IncentiveModel model, IncentiveStatus status, String action, String outcome) {
		if (STATUS_SUCCESS.equals(outcome)) {
			int inc = Math.abs((Integer)evaluateExpression(model.getActionPoints().getOrDefault(action, "0"), status));
			if (inc > 0) {
				return inc;
			}
		}
		return 0;
	}
	/**
	 * @param model
	 * @param delivery
	 * @return
	 */
	private int computeActionRewardsInc(IncentiveModel model, String action, String status) {
		if (STATUS_SUCCESS.equals(status)) {
			int inc = Math.abs(model.getActionRewards().getOrDefault(action, 0));
			if (inc > 0) {
				return inc;
			}
		} else if (STATUS_FAILURE.equals(status)) {
			int inc = Math.abs(model.getActionPenalties().getOrDefault(action, 0));
			if (inc > 0) {
				return -inc;
			}
		}
		return 0;
	}
	
	
	private Object evaluateExpression(String expr, IncentiveStatus status) {
		ExpressionParser expressionParser = new SpelExpressionParser();
		Expression expression = expressionParser.parseExpression(expr);
		EvaluationContext context = new StandardEvaluationContext(status);
		return expression.getValue(context);
		
	}
	
	private String getText(String rewardTrigger, Map<String, Object> params) {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile("rewards/"+rewardTrigger+".mustache");
		StringWriter writer = new StringWriter();
		m.execute(writer, params);
		return writer.toString();
	} 
	
}
