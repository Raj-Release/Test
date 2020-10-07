package com.shaic.claim.pcc.zonalMedicalHead;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.claim.pcc.SearchProcessPCCRequestMapper;
import com.shaic.claim.pcc.beans.MasterPCCRole;
import com.shaic.claim.pcc.beans.PCCCategory;
import com.shaic.claim.pcc.beans.PCCQuery;
import com.shaic.claim.pcc.beans.PCCRequest;
import com.shaic.claim.pcc.dto.PCCQueryDetailsTableDTO;
import com.shaic.claim.pcc.dto.PCCReplyDetailsTableDTO;
import com.shaic.claim.pcc.dto.PccDTO;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestFormDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;
import com.shaic.domain.Intimation;
import com.shaic.domain.MasUser;
import com.shaic.domain.MastersValue;
import com.shaic.domain.PreauthService;
import com.shaic.domain.Status;
import com.shaic.domain.TmpCPUCode;
import com.shaic.domain.TmpEmployee;
import com.vaadin.v7.data.util.BeanItemContainer;

@Stateless
public class ZonalMedicalHeadRequestService {


	@PersistenceContext
	protected EntityManager entityManager;
	
	Map<Long, Object> workFlowMap= null;

	@EJB
	private PreauthService preauthService;

	public Page<SearchProcessPCCRequestTableDTO> search(SearchProcessPCCRequestFormDTO searchFormDTO, String userName,String passWord) {


		try{
			String intimationNo = searchFormDTO.getIntimationNo() != null && !searchFormDTO.getIntimationNo().isEmpty() ? searchFormDTO.getIntimationNo():null;
			Long cpuCode = searchFormDTO.getCpuCode() != null && searchFormDTO.getCpuCode().getId() !=null ? searchFormDTO.getCpuCode().getId():null;
			Long source = searchFormDTO.getSource() != null && searchFormDTO.getSource().getId() !=null ? searchFormDTO.getSource().getId():null;
			Long pccCatagory = searchFormDTO.getPccCatagory() != null && searchFormDTO.getPccCatagory().getId() !=null ? searchFormDTO.getPccCatagory().getId():null;

			final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			List<SearchProcessPCCRequestTableDTO> tableDTO = new ArrayList<SearchProcessPCCRequestTableDTO>();
			int pageNumber = searchFormDTO.getPageable().getPageNumber();
			int firtResult;
			List<PCCRequest> doList = new ArrayList<PCCRequest>();
			final CriteriaQuery<PCCRequest> criteriaQuery = criteriaBuilder.createQuery(PCCRequest.class);
			Root<PCCRequest> root = criteriaQuery.from(PCCRequest.class);
			List<Long> statusListKey= new ArrayList<Long>();
			statusListKey.add(SHAConstants.PCC_PROCESSOR_APPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_ZC_NEGOTIATION_DISAPPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_HRMC_NEGOTIATION_DISAPPROVED_STATUS);

			List<Predicate> conditionList = new ArrayList<Predicate>();
			if(intimationNo != null){
				Predicate condition1 = criteriaBuilder.equal(root.<String>get("intimationNo"),intimationNo);
				conditionList.add(condition1);
			}
			if(cpuCode != null){
				Predicate condition2 = criteriaBuilder.equal(root.<Intimation>get("intimation").<TmpCPUCode>get("cpuCode").<Long>get("key"), cpuCode);
				conditionList.add(condition2);
			}
			if(source != null){
				Predicate condition3 = criteriaBuilder.equal(root.<MastersValue>get("pccSource").<Long>get("key"),source);
				conditionList.add(condition3);
			}
			if(pccCatagory != null){
				Predicate condition4 = criteriaBuilder.equal(root.<PCCCategory>get("pccCategory").<Long>get("key"), pccCatagory);
				conditionList.add(condition4);
			}


			Predicate statusCondition =  root.<Status>get("status").get("key").in(statusListKey); 
			conditionList.add(statusCondition);

			if(intimationNo == null && cpuCode == null && source == null && pccCatagory == null){
				criteriaQuery.select(root).where(conditionList.toArray(new Predicate[]{}));
			} else{
				criteriaQuery.select(root).where(
						criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
			}

			final TypedQuery<PCCRequest> typedQuery = entityManager.createQuery(criteriaQuery);

			if(pageNumber > 1){
				firtResult = (pageNumber-1) *10;
			}else{
				firtResult = 0;
			}

			if(intimationNo == null){
				doList = typedQuery.setFirstResult(firtResult).setMaxResults(10).getResultList();
			}else{
				doList = typedQuery.getResultList();
			}
			tableDTO = SearchProcessPCCRequestMapper.getInstance().getProcessPCCRequestTableDTOs(doList);		
							
			List<SearchProcessPCCRequestTableDTO> result = new ArrayList<SearchProcessPCCRequestTableDTO>();
			result.addAll(tableDTO);
			Page<SearchProcessPCCRequestTableDTO> page = new Page<SearchProcessPCCRequestTableDTO>();
			searchFormDTO.getPageable().setPageNumber(pageNumber+1);
			page.setHasNext(true);
			if(result.isEmpty()){
				searchFormDTO.getPageable().setPageNumber(1);
			}
			page.setPageNumber(pageNumber);
			page.setPageItems(result);
			page.setIsDbSearch(true);

			return page;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return null;

	}

	public PCCRequest getPCCRequestByKey(Long key) {
		Query findByKey = entityManager.createNamedQuery("PCCRequest.findByKey");
		findByKey = findByKey.setParameter("key", key);
		List<PCCRequest> pccList = findByKey.getResultList();
		if(null != pccList && !pccList.isEmpty()){
			entityManager.refresh(pccList.get(0));
			return pccList.get(0);
		}
		return null;
	}


	
	public void submitZonalMedicalHead(PccDTO pccDTO,String userName){
		
		if(pccDTO.getPccKey() !=null){
			PCCRequest pccRequest= getPCCRequestByKey(pccDTO.getPccKey());
			if(pccDTO.getIsAssign() !=null && pccDTO.getIsAssign()){

				PCCQuery pccQuery = new PCCQuery();
				Status status = new Status();
				SelectValue roleAssigned = pccDTO.getUserRoleAssigned();
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.ZONAL_MEDICAL_HEAD_SOURCE);
				if(roleAssigned !=null && roleAssigned.getCommonValue() !=null){
					if(roleAssigned.getCommonValue().equals(SHAConstants.HRM_COORDINATOR_ROLE)){
						status.setKey(SHAConstants.PCC_HRMC_ASSIGNED_STATUS);
					}else if(roleAssigned.getCommonValue().equals(SHAConstants.ZONAL_COORDINATOR_ROLE)){
						status.setKey(SHAConstants.PCC_ZC_ASSIGNED_STATUS);
					}
					pccQuery.setRoleAssigned(roleAssigned.getCommonValue());
				}			
				pccQuery.setPccRequest(pccRequest);
				pccQuery.setQueryRemarks(pccDTO.getRemarksAssignforZMH());	
				pccQuery.setRoleAssignedBy(SHAConstants.ZONAL_MEDICAL_HEAD_ROLE);
				pccQuery.setUserAssignedBy(userName);
				pccQuery.setStatus(status);
				pccQuery.setCreatedBy(userName);
				pccQuery.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				pccQuery.setPccSource(mastersValue);
				entityManager.persist(pccQuery);
				entityManager.flush();
				entityManager.clear();	

				pccRequest.setPccSource(mastersValue);
				pccRequest.setStatus(status);
				pccRequest.setModifiedBy(userName);
				pccRequest.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				pccRequest.setLockedBy(null);
				pccRequest.setLockFlag("N");
				pccRequest.setLockedDate(null);
				entityManager.merge(pccRequest);
				entityManager.flush();
				entityManager.clear();

			}else if(pccDTO.getIsResponse() !=null && pccDTO.getIsResponse()){
				Status status = new Status();
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.ZONAL_MEDICAL_HEAD_SOURCE);
				if(pccDTO.getIsNegotiation()){
					status.setKey(SHAConstants.PCC_ZMH_NEGOTIATION_APPROVED_STATUS);
				}else{
					status.setKey(SHAConstants.PCC_ZMH_NEGOTIATION_DISAPPROVED_STATUS);
				}
				pccRequest.setZonalRemark(pccDTO.getRemarksNegotioanforZMH());
				pccRequest.setStatus(status);
				pccRequest.setModifiedBy(userName);
				pccRequest.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				pccRequest.setPccSource(mastersValue);
				pccRequest.setLockedBy(null);
				pccRequest.setLockFlag("N");
				pccRequest.setLockedDate(null);
				entityManager.merge(pccRequest);
				entityManager.flush();
				entityManager.clear();
			}
		}
	}
}
