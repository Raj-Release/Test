package com.shaic.claim.processdatacorrection.search;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.AbstractTableDTO;

public class ProcedureCorrectionDTO extends AbstractTableDTO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5665373532612108522L;

	private Long key;

	private SelectValue procedureName;

	private SelectValue procedureCode;
	
	private SelectValue proposedProcedureName;

	private SelectValue proposedProcedureCode;

	private Boolean hasChanges = false;
	
	private int serialNo;
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public SelectValue getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(SelectValue procedureName) {
		this.procedureName = procedureName;
	}

	public SelectValue getProcedureCode() {
		return procedureCode;
	}

	public void setProcedureCode(SelectValue procedureCode) {
		this.procedureCode = procedureCode;
	}

	public SelectValue getProposedProcedureName() {
		return proposedProcedureName;
	}

	public void setProposedProcedureName(SelectValue proposedProcedureName) {
		this.proposedProcedureName = proposedProcedureName;
	}

	public SelectValue getProposedProcedureCode() {
		return proposedProcedureCode;
	}

	public void setProposedProcedureCode(SelectValue proposedProcedureCode) {
		this.proposedProcedureCode = proposedProcedureCode;
	}

	public Boolean getHasChanges() {
		return hasChanges;
	}

	public void setHasChanges(Boolean hasChanges) {
		this.hasChanges = hasChanges;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}
	
}
