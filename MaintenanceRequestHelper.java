public class MaintenanceRequestHelper
{    
    public static void updateWorkOrders(List<Case> reqUpdated)
    {
        String recID = 'InitialVal';
        Map<ID, Integer> dateToReqMap = new Map<ID, Integer>();
        Map<ID, Case> mapToHoldOriReq = new Map<ID, Case>();
        List<Case> newReqs = new List<Case>();
        List<Work_Part__c> newWorkParts = new List<Work_Part__c>();

        List<Work_Part__c> workParts = [SELECT ID, Equipment__r.Maintenance_Cycle__c, Maintenance_Request__c
                                        FROM Work_Part__c
                                        WHERE Maintenance_Request__c IN : reqUpdated
                                        ORDER BY Equipment__r.Maintenance_Cycle__c ASC];
        

		if(workParts.size() > 0)
		{
			for(Work_Part__c workPart: workParts)
			{				
				if (recID != String.valueOf(workPart.Maintenance_Request__c))
				{
					dateToReqMap.put(workPart.Maintenance_Request__c, (Integer)workPart.Equipment__r.Maintenance_Cycle__c);
					recID = workPart.Maintenance_Request__c;					
				}
			}
		}
		
		if (workParts.size() == 0)
		{
			for(Case req: reqUpdated)
			{
				dateToReqMap.put(req.ID, 0);
			}
		}
        
        for (Case req: reqUpdated)
        {
            if ((req.Type == 'Routine Maintenance' || req.Type == 'Repair') && req.Status == 'Closed')
            {
                Case newReq = req.clone(false, true, false, false);
                newReq.Date_Reported__c = System.today();
                newReq.Type = 'Routine Maintenance';
				newReq.Status = 'New';
                newReq.Date_Due__c = System.today()+(dateToReqMap.get(req.ID));
                newReq.Subject = req.Subject + ' Additional Maintenance';
                mapToHoldOriReq.put(req.ID, newReq);
                newReqs.add(newReq);
            }
        }
        insert newReqs;
        
		if(workParts.size() > 0)
		{
			for(Work_Part__c workPart: workParts)
			{
				Work_Part__c newWorkPart = new Work_Part__c();
				newWorkPart = workPart.clone(false, true, false, false);
				newWorkPart.Maintenance_Request__c = mapToHoldOriReq.get(workPart.Maintenance_Request__c).Id;
				newWorkParts.add(newWorkPart);
			}
        insert newWorkParts;
		}
    }
}