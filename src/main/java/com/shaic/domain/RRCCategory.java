/**
 * 
 */
package com.shaic.domain;

/**
 * @author ntv.vijayar
 *
 */
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.shaic.arch.fields.dto.AbstractEntity;


/**
 * The persistent class for the IMS_CLS_RRC_REQUEST database table.
 * 
 */
@Entity
@Table(name="IMS_CLS_RRC_CATEGORY")
@NamedQueries({
	@NamedQuery(name="RRCCategory.findAll", query="SELECT m FROM RRCCategory m"),
	@NamedQuery(name="RRCCategory.findByKey", query="SELECT m FROM RRCCategory m where m.rrcCategoryKey = :rrcCategoryKey"),
	@NamedQuery(name="RRCCategory.findByRequestKey", query="SELECT m FROM RRCCategory m where m.rrcRequest = :rrcRequestKey")
})
public class RRCCategory extends AbstractEntity implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1086997314154372927L;

	@Id
	@SequenceGenerator(name="IMS_CLS_RRC_CATEGORY_GENERATOR", sequenceName = "SEQ_RRC_CATEGORY_KEY", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="IMS_CLS_RRC_CATEGORY_GENERATOR" ) 
	@Column(name="RRC_CATEGORY_KEY")
	private Long rrcCategoryKey;
	
//	@OneToOne
//	@JoinColumn(name="RRC_REQUEST_KEY", nullable=false)
//	private RRCRequest rrcRequest;
	
	@Column(name = "RRC_REQUEST_KEY")
	private Long rrcRequest;

	@OneToOne
	@JoinColumn(name="CATEGORY_ID", nullable=false)
	private MastersValue categoryId;
	
	@Column(name = "RRC_SUB_CATEGORY_KEY")
	private Long subCategorykey;
	
	@Column(name = "RRC_SOURCE_KEY")
	private Long sourcekey;

	public Long getRrcCategoryKey() {
		return rrcCategoryKey;
	}

	public void setRrcCategoryKey(Long rrcCategoryKey) {
		this.rrcCategoryKey = rrcCategoryKey;
	}

//	public RRCRequest getRrcRequest() {
//		return rrcRequest;
//	}
//
//	public void setRrcRequest(RRCRequest rrcRequest) {
//		this.rrcRequest = rrcRequest;
//	}

	public MastersValue getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(MastersValue categoryId) {
		this.categoryId = categoryId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public Long getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setKey(Long key) {
		// TODO Auto-generated method stub
		
	}

	public Long getRrcRequest() {
		return rrcRequest;
	}

	public void setRrcRequest(Long rrcRequest) {
		this.rrcRequest = rrcRequest;
	}

	public Long getSubCategorykey() {
		return subCategorykey;
	}

	public void setSubCategorykey(Long subCategorykey) {
		this.subCategorykey = subCategorykey;
	}

	public Long getSourcekey() {
		return sourcekey;
	}

	public void setSourcekey(Long sourcekey) {
		this.sourcekey = sourcekey;
	}
	
}
