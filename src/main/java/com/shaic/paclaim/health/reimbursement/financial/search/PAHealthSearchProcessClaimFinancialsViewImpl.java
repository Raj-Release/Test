/**
 * 
 */
package com.shaic.paclaim.health.reimbursement.financial.search;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Page;
import com.shaic.arch.table.Pageable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.shaic.main.navigator.ui.MenuPresenter;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author ntv.narenj
 *
 */
public class PAHealthSearchProcessClaimFinancialsViewImpl extends AbstractMVPView implements PAHealthSearchProcessClaimFinancialsView{

	
	@Inject
	private PAHealthSearchProcessClaimFinancialsForm  searchForm;
	
	@Inject
	private PAHealthSearchProcessClaimFinancialsTable searchResultTable;
	
	
	private VerticalSplitPanel mainPanel;
	
	@PostConstruct
	protected void initView() {
		addStyleName("view");
		setSizeFull();
		searchForm.init();
		searchResultTable.init("", false, false);
		mainPanel = new VerticalSplitPanel();
		mainPanel.setFirstComponent(searchForm);
		mainPanel.setSecondComponent(searchResultTable);
		mainPanel.setSplitPosition(48);
		setHeight("570px");
		//mainPanel.setHeight("625px");
		setCompositionRoot(mainPanel);
		searchResultTable.addSearchListener(this);
		searchForm.addSearchListener(this);
		resetView();
	}
	
	@Override
	public void resetView() {
		searchForm.refresh(); 
		
	}

	@Override
	public void doSearch() {
		PAHealthSearchProcessClaimFinancialsFormDTO searchDTO = searchForm.getSearchDTO();
		Pageable pageable = searchResultTable.getPageable();
		searchDTO.setPageable(pageable);
		String userName=(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
		String passWord=(String)getUI().getSession().getAttribute(BPMClientContext.PASSWORD);
		
		
		Double claimedAmountFrom = searchDTO.getClaimedAmountFrom();
		Double claimedAmountTo = searchDTO.getClaimedAmountTo();
		
		if(claimedAmountFrom != null && claimedAmountTo != null)  {
			if((claimedAmountFrom > claimedAmountTo || claimedAmountTo < claimedAmountFrom)) {
				 getErrorMessage("Claimed Amount From should not less than Claim Amount To");
			}else{
				fireViewEvent(PAHealthSearchProcessClaimFinancialsPresenter.SEARCH_BUTTON_CLICK, searchDTO,userName,passWord);
			}
		     
		}else{
			fireViewEvent(PAHealthSearchProcessClaimFinancialsPresenter.SEARCH_BUTTON_CLICK, searchDTO,userName,passWord);
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
	
	
	@Override
	public void resetSearchResultTableValues() {
		searchResultTable.getPageable().setPageNumber(1);
		searchResultTable.resetTable();
		Iterator<Component> componentIter = mainPanel.getComponentIterator();
		while(componentIter.hasNext())
		{
			Component comp = (Component)componentIter.next();
			if(comp instanceof PAHealthSearchProcessClaimFinancialsTable)
			{
				((PAHealthSearchProcessClaimFinancialsTable) comp).removeRow();
			}
		}
	
		
	}
	
//	public void setUpDropDownValues(BeanItemContainer<SelectValue> claimType,
//		BeanItemContainer<SelectValue> productName,
//		BeanItemContainer<SelectValue> cpuCode){
//		searchForm.setUpDropDownValues(claimType,productName,cpuCode);
//	}

	@Override
	public void list(Page<PAHealthSearchProcessClaimFinancialsTableDTO> tableRows) {
		if(null != tableRows && null != tableRows.getPageItems() && 0!= tableRows.getPageItems().size())
		{	
			searchResultTable.setTableList(tableRows);
			searchResultTable.tablesize();
			searchResultTable.setHasNextPage(tableRows.isHasNext());
		}
		
		else
		{
			PAHealthSearchProcessClaimFinancialsFormDTO searchDTO = searchForm.getSearchDTO();
			/**
			 * If every screen has intimation no within, then the 
			 * below search id setter is not required. Post analysis, shall
			 * think of removing the same.
			 * */
			if(searchDTO.getIntimationNo() != null && !searchDTO.getIntimationNo().isEmpty() && searchDTO.getIntimationNo().length() > 0  && 
					(null == searchDTO.getPolicyNo() ||  searchDTO.getPolicyNo().isEmpty()) && (null == searchDTO.getCpuCode())  
					 && (null == searchDTO.getType()) && (null == searchDTO.getProductName()) && (null == searchDTO.getClaimType()) && (null == searchDTO.getSource()) && (null == searchDTO.getPriority()) && (null == searchDTO.getClaimedAmountFrom()) && (null == searchDTO.getClaimedAmountTo())
					) {
				searchDTO.setSearchId(searchDTO.getIntimationNo());
				fireViewEvent(MenuPresenter.SHOW_SEARCH_SCREEN_VALIDATION_MESSAGE, searchDTO,null);
			} 
			else
			{
				
				Label successLabel = new Label("<b style = 'color: black;'>No Records found.</b>", ContentMode.HTML);			
				Button homeButton = new Button("Process Claim Financials Home");
				homeButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
				VerticalLayout layout = new VerticalLayout(successLabel, homeButton);
				layout.setComponentAlignment(homeButton, Alignment.BOTTOM_CENTER);
				layout.setSpacing(true);
				layout.setMargin(true);
				HorizontalLayout hLayout = new HorizontalLayout(layout);
				hLayout.setMargin(true);
				
				final ConfirmDialog dialog = new ConfirmDialog();
				dialog.setCaption("");
				dialog.setClosable(false);
				dialog.setContent(hLayout);
				dialog.setResizable(false);
				dialog.setModal(true);
				dialog.show(getUI().getCurrent(), null, true);
				
				homeButton.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 7396240433865727954L;

					@Override
					public void buttonClick(ClickEvent event) {
						dialog.close();
						fireViewEvent(MenuItemBean.PA_HEALTH_PROCESS_CLAIM_FINANCIALS, null);
						
					}
				});
			}
			}
		
		searchForm.enableButtons();
	}

	public void setUpDropDownValues(BeanItemContainer<SelectValue> claimType,
			BeanItemContainer<SelectValue> productName,
			BeanItemContainer<SelectValue> cpuCode, BeanItemContainer<SelectValue> type, BeanItemContainer<SelectValue> selectValueForPriority, BeanItemContainer<SelectValue> statusByStage) {
		searchForm.setUpDropDownValues(claimType,productName,cpuCode,type,selectValueForPriority,statusByStage);
		
	}

}
