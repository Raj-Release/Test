package com.shaic.claim.preauth.wizard.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.ViewComponent;

import com.shaic.arch.SHAConstants;
import com.shaic.claim.rod.wizard.service.CreateRODService;
import com.shaic.claim.viewEarlierRodDetails.CorporateBufferUtilisationTable;
import com.shaic.claim.viewEarlierRodDetails.CorporateBufferUtilisationTableDTO;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimService;
import com.shaic.domain.Intimation;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.preauth.Preauth;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

public class ViewGmcCorporateBufferUtilisationPage extends ViewComponent{
	
	private FormLayout formLayoutLeft;
	private FormLayout formLayoutRight;
	private TextField txtCorpBufferSI;
	private TextField txtCorpUtilizedAmt;
	private TextField txtCorpBufferAllocatedToClaim;
	private TextField txtCorpBufferAllocatedToClaim1;
	private TextField txtCorpBufferUtilisedforCurrentClaim;
	private TextField txtCorpBufferAvailableSIforCurrentClaim;
	private TextField txtBalanceAvailable;
	
	
	@Inject
	public CorporateBufferUtilisationTable corpbufferUtilisationTable;
	
   @EJB
	private ClaimService claimService;
	
   
   @EJB
  	private CreateRODService createRodService;
   
   @Inject 
	CorporateBufferUtilisationTable corporateBufferUtilisationTable; 


	  public void init(Intimation intimation){
		  
		  
		Claim claimforIntimation = claimService.getClaimforIntimation(intimation.getKey()); 		  
		
		HorizontalLayout gmcLayout = new HorizontalLayout();
		VerticalLayout gmcVLayout = new VerticalLayout();
		
		txtCorpBufferSI = new TextField("Corporate Buffer SI");
		txtCorpUtilizedAmt = new TextField("Corporate Buffer Utilisation Amount");
		txtCorpBufferAllocatedToClaim = new TextField("Corporate Buffer allocated for this claim");	
		txtCorpBufferAllocatedToClaim1 = new TextField("Corporate Buffer allocated for this claim");	
		txtCorpBufferUtilisedforCurrentClaim = new TextField("Corporate Buffer Utilised for this claim");
		txtCorpBufferAvailableSIforCurrentClaim = new TextField("Corporate Buffer balance available for this claim");
		txtBalanceAvailable = new TextField("Balance Available");
		
		txtCorpBufferSI.setEnabled(false);
		txtCorpUtilizedAmt.setEnabled(false);
		txtCorpBufferAllocatedToClaim.setEnabled(false);
		txtCorpBufferAllocatedToClaim1.setEnabled(false);
		txtCorpBufferUtilisedforCurrentClaim.setEnabled(false);
		txtCorpBufferAvailableSIforCurrentClaim.setEnabled(false);
		txtBalanceAvailable.setEnabled(false);
		
		
		formLayoutLeft = new FormLayout(txtCorpBufferSI,  txtCorpUtilizedAmt,txtCorpBufferAllocatedToClaim,txtBalanceAvailable);
		formLayoutLeft.addStyleName("layoutDesign");
		formLayoutLeft.setSpacing(true);
    	
		formLayoutRight = new FormLayout(txtCorpBufferAllocatedToClaim1,txtCorpBufferUtilisedforCurrentClaim,txtCorpBufferAvailableSIforCurrentClaim);
		formLayoutRight.addStyleName("layoutDesign");
		formLayoutRight.setSpacing(true);		
		
		gmcLayout.addComponents(formLayoutLeft,formLayoutRight);	
		
		corpbufferUtilisationTable.init("", false, false);			
		gmcVLayout.addComponents(gmcLayout,corpbufferUtilisationTable);			
		gmcVLayout.setSpacing(true);
		gmcVLayout.setMargin(true);
		
		DBCalculationService dbCalculationService = new DBCalculationService();		 
		Map<String, Double> values = null;
		   
		   
		if(claimforIntimation != null){
			   values = dbCalculationService.getGmcCorpBufferASIForRegister(intimation.getPolicy().getPolicyNumber(), claimforIntimation.getKey());
			  
		   }else{
			   values = dbCalculationService.getGmcCorpBufferASIForRegister(intimation.getPolicy().getPolicyNumber(), 0l);
		   }

			
		if(values != null && !values.isEmpty()){
				
			if(values.get(SHAConstants.GMC_CORPORATE_BUFFER_SI) != null){
				
					txtCorpBufferSI.setReadOnly(false);
					txtCorpBufferSI.setValue(values.get(SHAConstants.GMC_CORPORATE_BUFFER_SI).toString());
					txtCorpBufferSI.setReadOnly(true);	
				}
				
			if(values.get(SHAConstants.GMC_BUFFER_UTILISED_AMT) != null){
				
					txtCorpUtilizedAmt.setReadOnly(false);
					txtCorpUtilizedAmt.setValue(values.get(SHAConstants.GMC_BUFFER_UTILISED_AMT).toString());
					txtCorpUtilizedAmt.setReadOnly(true);	
				}
			
			if(values.get(SHAConstants.GMC_CORPORATE_LIMIT_AMT) != null){
				
				txtCorpBufferAllocatedToClaim.setReadOnly(false);
				txtCorpBufferAllocatedToClaim.setValue(values.get(SHAConstants.GMC_CORPORATE_LIMIT_AMT).toString());
				txtCorpBufferAllocatedToClaim.setReadOnly(true);
				
				txtCorpBufferAllocatedToClaim1.setReadOnly(false);
				txtCorpBufferAllocatedToClaim1.setValue(values.get(SHAConstants.GMC_CORPORATE_LIMIT_AMT).toString());
				txtCorpBufferAllocatedToClaim1.setReadOnly(true);
			}	
			
			if(values.get(SHAConstants.GMC_AVAILABLE_SI) != null){
				txtBalanceAvailable.setValue(values.get(SHAConstants.GMC_AVAILABLE_SI).toString());	
			}			 
								
			}
		
		setCorporateUtilisedTableValues(claimforIntimation);
		setCompositionRoot(gmcVLayout);
		
	}	
	  
	  private void setCorporateUtilisedTableValues(Claim claim)
		 {
		  
		  	 Double reimbUtilisedAmnt = 0d;
		  	 Double preauthUtilisedAmnt = 0d;  	
		  	 
		  
			 List<CorporateBufferUtilisationTableDTO> corpbufferTableList = new ArrayList<CorporateBufferUtilisationTableDTO>();
			 
			 List<Preauth> preauthList = null;
			 if(claim != null){
				 preauthList = createRodService.getPreauthListByClaimKey(claim.getKey());
			 }else{
				 preauthList = new ArrayList<Preauth>();
			 }
			 
			 Preauth preauthObj = new Preauth();
			 
			 
			 if(null != preauthList && !preauthList.isEmpty()){
				 
				 for (Preauth preauth : preauthList) {
					 if(preauth.getCorporateUtilizedAmt() != null){
						 preauthObj = preauth;
						 break;
					 }
				 }
				  //preauthObj = preauthList.get(0);
				  
			  for (Preauth preauth : preauthList) {
				 CorporateBufferUtilisationTableDTO corpBufferTableDto = new CorporateBufferUtilisationTableDTO();
				 
				 corpBufferTableDto.setTypeOfClaim(preauth.getClaim().getClaimType().getValue());
				 corpBufferTableDto.setReferenceOrRodNo(preauth.getPreauthId());
				 corpBufferTableDto.setStatus(preauth.getStatus().getProcessValue());
				 if(null != preauth.getCorporateUtilizedAmt()){
				 corpBufferTableDto.setCorpBufferUtilisation(Double.valueOf(preauth.getCorporateUtilizedAmt()));
				 }
				 
				 if(null != txtCorpBufferAllocatedToClaim && null != txtCorpBufferAllocatedToClaim.getValue() && !("").equalsIgnoreCase(txtCorpBufferAllocatedToClaim.getValue())
						 && null != corpBufferTableDto.getCorpBufferUtilisation()){
					 
					 corpBufferTableDto.setCorpBufferRemainingforClaim(Double.valueOf(txtCorpBufferAllocatedToClaim.getValue()) - corpBufferTableDto.getCorpBufferUtilisation());
				 }
				 
				 corpbufferTableList.add(corpBufferTableDto);				 
			}	
			 }
			 
			 List<Reimbursement> reimList = new ArrayList<Reimbursement>();
			 if(claim != null){
				 reimList = createRodService.getReimbursementByClaimKey(claim.getKey());
			 }
			 
			 
			 if(null != reimList && !reimList.isEmpty()){				 
			 
			 for (Reimbursement reimbursement : reimList) {
				 CorporateBufferUtilisationTableDTO corpBufferTableDto1 = new CorporateBufferUtilisationTableDTO();
				 corpBufferTableDto1.setTypeOfClaim(reimbursement.getClaim().getClaimType().getValue());
				 corpBufferTableDto1.setReferenceOrRodNo(reimbursement.getRodNumber());
				 corpBufferTableDto1.setStatus(reimbursement.getStatus().getProcessValue());
				 if(null != reimbursement.getCorporateUtilizedAmt()){
				 corpBufferTableDto1.setCorpBufferUtilisation(Double.valueOf(reimbursement.getCorporateUtilizedAmt()));
				 }
				 
				 if(null != txtCorpBufferAllocatedToClaim && null != txtCorpBufferAllocatedToClaim.getValue() && !("").equalsIgnoreCase(txtCorpBufferAllocatedToClaim.getValue())){
					 Double corpUtilizationAmt = 0.0;
					 if (null != corpBufferTableDto1.getCorpBufferUtilisation()) {
						 corpUtilizationAmt = corpBufferTableDto1.getCorpBufferUtilisation();
					 }
					 corpBufferTableDto1.setCorpBufferRemainingforClaim(Double.valueOf(txtCorpBufferAllocatedToClaim.getValue()) - corpUtilizationAmt);
				 } 
				 
				 corpbufferTableList.add(corpBufferTableDto1);
				 
				 if(null != reimbursement.getCorporateUtilizedAmt()){
				 reimbUtilisedAmnt += reimbursement.getCorporateUtilizedAmt();
				 }
			}
			 
			 }
			 
			 
			 if(null != preauthObj && null != preauthObj.getCorporateUtilizedAmt()){
				 preauthUtilisedAmnt += preauthObj.getCorporateUtilizedAmt();
				 }			 
			
			 
			 if(null != txtCorpBufferUtilisedforCurrentClaim){
					
				 	txtCorpBufferUtilisedforCurrentClaim.setReadOnly(false);
				 	if(!preauthUtilisedAmnt.equals(0d) && !reimbUtilisedAmnt.equals(0d)){
				 		txtCorpBufferUtilisedforCurrentClaim.setValue(String.valueOf(reimbUtilisedAmnt));
				 	}else{
				 		txtCorpBufferUtilisedforCurrentClaim.setValue(String.valueOf(preauthUtilisedAmnt + reimbUtilisedAmnt));
				 	}
					txtCorpBufferUtilisedforCurrentClaim.setReadOnly(true);
				}
			 
			 
			 if(null != txtCorpBufferAvailableSIforCurrentClaim){
				 
				 if(null != txtCorpBufferUtilisedforCurrentClaim && null!= txtCorpBufferUtilisedforCurrentClaim.getValue() && 
						 !("").equalsIgnoreCase(txtCorpBufferUtilisedforCurrentClaim.getValue())&&
						 null != txtCorpBufferAllocatedToClaim1 && null != txtCorpBufferAllocatedToClaim1.getValue() &&
						 !("").equalsIgnoreCase(txtCorpBufferAllocatedToClaim1.getValue())){
					 
					Double corpBufferAvailableSI = Double.valueOf(txtCorpBufferAllocatedToClaim1.getValue()) - Double.valueOf(txtCorpBufferUtilisedforCurrentClaim.getValue());
					 
					 txtCorpBufferAvailableSIforCurrentClaim.setReadOnly(false);
					 txtCorpBufferAvailableSIforCurrentClaim.setValue(String.valueOf(corpBufferAvailableSI));
					 txtCorpBufferAvailableSIforCurrentClaim.setReadOnly(true);
					 
				 }
				 
				 	
			 }
						 
			 corporateBufferUtilisationTable.setTableList(corpbufferTableList);
			 
		 }
	
}
