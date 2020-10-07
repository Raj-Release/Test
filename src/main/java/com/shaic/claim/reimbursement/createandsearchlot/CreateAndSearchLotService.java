package com.shaic.claim.reimbursement.createandsearchlot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.fields.dto.HospitalInfo;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.claim.leagalbilling.LegalBillingDTO;
import com.shaic.claim.leagalbilling.LegalTaxDeduction;
import com.shaic.claim.leagalbilling.LegalTaxDeductionMapper;
import com.shaic.claim.reimbursement.financialapproval.pages.billinghospitalization.DuplicatePaymentVerify;
import com.shaic.claim.reimbursement.financialapproval.pages.billinghospitalization.VerificationAccountDeatilsTableDTO;
import com.shaic.claim.rod.searchCriteria.ViewSearchCriteriaTableDTO;
import com.shaic.domain.BankMaster;
import com.shaic.domain.ClaimPayment;
import com.shaic.domain.Hospitals;
import com.shaic.domain.Insured;
import com.shaic.domain.InsuredService;
import com.shaic.domain.Intimation;
import com.shaic.domain.MasHospitals;
import com.shaic.domain.MastersValue;
import com.shaic.domain.NomineeDetails;
import com.shaic.domain.OrganaizationUnit;
import com.shaic.domain.Policy;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.Status;
import com.shaic.domain.TmpCPUCode;
import com.shaic.domain.reimbursement.ReimbursementService;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.vaadin.server.VaadinSession;
import com.vaadin.v7.data.util.BeanItemContainer;

@Stateless
public class CreateAndSearchLotService {

	@PersistenceContext
	protected EntityManager entityManager;
	
	@EJB
	private ReimbursementService reimbursementService;

	
	private String hospitalName = null;


	private final Logger log = LoggerFactory
			.getLogger(CreateAndSearchLotService.class);

	public CreateAndSearchLotService() {
		super();
	}

	public Page<CreateAndSearchLotTableDTO> search(
			CreateAndSearchLotFormDTO searchFormDTO, String userName,
			String passWord) {
		Page<CreateAndSearchLotTableDTO> page = null;

		try {
			String strType = null;
			String lotNo = null;
			String intimationNo = null;
			String rodNo = null;
			String claimNo = null;
			String batchNo = null;
//			Long cpuCode = null;
			String strClaimType = null;
			SelectValue selClaimType = null;
			SelectValue claimantType = null;
			String strClaimant = null;
			String paymentStatus = null;
			SelectValue paymentStatusType = null;
			Long paymentStatusId = 0l;
			Date startDate = null;
			Date toDate = null;
			String strProduct = null;
			SelectValue selProduct = null;	
			StringBuffer productCodeWithName;			
			SelectValue docVerified = null;
			String strdocVerified = null;
			List<Long> listOfCPUs = new ArrayList<Long>();
			
			StringBuilder multiCpu = null;
			String cpuSearch = null;
			SelectValue verificationType = null;
			String strVerificationType= null;
			int pageNumber = searchFormDTO.getPageable().getPageNumber();
			
			SelectValue selTypeValue = searchFormDTO.getType();
			Map<String, Object> mapValues = new WeakHashMap<String, Object>();
			if (null != selTypeValue && null != selTypeValue.getValue()) {
				strType = selTypeValue.getValue();
			}
//			SelectValue selCpuCodeValue = null;
			if (null != searchFormDTO && null != strType
					&& (SHAConstants.CREATE_LOT).equalsIgnoreCase(strType)) {
				
				startDate = searchFormDTO.getFromDate();
				toDate = searchFormDTO.getToDate();
				intimationNo = searchFormDTO.getIntimationNo();
				
				

				/*selCpuCodeValue = searchFormDTO.getCpuCode();
				if (null != selCpuCodeValue
						&& null != selCpuCodeValue.getValue()) {
					// cpuCode = selCpuCodeValue.getValue();
					String cpuCodeVal[] = selCpuCodeValue.getValue().split("-");

					String strcpuCode = cpuCodeVal[0];
					if (null != strcpuCode)
						cpuCode = Long.valueOf(strcpuCode.trim());
				}*/
				
				//

				/*if(searchFormDTO.getCpuCodeMulti() != null){
					String cpuSearch = searchFormDTO.getCpuCodeMulti().toString();
					if(!cpuSearch.equals("[]")){
						String temp[] = cpuSearch.split(",");
						listOfCPUs.clear();
						for (int i = 0; i < temp.length; i++) {
							String valtemp[] = temp[i].split("-");
							String val = valtemp[0].replaceAll("\\[", "");
							listOfCPUs.add(Long.valueOf(val.trim()));
						}
					}
//					System.out.println("Iterator : "+listOfCPUs);
				}*/
				
				
				if(searchFormDTO.getCpuCodeMulti() != null){
					cpuSearch = searchFormDTO.getCpuCodeMulti().toString();					
					if(!cpuSearch.equals("[]")){
						multiCpu = new StringBuilder();
						String temp[] = cpuSearch.split(",");
						listOfCPUs.clear();
						for (int i = 0; i < temp.length; i++) {
							String valtemp[] = temp[i].split("-");
							String val = valtemp[0].replaceAll("\\[", "");
							listOfCPUs.add(Long.valueOf(val.trim()));							
							if(temp.length-1 == i){
								multiCpu = multiCpu.append(val);
							}
							else
							{
								multiCpu = multiCpu.append(val).append(",");
							}
							
						}
						
					}					
				}
								

				selClaimType = searchFormDTO.getClaimType();
				if (null != selClaimType && null != selClaimType.getValue()) {
					strClaimType = selClaimType.getValue();
				}

				claimantType = searchFormDTO.getClaimant();
				if (null != claimantType && null != claimantType.getValue()) {
					strClaimant = claimantType.getValue();
				}
				
				/*verificationType = searchFormDTO.getVerificationType();
				verificationType = searchFormDTO.getVerificationType();
				if (null != verificationType && null != verificationType.getValue()) {
					if(verificationType.getValue().equalsIgnoreCase(ReferenceTable.VERIFICATION_REQUIRED)){
						strVerificationType = "VR";
					}else if(verificationType.getValue().equalsIgnoreCase(ReferenceTable.VERIFICATION_NOT_REQUIRED)){
						strVerificationType = "VNR";
					}
				}*/

				paymentStatusType = searchFormDTO.getPaymentStatus();
				if (null != paymentStatusType
						&& null != paymentStatusType.getValue()) {
					paymentStatus = paymentStatusType.getValue();
					paymentStatusId = paymentStatusType.getId();
					mapValues.put(SHAConstants.PAYMENT_STATUS, paymentStatusId);
				}
				
				selProduct = searchFormDTO.getProduct();
				if (null != selProduct && null != selProduct.getValue()) {
					strProduct = selProduct.getValue();
				}
				docVerified = searchFormDTO.getDocVerified();
				if (null != docVerified && null != docVerified.getValue()) {
					strdocVerified = docVerified.getValue().trim();
				}
			}

			else if (null != searchFormDTO && null != strType
					&& (SHAConstants.SEARCH_LOT).equalsIgnoreCase(strType)) {
				lotNo = searchFormDTO.getLotNo();
				intimationNo = searchFormDTO.getIntimationNo();
				rodNo = searchFormDTO.getRodNo();
				claimNo = searchFormDTO.getClaimNo();
				batchNo = searchFormDTO.getBatchNo();
				
				selProduct = searchFormDTO.getProduct();
				if (null != selProduct && null != selProduct.getValue()) {
					strProduct = selProduct.getValue();
				}
				docVerified = searchFormDTO.getDocVerified();
				if (null != docVerified && null != docVerified.getValue()) {
					strdocVerified = docVerified.getValue();
				}
			}
			


			/*final CriteriaBuilder criteriaBuilder = entityManager
					.getCriteriaBuilder();
			final CriteriaBuilder criteriaBuilderCount = entityManager.getCriteriaBuilder();
			final CriteriaQuery<Long> criteriaQueryCount = criteriaBuilderCount.createQuery(Long.class);
			final CriteriaQuery<ClaimPayment> criteriaQuery = criteriaBuilder
					.createQuery(ClaimPayment.class);

			Root<ClaimPayment> root = criteriaQuery.from(ClaimPayment.class);

			List<Predicate> conditionList = new ArrayList<Predicate>();

			if (null != startDate) {
				
				startDate.setHours(0);
				startDate.setMinutes(0);
				startDate.setSeconds(0);
				
				Predicate condition1 = criteriaBuilder.greaterThanOrEqualTo(
						root.<Date> get("createdDate"), startDate);
				conditionList.add(condition1);
				java.sql.Date createdDate = new java.sql.Date(startDate.getTime());
				SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				String date_to_string = dateformatyyyyMMdd.format(createdDate);

				mapValues.put(SHAConstants.START_DATE, date_to_string);
			}
			if (null != toDate) {
				
				toDate.setHours(0);
				toDate.setMinutes(0);
				toDate.setSeconds(0);
				
				Calendar c = Calendar.getInstance();
				c.setTime(toDate);
				c.add(Calendar.DATE, 1);
				toDate = c.getTime();
				Predicate condition2 = criteriaBuilder.lessThanOrEqualTo(
						root.<Date> get("createdDate"), toDate);
				conditionList.add(condition2);
				java.sql.Date toDateValue = new java.sql.Date(toDate.getTime());
				SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				String date_to_string = dateformatyyyyMMdd.format(toDateValue);
				mapValues.put(SHAConstants.END_DATE, date_to_string);
			}
			if (null != listOfCPUs && listOfCPUs.size() > 0) {
//				Predicate condition3 = criteriaBuilder.equal(
//						root.<Long> get("cpuCode"), cpuCode);
//				conditionList.add(condition3);
//				mapValues.put(SHAConstants.CPU_CODE, cpuCode);
				
				//Predicate condition3 = criteriaBuilder.in(root.<Long> get("cpuCode").in(listOfCPUs)); //equal(root.<Long> get("cpuCode").in(cpuCode));
				Predicate condition3 =  root.<Long> get("cpuCode").in(listOfCPUs);
				conditionList.add(condition3);
				String cpuList = null!= multiCpu ? String.valueOf(multiCpu) : null;
				mapValues.put(SHAConstants.CPU_CODE, cpuList);
				
			}
			
			if (null != strClaimType) {
				Predicate condition4 = criteriaBuilder.like(
						root.<String> get("claimType"),
						"%" + strClaimType.toUpperCase() + "%");
				conditionList.add(condition4);
				mapValues.put(SHAConstants.CLAIM_TYPE, strClaimType);
			}
			
			if (null != paymentStatus) {
				
				Predicate condition5 = criteriaBuilder.like(
						root.<MastersValue> get("paymentStatus").<String> get(
								"value"), "%" + paymentStatus + "%");
				conditionList.add(condition5);
				
				// }
			}			
			
			if (null != lotNo && !("").equalsIgnoreCase(lotNo)) {
				Predicate condition6 = criteriaBuilder.like(
						root.<String> get("lotNumber"), "%" + lotNo + "%");
				conditionList.add(condition6);
			}

			if (null != intimationNo && !("").equalsIgnoreCase(intimationNo)) {
				Predicate condition7 = criteriaBuilder.like(
						root.<String> get("intimationNumber"), "%"
								+ intimationNo + "%");
				conditionList.add(condition7);
			}
			if (null != rodNo && !("").equalsIgnoreCase(rodNo)) {
				Predicate condition8 = criteriaBuilder.like(
						root.<String> get("rodNumber"), "%" + rodNo + "%");
				conditionList.add(condition8);
			}
			if (null != claimNo && !("").equalsIgnoreCase(claimNo)) {
				Predicate condition9 = criteriaBuilder.like(
						root.<String> get("claimNumber"), "%" + claimNo + "%");
				conditionList.add(condition9);
			}
			if(null != batchNo && !("").equalsIgnoreCase(batchNo)){
				Predicate condition9 = criteriaBuilder.like(
						root.<String> get("batchNumber"), "%" + batchNo + "%");
				conditionList.add(condition9);
			}

			if (null != strClaimant && !("").equalsIgnoreCase(strClaimant)) {
				Predicate condition12 = criteriaBuilder.like(
						root.<String> get("documentReceivedFrom"), "%"
								+ strClaimant + "%");
				conditionList.add(condition12);
			}

			if (null != strType
					&& (SHAConstants.CREATE_LOT).equalsIgnoreCase(strType)) {
				conditionList.add(criteriaBuilder.isNull(root
						.<Long> get("lotNumber")));				
			}

			else if (null != strType
					&& (SHAConstants.SEARCH_LOT).equalsIgnoreCase(strType)) {
				conditionList.add(criteriaBuilder.isNotNull(root
						.<Long> get("lotNumber")));
			}
			
			Predicate condition13 = criteriaBuilder.notEqual(root.<String>get("deletedFlag"),SHAConstants.YES_FLAG);
			
			Predicate condition14 = criteriaBuilder.isNull(root.<String>get("deletedFlag"));
			
			
			Predicate condition15 = criteriaBuilder.or(condition13,condition14);
			conditionList.add(condition15);
			
			if (null != strProduct) {
				Predicate condition16 = criteriaBuilder.like(
						root.<String> get("productName"),
						"%" + strProduct + "%");
				conditionList.add(condition16);
			}
			
			criteriaQuery.select(root).where(
					criteriaBuilder.and(conditionList
							.toArray(new Predicate[] {})));
			criteriaQuery.orderBy(criteriaBuilder.asc(root
					.<String> get("rodNumber")));
			
			criteriaQuery.select(root).where(
					criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
			
			final TypedQuery<ClaimPayment> typedQuery = entityManager
					.createQuery(criteriaQuery);
			
			criteriaQueryCount.select(criteriaBuilderCount.count(criteriaQueryCount.from(ClaimPayment.class)));
			
			criteriaQueryCount.where(
					criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
			
			Long totalCount = entityManager.createQuery(criteriaQueryCount).getSingleResult();
			
			if(totalCount > BPMClientContext.getLotBatchFectchSize()){
				List<CreateAndSearchLotTableDTO> cpucodeList = new ArrayList<CreateAndSearchLotTableDTO>();
				Object[] setMapValues = SHAUtils.setMappingValueForLot(mapValues);
				
				DBCalculationService dbCalculationService = new DBCalculationService();
				cpucodeList = dbCalculationService.getCpuWiseCountForLot(setMapValues);	
				
				page = new Page<CreateAndSearchLotTableDTO>();
				page.setPageItems(cpucodeList);
				return page;
			}
	
			int pageNumber = searchFormDTO.getPageable().getPageNumber();
			int firtResult;
			if (pageNumber > 1) {
				firtResult = (pageNumber - 1) * 25;
			} else {
				firtResult = 0;
			}			

			//List<ClaimPayment> claimPaymentList = new ArrayList<ClaimPayment>();
			List<ClaimPayment> totalPaymentList = new ArrayList<ClaimPayment>();


			totalPaymentList = typedQuery.getResultList();
			claimPaymentList = typedQuery.setFirstResult(firtResult)
					.setMaxResults(25).getResultList();

			List result = new ArrayList<ClaimPayment>();
			if (claimPaymentList.size() >= 25) {
				result = typedQuery.setFirstResult(claimPaymentList.size() + 1)
						.setMaxResults(1).getResultList();
			}*/
			
			
			int firtResult;
 			if(pageNumber > 1){ 
				firtResult = (pageNumber-1) *25;
			}else{
				firtResult = 0;
			}
			
			List<ClaimPayment> claimPaymentList =  new ArrayList<ClaimPayment>(); 
			List<ClaimPayment> totalPaymentList =  new ArrayList<ClaimPayment>();
			
			DBCalculationService dbCalculationService = new DBCalculationService();
			totalPaymentList = dbCalculationService.getCreateLotPaymentDetails(strType,startDate,toDate,intimationNo,(null!= multiCpu ? String.valueOf(multiCpu) : null),strClaimType,strClaimant,paymentStatusId,strVerificationType,lotNo,claimNo,rodNo,strProduct,batchNo);
			
			for (ClaimPayment claimPayment : totalPaymentList) {
				Long hospitalKey = claimPayment.getHospitalKey();
				if(null != hospitalKey){
					MasHospitals hospitalById = getMasHospitalById(hospitalKey,entityManager);
						if(null != hospitalById){
							claimPayment.setHospital(hospitalById);
						}
				}
			}
				
			
			
		      int fromIndex = (pageNumber - 1) * 25;
		      int toIndex = Math.min(totalPaymentList.size(), Math.abs(pageNumber * 25));

		    claimPaymentList = totalPaymentList.subList(fromIndex, toIndex);
		
		    Long totalCount = Long.valueOf(totalPaymentList.size());
		    
			if(totalCount > BPMClientContext.getLotBatchFectchSize()){
				List<CreateAndSearchLotTableDTO> cpucodeList = new ArrayList<CreateAndSearchLotTableDTO>();
				Object[] setMapValues = SHAUtils.setMappingValueForLot(mapValues);
				
				DBCalculationService dbCalculationServices = new DBCalculationService();
				cpucodeList = dbCalculationServices.getCpuWiseCountForLot(setMapValues);	
				
				page = new Page<CreateAndSearchLotTableDTO>();
				page.setPageItems(cpucodeList);
				return page;
			}
		
			List<CreateAndSearchLotTableDTO> tableDTO = CreateAndSearchLotMapper.getInstance()
					.getListOfCreateAndSearchLotTableDTO(claimPaymentList);
			
			if (null != tableDTO && !tableDTO.isEmpty()) {
				int i =1;
				for (CreateAndSearchLotTableDTO createAndSearchLotTableDTO : tableDTO) {
					DBCalculationService calculationService = new DBCalculationService();
					if(createAndSearchLotTableDTO.getIntimationNo()!=null){
						//Intimation intimationByNo = getIntimationByNo(createAndSearchLotTableDTO.getIntimationNo());
					//	if(intimationByNo!=null){
							//List<String> gmcEmployeeName = calculationService.getGMCEmployeeName(intimationByNo.getKey());
						//	BeanItemContainer<SelectValue> gmcEmployeeName = getValuesForNameDropDown(intimationByNo,createAndSearchLotTableDTO);
							/*BeanItemContainer<SelectValue> selectValueContainer = new BeanItemContainer<SelectValue>(
									SelectValue.class);*/
							/*if (gmcEmployeeName != null) {
								
								for(int value = 0 ; value<gmcEmployeeName.size() ; value++)
							 	{
									if(createAndSearchLotTableDTO.getPayeeNameStr()!=null && createAndSearchLotTableDTO.getPayeeNameStr().equalsIgnoreCase(gmcEmployeeName.getIdByIndex(value).getValue())){
										createAndSearchLotTableDTO.setPayeeName(gmcEmployeeName.getIdByIndex(value));
										createAndSearchLotTableDTO.setTempPayeeName(createAndSearchLotTableDTO.getPayeeName());
										break;
									}
							 	}
								
								List<SelectValue> selectValueList = new ArrayList<SelectValue>();
								SelectValue selectValue = null;
								for (SelectValue cpuLotNo : gmcEmployeeName) {
									selectValue = new SelectValue();
									long id =selectValueList.size()+1;
									selectValue.setId(id);
									selectValue.setValue(cpuLotNo);
									if(createAndSearchLotTableDTO.getPayeeNameStr()!=null && createAndSearchLotTableDTO.getPayeeNameStr().equals(cpuLotNo)){
										createAndSearchLotTableDTO.setPayeeName(selectValue);
									}
									selectValueList.add(selectValue);
								}
								//selectValueContainer.addAll(selectValueList);
								createAndSearchLotTableDTO.setPayeeNameContainer(gmcEmployeeName);
							}*/
						//}
					}
					Policy policyDetails = getPolicyByPolicyNubember(createAndSearchLotTableDTO.getPolicyNo());
					if (policyDetails !=null && policyDetails.getPaymentParty() != null && !policyDetails.getPaymentParty().isEmpty()){
						createAndSearchLotTableDTO.setPaymentPolicyType(policyDetails.getGmcPolicyType());
					}
					if(createAndSearchLotTableDTO.getCpuCode()!=null){
						TmpCPUCode masCpuCode = null;
						try {
							masCpuCode = getMasCpuCode(Long.parseLong(createAndSearchLotTableDTO.getCpuCode()));
							if(masCpuCode!=null){
								createAndSearchLotTableDTO.setCpuCode(masCpuCode.getCpuCode().toString() + " - " + masCpuCode.getDescription());
							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						}
					}
					
					if(createAndSearchLotTableDTO.getPaymentCpucodeTextValue()!=null){
						TmpCPUCode masCpuCode = null;
						try {
							masCpuCode = getMasCpuCode(Long.parseLong(createAndSearchLotTableDTO.getPaymentCpucodeTextValue()));
							createAndSearchLotTableDTO.setPaymentCpucodeTextValue(masCpuCode.getCpuCode().toString() + " - " + masCpuCode.getDescription());
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						}
					}
					
					if(createAndSearchLotTableDTO.getPayeeNameStr()!=null){
						SelectValue payeeName = new SelectValue();
						payeeName.setId(0l);
						payeeName.setValue(createAndSearchLotTableDTO.getPayeeNameStr());
						createAndSearchLotTableDTO.setTempPayeeName(payeeName);
					}
					
					if(createAndSearchLotTableDTO.getPaymentCpuString()!=null){
						TmpCPUCode masCpuCode = null;
						try {
							masCpuCode = getMasCpuCode(Long.parseLong(createAndSearchLotTableDTO.getPaymentCpuString()));
							if(masCpuCode!=null){
								SelectValue select = new SelectValue();
								select.setId(masCpuCode.getKey());
								select.setValue(masCpuCode.getCpuCode().toString() + " - " + masCpuCode.getDescription());
								createAndSearchLotTableDTO.setPaymentCpuCode(select);
							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						}
					}
					if(createAndSearchLotTableDTO.getDocumentReceivedFrom()!=null && 
							!createAndSearchLotTableDTO.getDocumentReceivedFrom().equals(SHAConstants.DOC_RECEIVED_FROM_HOSPITAL)){
						createAndSearchLotTableDTO.setIsInsured(Boolean.TRUE);
					}
					createAndSearchLotTableDTO.setUsername(userName);
					createAndSearchLotTableDTO.setPassword(passWord);
					createAndSearchLotTableDTO.setSerialNo(i);

					if (null != createAndSearchLotTableDTO
							&& (ReferenceTable.PAYMENT_TYPE_CHEQUE)
									.equalsIgnoreCase(createAndSearchLotTableDTO
											.getPaymentTypeValue())) {
						createAndSearchLotTableDTO
								.setPaymentModeFlag(SHAConstants.YES_FLAG);
						
						createAndSearchLotTableDTO.setChangedPaymentMode(ReferenceTable.PAYMENT_TYPE_CHEQUE);
						
						//createAndSearchLotTableDTO.setPaymentType(ReferenceTable.PAYMENT_TYPE_CHEQUE);
						createAndSearchLotTableDTO.setBeneficiaryAcntNo(null);
						createAndSearchLotTableDTO.setIfscCode(null);
						createAndSearchLotTableDTO.setBankCode(null);
						createAndSearchLotTableDTO.setBankName(null);
						createAndSearchLotTableDTO.setBranchName(null);
						
					} else {
						createAndSearchLotTableDTO
								.setPaymentModeFlag(SHAConstants.N_FLAG);
						createAndSearchLotTableDTO.setChangedPaymentMode(SHAConstants.NEFT_TYPE);
					}					

					BankMaster cityName = getCityName(createAndSearchLotTableDTO 
							.getIfscCode());
					if (null != cityName)

					{
						createAndSearchLotTableDTO.setCity(cityName.getCity());
						createAndSearchLotTableDTO.setPayableAt(cityName
								.getCity());
					}					
					 
					productCodeWithName = new StringBuffer();
					if(null != createAndSearchLotTableDTO.getProductName()){
						
						createAndSearchLotTableDTO.setProduct(String.valueOf(productCodeWithName.append(createAndSearchLotTableDTO.getProduct()).append("-").append(null != createAndSearchLotTableDTO.getProductName()?createAndSearchLotTableDTO.getProductName() : "")));
					}
					else
					{
						createAndSearchLotTableDTO.setProduct(String.valueOf(productCodeWithName.append(createAndSearchLotTableDTO.getProduct()).append("-").append(null != createAndSearchLotTableDTO.getProductName()?createAndSearchLotTableDTO.getProductName() : "")));
					}
					if(createAndSearchLotTableDTO.getRodNo() != null){
						Reimbursement reimbursement= getReasonForChangePayeeName(createAndSearchLotTableDTO.getRodNo());
						if(reimbursement.getClaim() !=null 
								&& reimbursement.getClaim().getLegalClaim() !=null
								&& reimbursement.getClaim().getLegalClaim().equals("Y")){
							createAndSearchLotTableDTO.setLegalPayment(true);
							LegalTaxDeduction taxDeduction = reimbursementService.getTaxDeductionFromRODKey(reimbursement.getKey());
							 if(taxDeduction !=null){
								 LegalTaxDeductionMapper taxDeductionMapper = LegalTaxDeductionMapper.getInstance();
								 LegalBillingDTO billingDTO = taxDeductionMapper.getDTOFromTaxDeduction(taxDeduction);
								 billingDTO = reimbursementService.calculateLegalSettlment(billingDTO);
								 createAndSearchLotTableDTO.setLegalBillingDTO(billingDTO);
							 }
						}else{
							createAndSearchLotTableDTO.setLegalPayment(false);
						}
					}	
					i++;
				}
			}

			/**
			 * The below code is added for downloading all the payment records
			 * in a single select. For this, we need to have a complete list of
			 * records without pagination. Also, this list needs to be converted
			 * to DTO list for excel report generation. Hence adding below code.
			 * 
			 * **/

			
			List<CreateAndSearchLotTableDTO> completeTableDTOList = CreateAndSearchLotMapper
					.getListOfCreateAndSearchLotTableDTO(totalPaymentList);
						
			
			if (null != completeTableDTOList && !completeTableDTOList.isEmpty()) {
				int i =1;
				for (CreateAndSearchLotTableDTO createAndSearchLotTableDTO : completeTableDTOList) {
					
					//DBCalculationService calculationService = new DBCalculationService();
					if(createAndSearchLotTableDTO.getIntimationNo()!=null){
						Intimation intimationByNo = getIntimationByNo(createAndSearchLotTableDTO.getIntimationNo());
						if(intimationByNo!=null){
							//BeanItemContainer<SelectValue> gmcEmployeeName = getValuesForNameDropDown(intimationByNo,createAndSearchLotTableDTO);
							//List<String> gmcEmployeeName = calculationService.getGMCEmployeeName(intimationByNo.getKey());
							/*BeanItemContainer<SelectValue> selectValueContainer = new BeanItemContainer<SelectValue>(
									SelectValue.class);
							if (!gmcEmployeeName.isEmpty()) {
								
								List<SelectValue> selectValueList = new ArrayList<SelectValue>();
								SelectValue selectValue = null;
								for (String cpuLotNo : gmcEmployeeName) {
									selectValue = new SelectValue();
									long id =selectValueList.size()+1;
									selectValue.setId(id);
									selectValue.setValue(cpuLotNo);
									if(createAndSearchLotTableDTO.getPayeeNameStr()!=null && createAndSearchLotTableDTO.getPayeeNameStr().equals(cpuLotNo)){
										createAndSearchLotTableDTO.setPayeeName(selectValue);
									}
									selectValueList.add(selectValue);
								}
								selectValueContainer.addAll(selectValueList);
								createAndSearchLotTableDTO.setPayeeNameContainer(selectValueContainer);
							}*/
							
							/*if (gmcEmployeeName != null) {
								
								for(int value2 = 0 ; value2<gmcEmployeeName.size() ; value2++)
							 	{
									if(createAndSearchLotTableDTO.getPayeeNameStr()!=null && createAndSearchLotTableDTO.getPayeeNameStr().equalsIgnoreCase(gmcEmployeeName.getIdByIndex(value2).getValue())){
										createAndSearchLotTableDTO.setPayeeName(gmcEmployeeName.getIdByIndex(value2));
										break;
									}
							 	}
								createAndSearchLotTableDTO.setPayeeNameContainer(gmcEmployeeName);
							}*/
							
						}
					}
					if(createAndSearchLotTableDTO.getCpuCode()!=null){
						TmpCPUCode masCpuCode = null;
						try {
							masCpuCode = getMasCpuCode(Long.parseLong(createAndSearchLotTableDTO.getCpuCode()));
							createAndSearchLotTableDTO.setCpuCode(masCpuCode.getCpuCode().toString() + " - " + masCpuCode.getDescription());
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(masCpuCode!=null){
							createAndSearchLotTableDTO.setCpuName(masCpuCode.getDescription());
						}
					}
					
					if(createAndSearchLotTableDTO.getPayeeNameStr()!=null){
						SelectValue payeeName = new SelectValue();
						payeeName.setId(0l);
						payeeName.setValue(createAndSearchLotTableDTO.getPayeeNameStr());
						createAndSearchLotTableDTO.setTempPayeeName(payeeName);
					}
					
					if(createAndSearchLotTableDTO.getPaymentCpucodeTextValue()!=null){
						TmpCPUCode masCpuCode = null;
						try {
							masCpuCode = getMasCpuCode(Long.parseLong(createAndSearchLotTableDTO.getPaymentCpucodeTextValue()));
							createAndSearchLotTableDTO.setPaymentCpucodeTextValue(masCpuCode.getCpuCode().toString() + " - " + masCpuCode.getDescription());
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						}
					}
					
					
					if(createAndSearchLotTableDTO.getPaymentCpuString()!=null){
						TmpCPUCode masCpuCode = null;
						try {
							masCpuCode = getMasCpuCode(Long.parseLong(createAndSearchLotTableDTO.getPaymentCpuString()));
							if(masCpuCode!=null){
								SelectValue select = new SelectValue();
								select.setId(masCpuCode.getCpuCode());
								select.setValue(masCpuCode.getCpuCode().toString() + " - " + masCpuCode.getDescription());
								createAndSearchLotTableDTO.setPaymentCpuCode(select);
							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						}
					}
					
					createAndSearchLotTableDTO.setUsername(userName);
					createAndSearchLotTableDTO.setPassword(passWord);
					createAndSearchLotTableDTO.setSerialNo(i);

					if (null != createAndSearchLotTableDTO
							&& (ReferenceTable.PAYMENT_TYPE_CHEQUE)
							.equalsIgnoreCase(createAndSearchLotTableDTO
									.getPaymentTypeValue())) {
						createAndSearchLotTableDTO
								.setPaymentModeFlag(SHAConstants.YES_FLAG);
						createAndSearchLotTableDTO.setBeneficiaryAcntNo(null);
						createAndSearchLotTableDTO.setIfscCode(null);
						createAndSearchLotTableDTO.setBankCode(null);
						createAndSearchLotTableDTO.setBankName(null);
						createAndSearchLotTableDTO.setBranchName(null);
					} else {
						createAndSearchLotTableDTO
								.setPaymentModeFlag(SHAConstants.N_FLAG);
					}
					

					BankMaster cityName = getCityName(createAndSearchLotTableDTO
							.getIfscCode());
					if (null != cityName)

					{
						createAndSearchLotTableDTO.setCity(cityName.getCity());
						createAndSearchLotTableDTO.setPayableAt(cityName
								.getCity());
					}					

					productCodeWithName = new StringBuffer();
					if(null != createAndSearchLotTableDTO.getProductName()){
						
						createAndSearchLotTableDTO.setProduct(String.valueOf(productCodeWithName.append(createAndSearchLotTableDTO.getProduct()).append("-").append(null != createAndSearchLotTableDTO.getProductName()?createAndSearchLotTableDTO.getProductName() : "")));
					}
					else
					{
						createAndSearchLotTableDTO.setProduct(String.valueOf(productCodeWithName.append(createAndSearchLotTableDTO.getProduct()).append("").append(null != createAndSearchLotTableDTO.getProductName()?createAndSearchLotTableDTO.getProductName() : "")));
					}
					if(createAndSearchLotTableDTO.getRodNo() != null){
						Reimbursement reimbursement= getReasonForChangePayeeName(createAndSearchLotTableDTO.getRodNo());
						if(reimbursement.getClaim() !=null 
								&& reimbursement.getClaim().getLegalClaim() !=null
								&& reimbursement.getClaim().getLegalClaim().equals("Y")){
							createAndSearchLotTableDTO.setLegalPayment(true);
							LegalTaxDeduction taxDeduction = reimbursementService.getTaxDeductionFromRODKey(reimbursement.getKey());
							 if(taxDeduction !=null){
								 LegalTaxDeductionMapper taxDeductionMapper = LegalTaxDeductionMapper.getInstance();
								 LegalBillingDTO billingDTO = taxDeductionMapper.getDTOFromTaxDeduction(taxDeduction);
								 billingDTO = reimbursementService.calculateLegalSettlment(billingDTO);
								 createAndSearchLotTableDTO.setLegalBillingDTO(billingDTO);
							 }
						}else{
							createAndSearchLotTableDTO.setLegalPayment(false);
						}
					}	
					i++;
				}
			}

		page = new Page<CreateAndSearchLotTableDTO>();			
			searchFormDTO.getPageable().setPageNumber(pageNumber);
			page.setHasNext(true);
			if(null != claimPaymentList && claimPaymentList.isEmpty()) {
				page.setHasNext(false);
				searchFormDTO.getPageable().setPageNumber(1);
			}
			else if(null !=totalPaymentList && toIndex >= totalPaymentList.size()){
				page.setHasNext(false);
				page.setPageNumber(pageNumber);
			}
			else{
				page.setPageNumber(pageNumber);
				page.setHasNext(true);
			}
			page.setPageNumber(pageNumber);
			page.setPageItems(tableDTO);		
			if (null != totalPaymentList) {
				page.setTotalRecords(totalPaymentList.size());
			}
			if (null != completeTableDTOList) {
				page.setTotalList(completeTableDTOList);
			}		
			page.setIsDbSearch(true);
			return page;

		} catch (Exception e) {
		
			e.printStackTrace();
		}
			return page;
	}

	

	public Map<String, Object> updateLotNumberForPaymentProcessing(
			List<CreateAndSearchLotTableDTO> tableDTOList) {
		Map<String, Object> lotCreationMap = new HashMap<String, Object>();
		String strLotNo = null;
		int itotalNoOfRecords = 0;
		int iNoOfRecordsSentToChecker = 0;
		List<CreateAndSearchLotTableDTO> validationList = new ArrayList<CreateAndSearchLotTableDTO>();
		List<String> intimationNoList = new ArrayList<String>();
		Boolean isError = false;
		// Boolean isError = true;
		if (null != tableDTOList && !tableDTOList.isEmpty()) {
			itotalNoOfRecords = tableDTOList.size();
			DBCalculationService dbCalculationService = new DBCalculationService();
			Long lotNo = dbCalculationService
					.generateSequence(SHAConstants.LOT_NO_SEQUENCE_NAME);
			strLotNo = SHAConstants.LOT_NO_FORMAT + lotNo;
		
			if (!isError) {
				for (CreateAndSearchLotTableDTO createAndSearchLotTableDTO : tableDTOList) {
					if (null != createAndSearchLotTableDTO.getCheckBoxStatus()
							&& ("true")
									.equalsIgnoreCase(createAndSearchLotTableDTO
											.getCheckBoxStatus())) {
						ClaimPayment claimPayment = populateClaimPaymentObject(createAndSearchLotTableDTO);
						claimPayment.setLotNumber(strLotNo);
						
						if (null != claimPayment.getKey()) {
							entityManager.merge(claimPayment);
							entityManager.flush();
							entityManager.clear();
							log.info("------ClaimPayment------>" + claimPayment
									+ "<------------");
						}
						iNoOfRecordsSentToChecker++;
					}
				}
			}
		}
		lotCreationMap.put(SHAConstants.LOT_NUMBER, strLotNo);
		lotCreationMap.put(SHAConstants.TOTAL_NO_LOT_RECORDS,
				String.valueOf(itotalNoOfRecords));
		lotCreationMap.put(SHAConstants.NO_RECORDS_SENT_TO_CHECKER,
				String.valueOf(iNoOfRecordsSentToChecker)); 
		if (isError)
			lotCreationMap.put(SHAConstants.LOT_CREATION_ERROR, "true");
		else
			lotCreationMap.put(SHAConstants.LOT_CREATION_ERROR, "false");

		return lotCreationMap;
	}

	private Reimbursement getReimbursementObject(String rodNo) {
		Query query = entityManager
				.createNamedQuery("Reimbursement.findRodByNumber");
		query = query.setParameter("rodNumber", rodNo);
		List<Reimbursement> reimbursementObjectList = query.getResultList();
		if (null != reimbursementObjectList
				&& !reimbursementObjectList.isEmpty()) {
			entityManager.refresh(reimbursementObjectList.get(0));
			return reimbursementObjectList.get(0);
		}
		return null;
	}

	private ClaimPayment populateClaimPaymentObject(
			CreateAndSearchLotTableDTO createAndSearchLotTableDTO) {
		ClaimPayment claimPayment = getClaimPayment(createAndSearchLotTableDTO
				.getClaimPaymentKey());
		if(null != claimPayment)
		{
			if(ReferenceTable.PAYMENT_TYPE_CHEQUE.equalsIgnoreCase(claimPayment.getPaymentType()))
			{
				createAndSearchLotTableDTO.setPaymentModeFlag(SHAConstants.YES_FLAG);
			}
			else
			{
				createAndSearchLotTableDTO.setPaymentModeFlag(SHAConstants.N_FLAG);
			}
		}
		if (null != createAndSearchLotTableDTO.getPaymentModeFlag()
				&& (SHAConstants.YES_FLAG)
						.equalsIgnoreCase(createAndSearchLotTableDTO
								.getPaymentModeFlag()))		
		{
			claimPayment.setPaymentType(ReferenceTable.PAYMENT_TYPE_CHEQUE);
			claimPayment.setAccountNumber(null);
			claimPayment.setIfscCode(null);
			claimPayment.setBankCode(null);
			claimPayment.setBankName(null);
			claimPayment.setBranchName(null);
			if (null != createAndSearchLotTableDTO.getPayableAt())
				claimPayment.setPayableAt(createAndSearchLotTableDTO
						.getPayableAt());
		} else {
			claimPayment.setPaymentType(ReferenceTable.BANK_TRANSFER);
			claimPayment.setPayableAt(null);
			if (null != createAndSearchLotTableDTO.getBeneficiaryAcntNo())
				claimPayment.setAccountNumber(createAndSearchLotTableDTO
						.getBeneficiaryAcntNo());
			if (null != createAndSearchLotTableDTO.getIfscCode())
				claimPayment.setIfscCode(createAndSearchLotTableDTO
						.getIfscCode());
			if (null != createAndSearchLotTableDTO.getBranchName())
				claimPayment.setBranchName(createAndSearchLotTableDTO
						.getBranchName());
			if (null != createAndSearchLotTableDTO.getBankName())
				claimPayment.setBankName(createAndSearchLotTableDTO
						.getBankName());
		}

		if (null != createAndSearchLotTableDTO.getCmbPayeeName()) {
			claimPayment.setPayeeName(createAndSearchLotTableDTO
					.getCmbPayeeName().getValue());
		}
		if (null != createAndSearchLotTableDTO.getEmailID())
			claimPayment.setEmailId(createAndSearchLotTableDTO.getEmailID());

		if (null != createAndSearchLotTableDTO.getPayModeChangeReason())
			claimPayment.setPayModeChangeReason(createAndSearchLotTableDTO
					.getPayModeChangeReason());
		
		if (null != createAndSearchLotTableDTO.getReasonForChange())
			claimPayment.setReasonForChange(createAndSearchLotTableDTO
					.getReasonForChange());
	
		if (null != createAndSearchLotTableDTO.getPanNo())
			claimPayment.setPanNumber(createAndSearchLotTableDTO.getPanNo());

		if (null != createAndSearchLotTableDTO.getLegalFirstName())
			claimPayment.setLegalHeirName(createAndSearchLotTableDTO
					.getLegalFirstName());
		
		if (null != createAndSearchLotTableDTO.getGmcEmployeeName())
			claimPayment.setGmcEmployeeName(createAndSearchLotTableDTO
					.getGmcEmployeeName());
		

		Status status = new Status();
		status.setKey(ReferenceTable.LOT_CREATED_STATUS);
		claimPayment.setStatusId(status);

		Status lotStatus = new Status();
		lotStatus.setKey(ReferenceTable.LOT_CREATED_STATUS);

		claimPayment.setLotStatus(lotStatus);

		claimPayment.setModifiedBy(createAndSearchLotTableDTO.getUsername());
		claimPayment.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		/**
		 * City to be implemented.
		 */
		return claimPayment;

	}

	public Boolean savePaymentDetails(
			CreateAndSearchLotTableDTO createAndSearchLotTableDTO, Boolean true1) {
		try {
			ClaimPayment claimPayment = getClaimPayment(createAndSearchLotTableDTO
					.getClaimPaymentKey());
			if(createAndSearchLotTableDTO.getPaymentCpuCode()!=null){
				SelectValue selCpuCodeValue = createAndSearchLotTableDTO.getPaymentCpuCode();
				if (null != selCpuCodeValue
						&& null != selCpuCodeValue.getValue()) {
					// cpuCode = selCpuCodeValue.getValue();
					String cpuCodeVal[] = selCpuCodeValue.getValue().split("-");

					String strcpuCode = cpuCodeVal[0];
					if (null != strcpuCode)
					claimPayment.setPaymentCpuCode(Long.parseLong(strcpuCode.trim()));
				}
			}
			
			if(createAndSearchLotTableDTO.getPaymentCpucodeTextValue()!=null){
					String cpuCodeVal[] = createAndSearchLotTableDTO.getPaymentCpucodeTextValue().split("-");
					String strcpuCode = cpuCodeVal[0];
					if (null != strcpuCode)
					claimPayment.setPaymentCpuCode(Long.parseLong(strcpuCode.trim()));
				}
			
			if (null != createAndSearchLotTableDTO.getReasonForChange())
				claimPayment.setReasonForChange(createAndSearchLotTableDTO
						.getReasonForChange());
			
			/*if (null != createAndSearchLotTableDTO.getPaymentModeFlag()
					&& (SHAConstants.YES_FLAG)
							.equalsIgnoreCase(createAndSearchLotTableDTO
									.getPaymentModeFlag()))
			// if(null != createAndSearch)
			{
				claimPayment.setPaymentType(ReferenceTable.PAYMENT_TYPE_CHEQUE);
				claimPayment.setAccountNumber(null);
				claimPayment.setIfscCode(null);
				claimPayment.setBankCode(null);
				claimPayment.setBankName(null);
				claimPayment.setBranchName(null);
				if (null != createAndSearchLotTableDTO.getPayableAt())
					claimPayment.setPayableAt(createAndSearchLotTableDTO
							.getPayableAt());
			} else {
				claimPayment.setPaymentType(ReferenceTable.BANK_TRANSFER);
				claimPayment.setPayableAt(null);
				if (null != createAndSearchLotTableDTO.getBeneficiaryAcntNo())
					claimPayment.setAccountNumber(createAndSearchLotTableDTO
							.getBeneficiaryAcntNo());
				if (null != createAndSearchLotTableDTO.getIfscCode())
					claimPayment.setIfscCode(createAndSearchLotTableDTO
							.getIfscCode());
				if (null != createAndSearchLotTableDTO.getBranchName())
					claimPayment.setBranchName(createAndSearchLotTableDTO
							.getBranchName());
				if (null != createAndSearchLotTableDTO.getBankName())
					claimPayment.setBankName(createAndSearchLotTableDTO
							.getBankName());
			}*/
			if(createAndSearchLotTableDTO.getPaymentType()!=null && createAndSearchLotTableDTO.getPaymentType().getId().equals(ReferenceTable.PAYMENT_MODE_BANK_TRANSFER)){
				claimPayment.setPaymentType(ReferenceTable.BANK_TRANSFER);
			}else if(createAndSearchLotTableDTO.getPaymentType()!=null && createAndSearchLotTableDTO.getPaymentType().getId().equals(ReferenceTable.PAYMENT_MODE_CHEQUE_DD)){
				claimPayment.setPaymentType(ReferenceTable.PAYMENT_TYPE_CHEQUE);
			}
			if (null != createAndSearchLotTableDTO.getPayableAt())
				claimPayment.setPayableAt(createAndSearchLotTableDTO
						.getPayableAt());
			if (null != createAndSearchLotTableDTO.getBeneficiaryAcntNo())
				claimPayment.setAccountNumber(createAndSearchLotTableDTO
						.getBeneficiaryAcntNo());
			if (null != createAndSearchLotTableDTO.getIfscCode())
				claimPayment.setIfscCode(createAndSearchLotTableDTO
						.getIfscCode());
			if (null != createAndSearchLotTableDTO.getBranchName())
				claimPayment.setBranchName(createAndSearchLotTableDTO
						.getBranchName());
			if (null != createAndSearchLotTableDTO.getBankName())
				claimPayment.setBankName(createAndSearchLotTableDTO
						.getBankName());

			if (null != createAndSearchLotTableDTO.getPayeeName()) {
				claimPayment.setPayeeName(createAndSearchLotTableDTO
						.getPayeeName().getValue());
			}else{
				claimPayment.setPayeeName(null);
			}
			
			if (null != createAndSearchLotTableDTO.getPayeeNameStr()) {
				claimPayment.setPayeeName(createAndSearchLotTableDTO
						.getPayeeNameStr());
			}else{
				claimPayment.setPayeeName(null);
			}
			
			if (null != createAndSearchLotTableDTO.getEmailID())
				claimPayment
						.setZonalMailId(createAndSearchLotTableDTO.getEmailID());

			if (null != createAndSearchLotTableDTO.getReasonForChange())
				claimPayment.setReasonForChange(createAndSearchLotTableDTO
						.getReasonForChange());
			
			if (null != createAndSearchLotTableDTO.getPayModeChangeReason())
				claimPayment.setPayModeChangeReason(createAndSearchLotTableDTO
						.getPayModeChangeReason());
			
			if (null != createAndSearchLotTableDTO.getGmcEmployeeName())
				claimPayment.setGmcEmployeeName(createAndSearchLotTableDTO
						.getGmcEmployeeName());
			
			if (null != createAndSearchLotTableDTO.getPanNo())
				claimPayment
						.setPanNumber(createAndSearchLotTableDTO.getPanNo());

			if (null != createAndSearchLotTableDTO.getLegalFirstName()){
				claimPayment.setLegalHeirName(createAndSearchLotTableDTO
						.getLegalFirstName());
			}
			else
			{
				claimPayment.setLegalHeirName(null);
			}
			
			if(null != createAndSearchLotTableDTO.getNumberofdays())
			{
			claimPayment.setDelayDays(Long.valueOf(createAndSearchLotTableDTO.getNumberofdays()));	
			}
			else
			{
				claimPayment.setDelayDays(0l);
			}
			if(null != createAndSearchLotTableDTO.getNoofdaysexceeding())
			{
				claimPayment.setAllowedDelayDays(Long.valueOf(createAndSearchLotTableDTO.getNoofdaysexceeding()));
			}
			else
			{
				claimPayment.setAllowedDelayDays(0l);
			}

			if(null != createAndSearchLotTableDTO.getIntrestRate())
			{
			claimPayment.setInterestRate(createAndSearchLotTableDTO.getIntrestRate());	
			}
			else
			{
				claimPayment.setInterestRate(0d);
			}

			if(null != createAndSearchLotTableDTO.getIntrestAmount())
			{
								
				claimPayment.setInterestAmount(createAndSearchLotTableDTO.getIntrestAmount());
			}
			else
			{
				claimPayment.setInterestAmount(0d);
			}

			if(null !=createAndSearchLotTableDTO.getPenalTotalAmnt())
			{
				
				claimPayment.setTotalAmount(createAndSearchLotTableDTO.getPenalTotalAmnt());
				
			}
			
			if(null != createAndSearchLotTableDTO.getRemarks())
			{
				claimPayment.setIntrestRemarks(createAndSearchLotTableDTO.getRemarks());	
			}
			
			String vnrFlag = null;
			if(SHAConstants.DOC_RECEIVED_FROM_INSURED.equalsIgnoreCase(createAndSearchLotTableDTO.getDocReceivedFrom()) && 
					(createAndSearchLotTableDTO.getPaymentType()!=null && createAndSearchLotTableDTO.getPaymentType().getId().equals(ReferenceTable.PAYMENT_MODE_BANK_TRANSFER))){
				
				vnrFlag = callPaymentFunDeDupProcedure(createAndSearchLotTableDTO.getIntimationNo(),createAndSearchLotTableDTO.getBeneficiaryAcntNo(),createAndSearchLotTableDTO.getIfscCode(),
						  createAndSearchLotTableDTO.getPayeeNameStr() != null ? createAndSearchLotTableDTO.getPayeeNameStr().toUpperCase():null,
						  createAndSearchLotTableDTO.getLegalFirstName() != null ?createAndSearchLotTableDTO.getLegalFirstName().toUpperCase():null);
				
				if(null != vnrFlag){
					claimPayment.setVnrFlag(vnrFlag);
				}
				
			}else{
				claimPayment.setVnrFlag(SHAConstants.VERIFICATION_NOT_REQUIRED);
			}
			
			/*Reimbursement rodkey = claimPayment.getRodkey();
			if(rodkey!=null && true1){
				rodkey.setUpdatePaymentDtlsFlag("Y");
				entityManager.merge(rodkey);
			}*/
			if (null != claimPayment.getKey()) {
				entityManager.merge(claimPayment);
				entityManager.flush();
				entityManager.clear();

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private ClaimPayment getClaimPayment(Long key) {
		Query query = entityManager.createNamedQuery("ClaimPayment.findByKey");
		query = query.setParameter("primaryKey", key);
		List<ClaimPayment> claimPaymentList = query.getResultList();
		if (null != claimPaymentList && !claimPaymentList.isEmpty()) {
			for (ClaimPayment claimPayment : claimPaymentList) {
				//entityManager.refresh(claimPayment);
			}

			return claimPaymentList.get(0);
		} else {
			return null;
		}

	}

	private Hospitals getHospitalName(String hospCode) {
		Query query = entityManager.createNamedQuery("Hospitals.findByCode");
		query = query.setParameter("hospitalCode", hospCode.toUpperCase());
		List<Hospitals> hospitalList = query.getResultList();
		if (null != hospitalList && !hospitalList.isEmpty()) {
			return hospitalList.get(0);
		} else {
			return null;
		}

	}

	private Reimbursement getReasonForChangePayeeName(String rodNO) {
		Query query = entityManager
				.createNamedQuery("Reimbursement.findRodByNumber");
		query = query.setParameter("rodNumber", rodNO);
		List<Reimbursement> reimbursementList = query.getResultList();
		if (null != reimbursementList && !reimbursementList.isEmpty()) {
			return reimbursementList.get(0);
		} else {
			return null;
		}

	}

	private OrganaizationUnit getZonalEmailDetails(String pioCode)
	{
		Query query = entityManager.createNamedQuery("OrganaizationUnit.findByUnitId");
		query = query.setParameter("officeCode", pioCode);
		List<OrganaizationUnit> branchList = query.getResultList();
		if(null != branchList && !branchList.isEmpty())
		{
			return branchList.get(0);
		}
		else
		{
			return null;
		}
		
	}
	private BankMaster getCityName(String ifscCode) {
		Query query = entityManager
				.createNamedQuery("BankMaster.findByIfscCode");
		query = query.setParameter("ifscCode", ifscCode);
		List<BankMaster> bankList = query.getResultList();
		if (null != bankList && !bankList.isEmpty()) {
			return bankList.get(0);
		} else {
			return null;
		}

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
	
	public Intimation getIntimationKey(String intimationNo)
	{
		Query query = entityManager.createNamedQuery("Intimation.findByIntimationNumber");
		query = query.setParameter("intimationNo", intimationNo);
		List<Intimation> intimationList = query.getResultList();
		if(null != intimationList && !intimationList.isEmpty())
		{
			
				entityManager.refresh(intimationList.get(0));
				return intimationList.get(0);
	}
		
		return null;
	}
	
	public Map<String, String> getHospitalList(List<String> hospitalCodeList){
		
		Map<String, String> hospitalMap = new WeakHashMap<String, String>();
		
		Session session = (Session) entityManager.getDelegate();
		@SuppressWarnings("unchecked")
		List<HospitalInfo> hospitalList = session.createCriteria(Hospitals.class)
										.add(Restrictions.in("hospitalCode", hospitalCodeList))
										.addOrder(Order.asc("key"))
										.setProjection(Projections.projectionList()
														.add(Projections.property("hospitalCode"), "hospitalCode")
														.add(Projections.property("payableAt"), "hospitalName")
														.add(Projections.property("emailId"),"emailId"))														
										.setResultTransformer(org.hibernate.transform.Transformers.aliasToBean(HospitalInfo.class)).list();	
		
		for (HospitalInfo hospitalValue : hospitalList) {
			hospitalMap.put(hospitalValue.getHospitalCode(), hospitalValue.getHospitalName());
		}
		return hospitalMap;
	}
	
	public TmpCPUCode getMasCpuCode(Long cpuCode){
		Query  query = entityManager.createNamedQuery("TmpCPUCode.findByCode");
		query = query.setParameter("cpuCode", cpuCode);
		List<TmpCPUCode> listOfTmpCodes = query.getResultList();
		if(null != listOfTmpCodes && !listOfTmpCodes.isEmpty()){
			return listOfTmpCodes.get(0);
		}
		return null;
	}
	
	public List<Insured> getInsuredList(String policyNumber,EntityManager entityManager){
		InsuredService insuredService = new InsuredService();
		List<Insured> insuredList = insuredService.getInsuredListByPolicyNo(policyNumber, entityManager);
		return insuredList;
	}
	
	public Hospitals getHospitalById(Long key){
		
		Query query = entityManager.createNamedQuery("Hospitals.findByKey");
		query.setParameter("key", key);
		
		List<Hospitals> resultList = (List<Hospitals>) query.getResultList();
		
		if(resultList != null && ! resultList.isEmpty()){
			return resultList.get(0);
		}
		
		return null;
		
	}
	
	public List<NomineeDetails> getNomineeDetails(Long insuredKey){
		
		Query query = entityManager.createNamedQuery("NomineeDetails.findByInsuredKey");
		query = query.setParameter("insuredKey", insuredKey);
		
		List<NomineeDetails> nomineeDetails = (List<NomineeDetails>) query.getResultList();
		return nomineeDetails;
		
	}
	
	private BeanItemContainer<SelectValue> getValuesForNameDropDown(Intimation intimation, CreateAndSearchLotTableDTO createAndSearchLotTableDTO)
	{
		
		Policy policy = getPolicyByPolicyNubember(intimation.getPolicy().getPolicyNumber());
		if(null != policy)
		{
		String proposerName =  policy.getProposerFirstName();
		List<Insured> insuredList = policy.getInsured();
		
		List<SelectValue> selectValueList = new ArrayList<SelectValue>();
		List<SelectValue> payeeValueList = new ArrayList<SelectValue>();
		
		SelectValue selectValue= null;
		for (int i = 0; i < insuredList.size(); i++) {
			
			Insured insured = insuredList.get(i);
			selectValue = new SelectValue();
			//SelectValue payeeValue = new SelectValue();
			selectValue.setId(Long.valueOf(String.valueOf(i)));
			selectValue.setValue(insured.getInsuredName());
			
			//payeeValue.setId(Long.valueOf(String.valueOf(i)));
			//payeeValue.setValue(insured.getInsuredName());
			
			selectValueList.add(selectValue);
			//payeeValueList.add(payeeValue);
		}
		payeeValueList.addAll(selectValueList);
		
		for (int i = 0; i < insuredList.size(); i++) {
			Insured insured = insuredList.get(i);
			List<NomineeDetails> nomineeDetails = getNomineeDetails(insured.getKey());
			for (NomineeDetails nomineeDetails2 : nomineeDetails) {
				selectValue = new SelectValue();
				selectValue.setId(nomineeDetails2.getKey());
				selectValue.setValue(nomineeDetails2.getNomineeName());
				payeeValueList.add(selectValue);
				selectValue = null;
			}
			
		}
		
		BeanItemContainer<SelectValue> selectValueContainer = new BeanItemContainer<SelectValue>(
				SelectValue.class);
		selectValueContainer.addAll(selectValueList);
		
		
		SelectValue payeeSelValue = new SelectValue();
		int iSize = payeeValueList.size() +1;
		payeeSelValue.setId(Long.valueOf(String.valueOf(iSize)));
		payeeSelValue.setValue(proposerName);
		
		payeeValueList.add(payeeSelValue);
		
		if(createAndSearchLotTableDTO.getDocumentReceivedFrom()!=null && createAndSearchLotTableDTO.getDocumentReceivedFrom().equalsIgnoreCase(SHAConstants.DOC_RECEIVED_FROM_HOSPITAL)){
			Hospitals hosptial = getHospitalById(intimation.getHospital());
			
			if(hosptial != null && hosptial.getPayableAt() != null){
				SelectValue hospitalName = new SelectValue();
				hospitalName.setId(Long.valueOf(payeeValueList.size()+1));
				hospitalName.setValue(hosptial.getPayableAt());
				payeeValueList.add(hospitalName);
				createAndSearchLotTableDTO.setHospitalPayableAt(hosptial.getPayableAt());
			}
			else if(hosptial != null && hosptial.getName() != null){
				SelectValue hospitalName = new SelectValue();
				hospitalName.setId(Long.valueOf(payeeValueList.size()+1));
				hospitalName.setValue(hosptial.getName());
				payeeValueList.add(hospitalName);
				createAndSearchLotTableDTO.setHospitalPayableName(hosptial.getName());
			}
		}
		
		
		BeanItemContainer<SelectValue> insuredPatientNameValueContainer = new BeanItemContainer<SelectValue>(SelectValue.class);
		
		insuredPatientNameValueContainer.addAll(payeeValueList);
		DBCalculationService dbCalculationService = new DBCalculationService();
		BeanItemContainer<SelectValue> payeeNameValueContainer = dbCalculationService.getPayeeName(intimation.getPolicy().getKey(),
				intimation.getKey());
		
		if(ReferenceTable.getGMCProductList().containsKey(intimation.getPolicy().getProduct().getKey())){	
			
			return payeeNameValueContainer;
		}
		else
		{
			return insuredPatientNameValueContainer;
		}
		
		}else{
			return null;
		}
	}

	public void saveEmailForPaymentCpu(String cpucode,
			CreateAndSearchLotTableDTO createAndSearchLotTableDTO) {
		OrganaizationUnit branchCode = getBranchCode(cpucode);
		Intimation intimationByNo = getIntimationByNo(createAndSearchLotTableDTO.getIntimationNo());
		if(intimationByNo!=null){
		if(branchCode != null){
			if(createAndSearchLotTableDTO.getDocumentReceivedFrom()!=null && createAndSearchLotTableDTO.getDocumentReceivedFrom().equals(SHAConstants.DOC_RECEIVED_FROM_HOSPITAL)){
				Hospitals hospitalById = getHospitalById(intimationByNo.getHospital());
				
				String emailId2 = branchCode.getEmailId2();
				
				if(emailId2 != null && emailId2.contains(SHAConstants.REIMBURSEMENT_PAYMENT_MAIL_ID)){
					emailId2 = emailId2.replace(SHAConstants.REIMBURSEMENT_PAYMENT_MAIL_ID, "");
				}
				
				if(emailId2 != null && hospitalById != null && hospitalById.getEmailId() != null){
					emailId2 = hospitalById.getEmailId()+","+emailId2;
				}else if(hospitalById != null && hospitalById.getEmailId() != null){
					emailId2 = hospitalById.getEmailId();
				}
				
				
				
				createAndSearchLotTableDTO.setZonalMailId(emailId2);
			}if(createAndSearchLotTableDTO.getDocumentReceivedFrom()!=null && createAndSearchLotTableDTO.getDocumentReceivedFrom().equals(SHAConstants.DOC_RECEIVED_FROM_INSURED)){
				
				/*if(intimationByNo.getPolicy().getHomeOfficeCode() != null){
					String zonalEmailId = "";
					//OrganaizationUnit zonalMailBasedOnBranchType = getZonalMailBasedOnBranchType(reimbursement.getClaim().getIntimation().getPolicy().getHomeOfficeCode(), ReferenceTable.BRANCH_TYPE_ID);
					OrganaizationUnit zonalMailBasedOnBranchType = getBranchCode(intimationByNo.getPolicy().getHomeOfficeCode());
					if(zonalMailBasedOnBranchType != null){
						zonalEmailId = zonalMailBasedOnBranchType.getEmailId2();
						createAndSearchLotTableDTO.setZonalMailId(zonalEmailId);
						
					}
				}*/
				
				String zonalEmailId = branchCode.getEmailId2();
				if(zonalEmailId != null && ! zonalEmailId.contains(SHAConstants.REIMBURSEMENT_PAYMENT_MAIL_ID)){
					zonalEmailId = (branchCode.getEmailId2() != null ? branchCode.getEmailId2() + ",":"")+ SHAConstants.REIMBURSEMENT_PAYMENT_MAIL_ID;
				}
				createAndSearchLotTableDTO.setZonalMailId(zonalEmailId);
			}
		 }
		}
		
	}
	
	private OrganaizationUnit getBranchCode(String branchCode)
    {
    	Query query = entityManager.createNamedQuery("OrganaizationUnit.findByBranchode");
    	query = query.setParameter("parentKey", branchCode);
    	List<OrganaizationUnit> orgList = query.getResultList();
    	if(null != orgList && !orgList.isEmpty())
    	{
    		return orgList.get(0);
    	}
    	return null;
    }
	
	public Boolean savePaymentDetailsPenal(
			CreateAndSearchLotTableDTO createAndSearchLotTableDTO, Boolean true1) {
		try {
			ClaimPayment claimPayment = getClaimPayment(createAndSearchLotTableDTO
					.getClaimPaymentKey());
			if(createAndSearchLotTableDTO.getPaymentCpuCode()!=null){
				SelectValue selCpuCodeValue = createAndSearchLotTableDTO.getPaymentCpuCode();
				if (null != selCpuCodeValue
						&& null != selCpuCodeValue.getValue()) {
					// cpuCode = selCpuCodeValue.getValue();
					String cpuCodeVal[] = selCpuCodeValue.getValue().split("-");

					String strcpuCode = cpuCodeVal[0];
					if (null != strcpuCode)
					claimPayment.setPaymentCpuCode(Long.parseLong(strcpuCode.trim()));
				}
			}
			
			if(createAndSearchLotTableDTO.getPaymentCpucodeTextValue()!=null){
					String cpuCodeVal[] = createAndSearchLotTableDTO.getPaymentCpucodeTextValue().split("-");
					String strcpuCode = cpuCodeVal[0];
					if (null != strcpuCode)
					claimPayment.setPaymentCpuCode(Long.parseLong(strcpuCode.trim()));
				}
			
			if (null != createAndSearchLotTableDTO.getReasonForChange())
				claimPayment.setReasonForChange(createAndSearchLotTableDTO
						.getReasonForChange());
			
			/*if (null != createAndSearchLotTableDTO.getPaymentModeFlag()
					&& (SHAConstants.YES_FLAG)
							.equalsIgnoreCase(createAndSearchLotTableDTO
									.getPaymentModeFlag()))
			// if(null != createAndSearch)
			{
				claimPayment.setPaymentType(ReferenceTable.PAYMENT_TYPE_CHEQUE);
				claimPayment.setAccountNumber(null);
				claimPayment.setIfscCode(null);
				claimPayment.setBankCode(null);
				claimPayment.setBankName(null);
				claimPayment.setBranchName(null);
				if (null != createAndSearchLotTableDTO.getPayableAt())
					claimPayment.setPayableAt(createAndSearchLotTableDTO
							.getPayableAt());
			} else {
				claimPayment.setPaymentType(ReferenceTable.BANK_TRANSFER);
				claimPayment.setPayableAt(null);
				if (null != createAndSearchLotTableDTO.getBeneficiaryAcntNo())
					claimPayment.setAccountNumber(createAndSearchLotTableDTO
							.getBeneficiaryAcntNo());
				if (null != createAndSearchLotTableDTO.getIfscCode())
					claimPayment.setIfscCode(createAndSearchLotTableDTO
							.getIfscCode());
				if (null != createAndSearchLotTableDTO.getBranchName())
					claimPayment.setBranchName(createAndSearchLotTableDTO
							.getBranchName());
				if (null != createAndSearchLotTableDTO.getBankName())
					claimPayment.setBankName(createAndSearchLotTableDTO
							.getBankName());
			}*/
			if(createAndSearchLotTableDTO.getPaymentType()!=null && createAndSearchLotTableDTO.getPaymentType().getId().equals(ReferenceTable.PAYMENT_MODE_BANK_TRANSFER)){
				claimPayment.setPaymentType(ReferenceTable.BANK_TRANSFER);
			}else if(createAndSearchLotTableDTO.getPaymentType()!=null && createAndSearchLotTableDTO.getPaymentType().getId().equals(ReferenceTable.PAYMENT_MODE_CHEQUE_DD)){
				claimPayment.setPaymentType(ReferenceTable.PAYMENT_TYPE_CHEQUE);
			}
			if (null != createAndSearchLotTableDTO.getPayableAt())
				claimPayment.setPayableAt(createAndSearchLotTableDTO
						.getPayableAt());
			if (null != createAndSearchLotTableDTO.getBeneficiaryAcntNo())
				claimPayment.setAccountNumber(createAndSearchLotTableDTO
						.getBeneficiaryAcntNo());
			if (null != createAndSearchLotTableDTO.getIfscCode())
				claimPayment.setIfscCode(createAndSearchLotTableDTO
						.getIfscCode());
			if (null != createAndSearchLotTableDTO.getBranchName())
				claimPayment.setBranchName(createAndSearchLotTableDTO
						.getBranchName());
			if (null != createAndSearchLotTableDTO.getBankName())
				claimPayment.setBankName(createAndSearchLotTableDTO
						.getBankName());

			if (null != createAndSearchLotTableDTO.getPayeeName()) {
				claimPayment.setPayeeName(createAndSearchLotTableDTO
						.getPayeeName().getValue());
			}else{
				claimPayment.setPayeeName(null);
			}
			
			if (null != createAndSearchLotTableDTO.getPayeeNameStr()) {
				claimPayment.setPayeeName(createAndSearchLotTableDTO
						.getPayeeNameStr());
			}else{
				claimPayment.setPayeeName(null);
			}
			
			if (null != createAndSearchLotTableDTO.getEmailID())
				claimPayment
						.setZonalMailId(createAndSearchLotTableDTO.getEmailID());

			if (null != createAndSearchLotTableDTO.getReasonForChange())
				claimPayment.setReasonForChange(createAndSearchLotTableDTO
						.getReasonForChange());
			
			if (null != createAndSearchLotTableDTO.getPayModeChangeReason())
				claimPayment.setPayModeChangeReason(createAndSearchLotTableDTO
						.getPayModeChangeReason());
			
			if (null != createAndSearchLotTableDTO.getGmcEmployeeName())
				claimPayment.setGmcEmployeeName(createAndSearchLotTableDTO
						.getGmcEmployeeName());
			
			if (null != createAndSearchLotTableDTO.getPanNo())
				claimPayment
						.setPanNumber(createAndSearchLotTableDTO.getPanNo());

			if (null != createAndSearchLotTableDTO.getLegalFirstName()){
				claimPayment.setLegalHeirName(createAndSearchLotTableDTO
						.getLegalFirstName());
			}
			else{
				claimPayment.setLegalHeirName(null);
			}
			
			if(null != createAndSearchLotTableDTO.getNumberofdays())
			{
			claimPayment.setDelayDays(Long.valueOf(createAndSearchLotTableDTO.getNumberofdays()));	
			}
			else
			{
				claimPayment.setDelayDays(0l);
			}
			if(null != createAndSearchLotTableDTO.getNoofdaysexceeding())
			{
				claimPayment.setAllowedDelayDays(Long.valueOf(createAndSearchLotTableDTO.getNoofdaysexceeding()));
			}
			else
			{
				claimPayment.setAllowedDelayDays(0l);
			}

			if(null != createAndSearchLotTableDTO.getIntrestRate())
			{
			claimPayment.setInterestRate(createAndSearchLotTableDTO.getIntrestRate());	
			}
			else
			{
				claimPayment.setInterestRate(0d);
			}

			if(null != createAndSearchLotTableDTO.getIntrestAmount())
			{
								
				claimPayment.setInterestAmount(createAndSearchLotTableDTO.getIntrestAmount());
			}
			else
			{
				claimPayment.setInterestAmount(0d);
			}

			if(null !=createAndSearchLotTableDTO.getPenalTotalAmnt())
			{
				
				claimPayment.setTotalAmount(createAndSearchLotTableDTO.getPenalTotalAmnt());
				
			}
			
			if(null != createAndSearchLotTableDTO.getRemarks())
			{
				claimPayment.setIntrestRemarks(createAndSearchLotTableDTO.getRemarks());	
			}
			
			claimPayment.setSaveFlag(SHAConstants.YES_FLAG);
			
			claimPayment.setVerifiedStatusFlag(createAndSearchLotTableDTO.getVerifiedStatusFlag());
			
			claimPayment.setVnrFlag(SHAConstants.VERIFICATION_NOT_REQUIRED);

			claimPayment.setVerifiedUserId(createAndSearchLotTableDTO.getUserId());

			claimPayment.setVerifiedDate(new Date());

			
			
			/*Reimbursement rodkey = claimPayment.getRodkey();
			if(rodkey!=null && true1){
				rodkey.setUpdatePaymentDtlsFlag("Y");
				entityManager.merge(rodkey);
			}*/
			if (null != claimPayment.getKey()) {
				entityManager.merge(claimPayment);
				entityManager.flush();
				entityManager.clear();

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	public void updatePayeeName(
			CreateAndSearchLotTableDTO createAndSearchLotTableDTO,ViewSearchCriteriaTableDTO viewSearchDto) {
		try {
			ClaimPayment claimPayment = getClaimPayment(createAndSearchLotTableDTO
					.getClaimPaymentKey());
		
			if (null != createAndSearchLotTableDTO.getPayeeName()) {
				claimPayment.setPayeeName(createAndSearchLotTableDTO.getPayeeName().getValue());
				
			}	
			else if(null != viewSearchDto.getChangePayeeName()){
				claimPayment.setPayeeName(viewSearchDto.getChangePayeeName());
			}
			else if (null != createAndSearchLotTableDTO.getPayeeNameStr()) {
				claimPayment.setPayeeName(createAndSearchLotTableDTO.getPayeeNameStr());
			}			
			else
			{
				claimPayment.setPayeeName(null);
			}
		
			if (null != claimPayment.getKey()) {
				entityManager.merge(claimPayment);
				entityManager.flush();
				entityManager.clear();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public Policy getPolicyByPolicyNubember(String policyNumber){
		Query query = entityManager.createNamedQuery("Policy.findByPolicyNumber");
		query.setParameter("policyNumber", policyNumber);
		
		List<Policy> result =  (List<Policy>) query.getResultList();
		if(result != null && !result.isEmpty()){
			return result.get(0);
		}
		return null;
	}
		
	private MasHospitals getMasHospitalById(Long key,EntityManager entityManager) {

		Query query = entityManager.createNamedQuery("MasHospitals.findByKey");
		query.setParameter("key", key);

		List<MasHospitals> resultList = (List<MasHospitals>) query.getResultList();

		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;

	}
	
	public String callPaymentFunDeDupProcedure(String intimationNo, String accountNo,String ifscCode, String payeeName,
			String legalHierName) {
		
		Query cs = entityManager.createNativeQuery(
				"select FUN_PAYMENT_DE_DUPE (?1,?2,?3,?4,?5) from dual");
		cs.setParameter(1, intimationNo);
		cs.setParameter(2, accountNo);
		cs.setParameter(3, ifscCode);
		cs.setParameter(4, payeeName);
		cs.setParameter(5, legalHierName);
		String result = (String) cs.getSingleResult();
		return result;
	}
}
