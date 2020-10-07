package com.shaic.claim.pcc.hrmCoordinator;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;

import com.shaic.claim.pcc.dto.PccDTO;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.zonalCoordinator.ZonalCoordinatorRequestService;


@ViewInterface(ProcessPCCHrmCoordinatorRequestWizardView.class)
public class ProcessPCCHrmCoordinatorRequestPresenter extends AbstractMVPPresenter<ProcessPCCHrmCoordinatorRequestWizardView> {
	
	@EJB
	private HRMCoordinatorRequestService requestService;
	
	public static final String SUBMIT_HRM_COORDINATOR_DETAILS = "submit_hrm_coorinator_details";
	
	public static final String HRM_COORDINATOR_GENERATE_NEGOTIATION_APPLICABLE = "hrm_coordinator_generate_negotiation_applicable";
	
	public void submitZonalMedicalHead(@Observes @CDIEvent(SUBMIT_HRM_COORDINATOR_DETAILS) final ParameterDTO parameters) {	

		PccDTO pccDTO = (PccDTO) parameters.getPrimaryParameter();
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		PccDetailsTableDTO pccDetailsTableDTO=(PccDetailsTableDTO)parameters.getSecondaryParameter(1, PccDetailsTableDTO.class);
		requestService.submitHRMCoordinator(pccDTO,userName,pccDetailsTableDTO);
		view.buildSuccessLayout();		
	}

	public void generateFieldsBasedOnNegotiationApplicable(@Observes @CDIEvent(HRM_COORDINATOR_GENERATE_NEGOTIATION_APPLICABLE) final ParameterDTO parameters)
	{
		Boolean isCked = (Boolean) parameters.getPrimaryParameter();
		view.generateFieldsBasedOnNegotiation(isCked);
	}
	
	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}

}
