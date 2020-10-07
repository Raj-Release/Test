package com.shaic.claim.pcc.processor;

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
public class SearchProcessPCCProcessorRequestService {


	@PersistenceContext
	protected EntityManager entityManager;

	Map<Long, Object> workFlowMap= null;

	@EJB
	private PreauthService preauthService;


	public Page<SearchProcessPCCRequestTableDTO> search(SearchProcessPCCRequestFormDTO searchFormDTO, String userName,String passWord) {


		try{
			userName = userName.toUpperCase();
			String intimationNo = searchFormDTO.getIntimationNo() != null && !searchFormDTO.getIntimationNo().isEmpty() ? searchFormDTO.getIntimationNo():null;
			Long cpuCode = searchFormDTO.getCpuCode() != null && searchFormDTO.getCpuCode().getId() !=null ? searchFormDTO.getCpuCode().getId():null;
			Long source = searchFormDTO.getSource() != null && searchFormDTO.getSource().getId() !=null ? searchFormDTO.getSource().getId():null;
			Long pccCatagory = searchFormDTO.getPccCatagory() != null && searchFormDTO.getPccCatagory().getId() !=null ? searchFormDTO.getPccCatagory().getId():null;

			List<SearchProcessPCCRequestTableDTO> tableDTO = new ArrayList<SearchProcessPCCRequestTableDTO>();
			List<PCCRequest> requestdoList = new ArrayList<PCCRequest>();
			List<Long> pccRequestsKeys = new ArrayList<Long>();
			final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

			List<Long> statusListKey = new ArrayList<Long>();
			statusListKey.add(SHAConstants.PCC_PROCESSOR_QUERY_INITIATED_STATUS);
			statusListKey.add(SHAConstants.PCC_PROCESSOR_INITIATED_STATUS);
			statusListKey.add(SHAConstants.PCC_ZMH_NEGOTIATION_DISAPPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_REVIEWER_QUERY_REPLIED_STATUS);
			statusListKey.add(SHAConstants.PCC_DIVISIONHEAD_QUERY_REPLIED_STATUS);

			int pageNumber = searchFormDTO.getPageable().getPageNumber();
			int firtResult;
			pccRequestsKeys.addAll(getqueryKeys(intimationNo,cpuCode,source,pccCatagory,userName));
			pccRequestsKeys.addAll(getqueryResponseKeys(intimationNo,cpuCode,source,pccCatagory,userName));
			pccRequestsKeys.addAll(getProcesserIntiatedKeys(intimationNo,cpuCode,source,pccCatagory));
			if(pccRequestsKeys !=null && !pccRequestsKeys.isEmpty()){
				List<Predicate> requestconditionList = new ArrayList<Predicate>();
				final CriteriaQuery<PCCRequest> pccRequestcriteria = criteriaBuilder.createQuery(PCCRequest.class);
				Root<PCCRequest> requestroot = pccRequestcriteria.from(PCCRequest.class);
				Predicate statusCondition =  requestroot.<Status>get("status").get("key").in(statusListKey); 
				requestconditionList.add(statusCondition);	
				Predicate keyCondition =  requestroot.<Long>get("key").in(pccRequestsKeys); 
				requestconditionList.add(keyCondition);	
				pccRequestcriteria.select(requestroot).where(criteriaBuilder.and(requestconditionList.toArray(new Predicate[] {})));
				final TypedQuery<PCCRequest> requesttypedQuery = entityManager.createQuery(pccRequestcriteria);

				if(pageNumber > 1){
					firtResult = (pageNumber-1) *10;
				}else{
					firtResult = 0;
				}

				if(intimationNo == null){
					requestdoList = requesttypedQuery.setFirstResult(firtResult).setMaxResults(10).getResultList();
				}else{
					requestdoList = requesttypedQuery.getResultList();
				}
				tableDTO = SearchProcessPCCRequestMapper.getInstance().getProcessPCCRequestTableDTOs(requestdoList);	

			}
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
			page.setTotalRecords(result.size());
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

	public PCCQuery getPCCQueryByKey(Long key) {
		Query findByKey = entityManager.createNamedQuery("PCCQuery.findByKey");
		findByKey = findByKey.setParameter("key", key);
		List<PCCQuery> pccList = findByKey.getResultList();
		if(null != pccList && !pccList.isEmpty()){
			entityManager.refresh(pccList.get(0));
			return pccList.get(0);
		}
		return null;
	}

	public void submitPCCProcessor(PccDTO pccDTO,String userName,PccDetailsTableDTO pccDetailsTableDTO){

		if(pccDTO.getPccKey() !=null){
			userName = userName.toUpperCase();
			PCCRequest pccRequest= getPCCRequestByKey(pccDTO.getPccKey());
			if(pccDTO.getIsApproved() !=null && pccDTO.getIsApproved()){

				Status status = new Status();
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.PCC_PROCESSOR_SOURCE);
				status.setKey(SHAConstants.PCC_PROCESSOR_APPROVED_STATUS);
				pccRequest.setProcessorRemark(pccDTO.getRemarksforApprove());
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

			}else if(pccDTO.getIsQueryRaised() !=null && pccDTO.getIsQueryRaised()){

				PCCQuery pccQuery = new PCCQuery();
				Status status = new Status();
				SelectValue roleAssigned = pccDTO.getUserRoleAssigned();
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.PCC_PROCESSOR_SOURCE);
				if(roleAssigned !=null && roleAssigned.getCommonValue() !=null){
					if(roleAssigned.getCommonValue().equals(SHAConstants.PCC_REVIEWER_ROLE)){
						status.setKey(SHAConstants.PCC_REVIEWER_QUERY_INITIATED_STATUS);
					}else if(roleAssigned.getCommonValue().equals(SHAConstants.DIVISION_HEAD_ROLE)){
						status.setKey(SHAConstants.PCC_DIVISIONHEAD_QUERY_INITIATED_STATUS);
					}
					pccQuery.setRoleAssigned(roleAssigned.getCommonValue());
				}			
				if(pccDTO.getUserNameAssigned() !=null && pccDTO.getUserNameAssigned().getCommonValue() !=null){
					String userNameAssigned = pccDTO.getUserNameAssigned().getCommonValue();
					pccQuery.setUserAssigned(userNameAssigned.toUpperCase());
				}
				pccQuery.setPccRequest(pccRequest);
				pccQuery.setQueryRemarks(pccDTO.getRemarksforQuery());	
				pccQuery.setRoleAssignedBy(SHAConstants.PCC_PROCESSOR_ROLE);
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

			}else if(pccDTO.getIsDisapproved() !=null && pccDTO.getIsDisapproved()){
				Status status = new Status();
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.PCC_PROCESSOR_SOURCE);
				status.setKey(SHAConstants.PCC_PROCESSOR_DISAPPROVED_STATUS);
				pccRequest.setProcessorRemark(pccDTO.getRemarkForDisapprove());
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
			}else if(pccDTO.getIsResponse() !=null && pccDTO.getIsResponse()
					&& pccDetailsTableDTO.getReponceQueryKey() !=null){
				PCCQuery pccQuery= getPCCQueryByKey(pccDetailsTableDTO.getReponceQueryKey());
				Status status = new Status();
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.PCC_PROCESSOR_SOURCE);
				status.setKey(SHAConstants.PCC_PROCESSOR_QUERY_REPLIED_STATUS);
				pccQuery.setQueryReplyRemarks(pccDTO.getRemarkForResponse());
				pccQuery.setStatus(status);
				pccQuery.setModifiedBy(userName);
				pccQuery.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				pccQuery.setPccSource(mastersValue);
				pccQuery.setRepliedBy(userName);
				pccQuery.setRepliedDate(new Timestamp(System.currentTimeMillis()));
				entityManager.merge(pccQuery);
				entityManager.flush();
				entityManager.clear();

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

	private List<Long> getqueryResponseKeys(String intimationNo,Long cpuCode,Long source,Long pccCatagory,String userName){

		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		List<Long> pccRequestsKeys = new ArrayList<Long>();
		List<PCCQuery> doList = new ArrayList<PCCQuery>();
		final CriteriaQuery<PCCQuery> criteriaQuery = criteriaBuilder.createQuery(PCCQuery.class);
		Root<PCCQuery> queryroot = criteriaQuery.from(PCCQuery.class);

		List<Predicate> conditionList = new ArrayList<Predicate>();
		if(intimationNo != null){
			Predicate condition1 = criteriaBuilder.equal(queryroot.<PCCRequest>get("pccRequest").<String>get("intimationNo"),intimationNo);
			conditionList.add(condition1);
		}
		if(cpuCode != null){
			Predicate condition2 = criteriaBuilder.equal(queryroot.<PCCRequest>get("pccRequest").<Intimation>get("intimation").<TmpCPUCode>get("cpuCode").<Long>get("key"), cpuCode);
			conditionList.add(condition2);
		}
		if(source != null){
			Predicate condition3 = criteriaBuilder.equal(queryroot.<MastersValue>get("pccSource").<Long>get("key"),source);
			conditionList.add(condition3);
		}
		if(pccCatagory != null){
			Predicate condition4 = criteriaBuilder.equal(queryroot.<PCCRequest>get("pccRequest").<PCCCategory>get("pccCategory").<Long>get("key"), pccCatagory);
			conditionList.add(condition4);
		}

		List<Long> statusListKey = new ArrayList<Long>();
		statusListKey.add(SHAConstants.PCC_REVIEWER_QUERY_REPLIED_STATUS);
		statusListKey.add(SHAConstants.PCC_DIVISIONHEAD_QUERY_REPLIED_STATUS);

		Predicate statusCondition =  queryroot.<Status>get("status").get("key").in(statusListKey); 
		conditionList.add(statusCondition);

		Predicate roleCondition = criteriaBuilder.equal(queryroot.<String>get("roleAssignedBy"),SHAConstants.PCC_PROCESSOR_ROLE); 
		conditionList.add(roleCondition);

		Predicate userCondition = criteriaBuilder.equal(queryroot.<String>get("userAssignedBy"),userName);
		conditionList.add(userCondition);

		criteriaQuery.select(queryroot).where(criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
		final TypedQuery<PCCQuery> typedQuery = entityManager.createQuery(criteriaQuery);	
		doList = typedQuery.getResultList();

		if(doList !=null && !doList.isEmpty()){
			for(PCCQuery pccQuery:doList){
				if(pccQuery.getPccRequest() !=null && pccQuery.getPccRequest().getKey() !=null &&
						!pccRequestsKeys.contains(pccQuery.getPccRequest().getKey())){
					pccRequestsKeys.add(pccQuery.getPccRequest().getKey());
				}
			}
		}
		return pccRequestsKeys;
	}

	private List<Long> getqueryKeys(String intimationNo,Long cpuCode,Long source,Long pccCatagory,String userName){

		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		List<Long> pccRequestsKeys = new ArrayList<Long>();
		List<PCCQuery> doList = new ArrayList<PCCQuery>();
		final CriteriaQuery<PCCQuery> criteriaQuery = criteriaBuilder.createQuery(PCCQuery.class);
		Root<PCCQuery> queryroot = criteriaQuery.from(PCCQuery.class);

		List<Predicate> conditionList = new ArrayList<Predicate>();
		if(intimationNo != null){
			Predicate condition1 = criteriaBuilder.equal(queryroot.<PCCRequest>get("pccRequest").<String>get("intimationNo"),intimationNo);
			conditionList.add(condition1);
		}
		if(cpuCode != null){
			Predicate condition2 = criteriaBuilder.equal(queryroot.<PCCRequest>get("pccRequest").<Intimation>get("intimation").<TmpCPUCode>get("cpuCode").<Long>get("key"), cpuCode);
			conditionList.add(condition2);
		}
		if(source != null){
			Predicate condition3 = criteriaBuilder.equal(queryroot.<MastersValue>get("pccSource").<Long>get("key"),source);
			conditionList.add(condition3);
		}
		if(pccCatagory != null){
			Predicate condition4 = criteriaBuilder.equal(queryroot.<PCCRequest>get("pccRequest").<PCCCategory>get("pccCategory").<Long>get("key"), pccCatagory);
			conditionList.add(condition4);
		}

		List<Long> statusListKey = new ArrayList<Long>();
		statusListKey.add(SHAConstants.PCC_PROCESSOR_QUERY_INITIATED_STATUS);

		Predicate statusCondition =  queryroot.<Status>get("status").get("key").in(statusListKey); 
		conditionList.add(statusCondition);

		Predicate roleCondition = criteriaBuilder.equal(queryroot.<String>get("roleAssigned"),SHAConstants.PCC_PROCESSOR_ROLE); 
		conditionList.add(roleCondition);

		Predicate userCondition = criteriaBuilder.equal(queryroot.<String>get("userAssigned"),userName);
		conditionList.add(userCondition);

		criteriaQuery.select(queryroot).where(criteriaBuilder.and(conditionList.toArray(new Predicate[] {})));
		final TypedQuery<PCCQuery> typedQuery = entityManager.createQuery(criteriaQuery);	
		doList = typedQuery.getResultList();

		if(doList !=null && !doList.isEmpty()){
			for(PCCQuery pccQuery:doList){
				if(pccQuery.getPccRequest() !=null && pccQuery.getPccRequest().getKey() !=null &&
						!pccRequestsKeys.contains(pccQuery.getPccRequest().getKey())){
					pccRequestsKeys.add(pccQuery.getPccRequest().getKey());
				}
			}
		}
		return pccRequestsKeys;
	}

	private List<Long> getProcesserIntiatedKeys(String intimationNo,Long cpuCode,Long source,Long pccCatagory){

		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		List<Long> pccRequestsKeys = new ArrayList<Long>();
		List<PCCRequest> doList = new ArrayList<PCCRequest>();
		final CriteriaQuery<PCCRequest> criteriaQuery = criteriaBuilder.createQuery(PCCRequest.class);
		Root<PCCRequest> root = criteriaQuery.from(PCCRequest.class);
		List<Long> statusListKey= new ArrayList<Long>();
		statusListKey.add(SHAConstants.PCC_PROCESSOR_INITIATED_STATUS);
		statusListKey.add(SHAConstants.PCC_ZMH_NEGOTIATION_DISAPPROVED_STATUS);

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
		doList = typedQuery.getResultList();
		if(doList !=null && !doList.isEmpty()){
			for(PCCRequest pccRequest:doList){
				if(pccRequest.getKey() !=null &&
						!pccRequestsKeys.contains(pccRequest.getKey())){
					pccRequestsKeys.add(pccRequest.getKey());
				}
			}
		}
		return pccRequestsKeys;
	}
}
