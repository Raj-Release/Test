package com.shaic.claim.pcc.divisionHead;

import javax.ejb.EJB;

import com.shaic.arch.SHAUtils;
import com.shaic.arch.table.GBaseTable;
import com.shaic.claim.pcc.SearchProcessPCCRequestService;
import com.shaic.claim.pcc.beans.PCCRequest;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;
import com.shaic.main.navigator.ui.MenuPresenter;
import com.vaadin.v7.data.util.BeanItemContainer;

public class SearchProcessPCCDivisionHeadRequestTable extends GBaseTable<SearchProcessPCCRequestTableDTO>{
			
	/**
	 * 
	 */
	private static final long serialVersionUID = 2138621920923809841L;

	private String userName;
	
	@EJB
	private SearchProcessPCCRequestService pccRequestService;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private final static Object[] NATURAL_COL_ORDER_PCC = new Object[]{"serialNumber","intimationNo","pccSource","cpuName","productName","insuredPatientName","hospitalName","pccCatagory"};

	@Override
	public void removeRow() {
		table.removeAllItems();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initTable() {
		table.setContainerDataSource(new BeanItemContainer<SearchProcessPCCRequestTableDTO>(SearchProcessPCCRequestTableDTO.class));
		table.setVisibleColumns(NATURAL_COL_ORDER_PCC);
		table.setHeight("300px");
		table.setWidth("100%");
	}

	@Override
	public void tableSelectHandler(SearchProcessPCCRequestTableDTO t) {

		fireViewEvent(MenuPresenter.SHOW_PROCESS_PCC_DIVISION_HEAD_REQUEST_WIZARD, t,userName);

	}

	@Override
	public String textBundlePrefixString() {
		// TODO Auto-generated method stub
		return "search-process-pcc-";
	}
	
	@SuppressWarnings("deprecation")
	protected void tablesize(){
		table.setPageLength(table.size()+1);
		int length =table.getPageLength();
		if(length>=7){
			table.setPageLength(7);
		}
		
	}



}
