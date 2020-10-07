package com.shaic.claim.pcc.zonalMedicalHead;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.ViewComponent;

import com.galaxyalert.utils.GalaxyAlertBox;
import com.galaxyalert.utils.GalaxyButtonTypesEnum;
import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.pcc.SearchProcessPCCRequestService;
import com.shaic.claim.pcc.dto.PccDTO;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;
import com.shaic.claim.pcc.views.ProcessingDoctorDetailsTable;
import com.shaic.claim.pcc.views.QueryDetailsTable;
import com.shaic.claim.pcc.views.ReplyDetailsTable;
import com.shaic.claim.pcc.wizard.ProcessPccCoOrdinateRequestPresenter;
import com.shaic.claim.preauth.PreauthWizardPresenter;
import com.shaic.domain.ReferenceTable;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;

public class ProcessPccZonalMedicalHeadRequestPage extends ViewComponent {



	@Inject
	private Instance<ProcessingDoctorDetailsTable> processingDoctorDetailsTableInst;
	private ProcessingDoctorDetailsTable processingDoctorDetailsTable;
	
	@Inject
	private Instance<QueryDetailsTable> queryDetailsTableInst;
	private QueryDetailsTable queryDetailsTable;

	@Inject
	private Instance<ReplyDetailsTable> replyDetailsTableInst;
	private ReplyDetailsTable replyDetailsTable;

	private Button submitBtn;
	private Button cancelBtn;

	private PccDTO bean;

	private BeanFieldGroup<PccDTO> binder;

	private ArrayList<Component> mandatoryFields = new ArrayList<Component>();

	private PccDetailsTableDTO pccDetailsTableDTO;
	
	private String presenterString;
	
	private ComboBox cmbSelectRole;
	
	private TextArea txtAssignReamrks;
	private TextArea txtAssignNegotioanReamrks;

	private Boolean isResponse = false;
	private Boolean isAssign = false;
	
	private Button btnResponse;
	private Button btnAssign;

    private VerticalLayout dyanamicVLayout;
	
	private VerticalLayout mainVLayout;
	
	private FormLayout responseLayout;
	private FormLayout assignLayout;
	private FormLayout negotiationLayout;
	
	private OptionGroup isNegotiation;
	
	private FormLayout dummyForm;
	private TextField dummytext;
	
	private BeanItemContainer<SelectValue> userDetailsContainer;
	
	@EJB
	private ZonalMedicalHeadRequestService zonalMedicalHeadRequestService;
	
	@EJB
	private SearchProcessPCCRequestService pccRequestService;

	@PostConstruct
	protected void initView() {

	}

	public void init(PccDetailsTableDTO pccDetailsTableDTO, String presenterString) {
		
		this.presenterString=presenterString;
		this.pccDetailsTableDTO = pccDetailsTableDTO;
		
		this.binder = new BeanFieldGroup<PccDTO>(PccDTO.class);
		this.binder.setItemDataSource(new PccDTO());
		binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
		
		dummyForm = new FormLayout();
		dummyForm.setWidth("500px");
		dummyForm.setHeight("45px");
		
		dummytext = new TextField();
		dummytext.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		dummytext.setWidth("-1px");
		dummytext.setHeight("-10px");
		
		cmbSelectRole = binder.buildAndBind("Select Role","userRoleAssigned",ComboBox.class);	

		txtAssignReamrks = binder.buildAndBind("Remarks", "remarksAssignforZMH", TextArea.class);
		txtAssignReamrks.setMaxLength(4000);
		txtAssignReamrks.setWidth("278px");
		txtAssignReamrks.setHeight("130px");
		txtAssignReamrks.setDescription(SHAConstants.F8_POPUP_FOR_TEXTAREA);
		SHAUtils.handleTextAreaPopupDetails(txtAssignReamrks,null,getUI(),SHAConstants.STP_REMARKS);
		
		
		txtAssignNegotioanReamrks = binder.buildAndBind("Negotioation Remarks", "remarksNegotioanforZMH", TextArea.class);
		txtAssignNegotioanReamrks.setMaxLength(1000);
		txtAssignNegotioanReamrks.setWidth("278px");
		txtAssignNegotioanReamrks.setHeight("130px");
		txtAssignNegotioanReamrks.setDescription(SHAConstants.F8_POPUP_FOR_TEXTAREA);
		SHAUtils.handleTextAreaPopupDetails(txtAssignNegotioanReamrks,null,getUI(),SHAConstants.STP_REMARKS);
		
	
		isNegotiation = (OptionGroup) binder.buildAndBind("Is Negotiation sucessful", "isNegotiation", OptionGroup.class);
		isNegotiation.addItems(getReadioButtonOptions());
		isNegotiation.setItemCaption(true, "Yes");
		isNegotiation.setItemCaption(false, "No");
		isNegotiation.setStyleName("horizontal");
		
		processingDoctorDetailsTable = processingDoctorDetailsTableInst.get();
		processingDoctorDetailsTable.init("", false, false);
		processingDoctorDetailsTable.setCaption("Processing Doctor Details");
		if(pccDetailsTableDTO !=null){
			List<PccDetailsTableDTO> pccDetailsTableDTOs = new ArrayList<PccDetailsTableDTO>();
			pccDetailsTableDTOs.add(pccDetailsTableDTO);
			processingDoctorDetailsTable.setTableList(pccDetailsTableDTOs);
		}
		
		queryDetailsTable = queryDetailsTableInst.get();
		queryDetailsTable.init("", false, false);
		queryDetailsTable.setCaption("Query Details");
		if(pccDetailsTableDTO.getQueryDetails() !=null 
				&& !pccDetailsTableDTO.getQueryDetails().isEmpty()){
			queryDetailsTable.setTableList(pccDetailsTableDTO.getQueryDetails());
		}
		
		replyDetailsTable = replyDetailsTableInst.get();
		replyDetailsTable.init("", false, false);
		replyDetailsTable.setCaption("Reply Details");
		if(pccDetailsTableDTO.getReplyDetails() !=null 
				&& !pccDetailsTableDTO.getReplyDetails().isEmpty()){
			replyDetailsTable.setTableList(pccDetailsTableDTO.getReplyDetails());
		}
		
		
		btnResponse=new Button("Response");
		btnAssign=new Button("Assign");
			
		HorizontalLayout appBtnLayout=new HorizontalLayout(btnResponse,btnAssign);
		appBtnLayout.setSpacing(true);
		appBtnLayout.setMargin(false);
		
		dyanamicVLayout = new VerticalLayout();
		dyanamicVLayout.setVisible(false);
		
		mainVLayout = new VerticalLayout(processingDoctorDetailsTable,queryDetailsTable,replyDetailsTable,dummytext,appBtnLayout,dyanamicVLayout);	
		mainVLayout.setComponentAlignment(appBtnLayout, Alignment.BOTTOM_LEFT);
		mainVLayout.setComponentAlignment(dyanamicVLayout, Alignment.BOTTOM_CENTER);

		addListener();
		setCompositionRoot(mainVLayout);

	}

	private Collection<Boolean> getReadioButtonOptions() {
		Collection<Boolean> coordinatorValues = new ArrayList<Boolean>(2);
		coordinatorValues.add(true);
		coordinatorValues.add(false);
		
		return coordinatorValues;
	}
	

	@SuppressWarnings("deprecation")
	public void addListener() {
		
	isNegotiation.addValueChangeListener(new Property.ValueChangeListener() {
		
		@Override
		public void valueChange(ValueChangeEvent event) {

			Boolean isChecked = false;
			Boolean isChangesneed = true;
			if(event.getProperty() != null && event.getProperty().getValue() != null && event.getProperty().getValue().toString() == "true") {
				isChecked = true;
			}
			if(event.getProperty() != null && event.getProperty().getValue() != null) {
				fireViewEvent(ProcessPCCZonalMedicalHeadRequestPresenter.ZONAL_MEDICAL_HEAD_GENERATE_NEGOTIATION_APPLICABLE, isChecked);
			}
		}
	});
		
	btnResponse.addClickListener(new Button.ClickListener() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			isResponse = true;
			isAssign = false;
			fireViewEvent(ProcessPCCZonalMedicalHeadRequestPresenter.ZONAL_MEDICAL_HEAD_GENERATE_RESPONSE_LAYOUT,null);
		}
	});
	
	
	btnAssign.addClickListener(new Button.ClickListener() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			isAssign = true;
			isResponse = false;
			fireViewEvent(ProcessPCCZonalMedicalHeadRequestPresenter.ZONAL_MEDICAL_HEAD_GENERATE_ASSIGN_LAYOUT,null);
		}
	});		

}

	protected void showOrHideValidation(Boolean isVisible) {
		for (Component component : mandatoryFields) {
			AbstractField<?> field = (AbstractField<?>) component;
			field.setRequired(!isVisible);
			field.setValidationVisible(isVisible);
		}
	}

	
	private void unbindField(Field<?> field) {
		if (field != null ) {
			Object propertyId = this.binder.getPropertyId(field);
			if (field!= null && field.isAttached() && propertyId != null) {
				field.setValue(null);
				this.binder.unbind(field);
			}
		}
	}

	public Boolean validatePage() {
		
		Boolean hasError=false;
		StringBuffer eMsg = new StringBuffer(); 
		
		if(isResponse){
			if(cmbSelectRole != null && cmbSelectRole.getValue() == null){			
				hasError=true;
				eMsg.append("Select Role to proceed. </br> ");
			}
			if(txtAssignNegotioanReamrks != null && txtAssignNegotioanReamrks.getValue() == null){
				hasError=true;
				eMsg.append("Enter Negotiation Remarks for Approve to proceed </br> ");
			}
			if(isNegotiation != null && isNegotiation.getValue() == null){
				hasError=true;
				eMsg.append("Select Negotiation to proceed. </br> ");
			}
		}
		if(isAssign){
			if(cmbSelectRole != null && cmbSelectRole.getValue() == null){			
				hasError=true;
				eMsg.append("Select Role to proceed. </br> ");
			}
			
			if(txtAssignReamrks != null && txtAssignReamrks.getValue() == null){
				hasError=true;
				eMsg.append("Enter the Remarks to proceed further </br> ");
			}
		}
		
		if(hasError){
			HashMap<String, String> buttonsNamewithType = new HashMap<String, String>();
			buttonsNamewithType.put(GalaxyButtonTypesEnum.OK.toString(), "OK");
			GalaxyAlertBox.createErrorBox(eMsg.toString(), buttonsNamewithType);
			hasError = true;
			return !hasError;
		}
		
		return !hasError;
	}

	public PccDTO getvalues() {
		
		
			PccDTO pccDTO = binder.getItemDataSource().getBean();
			pccDTO.setPccKey(pccDetailsTableDTO.getPccKey());
			pccDTO.setIsAssign(isAssign);
			pccDTO.setIsResponse(isResponse);
			if(cmbSelectRole !=null && cmbSelectRole.getValue() !=null){
				pccDTO.setUserRoleAssigned((SelectValue)cmbSelectRole.getValue());
			}
			if(isResponse){
				
				if(txtAssignNegotioanReamrks != null && txtAssignNegotioanReamrks.getValue() != null){
					pccDTO.setRemarksNegotioanforZMH(txtAssignNegotioanReamrks.getValue());
				}
				if(isNegotiation != null && isNegotiation.getValue() != null){
					pccDTO.setIsNegotiation((Boolean)isNegotiation.getValue());
				}
			}
			if(isAssign){
				if(txtAssignReamrks != null && txtAssignReamrks.getValue() != null){
					pccDTO.setRemarksAssignforZMH(txtAssignReamrks.getValue());
				}
			}
			return pccDTO;
	}

	private void addUserRole(BeanItemContainer<SelectValue> userRoles,SelectValue selectedrole,Boolean isEnabel) {

		unbindField(cmbSelectRole);
		cmbSelectRole.setContainerDataSource(userRoles);
		cmbSelectRole.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbSelectRole.setItemCaptionPropertyId("value");
		cmbSelectRole.setEnabled(isEnabel);
		
		if(selectedrole !=null){
			cmbSelectRole.setValue(selectedrole);
		}
		
	}

	public void generateResponseLayout() {

		unbindandRemoveComponents();
		
		showOrHideValidation(false);
		negotiationLayout=new FormLayout(isNegotiation);
		HorizontalLayout hLyout = new HorizontalLayout(negotiationLayout);
		dyanamicVLayout.addComponent(hLyout);
		dyanamicVLayout.setVisible(true);		
	}

	public void generateAssignLayout() {

		unbindandRemoveComponents();
		
		mandatoryFields.add(txtAssignReamrks);
		showOrHideValidation(false);
		
		dummyForm.setWidth("500px");
		setRolesByActions("ZMH_ASIGN");
		
		assignLayout=new FormLayout(cmbSelectRole,txtAssignReamrks);
		HorizontalLayout hLyout = new HorizontalLayout(dummyForm,assignLayout);
		dyanamicVLayout.addComponent(hLyout);
		dyanamicVLayout.setVisible(true);
		
	}

	public void generateNegotiation(Boolean isChecked) {
		
		unbindandRemoveComponents();
		mandatoryFields.add(cmbSelectRole);
		mandatoryFields.add(txtAssignNegotioanReamrks);
		
		showOrHideValidation(false);
		if(isChecked){
			setRolesByActions("ZMH_NEGOTATION_YES");
		}else{
			setRolesByActions("ZMH_NEGOTATION_NO");
		}
		dummyForm.setWidth("186px");
		negotiationLayout=new FormLayout(isNegotiation);
		responseLayout=new FormLayout(cmbSelectRole,txtAssignNegotioanReamrks);
		HorizontalLayout hLyout = new HorizontalLayout(negotiationLayout,dummyForm,responseLayout);
		dyanamicVLayout.addComponent(hLyout);
		dyanamicVLayout.setVisible(true);	
		
		
	}
	
	private void unbindandRemoveComponents(){

		if (dyanamicVLayout != null
				&& dyanamicVLayout.getComponentCount() > 0) {
			dyanamicVLayout.removeAllComponents();
		}
		if(assignLayout !=null 
				&& assignLayout.getComponentCount() >0){
			assignLayout.removeAllComponents();
			this.assignLayout = null;
		}	
		if(responseLayout !=null 
				&& responseLayout.getComponentCount() >0){
			responseLayout.removeAllComponents();
			this.responseLayout = null;
		}
		if(negotiationLayout !=null 
				&& negotiationLayout.getComponentCount() >0){
			negotiationLayout.removeAllComponents();
			this.negotiationLayout = null;
		}
	}

	private void setRolesByActions(String action){

		  ArrayList<String> roles;
		BeanItemContainer<SelectValue> userRoles = null;	

		 if(action.equals("ZMH_NEGOTATION_YES")){
			  roles = new ArrayList<String>();
			  roles.add(SHAConstants.PCC_COORDINATOR_ROLE);
			  userRoles = pccRequestService.getPCCRoles(roles);
			  SelectValue selectedrole = pccRequestService.getMasRoleSelectValue(SHAConstants.PCC_COORDINATOR_ROLE);
			  addUserRole(userRoles,selectedrole,false);
		  }else if(action.equals("ZMH_NEGOTATION_NO")){
			  roles = new ArrayList<String>();
			  roles.add(SHAConstants.PCC_PROCESSOR_ROLE);
			  userRoles = pccRequestService.getPCCRoles(roles);
			  SelectValue selectedrole = pccRequestService.getMasRoleSelectValue(SHAConstants.PCC_PROCESSOR_ROLE);
			  addUserRole(userRoles,selectedrole,false);
		  }else if(action.equals("ZMH_ASIGN")){
			  roles = new ArrayList<String>();
			  roles.add(SHAConstants.ZONAL_COORDINATOR_ROLE);
			  roles.add(SHAConstants.HRM_COORDINATOR_ROLE);
			  userRoles = pccRequestService.getPCCRoles(roles);
			  addUserRole(userRoles,null,true);
		  }
	  }




}
