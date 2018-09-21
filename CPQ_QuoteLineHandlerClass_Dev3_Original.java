/************************************************************************************************************************
Class Name : CPQ_QuoteLineHandlerClass
Created By : Sameer Kulakarni
Release : sprint 3 - CPQ project

*************************************************************************************************************************/
public Class CPQ_QuoteLineHandlerClass{

    //before Insert Method
    public Static void beforeInsertMethod(List<SBQQ__QuoteLine__c>  SBQQQuoteNewList)
    {
       Set<Id> SBQQquoteIDSet = new Set<Id>();
       List<SBQQ__Quote__c> quoteList = new List<SBQQ__Quote__c>();
       
        //Additional Discount data 
        List<Deal_Registration__c> dealRegistrationList = new List<Deal_Registration__c>();
        Map<string,Deal_Registration__c> dealRegistrationORANMap = new Map<String,Deal_Registration__c>();
        Set<Id> quoteIDSet = new Set<Id>();
        Set<String> oranNameSet = new Set<String>();
        List<SBQQ__Quote__c> quoteNewList = new List<SBQQ__Quote__c>();
        Map<String,Partner_Select_Product__c> platFormGrpPartnerselectPrdMap = new Map<String,Partner_Select_Product__c>();
        Set<Id> accountIdSet = new Set<Id>();
        Map<Id,account> accountIDRecordMap = new Map<Id,Account>();
        Set<Id> productIDSet = new Set<Id>();
        Map<Id,Product2> ProductIdRecordMap = new Map<Id,Product2>();
        
        Set<Id> distributorIDSet = new Set<Id>();
        Set<Id> opportunityIdSet = new Set<Id>();
        Map<Id,CPQ_Duplicate_Opportunity__c> duplicateOpportunityIdRecMap = new Map<Id,CPQ_Duplicate_Opportunity__c>();
        Map<Id,SBQQ__Quote__c> SBQQquoteIDRecordMap = new Map<Id,SBQQ__Quote__c>();
        
       
        //Error Message By Pradeep[for CW-190] ---> Start   
        List<CPQ_On_Off_Switch__mdt> cpqOnOffList = [Select Active__c, DeveloperName from CPQ_On_Off_Switch__mdt Where DeveloperName IN ('CPQ_Disable_QuoteLine_Validations')];
        Boolean CPQ_Disable_QuoteLine_ValidationsOnOffSwitch;
        for(CPQ_On_Off_Switch__mdt cpqonoff : cpqOnOffList)
        {
            if(cpqonoff.DeveloperName.equalsIgnoreCase('CPQ_Disable_QuoteLine_Validations'))
            {
                CPQ_Disable_QuoteLine_ValidationsOnOffSwitch = cpqonoff.Active__c;
            }
        }
        
        //OnOffSwitch__c CPQ_Disable_QuoteLine_ValidationsOnOffSwitch = OnOffSwitch__c.getInstance('CPQ_Disable_QuoteLine_Validations');
         String Support_product_not_available_Err, Subscription_Support_ID, Product_is_EOL_Error_Message, Product_not_available_for_segment,Only_SPF_Products_SKUs_are_eligible_for,Valid_support_coverage_was_not_found,Price_Book_entry_not_contain_Subs_Supprt,Price_Book_entry_not_contain_Product;
        If(CPQ_Disable_QuoteLine_ValidationsOnOffSwitch != null && CPQ_Disable_QuoteLine_ValidationsOnOffSwitch == false){ 
            List < CPQ_Message__mdt > cpqMsgList = [Select CPQ_Value__c, DeveloperName from CPQ_Message__mdt WHERE DeveloperName IN('Support_product_not_available_Err', 'Subscription_Support_ID', 'Product_is_EOL_Error_Message', 'Product_not_available_for_segment','Only_SPF_Products_SKUs_are_eligible_for','Valid_support_coverage_was_not_found','Price_Book_entry_not_contain_Product','Price_Book_entry_not_contain_Subs_Supprt') and CPQ_Flag__c=true];
            for (CPQ_Message__mdt rec: cpqMsgList) {
                if (rec.DeveloperName.equalsIgnoreCase('Support_product_not_available_Err')) {
                    Support_product_not_available_Err = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Subscription_Support_ID')) {
                    Subscription_Support_ID = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Product_is_EOL_Error_Message')) {
                    Product_is_EOL_Error_Message = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Product_not_available_for_segment')) {
                    Product_not_available_for_segment = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Only_SPF_Products_SKUs_are_eligible_for')) {
                    Only_SPF_Products_SKUs_are_eligible_for= rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Valid_support_coverage_was_not_found')) {
                    Valid_support_coverage_was_not_found= rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Price_Book_entry_not_contain_Product')) {
                    Price_Book_entry_not_contain_Product= rec.CPQ_Value__c;
                }/*
                if (rec.DeveloperName.equalsIgnoreCase('Price_Book_entry_not_contain_Subs_Supprt')) {
                    Price_Book_entry_not_contain_Subs_Supprt= rec.CPQ_Value__c;
                }*/
            }
        }
        //Error Message By Pradeep[for CW-190] ---> End 
        for(SBQQ__QuoteLine__c quoteLine : SBQQQuoteNewList){
           if(quoteLine.SBQQ__Quote__c != null){
               SBQQquoteIDSet.add(quoteLine.SBQQ__Quote__c);
               productIDSet.add(quoteLine.SBQQ__Product__c);
           }
        }
        
        
        
        if(!SBQQquoteIDSet.isEmpty()){
            quoteList =[select id,SBQQ__Opportunity2__r.AccountID,CPQ_NO_ADPlusDiscount__c,SBQQ__PriceBook__c,CPQ_Reseller__c,CPQ_Distributor__c,SBQQ__Account__c,CPQ_Oran__c,SBQQ__Opportunity2__c from SBQQ__Quote__c where id In :SBQQquoteIDSet ];
        }
        if(!quoteList.isEmpty()){
            system.debug('quoteList'+quoteList);
             for(SBQQ__Quote__c quote : quoteList){
                 SBQQquoteIDRecordMap.put(quote.id,quote);
                 
                if(quote.SBQQ__Opportunity2__c != null){
                    opportunityIdSet.add(quote.SBQQ__Opportunity2__c);
                }
                
                quoteIDSet.add(quote.id);
                if(quote.CPQ_Reseller__c != null && quote.CPQ_Distributor__c != null  && quote.CPQ_Reseller__c != quote.SBQQ__Account__c ){
                    if(quote.CPQ_Oran__c != null && quote.CPQ_Oran__c != ''){
                        oranNameSet.add(quote.CPQ_Oran__c);
                        quoteNewList.add(quote);
                        distributorIDSet.add(quote.CPQ_Distributor__c);
                    }
                    else {
                        quote.CPQ_NO_ADPlusDiscount__c = true;
                    }
                }
                else {
                    quote.CPQ_NO_ADPlusDiscount__c = true;
                }
                
                if(quote.SBQQ__Opportunity2__r.AccountID != null ){
                    accountIdSet.add(quote.SBQQ__Opportunity2__r.AccountID);
                }
            }
            
            if(!accountIdSet.isEmpty() || !distributorIDSet.isEmpty()){
                 for(account acc : [select id,name,Partner_ID__c from Account  where (id IN : accountIdSet or id IN :distributorIDSet) ]){
                    accountIDRecordMap.put(acc.id,acc);
                }
            }
            
            if(!opportunityIdSet.isEmpty()){
                    for(CPQ_Duplicate_Opportunity__c duplicate :[Select id,name,Duplicate_Opportunity__c,Opportunity_ID__c from CPQ_Duplicate_Opportunity__c where Duplicate_Opportunity__c IN :opportunityIdSet]){
                        
                        duplicateOpportunityIdRecMap.put(duplicate.Duplicate_Opportunity__c,duplicate);
                    }
                }
            
           
            if(!productIDSet.isEmpty()){
                ProductIdRecordMap = new Map<Id,Product2>([select id,Platform_Group__c from Product2 where id In :productIDSet ]);
            }
            system.debug('SBQQQuoteNewList'+SBQQQuoteNewList);
            system.debug('SBQQquoteIDRecordMap'+SBQQquoteIDRecordMap);
            system.debug('duplicateOpportunityIdRecMap'+duplicateOpportunityIdRecMap);
            system.debug('ProductIdRecordMap'+ProductIdRecordMap);
            CPQ_quoteLineHelperClass.ValidationForAdditionalDiscount(SBQQQuoteNewList,SBQQquoteIDRecordMap,duplicateOpportunityIdRecMap,ProductIdRecordMap);
        }


        // 190
        system.debug('::Pradeep:: Start');
        If(CPQ_Disable_QuoteLine_ValidationsOnOffSwitch != null && CPQ_Disable_QuoteLine_ValidationsOnOffSwitch == false){ 
            Set < Id > productIdSet1 = new Set < Id > ();
            Set < Id > QuoteIdSet1 = new Set < Id > ();
            Map < id, List<SBQQ__QuoteLine__c> > productIdQuotelineMap = new Map < id, List<SBQQ__QuoteLine__c> > ();
            Set < id > isProductSupportSet = new Set < id > ();
            Map < Id, Set < String >> quoteIdLineItemsParentSKU = new Map < Id, Set < String >> ();
            Set<Id> quoteContainingSupportProductSet = new Set<Id>();
            List<PricebookEntry> priceBookEntryList = new List<PricebookEntry>();
            Map<id,id> quoteIdPricebbokIdMap = new Map<id,id>();
            Map<id,id> pricebbokIdQuoteIdMap = new Map<id,id>();
            Set<String> PricebookIdProdIdSet = new Set<String>();
            Set<String> priceBookEntryPricebookIdProdIdSet = new Set<String>();
            Set<id> invalidProductIdSet = new Set<id>();




        //map<id,SBQQ__Quote__c> quoteMap = new Map<Id,SBQQ__Quote__c>();
        Map < id, Set < Id >> quoteProductMap = new Map < id, Set < Id >> ();
        system.debug('::::Pradeep::: SBQQQuoteNewList ' + SBQQQuoteNewList);
        system.debug('::::Pradeep::: SBQQQuoteNewList.size ' + SBQQQuoteNewList.size());

        for (SBQQ__QuoteLine__c quoteLine: SBQQQuoteNewList) {
            productIdSet1.add(quoteLine.SBQQ__Product__c);
            QuoteIdSet1.add(quoteLine.SBQQ__Quote__c);
            system.debug('::::Pradeep::: quoteLine ' + quoteLine);
                if(quoteLine.SBQQ__Product__c!=Subscription_Support_ID){
                    quoteContainingSupportProductSet.add(quoteLine.SBQQ__Quote__c);
                }
                List<SBQQ__QuoteLine__c> tempQuoteLineList = new List<SBQQ__QuoteLine__c> ();
                if(productIdQuotelineMap!=null && productIdQuotelineMap.containsKey(quoteLine.SBQQ__Product__c)){
                  tempQuoteLineList = productIdQuotelineMap.get(quoteLine.SBQQ__Product__c);
                  tempQuoteLineList.add(quoteLine);
                  productIdQuotelineMap.put(quoteLine.SBQQ__Product__c, tempQuoteLineList);
                }else {
                  tempQuoteLineList.add(quoteLine);
                  productIdQuotelineMap.put(quoteLine.SBQQ__Product__c, tempQuoteLineList);
                }
                
                if (quoteProductMap.containsKey(quoteLine.SBQQ__Quote__c)) {
                    set < id > productIdTemp = new set < id > ();
                    productIdTemp = quoteProductMap.get(quoteLine.SBQQ__Quote__c);
                    productIdTemp.add(quoteLine.SBQQ__Product__c);
                    quoteProductMap.put(quoteLine.SBQQ__Quote__c, productIdTemp);
                } else {
                    set < id > productIdTemp = new set < id > ();
                    productIdTemp.add(quoteLine.SBQQ__Product__c);
                    quoteProductMap.put(quoteLine.SBQQ__Quote__c, productIdTemp);
                }
   /* system.debug('Pradeep:::>> quoteLine.CPQ_SKU_Type__c'+quoteLine.CPQ_SKU_Type__c);
    system.debug('Pradeep:::>> quoteLine.CPQ_Valid_Combination__c'+quoteLine.CPQ_Valid_Combination__c);

                if((quoteLine.CPQ_SKU_Type__c=='SNS' && quoteline.CPQ_Valid_Combination__c==false)){
                    if(Valid_support_coverage_was_not_found!=null && Valid_support_coverage_was_not_found!=''){
                        quoteLine.addError(Valid_support_coverage_was_not_found + ' - ' + quoteLine.CPQ_Parent_SKU__c);
                    }
                }*/

            system.debug('::::Pradeep::: quoteLine.CPQ_Parent_SKU__c >> ' + quoteLine.CPQ_Parent_SKU__c);

        }

            map < id, product2 > productMap = new Map < Id, Product2 > ([Select id, CPQ_Quotable__c, CPQ_Geo__c, CurrencyIsoCode, CPQ_Route_To_Market__c, CPQ_License_Type__c, CPQ_SKU_Type__c, SKU__c, CPQ_Is_Compliant__c, CPQ_Customer_Segment__c, CPQ_EOL_DATE__c,CPQ_Quote_Type__c from product2 where id in: productIdSet1 and id !=: Subscription_Support_ID]);
            map < id, SBQQ__Quote__c > quoteMap = new Map < Id, SBQQ__Quote__c > ([Select id,SBQQ__PriceBook__c, (Select id, name, SBQQ__Product__c, CPQ_Parent_SKU__c from SBQQ__LineItems__r), CPQ_GEO__c, CurrencyIsoCode, CPQ_RTM__c, CPQ_Account_Type__c from SBQQ__Quote__c where id in: QuoteIdSet1]);

        system.debug('::::Pradeep::: quoteMap >> ' + quoteMap);

            //PriceBook Entry Validations --Start
            for(id qId : quoteMap.keyset()){
                quoteIdPricebbokIdMap.put(qId,quoteMap.get(qid).SBQQ__PriceBook__c);
                pricebbokIdQuoteIdMap.put(quoteMap.get(qid).SBQQ__PriceBook__c,qId);
                for(id prodId : quoteProductMap.get(qid)){
                    PricebookIdProdIdSet.add(quoteMap.get(qid).SBQQ__PriceBook__c+';'+prodId);
                }

            }   

            priceBookEntryList = [Select id,Pricebook2Id,Product2Id from PricebookEntry where (Product2Id=:Subscription_Support_ID or Product2Id in: productMap.keyset()) and Pricebook2Id in: quoteIdPricebbokIdMap.values()];

            
                for(PricebookEntry pbEntry : priceBookEntryList){
                    priceBookEntryPricebookIdProdIdSet.add(pbEntry.Pricebook2Id+';'+pbEntry.Product2Id);
                }

                for(String PricebookIdProdIdIter : PricebookIdProdIdSet){
                   if( !priceBookEntryPricebookIdProdIdSet.contains(PricebookIdProdIdIter)){
                        invalidProductIdSet.add(Id.valueOf(PricebookIdProdIdIter.split(';')[1]));
                   }
                }

                for(id prodIds : invalidProductIdSet){
                    if(productIdQuotelineMap!=null && productIdQuotelineMap.get(prodIds)!=null){
                        if(prodIds==Subscription_Support_ID){
                           // productIdQuotelineMap.get(prodIds).addError(Price_Book_entry_not_contain_Subs_Supprt);
                        }else{
                            //Adding extra validation data - Nikhil
                          for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(prodIds)){
                            qLineItr.addError(Price_Book_entry_not_contain_Product + ' - ' + productMap.get(prodIds).SKU__c);
                          }
                        }
                    }
                }


            
            //PriceBook Entry Validations --end

            // subscription/support logic--->Start

        /*Set < Id > prodOptionConfiguredSKU_Set = new Set < Id > ();
       

        for (product2 productIter: productMap.Values()) {
            if (productIter.CPQ_License_Type__c != null && productIter.CPQ_License_Type__c.equalsIgnorecase('Perpetual') &&
                productIter.CPQ_SKU_Type__c != null && productIter.CPQ_SKU_Type__c.equalsIgnorecase('License') && productIter.CPQ_Is_Compliant__c != null && productIter.CPQ_Is_Compliant__c == false) {
                isProductSupportSet.add(productIter.id);
            }
        }

         for (SBQQ__ProductOption__c prodOptionItr: [select id, SBQQ__OptionalSKU__c, SBQQ__ConfiguredSKU__c from SBQQ__ProductOption__c where SBQQ__OptionalSKU__c =: Subscription_Support_ID and SBQQ__ConfiguredSKU__c in:isProductSupportSet]) {
            if (prodOptionItr.SBQQ__ConfiguredSKU__c != null) {
                prodOptionConfiguredSKU_Set.Add(prodOptionItr.SBQQ__ConfiguredSKU__c);
            }
        }


        system.debug(':::Pradeep::: isProductSupportSet >> ' + isProductSupportSet);
        system.debug(':::Pradeep::: prodOptionConfiguredSKU_Set >> ' + prodOptionConfiguredSKU_Set);

        for (id prodId: productIdQuotelineMap.keyset()) {
            system.debug(':::Pradeep::: prodId >> ' + prodId);

                if (productIdQuotelineMap.get(prodId).SBQQ__Product__c != Subscription_Support_ID) {
                    if ((isProductSupportSet.contains(prodId)) && !(isProductSupportSet.contains(prodId) && prodOptionConfiguredSKU_Set.Contains(prodId))) {
                        if(Support_product_not_available_Err!=null && Support_product_not_available_Err!=''){
                        productIdQuotelineMap.get(prodId).addError(Support_product_not_available_Err + ' - ' + productMap.get(prodId).SKU__c);
                        }
                    }
                }

        }
        // subscription/support logic--->End*/




        for (id quoteId: quoteProductMap.keyset()) {
            for (id productId: quoteProductMap.get(quoteId)) {

                    if (productId == Subscription_Support_ID) {
                        continue;
                    }
                    system.debug(':::pradeep:::>>>productMap ' + productMap);
                    system.debug(':::pradeep:::>>>quoteMap ' + quoteMap);
                    system.debug(':::pradeep:::>>>productId ' + productId);
                    system.debug(':::pradeep:::>>>productMap.get(productId) ' + productMap.get(productId));
                   
                    if (quoteMap.containsKey(quoteId) && quoteMap.get(quoteId).CPQ_Account_Type__c != null) {
                        if (!(quoteMap.get(quoteId).CPQ_Account_Type__c.equalsIgnorecase('Academic'))) {
                            if (productMap.get(productId) != null && !(productMap.get(productId).CPQ_Customer_Segment__c != null && productMap.get(productId).CPQ_Customer_Segment__c.containsIgnoreCase(quoteMap.get(quoteId).CPQ_Account_Type__c))) {
                                if(Product_not_available_for_segment!=null && Product_not_available_for_segment!=''){
                                    //productIdQuotelineMap.get(productId).addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                    /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                      qLineItr.addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                    }*/
                                    for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Product_not_available_for_segment + ' - '+temp + productMap.get(productId).SKU__c);
                                }
                                }
                            }
                        } else {
                            if (productMap.get(productId)!=null && !((productMap.get(productId).CPQ_Customer_Segment__c != null && productMap.get(productId) != null && productMap.get(productId).CPQ_Customer_Segment__c.containsIgnoreCase('Academic')) || (productMap.get(productId) != null && productMap.get(productId).CPQ_Customer_Segment__c != null && productMap.get(productId).CPQ_Customer_Segment__c.containsIgnoreCase('Commercial')))) {
                                if(Product_not_available_for_segment!=null && Product_not_available_for_segment!=''){
                                    //productIdQuotelineMap.get(productId).addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                  /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                      qLineItr.addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                    }*/
                                    for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Product_not_available_for_segment + ' - '+temp + productMap.get(productId).SKU__c);
                                }
                                }
                            }
                        }
                    }


                    if (productMap.get(productId) != null && productMap.get(productId).CPQ_Quotable__c != true) {
                        if(Product_is_EOL_Error_Message!=null && Product_is_EOL_Error_Message!=''){
                            //productIdQuotelineMap.get(productId).addError(Product_is_EOL_Error_Message + ' - ' + productMap.get(productId).SKU__c);
                        /*  for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                            qLineItr.addError(Product_is_EOL_Error_Message + ' - ' + productMap.get(productId).SKU__c);
                                    }*/

                                    for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Product_is_EOL_Error_Message + ' - '+temp+ productMap.get(productId).SKU__c);
                                    }
                                    
                        }

                    }
                    
                    if (productMap.get(productId) != null && !(productMap.get(productId).CPQ_Quote_Type__c!=null 
                        && productMap.get(productId).CPQ_Quote_Type__c.containsIgnoreCase('SPF'))) {
                            if(Only_SPF_Products_SKUs_are_eligible_for!=null && Only_SPF_Products_SKUs_are_eligible_for!=''){
                                //productIdQuotelineMap.get(productId).addError(Only_SPF_Products_SKUs_are_eligible_for + ' - ' + productMap.get(productId).SKU__c);
                              /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                qLineItr.addError(Only_SPF_Products_SKUs_are_eligible_for + ' - ' + productMap.get(productId).SKU__c);
                              }*/

                              for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Only_SPF_Products_SKUs_are_eligible_for + ' - '+temp + productMap.get(productId).SKU__c);
                                }
                            }

                    }



                }
            }
        }

        //Pradeep[for CW-190] ---> End
        
        // Changes for CW-87 START
            CPQ_quoteLineHelperClass.populateRegionalDiscount(SBQQQuoteNewList);
        // Changes for CW-87 END
        

    }


    //before Update Method
    public Static void beforeUpdateMethod(Map<id,SBQQ__QuoteLine__c> SBQQQuoteNewMap,Map<id,SBQQ__QuoteLine__c> SBQQQuoteOldMap)
    {
        if(!CPQ_recursive.recursiveQuoteLineBeforeTrigger)
        {
               Set<Id> SBQQquoteIDSet = new Set<Id>();
       List<SBQQ__Quote__c> quoteList = new List<SBQQ__Quote__c>();
       
        //Additional Discount data 
        List<Deal_Registration__c> dealRegistrationList = new List<Deal_Registration__c>();
        Map<string,Deal_Registration__c> dealRegistrationORANMap = new Map<String,Deal_Registration__c>();
        Set<Id> quoteIDSet = new Set<Id>();
        Set<String> oranNameSet = new Set<String>();
         Set<Id> opportunityIdSet = new Set<Id>();
        List<SBQQ__Quote__c> quoteNewList = new List<SBQQ__Quote__c>();
        Map<String,Partner_Select_Product__c> platFormGrpPartnerselectPrdMap = new Map<String,Partner_Select_Product__c>();
        Set<Id> accountIdSet = new Set<Id>();
        Map<Id,account> accountIDRecordMap = new Map<Id,Account>();
        Set<Id> productIDSet = new Set<Id>();
        Map<Id,Product2> ProductIdRecordMap = new Map<Id,Product2>();
        Map<Id,SBQQ__Quote__c> quoteIDRecordMap = new Map<Id,SBQQ__Quote__c>();
        Set<Id> distributorIDSet = new Set<Id>();
        Map<Id,CPQ_Duplicate_Opportunity__c> duplicateOpportunityIdRecMap = new Map<Id,CPQ_Duplicate_Opportunity__c>();
        //Error Message By Pradeep[for CW-190] ---> Start   
        List<CPQ_On_Off_Switch__mdt> cpqOnOffList = [Select Active__c, DeveloperName from CPQ_On_Off_Switch__mdt Where DeveloperName IN ('CPQ_Disable_QuoteLine_Validations')];
        Boolean CPQ_Disable_QuoteLine_ValidationsOnOffSwitch;
        for(CPQ_On_Off_Switch__mdt cpqonoff : cpqOnOffList)
        {
            if(cpqonoff.DeveloperName.equalsIgnoreCase('CPQ_Disable_QuoteLine_Validations'))
            {
                CPQ_Disable_QuoteLine_ValidationsOnOffSwitch = cpqonoff.Active__c;
            }
        }
        
        //OnOffSwitch__c CPQ_Disable_QuoteLine_ValidationsOnOffSwitch = OnOffSwitch__c.getInstance('CPQ_Disable_QuoteLine_Validations');
        String Support_product_not_available_Err, Subscription_Support_ID, Product_is_EOL_Error_Message, Product_not_available_for_segment,Only_SPF_Products_SKUs_are_eligible_for,Valid_support_coverage_was_not_found,Price_Book_entry_not_contain_Product,Price_Book_entry_not_contain_Subs_Supprt;
        If(CPQ_Disable_QuoteLine_ValidationsOnOffSwitch != null && CPQ_Disable_QuoteLine_ValidationsOnOffSwitch == false)
        { 
            List < CPQ_Message__mdt > cpqMsgList = [Select CPQ_Value__c, DeveloperName from CPQ_Message__mdt WHERE DeveloperName IN('Support_product_not_available_Err', 'Subscription_Support_ID', 'Product_is_EOL_Error_Message', 'Product_not_available_for_segment','Only_SPF_Products_SKUs_are_eligible_for','Valid_support_coverage_was_not_found','Price_Book_entry_not_contain_Product','Price_Book_entry_not_contain_Subs_Supprt') and CPQ_Flag__c=true];
            for (CPQ_Message__mdt rec: cpqMsgList) {
                if (rec.DeveloperName.equalsIgnoreCase('Support_product_not_available_Err')) {
                    Support_product_not_available_Err = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Subscription_Support_ID')) {
                    Subscription_Support_ID = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Product_is_EOL_Error_Message')) {
                    Product_is_EOL_Error_Message = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Product_not_available_for_segment')) {
                    Product_not_available_for_segment = rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Only_SPF_Products_SKUs_are_eligible_for')) {
                    Only_SPF_Products_SKUs_are_eligible_for= rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Valid_support_coverage_was_not_found')) {
                    Valid_support_coverage_was_not_found= rec.CPQ_Value__c;
                }
                if (rec.DeveloperName.equalsIgnoreCase('Price_Book_entry_not_contain_Product')) {
                    Price_Book_entry_not_contain_Product= rec.CPQ_Value__c;
                }/*
                if (rec.DeveloperName.equalsIgnoreCase('Price_Book_entry_not_contain_Subs_Supprt')) {
                    Price_Book_entry_not_contain_Subs_Supprt= rec.CPQ_Value__c;
                }*/
            }
        }
        //Error Message By Pradeep[for CW-190] ---> End 
        for(SBQQ__QuoteLine__c quoteLine : SBQQQuoteNewMap.values())
        {
           if(quoteLine.SBQQ__Quote__c != null && quoteLine.SBQQ__Quote__c != SBQQQuoteOldMap.get(quoteLine.id).SBQQ__Quote__c
           || (quoteLine.SBQQ__Discount__c != SBQQQuoteOldMap.get(quoteLine.id).SBQQ__Discount__c)
           )
           {
               SBQQquoteIDSet.add(quoteLine.SBQQ__Quote__c);
               productIDSet.add(quoteLine.SBQQ__Product__c);
           }
           
        }
        
        if(!SBQQquoteIDSet.isEmpty()){
            quoteIDRecordMap = new Map<Id,SBQQ__Quote__c>([select id,SBQQ__Opportunity2__r.AccountID,CPQ_NO_ADPlusDiscount__c,CPQ_Reseller__c,CPQ_Distributor__c,SBQQ__Account__c,CPQ_Oran__c from SBQQ__Quote__c where id In :SBQQquoteIDSet ]);
        }
        System.debug('############ Data test ' + quoteIDRecordMap);
        System.debug('############ Data productIDSet ' + productIDSet);
        if(!quoteIDRecordMap.isEmpty() && quoteIDRecordMap.values() != null)
        {
             
            
            for(SBQQ__QuoteLine__c quoteLine : SBQQQuoteNewMap.values()){
                if(quoteLine.SBQQ__Quote__c != null && quoteIDRecordMap.containsKey(quoteLine.SBQQ__Quote__c)){
                    SBQQ__Quote__c quote = quoteIDRecordMap.get(quoteLine.SBQQ__Quote__c);
                     quoteIDSet.add(quote.id);
                    if(quote.CPQ_Reseller__c != null && quote.CPQ_Distributor__c != null  && quote.CPQ_Reseller__c != quote.SBQQ__Account__c  ){
                        if(quote.CPQ_Oran__c != null && quote.CPQ_Oran__c != ''){
                            oranNameSet.add(quote.CPQ_Oran__c);
                            quoteNewList.add(quote);
                            distributorIDSet.add(quote.CPQ_Distributor__c);
                        }
                        else {
                            quote.CPQ_NO_ADPlusDiscount__c = true;
                            if(quoteLine.SBQQ__PartnerDiscount__c != null){
                                quoteLine.SBQQ__PartnerDiscount__c = null;
                            }
                        }
                    }
                    else {
                        quote.CPQ_NO_ADPlusDiscount__c = true;
                        if(quoteLine.SBQQ__PartnerDiscount__c != null){
                            quoteLine.SBQQ__PartnerDiscount__c = null;
                        }
                    }
                    
                    if(quote.SBQQ__Opportunity2__r.AccountID != null ){
                        accountIdSet.add(quote.SBQQ__Opportunity2__r.AccountID);
                    }
                    if(quote.SBQQ__Opportunity2__c != null)
                    {
                        
                         opportunityIdSet.add(quote.SBQQ__Opportunity2__c);
                    }
                    
                }
            }
            
            
            if(!accountIdSet.isEmpty() || !distributorIDSet.isEmpty()){
                 for(account acc : [select id,name from Account where  (id =: accountIdSet or id =: distributorIDSet ) ]){

                    accountIDRecordMap.put(acc.id,acc);
                }
            }
             if(!opportunityIdSet.isEmpty())
            {
                    for(CPQ_Duplicate_Opportunity__c duplicate :[Select id,name,Duplicate_Cpq_Opportunity__c,Duplicate_Opportunity__c from CPQ_Duplicate_Opportunity__c where Duplicate_Cpq_Opportunity__c IN :opportunityIdSet]){
                        
                        duplicateOpportunityIdRecMap.put(duplicate.Duplicate_Cpq_Opportunity__c,duplicate);
                    }
            }
             if(!productIDSet.isEmpty()){
                    ProductIdRecordMap = new Map<Id,Product2>([select id,Platform_Group__c from Product2 where id In :productIDSet ]);
                }
            if(!oranNameSet.isEmpty())
            {
            
                 dealRegistrationList = [Select id,Registration_Status__c,Orion_Deal_Registration_Expiration_Date__c,XFire_Winning_Reseller__c,XFire_Winning_Distributor__c,Company__c,name,Active_Partner_Account__c,Incentive_Program__r.Orion_No_AD_Discount_Flag__c,Preferred_Distributor__c,Orion_Preferred_Disti_ID__c,ORAN__c,(select id,Platform_Group__c,Eligible_for_Benifits__c,Approved__c,Discount__c from PartnerSelects__r) from Deal_Registration__c where ORAN__c IN :oranNameSet ];
                if(!dealRegistrationList.isEmpty()){
                    for(Deal_Registration__c dealRec :dealRegistrationList){
                    dealRegistrationORANMap.put(dealRec.ORAN__c,dealRec) ;
                        for(Partner_Select_Product__c partnerSelectRec :dealRec.PartnerSelects__r){
                            platFormGrpPartnerselectPrdMap.put(partnerSelectRec.Platform_Group__c,partnerSelectRec);
                        }
                    }
                }
                
               
                if(!recursive.CPQ_avoidRecusriveCall){
                 CPQ_quoteLineHelperClass.applyAdditionalDiscount(quoteNewList,dealRegistrationORANMap,SBQQQuoteNewMap.values(),platFormGrpPartnerselectPrdMap,accountIDRecordMap,ProductIdRecordMap);
                }
            }
            System.debug('############ test data ' + SBQQQuoteNewMap.values());
            System.debug('############ test quoteIDRecordMap ' + quoteIDRecordMap);
            System.debug('############ test duplicateOpportunityIdRecMap ' + duplicateOpportunityIdRecMap);
            System.debug('############ test data ProductIdRecordMap ' + ProductIdRecordMap);
           CPQ_quoteLineHelperClass.ValidationForAdditionalDiscount(SBQQQuoteNewMap.values(),quoteIDRecordMap,duplicateOpportunityIdRecMap,ProductIdRecordMap);
 
        }
        
        //Pradeep[for CW-190] ---> Start

        system.debug('::Pradeep:: Start');
        If(CPQ_Disable_QuoteLine_ValidationsOnOffSwitch != null && CPQ_Disable_QuoteLine_ValidationsOnOffSwitch == false){ 
            Set < Id > productIdSet1 = new Set < Id > ();
            Set < Id > QuoteIdSet1 = new Set < Id > ();
           Map < id, List<SBQQ__QuoteLine__c> > productIdQuotelineMap = new Map < id, List<SBQQ__QuoteLine__c> > ();
            Set < id > isProductSupportSet = new Set < id > ();
            Map < Id, Set < String >> quoteIdLineItemsParentSKU = new Map < Id, Set < String >> ();
            Set<Id> quoteContainingSupportProductSet = new Set<Id>();
            List<PricebookEntry> priceBookEntryList = new List<PricebookEntry>();
            Map<id,id> quoteIdPricebbokIdMap = new Map<id,id>();
            Map<id,id> pricebbokIdQuoteIdMap = new Map<id,id>();
            Set<String> PricebookIdProdIdSet = new Set<String>();
            Set<String> priceBookEntryPricebookIdProdIdSet = new Set<String>();
            Set<id> invalidProductIdSet = new Set<id>();

        //map<id,SBQQ__Quote__c> quoteMap = new Map<Id,SBQQ__Quote__c>();
        Map < id, Set < Id >> quoteProductMap = new Map < id, Set < Id >> ();

        for (SBQQ__QuoteLine__c quoteLine: SBQQQuoteNewMap.values()) {
            productIdSet1.add(quoteLine.SBQQ__Product__c);
            QuoteIdSet1.add(quoteLine.SBQQ__Quote__c);
             List<SBQQ__QuoteLine__c> tempQuoteLineList = new List<SBQQ__QuoteLine__c> ();
                if(productIdQuotelineMap!=null && productIdQuotelineMap.containsKey(quoteLine.SBQQ__Product__c)){
                  tempQuoteLineList = productIdQuotelineMap.get(quoteLine.SBQQ__Product__c);
                  tempQuoteLineList.add(quoteLine);
                  productIdQuotelineMap.put(quoteLine.SBQQ__Product__c, tempQuoteLineList);
                }else {
                  tempQuoteLineList.add(quoteLine);
                  productIdQuotelineMap.put(quoteLine.SBQQ__Product__c, tempQuoteLineList);
                }
            //productIdQuotelineMap.put(quoteLine.SBQQ__Product__c, quoteLine);

            if (quoteProductMap.containsKey(quoteLine.SBQQ__Quote__c)) {
                set < id > productIdTemp = new set < id > ();
                productIdTemp = quoteProductMap.get(quoteLine.SBQQ__Quote__c);
                productIdTemp.add(quoteLine.SBQQ__Product__c);
                quoteProductMap.put(quoteLine.SBQQ__Quote__c, productIdTemp);
            } else {
                set < id > productIdTemp = new set < id > ();
                productIdTemp.add(quoteLine.SBQQ__Product__c);
                quoteProductMap.put(quoteLine.SBQQ__Quote__c, productIdTemp);
            }

              /*  if((quoteLine.CPQ_SKU_Type__c!=null && quoteLine.CPQ_SKU_Type__c.EqualsIgnorecase('SNS') && quoteline.CPQ_Valid_Combination__c==false)){
                    if(Valid_support_coverage_was_not_found!=null && Valid_support_coverage_was_not_found!=''){
                    quoteLine.addError(Valid_support_coverage_was_not_found + ' - ' + quoteLine.CPQ_Parent_SKU__c);
                    }
                }*/
                

        }

            map < id, product2 > productMap = new Map < Id, Product2 > ([Select id,CPQ_Quote_Type__c, CPQ_Quotable__c, CPQ_Geo__c, CurrencyIsoCode, CPQ_Route_To_Market__c, CPQ_License_Type__c, CPQ_SKU_Type__c, SKU__c, CPQ_Is_Compliant__c, CPQ_Customer_Segment__c, CPQ_EOL_DATE__c from product2 where id in: productIdSet1 and id !=: Subscription_Support_ID]);
            map < id, SBQQ__Quote__c > quoteMap = new Map < Id, SBQQ__Quote__c > ([Select id,SBQQ__PriceBook__c, (Select id, name, SBQQ__Product__c, CPQ_Parent_SKU__c from SBQQ__LineItems__r), CPQ_GEO__c, CurrencyIsoCode, CPQ_RTM__c, CPQ_Account_Type__c from SBQQ__Quote__c where id in: QuoteIdSet1]);
            //productMap = [Select id,CPQ_Geo__c,CurrencyIsoCode,CPQ_Route_To_Market__c,CPQ_Customer_Segment__c,CPQ_EOL_DATE__c from product2 where id in:productIdSet1];
            system.debug('::::Pradeep::: quoteMap >> ' + quoteMap);


                        //PriceBook Entry Validations --Start
            for(id qId : quoteMap.keyset()){
                quoteIdPricebbokIdMap.put(qId,quoteMap.get(qid).SBQQ__PriceBook__c);
                pricebbokIdQuoteIdMap.put(quoteMap.get(qid).SBQQ__PriceBook__c,qId);
                for(id prodId : quoteProductMap.get(qid)){
                    PricebookIdProdIdSet.add(quoteMap.get(qid).SBQQ__PriceBook__c+';'+prodId);
                }

            }   

            priceBookEntryList = [Select id,Pricebook2Id,Product2Id from PricebookEntry where (Product2Id=:Subscription_Support_ID or Product2Id in: productMap.keyset()) and Pricebook2Id in: quoteIdPricebbokIdMap.values()];

            
                for(PricebookEntry pbEntry : priceBookEntryList){
                    priceBookEntryPricebookIdProdIdSet.add(pbEntry.Pricebook2Id+';'+pbEntry.Product2Id);
                }

                for(String PricebookIdProdIdIter : PricebookIdProdIdSet){
                   if( !priceBookEntryPricebookIdProdIdSet.contains(PricebookIdProdIdIter)){
                        invalidProductIdSet.add(Id.valueOf(PricebookIdProdIdIter.split(';')[1]));
                   }
                }

                for(id prodIds : invalidProductIdSet){
                    if(productIdQuotelineMap!=null && productIdQuotelineMap.get(prodIds)!=null){
                        if(prodIds==Subscription_Support_ID){
                            //productIdQuotelineMap.get(prodIds).addError(Price_Book_entry_not_contain_Subs_Supprt);
                        }else{
                           // productIdQuotelineMap.get(prodIds).addError(Price_Book_entry_not_contain_Product + ' - ' + productMap.get(prodIds).SKU__c);
                          /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(prodIds)){
                            qLineItr.addError(Price_Book_entry_not_contain_Product + ' - ' + productMap.get(prodIds).SKU__c);
                          }*/
                          for (Integer i=0; i< productIdQuotelineMap.get(prodIds).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(prodIds)[i].addError(Price_Book_entry_not_contain_Product + ' - '+ temp + productMap.get(prodIds).SKU__c);
                                }
                        }
                    }
                }


            
            //PriceBook Entry Validations --end


        // subscription/support logic--->Start

        /*Set < Id > prodOptionConfiguredSKU_Set = new Set < Id > ();


        for (product2 productIter: productMap.Values()) {
            if (productIter.CPQ_License_Type__c != null && productIter.CPQ_License_Type__c.equalsIgnorecase('Perpetual') &&
                productIter.CPQ_SKU_Type__c != null && productIter.CPQ_SKU_Type__c.equalsIgnorecase('License') && productIter.CPQ_Is_Compliant__c != null && productIter.CPQ_Is_Compliant__c == false) {
                isProductSupportSet.add(productIter.id);
            }
        }


        for (SBQQ__ProductOption__c prodOptionItr: [select id, SBQQ__OptionalSKU__c, SBQQ__ConfiguredSKU__c from SBQQ__ProductOption__c where SBQQ__OptionalSKU__c =: Subscription_Support_ID and SBQQ__ConfiguredSKU__c in: isProductSupportSet]) {
            if (prodOptionItr.SBQQ__ConfiguredSKU__c != null) {
                prodOptionConfiguredSKU_Set.Add(prodOptionItr.SBQQ__ConfiguredSKU__c);
            }
        }


        system.debug(':::Pradeep::: isProductSupportSet >> ' + isProductSupportSet);
        system.debug(':::Pradeep::: prodOptionConfiguredSKU_Set >> ' + prodOptionConfiguredSKU_Set);

        for (id prodId: productIdQuotelineMap.keyset()) {
            system.debug(':::Pradeep::: prodId >> ' + prodId);

                if (productIdQuotelineMap.get(prodId).SBQQ__Product__c != Subscription_Support_ID) {
                    if ((isProductSupportSet.contains(prodId)) && !(isProductSupportSet.contains(prodId) && prodOptionConfiguredSKU_Set.Contains(prodId))) {
                        if(Support_product_not_available_Err!=null && Support_product_not_available_Err!=''){
                        productIdQuotelineMap.get(prodId).addError(Support_product_not_available_Err + ' - ' + productMap.get(prodId).SKU__c);
                        }
                    }
                }

        }
        // subscription/support logic--->End*/



        for (id quoteId: quoteProductMap.keyset()) {
            for (id productId: quoteProductMap.get(quoteId)) {
                if (productId == Subscription_Support_ID) {
                    continue;
                }

                    if (quoteMap.get(quoteId).CPQ_Account_Type__c != null) {
                        if (!(quoteMap.get(quoteId).CPQ_Account_Type__c.equalsIgnorecase('Academic'))) {
                            if (productMap.get(productId) != null && !(productMap.get(productId).CPQ_Customer_Segment__c != null && productMap.get(productId).CPQ_Customer_Segment__c.containsIgnoreCase(quoteMap.get(quoteId).CPQ_Account_Type__c))) {
                                if(Product_not_available_for_segment!=null && Product_not_available_for_segment!=''){
                                //productIdQuotelineMap.get(productId).addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                  
                                 /* for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                      qLineItr.addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                    }*/

                                    for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Product_not_available_for_segment + ' - '+temp + productMap.get(productId).SKU__c);
                                }
                                }
                            }
                        } else {
                            if (productMap.get(productId) != null && !((productMap.get(productId).CPQ_Customer_Segment__c != null && productMap.get(productId) != null && productMap.get(productId).CPQ_Customer_Segment__c.containsIgnoreCase('Academic')) || (productMap.get(productId) != null && productMap.get(productId).CPQ_Customer_Segment__c != null && productMap.get(productId).CPQ_Customer_Segment__c.containsIgnoreCase('Commercial')))) {
                                if(Product_not_available_for_segment!=null && Product_not_available_for_segment!=''){
                                //productIdQuotelineMap.get(productId).addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                  /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                      qLineItr.addError(Product_not_available_for_segment + ' - ' + productMap.get(productId).SKU__c);
                                    }*/
                                     for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Product_not_available_for_segment + ' - '+temp + productMap.get(productId).SKU__c);
                                    }
                                }
                            }
                        }
                    }

                    if (productMap.get(productId) != null && productMap.get(productId).CPQ_Quotable__c != null && productMap.get(productId).CPQ_Quotable__c != true) {
                        if(Product_is_EOL_Error_Message!=null && Product_is_EOL_Error_Message!=''){
                        //productIdQuotelineMap.get(productId).addError(Product_is_EOL_Error_Message + ' - ' + productMap.get(productId).SKU__c);
                          /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                      qLineItr.addError(Product_is_EOL_Error_Message + ' - ' + productMap.get(productId).SKU__c);
                                    }*/
                                    for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Product_is_EOL_Error_Message + ' - '+temp + productMap.get(productId).SKU__c);
                                    }
                        }

                    }
                    
                    if (productMap.get(productId) != null && !(productMap.get(productId).CPQ_Quote_Type__c != null && productMap.get(productId).CPQ_Quote_Type__c.containsIgnoreCase('SPF'))) {
                        if(Only_SPF_Products_SKUs_are_eligible_for!=null && Only_SPF_Products_SKUs_are_eligible_for!=''){
                        //productIdQuotelineMap.get(productId).addError(Only_SPF_Products_SKUs_are_eligible_for + ' - ' + productMap.get(productId).SKU__c);
                          /*for(SBQQ__QuoteLine__c qLineItr : productIdQuotelineMap.get(productId)){
                                      qLineItr.addError(Only_SPF_Products_SKUs_are_eligible_for + ' - ' + productMap.get(productId).SKU__c);
                                    }*/

                                    for (Integer i=0; i< productIdQuotelineMap.get(productId).size();i++ ) {
                                        string temp=addSpace(i);
                                        productIdQuotelineMap.get(productId)[i].addError(Only_SPF_Products_SKUs_are_eligible_for + ' - '+temp + productMap.get(productId).SKU__c);
                                    }
                        }

                    }

                }
            }
        }
                //Pradeep[for CW-190] ---> End
            // Changes for CW-87 START
                    CPQ_quoteLineHelperClass.populateRegionalDiscount(SBQQQuoteNewMap.values());
                    
                // Changes for CW-87 END
                CPQ_quoteLineHelperClass.approvedflag(SBQQQuoteNewMap,SBQQQuoteOldMap); //added Madhura CW-346
                
                CPQ_recursive.recursiveQuoteLineBeforeTrigger = true;
        
        }

    }
    //after Insert Method
    public Static void afterInsertMethod(List<SBQQ__QuoteLine__c>   SBQQQuoteNewList){
        Set<Id> quoteId = new Set<Id>();
        //Created for concationation of fields
        List<SBQQ__Quote__c> quoteList = new List<SBQQ__Quote__c>();
        Set<String> PGNameSet = new Set<String>();
        Map<id,Set<String>> quoteIDPGNameMap = new Map<id,Set<String>>();
        
        for(SBQQ__QuoteLine__c  objSQb : SBQQQuoteNewList)
        {
                quoteId.add(objSQb.SBQQ__Quote__c);
        }
        if(!quoteId.IsEmpty())
        { 
            setDiscountValue.setBlendedPerpetualDeiscount(quoteId);
            quoteList = [Select id,CPQ_All_Platform_Group__c,(select id,SBQQ__Product__c,SBQQ__Product__r.Platform_Group__c,SBQQ__Product__r.name,CPQ_GA_Future_Date_Validate__c from SBQQ__LineItems__r),CPQ_Warning_Messages__c from  SBQQ__Quote__c where id In : quoteId];
        }
        system.debug('quoteList'+quoteList);
        if(!quoteList.isEmpty()){
            for(SBQQ__Quote__c quoteRec :quoteList){
                 PGNameSet = new Set<String>();
                system.debug('quoteRec.SBQQ__LineItems__r'+quoteRec.SBQQ__LineItems__r);
                for(SBQQ__QuoteLine__c quoteLineRec :quoteRec.SBQQ__LineItems__r){
                    if(quoteLineRec.SBQQ__Product__c != null && quoteLineRec.SBQQ__Product__r.Platform_Group__c != null){
                        if(quoteLineRec.SBQQ__Product__r.name != 'Subscription/Support'){
                            PGNameSet.add(quoteLineRec.SBQQ__Product__r.Platform_Group__c);
                        }
                        if(quoteLineRec.CPQ_GA_Future_Date_Validate__c){
                            if(quoteRec.CPQ_Warning_Messages__c ==null || !quoteRec.CPQ_Warning_Messages__c.contains('Product Availability Date is in the future. Order may be put on hold until date is reached.')){
                              if(quoteRec.CPQ_Warning_Messages__c == null){
                                  quoteRec.CPQ_Warning_Messages__c='';
                              }
                            quoteRec.CPQ_Warning_Messages__c =quoteRec.CPQ_Warning_Messages__c + ' ' + 'Product Availability Date is in the future. Order may be put on hold until date is reached.';
                            
                           }
                        }
                        
                    }
                    
                }
                system.debug('PGNameSet'+PGNameSet);
                quoteIDPGNameMap.put(quoteRec.id,PGNameSet);
                system.debug('quoteIDPGNameMap'+quoteIDPGNameMap);
               
            }
            system.debug('quoteIDPGNameMap'+quoteIDPGNameMap);
            
            for(SBQQ__Quote__c quoteRec :quoteList){
                quoteRec.CPQ_All_Platform_Group__c = '';
                if(!quoteIDPGNameMap.isEmpty() && quoteIDPGNameMap.get(quoteRec.id) != null ){
                    for(String str :quoteIDPGNameMap.get(quoteRec.id)){
                        system.debug('quoteRec'+quoteRec.id);
                        quoteRec.CPQ_All_Platform_Group__c = quoteRec.CPQ_All_Platform_Group__c +  str + ',' ;
                    }
                }
                
                quoteRec.CPQ_All_Platform_Group__c = quoteRec.CPQ_All_Platform_Group__c.removeEnd(',');
            }
            
            Update quoteList;
        }

        CPQ_quoteLineHelperClass.restrictedProducts(quoteId);
        CPQ_quoteLineHelperClass.calculateTotalVPP(quoteId);
        //CPQ_quoteLineHelperClass.UpdateOpportunityLineItems(SBQQQuoteNewList);
        ID jobID = System.enqueueJob(new CPQ_OpportunityLineItemQueuebleJob(SBQQQuoteNewList,true,null));

    }
    
    //after Update Method
    public Static void afterUpdateMethod(Map<Id,SBQQ__QuoteLine__c>   SBQQQuoteLineNewMap,Map<Id,SBQQ__QuoteLine__c>   SBQQQuoteLineOldMap){
        if(!CPQ_recursive.recursiveQuoteLineAfterTrigger){
        Set<Id> quoteId = new Set<Id>();
        Set<Id> quoteIds = new Set<Id>();
        for(SBQQ__QuoteLine__c  objSQb : SBQQQuoteLineNewMap.values())
        {    quoteId.add(objSQb.SBQQ__Quote__c);
           
        }
        if(!quoteId.IsEmpty())
        {
            
            setDiscountValue.setBlendedPerpetualDeiscount(quoteId);
                
        //Madhura -- start
        
        CPQ_quoteLineHelperClass.restrictedProducts(quoteId);
        CPQ_quoteLineHelperClass.calculateTotalVPP(quoteId);
        //CPQ_quoteLineHelperClass.UpdateOLIOpportunityLineItems(SBQQQuoteLineNewMap.values(),SBQQQuoteLineOldMap);
        ID jobID = System.enqueueJob(new CPQ_OpportunityLineItemQueuebleJob(SBQQQuoteLineNewMap.values(),false,SBQQQuoteLineOldMap));
        //Madhura -- end
        }
         CPQ_recursive.recursiveQuoteLineAfterTrigger=true;
    }
    }
    //before Delete Method
    public Static void beforeDeleteMethod(List<SBQQ__QuoteLine__c>  SBQQQuoteOldList){
            
    }
    //after Delete Method
    public Static void afterDeleteMethod(List<SBQQ__QuoteLine__c>   SBQQQuoteOldList){
      Set<Id> quoteId = new Set<Id>();
    Set<Id> quoteLineIds = new Set<Id>();
    
    List<SBQQ__Quote__c> quoteListnew = new List<SBQQ__Quote__c>();
        Set<String> PGNameSet = new Set<String>();
        Map<id,Set<String>> quoteIDPGNameMap = new Map<id,Set<String>>();
    
    List<SBQQ__Quote__c> quoteListToUpdate = new List<SBQQ__Quote__c>();
        for(SBQQ__QuoteLine__c  objSQb : SBQQQuoteOldList)
        {    quoteId.add(objSQb.SBQQ__Quote__c);
          quoteLineIds.add(objSQb.Id);
        }
        
        
        if(!quoteId.IsEmpty())
        {
            
            setDiscountValue.setDeletedBlendedPerpetualDeiscount(quoteId,quoteLineIds);
             quoteListnew = [Select id,CPQ_All_Platform_Group__c,(select id,SBQQ__Product__c,SBQQ__Product__r.Platform_Group__c,SBQQ__Product__r.name from SBQQ__LineItems__r) from  SBQQ__Quote__c where id In : quoteId];
        }
        
        if(!quoteListnew.isEmpty()){
            for(SBQQ__Quote__c quoteRec :quoteListnew){
                 PGNameSet = new Set<String>();
                system.debug('quoteRec.SBQQ__LineItems__r'+quoteRec.SBQQ__LineItems__r);
                for(SBQQ__QuoteLine__c quoteLineRec :quoteRec.SBQQ__LineItems__r){
                    if(quoteLineRec.SBQQ__Product__c != null && quoteLineRec.SBQQ__Product__r.Platform_Group__c != null){
                        
                        if(quoteLineRec.SBQQ__Product__r.name != 'Subscription/Support'){
                            PGNameSet.add(quoteLineRec.SBQQ__Product__r.Platform_Group__c);
                        }
                        
                    }
                    
                }
                system.debug('PGNameSet'+PGNameSet);
                quoteIDPGNameMap.put(quoteRec.id,PGNameSet);
                system.debug('quoteIDPGNameMap'+quoteIDPGNameMap);
               
            }
            system.debug('quoteIDPGNameMap'+quoteIDPGNameMap);
            
            for(SBQQ__Quote__c quoteRec :quoteListnew){
                quoteRec.CPQ_All_Platform_Group__c = '';
                if(!quoteIDPGNameMap.isEmpty() && quoteIDPGNameMap.get(quoteRec.id) != null ){
                    for(String str :quoteIDPGNameMap.get(quoteRec.id)){
                        system.debug('quoteRec'+quoteRec.id);
                        quoteRec.CPQ_All_Platform_Group__c = quoteRec.CPQ_All_Platform_Group__c +  str + ',' ;
                    }
                }
                
                quoteRec.CPQ_All_Platform_Group__c = quoteRec.CPQ_All_Platform_Group__c.removeEnd(',');
            }
            
            Update quoteListnew;
        }
        
        
        
        List<SBQQ__Quote__c> quotelist = [Select id,CPQ_Restricted_SKU__c,CPQ_Total_VPP_Point__c,CPQ_VPP_Equivalent_Discount__c,CPQ_Country__c,CPQ_Restricted_Product_Message__c,(Select id,CPQ_Restricted__c,SBQQ__Product__c,SBQQ__Product__r.Name,SKU__c from SBQQ__LineItems__r) from SBQQ__Quote__c where id IN :quoteId];
    
        for(SBQQ__Quote__c quoteItr : quotelist){
            if(quoteItr.SBQQ__LineItems__r==null){
                quoteItr.CPQ_Restricted_SKU__c='';
                quoteItr.CPQ_Restricted_Product_Message__c='';
                quoteItr.CPQ_Total_VPP_Point__c=0.00;
                quoteItr.CPQ_VPP_Equivalent_Discount__c=0;
                quoteListToUpdate.add(quoteItr);
            }else if(quoteItr.SBQQ__LineItems__r.size()==0){
                quoteItr.CPQ_Restricted_SKU__c='';
                quoteItr.CPQ_Restricted_Product_Message__c='';
                quoteItr.CPQ_Total_VPP_Point__c=0.00;
                quoteItr.CPQ_VPP_Equivalent_Discount__c=0;
                quoteListToUpdate.add(quoteItr);
            }
        }
        update quoteListToUpdate;
    }
        static string addSpace(Integer i){
        string temp='';
        for(Integer j=0;j<=i;j++ ){
            temp=' ';
        }
        return temp;
    }
    
}