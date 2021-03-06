package com.shaic.claim.allowghiregistration;

import java.util.List;

import com.shaic.arch.GMVPView;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.ClaimDto;
import com.shaic.claim.premedical.dto.PreviousClaimsTableDTO;
import com.shaic.claim.registration.SearchClaimRegistrationTableDto;
import com.shaic.domain.SublimitFunObject;
import com.shaic.domain.TmpCPUCode;
import com.vaadin.v7.data.util.BeanItemContainer;


public interface GhiAllowClaimRegistrationView extends GMVPView{
	public void initView(SearchClaimRegistrationTableDto searchResult);
	void setCurrenceyMaster(BeanItemContainer<SelectValue> currencyMaster);
	void setPreviousClaims(List<PreviousClaimsTableDTO> previousClaimList);
	void setBalanceSumInsued(Double balanceSI);
	void setCpuObject(TmpCPUCode tmpCpuCode);
	void submitClaimRegister(ClaimDto claimDto, Boolean isProceedFurther);
    void registerClicked();
    void suggestRejectionClicked();
    void cancelClaimRegistration();
	void setClaimDetails(ClaimDto claimDto);
	void setRegistrationDetails(SearchClaimRegistrationTableDto searchClaimRegistrationTableDto);
	void setSublimt(List<SublimitFunObject> sublimitList);

	void buildRRCRequestSuccessLayout(String rrcRequestNo);
	void buildValidationUserRRCRequestLayout(Boolean isValid);
	void loadRRCRequestDropDownValues(BeanItemContainer<SelectValue> mastersValueContainer);
	
	void setCorpBufferTab(Double bufferSI, Double utilisedAmt);

}
