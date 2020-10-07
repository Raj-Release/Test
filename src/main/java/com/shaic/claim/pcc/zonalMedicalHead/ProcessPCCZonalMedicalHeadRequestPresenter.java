package com.shaic.claim.pcc.zonalMedicalHead;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;

import com.shaic.claim.pcc.dto.PccDTO;
import com.shaic.claim.pcc.wizard.ProcessPCCCoOrdinatorRequestWizard;

@ViewInterface(ProcessPCCZonalMedicalHeadRequestWizardView.class)
public class ProcessPCCZonalMedicalHeadRequestPresenter extends AbstractMVPPresenter<ProcessPCCZonalMedicalHeadRequestWizardView> {
	
	@EJB
	private ZonalMedicalHeadRequestService requestService;
	
	public static final String SUBMIT_ZONAL_MEDICAL_HEAD_DETAILS = "submit_zonal_medical_head_details";
	
    public static final String ZONAL_MEDICAL_HEAD_GENERATE_RESPONSE_LAYOUT = "zonal_medical_head_generate_response_layout";
	
	public static final String ZONAL_MEDICAL_HEAD_GENERATE_ASSIGN_LAYOUT = "zonal_medical_head_generate_assign_layout";
		
	public static final String ZONAL_MEDICAL_HEAD_GENERATE_NEGOTIATION_APPLICABLE = "zonal_medical_head_generate_negotiation_applicable";
	
	
	public void submitZonalMedicalHead(@Observes @CDIEvent(SUBMIT_ZONAL_MEDICAL_HEAD_DETAILS) final ParameterDTO parameters) {	

		PccDTO pccDTO = (PccDTO) parameters.getPrimaryParameter();
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		requestService.submitZonalMedicalHead(pccDTO,userName);
		view.buildSuccessLayout();		
	}
	
   public void generateapproveLayout(@Observes @CDIEvent(ZONAL_MEDICAL_HEAD_GENERATE_RESPONSE_LAYOUT) final ParameterDTO parameters) {	
		
		view.generateResponseLayout();		
	}

	public void generateQuerryLayout(@Observes @CDIEvent(ZONAL_MEDICAL_HEAD_GENERATE_ASSIGN_LAYOUT) final ParameterDTO parameters) {	

		view.generateAssignLayout();		
	}
	
	public void generateFieldsBasedOnNegotiationApplicable(@Observes @CDIEvent(ZONAL_MEDICAL_HEAD_GENERATE_NEGOTIATION_APPLICABLE) final ParameterDTO parameters)
	{
		Boolean isCked = (Boolean) parameters.getPrimaryParameter();
		view.generateFieldsBasedOnNegotiation(isCked);
	}

	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}

}
