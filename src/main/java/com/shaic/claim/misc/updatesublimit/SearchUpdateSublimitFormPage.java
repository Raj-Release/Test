package com.shaic.claim.misc.updatesublimit;

import javax.annotation.PostConstruct;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SearchComponent;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

public class SearchUpdateSublimitFormPage extends SearchComponent<SearchUpdateSublimitFormDTO>{
	
	private TextField txtIntimationNo;
	private TextField txtPolicyNo;
	
	private Panel mainPanel;
	
	@PostConstruct
	public void init(){
		initBinder();
		
		mainPanel = new Panel();
		mainPanel.addStyleName("panelHeader");
		mainPanel.addStyleName("g-search-panel");
		mainPanel.setCaption("Update Sublimit");
		mainPanel.setContent(mainVerticalLayout());
		setCompositionRoot(mainPanel);
	}
	
	public VerticalLayout mainVerticalLayout(){

		btnSearch.setCaption(SearchComponent.SEARCH_TASK_CAPTION);
		btnSearch.setDisableOnClick(true);
		mainVerticalLayout = new VerticalLayout();
		
		txtIntimationNo = binder.buildAndBind("Intimation No", "intimationNo", TextField.class);
		txtPolicyNo = binder.buildAndBind("Policy Number","policyNo",TextField.class);

		
		FormLayout formLayoutLeft = new FormLayout(txtIntimationNo);
		FormLayout formLayoutReight = new FormLayout(txtPolicyNo);	

		

		HorizontalLayout fieldLayout = new HorizontalLayout(formLayoutLeft,formLayoutReight);
		//Vaadin8-setImmediate() fieldLayout.setImmediate(false);
		fieldLayout.setSpacing(true);


		fieldLayout.setMargin(true);
		fieldLayout.setWidth("100%");		
		AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		absoluteLayout_3.addComponent(fieldLayout);		
		absoluteLayout_3.addComponent(btnSearch, "top:100.0px;left:220.0px;");
		absoluteLayout_3.addComponent(btnReset, "top:100.0px;left:329.0px;");
		
		
		mainVerticalLayout.addComponent(absoluteLayout_3);
		//Vaadin8-setImmediate() mainVerticalLayout.setImmediate(false);
		mainVerticalLayout.setWidth("1000px");
		mainVerticalLayout.setMargin(false);		 
		//Vaadin8-setImmediate() absoluteLayout_3.setImmediate(false);
		absoluteLayout_3.setWidth("100.0%");
		absoluteLayout_3.setHeight("140px");
		addListener();
		
		return mainVerticalLayout;
	
	}
	
	private void initBinder(){
		this.binder = new BeanFieldGroup<SearchUpdateSublimitFormDTO>(SearchUpdateSublimitFormDTO.class);
		this.binder.setItemDataSource(new SearchUpdateSublimitFormDTO());
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
	}
}
