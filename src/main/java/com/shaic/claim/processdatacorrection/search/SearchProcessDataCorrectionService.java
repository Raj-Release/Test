package com.shaic.claim.processdatacorrection.search;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.claim.ClaimDto;
import com.shaic.claim.RevisedClaimMapper;
import com.shaic.claim.preauth.wizard.dto.DiagnosisDetailsTableDTO;
import com.shaic.claim.preauth.wizard.dto.ProcedureDTO;
import com.shaic.claim.preauth.wizard.dto.SpecialityDTO;
import com.shaic.claim.premedical.mapper.PreMedicalMapper;
import com.shaic.claim.processtranslation.search.SearchProcessTranslationFormDTO;
import com.shaic.claim.processtranslation.search.SearchProcessTranslationTableDTO;
import com.shaic.claim.rod.wizard.service.AcknowledgementDocumentsReceivedService;
import com.shaic.claim.scoring.HospitalScoringDTO;
import com.shaic.claim.scoring.HospitalScoringService;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimPayment;
import com.shaic.domain.ClaimService;
import com.shaic.domain.DataValidation;
import com.shaic.domain.DocAcknowledgement;
import com.shaic.domain.HospitalCategory;
import com.shaic.domain.HospitalScoring;
import com.shaic.domain.Hospitals;
import com.shaic.domain.Intimation;
import com.shaic.domain.IntimationService;
import com.shaic.domain.MasterService;
import com.shaic.domain.MastersValue;
import com.shaic.domain.PreauthService;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.Reimbursement;
import com.shaic.domain.preauth.Diagnosis;
import com.shaic.domain.preauth.IcdCode;
import com.shaic.domain.preauth.ImplantDetails;
import com.shaic.domain.preauth.PedValidation;
import com.shaic.domain.preauth.Preauth;
import com.shaic.domain.preauth.Procedure;
import com.shaic.domain.preauth.ProcedureMaster;
import com.shaic.domain.preauth.ProcedureSpecialityMaster;
import com.shaic.domain.preauth.ProcedureSpecialityMaster;
import com.shaic.domain.preauth.Speciality;
import com.shaic.domain.preauth.SpecialityType;
import com.shaic.domain.preauth.TreatingDoctorDetails;
import com.shaic.domain.service.PreMedicalService;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.shaic.ims.bpm.claim.IntimationDto;
import com.shaic.newcode.wizard.domain.NewIntimationMapper;
import com.shaic.newcode.wizard.dto.NewIntimationDto;
import com.vaadin.server.VaadinSession;
import com.vaadin.v7.data.util.BeanItemContainer;

@Stateless
public class SearchProcessDataCorrectionService {

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	private DBCalculationService dbCalculationService;

	@EJB
	private HospitalScoringService hospitalScoringService;

	@EJB
	private MasterService masterService;

	private final Integer SCORING_VERSION = 5;

	@EJB
	private PreauthService preauthService;
	
	@EJB
    private AcknowledgementDocumentsReceivedService reimbursementService;
	
	@EJB
    private ClaimService claimService;
	
	@EJB
	private IntimationService intimationService;
	
	private final Integer OLD_SCORING_VERSION = 4;

	private final Logger log = LoggerFactory.getLogger(SearchProcessDataCorrectionService.class);

	public SearchProcessDataCorrectionService() {
		super();
	}


	public List<HospitalScoringDTO> populateScoringCategory(Long intimationKey,boolean isoldscoringview,String networkType){
		List<HospitalScoringDTO> categoryList = new ArrayList<HospitalScoringDTO>();
		categoryList = getScoringCategories(networkType);
		assignDataForCategory(categoryList, intimationKey,isoldscoringview);
		return categoryList;
	}

	@SuppressWarnings({ "unchecked" })
	public List<HospitalScoringDTO> getScoringCategories(String networkTypeID){

		List<HospitalScoringDTO> scoringDataList = new ArrayList<HospitalScoringDTO>();
		List<String> distinctCategory = new ArrayList<String>();

		Query catquery = entityManager.createNamedQuery("HospitalCategory.findDistinctCategory");
		catquery = catquery.setParameter("networkTypeID", networkTypeID);
		List<HospitalCategory> catlist  = catquery.getResultList();
		for(HospitalCategory catRec : catlist){
			if(!distinctCategory.contains(catRec.getCategoryDesc())){
				distinctCategory.add(catRec.getCategoryDesc());
			}	
		}

		int category_Id = 7;
		int sub_category_Id = 0;
		for(int j = 0; j < distinctCategory.size(); j++){
			String catName = distinctCategory.get(j).trim();
			Query subCatquery = entityManager.createNamedQuery("HospitalCategory.findByCategory");
			subCatquery = subCatquery.setParameter("categoryDesc", catName);
			subCatquery = subCatquery.setParameter("networkTypeID", networkTypeID);
			List<HospitalCategory> tempQueryList  = subCatquery.getResultList();

			HospitalScoringDTO categoryRec = new HospitalScoringDTO();
			categoryRec.setScoringName(catName);
			categoryRec.setScoringValue(null);
			categoryRec.setScoringBooleanValue(null);
			//R1292
			categoryRec.setActualCategoryId(category_Id);
			categoryRec.setActualSubCategoryId(null);
			if(categoryRec.getActualCategoryId() == 8){
				categoryRec.setScoringValue("N");
				categoryRec.setScoringBooleanValue(false);
				categoryRec.setOptionVisible(false);
			}else{
				categoryRec.setOptionVisible(true);
			}
			categoryRec.setSubCategoryKey(Long.parseLong((String.valueOf(category_Id))));
			categoryRec.setTextFieldStyleName("tfwbBold");
			categoryRec.setComponentId(category_Id+"_"+sub_category_Id);
			categoryRec.setOptionEnabled(true);
			scoringDataList.add(categoryRec);			
			sub_category_Id++;
			for (HospitalCategory rec : tempQueryList) {
				HospitalScoringDTO subCategoryRec = new HospitalScoringDTO();
				subCategoryRec.setScoringName(rec.getSubCategoryDesc().trim());
				subCategoryRec.setScoringValue(null);
				subCategoryRec.setScoringBooleanValue(null);
				subCategoryRec.setOptionVisible(true);
				subCategoryRec.setSubCategoryKey(rec.getSubCategoryKey());
				subCategoryRec.setTextFieldStyleName("tfwb");
				subCategoryRec.setComponentId(category_Id+"_"+sub_category_Id);
				subCategoryRec.setActualCategoryId(rec.getCategoryKey().intValue());
				subCategoryRec.setActualSubCategoryId(rec.getSubCategoryKey().intValue());

				List<Integer> NWEnabledList =  new ArrayList<Integer>();
				NWEnabledList.add(1004); //("ANH Package – For ANH");
				NWEnabledList.add(1006); //("Index Price/SOC/Market Rate  - For NANH");

				List<Integer> R3SCatList =  new ArrayList<Integer>();
				R3SCatList.add(1007);
				R3SCatList.add(1023);
				R3SCatList.add(1010);

				if(rec.getSubCategoryKey().intValue() == 1006){
					subCategoryRec.setOptionEnabled(false);
				}else if(!NWEnabledList.contains(rec.getSubCategoryKey())){
					if(R3SCatList.contains(rec.getSubCategoryKey().intValue())){
						subCategoryRec.setOptionEnabled(false); // disabling all 3rd hdr sub-categories radio-box
					}else{
						subCategoryRec.setOptionEnabled(true); // enabling all other sub-categories radio-box
					}
				}

				scoringDataList.add(subCategoryRec);	
				sub_category_Id++;
			}
			category_Id++;
			sub_category_Id = 0;
		}
		return scoringDataList;
	}

	@SuppressWarnings("unchecked")
	public void assignDataForCategory(List<HospitalScoringDTO> argList, Long intimationKey,boolean isoldscoringview){

		Query scoringquery = entityManager.createNamedQuery("HospitalScoring.findByScoringVersionIntimationKey");
		scoringquery = scoringquery.setParameter("intimationKey", intimationKey);
		scoringquery = scoringquery.setParameter("scoringVersion", SCORING_VERSION);
		List<HospitalScoring> catScorlist  = scoringquery.getResultList();

		if (!catScorlist.isEmpty()) {
			Map<String, Boolean> categoryFlag = new HashMap<String, Boolean>();
			Map<String, HospitalScoringDTO> categoryRec = new HashMap<String, HospitalScoringDTO>();

			for(HospitalScoringDTO dtoRec : argList){
				if(dtoRec.getSubCategoryKey() != null){
					HospitalScoring scoRec = hospitalScoringService.getHospitalScoringBySkey(intimationKey, dtoRec.getSubCategoryKey());
					if(scoRec != null) {
						if(dtoRec.getSubCategoryKey().intValue() == scoRec.getSubCategoryKey().intValue()){
							if(isoldscoringview
									&& scoRec.getOldGradeScore() !=null){
								dtoRec.setKey(scoRec.getKey());
								dtoRec.setScoringValue(scoRec.getOldGradeScore());
								if(scoRec.getOldGradeScore() == null){ // This condition is not possible...... So this is not throwing error.....
									dtoRec.setScoringBooleanValue(null);
									categoryFlag.put(dtoRec.getComponentId(), null);
								}else if(scoRec.getOldGradeScore().equals("Y")){
									dtoRec.setScoringBooleanValue(true);
									categoryFlag.put(dtoRec.getComponentId(), true);
								}else{
									dtoRec.setScoringBooleanValue(false);
									categoryFlag.put(dtoRec.getComponentId(), false);
								}
							}else{
								dtoRec.setKey(scoRec.getKey());
								dtoRec.setScoringValue(scoRec.getGradeScore());
								if(scoRec.getGradeScore() == null){ // This condition is not possible...... So this is not throwing error.....
									dtoRec.setScoringBooleanValue(null);
									categoryFlag.put(dtoRec.getComponentId(), null);
								}else if(scoRec.getGradeScore().equals("Y")){
									dtoRec.setScoringBooleanValue(true);
									categoryFlag.put(dtoRec.getComponentId(), true);
								}else{
									dtoRec.setScoringBooleanValue(false);
									categoryFlag.put(dtoRec.getComponentId(), false);
								}
							}

							categoryRec.put(dtoRec.getComponentId(), dtoRec);
						}
					} else {
						dtoRec.setScoringBooleanValue(null);
						categoryRec.put(dtoRec.getComponentId(), dtoRec);
					}
				}
			}
			if(categoryFlag.containsKey("7_0")){
				if(categoryFlag.get("7_0")){
					((HospitalScoringDTO)categoryRec.get("7_1")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("7_2")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("7_3")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("7_4")).setOptionEnabled(true);

					((HospitalScoringDTO)categoryRec.get("8_0")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("8_1")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("8_2")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("8_3")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("8_4")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("8_5")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("8_6")).setOptionEnabled(false);
				}else{
					((HospitalScoringDTO)categoryRec.get("7_1")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("7_2")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("7_3")).setOptionEnabled(false);
					((HospitalScoringDTO)categoryRec.get("7_4")).setOptionEnabled(false);

					((HospitalScoringDTO)categoryRec.get("8_0")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("8_1")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("8_2")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("8_3")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("8_4")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("8_5")).setOptionEnabled(true);
					((HospitalScoringDTO)categoryRec.get("8_6")).setOptionEnabled(true);
				}
			}
		}
	}

	public HospitalScoring getHospitalScoringBySkey(Long key) {
		Query findByKey = entityManager.createNamedQuery("HospitalScoring.findScoringByKey").setParameter("primaryKey", key);
		List<HospitalScoring> hospitalScorings = (List<HospitalScoring>) findByKey.getResultList();
		if (hospitalScorings !=null && !hospitalScorings.isEmpty()) {
			entityManager.refresh(hospitalScorings.get(0));
			return hospitalScorings.get(0);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public Intimation getIntimationByNo(Long intimationkey) {
		Query findByKey = entityManager.createNamedQuery("Intimation.findByKey").setParameter("intiationKey", intimationkey);
		List<Intimation> intimationList = (List<Intimation>) findByKey.getResultList();
		if (!intimationList.isEmpty()) {
			entityManager.refresh(intimationList.get(0));
			return intimationList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Claim getClaimByKey(Long claimkey) {
		Query findByKey = entityManager.createNamedQuery("Claim.findByKey").setParameter("primaryKey", claimkey);
		List<Claim> claims = (List<Claim>) findByKey.getResultList();
		if (!claims.isEmpty()) {
			entityManager.refresh(claims.get(0));
			return claims.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Claim getClaimByIntimationNo(String intimationNo) {
		Query findByKey = entityManager.createNamedQuery("Claim.findByIntimationNo").setParameter("intimationNumber", intimationNo);
		List<Claim> claims = (List<Claim>) findByKey.getResultList();
		if (!claims.isEmpty()) {
			entityManager.refresh(claims.get(0));
			return claims.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public DocAcknowledgement getAckByClaimKey(Long claimKey) {
		Query findByKey = entityManager.createNamedQuery("DocAcknowledgement.findhspAckByClaim").setParameter("claimkey", claimKey);
		List<DocAcknowledgement> acknowledgements = (List<DocAcknowledgement>) findByKey.getResultList();
		if (!acknowledgements.isEmpty()) {
			entityManager.refresh(acknowledgements.get(0));
			return acknowledgements.get(0);
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public List<PedValidation> findPedByintiKey(Long transactionKey) {

		Query query = entityManager.createNamedQuery("PedValidation.findByTransactionKey");
		query.setParameter("transactionKey", transactionKey);
		List<PedValidation> resultList = (List<PedValidation>) query.getResultList();
		if(resultList!=null && !resultList.isEmpty()){
			return resultList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public PedValidation findPedByKey(Long key) {

		Query query = entityManager.createNamedQuery("PedValidation.findByKey");
		query.setParameter("primaryKey", key);
		List<PedValidation> resultList = (List<PedValidation>) query.getResultList();
		if(resultList!=null && !resultList.isEmpty()){
			return resultList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Speciality findSpecialityByKey(Long primaryKey) {
		Query query = entityManager.createNamedQuery("Speciality.findByKey");
		query.setParameter("primaryKey", primaryKey);
		List<Speciality> resultList = (List<Speciality>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);		
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Procedure findProcedureByKey(Long key) {
		Query query = entityManager.createNamedQuery("Procedure.findByKey");
		query.setParameter("primaryKey", key);

		List<Procedure> resultList = (List<Procedure>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Reimbursement findRODByKey(Long key) {
		Query rodByKey = entityManager.createNamedQuery("Reimbursement.findByKey").setParameter("primaryKey", key);
		List<Reimbursement> reimbursements = (List<Reimbursement>) rodByKey.getResultList();
		if (reimbursements !=null && !reimbursements.isEmpty()) {
			return reimbursements.get(0);
		}
		return null;
	}

	private SelectValue getIcdcodeValue(SelectValue icdCode){
		Query rodByKey = entityManager.createNamedQuery("IcdCode.findByKey").setParameter("primaryKey", icdCode.getId());
		List<IcdCode> icdCodes = (List<IcdCode>) rodByKey.getResultList();
		if(icdCodes !=null && !icdCodes.isEmpty()){
			icdCode.setValue(icdCodes.get(0).getDescription()+ " - "+icdCodes.get(0).getValue());
			icdCode.setCommonValue(icdCodes.get(0).getDescription());
		}
		return icdCode;
	}
	private SelectValue getDiganosisValue(SelectValue diganosis){
		Query rodByKey = entityManager.createNamedQuery("Diagnosis.findDiagnosisByKey").setParameter("diagnosisKey", diganosis.getId());
		List<Diagnosis> diagnosisdata = (List<Diagnosis>) rodByKey.getResultList();
		if(diagnosisdata !=null && !diagnosisdata.isEmpty()){
			diganosis.setValue(diagnosisdata.get(0).getValue());
		}
		return diganosis;
	}

	@SuppressWarnings("unchecked")
	public DataValidation findDataByKey(String key) {
		Query rodByKey = entityManager.createNamedQuery("DataValidation.findbyIntimationNO").setParameter("intimationNO", key);
		List<DataValidation> reimbursements = (List<DataValidation>) rodByKey.getResultList();
		if (reimbursements !=null && !reimbursements.isEmpty()) {
			return reimbursements.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ProcedureMaster findMasProcByKey(Long key) {
		Query rodByKey = entityManager.createNamedQuery("ProcedureMaster.findByKey").setParameter("primarykey", key);
		List<ProcedureMaster> procedureMasters = (List<ProcedureMaster>) rodByKey.getResultList();
		if (procedureMasters !=null && !procedureMasters.isEmpty()) {
			return procedureMasters.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<TreatingDoctorDetails> findtreatingByClaimKey(Long claimkey) {
		Query rodByKey = entityManager.createNamedQuery("TreatingDoctorDetails.findByClaimKey").setParameter("claimKey", claimkey);
		List<TreatingDoctorDetails> treatingDoctorDetails = (List<TreatingDoctorDetails>) rodByKey.getResultList();
		if (treatingDoctorDetails !=null && !treatingDoctorDetails.isEmpty()) {
			return treatingDoctorDetails;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public TreatingDoctorDetails findtreatingByKey(Long key) {
		Query rodByKey = entityManager.createNamedQuery("TreatingDoctorDetails.findByKey").setParameter("primarykey", key);
		List<TreatingDoctorDetails> treatingDoctorDetails = (List<TreatingDoctorDetails>) rodByKey.getResultList();
		if (treatingDoctorDetails !=null && !treatingDoctorDetails.isEmpty()) {
			return treatingDoctorDetails.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Reimbursement findCorrectionRODByClaimKey(Long claimkey) {
		Query rodByKey = entityManager.createNamedQuery("Reimbursement.findCorrectionRODACKByClaimKey").setParameter("claimKey", claimkey);
		List<Reimbursement> reimbursements = (List<Reimbursement>) rodByKey.getResultList();
		if (reimbursements !=null && !reimbursements.isEmpty()) {
			return reimbursements.get(0);
		}
		return null;
	}

	public List<ImplantDetails> findImplantDetailsByClaimKey(Long claimKey) {
		Query query = entityManager.createNamedQuery("ImplantDetails.findByClaimKey");
		query.setParameter("claimKey", claimKey);
		List<ImplantDetails> resultList = (List<ImplantDetails>) query.getResultList();
		if(resultList != null && !resultList.isEmpty()) {
			return resultList;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ImplantDetails findImplantDetailsByKey(Long key) {
		Query rodByKey = entityManager.createNamedQuery("ImplantDetails.findByKey").setParameter("primarykey", key);
		List<ImplantDetails> resultList = (List<ImplantDetails>) rodByKey.getResultList();
		if (resultList !=null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

	private List<SpecialityCorrectionDTO> getSpecialityCorrection(Long claimkey,PreMedicalMapper preMedicalMapper,boolean isview) {
		List<SpecialityDTO> specialityDTOList = preMedicalMapper.getSpecialityDTOList(preauthService.findSpecialityByClaimKey(claimkey));
		if (specialityDTOList !=null && !specialityDTOList.isEmpty()) {
			List<SpecialityCorrectionDTO> specialityCorrectionDTOs = new ArrayList<SpecialityCorrectionDTO>();	
			for(SpecialityDTO speciality : specialityDTOList){
				if(speciality.getSpecialityType() !=null){
					SpecialityCorrectionDTO correctionDTO = new SpecialityCorrectionDTO();
					if(isview){
						
						if(speciality.getSplFlag() !=null &&
								speciality.getSplFlag().equals("Y")){
							correctionDTO.setSpecialityType(speciality.getOldspecialityType());
							correctionDTO.setProcedure(speciality.getOldProcedure());
							correctionDTO.setRemarks(speciality.getOldRemarks());
							correctionDTO.setActualspecialityType(speciality.getSpecialityType());
							correctionDTO.setActualProcedure(speciality.getProcedure());
							correctionDTO.setActualRemarks(speciality.getRemarks());
						}else{	
							correctionDTO.setSpecialityType(speciality.getSpecialityType());
							correctionDTO.setProcedure(speciality.getProcedure());
							correctionDTO.setRemarks(speciality.getRemarks());
						}

					}else{
						correctionDTO.setKey(speciality.getKey());
						correctionDTO.setSpecialityType(speciality.getSpecialityType());
						correctionDTO.setProcedure(speciality.getProcedure());
						correctionDTO.setRemarks(speciality.getRemarks());
					}

					specialityCorrectionDTOs.add(correctionDTO);
				}	
			}
			return specialityCorrectionDTOs;
		}
		return null;
	}

	private void setRODroomCat(Long rodKey,ProcessDataCorrectionDTO dataCorrectionDTO,boolean isview) {

		Reimbursement reimbursement = findRODByKey(rodKey);
		if(reimbursement !=null){
			if(reimbursement.getReasonForIcdExclusion() != null){
				dataCorrectionDTO.setIcdExclusionReason(reimbursement.getReasonForIcdExclusion());
			}
			if(isview){
				if(reimbursement.getRcFlag() !=null &&
						reimbursement.getRcFlag().equals("Y")){
					if(reimbursement.getRoomCategory() !=null){
						dataCorrectionDTO.setProposedroomCat(reimbursement.getRoomCategory().getValue());
					}if(reimbursement.getOldRoomCategory() !=null){
						dataCorrectionDTO.setRoomCat(reimbursement.getOldRoomCategory().getValue());
					}if(reimbursement.getTreatmentType() !=null){
						dataCorrectionDTO.setTreatment(reimbursement.getTreatmentType().getValue());
					}
				}else{
					if(reimbursement.getRoomCategory() !=null){
						dataCorrectionDTO.setRoomCat(reimbursement.getRoomCategory().getValue());
					}
					if(reimbursement.getTreatmentType() !=null){
						dataCorrectionDTO.setTreatment(reimbursement.getTreatmentType().getValue());
					}
				}

			}else{
				if(reimbursement.getRoomCategory() !=null){
					SelectValue value = new SelectValue();
					value.setId(reimbursement.getRoomCategory().getKey());
					value.setValue(reimbursement.getRoomCategory().getValue());
					dataCorrectionDTO.setRoomCategory(value);
				}
				if(reimbursement.getTreatmentType() !=null){
					SelectValue value = new SelectValue();
					value.setId(reimbursement.getTreatmentType().getKey());
					value.setValue(reimbursement.getTreatmentType().getValue());
					dataCorrectionDTO.setTreatmentType(value);
				}
			}
		}
	}

	private List<DiganosisCorrectionDTO> getDiganosisCorrection(Long transactionKey,PreMedicalMapper preMedicalMapper,boolean isview){

		List<DiagnosisDetailsTableDTO> newPedValidationTableListDto = preMedicalMapper.getNewPedValidationTableListDto(findPedByintiKey(transactionKey));
		if(newPedValidationTableListDto !=null && !newPedValidationTableListDto.isEmpty()){
			int sno =1;
			List<DiganosisCorrectionDTO> diganosisCorrectionDTOs = new ArrayList<DiganosisCorrectionDTO>();		
			for(DiagnosisDetailsTableDTO detailsTableDTO : newPedValidationTableListDto){
				DiganosisCorrectionDTO correctionDTO = new DiganosisCorrectionDTO();		
				if(isview){
					correctionDTO.setSerialNo(sno);
					sno++;
					if(detailsTableDTO.getDiagnosisFlag() !=null &&
							detailsTableDTO.getDiagnosisFlag().equals("Y")){
						correctionDTO.setDiagnosisName(getDiganosisValue(detailsTableDTO.getOldDiagnosisId()));
						correctionDTO.setProposeddiagnosisName(getDiganosisValue(detailsTableDTO.getDiagnosisName()));
					}else{
						correctionDTO.setDiagnosisName(getDiganosisValue(detailsTableDTO.getDiagnosisName()));
					}
					if(detailsTableDTO.getIcdFlag() !=null &&	
							detailsTableDTO.getIcdFlag().equals("Y")){
						correctionDTO.setIcdCode(getIcdcodeValue(detailsTableDTO.getOldIcdCode()));
						correctionDTO.setProposedicdCode(getIcdcodeValue(detailsTableDTO.getIcdCode()));
					}else{
						correctionDTO.setIcdCode(getIcdcodeValue(detailsTableDTO.getIcdCode()));
					}
				}else{
					correctionDTO.setKey(detailsTableDTO.getKey());
					correctionDTO.setDiagnosisName(getDiganosisValue(detailsTableDTO.getDiagnosisName()));
					correctionDTO.setIcdCode(getIcdcodeValue(detailsTableDTO.getIcdCode()));
				}
				
				if(detailsTableDTO.getPrimaryDiagnosisFlag()!=null && detailsTableDTO.getPrimaryDiagnosisFlag().equalsIgnoreCase(SHAConstants.YES_FLAG))
				{
					correctionDTO.setPrimaryDiagnosis(true);
				}
				else
				{
					correctionDTO.setPrimaryDiagnosis(null);
				}
				diganosisCorrectionDTOs.add(correctionDTO);
			}
			return diganosisCorrectionDTOs;
		}
		return null;
	}

	private List<ProcedureCorrectionDTO> getProcedureCorrection(Long transactionKey,PreMedicalMapper preMedicalMapper,boolean isview){
		List<ProcedureDTO> procedureMainDTOList = preMedicalMapper.getProcedureMainDTOList(preauthService
				.findProcedureByPreauthKey(transactionKey));
		if(procedureMainDTOList !=null && !procedureMainDTOList.isEmpty()){
			List<ProcedureCorrectionDTO> procedureCorrectionDTOs = new ArrayList<ProcedureCorrectionDTO>();
			int sno =1;
			for(ProcedureDTO procedureDTO : procedureMainDTOList){
				ProcedureCorrectionDTO procedureCorrectionDTO = new ProcedureCorrectionDTO();
				if(isview){
					procedureCorrectionDTO.setSerialNo(sno);
					sno++;
					if(procedureDTO.getProcedureFlag() !=null &&
							procedureDTO.getProcedureFlag().equals("Y")){
						ProcedureMaster procedureMaster = findMasProcByKey(procedureDTO.getOldprocedureID());
						SelectValue value = new SelectValue();
						if(procedureMaster !=null){
							value.setId(procedureMaster.getKey());
							value.setValue(procedureMaster.getProcedureName());
						}					
						procedureCorrectionDTO.setProcedureCode(value);
						procedureCorrectionDTO.setProcedureName(value);
						procedureCorrectionDTO.setProposedProcedureCode(procedureDTO.getProcedureCode());
						procedureCorrectionDTO.setProposedProcedureName(procedureDTO.getProcedureName());
					}else{
						procedureCorrectionDTO.setProcedureCode(procedureDTO.getProcedureCode());
						procedureCorrectionDTO.setProcedureName(procedureDTO.getProcedureName());
					}	
				}else{
					procedureCorrectionDTO.setKey(procedureDTO.getKey());
					procedureCorrectionDTO.setProcedureCode(procedureDTO.getProcedureCode());
					procedureCorrectionDTO.setProcedureName(procedureDTO.getProcedureName());
				}
				procedureCorrectionDTOs.add(procedureCorrectionDTO);
			}
			return procedureCorrectionDTOs;
		}
		return null;
	}

	public ProcessDataCorrectionDTO getCorrectionDatas(ProcessDataCorrectionDTO dataCorrectionDTO){

		Long claimKey = dataCorrectionDTO.getClaimKey();
		Long intiKey = dataCorrectionDTO.getIntimationKey();
		Long transactionKey = dataCorrectionDTO.getTransactionKey();
		PreMedicalMapper preMedicalMapper = PreMedicalMapper.getInstance();
		if(claimKey !=null){
			RevisedClaimMapper claimMapper = RevisedClaimMapper.getInstance();
			Claim claim = getClaimByKey(claimKey);
			ClaimDto claimDto = claimMapper.getClaimDto(claim);
			if(claim !=null && claim.getIntimation() !=null
					&& claimDto !=null){
				claimDto.setNewIntimationDto(intimationService.getIntimationDto(claim.getIntimation()));
			}
			dataCorrectionDTO.setClaimDto(claimDto);
			
		}
		dataCorrectionDTO.setSpecialityCorrectionDTOs(getSpecialityCorrection(claimKey,preMedicalMapper,false));
		dataCorrectionDTO.setDiganosisCorrectionDTOs(getDiganosisCorrection(transactionKey,preMedicalMapper,false));
		setRODroomCat(transactionKey,dataCorrectionDTO,false);
		if(dataCorrectionDTO.getTreatmentType()!=null && dataCorrectionDTO.getTreatmentType().getValue().equals("Surgical")){
			dataCorrectionDTO.setProcedureCorrectionDTOs(getProcedureCorrection(transactionKey,preMedicalMapper,false));
			dataCorrectionDTO.setImplantCorrectionDTOs(getImplantCorrection(claimKey,transactionKey,false,dataCorrectionDTO));
		}
		dataCorrectionDTO.setTreatingCorrectionDTOs(getTreatingCorrection(claimKey,false));
		
		if(dataCorrectionDTO.getIcdExclusionReason() == null){
			Preauth preauth = preauthService.getLatestPreauthByClaimKey(claimKey);
			if(preauth != null && preauth.getReasonForIcdExclusion() != null){
				dataCorrectionDTO.setIcdExclusionReason(preauth.getReasonForIcdExclusion());
			}
		}
		
		return dataCorrectionDTO;

	}

	private void saveDiganosisCorrectionDTO(List<DiganosisCorrectionDTO> diganosisCorrectionDTOs,String userID){
		if(diganosisCorrectionDTOs !=null && !diganosisCorrectionDTOs.isEmpty()){
			for(DiganosisCorrectionDTO correctionDTO : diganosisCorrectionDTOs){
				if(correctionDTO.getHasChanges()){
					PedValidation pedValidation = findPedByKey(correctionDTO.getKey());
					if(pedValidation !=null ){
						if(correctionDTO.getProposeddiagnosisName() !=null 
								&& !correctionDTO.getDiagnosisName().getId().equals(correctionDTO.getProposeddiagnosisName().getId())){
							pedValidation.setOldDiagnosisId(correctionDTO.getDiagnosisName().getId());
							pedValidation.setDiagnosisId(correctionDTO.getProposeddiagnosisName().getId());
							pedValidation.setDiagnosisFlag("Y");
						}
						if(correctionDTO.getProposedicdCode() !=null 
								&& !correctionDTO.getIcdCode().getId().equals(correctionDTO.getProposedicdCode().getId())){
							pedValidation.setOldIcdCode(correctionDTO.getIcdCode().getId());
							pedValidation.setIcdCodeId(correctionDTO.getProposedicdCode().getId());
							pedValidation.setIcdFlag("Y");
						}
						pedValidation.setModifiedBy(userID);
						pedValidation.setModifiedDate(new Timestamp(System.currentTimeMillis()));

						entityManager.merge(pedValidation);
						entityManager.flush();
						entityManager.clear();
						log.info("------pedValidation------>"+pedValidation+"<------------");
					}	
				}
			}
		}
	}

	private void saveSpecialityCorrectionDTO(List<SpecialityCorrectionDTO> specialityCorrectionDTOs,String userID,Claim claim){

		if(specialityCorrectionDTOs !=null && !specialityCorrectionDTOs.isEmpty()){
			for(SpecialityCorrectionDTO specialityCorrectionDTO:specialityCorrectionDTOs){
				if(specialityCorrectionDTO.getHasChanges()){
					if(specialityCorrectionDTO.getKey() !=null){
						Speciality speciality = findSpecialityByKey(specialityCorrectionDTO.getKey());
						if(speciality !=null){
							SpecialityType specialityType = new SpecialityType();
							specialityType.setKey(specialityCorrectionDTO.getActualspecialityType().getId());
							ProcedureSpecialityMaster proc = new ProcedureSpecialityMaster();
							proc.setKey(specialityCorrectionDTO.getActualProcedure().getId());
							speciality.setOldspecialityType(speciality.getSpecialityType());
							speciality.setOldProcedure(speciality.getProcedure());
							speciality.setOldRemarks(speciality.getRemarks());
							speciality.setSpecialityType(specialityType);
							speciality.setProcedure(proc);
							speciality.setRemarks(specialityCorrectionDTO.getActualRemarks());
							speciality.setSplFlag("Y");
							speciality.setModifiedBy(userID);
							speciality.setModifiedDate(new Timestamp(System.currentTimeMillis()));
							speciality.setActiveStatus(1L);
							entityManager.merge(speciality);
							entityManager.flush();
							entityManager.clear();
							log.info("------speciality------>"+speciality+"<------------");
						}
					}else{
						Speciality speciality = new Speciality();
						SpecialityType specialityType = new SpecialityType();
						ProcedureSpecialityMaster proc = new ProcedureSpecialityMaster();
						specialityType.setKey(specialityCorrectionDTO.getActualspecialityType().getId());
						proc.setKey(specialityCorrectionDTO.getActualProcedure().getId());
						speciality.setClaim(claim);
						speciality.setSpecialityType(specialityType);
						speciality.setProcedure(proc);
						speciality.setSplFlag("Y");
						speciality.setCreatedBy(userID);
						speciality.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						speciality.setActiveStatus(1L);
						entityManager.persist(speciality);
						entityManager.flush();
						entityManager.clear();
						log.info("------speciality------>"+speciality+"<------------");
					}

				}
			}
		}

	}

	private void saveProcedureCorrectionDTO(List<ProcedureCorrectionDTO> procedureCorrectionDTOs,String userID){

		if(procedureCorrectionDTOs !=null && !procedureCorrectionDTOs.isEmpty()){
			for(ProcedureCorrectionDTO correctionDTO:procedureCorrectionDTOs){
				if(correctionDTO.getHasChanges()){
					Procedure procedure = findProcedureByKey(correctionDTO.getKey());
					if(procedure !=null){
						procedure.setOldprocedureID(procedure.getProcedureID());
						if(correctionDTO.getProposedProcedureName() !=null 
								&& !correctionDTO.getProposedProcedureName().getId().equals(correctionDTO.getProcedureName().getId())){
							procedure.setProcedureID(correctionDTO.getProposedProcedureName().getId());
							procedure.setProcedureName(correctionDTO.getProposedProcedureName().getValue());
							if(correctionDTO.getProposedProcedureCode() !=null){
								procedure.setProcedureCode(correctionDTO.getProposedProcedureCode().getValue());

							}
						}		
						procedure.setProcedureFlag("Y");
						procedure.setModifiedBy(userID);
						procedure.setModifiedDate(new Timestamp(System.currentTimeMillis()));
						entityManager.merge(procedure);
						entityManager.flush();
						entityManager.clear();
						log.info("------procedure------>"+procedure+"<------------");
					}
				}
			}
		}

	}

	public void saveDataCorrection(ProcessDataCorrectionDTO dataCorrectionDTO,String userID){
		boolean haschanges =false;
		Claim claim = getClaimByKey(dataCorrectionDTO.getClaimKey());
		Reimbursement reimbursement = findRODByKey(dataCorrectionDTO.getTransactionKey());
		if(dataCorrectionDTO.getProposedroomCategory() !=null &&
				!dataCorrectionDTO.getProposedroomCategory().getId().equals(dataCorrectionDTO.getRoomCategory().getId())){
			haschanges= true;
			if(reimbursement !=null){
				MastersValue mastersValue = new MastersValue();
				mastersValue.setKey(dataCorrectionDTO.getProposedroomCategory().getId());
				reimbursement.setOldRoomCategory(reimbursement.getRoomCategory());
				reimbursement.setRoomCategory(mastersValue);
				reimbursement.setRcFlag("Y");
				reimbursement.setModifiedBy(userID);
				reimbursement.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				reimbursement.setDcFlag("Y");
				entityManager.merge(reimbursement);
				entityManager.flush();
				entityManager.clear();
				log.info("------reimbursement------>"+reimbursement+"<------------");
			}
		}if(dataCorrectionDTO.getIsspecialityChanged()){
			haschanges= true;
			saveSpecialityCorrectionDTO(dataCorrectionDTO.getSpecialityCorrectionDTOs(),userID,claim);
		}if(dataCorrectionDTO.getIsdiganosisChanged()){
			haschanges= true;
			saveDiganosisCorrectionDTO(dataCorrectionDTO.getDiganosisCorrectionDTOs(),userID);
		}if(dataCorrectionDTO.getIsprocedureChanged()){
			haschanges= true;
			saveProcedureCorrectionDTO(dataCorrectionDTO.getProcedureCorrectionDTOs(),userID);
		}if(dataCorrectionDTO.getRemarks() !=null){
			haschanges= true;
		}if(dataCorrectionDTO.getIsScoringChanged()){
			haschanges= true;
			saveScoringChanges(dataCorrectionDTO.getScoringDTOs(),claim.getIntimation().getKey(),userID,claim);
		}if(dataCorrectionDTO.getIstreatingChanged()){
			haschanges= true;
			saveDoctorCorrectionDTO(dataCorrectionDTO.getTreatingCorrectionDTOs(),userID,dataCorrectionDTO.getTransactionKey(),dataCorrectionDTO.getClaimKey());
		}if(dataCorrectionDTO.getIsimplantChanged()){
			haschanges= true;
			saveImplantCorrectionDTO(dataCorrectionDTO.getImplantCorrectionDTOs(), userID,dataCorrectionDTO.getTransactionKey(),dataCorrectionDTO.getClaimKey());
			if(claim !=null && claim.getClaimType() !=null){
	    		if(claim.getClaimType().getKey().equals(ReferenceTable.REIMBURSEMENT_CLAIM_TYPE_KEY)){
	    			reimbursement.setImplantFlag("Y");
	    			entityManager.merge(reimbursement);
					entityManager.flush();
					entityManager.clear();
					log.info("------ImplantReimbursement------>"+reimbursement+"<------------");
	        	}else{
	        		Preauth preauth = preauthService.getLatestPreauthByClaimKey(claim.getKey());
	        		preauth.setImplantFlag("Y");
	    			entityManager.merge(preauth);
					entityManager.flush();
					entityManager.clear();
					log.info("------ImplantPreauth------>"+preauth+"<------------");
	        	}
	    	}
		}else if(!dataCorrectionDTO.getImplantcorrectionApplicable()
				&& dataCorrectionDTO.getDeletedimplantDTOs() !=null){
			for(ImplantCorrectionDTO implantCorrectionDTO:dataCorrectionDTO.getDeletedimplantDTOs()){
				preauthService.deleteImplantDetails(implantCorrectionDTO.getKey(), userID);
				if(claim !=null && claim.getClaimType() !=null){
		    		if(claim.getClaimType().getKey().equals(ReferenceTable.REIMBURSEMENT_CLAIM_TYPE_KEY)){
		    			reimbursement.setImplantFlag("N");
		    			entityManager.merge(reimbursement);
						entityManager.flush();
						entityManager.clear();
						log.info("------ImplantReimbursement------>"+reimbursement+"<------------");
		        	}else{
		        		Preauth preauth = preauthService.getLatestPreauthByClaimKey(claim.getKey());
		        		preauth.setImplantFlag("N");
		    			entityManager.merge(preauth);
						entityManager.flush();
						entityManager.clear();
						log.info("------ImplantPreauth------>"+preauth+"<------------");
		        	}
		    	}
			}
		}
		if(haschanges){
			if(claim !=null){
				claim.setCoadingUser(userID);
				claim.setCoadingDate(new Date());
				claim.setCoadingFlag("Y");
				claim.setDcCoadingFlag("Y");
				claim.setCoadingRemark(dataCorrectionDTO.getRemarks());
				claim.setModifiedDate(new Date());
				entityManager.merge(claim);
				entityManager.flush();
				entityManager.clear();
				log.info("------claim------>"+claim+"<------------");
			}
			if(reimbursement !=null
					&& !dataCorrectionDTO.getIsroomCatChanged()){
				reimbursement.setModifiedBy(userID);
				reimbursement.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				reimbursement.setDcFlag("Y");
				entityManager.merge(reimbursement);
				entityManager.flush();
				entityManager.clear();
				log.info("------Reimbursement------>"+reimbursement+"<------------");
			}
			dbCalculationService.dataCoadingRelease(userID,null,"SUBMIT",dataCorrectionDTO.getCoadingKey());
		}else{
			claim.setCoadingUser(userID);
			claim.setCoadingDate(new Date());
			claim.setCoadingFlag("N");
			claim.setDcCoadingFlag("Y");
			claim.setCoadingRemark(dataCorrectionDTO.getRemarks());
			claim.setModifiedDate(new Date());
			entityManager.merge(claim);
			entityManager.flush();
			entityManager.clear();
			log.info("------claim------>"+claim+"<------------");
			
			if(reimbursement !=null){
				reimbursement.setModifiedBy(userID);
				reimbursement.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				reimbursement.setDcFlag("Y");
				entityManager.merge(reimbursement);
				entityManager.flush();
				entityManager.clear();
				log.info("------Reimbursement------>"+reimbursement+"<------------");
			}
			dbCalculationService.dataCoadingRelease(userID,null,"NOCHANGE",dataCorrectionDTO.getCoadingKey());
		}

	}
	
	public ProcessDataCorrectionDTO getCorrectionViewDatas(String intimationNO){
		Claim claim = getClaimByIntimationNo(intimationNO);
		if(claim.getDcCoadingFlag() !=null 
				&& claim.getDcCoadingFlag().equals("Y")){
			Reimbursement reimbursement = findCorrectionRODByClaimKey(claim.getKey());

			if(reimbursement !=null){
				ProcessDataCorrectionDTO dataCorrectionDTO = new ProcessDataCorrectionDTO();
				Long claimKey = claim.getKey();	
				Long transactionKey = reimbursement.getKey();
				dataCorrectionDTO.setIntimationKey(claim.getIntimation().getKey());
				dataCorrectionDTO.setIntimationNo(intimationNO);
				dataCorrectionDTO.setClaimKey(claimKey);
				dataCorrectionDTO.setTransactionKey(transactionKey);
				dataCorrectionDTO.setRemarks(claim.getCoadingRemark());

				PreMedicalMapper preMedicalMapper = PreMedicalMapper.getInstance();
				dataCorrectionDTO.setSpecialityCorrectionDTOs(getSpecialityCorrection(claimKey,preMedicalMapper,true));
				dataCorrectionDTO.setDiganosisCorrectionDTOs(getDiganosisCorrection(transactionKey,preMedicalMapper,true));
				setRODroomCat(transactionKey,dataCorrectionDTO,true);
				if(dataCorrectionDTO.getTreatment() !=null && dataCorrectionDTO.getTreatment().equals("Surgical")){
					dataCorrectionDTO.setProcedureCorrectionDTOs(getProcedureCorrection(transactionKey,preMedicalMapper,true));
					dataCorrectionDTO.setImplantCorrectionDTOs(getImplantCorrection(claimKey,transactionKey,true,dataCorrectionDTO));
				}
				dataCorrectionDTO.setTreatingCorrectionDTOs(getTreatingCorrection(claimKey,true));
				if(dataCorrectionDTO.getIcdExclusionReason() == null){
					Preauth preauth = preauthService.getLatestPreauthByClaimKey(claimKey);
					if(preauth != null && preauth.getReasonForIcdExclusion() != null){
						dataCorrectionDTO.setIcdExclusionReason(preauth.getReasonForIcdExclusion());
					}
				}
				return dataCorrectionDTO;
			}	
		}
		return null;
	}

	private void saveScoringChanges(List<HospitalScoringDTO> scoringDTOs,Long intimationkey,String loginUserId,Claim claim){
		List<HospitalScoring> scoringList = getHospitalScoringByIntimationkey(intimationkey);
		int sizeOfList = scoringList.size();
		if(sizeOfList > 0){
			boolean isOldVersionData = true;
			if(scoringList.size() ==  scoringDTOs.size()){
				isOldVersionData = false;
				for(HospitalScoring rec1 : scoringList){
					if(rec1.getScoringVersion() != null && rec1.getScoringVersion().equals(OLD_SCORING_VERSION)){
						isOldVersionData =true;
						break;
					}
				}
			}
			List<Long> listOfUpdatingSC =  new ArrayList<Long>();
			if(sizeOfList > 0){
				for(HospitalScoringDTO rec : scoringDTOs){
					HospitalScoring updIntimationScoring = getHospitalScoringBySkey(intimationkey, rec.getSubCategoryKey());
					if(updIntimationScoring != null){
						if((updIntimationScoring.getGradeScore() !=null && rec.getScoringValue()!=null
								&& !updIntimationScoring.getGradeScore().equals(rec.getScoringValue()))
								||(updIntimationScoring.getGradeScore() == null && rec.getScoringValue()!=null)){
							updIntimationScoring.setOldGradeScore(updIntimationScoring.getGradeScore());
						}
						updIntimationScoring.setGradeScore(rec.getScoringValue());
						updIntimationScoring.setModifiedBy(loginUserId.toLowerCase());
						updIntimationScoring.setModifiedDate((new Timestamp(System.currentTimeMillis())));
						updIntimationScoring.setScoringVersion(SCORING_VERSION);
						updIntimationScoring.setDeleteFlag(0);
						if(isOldVersionData){
							listOfUpdatingSC.add(updIntimationScoring.getSubCategoryKey());
						}
						entityManager.merge(updIntimationScoring);
					}else{
						HospitalScoring newIntimationScoring = new HospitalScoring();
						newIntimationScoring.setIntimationKey(claim.getIntimation().getKey());
						newIntimationScoring.setClaimKey(claim.getKey());
						newIntimationScoring.setHospitalKey(claim.getIntimation().getHospital());
						Hospitals hospital = getHospitalDetailsByKey(claim.getIntimation().getHospital());
						if(hospital != null){
							newIntimationScoring.setHospitalCode(hospital.getHospitalCode());
						}else{
							newIntimationScoring.setHospitalCode(null);
						}
						newIntimationScoring.setSubCategoryKey(rec.getSubCategoryKey());
						newIntimationScoring.setGradeScore(rec.getScoringValue());
						newIntimationScoring.setCreatedBy(loginUserId.toLowerCase());
						newIntimationScoring.setCreatedDate((new Timestamp(System.currentTimeMillis())));
						newIntimationScoring.setScoringVersion(SCORING_VERSION);
						newIntimationScoring.setDeleteFlag(0);
						if(isOldVersionData){
							listOfUpdatingSC.add(rec.getSubCategoryKey());
						}
						entityManager.persist(newIntimationScoring);
					}
				}
				System.out.println("isOldVersionData : "+isOldVersionData);
				System.out.println("listOfUpdatingSC : "+listOfUpdatingSC);
				//Marking oldVersion Data as Deleted R1292
				if(isOldVersionData && listOfUpdatingSC.size() > 0){
					for(HospitalScoring rec : scoringList){
						if(!listOfUpdatingSC.contains(rec.getSubCategoryKey())){
							rec.setDeleteFlag(1);
							rec.setModifiedBy(loginUserId.toLowerCase());
							rec.setModifiedDate((new Timestamp(System.currentTimeMillis())));
							rec.setScoringVersion(OLD_SCORING_VERSION);
							entityManager.merge(rec);
						}
					}
				}
			}
		}
	}  

	public boolean iscScoringChangesmade(List<HospitalScoringDTO> scoringDTOs){
		if(scoringDTOs !=null && !scoringDTOs.isEmpty()){
			for(HospitalScoringDTO rec : scoringDTOs){
				HospitalScoring scoring = getHospitalScoringBySkey(rec.getKey());
				if(scoring != null){
					if((scoring.getGradeScore() !=null 
							&& !scoring.getGradeScore().equals(rec.getScoringValue()))
							||(scoring.getGradeScore() == null && rec.getScoringValue()!=null)){
						return true;
					}
				}else {
					return true;
				}
			}
		}
		return false;			
	}

	private List<TreatingCorrectionDTO> getTreatingCorrection(Long claimKey,boolean isview){
		List<TreatingDoctorDetails> treatingDoctorDetails = findtreatingByClaimKey(claimKey);
		if (treatingDoctorDetails !=null && !treatingDoctorDetails.isEmpty()) {
			List<TreatingCorrectionDTO> treatingCorrectionDTOs = new ArrayList<TreatingCorrectionDTO>();	
			int sno=1;
			for(TreatingDoctorDetails doctorDetails : treatingDoctorDetails){
				TreatingCorrectionDTO correctionDTO = new TreatingCorrectionDTO();
				if(isview){
					correctionDTO.setSerialNo(sno);
					sno++;
					if(doctorDetails.getDcDoctorFlag() !=null 
							&& doctorDetails.getDcDoctorFlag().equals("Y")
							&& doctorDetails.getOldDoctorName() !=null){
						correctionDTO.setTreatingDoctorName(doctorDetails.getOldDoctorName());
						correctionDTO.setQualification(doctorDetails.getOldQualification());
						correctionDTO.setActualtreatingDoctorName(doctorDetails.getDoctorName());
						correctionDTO.setActualqualification(doctorDetails.getDoctorQualification());
					}else if(doctorDetails.getDcDoctorFlag() !=null 
							&& doctorDetails.getDcDoctorFlag().equals("Y")
							&& doctorDetails.getOldDoctorName() == null){
						correctionDTO.setActualtreatingDoctorName(doctorDetails.getDoctorName());
						correctionDTO.setActualqualification(doctorDetails.getDoctorQualification());
					}else{
						correctionDTO.setTreatingDoctorName(doctorDetails.getDoctorName());
						correctionDTO.setQualification(doctorDetails.getDoctorQualification());
					}
					
				}else{
					correctionDTO.setKey(doctorDetails.getKey());
					correctionDTO.setTreatingDoctorName(doctorDetails.getDoctorName());
					correctionDTO.setQualification(doctorDetails.getDoctorQualification());
				}
				treatingCorrectionDTOs.add(correctionDTO);		
			}
			return treatingCorrectionDTOs;
		}
		return null;
	}

	private void saveDoctorCorrectionDTO(List<TreatingCorrectionDTO> doctorDetails,String userID,Long rodKey,Long claimKey){

		if(doctorDetails !=null && !doctorDetails.isEmpty()){
			for(TreatingCorrectionDTO treatingDoctorDetails:doctorDetails){
				if(treatingDoctorDetails.getHasChanges()){
					if(treatingDoctorDetails.getKey() !=null){
						TreatingDoctorDetails details = findtreatingByKey(treatingDoctorDetails.getKey());
						if(details !=null){
							details.setOldDoctorName(details.getDoctorName());
							details.setOldQualification(details.getDoctorQualification());
							details.setDoctorName(treatingDoctorDetails.getActualtreatingDoctorName());
							details.setDoctorQualification(treatingDoctorDetails.getActualqualification());
							details.setDcDoctorFlag("Y");
							entityManager.merge(details);
							entityManager.flush();
							entityManager.clear();
							log.info("------TreatingDoctorDetails------>"+details+"<------------");
						}
					}else{
						TreatingDoctorDetails details = new TreatingDoctorDetails();
						details.setClaimKey(claimKey);
						details.setTransactionKey(rodKey);
						details.setCreatedBy(userID);
						details.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						details.setActiveStatus(1L);	
						details.setDoctorName(treatingDoctorDetails.getActualtreatingDoctorName());
						details.setDoctorQualification(treatingDoctorDetails.getActualqualification());
						details.setDcDoctorFlag("Y");
						entityManager.persist(details);
						entityManager.flush();
						entityManager.clear();
						log.info("------TreatingDoctorDetails------>"+details+"<------------");
					}

				}
			}
		}

	}
	
	private List<ImplantCorrectionDTO> getImplantCorrection(Long claimKey,Long rodKey,boolean isview,ProcessDataCorrectionDTO dataCorrectionDTO){
		
		Boolean implantApplicable =false;
		Claim claim = claimService.getClaimByKey(claimKey);
    	if(claim !=null && claim.getClaimType() !=null){
    		if(claim.getClaimType().getKey().equals(ReferenceTable.REIMBURSEMENT_CLAIM_TYPE_KEY)){
    			Reimbursement reimbursement = reimbursementService.getReimbursementByKey(rodKey);
        		if(reimbursement !=null && reimbursement.getImplantFlag() !=null 
        				&& reimbursement.getImplantFlag().equals("Y")){
        			implantApplicable = true;
        		}
        	}else{
        		Preauth preauth = preauthService.getLatestPreauthByClaimKey(claimKey);
        		if(preauth !=null && preauth.getImplantFlag() !=null 
        				&& preauth.getImplantFlag().equals("Y")){
        			implantApplicable = true;
        		}
        	}
    	}

		dataCorrectionDTO.setImplantApplicable(implantApplicable);
		if(implantApplicable){
			List<ImplantDetails> implantDetails = findImplantDetailsByClaimKey(claimKey);
			if (implantDetails !=null && !implantDetails.isEmpty()) {
				List<ImplantCorrectionDTO> implantCorrectionDTOs = new ArrayList<ImplantCorrectionDTO>();	
				for(ImplantDetails details : implantDetails){
					ImplantCorrectionDTO correctionDTO = new ImplantCorrectionDTO();
					if(isview){		
						if(details.getDcImplantFlag() !=null 
								&& details.getDcImplantFlag().equals("Y")){
							correctionDTO.setImplantName(details.getOldImplantName());
							correctionDTO.setImplantType(details.getOldImplantType());
							correctionDTO.setImplantCost(details.getOldImplantCost());
							correctionDTO.setActualImplantCost(details.getImplantCost());
							correctionDTO.setActualImplantName(details.getImplantName());
							correctionDTO.setActualImplantType(details.getImplantType());
						}else{
							correctionDTO.setImplantName(details.getImplantName());
							correctionDTO.setImplantType(details.getImplantType());
							correctionDTO.setImplantCost(details.getImplantCost());
						}
						
					}else{
						correctionDTO.setKey(details.getKey());
						correctionDTO.setImplantName(details.getImplantName());
						correctionDTO.setImplantType(details.getImplantType());
						correctionDTO.setImplantCost(details.getImplantCost());
					}
					implantCorrectionDTOs.add(correctionDTO);		
				}
				return implantCorrectionDTOs;
			}
		}	
		return null;
	}
	
	private void saveImplantCorrectionDTO(List<ImplantCorrectionDTO> implantCorrectionDTOs,String userID,Long rodKey,Long claimKey){

		if(implantCorrectionDTOs !=null && !implantCorrectionDTOs.isEmpty()){
			for(ImplantCorrectionDTO implantCorrectionDTO:implantCorrectionDTOs){
				if(implantCorrectionDTO.getHasChanges()){
					if(implantCorrectionDTO.getKey() !=null){
						ImplantDetails details = findImplantDetailsByKey(implantCorrectionDTO.getKey());
						if(details !=null){
							if(implantCorrectionDTO.getImplantName() !=null
									&& !implantCorrectionDTO.getImplantName().equalsIgnoreCase(implantCorrectionDTO.getActualImplantName())){
								details.setOldImplantName(details.getImplantName());
								details.setImplantName(implantCorrectionDTO.getActualImplantName());
							}
							if(implantCorrectionDTO.getImplantType() != null
									&& !implantCorrectionDTO.getImplantType().equalsIgnoreCase(implantCorrectionDTO.getActualImplantType())){
								details.setOldImplantType(details.getImplantType());
								details.setImplantType(implantCorrectionDTO.getActualImplantType());
							}
							if(implantCorrectionDTO.getImplantCost() != null
									&& !implantCorrectionDTO.getImplantCost().equals(implantCorrectionDTO.getActualImplantCost())){
								details.setOldImplantCost(details.getImplantCost());
								details.setImplantCost(implantCorrectionDTO.getActualImplantCost());
							}																				
							details.setDcImplantFlag("Y");
							entityManager.merge(details);
							entityManager.flush();
							entityManager.clear();
							log.info("------ImplantDetails------>"+details+"<------------");
						}
					}else{
						ImplantDetails implantDetails = new ImplantDetails();
						implantDetails.setImplantName(implantCorrectionDTO.getActualImplantName());
						implantDetails.setImplantType(implantCorrectionDTO.getActualImplantType());
						implantDetails.setImplantCost(implantCorrectionDTO.getActualImplantCost());
						implantDetails.setClaimKey(claimKey);
						implantDetails.setTransactionKey(rodKey);
						implantDetails.setDcImplantFlag("Y");
						implantDetails.setActiveStatus(1L);	
						implantDetails.setCreatedBy(userID);
						implantDetails.setCreateDate(new Timestamp(System.currentTimeMillis()));
						entityManager.persist(implantDetails);
						entityManager.flush();
						entityManager.clear();
						log.info("------ImplantDetails------>"+implantDetails+"<------------");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HospitalScoring> getHospitalScoringByIntimationkey(Long argIntimationKey){
		Query scoringquery = entityManager.createNamedQuery("HospitalScoring.findByIntimationKey");
		scoringquery = scoringquery.setParameter("intimationKey", argIntimationKey);
		List<HospitalScoring> catScorSublist  = scoringquery.getResultList();
		return catScorSublist;
	}
	
	@SuppressWarnings("unchecked")
	public HospitalScoring getHospitalScoringBySkey(Long argIntimationKey, Long argSubCatKey){
		Query scoringCatquery = entityManager.createNamedQuery("HospitalScoring.findScoringByCatKey");
		scoringCatquery = scoringCatquery.setParameter("intimationKey", argIntimationKey);
		scoringCatquery = scoringCatquery.setParameter("subKey", argSubCatKey);
		List<HospitalScoring> catScorlist  = scoringCatquery.getResultList();
		if(catScorlist != null && !catScorlist.isEmpty()){
			return catScorlist.get(0);
		}		
		return null;
	}
	
	public Hospitals getHospitalDetailsByKey(Long hospitalKey) {
		Query query = entityManager.createNamedQuery("Hospitals.findByHospitalKey").setParameter("key", hospitalKey);
		Hospitals hospitals = (Hospitals) query.getSingleResult();
		if (hospitals != null) {
			return hospitals;
		}
		return null;
	}
}
