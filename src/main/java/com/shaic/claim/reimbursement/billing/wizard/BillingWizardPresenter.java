package com.shaic.claim.reimbursement.billing.wizard;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.common.APIService;
import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.shaic.claim.reimbursement.dto.EmployeeMasterDTO;
import com.shaic.claim.rod.wizard.service.CreateRODService;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimService;
import com.shaic.domain.HospitalService;
import com.shaic.domain.Hospitals;
import com.shaic.domain.MasterService;
import com.shaic.domain.PolicyService;
import com.shaic.domain.PreauthService;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.preauth.Preauth;
import com.shaic.domain.reimbursement.ReimbursementService;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.zybnet.autocomplete.server.AutocompleteField;

@ViewInterface(BillingWizard.class)
public class BillingWizardPresenter extends AbstractMVPPresenter<BillingWizard>{
	private static final long serialVersionUID = 7488192193097057694L;
	
	public static final String SUBMIT_CLAIM_BILLING = "submit_claim_billing";
	
	public static final String SUBMIT_CLAIM_BILLING_MAPPING = "submit_claim_billing_mapping";
	
	public static final String SAVE_BILLING_RRC_REQUEST_VALUES = "save_billing_rrc_request_values";
	
	public static final String BILLING_LOAD_RRC_REQUEST_DROP_DOWN_VALUES = "billing_load_rrc_request_drop_down_values";
	public static final String BILLING_LOAD_EMPLOYEE_DETAILS = "billing_load_employee_details";
	
	public static final String VALIDATE_BILLING_USER_RRC_REQUEST = "validate_billing_user_rrc_request"; 
	
	public static final String BILLING_APPROVED_AMOUNT_VALIDATION = "approved amount validation for billing";
	
	public static final String VALIDATE_BILLING_USER_LUMEN_REQUEST = "validate_billing_user_lumen_request";	

	public static final String BILLING_LOAD_RRC_REQUEST_SUB_CATEGORY_VALUES = "billing_load_rrc_request_sub_category_values";

	public static final String BILLING_LOAD_RRC_REQUEST_SOURCE_VALUES = "billing_load_rrc_request_source_values";

		
	@EJB
	private ReimbursementService reimbursementService;
	
	@EJB
	private PolicyService policyService;
	
	@EJB
	private DBCalculationService dbCalculationService;
	
	@EJB
	private MasterService masterService;
	
	@EJB
	private APIService apiService;
	
	@EJB
	private HospitalService hospitalService;
	
	@EJB
	private ClaimService claimService;
	
	@EJB
	private CreateRODService createRODService;
	
	@EJB
	private PreauthService preauthService;
	
	@SuppressWarnings("static-access")
	public void submitWizard(
			@Observes @CDIEvent(SUBMIT_CLAIM_BILLING) final ParameterDTO parameters) throws Exception{

		PreauthDTO reimbursementDTO = (PreauthDTO) parameters.getPrimaryParameter();
		// If hospitalization ROD is not submitted then we should not allow other ROD to submit.
		
		Boolean isAlreadyAcquired = Boolean.FALSE;
		StringBuffer aquiredUser = new StringBuffer();
		Map<String, Object> wrkFlowMap = (Map<String, Object>) reimbursementDTO.getDbOutArray();
		if (wrkFlowMap != null){
			DBCalculationService db = new DBCalculationService();
			Long wrkFlowKey = (Long) wrkFlowMap.get(SHAConstants.WK_KEY);
			String aciquireByUserId = db.getLockUser(wrkFlowKey);
			if((reimbursementDTO.getStrUserName() != null && aciquireByUserId != null && ! reimbursementDTO.getStrUserName().equalsIgnoreCase(aciquireByUserId)) || aciquireByUserId == null){
				isAlreadyAcquired = Boolean.TRUE;
				aquiredUser.append(aciquireByUserId);
			}
		
		}
		
		if(! isAlreadyAcquired){
		
		Reimbursement currentReimbursement = createRODService.getReimbursementObjectByKey(reimbursementDTO.getKey());
		Boolean isFirstRODApproved = createRODService.getStatusOfFirstROD(reimbursementDTO.getClaimDTO().getKey(), currentReimbursement);
		if(isFirstRODApproved) {
			// Rollback procedure to clear the accumulator value.................
			if(((reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_CANCEL_ROD)) || (reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_REFER_TO_BILL_ENTRY))) && !reimbursementDTO.getIsDirectToBilling() ) {
//				dbCalculationService.reimbursementRollBackProc(reimbursementDTO.getPreviousPreauthKey().equals(0l) ? reimbursementDTO.getKey() : reimbursementDTO.getPreviousPreauthKey() , reimbursementDTO.getPreviousPreauthKey().equals(0l) ? "R" : "C");
				dbCalculationService.reimbursementRollBackProc(reimbursementDTO.getKey(),"R");
				
			}
			
			if(reimbursementDTO.getIsDirectToBilling() && ! reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_REFER_TO_BILL_ENTRY) 
					&& ! reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_REFER_TO_MEDICALA_APPROVER)){
				Reimbursement partialHospitalizationROD = reimbursementService.getPartialHospitalizationROD(reimbursementDTO.getClaimKey());
				
				if(partialHospitalizationROD == null && reimbursementDTO.getClaimDTO().getLatestPreauthKey() != null){
					dbCalculationService.reimbursementRollBackProc(reimbursementDTO.getClaimDTO().getLatestPreauthKey(), "C");
				} else if(partialHospitalizationROD != null) {
					dbCalculationService.reimbursementRollBackProc(partialHospitalizationROD.getKey(), "R");
				}
			}

			Reimbursement hospitalizationRod = reimbursementService.getHospitalizationRod(reimbursementDTO.getClaimKey(), reimbursementDTO.getKey());

//			Long transactionKey = null;
			
//			if(reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_CANCEL_ROD)){
//				
//				if(reimbursementDTO.getClaimDTO().getClaimType() != null && reimbursementDTO.getClaimDTO().getClaimType().getId().equals(ReferenceTable.CASHLESS_CLAIM_TYPE_KEY)){
//					
//					if(hospitalizationRod != null){
//						
//						transactionKey = hospitalizationRod.getKey();
//						dbCalculationService.reimbursementRollBackProc(transactionKey,"R");
//						
//					}else{
//						if(reimbursementDTO.getClaimDTO().getLatestPreauthKey() != null){
//							transactionKey = reimbursementDTO.getClaimDTO().getLatestPreauthKey();
//							dbCalculationService.reimbursementRollBackProc(transactionKey,"C");
//					    }
//					}
//				
//				}else{
//					if(hospitalizationRod != null){
//						transactionKey =  hospitalizationRod.getKey();
//					}else{
//						transactionKey = reimbursementDTO.getKey();
//					}
//					
//					dbCalculationService.reimbursementRollBackProc(transactionKey,"R");
//				}
	//
//			}
			
			if(ReferenceTable.getFHORevisedKeys().containsKey(reimbursementDTO.getNewIntimationDTO().getPolicy().getProduct().getKey())){
				policyService.updatePolicyBonusDetails(reimbursementDTO.getNewIntimationDTO().getPolicy().getPolicyNumber());
			}
			
			Reimbursement reimbursement = reimbursementService.submitClaimBilling(reimbursementDTO);
								
			Boolean shouldInvokeAcc = true;
			
			if((reimbursementDTO.getIsHospitalizationRepeat() != null && !reimbursementDTO.getIsHospitalizationRepeat()) && (reimbursementDTO.getHospitalizaionFlag() != null && !reimbursementDTO.getHospitalizaionFlag()) && (reimbursementDTO.getPartialHospitalizaionFlag() != null && !reimbursementDTO.getPartialHospitalizaionFlag())) {
				shouldInvokeAcc = false;
			}
			
			if(((reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_CANCEL_ROD)) || (reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_REFER_TO_BILL_ENTRY))) && !reimbursementDTO.getIsDirectToBilling() ) {
				
				if(reimbursementDTO.getClaimDTO().getClaimType() != null && reimbursementDTO.getClaimDTO().getClaimType().getId().equals(ReferenceTable.CASHLESS_CLAIM_TYPE_KEY)){
					if(hospitalizationRod == null){
						
						if(reimbursementDTO.getClaimDTO().getLatestPreauthKey() != null && shouldInvokeAcc){
							dbCalculationService.invokeAccumulatorProcedure(reimbursementDTO.getClaimDTO().getLatestPreauthKey());
						}else{
						
							Preauth latestPreauth = reimbursementService.getLatestPreauthByClaim(reimbursementDTO.getClaimDTO().getKey());
							if(shouldInvokeAcc) {
								dbCalculationService.invokeAccumulatorProcedure(latestPreauth.getKey());
							}
							
						}
					}else{
						
						if(hospitalizationRod.getKey().equals(reimbursementDTO.getKey())) {
							Reimbursement partialHospitalizationROD = reimbursementService.getPartialHospitalizationROD(reimbursementDTO.getClaimKey());
							if(shouldInvokeAcc) {
								if(partialHospitalizationROD != null) {
									dbCalculationService.invokeReimbursementAccumulatorProcedure(partialHospitalizationROD.getKey());
								} else {
									Preauth latestPreauth = reimbursementService.getLatestPreauthByClaim(reimbursementDTO.getClaimDTO().getKey());
									if(latestPreauth != null) {
										dbCalculationService.invokeAccumulatorProcedure(latestPreauth.getKey());
									}
								}
								
							}
						} else {
							if(shouldInvokeAcc) {
								dbCalculationService.invokeReimbursementAccumulatorProcedure(hospitalizationRod.getKey());
							}
						}
						
						
					}
				}else{
					if(hospitalizationRod != null && shouldInvokeAcc){
						dbCalculationService.invokeReimbursementAccumulatorProcedure(hospitalizationRod.getKey());
					}
				}
				
			}else {
				if(shouldInvokeAcc && !(reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_CANCEL_ROD)) && !(reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_REFER_TO_BILL_ENTRY))
						&& !(reimbursementDTO.getStatusKey().equals(ReferenceTable.BILLING_REFER_TO_MEDICALA_APPROVER))) {
					dbCalculationService.invokeReimbursementAccumulatorProcedure(reimbursementDTO.getKey());
				}
				
			}
			if(!ReferenceTable.getReferToKeys().containsKey(reimbursement.getStatus().getKey())) {
				dbCalculationService.updateProvisionAmount(reimbursement.getKey(), reimbursement.getClaim().getKey());
			}
			
			
			dbCalculationService.callBillAssessmentSheet(reimbursement.getKey());
			
			String strPremiaFlag = BPMClientContext.PREMIA_FLAG;
			if(strPremiaFlag != null && ("true").equalsIgnoreCase(strPremiaFlag)) {
				//try {
					Hospitals hospitalDetailsByKey = hospitalService.getHospitalDetailsByKey(reimbursement.getClaim().getIntimation().getHospital());
					Claim claim = claimService.getClaimByClaimKey(reimbursement.getClaim().getKey());
					String provisionAmtInput = SHAUtils.getProvisionAmtInput(claim, hospitalDetailsByKey.getName(), String.valueOf(claim.getCurrentProvisionAmount().longValue()));
					apiService.updateProvisionAmountToPremia(provisionAmtInput);
					
				/*} catch(Exception e) {
					
				}*/
			}
		
			view.buildSuccessLayout();
		} else {
			view.buildFailureLayout();
		}
		}else {
			view.alertForAlreadyAcquired(aquiredUser.toString());
		}
		
		
	}
	
	@SuppressWarnings("static-access")
	public void submitRoomRentMapping(
			@Observes @CDIEvent(SUBMIT_CLAIM_BILLING_MAPPING) final ParameterDTO parameters) {
		PreauthDTO reimbursementDTO = (PreauthDTO) parameters.getPrimaryParameter();
		reimbursementService.saveRoomRentNursingMapping(reimbursementDTO, reimbursementDTO.getRoomRentMappingDTOList());
		dbCalculationService.getBillDetailsSummary(reimbursementDTO.getKey());
	}
	
	public void saveRRCRequestValues(@Observes @CDIEvent(SAVE_BILLING_RRC_REQUEST_VALUES) final ParameterDTO parameters){
		PreauthDTO preauthDTO = (PreauthDTO) parameters.getPrimaryParameter();
		String rrcRequestNo = reimbursementService.submitRRCRequestValues(preauthDTO,SHAConstants.CLAIMREQUEST_REIMBURSEMENT);
		view.buildRRCRequestSuccessLayout(rrcRequestNo);
		
	}
	
	public void loadRRCRequestDropDownValues(
			@Observes @CDIEvent(BILLING_LOAD_RRC_REQUEST_DROP_DOWN_VALUES) final ParameterDTO parameters) 
		{
		BeanItemContainer<SelectValue> mastersValueContainer = masterService.getMasterValueByReference(ReferenceTable.SIGNIFICANT_CLINICAL_INFORMATION);
		view.loadRRCRequestDropDownValues(mastersValueContainer);
		}
	
	public void loadEmployeeDetails(@Observes @CDIEvent(BILLING_LOAD_EMPLOYEE_DETAILS) final ParameterDTO parameters){
		AutocompleteField<EmployeeMasterDTO> field = (AutocompleteField<EmployeeMasterDTO>) parameters.getPrimaryParameter();
		String query = parameters.getSecondaryParameter(0, String.class);
		List<EmployeeMasterDTO> employeeList = reimbursementService.getListOfEmployeeDetails(query);
		view.loadEmployeeMasterData(field, employeeList);
		
	}
	
	public void validatUserForRRCRequest(@Observes @CDIEvent(VALIDATE_BILLING_USER_RRC_REQUEST) final ParameterDTO parameters){
		PreauthDTO preauthDTO = (PreauthDTO) parameters.getPrimaryParameter();
		Boolean isValid = reimbursementService.validateUserForRRCRequest(preauthDTO);
		if(isValid){
			reimbursementService.loadRRCRequestValues(preauthDTO,0d,SHAConstants.BILLING);
		}
		view.buildValidationUserRRCRequestLayout(isValid);
		
	}


	public void showAlertMessageForAmount(@Observes @CDIEvent(BILLING_APPROVED_AMOUNT_VALIDATION) final ParameterDTO parameters)
	{
		String message = (String) parameters.getPrimaryParameter();
		view.confirmMessageForApprovedAmount(message);
		
	}
	
	public void preauthLumenRequest(@Observes @CDIEvent(VALIDATE_BILLING_USER_LUMEN_REQUEST) final ParameterDTO parameters){
		PreauthDTO preauthDTO = (PreauthDTO) parameters.getPrimaryParameter();
		view.buildInitiateLumenRequest(preauthDTO.getNewIntimationDTO().getIntimationId());
	}
	


	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}
	
	public void setUpSubCategoryValues(
			@Observes @CDIEvent(BILLING_LOAD_RRC_REQUEST_SUB_CATEGORY_VALUES) final ParameterDTO parameters) {
		Long categoryKey = (Long) parameters.getPrimaryParameter();
		GComboBox subCategory = (GComboBox) parameters.getSecondaryParameter(0, GComboBox.class);
		SelectValue sel = (SelectValue) parameters.getSecondaryParameter(1, SelectValue.class);
		BeanItemContainer<SelectValue> selectValueContainer = masterService.getRRCSubCatValues(categoryKey);
		view.setsubCategoryValues(selectValueContainer,subCategory,sel);
	}
	
	public void setUpsourceValues(
			@Observes @CDIEvent(BILLING_LOAD_RRC_REQUEST_SOURCE_VALUES) final ParameterDTO parameters) {
		Long subCategoryKey = (Long) parameters.getPrimaryParameter();
		GComboBox source = (GComboBox) parameters.getSecondaryParameter(0, GComboBox.class);
		SelectValue sel = (SelectValue) parameters.getSecondaryParameter(1, SelectValue.class);
		BeanItemContainer<SelectValue> selectValueContainer = masterService.getRRCSourceValues(subCategoryKey);
		view.setsourceValues(selectValueContainer,source,sel);
	}
}
