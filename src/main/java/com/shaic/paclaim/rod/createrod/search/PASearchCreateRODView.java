package com.shaic.paclaim.rod.createrod.search;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.arch.table.Searchable;
import com.shaic.reimbursement.rod.createrod.search.SearchCreateRODTableDTO;
import com.vaadin.v7.data.util.BeanItemContainer;

public interface PASearchCreateRODView extends Searchable{
	
	public void list(Page<SearchCreateRODTableDTO> tableRows);
	public void init(BeanItemContainer<SelectValue> parameter,
			BeanItemContainer<SelectValue> selectValueForPriority,
			BeanItemContainer<SelectValue> statusByStage,BeanItemContainer<SelectValue> selectValueForUploadedDocument);

}
