package com.shaic.claim.userproduct.document.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;

import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.table.GBaseTable;
import com.shaic.claim.fvrdetails.view.ViewFVRDTO;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.VerticalLayout;

@UIScoped
public class LimitTable extends GBaseTable<UserManagementDTO>{
	private final static Object[] NATURAL_COL_ORDER = new Object[]{"roleCtegory","limit"}; 
	@EJB
	private UserMagmtService userMgmtService;
	private List<UserManagementDTO> deletedList = null;
	private UserManagementDTO dto;	
	
	@Override
	public void removeRow() {
		//table.remov;
		
	}

	@Override
	public void initTable() {
		
		table.setContainerDataSource(new BeanItemContainer<UserManagementDTO>(UserManagementDTO.class));
		deletedList = new ArrayList<UserManagementDTO>();
		table.setVisibleColumns(NATURAL_COL_ORDER);
		table.setHeight("331px");
		table.setWidth("50%");
		table.removeGeneratedColumn("Delete");
		table.addGeneratedColumn("Delete", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				final Button del = new Button("Delete");
				del.setData(itemId);
				del.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						Object currentItemId = event.getButton().getData();
						dto = (UserManagementDTO) currentItemId;
						deletedList.add(dto);
						
						table.removeItem(del.getData());
					}
				});
				return del;
				
			}
			
		});
		table.setColumnHeader("Delete", "Delete");
		
	}
	
	@Override
	public String textBundlePrefixString() {
		
		return "user-limit-";
	}

	@Override
	public void tableSelectHandler(UserManagementDTO t) {
		// TODO Auto-generated method stub
		
	}
	
	public void addUserLimit(UserManagementDTO userManagementDTO) {
		
		List<UserManagementDTO> itemIds = (List<UserManagementDTO>) table.getItemIds();
		Iterator<UserManagementDTO> iterator = itemIds.iterator();
		List<UserManagementDTO> totalList = new ArrayList<UserManagementDTO>();
		
		while(iterator.hasNext()){
			UserManagementDTO next = iterator.next();
			totalList.add(next);
		}
		if(!totalList.isEmpty()) {
		if( totalList.get(0).getRoleCtegory().toString().equals(userManagementDTO.getRoleCtegory().toString())) {
			if(totalList.get(0).getLimit().toString().contains(SHAConstants.AMA)) {
				if(totalList.size()>=1) {
					showErrorMessage("User Limit Exceeded");
				}
				else if(userManagementDTO.getLimit().toString().contains(SHAConstants.CMA) || userManagementDTO.getLimit().toString().contains(SHAConstants.RMA) 
						|| userManagementDTO.getLimit().toString().contains(SHAConstants.AMA)  ) {
					showErrorMessage("Please select a valid limit");
				}else{
					totalList.add(userManagementDTO);
					for (UserManagementDTO userManagementDTO2 : totalList) {
						table.addItem(userManagementDTO2);
					}
					
				}
			}else if(totalList.get(0).getLimit().toString().contains(SHAConstants.RMA)) {
				if(totalList.size()>=2) {
					showErrorMessage("User Limit Exceeded");
				}else if(userManagementDTO.getLimit().toString().contains(SHAConstants.AMA) || userManagementDTO.getLimit().toString().contains(SHAConstants.RMA)) {
					showErrorMessage("Please select a valid limit");
				}else{
					totalList.add(userManagementDTO);
					for (UserManagementDTO userManagementDTO2 : totalList) {
						table.addItem(userManagementDTO2);
					}
					
				}
				
			}else if(totalList.get(0).getLimit().toString().contains(SHAConstants.CMA)) {
				if(totalList.size()>=2) {
					showErrorMessage("User Limit Exceeded");
				}else if(userManagementDTO.getLimit().toString().contains(SHAConstants.AMA) || userManagementDTO.getLimit().toString().contains(SHAConstants.CMA)) {
					showErrorMessage("Please select a valid limit");
				}else{
					totalList.add(userManagementDTO);
					for (UserManagementDTO userManagementDTO2 : totalList) {
						table.addItem(userManagementDTO2);
					}
				}
			}
			else if(totalList.get(0).getLimit().toString().contains(SHAConstants.FA)) {
				if(totalList.size()>=1) {
					showErrorMessage("User Limit Exceeded");
				}else{
				totalList.add(userManagementDTO);
				for (UserManagementDTO userManagementDTO2 : totalList) {
					table.addItem(userManagementDTO2);
				}
			}
		}
		}else{
			showErrorMessage("Role Category must be same");
		}
		}else{
			
		totalList.add(userManagementDTO);
		for (UserManagementDTO userManagementDTO2 : totalList) {
			table.addItem(userManagementDTO2);
		}
		}
	}
	
	private void showErrorMessage(String eMsg) {
		Label label = new Label(eMsg, ContentMode.HTML);
		label.setStyleName("errMessage");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(label);

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setCaption("Errors");
		dialog.setClosable(true);
		dialog.setContent(layout);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.show(getUI().getCurrent(), null, true);
	}

	public List<UserManagementDTO> getTableList() {
		return (List<UserManagementDTO>) table.getItemIds();
	}
	
	 @SuppressWarnings("unchecked")
		public List<UserManagementDTO> getDeletedList() {
	    	return deletedList;
		}
}
