package com.shaic.claim.pcc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.fields.dto.AbstractDAO;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.arch.table.Pageable;
import com.shaic.claim.OMPProcessOmpClaimProcessor.search.OMPProcessOmpClaimProcessorMapper;
import com.shaic.claim.OMPProcessOmpClaimProcessor.search.OMPProcessOmpClaimProcessorTableDTO;
import com.shaic.claim.pcc.beans.MasterPCCRole;
import com.shaic.claim.pcc.beans.PCCCategory;
import com.shaic.claim.pcc.beans.PCCQuery;
import com.shaic.claim.pcc.beans.PCCRequest;
import com.shaic.claim.pcc.beans.PCCStageInformation;
import com.shaic.claim.pcc.beans.PCCSubCategory;
import com.shaic.claim.pcc.dto.PCCApproveDetailsTableDTO;
import com.shaic.claim.pcc.dto.PCCQueryDetailsTableDTO;
import com.shaic.claim.pcc.dto.PCCReplyDetailsTableDTO;
import com.shaic.claim.pcc.dto.PccDTO;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestFormDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;
import com.shaic.claim.pcc.dto.ViewPCCRemarksDTO;
import com.shaic.claim.pcc.dto.ViewPCCTrailsDTO;
import com.shaic.claim.pcc.dto.ZonalMedicalDetailsTableDTO;
import com.shaic.claim.reimbursement.rawanalysis.SearchProcessRawRequestMapper;
import com.shaic.claim.reimbursement.rawanalysis.SearchProcessRawRequestTableDto;
import com.shaic.domain.Claim;
import com.shaic.domain.Hospitals;
import com.shaic.domain.Intimation;
import com.shaic.domain.MasUser;
import com.shaic.domain.MastersValue;
import com.shaic.domain.OMPClaim;
import com.shaic.domain.OMPIntimation;
import com.shaic.domain.Policy;
import com.shaic.domain.PreauthService;
import com.shaic.domain.RawInvsDetails;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Status;
import com.shaic.domain.TmpCPUCode;
import com.shaic.domain.TmpEmployee;
import com.shaic.domain.preauth.Preauth;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.vaadin.v7.data.util.BeanItemContainer;

@Stateless
public class SearchProcessPCCRequestService {

	@PersistenceContext
	protected EntityManager entityManager;
	
	Map<Long, Object> workFlowMap= null;

	@EJB
	private PreauthService preauthService;
	
	SearchProcessPCCRequestMapper pccRequestMapper = SearchProcessPCCRequestMapper.getInstance();

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
			statusListKey.add(SHAConstants.PCC_COORDINATOR_INITIATED_STATUS);
			statusListKey.add(SHAConstants.PCC_HRMC_NEGOTIATION_APPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_ZC_NEGOTIATION_APPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_ZMH_NEGOTIATION_APPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_PROCESSOR_DISAPPROVED_STATUS);
			statusListKey.add(SHAConstants.PCC_PROCESSOR_QUERY_REPLIED_STATUS);
			statusListKey.add(SHAConstants.PCC_REVIEWER_QUERY_REPLIED_STATUS);
			statusListKey.add(SHAConstants.PCC_DIVISIONHEAD_QUERY_REPLIED_STATUS);

			int pageNumber = searchFormDTO.getPageable().getPageNumber();
			int firtResult;
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

	public List<PCCStageInformation> getPCCtrailsByInitmationNo(String intimationNumber) {

		Query query = entityManager.createNamedQuery("PCCStageInformation.findEmpIdsByIntimationNo");
		query.setParameter("intimationId", intimationNumber);
		List<PCCStageInformation> resultList = (List<PCCStageInformation>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;

	}
	
	public List<PCCStageInformation> getPCCDetailsByInitmationNo(String intimationNumber,List<Long> statusIds) {

		Query query = entityManager.createNamedQuery("PCCStageInformation.findByStatusIds");
		query.setParameter("intimationId", intimationNumber);
		query.setParameter("statusList", statusIds);
		List<PCCStageInformation> resultList = (List<PCCStageInformation>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;

	}

	public List<PCCQuery> getPCCQueryByRole(Long pccRequestKey,String role,String userName,List<Long> statuskeyList) {

		Query query = entityManager.createNamedQuery("PCCQuery.findQuerysByPCCRole");
		query.setParameter("pccrequestKey", pccRequestKey);
		query.setParameter("roleAssignedBy", role);
		query.setParameter("roleAssigned", role);
		query.setParameter("userAssignedBy", userName);
		query.setParameter("userAssigned", userName);
		query.setParameter("statusList", statuskeyList);
		List<PCCQuery> resultList = (List<PCCQuery>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;

	}
	
	public List<MasterPCCRole> getPCCRolesByCodeList(List<String> codeList) {
		
		Query query = entityManager.createNamedQuery("MasterPCCRole.findByCodeList");
		query.setParameter("pccRoleCodes", codeList);
		List<MasterPCCRole> resultList = (List<MasterPCCRole>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;
	}

	public MasterPCCRole getPCCRolesByCode(String roleCode) {

		Query query = entityManager.createNamedQuery("MasterPCCRole.findByCode");
		query.setParameter("pccRoleCode", roleCode);
		List<MasterPCCRole> resultList = (List<MasterPCCRole>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			entityManager.refresh(resultList.get(0));
			return resultList.get(0);
		}
		return null;

	}
	
	public List<TmpEmployee> getTmpEmployeeByRole(String roleCode) {

		Query query = entityManager.createNamedQuery("TmpEmployee.findPCCRole");
		query.setParameter("pccRole", roleCode);
		List<TmpEmployee> resultList = (List<TmpEmployee>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;
	}
	
	public TmpEmployee getEmployeeByID(String empId) {

		Query query = entityManager.createNamedQuery("TmpEmployee.findByEmpId");
		query.setParameter("empId", empId.toLowerCase());
		List<TmpEmployee> resultList = (List<TmpEmployee>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			entityManager.refresh(resultList.get(0));
			return resultList.get(0);
		}
		return null;
	}
	
	public List<PCCQuery> getPCCQueryByStatus(Long pccRequestKey,List<Long> statuskeyList) {

		Query query = entityManager.createNamedQuery("PCCQuery.findReplayByStatus");
		query.setParameter("pccrequestKey", pccRequestKey);
		query.setParameter("statusList", statuskeyList);
		List<PCCQuery> resultList = (List<PCCQuery>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;

	}
	
	public List<PCCQuery> getPCCQueryByKey(Long pccRequestKey) {

		Query query = entityManager.createNamedQuery("PCCQuery.findPCCQueryByPCCkey");
		query.setParameter("pccrequestKey", pccRequestKey);
		List<PCCQuery> resultList = (List<PCCQuery>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;

	}
	
	public PCCRequest getPCCDetailsByInitmationNo(String intimationNumber) {

		Query query = entityManager.createNamedQuery("PCCRequest.findByintimationNo");
		query.setParameter("intimationNo", intimationNumber);
		List<PCCRequest> resultList = (List<PCCRequest>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			entityManager.refresh(resultList.get(0));
			return resultList.get(0);
		}
		return null;

	}
	
	public PCCQuery getDirectPCCQuery(Long pccrequestKey,Long statusKey,Long sourceKey) {
		Query findByKey = entityManager.createNamedQuery("PCCQuery.findPCCQueryBySource");
		findByKey.setParameter("pccrequestKey", pccrequestKey);
		findByKey.setParameter("statusKey", statusKey);
		findByKey.setParameter("sourceKey", sourceKey);
		List<PCCQuery> pccList = findByKey.getResultList();
		if(null != pccList && !pccList.isEmpty()){
			entityManager.refresh(pccList.get(0));
			return pccList.get(0);
		}
		return null;
	}
	
	public SelectValue getMasRoleSelectValue(String roleCode){
		MasterPCCRole pccRole= getPCCRolesByCode(roleCode);
		if(pccRole !=null){
			SelectValue selectValue = new SelectValue(pccRole.getKey(),pccRole.getPccRoleDesc(),pccRole.getPccRoleCode());			
			return selectValue;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public BeanItemContainer<SelectValue> getPCCRoles(List<String> codeList) {

		Session session = (Session) entityManager.getDelegate();		
		List<SelectValue> selectValuesList = session.createCriteria(MasterPCCRole.class)
				.add(Restrictions.eq("activeStatus", "Y"))
				.add(Restrictions.in("pccRoleCode",codeList))
				.addOrder(Order.asc("pccRoleDesc"))
				.setProjection(Projections.projectionList()
						.add(Projections.property("key"), "id")
						.add(Projections.property("pccRoleDesc"), "value")
						.add(Projections.property("pccRoleCode"), "commonValue"))
						.setResultTransformer(org.hibernate.transform.Transformers.aliasToBean(SelectValue.class)).list();	
		BeanItemContainer<SelectValue> selectValueContainer = new BeanItemContainer<SelectValue>(SelectValue.class);
		selectValueContainer.addAll(selectValuesList);
		System.out.println("---------------- MasterPCCRole" + new Date());
		return selectValueContainer;	
	}
	
	@SuppressWarnings("unchecked")
	public BeanItemContainer<SelectValue> getPCCUserNames(String roleCode) {

		List<TmpEmployee> employees = getTmpEmployeeByEmpIds(getMasUserPCCRole(roleCode));
		if(employees !=null && !employees.isEmpty()){
			List<SelectValue> selectValues = new ArrayList<SelectValue>();
			BeanItemContainer<SelectValue> userNamesContainer = new BeanItemContainer<SelectValue>(SelectValue.class);
			for(TmpEmployee tmpEmployee:employees){
				SelectValue selectValue = new SelectValue();
				selectValue.setId(tmpEmployee.getKey());
				selectValue.setValue(tmpEmployee.getEmpId()+" - "+tmpEmployee.getEmpFirstName());
				selectValue.setCommonValue(tmpEmployee.getEmpId());
				selectValues.add(selectValue);
			}
			userNamesContainer.addAll(selectValues);
			return userNamesContainer;
		}
		return null;
	}
	

	public PccDetailsTableDTO getPccDetailsFromPCCRequest(PCCRequest pccRequest){

		PccDetailsTableDTO detailsTableDTO = pccRequestMapper.getDetailsTableDTO(pccRequest);					
		MasUser masUser = preauthService.getUserName(pccRequest.getCreatedBy());
		detailsTableDTO.setDoctorName(masUser.getUserName()+"/"+masUser.getUserId());
		return detailsTableDTO;
	}
		
	public void submitPCCCOOrdinate(PccDTO pccDTO,String userName){
		
		userName = userName.toUpperCase();
		if(pccDTO.getIsApproved() !=null && pccDTO.getIsApproved()){
			if(pccDTO.getPccKey() !=null){
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.PCC_COORDINATOR_SOURCE);
				PCCRequest pccRequest= getPCCRequestByKey(pccDTO.getPccKey());
				Status status = new Status();
				status.setKey(SHAConstants.PCC_COORDINATOR_APPROVED_STATUS);
				pccRequest.setNegotiatedAmount(pccDTO.getNegotiatioAmount());
				pccRequest.setSavedAmount(pccDTO.getSavedAmount());
				pccRequest.setPccCoordinatorRemarks(pccDTO.getRemarksforApprove());
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
		}else if(pccDTO.getIsQueryRaised() !=null && pccDTO.getIsQueryRaised()){
			if(pccDTO.getPccKey() !=null){
				PCCQuery pccQuery = new PCCQuery();
				Status status = new Status();
				PCCRequest pccRequest= getPCCRequestByKey(pccDTO.getPccKey());
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(SHAConstants.PCC_COORDINATOR_SOURCE);
				SelectValue roleAssigned = pccDTO.getUserRoleAssigned();
				if(roleAssigned !=null && roleAssigned.getCommonValue() !=null){
					if(roleAssigned.getCommonValue().equals(SHAConstants.PCC_PROCESSOR_ROLE)){
						status.setKey(SHAConstants.PCC_PROCESSOR_QUERY_INITIATED_STATUS);
					}else if(roleAssigned.getCommonValue().equals(SHAConstants.PCC_REVIEWER_ROLE)){
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
				pccQuery.setNegotiatedAmount(pccDTO.getNegotiatioAmount());
				pccQuery.setSavedAmount(pccDTO.getSavedAmount());
				pccQuery.setQueryRemarks(pccDTO.getRemarksforQuery());	
				pccQuery.setRoleAssignedBy(SHAConstants.PCC_COORDINATOR_ROLE);
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
				pccRequest.setLockedBy(null);
				pccRequest.setLockFlag("N");
				pccRequest.setLockedDate(null);
				entityManager.merge(pccRequest);
				entityManager.flush();
				entityManager.clear();
			}
		}
	}
	
	public void setPCCQueryAndReplyDetails(PccDetailsTableDTO doctorDeatils,List<PCCQuery> pccQueries,Long intiatedStatus){

		if(pccQueries !=null && !pccQueries.isEmpty()){
			List<PCCQueryDetailsTableDTO> queryDetailsTableDTOs = new ArrayList<PCCQueryDetailsTableDTO>();
			List<PCCReplyDetailsTableDTO> replyDetailsTableDTOs = new ArrayList<PCCReplyDetailsTableDTO>();
				for(PCCQuery pccQuery : pccQueries){
					
					if(pccQuery.getQueryRemarks() !=null && !pccQuery.getQueryRemarks().isEmpty()){
						PCCQueryDetailsTableDTO pccQueryDetailsTableDTO = pccRequestMapper.getQueryDetailsTableDTO(pccQuery);
						TmpEmployee masUser = getEmployeeByID(pccQueryDetailsTableDTO.getQueryRaiseBy());
						pccQueryDetailsTableDTO.setQueryRaiseBy(masUser.getEmpFirstName()+"/"+masUser.getEmpId());
						MasterPCCRole querypccRole = getPCCRolesByCode(pccQueryDetailsTableDTO.getQueryRaiseRole());
						pccQueryDetailsTableDTO.setQueryRaiseRole(querypccRole.getPccRoleDesc());
						queryDetailsTableDTOs.add(pccQueryDetailsTableDTO);
					}					
					if(pccQuery.getQueryReplyRemarks() !=null && !pccQuery.getQueryReplyRemarks().isEmpty()){
						PCCReplyDetailsTableDTO replyDetailsTableDTO = pccRequestMapper.getReplyDetailsTableDTO(pccQuery);
						TmpEmployee replyUser = getEmployeeByID(replyDetailsTableDTO.getReplyGivenBy());
						replyDetailsTableDTO.setReplyGivenBy(replyUser.getEmpFirstName()+"/"+replyUser.getEmpId());
						MasterPCCRole replypccRole = getPCCRolesByCode(replyDetailsTableDTO.getReplyRole());
						replyDetailsTableDTO.setReplyRole(replypccRole.getPccRoleDesc());		
						replyDetailsTableDTOs.add(replyDetailsTableDTO);
					}
					if(pccQuery.getStatus() !=null && pccQuery.getStatus().getKey() !=null && intiatedStatus !=null
							&& intiatedStatus.equals(pccQuery.getStatus().getKey())){
						TmpEmployee masUser = getEmployeeByID(pccQuery.getUserAssignedBy());
						SelectValue responceUser = new SelectValue(masUser.getKey(),masUser.getEmpId()+" - "+masUser.getEmpFirstName(),masUser.getEmpId());	
						SelectValue responceRole = getMasRoleSelectValue(pccQuery.getRoleAssignedBy());
						doctorDeatils.setResponceRole(responceRole);
						doctorDeatils.setResponceUser(responceUser);
						doctorDeatils.setIsResponceRecord(true);
						doctorDeatils.setReponceQueryKey(pccQuery.getKey());
					}
				}
				doctorDeatils.setQueryDetails(queryDetailsTableDTOs);
				doctorDeatils.setReplyDetails(replyDetailsTableDTOs);
		}
	}
	
	public void setPCCZMHQueryAndReplyDetails(PccDetailsTableDTO doctorDeatils,List<PCCQuery> pccQueries){

		if(pccQueries !=null && !pccQueries.isEmpty()){
			List<PCCQueryDetailsTableDTO> queryDetailsTableDTOs = new ArrayList<PCCQueryDetailsTableDTO>();
			List<PCCReplyDetailsTableDTO> replyDetailsTableDTOs = new ArrayList<PCCReplyDetailsTableDTO>();
				for(PCCQuery pccQuery : pccQueries){
					
					PCCQueryDetailsTableDTO pccQueryDetailsTableDTO = new PCCQueryDetailsTableDTO();
					TmpEmployee masUser = getEmployeeByID(pccQuery.getCreatedBy());
					pccQueryDetailsTableDTO.setQueryRaiseBy(masUser.getEmpFirstName()+"/"+masUser.getEmpId());
					MasterPCCRole querypccRole = getPCCRolesByCode(pccQuery.getRoleAssignedBy());
					pccQueryDetailsTableDTO.setQueryRaiseRole(querypccRole.getPccRoleDesc());
					pccQueryDetailsTableDTO.setQueryRaiseDate(pccQuery.getCreatedDate());
					pccQueryDetailsTableDTO.setQueryRemarks(pccQuery.getQueryRemarks());
					queryDetailsTableDTOs.add(pccQueryDetailsTableDTO);
					if(pccQuery.getQueryReplyRemarks() !=null && !pccQuery.getQueryReplyRemarks().isEmpty()){
						PCCReplyDetailsTableDTO replyDetailsTableDTO = new PCCReplyDetailsTableDTO();
						TmpEmployee replyUser = getEmployeeByID(pccQuery.getModifiedBy());
						replyDetailsTableDTO.setReplyGivenBy(replyUser.getEmpFirstName()+"/"+replyUser.getEmpId());
						MasterPCCRole replypccRole = getPCCRolesByCode(pccQuery.getRoleAssigned());
						replyDetailsTableDTO.setReplyRole(replypccRole.getPccRoleDesc());	
						replyDetailsTableDTO.setRepliedDate(pccQuery.getModifiedDate());
						replyDetailsTableDTO.setReplyRemarks(pccQuery.getQueryReplyRemarks());
						replyDetailsTableDTOs.add(replyDetailsTableDTO);
					}
				}
				doctorDeatils.setQueryDetails(queryDetailsTableDTOs);
				doctorDeatils.setReplyDetails(replyDetailsTableDTOs);
		}
	}
	
	public void setPCCZonalDetails(PccDetailsTableDTO doctorDeatils,List<PCCQuery> pccQueries,Long intiatedStatus){

		if(pccQueries !=null && !pccQueries.isEmpty()){
			List<ZonalMedicalDetailsTableDTO> zonalMedicalDetailsTableDTOs = new ArrayList<ZonalMedicalDetailsTableDTO>();
				for(PCCQuery pccQuery : pccQueries){
					
					ZonalMedicalDetailsTableDTO zonalMedicalDetailsTableDTO = new ZonalMedicalDetailsTableDTO();
					TmpEmployee masUser = getEmployeeByID(pccQuery.getCreatedBy());
					zonalMedicalDetailsTableDTO.setMedicalIdAndName(masUser.getEmpFirstName()+"/"+masUser.getEmpId());
					zonalMedicalDetailsTableDTO.setAssignDateAndTime(pccQuery.getCreatedDate());
					zonalMedicalDetailsTableDTO.setRemarks(pccQuery.getQueryRemarks());
					zonalMedicalDetailsTableDTOs.add(zonalMedicalDetailsTableDTO);				
					if(pccQuery.getStatus() !=null && pccQuery.getStatus().getKey() !=null && intiatedStatus !=null
							&& intiatedStatus.equals(pccQuery.getStatus().getKey())){
						doctorDeatils.setReponceQueryKey(pccQuery.getKey());
					}
				}
				doctorDeatils.setZonalDetails(zonalMedicalDetailsTableDTOs);
		}
	}
	
	public ViewPCCRemarksDTO getViewPCCRemarksDTO(String intitmationNo){
		PCCRequest pccRequest= getPCCDetailsByInitmationNo(intitmationNo);
		if(pccRequest !=null){
			ViewPCCRemarksDTO viewPCCRemarksDTO = new ViewPCCRemarksDTO();
			
			viewPCCRemarksDTO.setIntimationId(pccRequest.getIntimationNo());
			TmpEmployee masUser = getEmployeeByID(pccRequest.getCreatedBy());
			viewPCCRemarksDTO.setUserNameAndIdforDoct(masUser.getEmpFirstName()+"/"+masUser.getEmpId());
			viewPCCRemarksDTO.setDateAndTimeforDoct(pccRequest.getCreatedDate());
			viewPCCRemarksDTO.setRemarksTypeforDoct(pccRequest.getPccCategory().getPccDesc());
			viewPCCRemarksDTO.setRemarksforDoct(pccRequest.getPccDoctorRemarks());
			viewPCCRemarksDTO.setUserRoleforDoct("Processing Doctor");
			
			if(pccRequest.getModifiedBy() !=null && !pccRequest.getModifiedBy().isEmpty()){
				TmpEmployee modifyUser = getEmployeeByID(pccRequest.getModifiedBy());
				viewPCCRemarksDTO.setUserNameAndId(modifyUser.getEmpFirstName()+"/"+modifyUser.getEmpId());
				viewPCCRemarksDTO.setDateAndTime(pccRequest.getModifiedDate());
				viewPCCRemarksDTO.setRemarksType(pccRequest.getPccCategory().getPccDesc());
				if(pccRequest.getPccSource() !=null && pccRequest.getPccSource().getValue() !=null){
					viewPCCRemarksDTO.setUserRole(pccRequest.getPccSource().getValue());
					if(pccRequest.getPccSource().getKey().equals(SHAConstants.PCC_COORDINATOR_SOURCE)){
						viewPCCRemarksDTO.setRemarks(pccRequest.getPccCoordinatorRemarks());
					}else if(pccRequest.getPccSource().getKey().equals(SHAConstants.PCC_PROCESSOR_SOURCE)){
						viewPCCRemarksDTO.setRemarks(pccRequest.getProcessorRemark());
					}else if(pccRequest.getPccSource().getKey().equals(SHAConstants.ZONAL_MEDICAL_HEAD_SOURCE)
							|| pccRequest.getPccSource().getKey().equals(SHAConstants.ZONAL_COORDINATOR_SOURCE)
							|| pccRequest.getPccSource().getKey().equals(SHAConstants.ZONAL_COORDINATOR_SOURCE)){
						viewPCCRemarksDTO.setRemarks(pccRequest.getZonalRemark());
					}
				}	
			}
			List<PCCQuery> pccQueries = getPCCQueryByKey(pccRequest.getKey());
			if(pccQueries !=null && !pccQueries.isEmpty()){
				setViewPCCQueryAndReplyDetails(viewPCCRemarksDTO,pccQueries);
			}	
			viewPCCRemarksDTO.setPccDetails(getViewPCCDetailsDTO(intitmationNo));
			return viewPCCRemarksDTO;
		}
		return null;
	}
	
	public void setViewPCCQueryAndReplyDetails(ViewPCCRemarksDTO viewPCCRemarksDTO,List<PCCQuery> pccQueries){

		if(pccQueries !=null && !pccQueries.isEmpty()){
			List<PCCQueryDetailsTableDTO> queryDetailsTableDTOs = new ArrayList<PCCQueryDetailsTableDTO>();
			List<PCCReplyDetailsTableDTO> replyDetailsTableDTOs = new ArrayList<PCCReplyDetailsTableDTO>();
				for(PCCQuery pccQuery : pccQueries){
					
					PCCQueryDetailsTableDTO pccQueryDetailsTableDTO = new PCCQueryDetailsTableDTO();
					TmpEmployee masUser = getEmployeeByID(pccQuery.getCreatedBy());
					pccQueryDetailsTableDTO.setQueryRaiseBy(masUser.getEmpFirstName()+"/"+masUser.getEmpId());
					MasterPCCRole querypccRole = getPCCRolesByCode(pccQuery.getRoleAssignedBy());
					pccQueryDetailsTableDTO.setQueryRaiseRole(querypccRole.getPccRoleDesc());
					pccQueryDetailsTableDTO.setQueryRaiseDate(pccQuery.getCreatedDate());
					pccQueryDetailsTableDTO.setQueryRemarks(pccQuery.getQueryRemarks());
					queryDetailsTableDTOs.add(pccQueryDetailsTableDTO);
					if(pccQuery.getQueryReplyRemarks() !=null && !pccQuery.getQueryReplyRemarks().isEmpty()){
						PCCReplyDetailsTableDTO replyDetailsTableDTO = new PCCReplyDetailsTableDTO();
						TmpEmployee replyUser = getEmployeeByID(pccQuery.getModifiedBy());
						replyDetailsTableDTO.setReplyGivenBy(replyUser.getEmpFirstName()+"/"+replyUser.getEmpId());
						MasterPCCRole replypccRole = getPCCRolesByCode(pccQuery.getRoleAssigned());
						replyDetailsTableDTO.setReplyRole(replypccRole.getPccRoleDesc());	
						replyDetailsTableDTO.setRepliedDate(pccQuery.getModifiedDate());
						replyDetailsTableDTO.setReplyRemarks(pccQuery.getQueryReplyRemarks());
						replyDetailsTableDTOs.add(replyDetailsTableDTO);
					}
				}
				viewPCCRemarksDTO.setQueryDetails(queryDetailsTableDTOs);
				viewPCCRemarksDTO.setReplyDetails(replyDetailsTableDTOs);
		}
	}
	
	public void lockandUnlockIntimation(Long requestkey,String action,String userName){
		PCCRequest pccRequest= getPCCRequestByKey(requestkey);
		if(pccRequest !=null){
			if(action.equals("LOCK")){
				pccRequest.setLockedBy(userName);
				pccRequest.setLockFlag("Y");
				pccRequest.setLockedDate(new Timestamp(System.currentTimeMillis()));
			}else if(action.equals("UNLOCK")){
				pccRequest.setLockedBy(null);
				pccRequest.setLockFlag("N");
				pccRequest.setLockedDate(null);
			}
		}
	}
	
	public List<ViewPCCTrailsDTO> getViewPCCTrailsDTO(String intitmationNo){
		List<PCCStageInformation> informations = getPCCtrailsByInitmationNo(intitmationNo);
		if(informations !=null && !informations.isEmpty()){
			List<ViewPCCTrailsDTO> viewPCCTrailsDTOs = new ArrayList<ViewPCCTrailsDTO>();
			for(PCCStageInformation pccStageInformation:informations){
				ViewPCCTrailsDTO pccTrailsDTO = new ViewPCCTrailsDTO();
				if(pccStageInformation.getCreatedBy() !=null ){
					TmpEmployee masUser = getEmployeeByID(pccStageInformation.getCreatedBy());
					pccTrailsDTO.setUserName(masUser.getEmpFirstName());
					pccTrailsDTO.setUserID(masUser.getEmpId());
					if(masUser.getPccRole() !=null){
						SelectValue selectValue = getMasRoleSelectValue(masUser.getPccRole());
						pccTrailsDTO.setRaiseRole(selectValue.getValue());
					}
				}
				if(pccStageInformation.getCreatedDate() !=null ){
					pccTrailsDTO.setDateAndTime(pccStageInformation.getCreatedDate());
				}
				pccTrailsDTO.setClaimStage("Post Cashless Caims");
				if(pccStageInformation.getStatus() != null){
					pccTrailsDTO.setStatus(pccStageInformation.getStatus().getProcessValue());
				}
				pccTrailsDTO.setStatusRemark(pccStageInformation.getStatusRemarks());
				if(pccStageInformation.getPccCategory() !=null){
					pccTrailsDTO.setPccRemarksType(pccStageInformation.getPccCategory().getPccDesc());
				}
				viewPCCTrailsDTOs.add(pccTrailsDTO);
			}
			return viewPCCTrailsDTOs;
		}
		return null;
	}
	
	public void setDirectrespoceDetails(PCCQuery pccQuery,PccDetailsTableDTO doctorDeatils){

		TmpEmployee masUser = getEmployeeByID(pccQuery.getUserAssignedBy());
		SelectValue directresponceUser = new SelectValue(masUser.getKey(),masUser.getEmpId()+" - "+masUser.getEmpFirstName(),masUser.getEmpId());	
		doctorDeatils.setDirectresponceUser(directresponceUser);
		doctorDeatils.setIsdirectResponceRecord(true);
		doctorDeatils.setDirectreponceQueryKey(pccQuery.getKey());
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
		statusListKey.add(SHAConstants.PCC_PROCESSOR_QUERY_REPLIED_STATUS);
		statusListKey.add(SHAConstants.PCC_REVIEWER_QUERY_REPLIED_STATUS);
		statusListKey.add(SHAConstants.PCC_DIVISIONHEAD_QUERY_REPLIED_STATUS);

		Predicate statusCondition =  queryroot.<Status>get("status").get("key").in(statusListKey); 
		conditionList.add(statusCondition);

		Predicate roleCondition = criteriaBuilder.equal(queryroot.<String>get("roleAssignedBy"),SHAConstants.PCC_COORDINATOR_ROLE); 
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

	private List<Long> getProcesserIntiatedKeys(String intimationNo,Long cpuCode,Long source,Long pccCatagory){

		final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		List<Long> pccRequestsKeys = new ArrayList<Long>();
		List<PCCRequest> doList = new ArrayList<PCCRequest>();
		final CriteriaQuery<PCCRequest> criteriaQuery = criteriaBuilder.createQuery(PCCRequest.class);
		Root<PCCRequest> root = criteriaQuery.from(PCCRequest.class);
		List<Long> statusListKey= new ArrayList<Long>();
		statusListKey.add(SHAConstants.PCC_COORDINATOR_INITIATED_STATUS);
		statusListKey.add(SHAConstants.PCC_HRMC_NEGOTIATION_APPROVED_STATUS);
		statusListKey.add(SHAConstants.PCC_ZC_NEGOTIATION_APPROVED_STATUS);
		statusListKey.add(SHAConstants.PCC_ZMH_NEGOTIATION_APPROVED_STATUS);
		statusListKey.add(SHAConstants.PCC_PROCESSOR_DISAPPROVED_STATUS);

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
	
	public List<PCCApproveDetailsTableDTO> getViewPCCDetailsDTO(String intitmationNo){

		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(SHAConstants.PCC_COORDINATOR_INITIATED_STATUS);
		statusIds.add(SHAConstants.PCC_PROCESSOR_INITIATED_STATUS);
		statusIds.add(SHAConstants.PCC_PROCESSOR_APPROVED_STATUS);
		statusIds.add(SHAConstants.PCC_PROCESSOR_DISAPPROVED_STATUS);
		statusIds.add(SHAConstants.PCC_ZMH_NEGOTIATION_DISAPPROVED_STATUS);
		statusIds.add(SHAConstants.PCC_ZMH_NEGOTIATION_APPROVED_STATUS);
		statusIds.add(SHAConstants.PCC_ZC_NEGOTIATION_APPROVED_STATUS);
		statusIds.add(SHAConstants.PCC_HRMC_NEGOTIATION_APPROVED_STATUS);

		List<PCCStageInformation> informations = getPCCDetailsByInitmationNo(intitmationNo,statusIds);
		if(informations !=null && !informations.isEmpty()){
			List<PCCApproveDetailsTableDTO> viewPCCTrailsDTOs = new ArrayList<PCCApproveDetailsTableDTO>();
			for(PCCStageInformation pccStageInformation:informations){
				PCCApproveDetailsTableDTO detailsTableDTO = new PCCApproveDetailsTableDTO();
				if(pccStageInformation.getCreatedBy() !=null ){
					TmpEmployee masUser = getEmployeeByID(pccStageInformation.getCreatedBy());
					detailsTableDTO.setRaiseBy(masUser.getEmpFirstName()+"/"+masUser.getEmpId());
					if(masUser.getPccRole() !=null){
						SelectValue selectValue = getMasRoleSelectValue(masUser.getPccRole());
						detailsTableDTO.setRaiseRole(selectValue.getValue());
					}
				}
				if(pccStageInformation.getCreatedDate() !=null ){
					detailsTableDTO.setRaiseDate(pccStageInformation.getCreatedDate());
				}
				if(pccStageInformation.getStatus() != null){
					detailsTableDTO.setStatus(pccStageInformation.getStatus().getProcessValue());
				}
				detailsTableDTO.setRemarks(pccStageInformation.getStatusRemarks());

				viewPCCTrailsDTOs.add(detailsTableDTO);
			}
			return viewPCCTrailsDTOs;
		}
		return null;
	}
	
	public List<String> getMasUserPCCRole(String roleCode) {

		Query query = entityManager.createNamedQuery("MasUserPCCRoleMapping.findUserIDsByRole");
		query.setParameter("roleCode", roleCode);
		List<String> resultList = (List<String>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;
	}
	
	public List<TmpEmployee> getTmpEmployeeByEmpIds(List<String> empIds) {

		if(empIds !=null && !empIds.isEmpty()){
			Query query = entityManager.createNamedQuery("TmpEmployee.findPCCEmpIds");
			query.setParameter("empIds", empIds);
			List<TmpEmployee> resultList = (List<TmpEmployee>) query.getResultList();
			if(resultList != null && !resultList.isEmpty()) {
				return resultList;
			}
		}		
		return null;
	}
}
