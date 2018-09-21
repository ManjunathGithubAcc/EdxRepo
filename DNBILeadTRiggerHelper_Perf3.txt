//**********************************************************************************************
// Name             : DNBILeadTriggerHelper.cls
// Description      : Class to create and insert the DNB Call record after insertion or updation of Lead
// Created By       : Ponna Gopal, Accenture
// Created Date     : 7/15/2013 
// **********************************************************************************************
// **************************************** Version Update *********************************************************
// Change Request                  Date              Updated By                  Description
// August 2014 HotFix CR        20th August 2014      Yoganand                   August 2014 HotFix
// March  2017 PICO             20th March  2017      Bhuvaneshwari Venkatesh    To bypass DNB Call for PSO Lead Record type
//Added    Null  Check              CR-00143491      Sameer
//******************************************************************************************************************
public with sharing class DNBILeadTriggerHelper {
    
    // **************************************************************************************************************
    // Name       :   addLeadToDNBCallLogQueue
    // Description:   Method to add the records of DNBCall with status as Queued created due to insertion/updation of Lead     
    // Parameters :   void 
    // Returns    :   void
    // ************************************************************************************************************** 
    
    // isFirstRun: var to make sure the code executes only once per execution
    // this will help to avoid more than one DNB_Call__c record creation in case lead is updated multiple times in same execution.
    //public static Boolean isFirstRun = true; commented as part August 2014 Hot Fix
    static Set<Id> IdSetLead;
    
    public static void addLeadToDNBCallLogQueue() {
     //START - Bhuvaneshwari Venkatesh - Changes added for PICO - To bypass DNB Call for PSO Lead Record type
     Id psoRecordType = Schema.SObjectType.Lead.getRecordTypeInfosByName().get('PSO Lead').getRecordTypeId(); //fetch pso lead record type id
    //ENDS - Bhuvaneshwari Venkatesh - Changes added for PICO - To bypass DNB Call for PSO Lead Record type
    
       // Set<Id> IdSetLead = New Set<id>();//Added as part of August 2014 Hotfix
        if(IdSetLead == null)
           IdSetLead = New Set<id>();//Added as part of August 2014 Hotfix
       
        List<DNB_Call__c> listdnbiCallLeadInsert = new List<DNB_Call__c>();
        List<Lead> triggernew = (List<Lead>)Trigger.new;//Added as part of August 2014 Hotfix
        
        for(Lead newLead : triggernew){
            
            String callType = null; 
            
            // Check if the event is update event then assign the trigger.oldMap values in the instance of Lead
            Lead oldLead = trigger.isUpdate ? (Lead) trigger.oldMap.get(newLead.Id) : null;
            
            /* commented as part August 2014 Hot Fix
            if(!isFirstRun) {
                continue;
            }*/
            
            // Check if the call is eligible for DNBDirectCallout
            if(DNBIUtilities.getMakeDNBDirectCalloutFlag(DNBIUtilities.OBJECT_LEAD, newLead.Country__c, false)) {
                
                //if(isFirstRun) { commented as part of August 2014 Hotfix
                    if(Trigger.isInsert && Trigger.isAfter) {
                        //START - Bhuvaneshwari Venkatesh - Changes added for PICO - To bypass DNB Call for PSO Lead Record type
                       // Added null check CR-00143491
                         if(psoRecordType!=null && newLead.recordtypeId!=null && !(newLead.recordtypeId.equals(psoRecordType)))
                         {
                        if(newLead.Company != null && newLead.Country__c != null) {
                            // Check if Lead is inserted via dataloader or integration user and Duns is present then make callType as Detail otherwise Both
                            callType = !DNBIUtilities.isDNBIntegrationUser() && newLead.DUNS__c!=null ? DNBIUtilities.CALL_TYPE_DETAIL : DNBIUtilities.CALL_TYPE_BOTH;
 
                            String callSource = DNBIUtilities.CALL_SOURCE_SYSTEM_LEAD; 
                            
                            // Call the method createDNBICallLog to create a new DNB_Call__c record
                            DNB_Call__c dnbiCallLeadObjInsert = DNBICallAuditUtility.createDNBICallLog(callType, newLead.Company,newLead.Address_1__c,newLead.Address_2__c,newLead.City__c,newLead.Country__c,newLead.DUNS__c,newLead.Postal_Code__c,newLead.State_Province__c,newLead.Phone,DNBIUtilities.OBJECT_LEAD,newLead.ID, callSource,DNBIUtilities.CALL_STATUS_QUEUED,newLead.DNB_IsSynchronous__c );                    
                            dnbiCallLeadObjInsert.Lead__c = newLead.ID;
                            
                            listdnbiCallLeadInsert.add(dnbiCallLeadObjInsert);
                        
                        }
                        }
                                        //ENDS - Bhuvaneshwari Venkatesh - Changes added for PICO - To bypass DNB Call for PSO Lead Record type
                                       System.debug('Count of SOQL Queries used: '+Limits.getQueries());
                    }
                    else if(trigger.isUpdate) {
                        if(newLead.Company != null && newLead.Country__c != null) {
                        
                            // If Name or Address1 or Address2 or City or State or Zip or Country is updated then make callType as Both
                            if(!(IdSetLead.contains(newLead.id)) && ((newLead.Company!=oldLead.Company)||(newLead.Address_1__c!=oldLead.Address_1__c)||(newLead.Address_2__c!=oldLead.Address_2__c)||(newLead.City__c!=oldLead.City__c)||(newLead.State_Province__c!=oldLead.State_Province__c)||(newLead.Country__c!=oldLead.Country__c)||(newLead.Postal_Code__c!=oldLead.Postal_Code__c))) {
                                
                                //if(UserInfo.getProfileId() != '00e80000001BE9K') { 
                                    callType = DNBIUtilities.CALL_TYPE_BOTH;
                                    String callSource = DNBIUtilities.CALL_SOURCE_SYSTEM_LEAD_UPDATE; 
                                        
                                    // Call the method createDNBICallLog to create a new DNB_Call__c record
                                    DNB_Call__c dnbiCallLeadObjInsert = DNBICallAuditUtility.createDNBICallLog(callType, newLead.Company,newLead.Address_1__c,newLead.Address_2__c,newLead.City__c,newLead.Country__c,newLead.DUNS__c,newLead.Postal_Code__c,newLead.State_Province__c,newLead.Phone,DNBIUtilities.OBJECT_LEAD,newLead.ID, callSource,DNBIUtilities.CALL_STATUS_QUEUED,newLead.DNB_IsSynchronous__c );                    
                                    dnbiCallLeadObjInsert.Lead__c = newLead.ID;
                                        
                                    listdnbiCallLeadInsert.add(dnbiCallLeadObjInsert);
                                //}
                                
                            }
                        }
                        
                    }
                //}
            }
            IdSetLead.add(newLead.id); //Added as part of August 2014 Hot Fix 
        }
        
        //isFirstRun = false; commented as part August 2014 Hot Fix

        if(listdnbiCallLeadInsert.size()> 0 && listdnbiCallLeadInsert!= null){  
            //System.debug('Insertion of DNBICallObject due to Updation of Records in Lead Started ');
            
            //Inserting the list of DNB_Call__c records
            Database.Saveresult[] insertRes = Database.insert(listdnbiCallLeadInsert, false); 

            //Calling method to insert the errors, if any in the operation
            CreateApexErrorLog.insertHandledExceptions(null, insertRes, null, null, 'ApexClass', 'DNB_Call__c', 'DNBILeadTriggerHelper');
        
        }
   
  }  
}