package com.shaic.reimburement.gatewayAddAdditinalDocument.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.fields.dto.AbstractDAO;
import com.shaic.arch.table.Page;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimPayment;
import com.shaic.domain.Hospitals;
import com.shaic.domain.Intimation;
import com.shaic.domain.Policy;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.TmpCPUCode;
import com.shaic.ims.bpm.claim.DBCalculationService;
@Stateless
public class PhysicalDocumentReceivedMakerService  extends AbstractDAO<Reimbursement>{



	@PersistenceContext
	protected EntityManager entityManager;

	public PhysicalDocumentReceivedMakerService() {
		super();
	}

	public Page<PhysicalDocumentReceivedMakerTableDTO> search(
			PhysicalDocumentReceivedMakerFormDTO searchFormDTO, String userName,
			String passWord) {
		List<Reimbursement> reimbursementList = new ArrayList<Reimbursement>();
		try {
			String intimationNo = null != searchFormDTO && !searchFormDTO.getIntimationNo().isEmpty() ? searchFormDTO
					.getIntimationNo() : null;
			String policyNo = null != searchFormDTO && !searchFormDTO.getPolicyNo().isEmpty() ? searchFormDTO
					.getPolicyNo() : null;

			final CriteriaBuilder criteriaBuilder = entityManager
					.getCriteriaBuilder();
			final CriteriaQuery<Reimbursement> criteriaQuery = criteriaBuilder
					.createQuery(Reimbursement.class);

			Root<Reimbursement> root = criteriaQuery.from(Reimbursement.class);

			List<Predicate> conditionList = new ArrayList<Predicate>();
			if (intimationNo != null) {
				Predicate condition1 = criteriaBuilder.like(
						root.<Claim> get("claim")
								.<Intimation> get("intimation")
								.<String> get("intimationId"), "%"
								+ intimationNo + "%");
				conditionList.add(condition1);
			}
			if (policyNo != null) {
				Predicate condition2 = criteriaBuilder.like(
						root.<Claim> get("claim")
								.<Intimation> get("intimation")
								.<Policy> get("policy")
								.<String> get("policyNumber"), "%" + policyNo
								+ "%");
				conditionList.add(condition2);
			}
			
			Predicate condition8 = criteriaBuilder.equal(root.<Reimbursement>get("claim").<Intimation> get("intimation").<Long>get("lobId"), ReferenceTable.HEALTH_LOB_KEY);
			conditionList.add(condition8);	
			List<Long> myStatusList = new ArrayList<Long>();
			/*myStatusList.add(ReferenceTable.FVR_STAGE_KEY);
			myStatusList.add(ReferenceTable.BILLING_PROCESS_STAGE_KEY);
			myStatusList
					.add(ReferenceTable.PROCESS_CLAIM_REQUEST_ZONAL_STAGE_KEY);
			myStatusList.add(ReferenceTable.PROCESS_CLAIM_REQUEST_STAGE_KEY);
			myStatusList.add(ReferenceTable.CREATE_ROD_STAGE_KEY);
			
			myStatusList.add(ReferenceTable.BILLING_STAGE);*/
			//myStatusList.add()
			/*Expression<Long> exp = root.<Stage> get("stage").<Long> get("key");
			Predicate condition3 = exp.in(myStatusList);
			conditionList.add(condition3);*/
			
			//Expression<Long> exp = root.<Stage> get("stage").<Long> get("key");
			/*Predicate condition3 = criteriaBuilder.notEqual(root.<Status> get("status").<Long> get("key"), ReferenceTable.FINANCIAL_APPROVE_STATUS);
			conditionList.add(condition3);*/
			

			/*Predicate condition4 = criteriaBuilder.notEqual(root.<Stage> get("stage").<Long> get("key"), ReferenceTable.FINANCIAL_STAGE);
			conditionList.add(condition4);*/

//			Predicate condition4 = criteriaBuilder.notEqual(root.<Stage> get("stage").<Long> get("key"), ReferenceTable.FINANCIAL_STAGE);
//			conditionList.add(condition4);

			
			/*
			 * if((intimationNo == null && policyNo == null)
			 * ||(intimationNo.isEmpty() && policyNo.isEmpty())){ return null; }
			 * else
			 */// {
			criteriaQuery.select(root).where(
					criteriaBuilder.and(conditionList
							.toArray(new Predicate[] {})));
			// }
			final TypedQuery<Reimbursement> typedQuery = entityManager
					.createQuery(criteriaQuery);
			int pageNumber = searchFormDTO.getPageable().getPageNumber();
			int firtResult;
			if(pageNumber > 1){
				firtResult = (pageNumber-1) *10;
			}else{
				firtResult = 1;
			}
			/*
			listIntimations = typedQuery.setFirstResult(firtResult).setMaxResults(10).getResultList();*/
			
			reimbursementList = typedQuery.getResultList();
			List<Reimbursement> listOfReimbursement = new ArrayList<Reimbursement>();
			Map<Long, Long> rejectMap = ReferenceTable.REJECT_ROD_KEYS;
			for (Reimbursement reimbursement : reimbursementList) {
				/*if(reimbursement.getStage() != null && !reimbursement.getStage().getKey().equals(ReferenceTable.CREATE_ROD_STAGE_KEY) 
						 && !reimbursement.getStatus().getKey().equals(ReferenceTable.FINANCIAL_APPROVE_STATUS)
						) {
					listOfReimbursement.add(reimbursement);
					continue;
				}
				if(reimbursement.getStage() != null && reimbursement.getStage().getKey().equals(ReferenceTable.CREATE_ROD_STAGE_KEY) && reimbursement.getClaim().getClaimType() != null && reimbursement.getClaim().getClaimType().getKey().equals(ReferenceTable.CASHLESS_CLAIM_TYPE_KEY) && reimbursement.getDocAcknowLedgement().getHospitalisationFlag() != null && reimbursement.getDocAcknowLedgement().getHospitalisationFlag().equalsIgnoreCase("Y")) {
					listOfReimbursement.add(reimbursement);
				}*/
				
				/**
				 * Records which move from create ROD to zmr skipping bill entry,
				 * documents needs to be added through add additional documents menu.
				 * This wasn't possible earlier because of above code. Since a ticket
				 * was raised, the above functionality is commented and user can
				 * add record in additional document menu irrespective of stage.
				 * Only FA approved records cannot be added.
				 * 
				 * This change was done as a part of ticket 2206.
				 * */
				if(!reimbursement.getStatus().getKey().equals(ReferenceTable.FINANCIAL_APPROVE_STATUS)
						&& reimbursement.getDocAcknowLedgement().getCheckerVerified() == null)
				{
					if(null != rejectMap && !rejectMap.isEmpty() && null == rejectMap.get(reimbursement.getStatus().getKey()))
						listOfReimbursement.add(reimbursement);
				}
//				Below condition added for additional changes in Mode of receipt Online/Email
				ClaimPayment paymentDtls =  getClaimPaymentByRodKey(reimbursement.getKey());
				if(paymentDtls != null && paymentDtls.getStatusId().getKey().equals(ReferenceTable.PAYMENT_HOLD_STATUS)
						&& reimbursement.getDocAcknowLedgement().getCheckerVerified() == null){
					listOfReimbursement.add(reimbursement);
				}
//				Boolean dbTaskForCurrentQ = getDBTaskForCurrentQ(reimbursement.getClaim().getIntimation(), SHAConstants.ZMR_CURRENT_QUEUE, reimbursement.getKey());
//				if(dbTaskForCurrentQ){
//					listOfReimbursement.add(reimbursement);
//				}else{
//					Boolean corporateCurrentQ = getDBTaskForCurrentQ(reimbursement.getClaim().getIntimation(), SHAConstants.MA_CURRENT_QUEUE, reimbursement.getKey());
//					if(corporateCurrentQ){
//						listOfReimbursement.add(reimbursement);
//					}
//				}
				
				
			}
			List<Reimbursement> doList = listOfReimbursement;
			List<PhysicalDocumentReceivedMakerTableDTO> tableDTO = PhysicalDocumentReceivedMakerMapper.getInstance()
					.getIntimationDTO(doList);
			// tableDTO =
			// SearchAcknowledgementDocumentReceiverMapper.getClaimDTO(getclaimNumber(tableDTO));
			// tableDTO = getclaimNumber(tableDTO);
			tableDTO = getHospitalDetails(tableDTO);
			List<PhysicalDocumentReceivedMakerTableDTO> result = new ArrayList<PhysicalDocumentReceivedMakerTableDTO>();

			Boolean flag = true;

			for (PhysicalDocumentReceivedMakerTableDTO searchAddAditionalDocoumentTableDTO : tableDTO) {
				searchAddAditionalDocoumentTableDTO.setUsername(userName);
				searchAddAditionalDocoumentTableDTO.setPassword(passWord);
				for (PhysicalDocumentReceivedMakerTableDTO searchAddAditionalDocoumentTableDTOResult : result) {
					if (searchAddAditionalDocoumentTableDTOResult
							.getIntimationNo().equalsIgnoreCase(
									searchAddAditionalDocoumentTableDTO
											.getIntimationNo())) {
						flag = false;
					}
				}
				if (flag) {
					result.add(searchAddAditionalDocoumentTableDTO);					
				}
				flag = true;
			}

			// result.addAll(tableDTO);
			Page<PhysicalDocumentReceivedMakerTableDTO> page = new Page<PhysicalDocumentReceivedMakerTableDTO>();
			searchFormDTO.getPageable().setPageNumber(pageNumber+1);
			page.setHasNext(true);
			if(result.isEmpty()){
				searchFormDTO.getPageable().setPageNumber(1);
			}
			page.setPageItems(result);
			page.setIsDbSearch(true);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("hhhhhhhhhhhhhhhhhhhhhhhh" + e.getMessage()
					+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		}
		return null;
	}

	@Override
	public Class<Reimbursement> getDTOClass() {
		// TODO Auto-generated method stub
		return Reimbursement.class;
	}

	/*
	 * private List<SearchAddAditionalDocoumentTableDTO>
	 * getclaimNumber(List<SearchAddAditionalDocoumentTableDTO> intimationList){
	 * Claim a_claim = null; for(int index = 0; index < intimationList.size();
	 * index++){
	 * System.out.println("Intimationkey+++++++++++++++++++++"+intimationList
	 * .get(index).getKey());
	 * 
	 * if (intimationList.get(index).getKey() != null) {
	 * 
	 * Query findByIntimationKey = entityManager
	 * .createNamedQuery("Claim.findByIntimationKey"); findByIntimationKey =
	 * findByIntimationKey.setParameter( "intimationKey",
	 * intimationList.get(index).getKey()); try{ a_claim = (Claim)
	 * findByIntimationKey.getSingleResult(); if(a_claim != null){
	 * intimationList.get(index).setClaimNo(a_claim.getClaimId());
	 * intimationList.get(index).setClaimKey(a_claim.getKey()); }else{
	 * intimationList.get(index).setClaimNo(""); } }catch(Exception e){
	 * continue; } } } return intimationList; }
	 */

	private List<PhysicalDocumentReceivedMakerTableDTO> getHospitalDetails(
			List<PhysicalDocumentReceivedMakerTableDTO> tableDTO) {
		Hospitals hospitalDetail;
		for (int index = 0; index < tableDTO.size(); index++) {

			Query findByHospitalKey = entityManager.createNamedQuery(
					"Hospitals.findByKey").setParameter("key",
					tableDTO.get(index).getHospitalNameID());
			try {
				hospitalDetail = (Hospitals) findByHospitalKey
						.getSingleResult();
				if (hospitalDetail != null) {
					tableDTO.get(index).setHospitalName(
							hospitalDetail.getName());

					tableDTO.get(index).setHospitalCity(
							hospitalDetail.getCityId() + " ,"
									+ hospitalDetail.getCity());
					tableDTO.get(index).setCpuId(hospitalDetail.getCpuId());
					Long cpuCode = null != getTmpCPUCode(tableDTO.get(index)
							.getCpuId()) ? getTmpCPUCode(
							tableDTO.get(index).getCpuId()).getCpuCode() : 0l;
					tableDTO.get(index).setCpuCode(cpuCode);
				}
			} catch (Exception e) {
				continue;
			}

		}

		return tableDTO;
	}

	private TmpCPUCode getTmpCPUCode(Long cpuId) {
		try {
			Query findCpuCode = entityManager.createNamedQuery(
					"TmpCPUCode.findByKey").setParameter("cpuId", cpuId);
			TmpCPUCode tmpCPUCode = (TmpCPUCode) findCpuCode.getSingleResult();
			return tmpCPUCode;
		} catch (Exception e) {

		}
		return null;
	}

	/*
	 * private CityTownVillage getCityTownVillage(Long cpuId){
	 * 
	 * try{ Query findCpuCode =
	 * entityManager.createNamedQuery("CityTownVillage.findByKey"
	 * ).setParameter("cpuId", cpuId); CityTownVillage cityTownVillage =
	 * (CityTownVillage) findCpuCode.getSingleResult(); return cityTownVillage;
	 * }catch(Exception e){
	 * 
	 * } return null;
	 * 
	 * }
	 */
	private Reimbursement getType(Long key) {
		try {
			Query findType = entityManager.createNamedQuery(
					"Reimbursement.findByKey").setParameter("primaryKey", key);
			Reimbursement reimbursement = (Reimbursement) findType
					.getSingleResult();
			return reimbursement;
		} catch (Exception e) {

		}
		return null;
	}
	
public Boolean getDBTaskForCurrentQ(Intimation intimation,String currentQ,Long reimbursementKey){
		
		Map<String, Object> mapValues = new WeakHashMap<String, Object>();
		mapValues.put(SHAConstants.INTIMATION_NO, intimation.getIntimationId());
		mapValues.put(SHAConstants.CURRENT_Q, currentQ);
		
		Object[] setMapValues = SHAUtils.setObjArrayForGetTask(mapValues);
		
		DBCalculationService db = new DBCalculationService();
		 List<Map<String, Object>> taskProcedure = db.getTaskProcedure(setMapValues);
		
		if (taskProcedure != null && !taskProcedure.isEmpty()){
			 for (Map<String, Object> map : taskProcedure) {
				 Long keyValue = (Long) map.get(SHAConstants.PAYLOAD_ROD_KEY);
				 if(keyValue != null && keyValue.equals(reimbursementKey)){
					 return true;
				 }
			}
		} 
		return false;
	}

	public ClaimPayment getClaimPaymentByRodKey(Long rodkey) {
	    Query query = entityManager.createNamedQuery("ClaimPayment.findByRodKey");
	    query.setParameter("rodKey", rodkey);
	    List<ClaimPayment> claimPayment = (List<ClaimPayment>)query.getResultList();
	    
	    if(claimPayment != null && ! claimPayment.isEmpty()){
	            for (ClaimPayment claimPayment2 : claimPayment) {
	                    entityManager.refresh(claimPayment2);
	            }
	            return claimPayment.get(0);
	    }
	            return null;
	    
	}

}
