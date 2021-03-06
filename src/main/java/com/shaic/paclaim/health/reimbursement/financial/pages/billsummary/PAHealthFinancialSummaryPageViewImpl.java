package com.shaic.paclaim.health.reimbursement.financial.pages.billsummary;

import java.util.Map;

import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.addon.cdiproperties.TextBundle;
import org.vaadin.teemu.wizards.GWizard;

import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.vaadin.ui.Component;

public class PAHealthFinancialSummaryPageViewImpl extends AbstractMVPView implements PAHealthFinancialSummaryPageWizard {

	private static final long serialVersionUID = -1756934701433733987L;
	
	@Inject
	private PAHealthFinancialSummaryPageUI billingSummaryPage;
	
	@Inject
	private TextBundle tb;
	
	private String strCaptionString;
	
	private PreauthDTO bean;
	
	private GWizard wizard;

	
	@Override
	public String getCaption() {
		return "View Bill Summary";
	}
	
	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void buildSuccessLayout() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void init(PreauthDTO bean) {
		this.bean = bean;
		
	}


	@Override
	public Component getContent() {
		billingSummaryPage.init(bean, wizard);
		Component comp =  billingSummaryPage.getContent();
		return comp;
	}


	@Override
	public void setupReferences(Map<String, Object> referenceData) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onAdvance() {
		
		if(billingSummaryPage.validatePage())
		{
			billingSummaryPage.setPayableAmounts();
			billingSummaryPage.setTableValuesToDTO();
			return true;
		}
		else
		{
			return false;
		}
		
		/*billingSummaryPage.setPayableAmounts();
		return true;*/
	}


	@Override
	public boolean onBack() {
		bean.setIsBack(true);
		return true;
	}


	@Override
	public boolean onSave() {
		// TODO Auto-generated method stub
		return false;
	}

}
