package com.shaic.paclaim.health.reimbursement.medicalapproval.pages.previousclaims;

import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdiproperties.Localizer.TextBundleUpdated;
import org.vaadin.addon.cdiproperties.TextBundle;

import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.shaic.claim.premedical.dto.PreviousClaimsTableDTO;
import com.vaadin.ui.Component;

public class PAHealthClaimRequestPreviousClaimsPageViewImpl extends AbstractMVPView 
implements PAHealthClaimRequestPreviousClaimsPageInterface {

	private static final long serialVersionUID = 4534162762623368472L;

	private PreauthDTO bean;
	
	@Inject
	private PAHealthClaimRequestPreviousClaimsPageUI previousClaimPage;
	
	@Inject
	private TextBundle tb;
	
	private String strCaptionString;
	
	@Override
	public String getCaption() {
		return "Previous Claim Details";
	}
	
	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(PreauthDTO bean) {
		this.bean = bean;
		
	}

	@Override
	public Component getContent() {
		previousClaimPage.init(bean);
		Component comp =  previousClaimPage.getContent();
		//setCompositionRoot(comp);
		fireViewEvent(PAHealthClaimRequestPreviousClaimsPagePresenter.MEDICAL_APPROVAL_PREVIOUS_CLAIMS_SETUP_REFERNCE, this.bean);
		return comp;
	}

	@Override
	public void setupReferences(Map<String, Object> referenceData) {
		previousClaimPage.setupReferences(referenceData);
		
	}

	@Override
	public boolean onAdvance() {
		return previousClaimPage.validatePage();
	}

	@Override
	public boolean onBack() {
		bean.setAlertMessageOpened(false);
		bean.setDialysisOpened(false);
		bean.setIsDialysis(false);
		bean.setIsComparisonDone(false);
		bean.setIsBack(true);
		return true;
	}

	@Override
	public boolean onSave() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void localize(
            @Observes(notifyObserver = Reception.IF_EXISTS) @TextBundleUpdated final ParameterDTO parameterDto) {
             strCaptionString = tb.getText(textBundlePrefixString() + "documentdetails");
        }
	
	private String textBundlePrefixString()
	{
		return "medical-approval-";
	}

	@Override
	public void getPreviousClaimDetails(
			List<PreviousClaimsTableDTO> previousClaimDTOList) {
		previousClaimPage.setPreviousClaims(previousClaimDTOList);
	}

	@Override
	public void genertateFieldsBasedOnRelapseOfIllness(
			Map<String, Object> referenceData) {
		previousClaimPage.generateFieldsBasedOnRelapseOfIllness(referenceData);
	}

}
