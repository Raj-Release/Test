package com.shaic.claim.omp.newregistration;

import com.shaic.arch.GMVPView;
import com.shaic.claim.ClaimDto;
import com.shaic.domain.Insured;
import com.shaic.domain.OMPClaim;
import com.vaadin.v7.ui.HorizontalLayout;

public interface OMPNewRegistrationPageView extends GMVPView{
	
	public void buildSuccessLayout();

	public void cancelIntimation();
	
	public void setInsuredInPage(Insured insured);
	
	public HorizontalLayout showSubmitMessagePanel(String messageInfo);
	
	public void showErrorMessagePanel(String messageInfo);

	public void registerClicked();

	public void suggestRejectionClicked();
	
//	void submitClaimRegister(ClaimDto claimDto, Boolean isProceedFurther);
	
	void setClaimDetails(ClaimDto claimDto);

	public void showSubmitMessage(OMPClaim claimsByIntimationNumber);

	void submitClaimRegister(ClaimDto claimDto, Boolean isProceedFurther, String vb64Status);
}
