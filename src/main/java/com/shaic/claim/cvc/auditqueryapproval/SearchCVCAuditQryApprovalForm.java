package com.shaic.claim.cvc.auditqueryapproval;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.SearchComponent;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.cvc.auditaction.SearchCVCAuditActionFormDTO;
import com.shaic.domain.MasterService;
import com.shaic.domain.ReferenceTable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;


public class SearchCVCAuditQryApprovalForm extends SearchComponent<SearchCVCAuditActionFormDTO>{
	
	private SearchCVCAuditActionFormDTO searchDto;
	
	private SearchCVCAuditQryApprovalTable searchTable;
	
	@EJB
	private MasterService masterService;
	
	private TextField intimationNumber;
	
//	private ComboBox cmbStatus;
	
	private TextField txtuserId;
	
	private ComboBox cmbClmType;
	
	private BeanItemContainer<SelectValue> auditStatusContainer;
	
	private BeanItemContainer<SelectValue> auditClmStatusContainer;
	
	@PostConstruct
	public void init() {
		initBinder();
		
		Panel mainPanel = new Panel();
		mainPanel.addStyleName("panelHeader");
		mainPanel.addStyleName("g-search-panel");
		mainPanel.setCaption("Claim Audit Query Approval");
		mainPanel.setContent(mainVerticalLayout());
		setCompositionRoot(mainPanel);
		resetBtnListener();
	}
	
	public VerticalLayout mainVerticalLayout(){
		btnSearch.setCaption(SearchComponent.SEARCH_TASK_CAPTION);
		btnSearch.setDisableOnClick(true);
		mainVerticalLayout = new VerticalLayout();
		
		intimationNumber = binder.buildAndBind("Intimation No", "intimationNumber", TextField.class);
		
		/*cmbStatus = binder.buildAndBind("Status", "cmbauditStatus", ComboBox.class);
		cmbStatus.setNullSelectionAllowed(Boolean.FALSE);
		auditStatusContainer = new BeanItemContainer<SelectValue>(SelectValue.class);
		auditStatusContainer.addBean(new SelectValue(1L,SHAConstants.AUDIT_QUERY_APPROVAL_PENDING));
		
		cmbStatus.setContainerDataSource(auditStatusContainer);
		cmbStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbStatus.setItemCaptionPropertyId("value");
		cmbStatus.setValue(auditStatusContainer.getItemIds().get(0));
		cmbStatus.setEnabled(false);*/
		
		txtuserId = binder.buildAndBind("User Id", "userId", TextField.class);
		
		cmbClmType = binder.buildAndBind("Claim Type", "clmType", ComboBox.class);
		auditClmStatusContainer = masterService.getMasterValueByReference(ReferenceTable.CLAIM_TYPE);
		
		cmbClmType.setContainerDataSource(auditClmStatusContainer);
		cmbClmType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbClmType.setItemCaptionPropertyId("value");
		
		FormLayout formLayoutLeft = new FormLayout(intimationNumber);
//		FormLayout formLayoutMiddle = new FormLayout(cmbStatus);
		FormLayout formLayoutRight = new FormLayout(txtuserId);
		FormLayout formLayoutRight1 = new FormLayout(cmbClmType);
		HorizontalLayout fieldLayout = new HorizontalLayout(formLayoutLeft, /*formLayoutMiddle,*/ formLayoutRight, formLayoutRight1);
		fieldLayout.setMargin(true);
		fieldLayout.setSpacing(true);
		fieldLayout.setWidth("100%");
		fieldLayout.setSizeFull();
		AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		absoluteLayout_3.addComponent(fieldLayout);		
		absoluteLayout_3.addComponent(btnSearch, "top:100.0px;left:220.0px;");
		absoluteLayout_3.addComponent(btnReset, "top:100.0px;left:320.0px;");
		
		mainVerticalLayout.addComponent(absoluteLayout_3);
		//Vaadin8-setImmediate() mainVerticalLayout.setImmediate(false);
		mainVerticalLayout.setWidth("100%");
		mainVerticalLayout.setMargin(false);		 
		//Vaadin8-setImmediate() absoluteLayout_3.setImmediate(false);
		absoluteLayout_3.setWidth("100.0%");
		absoluteLayout_3.setHeight("150px");
		 
		addListener();
		return mainVerticalLayout;

	}
	private void initBinder()

	{
		this.binder = new BeanFieldGroup<SearchCVCAuditActionFormDTO>(SearchCVCAuditActionFormDTO.class);
		this.binder.setItemDataSource(new SearchCVCAuditActionFormDTO());
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
	}
	
	public void setDropDownValues() {
		
		auditStatusContainer = masterService.getMasterValueByReference(SHAConstants.CVC_REMEDIATION_STATUS);
		
		/*cmbStatus.setContainerDataSource(auditStatusContainer);
		cmbStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbStatus.setItemCaptionPropertyId("value");*/
		// TODO Implements
//		cmbStatus.setValue(auditStatusContainer.getItemIds().get(2));
		
		cmbClmType.setContainerDataSource(auditClmStatusContainer);
		cmbClmType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbClmType.setItemCaptionPropertyId("value");
				
		/*String userName=(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
		txtuserId.setValue(userName != null ? userName.toUpperCase() : "");*/
	}
	private void resetBtnListener(){
	btnReset.addClickListener(new Button.ClickListener() {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			
//			cmbStatus.setValue(auditStatusContainer.getItemIds().get(1));
			
			/*String userName=(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
			txtuserId.setValue(userName != null ? userName.toUpperCase() : "");*/
			
			intimationNumber.setValue("");
			cmbClmType.setValue(null);
		}
	});
	}
}
