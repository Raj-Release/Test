package com.shaic.claim.misc.updatesublimit;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.table.Page;
import com.shaic.arch.table.Pageable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

public class SearchUpdateSublimitViewImpl extends AbstractMVPView implements SearchUpdateSublimitView {
	
	@Inject
	private SearchUpdateSublimitFormPage searchUpdateSublimitPage;
	
	@Inject 
	private SearchUpdateSublimitTable searchUpdateSublimitTable;
	
	private VerticalSplitPanel splitPanel;
	
	@PostConstruct
	protected void initView(){

		addStyleName("view");
		setSizeFull();
		searchUpdateSublimitPage.init();
		searchUpdateSublimitTable.init("", false, false);
		splitPanel = new VerticalSplitPanel();
		splitPanel.setFirstComponent(searchUpdateSublimitPage);
		splitPanel.setSecondComponent(searchUpdateSublimitTable);
		splitPanel.setSplitPosition(27);
		splitPanel.setSizeFull();
		setHeight("680px");
		setCompositionRoot(splitPanel);
		searchUpdateSublimitTable.addSearchListener(this);
		searchUpdateSublimitPage.addSearchListener(this);
		resetView();
	
	}

	@Override
	public void doSearch() {
		
		SearchUpdateSublimitFormDTO searchDTO = searchUpdateSublimitPage.getSearchDTO();
		if((searchDTO.getIntimationNo() != null && !("").equalsIgnoreCase(searchDTO.getIntimationNo())) || 
				(searchDTO.getPolicyNo() != null && !("").equalsIgnoreCase(searchDTO.getPolicyNo())) ){
		Pageable pageable = searchUpdateSublimitTable.getPageable();
		searchDTO.setPageable(pageable);
		String userName=(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
		String passWord=(String)getUI().getSession().getAttribute(BPMClientContext.PASSWORD);
		fireViewEvent(SearchUpdateSublimitPresenter.SEARCH_BUTTON, searchDTO,userName,passWord);
		} else {
			getErrorMessage("Any One Field is mandatory for search");
		}
	}

	@Override
	public void resetSearchResultTableValues() {
		searchUpdateSublimitTable.resetTable();
		Iterator<Component> componentIter = splitPanel.getComponentIterator();
		while(componentIter.hasNext())
		{
			Component comp = (Component)componentIter.next();
			if(comp instanceof SearchUpdateSublimitTable)
			{
				((SearchUpdateSublimitTable) comp).removeRow();
			}
		}
		
	}

	@Override
	public void resetView() {
		searchUpdateSublimitPage.refresh();
	}

	@Override
	public void list(Page<SearchUpdateSublimitTableDTO> searchDTO) {
		

		if(null != searchDTO && null != searchDTO.getPageItems() && 0!= searchDTO.getPageItems().size())
		{	
			searchUpdateSublimitTable.setTableList(searchDTO);
		}
		else
		{
			
			Label successLabel = new Label("<b style = 'color: black;'>No Records found.</b>", ContentMode.HTML);			
			Button homeButton = new Button("Home");
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
					resetSearchResultTableValues();
					fireViewEvent(MenuItemBean.UPDATE_SUBLIMIT, null);
					
				}
			});
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
