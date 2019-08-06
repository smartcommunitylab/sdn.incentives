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

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.incentives.model.Delivery;
import it.smartcommunitylab.incentives.model.IncentiveModel;
import it.smartcommunitylab.incentives.model.IncentiveStatus;
import it.smartcommunitylab.incentives.model.Reward;
import it.smartcommunitylab.incentives.repository.DeliveryRepository;
import it.smartcommunitylab.incentives.repository.RewardRepository;
import it.smartcommunitylab.incentives.repository.StatusRepository;

/**
 * @author raman
 *
 */
@Service
@Transactional
public class IncentivesService {

	@Autowired
	private DeliveryRepository deliveryRepo;
	@Autowired
	private RewardRepository rewardRepo;
	@Autowired
	private StatusRepository statusRepo;

	@Autowired
	private IncentiveCalculator calc;
	@Autowired
	private IncentiveModel model;
	
	
	public void storeDeliveries(List<Delivery> deliveries) {
		deliveries.forEach(d -> {
			IncentiveStatus status = getRecipient(d.getRecipientId());
			statusRepo.save(status);
			List<Reward> rewards = calc.updateStatusAndComputeRewards(
					model,
					status, 
					d, 
					deliveryRepo.findByRecipientId(d.getRecipientId(), LocalDateTime.now().minusMonths(6)), 
					rewardRepo.findByRecipientId(d.getRecipientId()));
			deliveryRepo.save(d);
			if (rewards.size() > 0) rewardRepo.saveAll(rewards);
		});
	}

	public IncentiveStatus processAction(String recipientId, String action, String outcome) {
		IncentiveStatus status = getRecipient(recipientId);
		List<Reward> rewards = calc.updateStatusAndComputeRewards(model, status, action, outcome, rewardRepo.findByRecipientId(recipientId));
		statusRepo.save(status);
		if (rewards.size() > 0) rewardRepo.saveAll(rewards);
		return getStatus(recipientId);
	}

	private IncentiveStatus getRecipient(String id) {
		IncentiveStatus elem = statusRepo.findById(id).orElse(null);
		if (elem == null) {
			elem = new IncentiveStatus();
			elem.setPoints(0);
			elem.setRecipientId(id);
			elem.setLastUpdate(LocalDateTime.now());
			statusRepo.save(elem);
		}
		return elem;
	}
	
	public List<Reward> findRewards(LocalDateTime from, LocalDateTime to) {
		if (from != null && to != null) {
			return rewardRepo.findByCreatedBetween(from, to);
		} else if (from != null) {
			return rewardRepo.findByCreatedAfter(from);
		} else if (to != null) {
			return rewardRepo.findByCreatedBefore(to);
		} else {
			return rewardRepo.findAll();
		}
	}
	
	public IncentiveStatus getStatus(String recipientId) {
		return getRecipient(recipientId);
	}
	public List<IncentiveStatus> getAll() {
		return statusRepo.findAll();
	}
	
	public IncentiveStatus updateStatus(String recipientId, IncentiveStatus status) {
		IncentiveStatus old = getStatus(recipientId);
		old.setLastUpdate(LocalDateTime.now());
		old.setBlackListEnd(status.getBlackListEnd());
		old.setBlackListStart(status.getBlackListStart());
		old.setPoints(status.getPoints());
		statusRepo.save(old);
		return getRecipient(recipientId);
	}

}
