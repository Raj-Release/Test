package com.shaic.claim.intimation.unlock;

import java.io.Serializable;

import com.shaic.claim.registration.ackhoscomm.search.AbstractSearchDTO;

public class SearchUnlockIntimationFormDTO extends AbstractSearchDTO implements Serializable{
	
	
	private String intimationNo;
	
	public String getIntimationNo() {
		return intimationNo;
	}
	public void setIntimationNo(String intimationNo) {
		this.intimationNo = intimationNo;
	}
	
	
}