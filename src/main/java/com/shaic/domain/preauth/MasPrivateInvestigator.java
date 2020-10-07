package com.shaic.domain.preauth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name= "MAS_PRIVATE_INVESTIGATOR")
@NamedQueries({
	@NamedQuery(name="MasPrivateInvestigator.findAll",query="SELECT i from MasPrivateInvestigator i"),
	@NamedQuery(name="MasPrivateInvestigator.findByInvestigaitonKey",query="SELECT i from MasPrivateInvestigator i where i.privateInvestigationKey = :privateInvestigationKey"),
	@NamedQuery(name="MasPrivateInvestigator.findByZoneCode",query="SELECT i from MasPrivateInvestigator i where i.zoneCode = :zoneCode"),
	@NamedQuery(name="MasPrivateInvestigator.findByUniqueZone",query="SELECT distinct(i.zoneCode) from MasPrivateInvestigator i"),
	@NamedQuery(name="MasPrivateInvestigator.findByCoordinatorCode",query="SELECT i from MasPrivateInvestigator i where i.coridnatorCode = :coridnatorCode")
})
public class MasPrivateInvestigator implements Serializable{
	
	@Id
	@Column(name="PRIVATE_INVS_KEY")
	private Long privateInvestigationKey;
	
	@Column(name="INVESTIGATOR_NAME")
	private String investigatorName;
	
	@Column(name="INVESTICATOR_CONSULTANCY")
	private String consultancy;
	
	@Column(name="EMAIL_ID")
	private String emailId;
	
	@Column(name="CONTACT_PERSON")
	private String contactPerson;
	
	@Column(name="MOBILE_NUMBER_1")
	private Long mobileNumberOne;
	
	@Column(name="MOBILE_NUMBER_2")
	private Long mobileNumberTwo;
	
	@Column(name="ZONE_CODE")
	private Long zoneCode;
	
	@Column(name="ZONE_NAME")
	private String zoneName;
	
	@Column(name="STAR_CORDINATOR_CODE")
	private String coridnatorCode;
	
	@Column(name="STAR_CORDINATOR_NAME")
	private String cordinatorName;

	public Long getPrivateInvestigationKey() {
		return privateInvestigationKey;
	}

	public void setPrivateInvestigationKey(Long privateInvestigationKey) {
		this.privateInvestigationKey = privateInvestigationKey;
	}

	public String getInvestigatorName() {
		return investigatorName;
	}

	public void setInvestigatorName(String investigatorName) {
		this.investigatorName = investigatorName;
	}

	public String getConsultancy() {
		return consultancy;
	}

	public void setConsultancy(String consultancy) {
		this.consultancy = consultancy;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Long getMobileNumberOne() {
		return mobileNumberOne;
	}

	public void setMobileNumberOne(Long mobileNumberOne) {
		this.mobileNumberOne = mobileNumberOne;
	}

	public Long getMobileNumberTwo() {
		return mobileNumberTwo;
	}

	public void setMobileNumberTwo(Long mobileNumberTwo) {
		this.mobileNumberTwo = mobileNumberTwo;
	}

	public Long getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(Long zoneCode) {
		this.zoneCode = zoneCode;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getCoridnatorCode() {
		return coridnatorCode;
	}

	public void setCoridnatorCode(String coridnatorCode) {
		this.coridnatorCode = coridnatorCode;
	}

	public String getCordinatorName() {
		return cordinatorName;
	}

	public void setCordinatorName(String cordinatorName) {
		this.cordinatorName = cordinatorName;
	}

	

}
