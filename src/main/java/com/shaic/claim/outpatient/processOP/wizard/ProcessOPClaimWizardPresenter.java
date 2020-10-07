package com.shaic.claim.outpatient.processOP.wizard;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.claim.intimation.create.dto.HospitalDto;
import com.shaic.claim.outpatient.processOPpages.ConsultationTabPage;
import com.shaic.claim.outpatient.registerclaim.dto.OutPatientDTO;
import com.shaic.domain.outpatient.OutpatientService;
import com.shaic.ims.bpm.claim.DBCalculationService;

@ViewInterface(ProcessOPClaimWizard.class)
public class ProcessOPClaimWizardPresenter extends AbstractMVPPresenter<ProcessOPClaimWizard>{

	private static final long serialVersionUID = 8820720086680581847L;
	public static final String PROECSS_OP_SUBMITTED_EVENT = "process_op_claim_submitted_event";
	
	public static final String HOSPITAL_CONTACT_DETAILS = "hospital_contact_details";
	public static final String HOSPITAL_CONTACT_SEARCH = "hospital_contact_search";
	
	public static final String SETUP_OP_PAYABLE_DETAILS  = "setup_payable_details_op";

	@EJB
	private OutpatientService outpatientService;
	
	@EJB
	private DBCalculationService dbCalculationService;
	
	@Inject
	private ConsultationTabPage consultationTabPage;
	
	
	public void submitWizard(
			@Observes @CDIEvent(PROECSS_OP_SUBMITTED_EVENT) final ParameterDTO parameters) {
		OutPatientDTO bean = (OutPatientDTO) parameters.getPrimaryParameter();
		outpatientService.submitProcessOPClaim(bean);
		dbCalculationService.invokeAccumulatorForOP(bean.getPolicy().getPolicyNumber(),bean.getDocumentDetails().getInsuredPatientName().getHealthCardNumber(),bean.getDocumentDetails().getConsultationType().getCommonValue());
		view.buildSuccessLayout();	
	}
	
	@SuppressWarnings("deprecation")
	public void searchHospitalContactNo(
			@Observes @CDIEvent(HOSPITAL_CONTACT_DETAILS) final ParameterDTO parameters) {
		HospitalDto searchHospitalDetails = (HospitalDto) parameters
				.getPrimaryParameter();
		view.setHospitalDetails(searchHospitalDetails);
		
	}
	
	@SuppressWarnings("deprecation")
	public void searchDiagnosticHospitalContactNo(
			@Observes @CDIEvent(HOSPITAL_CONTACT_SEARCH) final ParameterDTO parameters) {
		HospitalDto searchHospitalDetails = (HospitalDto) parameters
				.getPrimaryParameter();
		view.setDiagnosticHospitalDetails(searchHospitalDetails);
		
	}
	
	public void setupPayableDetails(
			@Observes @CDIEvent(SETUP_OP_PAYABLE_DETAILS) final ParameterDTO parameters) {
		String viewSearchCriteriaDTO = (String) parameters.getPrimaryParameter();
		view.setUpPayableDetails(viewSearchCriteriaDTO);
	}

	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}

}
