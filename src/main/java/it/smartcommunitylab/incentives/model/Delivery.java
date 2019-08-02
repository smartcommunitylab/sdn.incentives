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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author raman
 *
 */
@Entity
@Table(name="delivery",schema="public")
public class Delivery {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ApiModelProperty("Unique Id od a recipient")
	private String recipientId;
	@ApiModelProperty("Comma-separated list of parcelId")
	private String parcels;
	@ApiModelProperty(value="Type of delivery location", allowableValues = "locker, pickup, home")
	private String locationType;
	
	@ApiModelProperty("Start of defined delivery time slot")
	@Column(name = "timeFrom", columnDefinition = "TIMESTAMP")
	private LocalDateTime timeSlotFrom;
	@ApiModelProperty("End of defined delivery time slot")
	@Column(name = "timeTo", columnDefinition = "TIMESTAMP")
	private LocalDateTime timeSlotTo;
	
	@ApiModelProperty(value="Delivery status", allowableValues = "success, failure, aborted")
	private String status;
	@Column(name = "failure_reason")
	@ApiModelProperty("Reason of failure, if applicable")
	private String failureReason; 
	@ApiModelProperty("Number of attempts to deliver")
	private Integer attempts;
	
	public String getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	public LocalDateTime getTimeSlotFrom() {
		return timeSlotFrom;
	}
	public void setTimeSlotFrom(LocalDateTime timeSlotFrom) {
		this.timeSlotFrom = timeSlotFrom;
	}
	public LocalDateTime getTimeSlotTo() {
		return timeSlotTo;
	}
	public void setTimeSlotTo(LocalDateTime timeSlotTo) {
		this.timeSlotTo = timeSlotTo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	public Integer getAttempts() {
		return attempts;
	}
	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLocationType() {
		return locationType;
	}
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
	public String getParcels() {
		return parcels;
	}
	public void setParcels(String parcels) {
		this.parcels = parcels;
	}

}
