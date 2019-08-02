# Incentives Application

### Reference Documentation

Implementation of an API for incentives related operations. The API should have the following features:

1. POST operation for delivering a summary of parcel deliveries for a day, wherein for each parcel, the following properties are given:

    * unique recipient id (e.g. phone number or customer id) 
    * ids of parcels that were delivered (or at least tried)
    * agreed delivery time slot
    * final status of the delivery (success, failure, aborted)
    * number of delivery attempts
    * in case of failed delivery, description of the failure reason (e.g. recipient not at home, door locked, ...)

The  Incentives module should update recipient specific statistics (incentive status) based on the given information, and decide which recipients have earned a reward (several successful deliveries) and which should be put into the "black list" (multiple failed deliveries, only pickup points offered for a while).

2. GET operation for fetching the list of rewarded recipients, optionally filtered by the reward time stamp. Input parameters

    * begin time (optional), e.g. 2019-06-24T00:00:00. If given, only rewards granted after the begin_time should be returned.
    * end time (optional), e.g. 2019-06-28T23:59:59. If given, only rewards granted before the end_time should be returned.

As a response, a list of reward entries should be returned, wherein each reward entry has the following properties:
    
    * unique recipient id (the same as submitted in the POST above)
    * reward timestamp (e.g. 2019-06-26T15:30:00), i.e. the time when reward was granted.
    * description of the reward. This should be exactly the text that can be communicated to the recipient by SMS, and may include web links for details and/or collecting the reward.

3. GET operation to check the incentive status for a recipient. Input: 
    
    * unique recipient id (e.g. as path parameter)

Output: black list status including the following fields:

    * recipient id
    * current amount of earned incentive points
    * the latest status update time
    * black list timestamp, i.e. the time when recipient was put onto the black list, null="not on the black list"
    * in case the recipient is on the black list, the time when the recipient will be removed from the black list
    
4. PUT (or POST) operation to modify the incentive status for a recipient. Input: The same as the output of 3

5. GET operation to fetch all incentive status entries for all recipients (no input parameters). Output: List of entries defined as the output of 3