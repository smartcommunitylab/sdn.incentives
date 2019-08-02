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
package it.smartcommunitylab.incentives.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.smartcommunitylab.incentives.model.Delivery;

/**
 * @author raman
 *
 */
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	@Query("SELECT d FROM Delivery d WHERE d.recipientId = ?1 and d.timeSlotFrom > ?2 ORDER BY timeSlotFrom DESC")
	List<Delivery> findByRecipientId(String recipientId, LocalDateTime from);

}
