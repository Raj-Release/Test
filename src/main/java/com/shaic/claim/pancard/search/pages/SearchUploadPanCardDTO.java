/**
 * 
 */
package com.shaic.claim.pancard.search.pages;

import java.io.Serializable;

import com.shaic.claim.registration.ackhoscomm.search.AbstractSearchDTO;

/**
 * @author ntv.narenj
 *
 */
public class SearchUploadPanCardDTO extends AbstractSearchDTO implements Serializable{
	
	
	private String intimationNo;
	private String policyNo;
	
	public String getIntimationNo() {
		return intimationNo;
	}
	public void setIntimationNo(String intimationNo) {
		this.intimationNo = intimationNo;
	}
	
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	

}
