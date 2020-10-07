package com.shaic.claim.corpbuffer.allocation.search;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.shaic.arch.SHAConstants;
import com.shaic.arch.table.Page;
import com.shaic.claim.corpbuffer.allocation.wizard.AllocateCorpBufferDetailDTO;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimService;
import com.shaic.domain.Hospitals;
import com.shaic.domain.Intimation;
import com.shaic.domain.Policy;
import com.shaic.domain.PreauthService;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.Status;
import com.shaic.domain.preauth.Preauth;
import com.shaic.domain.preauth.StageInformation;
import com.shaic.domain.reimbursement.ReimbursementService;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.vaadin.server.VaadinSession;

@Stateless
public class AllocateCorpBufferService {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	@EJB
	private ClaimService claimService;
	
	@EJB
	private PreauthService preauthService;
	
	@EJB
	private ReimbursementService reimbursementService;

	public Page<AllocateCorpBufferTableDTO> search(AllocateCorpBufferFormDTO searchFormDTO) {
		List<Claim> listOfClaim = new ArrayList<Claim>(); 
		
		try {
			String intimationNo = searchFormDTO.getIntimationNo() != null ? searchFormDTO.getIntimationNo() : null;
			String policyNo = searchFormDTO.getPolicyNo() != null ? searchFormDTO.getPolicyNo() : null;
			
			final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			final CriteriaQuery<Claim> criteriaQuery = criteriaBuilder.createQuery(Claim.class);
			
			Root<Claim> root = criteriaQuery.from(Claim.class);
			List<Predicate> conditionList = new ArrayList<Predicate>();
			
			if (intimationNo != null && !intimationNo.isEmpty()) {
				Predicate intimationCriteria = criteriaBuilder.like(root.<Intimation>get("intimation").<String>get("intimationId"), "%"+intimationNo+"%");
				conditionList.add(intimationCriteria);
			}
			
			if (policyNo != null && !policyNo.isEmpty()) {
				Predicate policyCriteria = criteriaBuilder.like(root.<Intimation>get("intimation").<Policy>get("policy").<String>get("policyNumber"), "%"+policyNo+"%");
				conditionList.add(policyCriteria);
			}
			
			Predicate closeCriteria =  criteriaBuilder.notEqual(root.<Status>get("status").<Long>get("key"), ReferenceTable.CLAIM_CLOSED_STATUS);
			conditionList.add(closeCriteria);		
					
			criteriaQuery.select(root).where(conditionList.toArray(new Predicate[] {}));
			final TypedQuery<Claim> typedQuery = entityManager.createQuery(criteriaQuery);
			listOfClaim = typedQuery.getResultList();
		    List<Claim> doList = listOfClaim;

			List<AllocateCorpBufferTableDTO> tableDTO = AllocateCorpBufferMapper.getInstance().getClaimDTO(doList);
			tableDTO = getHospitalDetails(tableDTO);
			
			DBCalculationService dbCalculationService = new DBCalculationService();
			for (AllocateCorpBufferTableDTO bufferTableDto : tableDTO) {
				Long lPolicyNumber = bufferTableDto.getPolicyKey();
				Long insuredId = bufferTableDto.getInsuredId();
				Double sumInsured = dbCalculationService.getInsuredSumInsured(insuredId.toString(), lPolicyNumber,"H");
				Long insuredKey = bufferTableDto.getInsuredKey();
				Long claimKey = bufferTableDto.getKey();
				
				if ((ReferenceTable.getGMCProductList().containsKey(bufferTableDto.getProductKey()))) {
					bufferTableDto.setSumInsured(dbCalculationService.getBalanceSIForGMC(lPolicyNumber, insuredKey, claimKey));
				} else if ((ReferenceTable.GPA_PRODUCT_KEY.equals(bufferTableDto.getProductKey()))) {
					bufferTableDto.setSumInsured(dbCalculationService.getBalanceSI(lPolicyNumber, insuredKey , claimKey , sumInsured , bufferTableDto.getIntimationKey()).get(SHAConstants.TOTAL_BALANCE_SI));
				} else {
					bufferTableDto.setSumInsured(dbCalculationService.getBalanceSI(lPolicyNumber, insuredKey , claimKey , sumInsured , bufferTableDto.getIntimationKey()).get(SHAConstants.TOTAL_BALANCE_SI));
				}
			}
			
			List<AllocateCorpBufferTableDTO> result = new ArrayList<AllocateCorpBufferTableDTO>();
			result.addAll(tableDTO);
			Page<AllocateCorpBufferTableDTO> page = new Page<AllocateCorpBufferTableDTO>();
			page.setPageItems(result);
			page.setIsDbSearch(false);
			page.setTotalRecords(listOfClaim.size());
			return page;
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		return null;		
	}
	
	private List<AllocateCorpBufferTableDTO> getHospitalDetails(List<AllocateCorpBufferTableDTO> tableDTO) {
		Hospitals hospitalDetail;
		for (int index = 0; index < tableDTO.size(); index++) {
			Query findByHospitalKey = entityManager.createNamedQuery("Hospitals.findByKey").setParameter("key", tableDTO.get(index).getHospitalNameID());
			try {
				hospitalDetail = (Hospitals) findByHospitalKey.getSingleResult();
				if (hospitalDetail != null) {
					tableDTO.get(index).setHospitalName(hospitalDetail.getName());
				}
			} catch(Exception e) {
				continue;
			}
		}
		return tableDTO;
	}

	public void updateCorpBufferLimit(Double sumInsured, AllocateCorpBufferDetailDTO corpBufferDto) {
		if (sumInsured != null && corpBufferDto != null && corpBufferDto.getClaimKey() != null) {
			Claim claim = claimService.getClaimByKey(corpBufferDto.getClaimKey());
			if (claim != null) {
				claim.setGmcCorpBufferLmt(sumInsured);
				claim.setGmcCorpBufferFlag("Y");
				claim.setModifiedBy(corpBufferDto.getUserName() != null ? corpBufferDto.getUserName().toUpperCase() : "");
				claim.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				entityManager.merge(claim);
				entityManager.flush();
				entityManager.clear();
			}
			updateStageInformation(corpBufferDto);
		}
	}

	private void updateStageInformation(AllocateCorpBufferDetailDTO corpBufferDto) {
		if (corpBufferDto != null && corpBufferDto.getClaimKey() != null && corpBufferDto.getNewIntimationDto() != null
				&& corpBufferDto.getNewIntimationDto().getStage() != null) {
			Claim claim = claimService.getClaimByKey(corpBufferDto.getClaimKey());
			if (claim != null) {
				Preauth preauth = preauthService.getLatestPreauthDetails(claim.getKey());
				
				if(preauth != null){
					preauth.setCorporateBufferFlag(1L);
					preauth.setModifiedBy(corpBufferDto.getUserName() != null ? corpBufferDto.getUserName().toUpperCase() : "");
					preauth.setModifiedDate(new Timestamp(System.currentTimeMillis()));
					entityManager.merge(preauth);
					entityManager.flush();
					entityManager.clear();
				}
				
				Reimbursement reimbursement = reimbursementService.getLatestActiveROD(claim.getKey());
				StageInformation stgInformation = new StageInformation();
				stgInformation.setIntimation(claim.getIntimation());				
				stgInformation.setClaimType(claim.getClaimType());
				stgInformation.setStage(corpBufferDto.getNewIntimationDto().getStage());
				Status status = new Status();
				status.setKey(ReferenceTable.ALLOCATE_CORP_BUFFER_STATUS_KEY);
				status.setProcessValue(ReferenceTable.ALLOCATE_CORP_BUFFER_STATUS_VALUE);
				stgInformation.setStatus(status);
				stgInformation.setClaim(claim);
				if (reimbursement == null ) {
					stgInformation.setPreauth(preauth);
				} else {
					stgInformation.setReimbursement(reimbursement);
				}
				stgInformation.setCreatedBy(corpBufferDto.getUserName() != null ? corpBufferDto.getUserName().toUpperCase() : "");
				stgInformation.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				entityManager.persist(stgInformation);
				entityManager.flush();
				entityManager.clear();
			}
			
		}
		
	}

}
