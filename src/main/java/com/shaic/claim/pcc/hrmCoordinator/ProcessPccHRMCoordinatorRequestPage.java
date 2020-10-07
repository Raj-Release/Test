package com.shaic.claim.pcc.hrmCoordinator;

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
import com.shaic.claim.pcc.views.ProcessingDoctorDetailsTable;
import com.shaic.claim.pcc.views.ZonalMedicalDetailsTable;
import com.shaic.claim.pcc.zonalCoordinator.ProcessPCCZonalCoordinatorRequestPresenter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
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

public class ProcessPccHRMCoordinatorRequestPage extends ViewComponent{


	@Inject
	private Instance<ProcessingDoctorDetailsTable> processingDoctorDetailsTableInst;
	private ProcessingDoctorDetailsTable processingDoctorDetailsTable;
	
	@Inject
	private Instance<ZonalMedicalDetailsTable> zonalMedicalDetailsTableInst;
	private ZonalMedicalDetailsTable zonalMedicalDetailsTable;
	
	private PccDTO bean;

	private BeanFieldGroup<PccDTO> binder;

	private ArrayList<Component> mandatoryFields = new ArrayList<Component>();

	private PccDetailsTableDTO pccDetailsTableDTO;
	
	private String presenterString;
	
	private ComboBox cmbSelectRole;
		
	private TextArea txtAssignNegotioanReamrks;

	private ComboBox cmbSelectUserName;
	
	private FormLayout negotiationLayout;
	
	private FormLayout responceLayout;

	private OptionGroup negotiation;
	
	private TextArea txtRmksforNegotiationZonal;
		
	private FormLayout dummyForm;
	
	private TextField dummytext;
	
	private VerticalLayout mainVLayout;
	
	private VerticalLayout dyanamicVLayout;
	
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
		
		processingDoctorDetailsTable = processingDoctorDetailsTableInst.get();
		processingDoctorDetailsTable.init("", false, false);
		processingDoctorDetailsTable.setCaption("Processing Doctor Details");
		if(pccDetailsTableDTO !=null){
			List<PccDetailsTableDTO> pccDetailsTableDTOs = new ArrayList<PccDetailsTableDTO>();
			pccDetailsTableDTOs.add(pccDetailsTableDTO);
			processingDoctorDetailsTable.setTableList(pccDetailsTableDTOs);
		}
		
		zonalMedicalDetailsTable = zonalMedicalDetailsTableInst.get();
		zonalMedicalDetailsTable.init("", false, false);
		zonalMedicalDetailsTable.setCaption("Zonal Medical Details");
		if(pccDetailsTableDTO !=null){
			zonalMedicalDetailsTable.setTableList(pccDetailsTableDTO.getZonalDetails());
		}
		
		cmbSelectRole = binder.buildAndBind("Select Role","userRoleAssigned",ComboBox.class);	
		
		negotiation = (OptionGroup) binder.buildAndBind("Negotiation sucess", "isNegotiation", OptionGroup.class);
		negotiation.addItems(getReadioButtonOptions());
		negotiation.setItemCaption(true, "Yes");
		negotiation.setItemCaption(false, "No");
		negotiation.setStyleName("horizontal");
			
		txtAssignNegotioanReamrks = binder.buildAndBind("Remarks", "remarksNegotioanforZMH", TextArea.class);
		txtAssignNegotioanReamrks.setMaxLength(1000);
		txtAssignNegotioanReamrks.setWidth("278px");
		txtAssignNegotioanReamrks.setHeight("130px");
		txtAssignNegotioanReamrks.setDescription(SHAConstants.F8_POPUP_FOR_TEXTAREA);
		SHAUtils.handleTextAreaPopupDetails(txtAssignNegotioanReamrks,null,getUI(),SHAConstants.STP_REMARKS);
			
        FormLayout appBtnLayout=new FormLayout(negotiation);
        
        dyanamicVLayout = new VerticalLayout();
		dyanamicVLayout.setVisible(false);
		
		mainVLayout = new VerticalLayout(processingDoctorDetailsTable,zonalMedicalDetailsTable,dummytext,appBtnLayout,dyanamicVLayout);
		mainVLayout.setComponentAlignment(appBtnLayout, Alignment.BOTTOM_LEFT);
		mainVLayout.setComponentAlignment(dyanamicVLayout, Alignment.BOTTOM_CENTER);

		addListener();
		setCompositionRoot(mainVLayout);

	}

	private Collection<Boolean> getReadioButtonOptions() {
		Collection<Boolean> coordinatorValues = new ArrayList<Boolean>();
		coordinatorValues.add(true);
		coordinatorValues.add(false);	
		return coordinatorValues;
	}
	

	@SuppressWarnings("deprecation")
	public void addListener() {	
		
		negotiation.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean isChecked = false;
				if(event.getProperty() != null && event.getProperty().getValue() != null && event.getProperty().getValue().toString() == "true") {
					isChecked = true;
				}
				fireViewEvent(ProcessPCCHrmCoordinatorRequestPresenter.HRM_COORDINATOR_GENERATE_NEGOTIATION_APPLICABLE, isChecked);				
			}
		});				
	}


	public void generateNegotiation(Boolean isChecked) {

		if (dyanamicVLayout != null
				&& dyanamicVLayout.getComponentCount() > 0) {
			dyanamicVLayout.removeAllComponents();
		}
		if(responceLayout !=null 
				&& responceLayout.getComponentCount() >0){
			responceLayout.removeAllComponents();
			this.responceLayout = null;
		}
		
		unbindField(cmbSelectRole);
		unbindField(txtAssignNegotioanReamrks);
		
		mandatoryFields.add(cmbSelectRole);
		mandatoryFields.add(txtAssignNegotioanReamrks);
		showOrHideValidation(false);
		
		if(isChecked){
			setRolesByActions("ZC_NEGOTATION_YES");
		}else{
			setRolesByActions("ZC_NEGOTATION_NO");
		}
		
		responceLayout=new FormLayout(cmbSelectRole,txtAssignNegotioanReamrks);
		HorizontalLayout hLyout = new HorizontalLayout(dummyForm,responceLayout);
		dyanamicVLayout.addComponent(hLyout);
		dyanamicVLayout.setVisible(true);	

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
		
		if(negotiation != null && negotiation.getValue() == null){
			hasError=true;
			eMsg.append("Select Negotiation option to proceed further </br> ");
		}
		
		if(negotiation != null && negotiation.getValue() != null){

			if(txtAssignNegotioanReamrks != null && txtAssignNegotioanReamrks.getValue() == null){
				hasError=true;
				eMsg.append("Enter the remarks to proceed further </br> ");
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

	@SuppressWarnings("deprecation")
	public PccDTO getvalues() {

		PccDTO pccDTO = binder.getItemDataSource().getBean();
		pccDTO.setPccKey(pccDetailsTableDTO.getPccKey());
		if(cmbSelectRole.getValue() !=null){
			pccDTO.setUserRoleAssigned((SelectValue)cmbSelectRole.getValue());
		}
		if(negotiation != null && negotiation.getValue() != null){
			pccDTO.setIsNegotiation((Boolean)negotiation.getValue());
		}

		if(txtAssignNegotioanReamrks != null && txtAssignNegotioanReamrks.getValue() != null){
			pccDTO.setRemarksNegotioanforZMH(txtAssignNegotioanReamrks.getValue());
		}

		return pccDTO;

	}

	private void setRolesByActions(String action){

		  ArrayList<String> roles;
		BeanItemContainer<SelectValue> userRoles = null;	

		 if(action.equals("ZC_NEGOTATION_YES")){
			  roles = new ArrayList<String>();
			  roles.add(SHAConstants.PCC_COORDINATOR_ROLE);
			  userRoles = pccRequestService.getPCCRoles(roles);
			  SelectValue selectedrole = pccRequestService.getMasRoleSelectValue(SHAConstants.PCC_COORDINATOR_ROLE);
			  addUserRole(userRoles,selectedrole,false);
		  }else if(action.equals("ZC_NEGOTATION_NO")){
			  roles = new ArrayList<String>();
			  roles.add(SHAConstants.ZONAL_MEDICAL_HEAD_ROLE);
			  userRoles = pccRequestService.getPCCRoles(roles);
			  SelectValue selectedrole = pccRequestService.getMasRoleSelectValue(SHAConstants.ZONAL_MEDICAL_HEAD_ROLE);
			  addUserRole(userRoles,selectedrole,false);
		  }
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

}
