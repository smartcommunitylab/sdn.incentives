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
package it.smartcommunitylab.incentives.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import it.smartcommunitylab.incentives.model.Delivery;
import it.smartcommunitylab.incentives.model.IncentiveStatus;
import it.smartcommunitylab.incentives.model.Reward;
import it.smartcommunitylab.incentives.service.IncentivesService;

/**
 * @author raman
 *
 */
@RestController
public class IncentivesController {

	@Autowired
	private IncentivesService service;
	
	@ApiOperation(value="Store a list of deliveries")
	@PostMapping("/incentives/deliveries")
	public @ResponseBody ResponseEntity<Void> storeDeliveries(@RequestBody List<Delivery> deliveries) {
		service.storeDeliveries(deliveries);
		return ResponseEntity.ok(null);
	}
	@ApiOperation(value="Read a list of rewards, optionally bounded by the time interval")
	@GetMapping("/incentives/rewards")
	public @ResponseBody ResponseEntity<List<Reward>> findRewards(@RequestParam(required = false, name="begin_time") LocalDateTime from, @RequestParam(required = false, name="end_time") LocalDateTime to) {
		return ResponseEntity.ok(service.findRewards(from, to));
	}
	
	@ApiOperation(value="Read an incentive status of a specific recipient")
	@GetMapping("/incentives/status/{recipientId}")
	public @ResponseBody ResponseEntity<IncentiveStatus> getStatus(@PathVariable String recipientId) {
		return ResponseEntity.ok(service.getStatus(recipientId));
	}

	@ApiOperation(value="Read incentives status of all the recipients")
	@GetMapping("/incentives/status")
	public @ResponseBody ResponseEntity<List<IncentiveStatus>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}
	
	@ApiOperation(value="Overwrite an incentive status of the specified recipient")
	@PutMapping("/incentives/status/{recipientId}")
	public  @ResponseBody ResponseEntity<IncentiveStatus> updateStatus(@PathVariable String recipientId, @RequestBody IncentiveStatus status) {
		return ResponseEntity.ok(service.updateStatus(recipientId, status));
	}
	
	@ApiOperation(value="Perform custom action that rewards/penalizes the recipient")
	@PutMapping("/incentives/action/{recipientId}/{action}/{status}")
	public  @ResponseBody ResponseEntity<IncentiveStatus> performAction(@PathVariable String recipientId, @PathVariable String action, @PathVariable String status) {
		return ResponseEntity.ok(service.processAction(recipientId, action, status));
	}

}
