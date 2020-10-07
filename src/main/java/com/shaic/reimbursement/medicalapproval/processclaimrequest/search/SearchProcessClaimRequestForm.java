/**
 * 
 */
package com.shaic.reimbursement.medicalapproval.processclaimrequest.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.vaadin.csvalidation.CSValidator;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.SearchComponent;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.fields.dto.SpecialSelectValue;
import com.shaic.domain.MasterService;
import com.shaic.domain.ReferenceTable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

/**
 * @author ntv.narenj
 *
 */
public class SearchProcessClaimRequestForm extends SearchComponent<SearchProcessClaimRequestFormDTO> {
	
	@EJB
	private MasterService masterService;
	
	
	private TextField txtIntimationNo;
	private TextField txtPolicyNo;
	private ComboBox cbxhospitalType;
	private ComboBox cbxType;
	private ComboBox cbxIntimationSource;
	private ComboBox cbxNetworkHospType;
	private ComboBox cbxTreatmentType;
	private ComboBox cbxSpeciality;
	private ComboBox cmbCpuCode;
	private ComboBox cmbProductName;
	private ComboBox cmbPriority;
//	private ComboBox cmbPriorityNew;
	private Label priority;
	private CheckBox chkAll;
	private CheckBox chkCRM;
	private CheckBox chkVIP;
	private ComboBox cmbSource;
	private TextField txtClaimedAmountFrom;
	private TextField txtClaimedAmountTo;
	private ComboBox cmbRequestBy;
	private ComboBox cmbClaimType;
	private ComboBox cmbType;
	private String screenName;
	
	private BeanItemContainer<SelectValue> networkHospitalType;
	private BeanItemContainer<SelectValue> Speicalityvalue ;
	
	private SearchProcessClaimRequestFormDTO dto =  new SearchProcessClaimRequestFormDTO();
	
	Panel mainPanel = null;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void initView(SearchProcessClaimRequestFormDTO dto, Boolean shouldDoSearch) {
		initBinder(dto);
		
		mainPanel = new Panel();
		mainPanel.addStyleName("panelHeader");
		mainPanel.addStyleName("g-search-panel");
		mainPanel.setContent(mainVerticalLayout());
		mainPanel.setHeight("255px");
		setCompositionRoot(mainPanel);
		cbxhospitalListener();
	}
	
	public VerticalLayout mainVerticalLayout(){
		btnSearch.setCaption(SearchComponent.SEARCH_TASK_CAPTION);
		btnSearch.setDisableOnClick(true);
	//	btnSearch.setStyleName("hover");
		mainVerticalLayout = new VerticalLayout();
		
		txtIntimationNo = binder.buildAndBind("Intimation No", "intimationNo", TextField.class);
		
		txtPolicyNo = binder.buildAndBind("Policy Number","policyNo",TextField.class);
		
		cbxhospitalType = binder.buildAndBind("Hospital Type","hospitalType",ComboBox.class);
		
		cbxType = binder.buildAndBind("Type","type",ComboBox.class);
//		cbxType.setEnabled(false);
		
		cbxIntimationSource = binder.buildAndBind("Intimation Source","intimationSource",ComboBox.class);
		
		cbxNetworkHospType = binder.buildAndBind("Network Hosp Type","networkHospType",ComboBox.class);
		
		cbxTreatmentType = binder.buildAndBind("Treatment Type","treatementType",ComboBox.class);
		
		cbxSpeciality = binder.buildAndBind("Speciality","speciality",ComboBox.class);
		
		cmbCpuCode = binder.buildAndBind("CPU Code","cpuCode",ComboBox.class);
		
		cmbProductName = binder.buildAndBind("Product Name/Code","productName",ComboBox.class);
		
		cmbPriority = binder.buildAndBind("Priority(IRDA)","priority",ComboBox.class);
		
//		cmbPriorityNew = binder.buildAndBind("Priority","priorityNew",ComboBox.class);
		
		cmbSource = binder.buildAndBind("Source","source",ComboBox.class);
		
		cmbClaimType = binder.buildAndBind("Claim Type","claimType",ComboBox.class);
		
		cmbRequestBy = binder.buildAndBind("Requested User","requestedBy",ComboBox.class);
		
		txtClaimedAmountFrom = binder.buildAndBind("Claimed Amount From", "claimedAmountFrom", TextField.class);
		txtClaimedAmountFrom.setNullRepresentation("");
		
		cmbType = binder.buildAndBind("Type","pendingStatusType",ComboBox.class);
		cmbType.setId(SHAConstants.COMBOBOX_NOT_RESET);
		
		CSValidator validator = new CSValidator();
		validator.extend(txtClaimedAmountFrom);
		validator.setRegExp("^[0-9.]*$");
		validator.setPreventInvalidTyping(true);
		
		txtClaimedAmountTo = binder.buildAndBind("Claimed Amount To", "claimedAmountTo", TextField.class);
		txtClaimedAmountTo.setNullRepresentation("");
		
		CSValidator validator1 = new CSValidator();
		validator1.extend(txtClaimedAmountTo);
		validator1.setRegExp("^[0-9.]*$");
		validator1.setPreventInvalidTyping(true);
		
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
		
		FormLayout formLayout1 = new FormLayout(txtIntimationNo,cmbCpuCode,cbxIntimationSource,cbxhospitalType);
		formLayout1.setMargin(false);
		FormLayout formLayout2 = new FormLayout(cmbSource,cmbRequestBy,cmbClaimType,cmbProductName);	
		formLayout2.setMargin(false);
		FormLayout formLayout3 = new FormLayout(txtPolicyNo,cbxNetworkHospType,cbxTreatmentType,cbxSpeciality);
		formLayout3.setMargin(false);
		FormLayout formLayout4 = new FormLayout(txtClaimedAmountFrom,txtClaimedAmountTo,cmbPriority,cbxType/*,cmbPriorityNew*/);
		formLayout4.setMargin(false);
		
		HorizontalLayout fieldLayout = new HorizontalLayout(formLayout1,formLayout2,formLayout3,formLayout4);	
		
		cbxhospitalListener();	

		fieldLayout.setMargin(true);
		fieldLayout.setWidth("100%");		
		 AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		 absoluteLayout_3.addComponent(fieldLayout);	
		absoluteLayout_3.addComponent(formLayoutChk,"top:125.0px;left:14.0px;");	
		absoluteLayout_3.addComponent(btnSearch, "top:160.0px;left:260.0px;");
		absoluteLayout_3.addComponent(btnReset, "top:160.0px;left:370.0px;");
		
		
		mainVerticalLayout.addComponent(absoluteLayout_3);
		 //Vaadin8-setImmediate() mainVerticalLayout.setImmediate(false);
		 mainVerticalLayout.setWidth("1500px");
		// mainVerticalLayout.setHeight("500px");
		 mainVerticalLayout.setMargin(false);		 
		 //Vaadin8-setImmediate() absoluteLayout_3.setImmediate(false);
		 absoluteLayout_3.setWidth("100.0%");
		 
		 absoluteLayout_3.setHeight("220px");
		
		addListener();
		
		return mainVerticalLayout;
	}
	
	private void initBinder(SearchProcessClaimRequestFormDTO dto)
	{
		if(dto != null) {
			this.dto = dto;
		} else {
			this.dto = new SearchProcessClaimRequestFormDTO();
		}
		
		this.binder = new BeanFieldGroup<SearchProcessClaimRequestFormDTO>(SearchProcessClaimRequestFormDTO.class);
		this.binder.setItemDataSource(this.dto);
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
	}

	public void setCBXValue(BeanItemContainer<SelectValue> intimationSource,
			BeanItemContainer<SelectValue> hospitalType,
			BeanItemContainer<SelectValue> networkHospitalType,
			BeanItemContainer<SelectValue> treatementType, BeanItemContainer<SelectValue> typeContainer, 
			BeanItemContainer<SelectValue> productName1, BeanItemContainer<SelectValue> cpuCode, BeanItemContainer<SelectValue> statusByStage,
			BeanItemContainer<SelectValue> claimType,String screenName) {
		
		if(null != screenName && (SHAConstants.MEDICAL_PENDING_SCREEN.equalsIgnoreCase(screenName))){
			
			mainPanel.setCaption("Process Claim Request");
		}
		else if(null != screenName && (SHAConstants.WAIT_FOR_INPUT_SCREEN.equalsIgnoreCase(screenName)))
		{
			mainPanel.setCaption("Wait For Input");
		}

		cbxIntimationSource.setContainerDataSource(intimationSource);
		cbxIntimationSource.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxIntimationSource.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getIntimationSource() != null) {
			cbxIntimationSource.setValue(this.dto.getIntimationSource());
		}
		cbxhospitalType.setContainerDataSource(hospitalType);
		cbxhospitalType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxhospitalType.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getHospitalType() != null) {
			cbxhospitalType.setValue(this.dto.getHospitalType());
		}
		cbxTreatmentType.setContainerDataSource(treatementType);
		cbxTreatmentType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxTreatmentType.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getTreatementType() != null) {
			cbxTreatmentType.setValue(this.dto.getTreatementType());
		}
		BeanItemContainer<SelectValue> selectValueForType = SHAUtils.getSelectValueForType();
		
		BeanItemContainer<SpecialSelectValue> productName = masterService.getContainerForProduct();
		
		String userName=(String)getUI().getSession().getAttribute(BPMClientContext.USERID);
		
		BeanItemContainer<SelectValue> employeeLoginNameContainer = masterService.getEmployeeLoginContainer(userName);
		
		cmbClaimType.setContainerDataSource(claimType);
		cmbClaimType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbClaimType.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getClaimType() != null) {
			cmbClaimType.setValue(this.dto.getClaimType());
		}
		
		cbxType.setContainerDataSource(selectValueForType);
		cbxType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxType.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getType() != null) {
			cbxType.setValue(this.dto.getType());
		}
		
		cmbCpuCode.setContainerDataSource(cpuCode);
		cmbCpuCode.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbCpuCode.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getCpuCode() != null) {
			cmbCpuCode.setValue(this.dto.getCpuCode());
		}
		
		cmbProductName.setContainerDataSource(productName);
		cmbProductName.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbProductName.setItemCaptionPropertyId("specialValue");
		if(this.dto != null && this.dto.getProductName() != null) {
			cmbProductName.setValue(this.dto.getProductName());
		}
		
		BeanItemContainer<SelectValue> selectValueForPriority = SHAUtils.getSelectValueForPriorityIRDA();
		cmbPriority.setContainerDataSource(selectValueForPriority);
		cmbPriority.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPriority.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getPriority() != null) {
			cmbPriority.setValue(this.dto.getPriority());
		}
		
		/*BeanItemContainer<SelectValue> selectValueForPriorityNew = SHAUtils.getSelectValueForPriorityNew();
		
		cmbPriorityNew.setContainerDataSource(selectValueForPriorityNew);
		cmbPriorityNew.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPriorityNew.setItemCaptionPropertyId("value");
		cmbPriorityNew.setValue(selectValueForPriorityNew.getItemIds().get(0));*/
		
		cmbSource.setContainerDataSource(statusByStage);
		cmbSource.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbSource.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getSource() != null) {
			cmbSource.setValue(this.dto.getSource());
		}
		
		cmbRequestBy.setContainerDataSource(employeeLoginNameContainer);
		cmbRequestBy.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbRequestBy.setItemCaptionPropertyId("value");
		if(this.dto != null && this.dto.getRequestedBy() != null) {
			cmbRequestBy.setValue(this.dto.getRequestedBy());
		}
		
		SelectValue recordType = new SelectValue();
		
		if(null != screenName && (SHAConstants.WAIT_FOR_INPUT_SCREEN.equalsIgnoreCase(screenName))){
			
			recordType.setId(51l);
			recordType.setValue(SHAConstants.PARALLEL_WAITING_FOR_INPUT);
		}
		else if(null != screenName && (SHAConstants.MEDICAL_PENDING_SCREEN.equalsIgnoreCase(screenName))){

			recordType.setId(49l);
			recordType.setValue(SHAConstants.PARALLEL_MEDICAL_PENDING);
		}
		

		List<SelectValue> selectVallueList = new ArrayList<SelectValue>();
		selectVallueList.add(recordType);
		
		BeanItemContainer<SelectValue> selectValueContainer = new BeanItemContainer<SelectValue>(SelectValue.class);
		selectValueContainer.addAll(selectVallueList);
		cmbType.setContainerDataSource(selectValueContainer);
		cmbType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbType.setItemCaptionPropertyId("value");
		cmbType.setValue(recordType);
		cmbType.setId(SHAConstants.COMBOBOX_NOT_RESET);
		
		employeeLoginNameContainer.sort(new Object[] {"value"}, new boolean[] {true});
		
		statusByStage.sort(new Object[] {"value"}, new boolean[] {true});

		this.networkHospitalType = networkHospitalType;
	}	
	private void cbxhospitalListener(){
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
		
		cbxTreatmentType.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
			
				if(cbxTreatmentType.getValue() != null ){
					if(ReferenceTable.MEDICAL.equals(cbxTreatmentType.getValue().toString())){
					
						fireViewEvent(SearchProcessClaimRequestPresenter.SPECIALITY, ReferenceTable.MEDICAL_CODE );
						
						cbxSpeciality.setContainerDataSource(Speicalityvalue);
						cbxSpeciality.setItemCaptionMode(ItemCaptionMode.PROPERTY);
						cbxSpeciality.setItemCaptionPropertyId("value");
					}else if(ReferenceTable.SURGICAL.equals(cbxTreatmentType.getValue().toString())){
					
						fireViewEvent(SearchProcessClaimRequestPresenter.SPECIALITY,ReferenceTable.SURGICAL_CODE );
						
						cbxSpeciality.setContainerDataSource(Speicalityvalue);
						cbxSpeciality.setItemCaptionMode(ItemCaptionMode.PROPERTY);
						cbxSpeciality.setItemCaptionPropertyId("value");
					}
					
				}else{
					cbxSpeciality.setContainerDataSource(null);
				}
			}
		});
		
	cmbSource.addValueChangeListener(new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
			
				if(cmbSource.getValue() != null ) {
					SelectValue value = (SelectValue) cmbSource.getValue();
					if(value != null && ReferenceTable.getReplyReceivedStatus().containsKey(value.getId())) {
						if(cmbRequestBy != null && !cmbRequestBy.getItemIds().isEmpty()) {
							cmbRequestBy.setValue(cmbRequestBy.getItemIds().toArray()[0]);
						}
					} else {
						cmbRequestBy.setValue(null);
					}
				}
			}
		});
	
	btnReset.addClickListener(new Button.ClickListener() {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			refresh();
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

	public void setSpecialityCBX(
			BeanItemContainer<SelectValue> specialityValueByReference) {
		Speicalityvalue = specialityValueByReference;
		
	}
}