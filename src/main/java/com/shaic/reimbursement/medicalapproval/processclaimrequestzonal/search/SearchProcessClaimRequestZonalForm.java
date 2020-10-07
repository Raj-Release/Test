/**
 * 
 */
package com.shaic.reimbursement.medicalapproval.processclaimrequestzonal.search;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.SearchComponent;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.fields.dto.SpecialSelectValue;
import com.shaic.claim.preauth.search.SearchPreauthPresenter;
import com.shaic.domain.MasterService;
import com.shaic.domain.ReferenceTable;
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
public class SearchProcessClaimRequestZonalForm extends SearchComponent<SearchProcessClaimRequestZonalFormDTO> {
	
	private static final long serialVersionUID = -4088910426204201267L;

	@Inject
	private SearchProcessClaimRequestZonalTable searchTable;
	
	@EJB
	private MasterService masterService;
	
	private TextField txtIntimationNo;
	private TextField txtPolicyNo;
	private ComboBox cbxhospitalType;
	private ComboBox cbxType;
	private ComboBox cmbCpuCode;
	private ComboBox cmbProductName;
	private ComboBox cbxIntimationSource;
	private ComboBox cbxNetworkHospType;
	private ComboBox cmbPriority;
	private ComboBox cmbSource;
	
	private BeanItemContainer<SelectValue> networkHospitalType;

//	private ComboBox cmbPriorityNew;
	
	private Label priority;
	
	private CheckBox chkAll;
	
	private CheckBox chkCRM;
	
	private CheckBox chkVIP;
	
	@PostConstruct
	public void init() {
		initBinder();
		
		Panel mainPanel = new Panel();
		mainPanel.addStyleName("panelHeader");
		mainPanel.addStyleName("g-search-panel");
		mainPanel.setCaption("Process Claim Request (Zonal Medical Review)");
		mainPanel.setContent(mainVerticalLayout());
		setCompositionRoot(mainPanel);
		cbxhospitalListener();
	}
	
	public VerticalLayout mainVerticalLayout(){
		btnSearch.setCaption(SearchComponent.SEARCH_TASK_CAPTION);
		btnSearch.setDisableOnClick(true);
		mainVerticalLayout = new VerticalLayout();
		
		txtIntimationNo = binder.buildAndBind("Intimation No", "intimationNo", TextField.class);
		
		txtPolicyNo = binder.buildAndBind("Policy Number","policyNo",TextField.class);
		
		cbxhospitalType = binder.buildAndBind("Hospital Type","hospitalType",ComboBox.class);
		//Vaadin8-setImmediate() cbxhospitalType.setImmediate(true);
		
         cmbCpuCode = binder.buildAndBind("CPU Code","cpuCode",ComboBox.class);
		
		cmbProductName = binder.buildAndBind("Product Name/Code","productName",ComboBox.class);

		cbxType = binder.buildAndBind("Type","type",ComboBox.class);
		cmbSource = binder.buildAndBind("Source","source",ComboBox.class);
		
		cbxIntimationSource = binder.buildAndBind("Intimation Source","intimationSource",ComboBox.class);
		
		cbxNetworkHospType = binder.buildAndBind("Network Hosp Type","networkHospType",ComboBox.class);
		cmbPriority = binder.buildAndBind("Priority (IRDA)","priority",ComboBox.class);
		
//		cmbPriorityNew = binder.buildAndBind("Priority","priorityNew",ComboBox.class);
		
		priority = new Label();
		priority.setCaption("Priority");
		
		chkAll = binder.buildAndBind("All","priorityAll",CheckBox.class);
		chkAll.setValue(Boolean.TRUE);
		
		chkCRM = binder.buildAndBind("CRM","crm",CheckBox.class);
		
		chkVIP = binder.buildAndBind("VIP","vip",CheckBox.class);
		cbxhospitalListener();
		
		HorizontalLayout priorityHorLayout = new HorizontalLayout(priority,chkAll,chkCRM,chkVIP);
		priorityHorLayout.setMargin(false);
		priorityHorLayout.setSpacing(true);
		FormLayout formLayoutChk = new FormLayout(priorityHorLayout);
		
		/*FormLayout formLayoutLeft = new FormLayout(txtIntimationNo,txtPolicyNo,cbxhospitalType,cmbProductName,cmbPriority,cmbPriorityNew);
		FormLayout formLayoutReight = new FormLayout(cbxType,cbxIntimationSource,cmbCpuCode,cbxNetworkHospType,cmbSource);*/
		
		FormLayout formLayoutLeft = new FormLayout(txtIntimationNo,txtPolicyNo,cmbCpuCode);
		FormLayout formLayoutMiddleLeft = new FormLayout(cbxType,cbxIntimationSource,cmbProductName);
		FormLayout formLayoutMiddleRight = new FormLayout(cbxhospitalType,cbxNetworkHospType,cmbSource);	
		FormLayout formLayoutRight = new FormLayout(cmbPriority);	
	
		HorizontalLayout fieldLayout = new HorizontalLayout(formLayoutLeft,formLayoutMiddleLeft,formLayoutMiddleRight,formLayoutRight);

		fieldLayout.setMargin(true);
		fieldLayout.setWidth("100%");		
		 AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		 absoluteLayout_3.addComponent(fieldLayout);
		absoluteLayout_3.addComponent(formLayoutChk,"top:120.0px;left:14.0px;");		
		absoluteLayout_3.addComponent(btnSearch, "top:160.0px;left:260.0px;");
		absoluteLayout_3.addComponent(btnReset, "top:160.0px;left:370.0px;");
		
		
		mainVerticalLayout.addComponent(absoluteLayout_3);
		 //Vaadin8-setImmediate() mainVerticalLayout.setImmediate(false);
		 mainVerticalLayout.setWidth("1400px");
		// mainVerticalLayout.setHeight("500px");
		 mainVerticalLayout.setMargin(false);		 
		 //Vaadin8-setImmediate() absoluteLayout_3.setImmediate(false);
		 absoluteLayout_3.setWidth("100.0%");
		 
		 absoluteLayout_3.setHeight("222px");
		 addListener();
		
		return mainVerticalLayout;
	}
	
	private void initBinder()
	{
		this.binder = new BeanFieldGroup<SearchProcessClaimRequestZonalFormDTO>(SearchProcessClaimRequestZonalFormDTO.class);
		this.binder.setItemDataSource(new SearchProcessClaimRequestZonalFormDTO());
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
	}

	public void setCBXValue(BeanItemContainer<SelectValue> intimationSource,
			BeanItemContainer<SelectValue> hospitalType,
			BeanItemContainer<SelectValue> networkHospitalType, BeanItemContainer<SelectValue> typeContainer, BeanItemContainer<SelectValue> selectValueForPriority, BeanItemContainer<SelectValue> statusByStage) {
		cbxIntimationSource.setContainerDataSource(intimationSource);
		cbxIntimationSource.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxIntimationSource.setItemCaptionPropertyId("value");
		
		
		cbxhospitalType.setContainerDataSource(hospitalType);
		cbxhospitalType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxhospitalType.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SelectValue> selectValueForType = SHAUtils.getSelectValueForType();
		
		cbxType.setContainerDataSource(selectValueForType);
		cbxType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxType.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SpecialSelectValue> productName = masterService.getContainerForProduct();
		BeanItemContainer<SelectValue> cpuCode = masterService.getTmpCpuCodes();
		
		cmbCpuCode.setContainerDataSource(cpuCode);
		cmbCpuCode.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbCpuCode.setItemCaptionPropertyId("value");
		
		cmbProductName.setContainerDataSource(productName);
		cmbProductName.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbProductName.setItemCaptionPropertyId("specialValue");
		
		selectValueForPriority = SHAUtils.getSelectValueForPriorityIRDA();
		
		cmbPriority.setContainerDataSource(selectValueForPriority);
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
		
//		cbxNetworkHospType.setContainerDataSource(networkHospitalType);
//		cbxNetworkHospType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
//		cbxNetworkHospType.setItemCaptionPropertyId("value");
		
		this.networkHospitalType = networkHospitalType;
		
	}	
	
	private void cbxhospitalListener(){
		cbxhospitalType.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
			
				
				
				if(cbxhospitalType.getValue() != null){
					System.out.println("ggggggggggggggggggggggg"+cbxhospitalType.getValue());
				if(  ReferenceTable.HOSPITAL_NETWORK.equals(cbxhospitalType.getValue().toString())){
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
	
}