package com.shaic.reimbursement.processi_investigationi_initiated.search;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.SearchComponent;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.domain.MasterService;
import com.shaic.domain.ReferenceTable;
import com.shaic.reimbursement.investigation.assigninvestigation.search.SearchAssignInvestigationTable;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

/**
 * @author ntv.narenj
 *
 */
public class SearchProcessInvestigationInitiatedForm extends SearchComponent<SearchProcessInvestigationInitiatedFormDTO> {


	@Inject
	private SearchAssignInvestigationTable searchTable; 
	
	private TextField txtIntimationNo;
	private TextField txtPolicyNo;
	private ComboBox cbxCPUCode;
	private ComboBox cmbClaimType;
	
	private ComboBox cmbType;
	private ComboBox cmbPriority;
	private ComboBox cmbSource;
	
	private ComboBox cbxhospitalType;
	private ComboBox cbxNetworkHospType;
//	private ComboBox cmbPriorityNew;
	
	private Label priority;
	
	private CheckBox chkAll;
	
	private CheckBox chkCRM;
	
	private CheckBox chkVIP;
	
	@EJB
	private MasterService masterService;
	
	BeanItemContainer<SelectValue> networkHospitalType;	
	
	@PostConstruct
	public void init() {
		initBinder();
		
		Panel mainPanel = new Panel();
		mainPanel.addStyleName("panelHeader");
		mainPanel.addStyleName("g-search-panel");
		mainPanel.setCaption("Process Investigation Initiated");
		mainPanel.setContent(mainVerticalLayout());
		setCompositionRoot(mainPanel);
	}
	
	public VerticalLayout mainVerticalLayout(){
		btnSearch.setCaption(SearchComponent.SEARCH_TASK_CAPTION);
		btnSearch.setDisableOnClick(true);
		mainVerticalLayout = new VerticalLayout();
		
		txtIntimationNo = binder.buildAndBind("Intimation No", "intimationNo", TextField.class);
		cbxCPUCode = binder.buildAndBind("CPU Code","cpuCode",ComboBox.class);
		txtPolicyNo = binder.buildAndBind("Policy Number","policyNo",TextField.class);
		cmbClaimType = binder.buildAndBind("Claim Type","claimType",ComboBox.class);
		
		cmbPriority = binder.buildAndBind("Priority(IRDA)","priority",ComboBox.class);
		cmbType = binder.buildAndBind("Type","type",ComboBox.class);
		cmbSource = binder.buildAndBind("Source","source",ComboBox.class);
		
		cbxhospitalType = binder.buildAndBind("Hospital Type","hospitalType",ComboBox.class);
		cbxNetworkHospType = binder.buildAndBind("Network Hosp Type","networkHospType",ComboBox.class);
//		cmbPriorityNew = binder.buildAndBind("Priority", "priorityNew", ComboBox.class);
		
		priority = new Label();
		priority.setCaption("Priority");
		
		chkAll = binder.buildAndBind("All","priorityAll",CheckBox.class);
		chkAll.setValue(Boolean.TRUE);
		
		chkCRM = binder.buildAndBind("CRM","crm",CheckBox.class);
		
		chkVIP = binder.buildAndBind("VIP","vip",CheckBox.class);
		
		HorizontalLayout priorityHorLayout = new HorizontalLayout(priority,chkAll,chkCRM,chkVIP);
		priorityHorLayout.setMargin(false);
		priorityHorLayout.setSpacing(true);
		FormLayout formLayoutChk = new FormLayout(priorityHorLayout);
		
		FormLayout formLayoutLeft = new FormLayout(txtIntimationNo,cbxCPUCode,cmbPriority);
	
		FormLayout formLayoutMiddle = new FormLayout(txtPolicyNo,cmbClaimType,cmbType);
		
		FormLayout formLayoutRight = new FormLayout(cbxNetworkHospType,cmbSource,cbxhospitalType/*, cmbPriorityNew*/);
		//Added onChange listener for hospital type in addChangeListenerForHospitalType method.
		addChangeListenerForHospitalType();
		HorizontalLayout fieldLayout = new HorizontalLayout(formLayoutLeft,formLayoutMiddle,formLayoutRight);
		fieldLayout.setComponentAlignment(formLayoutLeft , Alignment.MIDDLE_LEFT);
		
		 
		fieldLayout.setMargin(true);
		fieldLayout.setWidth("100%");		
		 AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		 absoluteLayout_3.addComponent(fieldLayout);
		absoluteLayout_3.addComponent(formLayoutChk,"top:120.0px;left:14.0px;");		
		absoluteLayout_3.addComponent(btnSearch, "top:165.0px;left:370.0px;");
		absoluteLayout_3.addComponent(btnReset, "top:165.0px;left:470.0px;");		
		
		mainVerticalLayout.addComponent(absoluteLayout_3);

		 //Vaadin8-setImmediate() mainVerticalLayout.setImmediate(false);
		 mainVerticalLayout.setWidth("1000px");
		 mainVerticalLayout.setMargin(false);		 
		 //Vaadin8-setImmediate() absoluteLayout_3.setImmediate(false);
		 absoluteLayout_3.setWidth("100.0%");
		 
		 absoluteLayout_3.setHeight("205px");
		
		 addListener();
		 resetListener();
		
		return mainVerticalLayout;
	}
	
	@SuppressWarnings("deprecation")
	private void resetListener() {
		
		btnReset.addClickListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				chkAll.setValue(true);
				chkCRM.setValue(false);
				chkVIP.setValue(false);
				
				/*BeanItemContainer<SelectValue> selectValueForPriorityNew = SHAUtils.getSelectValueForPriorityNew();
				
				cmbPriorityNew.setContainerDataSource(selectValueForPriorityNew);
				cmbPriorityNew.setItemCaptionMode(ItemCaptionMode.PROPERTY);
				cmbPriorityNew.setItemCaptionPropertyId("value");
				cmbPriorityNew.setValue(selectValueForPriorityNew.getItemIds().get(0));*/
			}
		});
		
		chkCRM.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(null != event && null != event.getProperty() && null != event.getProperty().getValue())
				{
					boolean value = (Boolean) event.getProperty().getValue();

					if(value || (chkVIP != null && chkVIP.getValue() != null && chkVIP.getValue().equals(Boolean.TRUE)))
					{
						chkAll.setValue(false);
						chkAll.setEnabled(false);
					}
					else{
						chkAll.setEnabled(true);
					}	 						 
					
				}
			}
		});
		
		chkVIP.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(null != event && null != event.getProperty() && null != event.getProperty().getValue())
				{
					boolean value = (Boolean) event.getProperty().getValue();

					if(value || (chkCRM != null && chkCRM.getValue() != null && chkCRM.getValue().equals(Boolean.TRUE)))
					{
						chkAll.setValue(false);
						chkAll.setEnabled(false);
					}
					else{
						chkAll.setEnabled(true);
					}	 						 
					
				}
			}
		});
		
	}

	private void initBinder()
	{
		this.binder = new BeanFieldGroup<SearchProcessInvestigationInitiatedFormDTO>(SearchProcessInvestigationInitiatedFormDTO.class);
		this.binder.setItemDataSource(new SearchProcessInvestigationInitiatedFormDTO());
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
	}

	public void setCPUCode(BeanItemContainer<SelectValue> parameter,BeanItemContainer<SelectValue> claimTypeContainer, BeanItemContainer<SelectValue> selectValueForPriority, BeanItemContainer<SelectValue> statusByStage) {
		
		cbxCPUCode.setContainerDataSource(parameter);
		cbxCPUCode.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxCPUCode.setItemCaptionPropertyId("value");
		
		cmbClaimType.setContainerDataSource(claimTypeContainer);
		cmbClaimType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbClaimType.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SelectValue> selectValueForType = SHAUtils.getSelectValueForType();
		
		cmbType.setContainerDataSource(selectValueForType);
		cmbType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbType.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SelectValue> selectValueForPriorityIRDA = SHAUtils.getSelectValueForPriorityIRDA();
		cmbPriority.setContainerDataSource(selectValueForPriorityIRDA);
		cmbPriority.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPriority.setItemCaptionPropertyId("value");
		
		/*BeanItemContainer<SelectValue> selectValueForPriorityNew = SHAUtils.getSelectValueForPriorityNew();
		cmbPriorityNew.setContainerDataSource(selectValueForPriorityNew);
		cmbPriorityNew.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPriorityNew.setItemCaptionPropertyId("value");
		cmbPriorityNew.setValue(selectValueForPriorityNew.getItemIds().get(0));*/
		
		cmbSource.setContainerDataSource(statusByStage);
		cmbSource.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbSource.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SelectValue> hospitalType = masterService
				.getMasterValueByReference(ReferenceTable.HOSPITAL_TYPE);
		cbxhospitalType.setContainerDataSource(hospitalType);
		cbxhospitalType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxhospitalType.setItemCaptionPropertyId("value");
		
		networkHospitalType = getNetworkHosTypes();
	}	
	
	
	@SuppressWarnings("serial")
	private void addChangeListenerForHospitalType(){
		cbxhospitalType.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				if(cbxhospitalType.getValue() != null){
					if(ReferenceTable.HOSPITAL_NETWORK.equals(cbxhospitalType.getValue().toString())){
						cbxNetworkHospType.setContainerDataSource(networkHospitalType);
						cbxNetworkHospType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
						cbxNetworkHospType.setItemCaptionPropertyId("value");
				}else{
					cbxNetworkHospType.setContainerDataSource(null);
				}
			}else{
				cbxNetworkHospType.setContainerDataSource(null);
				}
			}
			});
	}
	
	private BeanItemContainer<SelectValue> getNetworkHosTypes(){
		BeanItemContainer<SelectValue> networkHospitalType = masterService
				.getMasterValueByReference(ReferenceTable.NETWORK_HOSPITAL_TYPE);
		return networkHospitalType;
	}
}
