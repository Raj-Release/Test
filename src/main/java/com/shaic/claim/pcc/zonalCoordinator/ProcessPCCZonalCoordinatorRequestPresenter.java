package com.shaic.claim.pcc.zonalCoordinator;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;

import com.shaic.claim.pcc.dto.PccDTO;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;

@ViewInterface(ProcessPCCZonalCoordinatorRequestWizardView.class)
public class ProcessPCCZonalCoordinatorRequestPresenter extends AbstractMVPPresenter<ProcessPCCZonalCoordinatorRequestWizardView> {
	
	@EJB
    private ZonalCoordinatorRequestService requestService;
	
	public static final String SUBMIT_ZONAL_COORDINATOR_DETAILS = "submit_zonal_coorinator_details";
	
	public static final String ZONAL_COORDINATOR_GENERATE_NEGOTIATION_APPLICABLE = "zonal_coordinator_generate_negotiation_applicable";

	
	public void submitZonalMedicalHead(@Observes @CDIEvent(SUBMIT_ZONAL_COORDINATOR_DETAILS) final ParameterDTO parameters) {	

		PccDTO pccDTO = (PccDTO) parameters.getPrimaryParameter();
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		PccDetailsTableDTO pccDetailsTableDTO=(PccDetailsTableDTO)parameters.getSecondaryParameter(1, PccDetailsTableDTO.class);
		requestService.submitZonalCoordinator(pccDTO,userName,pccDetailsTableDTO);
		view.buildSuccessLayout();		
	}
	
	public void generateFieldsBasedOnNegotiationApplicable(@Observes @CDIEvent(ZONAL_COORDINATOR_GENERATE_NEGOTIATION_APPLICABLE) final ParameterDTO parameters)
	{
		Boolean isCked = (Boolean) parameters.getPrimaryParameter();
		view.generateFieldsBasedOnNegotiation(isCked);
	}
	

	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}

}
