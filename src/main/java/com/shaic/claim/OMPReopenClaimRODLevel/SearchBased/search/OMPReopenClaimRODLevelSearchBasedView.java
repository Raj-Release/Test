package com.shaic.claim.OMPReopenClaimRODLevel.SearchBased.search;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.arch.table.Searchable;
import com.vaadin.v7.data.util.BeanItemContainer;

public interface OMPReopenClaimRODLevelSearchBasedView extends Searchable{
	
	public void list(Page<OMPReopenClaimRODLevelSearchBasedTableDTO> tableRows);
	public void init(BeanItemContainer<SelectValue> parameter,
			BeanItemContainer<SelectValue> selectValueForPriority,
		BeanItemContainer<SelectValue> statusByStage,BeanItemContainer<SelectValue> selectValueForUploadedDocument);


}
