package com.shaic.claim.withdrawWizard;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.teemu.wizards.event.GWizardEvent;
import org.vaadin.teemu.wizards.event.GWizardListener;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardInitEvent;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.ClaimDto;
import com.shaic.claim.ViewDetails;
import com.shaic.claim.ViewDetails.ViewLevels;
import com.shaic.claim.cashlessprocess.withdrawpreauth.SearchWithdrawCashLessProcessTableDTO;
import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.shaic.claim.viewEarlierRodDetails.RewardRecognitionRequestView;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.preauth.MasterRemarks;
import com.shaic.ims.carousel.RevisedCashlessCarousel;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.shaic.main.navigator.ui.Toolbar;
import com.shaic.newcode.wizard.IWizardPartialComplete;
import com.shaic.newcode.wizard.dto.NewIntimationDto;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Reindeer;

public class WithdrawPreauthWizardViewImpl extends AbstractMVPView implements WithdrawPreauthWizard,GWizardListener {
	
	
	@Inject
	private Instance<WithdrawPreauthPage> withdrawPreauthPageInstance;
	
	@Inject
	private Instance<WithdrawPreauthPdfPage> withdrawPreauthPdfInstance;
	
	@Inject
	private Instance<RevisedCashlessCarousel> commonCarouselInstance;
	
	private WithdrawPreauthPage withdrawPreauthPageObj;
	
	@Inject
	private ViewDetails viewDetails;
	
	private WithdrawPreauthPageDTO bean;
	
	private VerticalLayout mainPanel;
	
	private IWizardPartialComplete wizard;
	

	@Inject
	private Instance<RewardRecognitionRequestView> rewardRecognitionRequestViewInstance;
	
	private RewardRecognitionRequestView rewardRecognitionRequestViewObj;
	
	@Inject
	private Toolbar toolBar;
	
	////private static Window popup;
	
	private final Logger log = LoggerFactory.getLogger(WithdrawPreauthWizardViewImpl.class);
	
	@PostConstruct
	public void initView() {
		addStyleName("view");
		setSizeFull();		
	}
	
	
	public void initView(WithdrawPreauthPageDTO bean,SearchWithdrawCashLessProcessTableDTO tableDto,NewIntimationDto intimationDto,ClaimDto claimDto,BeanItemContainer<SelectValue> selectValueContainer)
	{
		
		this.wizard=new IWizardPartialComplete();
		wizard.getFinishButton().setCaption("Submit");
		this.bean=bean;
		this.bean.setSelectValueContainer(selectValueContainer);
		
		this.bean.setNewIntimationDto(intimationDto);
		this.bean.setClaimDto(claimDto);
		this.bean.setTableDto(tableDto);
		this.bean.setPreauthDto(tableDto.getPreauthDto());
		this.bean.setPreviousPreAuthTableDTO(tableDto.getPreviousPreAuthTableDTO());
		this.bean.setPreauth(tableDto.getPreauth());
		
//		viewDetails.initView(intimationDto.getIntimationId(), ViewLevels.INTIMATION, false,"Withdraw Pre-auth");
		viewDetails.initView(intimationDto.getIntimationId(), ViewLevels.INTIMATION, false,"Withdrawal of cashless approval");
		
		mainPanel = new VerticalLayout();
//		mainPanel.setSplitPosition(22, Unit.PERCENTAGE);
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");
		RevisedCashlessCarousel preauthIntimation=commonCarouselInstance.get();
		PreauthDTO preauthDto = bean.getPreauthDto();
		preauthDto.setNewIntimationDTO(intimationDto);
		preauthDto.setClaimDTO(claimDto);
//		preauthIntimation.init(preauthDto,"Withdraw Pre-auth");
		preauthIntimation.init(preauthDto,"Withdrawal of cashless approval");
		
		WithdrawPreauthPage withdrawPreauthPageObj=withdrawPreauthPageInstance.get();
		withdrawPreauthPageObj.init(this.bean);
		this.withdrawPreauthPageObj=withdrawPreauthPageObj;
		
		WithdrawPreauthPdfPage withdrawPreauthPdfObj=withdrawPreauthPdfInstance.get();
		withdrawPreauthPdfObj.init(this.bean);
		
		
		wizard.addStep(withdrawPreauthPageObj,/*"Withdraw Pre-auth"*/ "Withdrawal of cashless approval");
		
		wizard.addStep(withdrawPreauthPdfObj,"Decision Communication");
		
		wizard.setStyleName(ValoTheme.PANEL_WELL);
		wizard.setSizeFull();
		wizard.addListener(this);
		
		VerticalLayout wizardLayout2 = new VerticalLayout(wizard);
		mainPanel.addComponent(preauthIntimation);
		Label dummyLabel =new Label();
		dummyLabel.setWidth("300px");
		HorizontalLayout commonButtonsLayout = new HorizontalLayout(commonButtonsLayout(),viewDetails);
		commonButtonsLayout.setComponentAlignment(viewDetails, Alignment.TOP_RIGHT);
		commonButtonsLayout.setWidth("100%");
		//mainPanel.addComponent(viewDetails);
		mainPanel.addComponent(commonButtonsLayout);
		mainPanel.setComponentAlignment(commonButtonsLayout, Alignment.TOP_LEFT);
		mainPanel.addComponent(wizardLayout2);
		//mainPanel.setComponentAlignment(viewDetails, Alignment.TOP_RIGHT);
		
		setCompositionRoot(mainPanel);
	}
	
	public HorizontalLayout commonButtonsLayout()
	{
		Button btnRRC = new Button("RRC");
		btnRRC.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				validateUserForRRCRequestIntiation();
				
			}
			
		});
		
		TextField icrEarnedPremium = new TextField("ICR(Earned Premium %)");
		icrEarnedPremium.setReadOnly(Boolean.FALSE);
		if(null != bean.getNewIntimationDto().getIcrEarnedPremium()){
			icrEarnedPremium.setValue(bean.getPreauthDto().getNewIntimationDTO().getIcrEarnedPremium());
		}
		icrEarnedPremium.setReadOnly(Boolean.TRUE);
		FormLayout icrLayout = new FormLayout(icrEarnedPremium);
		icrLayout.setMargin(false);
		icrLayout.setSpacing(true);
		
		HorizontalLayout hopitalFlag = SHAUtils.hospitalFlag(bean.getPreauthDto());
		
		Button viewLinkedPolicy = viewDetails.getLinkedPolicyDetails(bean.getPreauthDto());
		
		HorizontalLayout alignmentHLayout = new HorizontalLayout();
		alignmentHLayout.setSpacing(true);

		if(ReferenceTable.getGMCProductList().containsKey(bean.getNewIntimationDto().getPolicy().getProduct().getKey())){
			
			if(bean.getPreauthDto().getNewIntimationDTO().getPolicy().getGmcPolicyType() != null && 
					((bean.getPreauthDto().getPolicyDto().getGmcPolicyType().equalsIgnoreCase(SHAConstants.GMC_POLICY_TYPE_PARENT)) || (bean.getPreauthDto().getPolicyDto().getGmcPolicyType().equalsIgnoreCase(SHAConstants.GMC_POLICY_TYPE_STANDALONE_PARENT)))){
						alignmentHLayout = new HorizontalLayout(btnRRC,viewLinkedPolicy,icrLayout);
					} else {
						alignmentHLayout = new HorizontalLayout(btnRRC,icrLayout);
					}
		}
		else
		{
			alignmentHLayout = new HorizontalLayout(btnRRC);
		}
//		alignmentHLayout.setComponentAlignment(vLayout, Alignment.TOP_LEFT);
		
		HorizontalLayout hlayout=new HorizontalLayout(alignmentHLayout, hopitalFlag);
		hlayout.setSpacing(true);
		return hlayout;
	}
	
	private void validateUserForRRCRequestIntiation()
	{
		
		fireViewEvent(WithdrawPreauthWizardPresenter.VALIDATE_WITHDRAW_PREAUTH_USER_RRC_REQUEST, bean.getPreauthDto());//, secondaryParameters);
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
			rewardRecognitionRequestViewObj.initPresenter(SHAConstants.PROCESS_WITHDRAW_PREAUTH);
			rewardRecognitionRequestViewObj.init(bean.getPreauthDto(), popup);
			
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


	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
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
		try{
			fireViewEvent(WithdrawPreauthWizardPresenter.WITHDRAW_SUBMITTED_EVENT, this.bean);
		}catch(Exception e)
		{
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		
		ConfirmDialog dialog = ConfirmDialog.show(getUI(), "Confirmation", "Are you sure you want to cancel ?",
		        "Cancel", "Ok", new ConfirmDialog.Listener() {

		            public void onClose(ConfirmDialog dialog) {
		                if (!dialog.isConfirmed()) {
		                    // Confirmed to continue
		                	if(bean.getPreauthDto() != null){
		    					SHAUtils.setClearPreauthDTO(bean.getPreauthDto());
		    				}
		                	fireViewEvent(MenuItemBean.WITHDRAW_PRE_AUTH, null);
		                } else {
		                    // User did not confirm
		                }
		            }
		        });
		
		dialog.setStyleName(Reindeer.WINDOW_BLACK);
		
	}

	@Override
	public void initData(WizardInitEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wizardSave(GWizardEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void buildSuccessLayout() {
		Label successLabel = new Label("<b style = 'color: green;'>Pre-auth has been withdrawn successfully !!! </b>", ContentMode.HTML);
		
//		Label noteLabel = new Label("<b style = 'color: red;'>  In case of query next step would be </br> viewing the letter and confirming </b>", ContentMode.HTML);
		
		Button homeButton = new Button("Withdraw Home");
		homeButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		VerticalLayout layout = new VerticalLayout(successLabel, homeButton);
		layout.setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
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
				if(bean.getPreauthDto() != null){
					SHAUtils.setClearPreauthDTO(bean.getPreauthDto());
				}
				
				fireViewEvent(MenuItemBean.WITHDRAW_PRE_AUTH, null);
				
			}
		});
	}


	@Override
	public void buildWithdrawFields(
			BeanItemContainer<SelectValue> selectValueContainer) {
		if(this.withdrawPreauthPageObj != null) {
			this.withdrawPreauthPageObj.buildWithdrawLayout(selectValueContainer);
		}
	}


	@Override
	public void buildWithdrawAndRejctFields(
			BeanItemContainer<SelectValue> withdrawContainer,
			BeanItemContainer<SelectValue> rejectionContainer) {
		if(this.withdrawPreauthPageObj != null) {
			this.withdrawPreauthPageObj.buildWithdrawAndRejectLayout(withdrawContainer, rejectionContainer);
		}
	}

	@Override
	public void buildWithdrawAndRejctFields(
			BeanItemContainer<SelectValue> withdrawRejectContainer) {
		if(this.withdrawPreauthPageObj != null) {
			this.withdrawPreauthPageObj.buildWithdrawAndRejectLayout(withdrawRejectContainer);
		}
	}
	
	@Override
	public void setRemarks(MasterRemarks remarks, String decision) {
		withdrawPreauthPageObj.setRemarks(remarks, decision);
		
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
