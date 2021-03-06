package com.shaic.reimbursement.assigninvesigation;

import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdiproperties.Localizer.TextBundleUpdated;
import org.vaadin.addon.cdiproperties.TextBundle;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;

public class ConfirmationViewImpl extends AbstractMVPView implements
		WizardStep<AssignInvestigatorDto> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ConfirmationUI confirmationUI;
	
	@Inject
	private AssignInvestigatorDto bean;
	
	@Inject
	private TextBundle tb;
	
	private String strCaptionString;
	
	public void init(AssignInvestigatorDto assignInvestigatorDto){
		localize(null);
		this.bean = assignInvestigatorDto;				
		confirmationUI.init(assignInvestigatorDto);
	}

	@Override
	public Component getContent() {		
		return confirmationUI.getContent();
	}

	@Override
	public void setupReferences(Map<String, Object> referenceData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onAdvance() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

	@Override
	public boolean onSave() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String getCaption() {
		return strCaptionString;
	}
	
	protected void localize(
			@Observes(notifyObserver = Reception.IF_EXISTS) @TextBundleUpdated final ParameterDTO parameterDto) {
		strCaptionString = tb.getText(textBundlePrefixString()
				+ "confirm");
	}
	
	private String textBundlePrefixString() {
		return "Assign-Investigation-";
	}

}
