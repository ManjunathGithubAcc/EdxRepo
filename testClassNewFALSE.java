@isTest(seeAllData = false)
public class DNBILeadTriggerHelperTest2{
    
    
    static void SetUpData() {
        SalesTestData.customSettingData();
    }
    
    
    static testmethod void Test1(){
        SetUpData() ;
        test.startTest();

        
        Lead lead1 = new Lead();
        lead1.FirstName = 'FirstName';
        
        lead1.LastName = 'LastName';
        lead1.Email = 'first@last.com';
        lead1.RecordTypeId = Schema.SObjectType.Lead.getRecordTypeInfosByName().get('Sales Play Lead Record Type').getRecordTypeId();
        lead1.Company = 'New Test Account 001';
        lead1.Address_1__c = 'test';
        lead1.Address_2__c = 'test';
        lead1.City__c = 'SC';
        lead1.State_Province__c = 'CA';
        lead1.Country__c = 'INDIA';
        lead1.Postal_Code__c = '95051';
        lead1.Employees_Worldwide__c = '100';
        lead1.of_Desktops__c='50';
        lead1.of_Servers__c='190';
        lead1.DULT__c = '567';
        lead1.Record_Source__c = 'AW Eloqua';
        lead1.PFST_Match_Reason__c = '157462698';
        lead1.DUNS__c = 'test';
        lead1.status = '0 New';
        lead1.GEO__c = 'AMER';
        lead1.is_locked__c = false;
        lead1.XFire_Fiscal_Quarter__c = 'Q3';
        lead1.XFire_Fiscal_Year__c = '2019';
        insert lead1;
        
        Lead lead2 = new Lead();
        lead2.FirstName = 'FirstName';
        
        lead2.LastName = 'LastName2';
        lead2.Email = 'second@last.com';
        lead2.RecordTypeId = Schema.SObjectType.Lead.getRecordTypeInfosByName().get('PSO Lead').getRecordTypeId();
        lead2.Company = 'New Test Account 001';
        lead2.Address_1__c = 'test2';
        lead2.Address_2__c = 'test';
        lead2.City__c = 'SC';
        lead2.State_Province__c = 'CA';
        lead2.Country__c = 'INDIA';
        lead2.Postal_Code__c = '95051';
        lead2.Employees_Worldwide__c = '100';
        lead2.of_Desktops__c='50';
        lead2.of_Servers__c='190';
        lead2.DULT__c = '567';
        lead2.Record_Source__c = 'AW Eloqua';
        lead2.PFST_Match_Reason__c = '157462698';
        lead2.DUNS__c = 'test';
        lead2.status = '0 New';
        lead2.GEO__c = 'AMER';
        lead2.is_locked__c = false;
        lead2.XFire_Fiscal_Quarter__c = 'Q3';
        lead2.XFire_Fiscal_Year__c = '2019';
        insert lead2;
        
        lead1.Address_1__c = 'new '+lead1.Address_1__c;
        lead2.Address_1__c = 'new '+lead2.Address_1__c;
        
        update lead1;
        update lead2;
            
        test.stopTest();
        
        List<Lead> leadsTested = [SELECT ID FROM Lead];
        List<DNB_Call__c> dnbCalls = [SELECT ID, Lead__c FROM DNB_Call__c];
        List<Lead> leadsTested2 = [SELECT ID FROM Lead WHERE ID =: dnbCalls[0].Lead__c];
                
        System.assert(leadsTested.size() == 2);
        System.assert(dnbCalls.size() == 1);
        System.assert(dnbCalls[0].Lead__c == leadsTested2[0].Id);        
    }
}