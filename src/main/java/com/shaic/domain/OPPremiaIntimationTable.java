package com.shaic.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.shaic.arch.fields.dto.AbstractEntity;

@Entity
@Table(name="IMS_CLS_STG_OP_INTIMATION")
@NamedQueries({
	@NamedQuery(name = "OPPremiaIntimationTable.findByAll", query = "SELECT o FROM OPPremiaIntimationTable o where o.readFlag is null and o.policySource= 'P' order by policySource asc"),
	@NamedQuery(name = "OPPremiaIntimationTable.findByIntimationNumber", query = "SELECT o FROM OPPremiaIntimationTable o where o is not null and o.intimationNumber = :intimationNo")
})
public class OPPremiaIntimationTable extends AbstractEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="IMS_CLS_STG_OP_INTIMATION_KEY_GENERATOR", sequenceName = "SEQ_OP_INTIMATION_ID"  ,allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="IMS_CLS_STG_OP_INTIMATION_KEY_GENERATOR" ) 
	@Column(name="INTIMATION_KEY")
	private Long key;
	
	@Column(name="INTIMATION_NUMBER")
	private String intimationNumber;   
	
	@Column(name="INTIMATION_TYPE")
	private String intimationType;   
		
	@Column(name="POLICY_NUMBER")
	private String policyNumber;
	
	@Column(name="INSURED_ID")
	private String insuredId;
	
	@Column(name="OPHC_CONSULTATION_DATE")
	private Date ophcConsultationDate;
	
	@Column(name="BILL_RECEIVED_DATE")
	private Date billReceivedDate;
	
	@Column(name="INTIMATION_MODE")
	private String intimationMode;   
	
	@Column(name="INTIMATOR_CONTACT_NUMBER")
	private String intimatorContactNumber;  
	
	@Column(name="INTIMATOR_NAME")
	private String intimatorName;
	
	@Column(name="REASON_FOR_CONSULTATION")
	private String reasonForConsultation;
	
	@Column(name="TREATMENT_TYPE")
	private String treatmentType;
	
	@Column(name="EMAIL_ID")
	private String emailID;
	
	@Column(name="AMOUNT_CLAIMED")
	private Double amountClaimed;
	
	@Column(name="EMERGENCY_FLAG")
	private String emergencyFlag;
	
	@Column(name="ACCIDENT_FLAG")
	private String accidentFlag;
	
	@Column(name="PROD_CODE")
	private String prodCode;
	
	@Column(name="POLICY_SOURCE")
	private String policySource;
	
	@Column(name="PIO_CODE")
	private String pioCode; 
	
	@Column(name="CREATED_DATE")
	private Date createdDate;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name="MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name="ACTIVE_STATUS")
	private Long activeStatus;
	
	@Column(name="READ_FLAG")
	private String readFlag;
	
	@Column(name="READ_STATUS")
	private String readStatus;
	
	@Column(name="EMERGENCY_ACCIDENT_REMARKS")
	private String emergencyAccidentRemarks;
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getIntimationNumber() {
		return intimationNumber;
	}

	public void setIntimationNumber(String intimationNumber) {
		this.intimationNumber = intimationNumber;
	}

	public String getIntimationType() {
		return intimationType;
	}

	public void setIntimationType(String intimationType) {
		this.intimationType = intimationType;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getInsuredId() {
		return insuredId;
	}

	public void setInsuredId(String insuredId) {
		this.insuredId = insuredId;
	}

	public Date getOphcConsultationDate() {
		return ophcConsultationDate;
	}

	public void setOphcConsultationDate(Date ophcConsultationDate) {
		this.ophcConsultationDate = ophcConsultationDate;
	}

	public Date getBillReceivedDate() {
		return billReceivedDate;
	}

	public void setBillReceivedDate(Date billReceivedDate) {
		this.billReceivedDate = billReceivedDate;
	}

	public String getIntimationMode() {
		return intimationMode;
	}

	public void setIntimationMode(String intimationMode) {
		this.intimationMode = intimationMode;
	}

	public String getIntimatorContactNumber() {
		return intimatorContactNumber;
	}

	public void setIntimatorContactNumber(String intimatorContactNumber) {
		this.intimatorContactNumber = intimatorContactNumber;
	}

	public String getIntimatorName() {
		return intimatorName;
	}

	public void setIntimatorName(String intimatorName) {
		this.intimatorName = intimatorName;
	}

	public String getReasonForConsultation() {
		return reasonForConsultation;
	}

	public void setReasonForConsultation(String reasonForConsultation) {
		this.reasonForConsultation = reasonForConsultation;
	}

	public String getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(String treatmentType) {
		this.treatmentType = treatmentType;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public Double getAmountClaimed() {
		return amountClaimed;
	}

	public void setAmountClaimed(Double amountClaimed) {
		this.amountClaimed = amountClaimed;
	}

	public String getEmergencyFlag() {
		return emergencyFlag;
	}

	public void setEmergencyFlag(String emergencyFlag) {
		this.emergencyFlag = emergencyFlag;
	}

	public String getAccidentFlag() {
		return accidentFlag;
	}

	public void setAccidentFlag(String accidentFlag) {
		this.accidentFlag = accidentFlag;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getPolicySource() {
		return policySource;
	}

	public void setPolicySource(String policySource) {
		this.policySource = policySource;
	}

	public String getPioCode() {
		return pioCode;
	}

	public void setPioCode(String pioCode) {
		this.pioCode = pioCode;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(Long activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}

	public String getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}

	public String getEmergencyAccidentRemarks() {
		return emergencyAccidentRemarks;
	}

	public void setEmergencyAccidentRemarks(String emergencyAccidentRemarks) {
		this.emergencyAccidentRemarks = emergencyAccidentRemarks;
	}
}
