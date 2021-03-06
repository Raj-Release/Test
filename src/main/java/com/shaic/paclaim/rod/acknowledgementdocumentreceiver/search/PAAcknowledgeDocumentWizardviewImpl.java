package com.shaic.paclaim.rod.acknowledgementdocumentreceiver.search;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.teemu.wizards.event.GWizardEvent;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardInitEvent;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.ViewDetails;
import com.shaic.claim.ViewDetails.ViewLevels;
import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.shaic.claim.reimbursement.dto.RRCDTO;
import com.shaic.claim.rod.wizard.dto.ReceiptOfDocumentsDTO;
import com.shaic.claim.rod.wizard.pages.AcknowledgeReceiptViewImpl;
import com.shaic.claim.viewEarlierRodDetails.EarlierRodDetailsViewImpl;
import com.shaic.claim.viewEarlierRodDetails.RewardRecognitionRequestView;
import com.shaic.claim.viewEarlierRodDetails.ViewDocumentDetailsDTO;
import com.shaic.ims.carousel.PARevisedCarousel;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.shaic.main.navigator.ui.Toolbar;
import com.shaic.newcode.wizard.IWizard;
import com.shaic.newcode.wizard.IWizardPartialComplete;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Reindeer;

public class PAAcknowledgeDocumentWizardviewImpl extends AbstractMVPView implements PAAcknowledgeDocumentWizardView{

	
	private static final String DOCUMENT_DETAILS = "Document Details";

	@Inject
	private Instance<PAAcknowledgementDocumentDetailsView> documentDetailsViewImpl;
	
	@Inject
	private Instance<IWizard> iWizard;
	
	@Inject
	private AcknowledgeReceiptViewImpl acknowledgeReceiptViewImpl;
	
/*	@Inject
	private Instance<RevisedCarousel> commonCarouselInstance;*/
	
	@Inject
	private Instance<PARevisedCarousel> commonCarouselInstance;
	
	
	@Inject
	private ViewDetails viewDetails;
	
	@Inject
	private Instance<EarlierRodDetailsViewImpl> earlierRodDetailsViemImplInstance;
	
	private EarlierRodDetailsViewImpl earlierRodDetailsViewObj;
	
	/*@Inject
	private TextBundle tb;*/
	
	private VerticalSplitPanel mainPanel;
	
	private IWizardPartialComplete wizard;
	
	private FieldGroup binder;
	
	private ReceiptOfDocumentsDTO bean;
	
	private PAAcknowledgementDocumentDetailsView documentDetailsView;
//	private String[] arrScreenName ; 
	//private Map<String,String> captionMap ;
	private PreauthDTO preauthDTO;
	
	@Inject
	private Instance<RewardRecognitionRequestView> rewardRecognitionRequestViewInstance;
	
	private RewardRecognitionRequestView rewardRecognitionRequestViewObj;
	
	private RRCDTO rrcDTO;	
	
	@Inject
	private Toolbar toolBar;
	
	//private static Window popup;
	

	
	
	private void initBinder() {
		
		wizard.getFinishButton().setCaption("Submit");
		this.binder = new FieldGroup();
		BeanItem<ReceiptOfDocumentsDTO> item = new BeanItem<ReceiptOfDocumentsDTO>(bean);
		item.addNestedProperty("documentDetails");
		item.addNestedProperty("documentDetails.documentsReceivedFrom");
		item.addNestedProperty("documentDetails.acknowledgmentContactNumber");
		item.addNestedProperty("documentDetails.documentsReceivedDate");
		item.addNestedProperty("documentDetails.emailId");
		item.addNestedProperty("documentDetails.modeOfReceipt");
		item.addNestedProperty("documentDetails.reconsiderationRequest");
		item.addNestedProperty("documentDetails.hospitalization");
		item.addNestedProperty("documentDetails.preHospitalization");
		item.addNestedProperty("documentDetails.postHospitalization");
		item.addNestedProperty("documentDetails.partialHospitalization");
		item.addNestedProperty("documentDetails.lumpSumAmount");
		item.addNestedProperty("documentDetails.addOnBenefitsHospitalCash");
		item.addNestedProperty("documentDetails.addOnBenefitsPatientCare");
		item.addNestedProperty("documentDetails.hospitalizationClaimedAmount");
		item.addNestedProperty("documentDetails.preHospitalizationClaimedAmount");
		item.addNestedProperty("documentDetails.postHospitalizationClaimedAmount");
/*		item.addNestedProperty("documentDetails.documentCheckList");
		item.addNestedProperty("documentDetails.documentCheckList");
		item.addNestedProperty("documentDetails.documentCheckList");
		item.addNestedProperty("documentDetails.documentCheckList");
		item.addNestedProperty("documentDetails.documentCheckList");
		item.addNestedProperty("documentDetails.documentCheckList");
		item.addNestedProperty("documentDetails.documentCheckList");*/
		this.binder.setItemDataSource(item);	
	}
	
	@PostConstruct
	public void initView() {
		addStyleName("view");
		
		setSizeFull();			
	}
	
	public void initView(ReceiptOfDocumentsDTO rodDTO)
	{
		this.bean = rodDTO;
		this.rrcDTO = rodDTO.getRrcDTO();
		
		mainPanel = new VerticalSplitPanel();
		//this.wizard = iWizard.get();
		this.wizard = new IWizardPartialComplete();
		initBinder();	
		documentDetailsView = documentDetailsViewImpl.get();
		documentDetailsView.setNextValue();
		documentDetailsView.init(rodDTO,wizard);
		acknowledgeReceiptViewImpl.init(rodDTO);
		
		preauthDTO = new PreauthDTO();
		preauthDTO.setRrcDTO(rrcDTO);
		//preauthDTO.setRodHumanTask(rrcDTO.getHumanTask());
		
		wizard.addStep(documentDetailsView,DOCUMENT_DETAILS);
		wizard.addStep(this.acknowledgeReceiptViewImpl,"Acknowledgement Receipt");
		wizard.setStyleName(ValoTheme.PANEL_WELL);
		wizard.setSizeFull();
		wizard.addListener(this);	
	//	RevisedCarousel intimationDetailsCarousel = commonCarouselInstance.get();
		PARevisedCarousel intimationDetailsCarousel = commonCarouselInstance.get();
		intimationDetailsCarousel.init(this.bean.getClaimDTO().getNewIntimationDto(), this.bean.getClaimDTO(),  "Acknowledge Receipt of Documents",rodDTO.getDiagnosis());
		mainPanel.setFirstComponent(intimationDetailsCarousel);
		viewDetails.initView(bean.getClaimDTO().getNewIntimationDto().getIntimationId(),0l, ViewLevels.PA_PROCESS,"Acknowledge Receipt of Documents");
		HorizontalLayout hLayout = new HorizontalLayout(commonButtonsLayout(),viewDetails);
		hLayout.setWidth("100%");
		hLayout.setComponentAlignment(viewDetails, Alignment.TOP_RIGHT);
		VerticalLayout wizardLayout1 = new VerticalLayout(hLayout);
		
		Panel panel1 = new Panel();
		panel1.setContent(wizardLayout1);
		//panel1.setHeight("90px");
		VerticalLayout wizardLayout2 = new VerticalLayout(panel1, wizard);
		wizardLayout2.setSpacing(true);
		mainPanel.setSecondComponent(wizardLayout2);
		
		mainPanel.setSizeFull();
		mainPanel.setSplitPosition(30, Unit.PERCENTAGE);
		mainPanel.setHeight("600px");
		
		setCompositionRoot(mainPanel);			
		}

	
	private HorizontalLayout commonButtonsLayout()
	{
		
		TextField acknowledgementNumber = new TextField("Acknowledgement Number");
		acknowledgementNumber.setValue(String.valueOf(this.bean.getAcknowledgementNumber()));
		//Vaadin8-setImmediate() acknowledgementNumber.setImmediate(true);
		acknowledgementNumber.setWidth("250px");
		acknowledgementNumber.setHeight("20px");
		acknowledgementNumber.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		acknowledgementNumber.setReadOnly(true);
		acknowledgementNumber.setEnabled(false);
		acknowledgementNumber.setNullRepresentation("");
		FormLayout hLayout = new FormLayout (acknowledgementNumber);
		hLayout.setComponentAlignment(acknowledgementNumber, Alignment.MIDDLE_LEFT);
		hLayout.setHeight("55px");
		
		Button viewEarlierRODDetails = new Button("View Earlier ROD Details");
		viewEarlierRODDetails.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Window popup = new com.vaadin.ui.Window();
				popup.setCaption("");
				popup.setWidth("75%");
				popup.setHeight("85%");
				earlierRodDetailsViewObj = earlierRodDetailsViemImplInstance.get();
				ViewDocumentDetailsDTO documentDetails =  new ViewDocumentDetailsDTO();
				documentDetails.setClaimDto(bean.getClaimDTO());
				earlierRodDetailsViewObj.init(bean.getClaimDTO().getKey(),null);
				popup.setContent(earlierRodDetailsViewObj);
				popup.setClosable(true);
				popup.center();
				popup.setResizable(false);
				popup.addCloseListener(new Window.CloseListener() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						System.out.println("Close listener called");
					}
				});

				popup.setModal(true);
				UI.getCurrent().addWindow(popup);
				
				
			}
			
		});
		
		Button msgButton = new Button("Message Button");
		msgButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				buildSuccessLayout();
			}
		});
		
		FormLayout viewEarlierRODLayout = new FormLayout(viewEarlierRODDetails);
		
		FormLayout viewDetailsForm = new FormLayout();
		//Vaadin8-setImmediate() viewDetailsForm.setImmediate(true);
		viewDetailsForm.setWidth("-1px");
		viewDetailsForm.setHeight("-1px");
		viewDetailsForm.setMargin(false);
		viewDetailsForm.setSpacing(true);
		//ComboBox viewDetailsSelect = new ComboBox()
		
		  //need to implements
		viewDetailsForm.addComponent(viewDetails);
		
		
	//	viewDetailsForm.addComponent(viewDetailsSelect);
	//	Button goButton = new Button("GO");
		/*HorizontalLayout horizontalLayout1 = new HorizontalLayout(
				viewDetailsForm, goButton);*/
	/*	HorizontalLayout horizontalLayout1 = new HorizontalLayout(
				viewDetailsForm);
		horizontalLayout1.setSizeUndefined();
		//Vaadin8-setImmediate() horizontalLayout1.setImmediate(true);
		horizontalLayout1.setSpacing(true);*/
		
		
//		TextField txtClaimCount = new TextField("Claim Count");
//		txtClaimCount.setValue(this.bean.getClaimCount().toString());
//		txtClaimCount.setReadOnly(true);
//		TextField dummyField = new TextField();
//		dummyField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
//		FormLayout firstForm = new FormLayout(txtClaimCount,dummyField);
//		dummyField.setReadOnly(true);
////		firstForm.setWidth(txtClaimCount.getWidth(), txtClaimCount.getWidthUnits());
//		Panel claimCount = new Panel(firstForm);
//		txtClaimCount.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
//		firstForm.setHeight("50px");
//		firstForm.setComponentAlignment(txtClaimCount, Alignment.TOP_LEFT);
////		txtClaimCount.addStyleName("fail");
////		claimCount.setWidth(txtClaimCount.getWidth(),txtClaimCount.getWidthUnits());
////		
//		if(this.bean.getClaimCount() > 1 && this.bean.getClaimCount() <=2){
//			claimCount.addStyleName("girdBorder1");
//		}else if(this.bean.getClaimCount() >2){
//			claimCount.addStyleName("girdBorder2");
//		}

		Button btnRRC = new Button("RRC");
		btnRRC.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				validateUserForRRCRequestIntiation();
				
			}
			
		});
	
		HorizontalLayout componentsHLayout = new HorizontalLayout(btnRRC, viewEarlierRODDetails);
		componentsHLayout.setSpacing(true);
	
		VerticalLayout vLayout = new VerticalLayout(componentsHLayout ,  hLayout);	
		
		HorizontalLayout alignmentHLayout = new HorizontalLayout(vLayout);
	
	
		return alignmentHLayout;
	}
	
	private void validateUserForRRCRequestIntiation()
	{
		fireViewEvent(PAAcknowledgeDocumentWizardPresenter.VALIDATE_ACK_DOC_REC_USER_RRC_REQUEST, preauthDTO);//, secondaryParameters);
	}
	
	
	private void alertMessageForClaimCount(Long claimCount){
		
		String msg = SHAConstants.CLAIM_COUNT_MESSAGE+claimCount;
		
		
   		Label successLabel = new Label(
				"<b style = 'color: black;'>"+msg+"</b>",
				ContentMode.HTML);
//   		successLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
//   		successLabel.addStyleName(ValoTheme.LABEL_BOLD);
   		successLabel.addStyleName(ValoTheme.LABEL_H3);
   		Button homeButton = new Button("ok");
		homeButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
   		FormLayout firstForm = new FormLayout(successLabel,homeButton);
		Panel panel = new Panel(firstForm);
		
		if(this.bean.getClaimCount() > 1 && this.bean.getClaimCount() <=2){
			panel.addStyleName("girdBorder1");
		}else if(this.bean.getClaimCount() >2){
			panel.addStyleName("girdBorder2");
		}
		
		panel.setHeight("103px");
//		panel.setSizeFull();
		
		
		final Window popup = new com.vaadin.ui.Window();
		popup.setWidth("50%");
		popup.setHeight("20%");
//		popup.setContent( viewDocumentDetailsPage);
		popup.setContent(panel);
		popup.setClosable(true);
		
		popup.center();
		popup.setResizable(false);
		popup.addCloseListener(new Window.CloseListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
				System.out.println("Close listener called");
			}
		});
		
		homeButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 7396240433865727954L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				popup.close();

			}
		});

		popup.setModal(true);
		UI.getCurrent().addWindow(popup);
	}

	@Override
	public void buildValidationUserRRCRequestLayout(Boolean isValid) {
		
			if (!isValid) {
				Label label = new Label("Same user cannot raise request more than once from same stage", ContentMode.HTML);
				label.setStyleName("errMessage");
				VerticalLayout layout = new VerticalLayout();
				layout.setMargin(true);
				layout.addComponent(label);
				ConfirmDialog dialog = new ConfirmDialog();
				dialog.setCaption("Errors");
				dialog.setClosable(true);
				dialog.setContent(layout);
				dialog.setResizable(true);
				dialog.setModal(true);
				dialog.show(getUI().getCurrent(), null, true);
			} 
		else
		{
			Window popup = new com.vaadin.ui.Window();
			popup.setCaption("");
			popup.setWidth("85%");
			popup.setHeight("100%");
			rewardRecognitionRequestViewObj = rewardRecognitionRequestViewInstance.get();
			//ViewDocumentDetailsDTO documentDetails =  new ViewDocumentDetailsDTO();
			//documentDetails.setClaimDto(bean.getClaimDTO());
			rewardRecognitionRequestViewObj.initPresenter(SHAConstants.PA_ACKNOWLEDGE_DOC_RECEIVED);
			//rewardRecognitionRequestViewObj.init(bean, popup);
			rewardRecognitionRequestViewObj.init(preauthDTO, popup);
			
			//earlierRodDetailsViewObj.init(bean.getClaimDTO().getKey(),bean.getKey());
			popup.setCaption("Reward Recognition Request");
			popup.setContent(rewardRecognitionRequestViewObj);
			popup.setClosable(true);
			popup.center();
			popup.setResizable(false);
			popup.addCloseListener(new Window.CloseListener() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void windowClose(CloseEvent e) {
					System.out.println("Close listener called");
				}
			});

			popup.setModal(true);
			UI.getCurrent().addWindow(popup);
		}
		}
		
	@Override
	public void loadRRCRequestDropDownValues(
			BeanItemContainer<SelectValue> mastersValueContainer) {
		// TODO Auto-generated method stub
		rewardRecognitionRequestViewObj.loadRRCRequestDropDownValues(mastersValueContainer)	;
		
	}
 
	@Override
	public void buildRRCRequestSuccessLayout(String rrcRequestNo) {
		// TODO Auto-generated method stub
		rewardRecognitionRequestViewObj.buildRRCRequestSuccessLayout(rrcRequestNo);
		
	}
	
	
	public void initDTO(ReceiptOfDocumentsDTO dto)
	{
		this.bean = dto;
	}
	

	@Override
	public void resetView() {
		
		/*if(null != this.wizard && !this.wizard.getSteps().isEmpty())
		{
			this.wizard.clearWizardMap(DOCUMENT_DETAILS);
			this.wizard.clearWizardMap("Acknowledgement Receipt");
		//	this.wizard.clearWizardMap(DOCUMENT_DETAILS);
			this.wizard.clearCurrentStep();
		}*/
	}

	@Override
	public void activeStepChanged(WizardStepActivationEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stepSetChanged(WizardStepSetChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wizardCompleted(WizardCompletedEvent event) {
		// TODO Auto-generated method stub
		fireViewEvent(PAAcknowledgeDocumentWizardPresenter.SUBMIT_ACKNOWLEDGE_DOC_RECEIVED, this.bean);
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		ConfirmDialog dialog = ConfirmDialog.show(getUI(), "Confirmation", "Are you sure you want to cancel ?",
		        "No", "yes", new ConfirmDialog.Listener() {
			
		            public void onClose(ConfirmDialog dialog) {
		                if (!dialog.isConfirmed()) {
		                    
		                	fireViewEvent(MenuItemBean.PA_SHOW_ACKNOWLEDGEMENT_DOCUMENT_RECEIVED, null);
		                } else {
		                    // User did not confirm
		                }
		            }
		        });
		
		dialog.setClosable(false);
		dialog.setStyleName(Reindeer.WINDOW_BLACK);
		
	}

	@Override
	public void initData(WizardInitEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wizardSave(GWizardEvent event) {
		fireViewEvent(PAAcknowledgeDocumentWizardPresenter.SUBMIT_ACKNOWLEDGE_DOC_RECEIVED, this.bean);

		
	}

	@Override
	public void buildSuccessLayout() {
		Label successLabel = new Label("<b style = 'color: green;'>Acknowledgement completed successfully !!!</b>", ContentMode.HTML);
		
//		Label noteLabel = new Label("<b style = 'color: red;'>  In case of query next step would be </br> viewing the letter and confirming </b>", ContentMode.HTML);
		
		Button homeButton = new Button("Acknowledge Document Received Home");
		homeButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		HorizontalLayout horizontalLayout = new HorizontalLayout(homeButton);
		horizontalLayout.setMargin(true);
		
		VerticalLayout layout = new VerticalLayout(successLabel, horizontalLayout);
		layout.setSpacing(true);
		layout.setMargin(true);
		HorizontalLayout hLayout = new HorizontalLayout(layout);
		hLayout.setMargin(true);
		hLayout.setStyleName("borderLayout");
		
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
				toolBar.countTool();
				fireViewEvent(MenuItemBean.PA_SHOW_ACKNOWLEDGEMENT_DOCUMENT_RECEIVED, null);
				
			}
		});
	}
	
	@Override
	public void setsubCategoryValues(BeanItemContainer<SelectValue> selectValueContainer,GComboBox subCategory,SelectValue value){
		 rewardRecognitionRequestViewObj.setsubCategoryValues(selectValueContainer, subCategory, value);
	 }
	 
	 @Override
	 public void setsourceValues(BeanItemContainer<SelectValue> selectValueContainer,GComboBox source,SelectValue value){
		 rewardRecognitionRequestViewObj.setsourceValues(selectValueContainer, source, value);
	 }

}
