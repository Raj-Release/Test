/**
 * 
 */
package com.shaic.claim.reimbursement.commonBillingFA;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.vaadin.csvalidation.CSValidator;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.SearchComponent;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.fields.dto.SpecialSelectValue;
import com.shaic.domain.MasterService;
import com.shaic.reimbursement.financialapprover.processclaimfinance.search.SearchProcessClaimFinancialsFormDTO;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
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
public class SearchProcessClaimCommonBillingAndFinancialsForm extends SearchComponent<SearchProcessClaimFinancialsFormDTO> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private SearchProcessClaimCommonBillingAndFinancialsTable searchTable;
	
	@EJB
	private MasterService masterService;
	
	private TextField txtIntimationNo;
	/*private TextField txtPolicyNo;
	private ComboBox cbxType;
	private ComboBox cmbCpuCode;
	private ComboBox cmbClaimType;
	private ComboBox cmbProductName;
	private ComboBox cmbPriority;*/
	
//	private ComboBox cmbPriorityNew;
	
	/*private Label priority;
	
	private CheckBox chkAll;
	
	private CheckBox chkCRM;
	
	private CheckBox chkVIP;
	
	private ComboBox cmbSource;
	
	private TextField txtClaimedAmountFrom;
	private TextField txtClaimedAmountTo;*/
	
	@PostConstruct
	public void init() {
		initBinder();
		
		Panel mainPanel = new Panel();
		mainPanel.addStyleName("panelHeader");
		mainPanel.addStyleName("g-search-panel");
		mainPanel.setCaption(SHAConstants.PROCESS_CLAIM_COMMON_BILLING_AND_FA);
		mainPanel.setHeight("160px");
		mainPanel.setContent(mainVerticalLayout());
		setCompositionRoot(mainPanel);
		cbxhospitalListener();
	}
	
	public VerticalLayout mainVerticalLayout(){
		btnSearch.setCaption(SearchComponent.SEARCH_TASK_CAPTION);
		btnSearch.setDisableOnClick(true);
		mainVerticalLayout = new VerticalLayout();
		
		txtIntimationNo = binder.buildAndBind("Intimation No", "intimationNo", TextField.class);
		/*cbxType = binder.buildAndBind("Type","type",ComboBox.class);
		txtPolicyNo = binder.buildAndBind("Policy Number","policyNo",TextField.class);
		cmbCpuCode = binder.buildAndBind("CPU Code","cpuCode",ComboBox.class);
		cmbClaimType = binder.buildAndBind("Claim Type","claimType",ComboBox.class);
		cmbProductName = binder.buildAndBind("Product Name/Code","productName",ComboBox.class);
		cmbPriority = binder.buildAndBind("Priority(IRDA)","priority",ComboBox.class);
		
//		cmbPriorityNew = binder.buildAndBind("Priority","priorityNew",ComboBox.class);
		
		cmbSource = binder.buildAndBind("Source","source",ComboBox.class);
		
		txtClaimedAmountFrom = binder.buildAndBind("Claimed Amount From", "claimedAmountFrom", TextField.class);
		txtClaimedAmountFrom.setNullRepresentation("");
		
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
		
		
//		FormLayout formLayoutLeft = new FormLayout(txtIntimationNo,cbxType,cmbProductName,cmbPriority, txtClaimedAmountFrom,cmbPriorityNew);
//		FormLayout formLayoutReight = new FormLayout(txtPolicyNo,cmbCpuCode,cmbClaimType,cmbSource, txtClaimedAmountTo);	
//		formLayoutReight.setMargin(true);   
		*/
		
		/*FormLayout formLayoutLeft = new FormLayout(txtIntimationNo,cmbCpuCode,cmbPriority);
		FormLayout formLayoutMiddleLeft = new FormLayout(cmbSource,cmbClaimType,cmbProductName);
		FormLayout formLayoutMiddleRight = new FormLayout(txtPolicyNo,cbxType);
		FormLayout formLayoutRight = new FormLayout(txtClaimedAmountFrom,txtClaimedAmountTo);*/
	
		FormLayout formLayoutLeft = new FormLayout(txtIntimationNo);
		
		HorizontalLayout fieldLayout = new HorizontalLayout(formLayoutLeft/*,formLayoutMiddleLeft,formLayoutMiddleRight,formLayoutRight*/);
		fieldLayout.setMargin(true);
		fieldLayout.setWidth("100%");		
		 AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		 absoluteLayout_3.addComponent(fieldLayout);		
//		absoluteLayout_3.addComponent(formLayoutChk,"top:120.0px;left:14.0px;");
		absoluteLayout_3.addComponent(btnSearch, "top:75.0px;left:290.0px;");
		absoluteLayout_3.addComponent(btnReset, "top:75.0px;left:420.0px;");
		
		
		mainVerticalLayout.addComponent(absoluteLayout_3);
		 //Vaadin8-setImmediate() mainVerticalLayout.setImmediate(false);
		 mainVerticalLayout.setWidth("100%");
		// mainVerticalLayout.setHeight("500px");
		 mainVerticalLayout.setMargin(false);		 
		 //Vaadin8-setImmediate() absoluteLayout_3.setImmediate(false);
		 absoluteLayout_3.setWidth("100.0%");
		 
		 absoluteLayout_3.setHeight("100px");
		addListener();
		
		return mainVerticalLayout;
	}
	
	private void initBinder()
	{
		this.binder = new BeanFieldGroup<SearchProcessClaimFinancialsFormDTO>(SearchProcessClaimFinancialsFormDTO.class);
		this.binder.setItemDataSource(new SearchProcessClaimFinancialsFormDTO());
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
		this.binder.getItemDataSource().getBean().setPriorityAll(true);     //Defaulted to True as per FA Screen.
	}

	/*public void setUpDropDownValues(BeanItemContainer<SelectValue> claimType,
			BeanItemContainer<SelectValue> productName1,
			BeanItemContainer<SelectValue> cpuCode, BeanItemContainer<SelectValue> type, BeanItemContainer<SelectValue> statusByStage) {
		
		cmbCpuCode.setContainerDataSource(cpuCode);
		cmbCpuCode.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbCpuCode.setItemCaptionPropertyId("value");
		
		
		cmbClaimType.setContainerDataSource(claimType);
		cmbClaimType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbClaimType.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SpecialSelectValue> productName = masterService.getContainerForProduct();
		
		cmbProductName.setContainerDataSource(productName);
		cmbProductName.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbProductName.setItemCaptionPropertyId("specialValue");
		
		BeanItemContainer<SelectValue> selectValueForType = SHAUtils.getSelectValueForType();
		
		cbxType.setContainerDataSource(selectValueForType);
		cbxType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxType.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SelectValue> selectValueForPriority = SHAUtils.getSelectValueForPriorityIRDA();
		cmbPriority.setContainerDataSource(selectValueForPriority);
		cmbPriority.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPriority.setItemCaptionPropertyId("value");
		
		BeanItemContainer<SelectValue> selectValueForPriorityNew = SHAUtils.getSelectValueForPriorityNew();
		cmbPriorityNew.setContainerDataSource(selectValueForPriorityNew);
		cmbPriorityNew.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPriorityNew.setItemCaptionPropertyId("value");
		cmbPriorityNew.setValue(selectValueForPriorityNew.getItemIds().get(0));
		
		cmbSource.setContainerDataSource(statusByStage);
		cmbSource.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbSource.setItemCaptionPropertyId("value");

	}*/	
	
private void cbxhospitalListener(){
	
	btnReset.addClickListener(new Button.ClickListener() {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			refresh();
			
			/*chkAll.setValue(true);
			chkCRM.setValue(false);
			chkVIP.setValue(false);*/
			
			/*BeanItemContainer<SelectValue> selectValueForPriorityNew = SHAUtils.getSelectValueForPriorityNew();
			
			cmbPriorityNew.setContainerDataSource(selectValueForPriorityNew);
			cmbPriorityNew.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			cmbPriorityNew.setItemCaptionPropertyId("value");
			cmbPriorityNew.setValue(selectValueForPriorityNew.getItemIds().get(0));*/
		}
	});
	
	/*chkCRM.addValueChangeListener(new ValueChangeListener() {

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
	});*/
	
}

}