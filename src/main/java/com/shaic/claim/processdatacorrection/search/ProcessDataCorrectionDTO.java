package com.shaic.claim.processdatacorrection.search;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.AbstractTableDTO;
import com.shaic.claim.ClaimDto;
import com.shaic.claim.scoring.HospitalScoringDTO;

public class ProcessDataCorrectionDTO extends AbstractTableDTO implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 4115196985338366211L;

	private Long intimationKey;
	
	private Long transactionKey;
	
	private Long claimKey;
	
	private Long coadingKey;
	
	private List<DiganosisCorrectionDTO> diganosisCorrectionDTOs;
	
	private List<ProcedureCorrectionDTO> procedureCorrectionDTOs;
	
	private List<SpecialityCorrectionDTO> specialityCorrectionDTOs;
	
	private SelectValue roomCategory;
	
	private SelectValue proposedroomCategory;
	
	private HospitalScoringDTO hospitalScoringDTO;
	
	private HospitalScoringDTO proposedScoringDTO;
	
	private SelectValue treatmentType;
	
	private ClaimDto claimDto;
	
	private Boolean isdiganosisChanged = false;
	
	private Boolean isprocedureChanged = false;
	
	private Boolean isspecialityChanged = false;
	
	private Boolean isroomCatChanged = false;
	
	private String remarks;
	
	private Map<String, Object> referenceData;
	
	private List<HospitalScoringDTO> scoringDTOs;
	
	private Boolean isScoringChanged = false;
	
	private String intimationNo;
	
	private List<TreatingCorrectionDTO> treatingCorrectionDTOs;
	
	private Boolean istreatingChanged = false;

	private String roomCat;

	private String proposedroomCat;

	private String treatment;
	
	private String icdExclusionReason;

	private List<ImplantCorrectionDTO> implantCorrectionDTOs;

	private Boolean isimplantChanged = false;
	
	private Boolean implantApplicable =false;
	
	private List<ImplantCorrectionDTO> deletedimplantDTOs;
	
	private Boolean implantcorrectionApplicable =false;
	
	public Long getIntimationKey() {
		return intimationKey;
	}

	public void setIntimationKey(Long intimationKey) {
		this.intimationKey = intimationKey;
	}

	public Long getTransactionKey() {
		return transactionKey;
	}

	public void setTransactionKey(Long transactionKey) {
		this.transactionKey = transactionKey;
	}

	public Long getClaimKey() {
		return claimKey;
	}

	public void setClaimKey(Long claimKey) {
		this.claimKey = claimKey;
	}

	public List<DiganosisCorrectionDTO> getDiganosisCorrectionDTOs() {
		return diganosisCorrectionDTOs;
	}

	public void setDiganosisCorrectionDTOs(
			List<DiganosisCorrectionDTO> diganosisCorrectionDTOs) {
		this.diganosisCorrectionDTOs = diganosisCorrectionDTOs;
	}

	public List<ProcedureCorrectionDTO> getProcedureCorrectionDTOs() {
		return procedureCorrectionDTOs;
	}

	public void setProcedureCorrectionDTOs(
			List<ProcedureCorrectionDTO> procedureCorrectionDTOs) {
		this.procedureCorrectionDTOs = procedureCorrectionDTOs;
	}

	public List<SpecialityCorrectionDTO> getSpecialityCorrectionDTOs() {
		return specialityCorrectionDTOs;
	}

	public void setSpecialityCorrectionDTOs(
			List<SpecialityCorrectionDTO> specialityCorrectionDTOs) {
		this.specialityCorrectionDTOs = specialityCorrectionDTOs;
	}

	public HospitalScoringDTO getHospitalScoringDTO() {
		return hospitalScoringDTO;
	}

	public void setHospitalScoringDTO(HospitalScoringDTO hospitalScoringDTO) {
		this.hospitalScoringDTO = hospitalScoringDTO;
	}

	public HospitalScoringDTO getProposedScoringDTO() {
		return proposedScoringDTO;
	}

	public void setProposedScoringDTO(HospitalScoringDTO proposedScoringDTO) {
		this.proposedScoringDTO = proposedScoringDTO;
	}

	public SelectValue getRoomCategory() {
		return roomCategory;
	}

	public void setRoomCategory(SelectValue roomCategory) {
		this.roomCategory = roomCategory;
	}

	public SelectValue getProposedroomCategory() {
		return proposedroomCategory;
	}

	public void setProposedroomCategory(SelectValue proposedroomCategory) {
		this.proposedroomCategory = proposedroomCategory;
	}

	public SelectValue getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(SelectValue treatmentType) {
		this.treatmentType = treatmentType;
	}

	public ClaimDto getClaimDto() {
		return claimDto;
	}

	public void setClaimDto(ClaimDto claimDto) {
		this.claimDto = claimDto;
	}

	public Boolean getIsdiganosisChanged() {
		return isdiganosisChanged;
	}

	public void setIsdiganosisChanged(Boolean isdiganosisChanged) {
		this.isdiganosisChanged = isdiganosisChanged;
	}

	public Boolean getIsprocedureChanged() {
		return isprocedureChanged;
	}

	public void setIsprocedureChanged(Boolean isprocedureChanged) {
		this.isprocedureChanged = isprocedureChanged;
	}

	public Boolean getIsspecialityChanged() {
		return isspecialityChanged;
	}

	public void setIsspecialityChanged(Boolean isspecialityChanged) {
		this.isspecialityChanged = isspecialityChanged;
	}

	public Boolean getIsroomCatChanged() {
		return isroomCatChanged;
	}

	public void setIsroomCatChanged(Boolean isroomCatChanged) {
		this.isroomCatChanged = isroomCatChanged;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getCoadingKey() {
		return coadingKey;
	}

	public void setCoadingKey(Long coadingKey) {
		this.coadingKey = coadingKey;
	}

	public Map<String, Object> getReferenceData() {
		return referenceData;
	}

	public void setReferenceData(Map<String, Object> referenceData) {
		this.referenceData = referenceData;
	}

	public List<HospitalScoringDTO> getScoringDTOs() {
		return scoringDTOs;
	}

	public void setScoringDTOs(List<HospitalScoringDTO> scoringDTOs) {
		this.scoringDTOs = scoringDTOs;
	}

	public Boolean getIsScoringChanged() {
		return isScoringChanged;
	}

	public void setIsScoringChanged(Boolean isScoringChanged) {
		this.isScoringChanged = isScoringChanged;
	}

	public String getIntimationNo() {
		return intimationNo;
	}

	public void setIntimationNo(String intimationNo) {
		this.intimationNo = intimationNo;
	}

	public List<TreatingCorrectionDTO> getTreatingCorrectionDTOs() {
		return treatingCorrectionDTOs;
	}

	public void setTreatingCorrectionDTOs(
			List<TreatingCorrectionDTO> treatingCorrectionDTOs) {
		this.treatingCorrectionDTOs = treatingCorrectionDTOs;
	}

	public Boolean getIstreatingChanged() {
		return istreatingChanged;
	}

	public void setIstreatingChanged(Boolean istreatingChanged) {
		this.istreatingChanged = istreatingChanged;
	}

	public String getRoomCat() {
		return roomCat;
	}

	public void setRoomCat(String roomCat) {
		this.roomCat = roomCat;
	}

	public String getProposedroomCat() {
		return proposedroomCat;
	}

	public void setProposedroomCat(String proposedroomCat) {
		this.proposedroomCat = proposedroomCat;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public List<ImplantCorrectionDTO> getImplantCorrectionDTOs() {
		return implantCorrectionDTOs;
	}

	public void setImplantCorrectionDTOs(
			List<ImplantCorrectionDTO> implantCorrectionDTOs) {
		this.implantCorrectionDTOs = implantCorrectionDTOs;
	}

	public Boolean getIsimplantChanged() {
		return isimplantChanged;
	}

	public void setIsimplantChanged(Boolean isimplantChanged) {
		this.isimplantChanged = isimplantChanged;
	}

	public Boolean getImplantApplicable() {
		return implantApplicable;
	}

	public void setImplantApplicable(Boolean implantApplicable) {
		this.implantApplicable = implantApplicable;
	}
	
	

	public List<ImplantCorrectionDTO> getDeletedimplantDTOs() {
		return deletedimplantDTOs;
	}

	public void setDeletedimplantDTOs(List<ImplantCorrectionDTO> deletedimplantDTOs) {
		this.deletedimplantDTOs = deletedimplantDTOs;
	}

	public Boolean getImplantcorrectionApplicable() {
		return implantcorrectionApplicable;
	}

	public void setImplantcorrectionApplicable(Boolean implantcorrectionApplicable) {
		this.implantcorrectionApplicable = implantcorrectionApplicable;
	}
	
	public String getIcdExclusionReason() {
		return icdExclusionReason;
	}

	public void setIcdExclusionReason(String icdExclusionReason) {
		this.icdExclusionReason = icdExclusionReason;
	}

}
