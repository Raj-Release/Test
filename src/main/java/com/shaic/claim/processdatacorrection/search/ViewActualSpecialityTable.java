package com.shaic.claim.processdatacorrection.search;

import com.shaic.arch.table.GBaseTable;
import com.vaadin.v7.data.util.BeanItemContainer;

public class ViewActualSpecialityTable extends GBaseTable<SpecialityCorrectionDTO>{
	


	private static final long serialVersionUID = 1L;

	@Override
	public void removeRow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initTable() {
		table.removeAllItems();
		table.setContainerDataSource(new BeanItemContainer<SpecialityCorrectionDTO>(SpecialityCorrectionDTO.class));
		Object[] VISIBLE_COLUMNS = new Object[] {"serialNumber","actualspecialityType","actualProcedure","actualRemarks"};

		table.setVisibleColumns(VISIBLE_COLUMNS);
		table.setColumnWidth("serialNumber",80);
		table.setWidth("100%");
		table.setPageLength(table.getItemIds().size());
	}

	@Override
	public void tableSelectHandler(SpecialityCorrectionDTO t) {
		// TODO Auto-generated method stub

	}

	@Override
	public String textBundlePrefixString() {
		// TODO Auto-generated method stub
		return "view-actual-speciality-table-";
	}


}
