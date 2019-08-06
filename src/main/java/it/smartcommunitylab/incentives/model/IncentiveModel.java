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

import java.util.List;
import java.util.Map;

/**
 * @author raman
 *
 */
public class IncentiveModel {

	private List<String> locations;
	
	private Map<String, Integer> locationPenalties;
	private Map<String, Integer> locationRewards;
	private Map<String, String> locationPoints;
	
	private List<String> pointRewards;
	private List<String> sequenceRewards;
	private Map<String, Integer> pointRewardMultipliers;
	private Map<String, Integer> sequenceRewardMultipliers;
	
	private Map<String, String> pointRewardTexts;
	private Map<String, String> sequenceRewardTexts;
	
	private Integer blacklistDuration;
	private Integer blacklistThreshold;
	private Integer blacklistSequenceThreshold;
	
	private List<String> actions;
	private Map<String, Integer> actionPenalties;
	private Map<String, Integer> actionRewards;
	private Map<String, String> actionPoints;
	
	public List<String> getLocations() {
		return locations;
	}
	public void setLocations(List<String> locations) {
		this.locations = locations;
	}
	public Map<String, Integer> getLocationPenalties() {
		return locationPenalties;
	}
	public void setLocationPenalties(Map<String, Integer> locationPenalties) {
		this.locationPenalties = locationPenalties;
	}
	public Map<String, Integer> getLocationRewards() {
		return locationRewards;
	}
	public void setLocationRewards(Map<String, Integer> locationRewards) {
		this.locationRewards = locationRewards;
	}
	public Map<String, String> getLocationPoints() {
		return locationPoints;
	}
	public void setLocationPoints(Map<String, String> locationPoints) {
		this.locationPoints = locationPoints;
	}
	public List<String> getPointRewards() {
		return pointRewards;
	}
	public void setPointRewards(List<String> pointRewards) {
		this.pointRewards = pointRewards;
	}
	public List<String> getSequenceRewards() {
		return sequenceRewards;
	}
	public void setSequenceRewards(List<String> sequenceRewards) {
		this.sequenceRewards = sequenceRewards;
	}
	public Map<String, Integer> getPointRewardMultipliers() {
		return pointRewardMultipliers;
	}
	public void setPointRewardMultipliers(Map<String, Integer> pointRewardMultipliers) {
		this.pointRewardMultipliers = pointRewardMultipliers;
	}
	public Map<String, Integer> getSequenceRewardMultipliers() {
		return sequenceRewardMultipliers;
	}
	public void setSequenceRewardMultipliers(Map<String, Integer> sequenceRewardMultipliers) {
		this.sequenceRewardMultipliers = sequenceRewardMultipliers;
	}
	public Map<String, String> getPointRewardTexts() {
		return pointRewardTexts;
	}
	public void setPointRewardTexts(Map<String, String> pointRewardTexts) {
		this.pointRewardTexts = pointRewardTexts;
	}
	public Map<String, String> getSequenceRewardTexts() {
		return sequenceRewardTexts;
	}
	public void setSequenceRewardTexts(Map<String, String> sequenceRewardTexts) {
		this.sequenceRewardTexts = sequenceRewardTexts;
	}
	public Integer getBlacklistDuration() {
		return blacklistDuration;
	}
	public void setBlacklistDuration(Integer blacklistDuration) {
		this.blacklistDuration = blacklistDuration;
	}
	public Integer getBlacklistThreshold() {
		return blacklistThreshold;
	}
	public void setBlacklistThreshold(Integer blacklistThreshold) {
		this.blacklistThreshold = blacklistThreshold;
	}
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public Integer getBlacklistSequenceThreshold() {
		return blacklistSequenceThreshold;
	}
	public void setBlacklistSequenceThreshold(Integer blacklistSequenceThreshold) {
		this.blacklistSequenceThreshold = blacklistSequenceThreshold;
	}
	public Map<String, Integer> getActionPenalties() {
		return actionPenalties;
	}
	public void setActionPenalties(Map<String, Integer> actionPenalties) {
		this.actionPenalties = actionPenalties;
	}
	public Map<String, Integer> getActionRewards() {
		return actionRewards;
	}
	public void setActionRewards(Map<String, Integer> actionRewards) {
		this.actionRewards = actionRewards;
	}
	public Map<String, String> getActionPoints() {
		return actionPoints;
	}
	public void setActionPoints(Map<String, String> actionPoints) {
		this.actionPoints = actionPoints;
	}
	
	
}
