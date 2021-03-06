/**
 * 
 */
package com.shaic.paclaim.reimbursement.service;

/**
 * @author ntv.vijayar
 *
 */



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.fields.dto.AbstractDAO;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.arch.table.Pageable;
import com.shaic.domain.Claim;
import com.shaic.domain.DocAcknowledgement;
import com.shaic.domain.Hospitals;
import com.shaic.domain.Insured;
import com.shaic.domain.Intimation;
import com.shaic.domain.MastersValue;
import com.shaic.domain.Policy;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.ReimbursementQuery;
import com.shaic.domain.Status;
import com.shaic.domain.TmpCPUCode;
import com.shaic.domain.TmpEmployee;
import com.shaic.domain.preauth.Coordinator;
import com.shaic.domain.preauth.Speciality;
import com.shaic.domain.preauth.SpecialityType;
import com.shaic.domain.reimbursement.Specialist;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.shaic.reimbursement.medicalapproval.processclaimrequest.search.SearchProcessClaimRequestFormDTO;
import com.shaic.reimbursement.medicalapproval.processclaimrequest.search.SearchProcessClaimRequestMapper;
import com.shaic.reimbursement.medicalapproval.processclaimrequest.search.SearchProcessClaimRequestTableDTO;
import com.vaadin.v7.data.util.BeanItemContainer;

/**
 * @author ntv.narenj
 *
 */
@Stateless
public class PASearchProcessClaimRequestService extends AbstractDAO<Claim>{

	
	private final Logger log = LoggerFactory.getLogger(PASearchProcessClaimRequestService.class);
	
	@EJB
	private DBCalculationService dbCalculationService;
	
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	Map<Long, Object> workFlowMap= new WeakHashMap<Long, Object>();
	
	String userName;
	
	
	public PASearchProcessClaimRequestService() {
		super();
		
	}
	
	public  Page<SearchProcessClaimRequestTableDTO> search(
			SearchProcessClaimRequestFormDTO searchFormDTO,
			String userName, String passWord,String screenName) {
		List<Map<String, Object>> taskProcedure = null ;
		try{
			
			Date date1 = new Date();
			
			log.info("%%%%%%%%%%%%%%%%%%% SEARCH SERVICE STARTED %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ date1);
			
			String intimationNo = null != searchFormDTO && null != searchFormDTO.getIntimationNo() ? searchFormDTO.getIntimationNo() : null ;
			this.userName = userName;
			String policyNo = null != searchFormDTO && null != searchFormDTO.getPolicyNo() ? searchFormDTO.getPolicyNo() : null ;
			String intimationSource = null != searchFormDTO && null != searchFormDTO.getIntimationSource() ? searchFormDTO.getIntimationSource().getValue() : null;
			String hospitaltype = null != searchFormDTO && null != searchFormDTO.getHospitalType()? searchFormDTO.getHospitalType().getValue() : null;
			String networkHospitalId = null != searchFormDTO && null !=  searchFormDTO.getNetworkHospType() ? searchFormDTO.getNetworkHospType().getValue() : null;
			String treatementType = null != searchFormDTO && null != searchFormDTO.getTreatementType()  ? searchFormDTO.getTreatementType().getValue() : null;
			String speciality = null != searchFormDTO && null != searchFormDTO.getSpeciality() ? searchFormDTO.getSpeciality().getValue() : null;
			String cpuCode = null != searchFormDTO && searchFormDTO.getCpuCode() != null ? searchFormDTO.getCpuCode().getId() != null ? searchFormDTO.getCpuCode().getId().toString() : null : null;
			String productName = null != searchFormDTO && null != searchFormDTO.getProductName() ? searchFormDTO.getProductName().getValue() : null;
			String productId = null != searchFormDTO && null != searchFormDTO.getProductName() ? searchFormDTO.getProductName().getId() != null ? searchFormDTO.getProductName().getId().toString() : null : null;
			String requestedBy = null != searchFormDTO && null != searchFormDTO.getRequestedBy() ? searchFormDTO.getRequestedBy().getValue() != null ? searchFormDTO.getRequestedBy().getValue() : null : null;
			
			
			String priority = null != searchFormDTO &&  searchFormDTO.getPriority() != null ? searchFormDTO.getPriority().getValue() != null ? searchFormDTO.getPriority().getValue() : null : null;
			String source = null != searchFormDTO && searchFormDTO.getSource() != null ? searchFormDTO.getSource().getValue() != null ? searchFormDTO.getSource().getValue(): null : null;
			String type = null != searchFormDTO && searchFormDTO.getType() != null ? searchFormDTO.getType().getValue() != null ? searchFormDTO.getType().getValue(): null : null;
			
			
			SelectValue accDeath = searchFormDTO.getAccidentDeath();
			String accDeathVal = null;
			if(null != accDeath)
			{
				
				if(SHAConstants.ACCIDENT.equalsIgnoreCase(accDeath.getValue()))
				{
					accDeathVal = "A";
				}
				else
				{
					accDeathVal = "D";
				}
			}
			
			Double claimedAmountFrom = searchFormDTO.getClaimedAmountFrom();
			Double claimedAmountTo = searchFormDTO.getClaimedAmountTo();
			String strClaimType = "";
			
			if(searchFormDTO.getSource() != null && (searchFormDTO.getSource().getId().equals(ReferenceTable.BILLING_REFER_TO_MEDICALA_APPROVER)
					|| searchFormDTO.getSource().getId().equals(ReferenceTable.FINANCIAL_REFER_TO_MEDICAL_APPROVER))){
				//log.info("%%%%%%%%%%%%%%%%%%% BEFORE GETTING STATUS FROM STATUS TABLE-------> QUERY 1  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				Status status = entityManager.find(Status.class, searchFormDTO.getSource().getId());
				//log.info("%%%%%%%%%%%%%%%%%%% AFTER GETTING STATUS FROM STATUS TABLE-------> QUERY 1  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				source = status.getProcessValue();
			}
			
			
			Long rodKey;
			List<String> intimationNoList = new ArrayList<String>();
			List<Long> rodKeyList = new ArrayList<Long>();
			//List<Integer> taskNumber = new ArrayList<Integer>();
			Map<String, Object> mapValues = new WeakHashMap<String, Object>();
			Integer totalRecords = 0; 
			//List<Long> keys = new ArrayList<Long>(); 
			mapValues.put(SHAConstants.USER_ID, userName);
			mapValues.put(SHAConstants.CURRENT_Q, SHAConstants.MA_CURRENT_QUEUE);
			
			mapValues.put(SHAConstants.LOB, SHAConstants.PA_LOB);
			mapValues.put(SHAConstants.LOB_TYPE, SHAConstants.PA_LOB_TYPE);
			
			if(null != intimationNo && !intimationNo.isEmpty()){
			
				mapValues.put(SHAConstants.INTIMATION_NO, intimationNo);
				/*intimationType.setIntimationNumber(intimationNo);
				payloadBOType.setIntimation(intimationType);*/

				}
			
				if(null != policyNo && !policyNo.isEmpty()){
					
					mapValues.put(SHAConstants.POLICY_NUMBER, policyNo);
					/*policyType.setPolicyId(policyNo);
					payloadBOType.setPolicy(policyType);*/
				}
				if(null != intimationSource){
					
					mapValues.put(SHAConstants.INT_SOURCE, intimationSource);
				/*	intimationType.setIntimationSource(intimationSource);
					payloadBOType.setIntimation(intimationType);*/
				}
				
				if(null != hospitaltype){
					
					mapValues.put(SHAConstants.HOSPITAL_TYPE, hospitaltype);
				/*	hospitalInfoType.setHospitalType(hospitaltype);
					payloadBOType.setHospitalInfo(hospitalInfoType);*/

				}
				
				
				if(null != requestedBy){

					String[] split = requestedBy.split("-");
					String requestedString = split[0];
					if(requestedString != null){
						requestedString = requestedString.replaceAll("\\s","");
					}
				}
				 
				if(null != networkHospitalId){
					
					mapValues.put(SHAConstants.NETWORK_TYPE, networkHospitalId);
				/*	hospitalInfoType.setNetworkHospitalType(networkHospitalId);
					payloadBOType.setHospitalInfo(hospitalInfoType);*/
				}
				
			
				if(null != treatementType){
					
					mapValues.put(SHAConstants.TREATEMENT_TYPE, treatementType);
					/*customerType.setTreatmentType(treatementType);
					payloadBOType.setCustomer(customerType);*/
				}
				if(null != speciality){
					
					mapValues.put(SHAConstants.SPECIALITY_NAME, speciality);
				/*	customerType.setSpeciality(speciality);
					payloadBOType.setCustomer(customerType);*/
				}
				
				if(null != cpuCode){
					
					mapValues.put(SHAConstants.CPU_CODE, cpuCode);
					/*claimRequestType.setCpuCode(cpuCode);
					payloadBOType.setClaimRequest(claimRequestType);*/
				}
				

				if(null != searchFormDTO.getClaimType() && ! searchFormDTO.getClaimType().equals(""))
				{
					strClaimType= searchFormDTO.getClaimType().getValue();
					mapValues.put(SHAConstants.CLAIM_TYPE, strClaimType.toUpperCase());
				}	
				
				if(productName != null){
					mapValues.put(SHAConstants.PRODUCT_NAME, productName);

				}
				
			if (priority != null && !priority.isEmpty() || source != null
					&& !source.isEmpty() || type != null && !type.isEmpty()) {
					
					if(priority != null && ! priority.isEmpty())
						if(priority.equalsIgnoreCase(SHAConstants.ALL)){
							priority = null;
						}
					//classification.setPriority(priority);
					mapValues.put(SHAConstants.PRIORITY,priority);
					if(source != null && ! source.isEmpty()){
						//classification.setSource(source);
						mapValues.put(SHAConstants.RECORD_TYPE,source);
					}
					
					if(type != null && ! type.isEmpty()){
						if(type.equalsIgnoreCase(SHAConstants.ALL)){
							type = null;
						}
					//	classification.setType(type);
						mapValues.put(SHAConstants.RECORD_TYPE, type);
					}
					
			}
			

			//Pageable pageable = null;
			Pageable pageable = searchFormDTO.getPageable();
			pageable.setPageNumber(1);
			pageable.setPageSize(BPMClientContext.ITEMS_PER_PAGE != null ? Integer.valueOf(BPMClientContext.ITEMS_PER_PAGE) : 10);
			
			//Adding for test purpose
			Date date3 = new Date();
			workFlowMap= new WeakHashMap<Long, Object>();
			Object[] setMapValues = SHAUtils.setRevisedObjArrayForGetTask(mapValues);
			DBCalculationService dbCalculationService = new DBCalculationService();
			taskProcedure = dbCalculationService.revisedGetTaskProcedure(setMapValues);	
			
			if (null != taskProcedure) {
				for (Map<String, Object> outPutArray : taskProcedure) {							
						Long keyValue = (Long) outPutArray.get(SHAConstants.PAYLOAD_ROD_KEY);
						String intimationNumber = (String) outPutArray.get(SHAConstants.INTIMATION_NO);
						workFlowMap.put(keyValue,outPutArray);
						intimationNoList.add(intimationNumber);
						rodKeyList.add(keyValue);
						totalRecords = (Integer) outPutArray.get(SHAConstants.TOTAL_RECORDS);
				}
			}
			
			
		/*	if(null != processClaimRequest){
				Date datea4 = new Date();
				//log.info("%%%%%%%%%%%%%%%%%%% BEFORE INVOKING GET HUMAN TASKS FROM TASK LIST %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				List<HumanTask> humanTaskList = taskList.getHumanTasks();
				Date date5 = new Date();
				//log.info("%%%%%%%%%%%%%%%%%%% AFTER INVOKING GET HUMAN TASKS FROM TASK LIST %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				log.info("%%%%%%%%%%%%%%%%%%% TOTAL TIME TAKEN BY BPMN FOR GETTING TASKLIST  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ SHAUtils.getDurationFromTwoDate(datea4, date5));

				if(null == humanTaskList | humanTaskList.isEmpty()){
					return null;
				}

				for(HumanTask humanTask : humanTaskList){
						PayloadBOType payloadBO = humanTask.getPayload();
						if(payloadBO.getRod() != null){
						intimationNoList.add(payloadBO.getIntimation().getIntimationNumber());
						humanTaskListDTO.add(humanTask);
						rodKeyList.add(payloadBO.getRod().getKey());
						taskNumber.add(humanTask.getNumber());
						}
				}
			}*/
			
			
			log.info("%%%%%%%%%%%%%%%%%%% BEFORE DATA FETCHING FROM DB %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			List<SearchProcessClaimRequestTableDTO> tableDTO = new ArrayList<SearchProcessClaimRequestTableDTO>();
			
			for(int index = 0; index < intimationNoList.size(); index++){
				 if(index < rodKeyList.size()){
					 intimationNo = intimationNoList.get(index);
					 rodKey = rodKeyList.get(index);
					 tableDTO.addAll(getIntimationData(intimationNo, rodKey, searchFormDTO));
				 }
			}
			log.info("%%%%%%%%%%%%%%%%%%% AFTER DATA FETCHING FROM DB %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			
			//ImsUser imsUser = searchFormDTO.getImsUser();
			/*for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
				searchProcessClaimRequestTableDTO.setImsUser(imsUser);
				searchProcessClaimRequestTableDTO.setSearchDTO(searchFormDTO);
			}*/
			
			List<SearchProcessClaimRequestTableDTO> filterDTO = new ArrayList<SearchProcessClaimRequestTableDTO>();			
			if(claimedAmountFrom != null || claimedAmountTo != null){
				if(claimedAmountFrom != null && claimedAmountTo != null){
					for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
						if(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() != null && 
								(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() >= claimedAmountFrom && searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() <= claimedAmountTo)){
							filterDTO.add(searchProcessClaimRequestTableDTO);
						}
					}
				}else if(claimedAmountFrom != null){
					for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
						if(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() != null && 
								(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() >= claimedAmountFrom)){
							filterDTO.add(searchProcessClaimRequestTableDTO);
						}
					}
				}else if(claimedAmountTo != null){
					for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
						if(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() != null && 
								(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() <= claimedAmountTo)){
							filterDTO.add(searchProcessClaimRequestTableDTO);
						}
					}
				}
			}else{
				filterDTO.addAll(tableDTO);
			}
			
			
		
			Page<SearchProcessClaimRequestTableDTO> page = new Page<SearchProcessClaimRequestTableDTO>();
			//page.setPageNumber(taskList.getCurrentPage());
			page.setTotalRecords(totalRecords);
			page.setPageItems(filterDTO);
			if(null != searchFormDTO.getIntimationNo() || ("").equalsIgnoreCase(searchFormDTO.getIntimationNo()))
			{
				page.setSearchId(searchFormDTO.getIntimationNo());
			}
			
			if(!(null != page && null != page.getPageItems() && 0!= page.getPageItems().size()) && intimationNo != null && intimationNo.length() > 0) {
				Intimation intimationByNo = getIntimationByNo(intimationNo);
				if(intimationByNo == null) {
					page.setCorrectMsg("Intimation Not Found");
				} 
			}
			log.info("%%%%%%%%%%%%%%%%%%% END TIME OF GET TASK %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date() + ": "+ System.currentTimeMillis());
			return page;
			}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("hhhhhhhhhhhhhhhhhhhhhhhh"+e.getMessage()+"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		}
			
		return null;	
	}

	
	@SuppressWarnings("unchecked")
	public Intimation getIntimationByNo(String intimationNo) {
		Query findByKey = entityManager.createNamedQuery(
				"Intimation.findByIntimationNumber").setParameter(
				"intimationNo", intimationNo);

		List<Intimation> intimationList = (List<Intimation>) findByKey
				.getResultList();

		if (!intimationList.isEmpty()) {
			entityManager.refresh(intimationList.get(0));
			return intimationList.get(0);

		}
		return null;
	}
	
	private List<SearchProcessClaimRequestTableDTO> getIntimationData(String intimationNo,  Long rodKey,SearchProcessClaimRequestFormDTO formDTO/*, HumanTask humanTask,Integer taskNumber*/){
		//log.info("%%%%%%%%%%%%%%%%%%% BEFORE STARTING INTO getIntimationData METHOD %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
		//log.info("%%%%%%%%%%%%%%%%%%% BEFORE GETTING CRITERIA BUILDER OBJECT FROM ENTITY MANAGER %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		final CriteriaQuery<Claim> criteriaQuery = criteriaBuilder.createQuery(Claim.class);
		Claim objClaim = null;
	
		List<Claim> claimList = new ArrayList<Claim>(); 
		
		Root<Claim> root = criteriaQuery.from(Claim.class);
	
		List<Predicate> conditionList = new ArrayList<Predicate>();
		if(intimationNo != null){
			Predicate condition1 = criteriaBuilder.equal(root.<Intimation>get("intimation").<String>get("intimationId"), intimationNo);
			conditionList.add(condition1);
		}
		
		if(!intimationNo.isEmpty() || intimationNo != null){
			criteriaQuery.select(root).where(
			criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
			//log.info("%%%%%%%%%%%%%%%%%%% BEFORE EXECUTING CRITERIA QUERY FROM CLAIM CLASS THROUGH ENTITY MANAGER ----> QUERY 2 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			final TypedQuery<Claim> typedQuery = entityManager.createQuery(criteriaQuery);
			claimList = typedQuery.getResultList();
			//log.info("%%%%%%%%%%%%%%%%%%% AFTER EXECUTING CRITERIA QUERY FROM CLAIM CLASS THROUGH ENTITY MANAGER ----> QUERY 2 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
		}
			
	
			for(Claim inti:claimList){
			System.out.println(inti.getIntimation().getIntimationId()+"oooooooooooooooooooooooooo"+inti.getIntimation().getPolicy().getPolicyNumber()+"tttttttttttttt"+inti.getCreatedBy());
			}
			
			List<SearchProcessClaimRequestTableDTO> tableDTO = SearchProcessClaimRequestMapper.getInstance().getIntimationDTO(claimList);
			for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
				
				if(searchProcessClaimRequestTableDTO.getProductKey() != null && ReferenceTable.getGPAProducts().containsKey(searchProcessClaimRequestTableDTO.getProductKey())){
					if(searchProcessClaimRequestTableDTO.getPaPatientName() != null){
						searchProcessClaimRequestTableDTO.setInsuredPatientName(searchProcessClaimRequestTableDTO.getPaPatientName());
					}
				}
				searchProcessClaimRequestTableDTO.setUsername(userName);
				
			}
			
			getSpecilityAndTreatementType(tableDTO, rodKey);
			getHospitalDetails(tableDTO, rodKey/*, humanTask,taskNumber*/);
			//tableDTO = SearchAcknowledgementDocumentReceiverMapper.getClaimDTO(getclaimNumber(tableDTO));
			try{
				if(intimationNo != null){
					criteriaQuery.select(root).where(
					criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
					final TypedQuery<Claim> typedQuery = entityManager.createQuery(criteriaQuery);
					objClaim = typedQuery.getSingleResult();
				}
			for (SearchProcessClaimRequestTableDTO tableDTO1 : tableDTO) {
				tableDTO1.setImsUser(formDTO.getImsUser());
				tableDTO1.setSearchDTO(formDTO);
				populateClaimData(tableDTO1, objClaim);
				tableDTO1.setCoverCode(objClaim.getClaimCoverCode());
				tableDTO1.setSubCoverCode(objClaim.getClaimSubCoverCode());
				tableDTO1.setSectionCode(objClaim.getClaimSectionCode());
			}
		//	SearchProcessClaimRequestTableDTO tableDTO1 = new SearchProcessClaimRequestTableDTO();
			
		//	log.info("%%%%%%%%%%%%%%%%%%% BEFORE INVOKING SPECIALITY TREATMENT METHOD  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			;
		//	log.info("%%%%%%%%%%%%%%%%%%% AFTER INVOKING SPECIALITY TREATMENT METHOD AND BEFORE INVOKING GET HOSPITAL DETAILS METHOD  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
		


			//log.info("%%%%%%%%%%%%%%%%%%% FINISHED PROCESSING getIntimationData METHOD AND AFTER INVOKING GET HOSPITALS DETAILS METHOD %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			return tableDTO;
		}catch(Exception e){
			return null;
		}
		
	}

	

	@Override
	public Class<Claim> getDTOClass() {
		// TODO Auto-generated method stub
		return Claim.class;
	}
	

	private List<SearchProcessClaimRequestTableDTO> getHospitalDetails(
			List<SearchProcessClaimRequestTableDTO> tableDTO, Long rodKey/*, HumanTask humanTask,Integer taskNumber*/) {
		
		Hospitals hospitalDetail;
		for(int index = 0; index < tableDTO.size(); index++){
			System.out.print(rodKey);
			 tableDTO.get(index).setRodKey(rodKey);
			 
				Object workflowKey = workFlowMap.get(tableDTO.get(index).getRodKey());
				Map<String, Object> outPutArray = (Map<String, Object>)workflowKey;
				String claimedAmount = (String) outPutArray.get(SHAConstants.CLAIMED_AMOUNT);
				String status = (String) outPutArray.get(SHAConstants.RECORD_TYPE);
				if(claimedAmount != null){
				tableDTO.get(index).setClaimedAmountAsPerBill(Double.valueOf(claimedAmount));
				}
				
//				tableDto.setDbOutArray(workflowKey);
				tableDTO.get(index).setDbOutArray(workflowKey);
			 
			 try{
					if(rodKey != null){
						Reimbursement reimbursementByKey = getReimbursementByKey(rodKey);
						 if(reimbursementByKey.getDocAcknowLedgement() != null && reimbursementByKey.getDocAcknowLedgement().getDocumentReceivedFromId() != null
								 && reimbursementByKey.getDocAcknowLedgement().getDocumentReceivedFromId().getValue() != null){
							 tableDTO.get(index).setDocumentReceivedFrom(reimbursementByKey.getDocAcknowLedgement().getDocumentReceivedFromId().getValue());
						 }
					    String rodAgeing = SHAUtils.getRodAgeing(reimbursementByKey.getCreatedDate());
					    tableDTO.get(index).setRodAgeing(rodAgeing);
					    
					    String ackAgeing = SHAUtils.getAckAgeing(reimbursementByKey.getDocAcknowLedgement().getCreatedDate());
						tableDTO.get(index).setAckAgeing(ackAgeing);
						
						if(status != null){
							tableDTO.get(index).setStatus(status);
						}
					}
				}catch(Exception e){
					
				}
			 
			 
			 Reimbursement reimbursementByKey = getReimbursementByKey(rodKey);
			 tableDTO.get(index).setOriginatorID(reimbursementByKey.getModifiedBy());
			 TmpEmployee employeeName = getEmployeeName(reimbursementByKey.getModifiedBy() != null ? reimbursementByKey.getModifiedBy().toLowerCase() : "");
			 if(employeeName != null) {
				 tableDTO.get(index).setOriginatorName(employeeName.getEmpFirstName());
			 }
			 
			 if(null != reimbursementByKey && null != reimbursementByKey.getClaim().getKey())
			 {
				 Claim claim = getClaimByKey(reimbursementByKey.getClaim().getKey());
				 
				 if((SHAConstants.ACCIDENT_FLAG).equalsIgnoreCase(claim.getIncidenceFlag()))
					{
						tableDTO.get(index).setAccidentOrdeath(SHAConstants.ACCIDENT);
					}
					else
					{
						tableDTO.get(index).setAccidentOrdeath(SHAConstants.DEATH);
					}				
			 }
			 if(null != reimbursementByKey)
			 {			 
			 if(null != reimbursementByKey.getBenApprovedAmt()){
					tableDTO.get(index).setClaimedAmountAsPerBill(reimbursementByKey.getBenApprovedAmt());
					}
			 }
			 if(reimbursementByKey != null && reimbursementByKey.getStatus() != null && (reimbursementByKey.getStatus().getKey().equals(ReferenceTable.CLAIM_REQUEST_SPECIALIST_REPLY_RECEIVED_STATUS))) {
				Specialist specialistByClaimKey = getSpecialistByClaimKey(reimbursementByKey.getClaim().getKey());
				if(specialistByClaimKey != null) {
					//log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> BEFORE EXECUTING QUERY FOR GETTING EMPLOYEE OBJECT. ----> QUERY 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
					 TmpEmployee specialistRequestedEmp = getEmployeeName(specialistByClaimKey.getCreatedBy() != null ? specialistByClaimKey.getCreatedBy().toLowerCase() : "");
					// log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> AFTER EXECUTING QUERY FOR GETTING EMPLOYEE OBJECT. ----> QUERY 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
					 tableDTO.get(index).setSpecialistOrQueryOrCoordinatorReqId(specialistByClaimKey.getCreatedBy());
					 tableDTO.get(index).setSpecialistOrQueryOrCoordinatorReqName(specialistRequestedEmp != null ? specialistRequestedEmp.getEmpFirstName() : "");
				}
			 } else if(reimbursementByKey != null && reimbursementByKey.getStatus() != null && (reimbursementByKey.getStatus().getKey().equals(ReferenceTable.CLAIM_REQUEST_COORDINATOR_REPLY_RECEIVED_STATUS))) {
				 Coordinator findCoordinatorByClaimKey = findCoordinatorByClaimKey(reimbursementByKey.getClaim().getKey());
				 if(findCoordinatorByClaimKey != null) {
					//	log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> BEFORE EXECUTING QUERY FOR GETTING EMPLOYEE OBJECT. ----> QUERY 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());

					 TmpEmployee coordinatorRequestedEmp = getEmployeeName(findCoordinatorByClaimKey.getCreatedBy() != null ? findCoordinatorByClaimKey.getCreatedBy().toLowerCase() : "");
						//log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> AFTER EXECUTING QUERY FOR GETTING EMPLOYEE OBJECT. ----> QUERY 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());

					 tableDTO.get(index).setSpecialistOrQueryOrCoordinatorReqId(findCoordinatorByClaimKey.getCreatedBy());
					 tableDTO.get(index).setSpecialistOrQueryOrCoordinatorReqName(coordinatorRequestedEmp != null ? coordinatorRequestedEmp.getEmpFirstName(): "");
				}
			 } else {
				 ReimbursementQuery reimbursementQueryDocAckKey = getReimbursementQueryDocAckKey(reimbursementByKey.getDocAcknowLedgement() != null ? reimbursementByKey.getDocAcknowLedgement().getKey() : 0l);
				 if(reimbursementQueryDocAckKey != null) {
						//log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> BEFORE EXECUTING QUERY FOR GETTING EMPLOYEE OBJECT. ----> QUERY 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());

					 TmpEmployee queryRequestedEmp = getEmployeeName(reimbursementQueryDocAckKey.getCreatedBy() != null ? reimbursementQueryDocAckKey.getCreatedBy().toLowerCase() : "");
					//	log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> AFTER EXECUTING QUERY FOR GETTING EMPLOYEE OBJECT. ----> QUERY 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());

					 tableDTO.get(index).setSpecialistOrQueryOrCoordinatorReqId(reimbursementQueryDocAckKey.getCreatedBy());
					 tableDTO.get(index).setSpecialistOrQueryOrCoordinatorReqName(queryRequestedEmp != null ? queryRequestedEmp.getEmpFirstName(): "");
				 }
			 } 
		/*	 tableDTO.get(index).setHumanTaskDTO(humanTask);
			 if(humanTask.getPayload() != null && humanTask.getPayload().getPaymentInfo() != null && humanTask.getPayload().getPaymentInfo().getClaimedAmount() != null){
				 tableDTO.get(index).setClaimedAmountAsPerBill(humanTask.getPayload().getPaymentInfo().getClaimedAmount());
			 }*/
			 tableDTO.get(index).setBalanceSI(getBalanceSI(tableDTO.get(index).getInsuredKey(),
			 tableDTO.get(index).getClaimKey(), rodKey, reimbursementByKey.getBenefitsId() != null ? reimbursementByKey.getBenefitsId().getKey() : 0l));
			// tableDTO.get(index).setTaskNumber(taskNumber);
			 
				//log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> BEFORE EXECUTING QUERY FOR GETTING HOSPITAL OBJECT. ----> QUERY 5 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());

			Query findByHospitalKey = entityManager.createNamedQuery(
					"Hospitals.findByKey").setParameter("key", tableDTO.get(index).getHospitalNameID());
			
			//log.info("%%%%%%%%%%%%%%%%%%% getHospitalDetails method ----> BEFORE EXECUTING QUERY FOR GETTING HOSPITAL OBJECT. ----> QUERY 5 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());

			try{
			 hospitalDetail = (Hospitals) findByHospitalKey.getSingleResult();
			 if(hospitalDetail != null){
				 
				 tableDTO.get(index).setHospitalName(hospitalDetail.getName());
//				 tableDTO.get(index).setCpuId(hospitalDetail.getCpuId());
//				 String cpuName = null != getTmpCPUName(tableDTO.get(index).getCpuId()) ? getTmpCPUName(tableDTO.get(index).getCpuId()).getDescription() : "";		
//				 tableDTO.get(index).setCpuName(cpuName);
				 
			 }
			}catch(Exception e){
				continue;
			}
		
		}
		
		
		return tableDTO;
	}
	
	public Coordinator findCoordinatorByClaimKey(Long claimKey) {
		Query query = entityManager
				.createNamedQuery("Coordinator.findByClaimKey");
		query.setParameter("claimKey", claimKey);
		List resultList = query.getResultList();
		Coordinator object = null;
		if(!resultList.isEmpty()) {
			 object = (Coordinator) resultList.get(0);
		}
		return object;
	}
	
	  public TmpEmployee getEmployeeName(String initiatorId)
		{
		  TmpEmployee fvrInitiatorDetail;
			Query findByTransactionKey = entityManager.createNamedQuery(
					"TmpEmployee.getEmpByLoginId").setParameter("loginId", initiatorId);
			try{
				fvrInitiatorDetail =(TmpEmployee) findByTransactionKey.getSingleResult();
				return fvrInitiatorDetail;
			}
			catch(Exception e)
			{
				return null;
			}
								
		}

	private TmpCPUCode getTmpCPUName(Long cpuId){
		try{
		Query findCpuCode = entityManager.createNamedQuery("TmpCPUCode.findByKey").setParameter("cpuId", cpuId);
		 TmpCPUCode tmpCPUCode = (TmpCPUCode) findCpuCode.getSingleResult();
		return tmpCPUCode;
		}catch(Exception e){
			return null;
		}
		
	}
	
	private Double getSumInsured(Long insuredKey,Long policyKey){
		try{
			Query findInsuredKey = entityManager.createNamedQuery("Insured.findByInsured").setParameter("key", insuredKey);
			Insured insured = (Insured) findInsuredKey.getSingleResult();
			return dbCalculationService.getInsuredSumInsured(String.valueOf(insured.getInsuredId()), policyKey,"P");
			}catch(Exception e){
				
				return null;
			}
			
		
	}
	
//	private Double getBalanceSI(Long policyKey, Long insuredKey, Long claimKey,
//			Double sumInsured){
//		Double balanceSI = 0.0;
//		System.out.println("policyKey----------"+policyKey+"-------------insuredkey----------"+insuredKey+"-----------cliamkey--------"+claimKey+"--------------suminsured"+sumInsured);
//		if(policyKey !=null && insuredKey != null && sumInsured != null){
//		balanceSI = dbCalculationService.getBalanceSI(policyKey ,insuredKey, claimKey, sumInsured).get(SHAConstants.CURRENT_BALANCE_SI);
//		}
//				return balanceSI;
//		
//	}
	
	private Double getBalanceSI(Long insuredKey, Long claimKey,Long rodKey, Long benefitId){
		Double balanceSI = 0.0;
		if(insuredKey != null){
		balanceSI = dbCalculationService.getPABalanceSI(insuredKey, claimKey, rodKey, benefitId);
		}
				return balanceSI;
		
	}
	private List<SearchProcessClaimRequestTableDTO> getSpecilityAndTreatementType(List<SearchProcessClaimRequestTableDTO> intimationList, Long rodKey){
		
		for(int index = 0; index < intimationList.size(); index++){
			System.out.println("Intimationkey+++++++++++++++++++++"+intimationList.get(index).getIntimationKey());
						
						
							intimationList.get(index).setSpeciality(getSpecialityType(intimationList.get(index).getClaimKey()));
							intimationList.get(index).setTreatementType(getTreatementType(getAcknowledgementKey(rodKey)));
							intimationList.get(index).setRequestedAmt(getRequestAmt(rodKey));
							log.info("%%%%%%%%%%%%%%%%%%% getSpecilityAndTreatementType method ----> BEFORE EXECUTING QUERY FOR GETTING REIMB OBJECT. ----> QUERY 3 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
							Reimbursement reimbursement = getReimbursementByKey(rodKey);
							log.info("%%%%%%%%%%%%%%%%%%% getSpecilityAndTreatementType method ----> AFTER EXECUTING QUERY FOR GETTING REIMB OBJECT. ----> QUERY 3 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
							if(reimbursement != null){
								intimationList.get(index).setOriginatorID(reimbursement.getCreatedBy());
							}

		}
		return intimationList;
	}
	private String getSpecialityType(Long claimKey){
		try{
			String specilityValue ="";
			Query findCpuCode = entityManager.createNamedQuery("Speciality.findByClaimKey").setParameter("claimKey", claimKey);
			List<Speciality> SpecialityList = findCpuCode.getResultList();
			for(Speciality speciality : SpecialityList){ 
				Query findSpecilty = entityManager.createNamedQuery("SpecialityType.findByKey").setParameter("key", speciality.getSpecialityType().getKey());
				SpecialityType result = (SpecialityType) findSpecilty.getSingleResult(); 
				specilityValue += result.getValue()+", ";
			}
			
			return specilityValue;
			}catch(Exception e){
				return null;
			}
		
	}
	
	private String getTreatementType(Long ackKey){
		try{
			String specilityValue ="";
			Query findCpuCode = entityManager.createNamedQuery("Reimbursement.findByAcknowledgement").setParameter("docAcknowledgmentKey", ackKey);
			List<Reimbursement> SpecialityList = findCpuCode.getResultList();
			for(Reimbursement speciality : SpecialityList){ 
				System.out.println("treatement type"+speciality.getTreatmentType().getValue());
				specilityValue += speciality.getTreatmentType().getValue()+",";
			}
			
			if(specilityValue != null){
				specilityValue = SHAUtils.removeLastChar(specilityValue);
			}
			
			return specilityValue;
			}catch(Exception e){
				
				return null;
			}
	}
	
	
	private Double getRequestAmt(Long rodKey){
		try{
			Double claimedAmount = 0.0;
			Query findType = entityManager.createNamedQuery(
					"DocAcknowledgement.findByLatestAcknowledge").setParameter("rodKey", rodKey);
			List<DocAcknowledgement> reimbursement = (List<DocAcknowledgement>) findType
					.getResultList();
			
			DocAcknowledgement docAcknowledgement = new DocAcknowledgement();
		    if(reimbursement != null && ! reimbursement.isEmpty()){
		    	docAcknowledgement = reimbursement.get(0);
		    }
			
		    if(docAcknowledgement.getBenifitClaimedAmount() != null){
		    	claimedAmount = docAcknowledgement.getBenifitClaimedAmount();
		    }
			return claimedAmount;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Reimbursement getReimbursementByKey(Long rodKey){
		Query query = entityManager
				.createNamedQuery("Reimbursement.findByKey").setParameter(
						"primaryKey", rodKey);
		List<Reimbursement> rodList = query.getResultList();
		
		if(rodList != null && !rodList.isEmpty()) {
			for (Reimbursement reimbursement : rodList) {
				entityManager.refresh(reimbursement);
			}
			return rodList.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ReimbursementQuery getReimbursementQueryDocAckKey(Long rodKey){
		Query query = entityManager
				.createNamedQuery("ReimbursementQuery.findLatestDocAckKey").setParameter(
						"docAckKey", rodKey);
		List<ReimbursementQuery> rodList = query.getResultList();
		
		if(rodList != null && !rodList.isEmpty()) {
			for (ReimbursementQuery reimbursement : rodList) {
				entityManager.refresh(reimbursement);
			}
			return rodList.get(0);
		}
		return null;
	}
	
	
	
	private Long getAcknowledgementKey(Long rodKey){
		try{
			
			Long value = 0L;
			Query findCpuCode = entityManager.createNamedQuery("DocAcknowledgement.findByRODKey").setParameter("rodKey", rodKey);
			List<DocAcknowledgement> SpecialityList = findCpuCode.getResultList();
			for(DocAcknowledgement docAcknowledgement : SpecialityList){ 
				 value = docAcknowledgement.getKey();
				
			}
			
			return value;
			}catch(Exception e){
				
				return null;
			}
	}
	
	public Specialist getSpecialistByClaimKey(Long claimKey){
		
		Query query = entityManager
				.createNamedQuery("Specialist.findByClaimKey");
		query.setParameter("claimKey", claimKey);
		List<Specialist> resultList = (List<Specialist>) query
				.getResultList();
		
		if(resultList != null && ! resultList.isEmpty()){
			return resultList.get(0);
		}
		return null;
		
	}
	
	/*------------------------------------------------------FORM SERVICE--------------------------------------------------------*/
	@SuppressWarnings({ "unchecked", "unused" })
	public BeanItemContainer<SelectValue> getSpecialityValueByReference(Long treatmentTypeId) {
		// Query findAll =
		// entityManager.createNamedQuery("CityTownVillage.findAll");
		Query query = entityManager
				.createNamedQuery("SpecialityType.findBytreatmentTypeId").setParameter("treatmentTypeId", treatmentTypeId);
		List<SpecialityType> specialityValueList = query.getResultList();
		List<SelectValue> selectValuesList = new ArrayList<SelectValue>();
		BeanItemContainer<SelectValue> specilityValueContainer = new BeanItemContainer<SelectValue>(
				SelectValue.class);
		for (SpecialityType value : specialityValueList) {
			SelectValue select = new SelectValue();
			select.setId(value.getKey());
			select.setValue(value.getValue().toString());
			selectValuesList.add(select);
		}
		specilityValueContainer.addAll(selectValuesList);

		return specilityValueContainer;
	}
	
//	@SuppressWarnings("unchecked")
//	public Reimbursement getReimbursementByKey(Long rodKey) {
//		Query query = entityManager.createNamedQuery("Reimbursement.findByKey")
//				.setParameter("primaryKey", rodKey);
//		List<Reimbursement> rodList = query.getResultList();
//
//		if (rodList != null && !rodList.isEmpty()) {
//			for (Reimbursement reimbursement : rodList) {
//				entityManager.refresh(reimbursement);
//			}
//			return rodList.get(0);
//		}
//		return null;
//	}
	
	public Claim getClaimByKey(Long claimKey){
		
		Query query = entityManager
				.createNamedQuery("Claim.findByKey");
		query.setParameter("primaryKey", claimKey);
		List<Claim> resultList = (List<Claim>) query
				.getResultList();
		
		if(resultList != null && ! resultList.isEmpty()){
			return resultList.get(0);
		}
		return null;
		
	}
	
	private void populateClaimData(SearchProcessClaimRequestTableDTO dto , Claim objClaim)
	{
		dto.setClaimKey(objClaim.getKey());
		Intimation objIntimation = objClaim.getIntimation();
		Policy objPolicy = objIntimation.getPolicy();
		dto.setIntimationKey(objIntimation.getKey());
		dto.setIntimationNo(objIntimation.getIntimationId());
		dto.setPolicyKey(objPolicy.getKey());
		dto.setPolicyNumber(objPolicy.getPolicyNumber());
		dto.setSumInsured(objPolicy.getTotalSumInsured());
		dto.setInsuredPatientName(objIntimation.getInsured().getInsuredName());
		
		dto.setHospitalNameID(objIntimation.getHospital());
		dto.setProductName(objPolicy.getProductName());
		dto.setInsuredKey(objIntimation.getInsured().getKey());
		dto.setCpuName(objIntimation.getCpuCode().getDescription());
		MastersValue sourceValue = objIntimation.getIntimationSource();
		if(null != sourceValue)
			dto.setIntimationSource(sourceValue.getValue());
		MastersValue hospital = objIntimation.getHospitalType();
		if(null != hospital)
			dto.setHospitalType(hospital.getValue());
		dto.setReasonForAdmission(objIntimation.getAdmissionReason());
		MastersValue claimType = objClaim.getClaimType();
		if(null != claimType)
			dto.setClaimType(claimType.getValue());
	}
	
	public  Page<SearchProcessClaimRequestTableDTO> searchForWaitForInput(
			SearchProcessClaimRequestFormDTO searchFormDTO,
			String userName, String passWord,String screenName) {
		List<Map<String, Object>> taskProcedure = null ;
		try{
			
			Date date1 = new Date();
			
			log.info("%%%%%%%%%%%%%%%%%%% SEARCH SERVICE STARTED %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ date1);
			
			String intimationNo = null != searchFormDTO && null != searchFormDTO.getIntimationNo() ? searchFormDTO.getIntimationNo() : null ;
			this.userName = userName;
			String policyNo = null != searchFormDTO && null != searchFormDTO.getPolicyNo() ? searchFormDTO.getPolicyNo() : null ;
			String intimationSource = null != searchFormDTO && null != searchFormDTO.getIntimationSource() ? searchFormDTO.getIntimationSource().getValue() : null;
			String hospitaltype = null != searchFormDTO && null != searchFormDTO.getHospitalType()? searchFormDTO.getHospitalType().getValue() : null;
			String networkHospitalId = null != searchFormDTO && null !=  searchFormDTO.getNetworkHospType() ? searchFormDTO.getNetworkHospType().getValue() : null;
			String treatementType = null != searchFormDTO && null != searchFormDTO.getTreatementType()  ? searchFormDTO.getTreatementType().getValue() : null;
			String speciality = null != searchFormDTO && null != searchFormDTO.getSpeciality() ? searchFormDTO.getSpeciality().getValue() : null;
			String cpuCode = null != searchFormDTO && searchFormDTO.getCpuCode() != null ? searchFormDTO.getCpuCode().getId() != null ? searchFormDTO.getCpuCode().getId().toString() : null : null;
			String productName = null != searchFormDTO && null != searchFormDTO.getProductName() ? searchFormDTO.getProductName().getValue() : null;
			String productId = null != searchFormDTO && null != searchFormDTO.getProductName() ? searchFormDTO.getProductName().getId() != null ? searchFormDTO.getProductName().getId().toString() : null : null;
			String requestedBy = null != searchFormDTO && null != searchFormDTO.getRequestedBy() ? searchFormDTO.getRequestedBy().getValue() != null ? searchFormDTO.getRequestedBy().getValue() : null : null;
			
			
			String priority = null != searchFormDTO &&  searchFormDTO.getPriority() != null ? searchFormDTO.getPriority().getValue() != null ? searchFormDTO.getPriority().getValue() : null : null;
			String source = null != searchFormDTO && searchFormDTO.getSource() != null ? searchFormDTO.getSource().getValue() != null ? searchFormDTO.getSource().getValue(): null : null;
			String type = null != searchFormDTO && searchFormDTO.getType() != null ? searchFormDTO.getType().getValue() != null ? searchFormDTO.getType().getValue(): null : null;
			
			
			SelectValue accDeath = searchFormDTO.getAccidentDeath();
			String accDeathVal = null;
			if(null != accDeath)
			{
				
				if(SHAConstants.ACCIDENT.equalsIgnoreCase(accDeath.getValue()))
				{
					accDeathVal = "A";
				}
				else
				{
					accDeathVal = "D";
				}
			}
			
			Double claimedAmountFrom = searchFormDTO.getClaimedAmountFrom();
			Double claimedAmountTo = searchFormDTO.getClaimedAmountTo();
			String strClaimType = "";
			
			if(searchFormDTO.getSource() != null && (searchFormDTO.getSource().getId().equals(ReferenceTable.BILLING_REFER_TO_MEDICALA_APPROVER)
					|| searchFormDTO.getSource().getId().equals(ReferenceTable.FINANCIAL_REFER_TO_MEDICAL_APPROVER))){
				//log.info("%%%%%%%%%%%%%%%%%%% BEFORE GETTING STATUS FROM STATUS TABLE-------> QUERY 1  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				Status status = entityManager.find(Status.class, searchFormDTO.getSource().getId());
				//log.info("%%%%%%%%%%%%%%%%%%% AFTER GETTING STATUS FROM STATUS TABLE-------> QUERY 1  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				source = status.getProcessValue();
			}
			
			
			Long rodKey;
			List<String> intimationNoList = new ArrayList<String>();
			List<Long> rodKeyList = new ArrayList<Long>();
			//List<Integer> taskNumber = new ArrayList<Integer>();
			Map<String, Object> mapValues = new WeakHashMap<String, Object>();
			Integer totalRecords = 0; 
			//List<Long> keys = new ArrayList<Long>(); 
			mapValues.put(SHAConstants.USER_ID, userName);
			mapValues.put(SHAConstants.CURRENT_Q, SHAConstants.WAIT_FOR_INPUT_CURRENT_QUEUE);
			
			mapValues.put(SHAConstants.LOB, SHAConstants.PA_LOB);
			mapValues.put(SHAConstants.LOB_TYPE, SHAConstants.PA_LOB_TYPE);
			
			if(null != intimationNo && !intimationNo.isEmpty()){
			
				mapValues.put(SHAConstants.INTIMATION_NO, intimationNo);
				/*intimationType.setIntimationNumber(intimationNo);
				payloadBOType.setIntimation(intimationType);*/

				}
			
				if(null != policyNo && !policyNo.isEmpty()){
					
					mapValues.put(SHAConstants.POLICY_NUMBER, policyNo);
					/*policyType.setPolicyId(policyNo);
					payloadBOType.setPolicy(policyType);*/
				}
				if(null != intimationSource){
					
					mapValues.put(SHAConstants.INT_SOURCE, intimationSource);
				/*	intimationType.setIntimationSource(intimationSource);
					payloadBOType.setIntimation(intimationType);*/
				}
				
				if(null != hospitaltype){
					
					mapValues.put(SHAConstants.HOSPITAL_TYPE, hospitaltype);
				/*	hospitalInfoType.setHospitalType(hospitaltype);
					payloadBOType.setHospitalInfo(hospitalInfoType);*/

				}
				
				
				if(null != requestedBy){

					String[] split = requestedBy.split("-");
					String requestedString = split[0];
					if(requestedString != null){
						requestedString = requestedString.replaceAll("\\s","");
					}
				}
				 
				if(null != networkHospitalId){
					
					mapValues.put(SHAConstants.NETWORK_TYPE, networkHospitalId);
				/*	hospitalInfoType.setNetworkHospitalType(networkHospitalId);
					payloadBOType.setHospitalInfo(hospitalInfoType);*/
				}
				
			
				if(null != treatementType){
					
					mapValues.put(SHAConstants.TREATEMENT_TYPE, treatementType);
					/*customerType.setTreatmentType(treatementType);
					payloadBOType.setCustomer(customerType);*/
				}
				if(null != speciality){
					
					mapValues.put(SHAConstants.SPECIALITY_NAME, speciality);
				/*	customerType.setSpeciality(speciality);
					payloadBOType.setCustomer(customerType);*/
				}
				
				if(null != cpuCode){
					
					mapValues.put(SHAConstants.CPU_CODE, cpuCode);
					/*claimRequestType.setCpuCode(cpuCode);
					payloadBOType.setClaimRequest(claimRequestType);*/
				}
				

				if(null != searchFormDTO.getClaimType() && ! searchFormDTO.getClaimType().equals(""))
				{
					strClaimType= searchFormDTO.getClaimType().getValue();
					mapValues.put(SHAConstants.CLAIM_TYPE, strClaimType.toUpperCase());
				}	
				
				if(productName != null){
					mapValues.put(SHAConstants.PRODUCT_NAME, productName);

				}
				
			if (priority != null && !priority.isEmpty() || source != null
					&& !source.isEmpty() || type != null && !type.isEmpty()) {
					
					if(priority != null && ! priority.isEmpty())
						if(priority.equalsIgnoreCase(SHAConstants.ALL)){
							priority = null;
						}
					//classification.setPriority(priority);
					mapValues.put(SHAConstants.PRIORITY,priority);
					if(source != null && ! source.isEmpty()){
						//classification.setSource(source);
						mapValues.put(SHAConstants.RECORD_TYPE,source);
					}
					
					if(type != null && ! type.isEmpty()){
						if(type.equalsIgnoreCase(SHAConstants.ALL)){
							type = null;
						}
					//	classification.setType(type);
						mapValues.put(SHAConstants.RECORD_TYPE, type);
					}
					
			}
			

			//Pageable pageable = null;
			Pageable pageable = searchFormDTO.getPageable();
			pageable.setPageNumber(1);
			pageable.setPageSize(BPMClientContext.ITEMS_PER_PAGE != null ? Integer.valueOf(BPMClientContext.ITEMS_PER_PAGE) : 10);
			
			//Adding for test purpose
			Date date3 = new Date();
			workFlowMap= new WeakHashMap<Long, Object>();
			Object[] setMapValues = SHAUtils.setRevisedObjArrayForGetTask(mapValues);
			DBCalculationService dbCalculationService = new DBCalculationService();
			taskProcedure = dbCalculationService.revisedGetTaskProcedure(setMapValues);	
			
			if (null != taskProcedure) {
				for (Map<String, Object> outPutArray : taskProcedure) {							
						Long keyValue = (Long) outPutArray.get(SHAConstants.PAYLOAD_ROD_KEY);
						String intimationNumber = (String) outPutArray.get(SHAConstants.INTIMATION_NO);
						workFlowMap.put(keyValue,outPutArray);
						intimationNoList.add(intimationNumber);
						rodKeyList.add(keyValue);
						totalRecords = (Integer) outPutArray.get(SHAConstants.TOTAL_RECORDS);
				}
			}
			
			
		/*	if(null != processClaimRequest){
				Date datea4 = new Date();
				//log.info("%%%%%%%%%%%%%%%%%%% BEFORE INVOKING GET HUMAN TASKS FROM TASK LIST %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				List<HumanTask> humanTaskList = taskList.getHumanTasks();
				Date date5 = new Date();
				//log.info("%%%%%%%%%%%%%%%%%%% AFTER INVOKING GET HUMAN TASKS FROM TASK LIST %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
				log.info("%%%%%%%%%%%%%%%%%%% TOTAL TIME TAKEN BY BPMN FOR GETTING TASKLIST  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ SHAUtils.getDurationFromTwoDate(datea4, date5));

				if(null == humanTaskList | humanTaskList.isEmpty()){
					return null;
				}

				for(HumanTask humanTask : humanTaskList){
						PayloadBOType payloadBO = humanTask.getPayload();
						if(payloadBO.getRod() != null){
						intimationNoList.add(payloadBO.getIntimation().getIntimationNumber());
						humanTaskListDTO.add(humanTask);
						rodKeyList.add(payloadBO.getRod().getKey());
						taskNumber.add(humanTask.getNumber());
						}
				}
			}*/
			
			
			log.info("%%%%%%%%%%%%%%%%%%% BEFORE DATA FETCHING FROM DB %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			List<SearchProcessClaimRequestTableDTO> tableDTO = new ArrayList<SearchProcessClaimRequestTableDTO>();
			
			for(int index = 0; index < intimationNoList.size(); index++){
				 if(index < rodKeyList.size()){
					 intimationNo = intimationNoList.get(index);
					 rodKey = rodKeyList.get(index);
					 tableDTO.addAll(getIntimationData(intimationNo, rodKey, searchFormDTO));
				 }
			}
			log.info("%%%%%%%%%%%%%%%%%%% AFTER DATA FETCHING FROM DB %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date());
			
			//ImsUser imsUser = searchFormDTO.getImsUser();
			/*for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
				searchProcessClaimRequestTableDTO.setImsUser(imsUser);
				searchProcessClaimRequestTableDTO.setSearchDTO(searchFormDTO);
			}*/
			
			List<SearchProcessClaimRequestTableDTO> filterDTO = new ArrayList<SearchProcessClaimRequestTableDTO>();			
			if(claimedAmountFrom != null || claimedAmountTo != null){
				if(claimedAmountFrom != null && claimedAmountTo != null){
					for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
						if(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() != null && 
								(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() >= claimedAmountFrom && searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() <= claimedAmountTo)){
							filterDTO.add(searchProcessClaimRequestTableDTO);
						}
					}
				}else if(claimedAmountFrom != null){
					for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
						if(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() != null && 
								(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() >= claimedAmountFrom)){
							filterDTO.add(searchProcessClaimRequestTableDTO);
						}
					}
				}else if(claimedAmountTo != null){
					for (SearchProcessClaimRequestTableDTO searchProcessClaimRequestTableDTO : tableDTO) {
						if(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() != null && 
								(searchProcessClaimRequestTableDTO.getClaimedAmountAsPerBill() <= claimedAmountTo)){
							filterDTO.add(searchProcessClaimRequestTableDTO);
						}
					}
				}
			}else{
				filterDTO.addAll(tableDTO);
			}
			
			
		
			Page<SearchProcessClaimRequestTableDTO> page = new Page<SearchProcessClaimRequestTableDTO>();
			//page.setPageNumber(taskList.getCurrentPage());
			page.setTotalRecords(totalRecords);
			page.setPageItems(filterDTO);
			if(null != searchFormDTO.getIntimationNo() || ("").equalsIgnoreCase(searchFormDTO.getIntimationNo()))
			{
				page.setSearchId(searchFormDTO.getIntimationNo());
			}
			
			if(!(null != page && null != page.getPageItems() && 0!= page.getPageItems().size()) && intimationNo != null && intimationNo.length() > 0) {
				Intimation intimationByNo = getIntimationByNo(intimationNo);
				if(intimationByNo == null) {
					page.setCorrectMsg("Intimation Not Found");
				} 
			}
			log.info("%%%%%%%%%%%%%%%%%%% END TIME OF GET TASK %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%------> "+ new Date() + ": "+ System.currentTimeMillis());
			return page;
			}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("hhhhhhhhhhhhhhhhhhhhhhhh"+e.getMessage()+"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		}
			
		return null;	
	}

}

