global class CPQProductLookupToProduct2Linker implements Database.Batchable<sObject>{

    global Database.QueryLocator start(Database.BatchableContext BC)
    {
        string query = 'SELECT ID, CPQ_Product2_Link__c, CPQ_SKU__c FROM CPQ_Support_Product_Lookup__c WHERE CPQ_Product2_Link__c = NULL AND CPQ_SKU__c != NULL';
        return Database.getQueryLocator(query);
    }

    global void execute(Database.BatchableContext BC, List<CPQ_Support_Product_Lookup__c> scope)
    {
        List <String> splLicenceSKU = new List<String>();
        List <CPQ_Support_Product_Lookup__c> spltoUpdateList = new List <CPQ_Support_Product_Lookup__c>();
        for (CPQ_Support_Product_Lookup__c spl : scope)
        {
            splLicenceSKU.add(spl.CPQ_SKU__c);
        }
        
        List<Product2> products = [SELECT ID, SKU__c FROM Product2 WHERE SKU__c IN: splLicenceSKU];
        
        if(products.size() > 0)
        {
            //Map to hold SKU and associated Product2
            Map<String, Product2> productsMap = new Map<String, Product2>();
            
            //List that holds all CPQ_Support_Product_Lookup__c(SPL) to update
            List<CPQ_Support_Product_Lookup__c> splsToUpdate = new List<CPQ_Support_Product_Lookup__c>();
            
            //Add the Product2 to the productsMap
            for (Product2 prod: products)
            {
                productsMap.put(prod.SKU__c, prod);
            }
            
            //Assign mapped product to the SPL with same SKU and add SPL to the list for update
            for(CPQ_Support_Product_Lookup__c spl: scope)
            {
                Product2 mappedProd = productsMap.get(spl.CPQ_SKU__c);
                if(mappedProd.ID != NULL)
                {
                    spl.CPQ_Product2_Link__c = mappedProd.Id;
                    splsToUpdate.add(spl);                
                }
       
            }
            update splsToUpdate;
        }
    }

    global void finish(Database.BatchableContext BC)
    {
        
    }
}