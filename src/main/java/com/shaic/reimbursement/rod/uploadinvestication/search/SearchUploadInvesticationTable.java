package com.shaic.reimbursement.rod.uploadinvestication.search;

import java.util.Map;

import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.table.GBaseTable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.shaic.main.navigator.ui.MenuPresenter;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

/**
 * @author ntv.narenj
 *
 */
public class SearchUploadInvesticationTable extends GBaseTable<SearchUploadInvesticationTableDTO>{

	private final static Object[] NATURAL_COL_ORDER = new Object[]{"serialNumber", "crmFlagged", "intimationNo", "claimNo", "policyNo", 
		"insuredPatientName", "cpuCode", "claimType" ,"hospitalName", "hospitalAddress", "hospitalCity",  "reasonForAdmission", "claimStatus"}; 
	
	@Override
	public void removeRow() {
		table.removeAllItems();
		
	}

	@Override
	public void initTable() {
		
		table.setContainerDataSource(new BeanItemContainer<SearchUploadInvesticationTableDTO>(SearchUploadInvesticationTableDTO.class));
		table.setVisibleColumns(NATURAL_COL_ORDER);
		table.setColumnWidth("hospitalAddress", 350);
		table.setColumnWidth("hospitalCity", 250);
		table.setHeight("280px");
	}

	@Override
	public void tableSelectHandler(
			SearchUploadInvesticationTableDTO t) {
		
		 VaadinSession session = getSession();
			
			Boolean isActiveHumanTask = false;
			
			Map<String, Object> wrkFlowMap = (Map<String, Object>) t.getDbOutArray();
			Long wrkFlowKey = (Long) wrkFlowMap.get(SHAConstants.WK_KEY);
			String currentqueue = (String) wrkFlowMap.get(SHAConstants.CURRENT_Q);
			String userID =(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
			DBCalculationService dbCalculationService = new DBCalculationService();
			String callLockProcedure = dbCalculationService.callLockProcedure(wrkFlowKey, currentqueue, userID);
			String[] lockSplit = callLockProcedure.split(":");
			String sucessMsg = lockSplit[0];
			String userName = lockSplit[1];
		 
			
			if (sucessMsg != null && sucessMsg.equalsIgnoreCase("SUCCESS")){
				isActiveHumanTask= true;
			}
			
			try{
				if(isActiveHumanTask){
					
						/*SHAUtils.setActiveOrDeactiveClaim(t.getUsername(),t.getPassword(),t.getTaskNumber(),session);*/
						SHAUtils.setDBActiveOrDeactiveClaim(wrkFlowKey,session);
						fireViewEvent(MenuPresenter.UPLOAD_INVESTIGATION_REPORT, t);
					}else{
						getErrorMessage("This record is already opened by another user");
					}
			}catch(Exception e){
				
				Integer existingTaskNumber= (Integer)session.getAttribute(SHAConstants.TOKEN_ID);
				SHAUtils.releaseHumanTask(t.getUsername(), t.getPassword(), existingTaskNumber, session);
				e.printStackTrace();
			}
		
	}

	@Override
	public String textBundlePrefixString() {
		
		return "search-upload-incestigation-report-";
	}
	protected void tablesize(){
		table.setPageLength(table.size()+1);
		int length =table.getPageLength();
		if(length>=7){
			table.setPageLength(7);
		}
		
	}
	
	public void getErrorMessage(String eMsg){
		
		Label label = new Label(eMsg, ContentMode.HTML);
		label.setStyleName("errMessage");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(label);

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setCaption("Error");
		dialog.setClosable(true);
		dialog.setContent(layout);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.show(getUI().getCurrent(), null, true);
	}

}
