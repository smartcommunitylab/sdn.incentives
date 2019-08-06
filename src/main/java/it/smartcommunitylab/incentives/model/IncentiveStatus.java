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
package it.smartcommunitylab.incentives.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import it.smartcommunitylab.incentives.service.IncentiveCalculator;

/**
 * @author raman
 *
 */
@Entity
@Table(name="incentive_status",schema="public")
public class IncentiveStatus {

	@Id
	private String recipientId;
	@ApiModelProperty("Incentive points earned")
	private Integer points;
	@JsonIgnore
	private Integer reliabilityIndex;
	
	@ApiModelProperty("Last status update time")
	@Column(name = "last_update", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastUpdate;
	@ApiModelProperty("If recepient is in blacklist, start of the blacklist status")
	@Column(name = "blacklist_start", columnDefinition = "TIMESTAMP")
	private LocalDateTime blackListStart;
	@ApiModelProperty("If recepient is in blacklist, end of the blacklist status")
	@Column(name = "blacklist_end", columnDefinition = "TIMESTAMP")
	private LocalDateTime blackListEnd;
	
	
	public IncentiveStatus() {
		super();
		this.reliabilityIndex = IncentiveCalculator.START_RELIEABILITY_INDEX;
	}
	public String getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	public Integer getPoints() {
		return points == null ? 0 : points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public LocalDateTime getBlackListStart() {
		return blackListStart;
	}
	public void setBlackListStart(LocalDateTime blackListStart) {
		this.blackListStart = blackListStart;
	}
	public LocalDateTime getBlackListEnd() {
		return blackListEnd;
	}
	public void setBlackListEnd(LocalDateTime blackListEnd) {
		this.blackListEnd = blackListEnd;
	}
	public Integer getReliabilityIndex() {
		return reliabilityIndex;
	}
	public void setReliabilityIndex(Integer reliabilityIndex) {
		this.reliabilityIndex = reliabilityIndex;
	}
	
}
