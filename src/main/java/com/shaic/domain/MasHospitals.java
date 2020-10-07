package com.shaic.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.shaic.arch.fields.dto.AbstractEntity;

@Entity
@Table(name = "MAS_HOSPITALS")
@NamedQueries({
		@NamedQuery(name = "MasHospitals.findAll", query = "SELECT m FROM MasHospitals m"),
		@NamedQuery(name = "MasHospitals.findByKey", query = "SELECT m FROM MasHospitals m where m.key = :key"),
})
public class MasHospitals extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "HOSPITAL_KEY")
	private Long key;

	@Column(name = "HOSPITAL_NAME")
	private String name;

	@Column(name = "EMAIL_ID")
	private String emailId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
}