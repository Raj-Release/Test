package com.shaic.claim.OMPprocessrejection.search;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.table.Page;
import com.shaic.arch.table.Pageable;
import com.shaic.claim.processrejection.search.SearchProcessRejectionFormDTO;
import com.shaic.claim.processrejection.search.SearchProcessRejectionTableDTO;
import com.shaic.cmn.login.ImsUser;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

public class SearchOMPProcessRejectionViewImpl extends AbstractMVPView implements SearchOMPProcessRejectionView {

	@Inject
	private SearchOMPProcessRejectionForm searchForm;
	
	@Inject
	private SearchOMPProcessRejectionDetailTable searchResultTable;
	
	private VerticalSplitPanel mainPanel = new VerticalSplitPanel();
	
	@PostConstruct
	protected void initView() {
		
		addStyleName("view");
		setSizeFull();
		searchForm.init();
		searchResultTable.init("", false, false);
		searchResultTable.setHeight("100.0%");
		searchResultTable.setWidth("100.0%");
		searchResultTable.addStyleName((ValoTheme.TABLE_COMPACT));
		//searchForm.setHeight(600, Unit.PIXELS);

		mainPanel.setFirstComponent(searchForm);
		mainPanel.setSecondComponent(searchResultTable);
		mainPanel.setSplitPosition(35);

		mainPanel.setWidth("100.0%");
		setHeight("100.0%");
		setHeight("600px");
		setCompositionRoot(mainPanel);
		searchResultTable.addSearchListener(this);
		searchForm.addSearchListener(this);
		resetView();
		
	}
	
	@Override
	public void resetView() {
		System.out.println("---tinside the reset view");
		
		searchForm.refresh(); 
		/*if(searchForm.get() != null) {
			searchForm.get().init();
		}*/
	}

	@Override
	public void list(Page<SearchProcessRejectionTableDTO> tableRows) {
		if(null != tableRows && null != tableRows.getPageItems() && 0!= tableRows.getPageItems().size())
		{	
			searchResultTable.setTableList(tableRows);
			searchResultTable.tablesize();
			searchResultTable.setHasNextPage(tableRows.isHasNext());
			//searchResultTable.setPage(tableRows);
		}
		else
		{
			

			Label successLabel = new Label("<b style = 'color: black;'>No Records found.</b>", ContentMode.HTML);			
			Button homeButton = new Button("Process Rejection Home");
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
			
			homeButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 7396240433865727954L;

				@Override
				public void buttonClick(ClickEvent event) {
					dialog.close();
//					fireViewEvent(MenuItemBean.SEARCH_OMP_PROCESS_REJECTION, null);
					
				}
			});
			
		}
	}

	@Override
	public void doSearch() {
		SearchProcessRejectionFormDTO searchDTO = searchForm.getSearchDTO();
		Pageable pageable = searchResultTable.getPageable();
		searchDTO.setPageable(pageable);
		
		String userName=(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
		String passWord=(String)getUI().getSession().getAttribute(BPMClientContext.PASSWORD);
		ImsUser imsUser = (ImsUser)UI.getCurrent().getSession().getAttribute(BPMClientContext.USER_OBJECT);
		searchDTO.setImsUser(imsUser);
		
		fireViewEvent(SearchOMPProcessRejectionPresenter.SEARCH_OMP_BUTTON_CLICK, searchDTO,userName,passWord);
	}
	
	@Override
	public void resetSearchResultTableValues() {
		searchResultTable.resetTable();
		
		Iterator<Component> componentIter = mainPanel.getComponentIterator();
		while(componentIter.hasNext())
		{
			Component comp = (Component)componentIter.next();
			if(comp instanceof SearchOMPProcessRejectionDetailTable)
			{
				((SearchOMPProcessRejectionDetailTable) comp).removeRow();
			}
		}
	}

}
