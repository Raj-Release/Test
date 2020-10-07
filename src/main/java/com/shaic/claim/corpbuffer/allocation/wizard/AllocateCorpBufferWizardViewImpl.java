package com.shaic.claim.corpbuffer.allocation.wizard;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.SHAConstants;
import com.shaic.claim.ViewDetails;
import com.shaic.claim.ViewDetails.ViewLevels;
import com.shaic.claim.corpbuffer.allocation.search.AllocateCorpBufferPresenter;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimService;
import com.shaic.domain.Intimation;
import com.shaic.domain.IntimationService;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.shaic.ims.carousel.RevisedCashlessCarousel;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class AllocateCorpBufferWizardViewImpl extends AbstractMVPView implements AllocateCorpBufferWizard {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Instance<RevisedCashlessCarousel> commonCarouselInstance;

	private AllocateCorpBufferDetailDTO bean;

	Panel panel1 = null;
	
	VerticalLayout wizardLayout2 = null;
	
	@Inject
	private ViewDetails viewDetails;
	
	@EJB
	private ClaimService claimService;
	
	@EJB
	private IntimationService intimationService;
	
	private TextField txtCorpBufferSI;
	private TextField txtCorpUtilizedAmt;
	private TextField txtCorpBufferAllocatedToClaim;
	private TextField txtCorpBufferAllocatedToClaimEdit;
	private TextField txtBalanceAvailable;

	private Button btnSubmit;
	
	private Button btnCancel;
	
	private String screenName;
	
	private CheckBox chkBox;
	
	String corpBufferAllocatedToClaim;
	
	Double gmcAvailableSI = 0.0;

	@PostConstruct
	public void initView() {
		addStyleName("view");
		setSizeFull();		
	}
	
	@Override
	public void resetView() {
	}

	public void initView(AllocateCorpBufferDetailDTO bean) {
		this.bean = bean;
		RevisedCashlessCarousel intimationDetailsCarousel = commonCarouselInstance.get();
		intimationDetailsCarousel.init(this.bean.getNewIntimationDto(),this.bean.getClaimDto(), "Allocate Corportate Buffer", bean.getDiagnosis());
		viewDetails.initView(this.bean.getNewIntimationDto().getIntimationId(), ViewLevels.CLOSE_CLAIM, false,"Re-open Claim(Search Based)");
		FormLayout formLayout = buildCorpBufferDetailsLayout();
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setWidth("100%");
		vLayout.addComponent(viewDetails);
		vLayout.setComponentAlignment(viewDetails, Alignment.TOP_RIGHT);
		vLayout.addComponent(formLayout);
		vLayout.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
		VerticalLayout mainVertical = new VerticalLayout(intimationDetailsCarousel, vLayout);
		mainVertical.setSpacing(true);
		setHeight("650px");
		setCompositionRoot(mainVertical);
	}
	
	private FormLayout buildCorpBufferDetailsLayout() {
		
		txtCorpBufferSI = new TextField("Corporate Buffer SI");
		txtCorpUtilizedAmt = new TextField("Corporate Buffer Utilisation Amount");
		txtCorpBufferAllocatedToClaim = new TextField("Corporate Buffer Limit for Current claim");	
		txtCorpBufferAllocatedToClaimEdit = new TextField("Corporate Buffer Limit for Current claim");	
		txtBalanceAvailable = new TextField("Available Balance");
					
		txtCorpBufferAllocatedToClaimEdit.setStyleName("nonEdit");
		txtCorpBufferAllocatedToClaimEdit.setVisible(false);
		txtCorpBufferAllocatedToClaimEdit.setWidth("250px");
		
		chkBox = new CheckBox("Update Corp Buffer");
		addListener(chkBox);
		FormLayout formLayout = new FormLayout(txtCorpBufferSI,txtCorpUtilizedAmt,txtCorpBufferAllocatedToClaim,txtCorpBufferAllocatedToClaimEdit, txtBalanceAvailable);
		formLayout.addComponent(chkBox);
		formLayout.addComponent(buildCorpBufferButtonLayout());
		formLayout.setSpacing(true);
		formLayout.addStyleName("layoutDesign");
		formLayout.setWidth("50%");
		setCorpBufferValues();
		setReadOnly(formLayout, true);
		return formLayout;
	}
	
	private void setCorpBufferValues() {
		Intimation intimation = intimationService.searchbyIntimationNo(this.bean.getIntimationNumber());
		Claim claimforIntimation = claimService.getClaimforIntimation(intimation.getKey()); 
		DBCalculationService dbCalculationService = new DBCalculationService();		 
		Map<String, Double> values = null;
		if (claimforIntimation != null) {
			values = dbCalculationService.getGmcCorpBufferASIForRegister(intimation.getPolicy().getPolicyNumber(), claimforIntimation.getKey());
		} else {
			values = dbCalculationService.getGmcCorpBufferASIForRegister(intimation.getPolicy().getPolicyNumber(), 0l);
		}

		if (values != null && !values.isEmpty()) {
			if (values.get(SHAConstants.GMC_CORPORATE_BUFFER_SI) != null) {
				txtCorpBufferSI.setValue(values.get(SHAConstants.GMC_CORPORATE_BUFFER_SI).toString());
			} else {
				txtCorpBufferSI.setValue("0.0");
			}
				
			if (values.get(SHAConstants.GMC_BUFFER_UTILISED_AMT) != null) {
				txtCorpUtilizedAmt.setValue(values.get(SHAConstants.GMC_BUFFER_UTILISED_AMT).toString());
			} else {
				txtCorpUtilizedAmt.setValue("0.0");
			}
			
			if (values.get(SHAConstants.GMC_CORPORATE_LIMIT_AMT) != null) {
				corpBufferAllocatedToClaim = values.get(SHAConstants.GMC_CORPORATE_LIMIT_AMT).toString();
				txtCorpBufferAllocatedToClaim.setValue(corpBufferAllocatedToClaim);
				txtCorpBufferAllocatedToClaimEdit.setValue(corpBufferAllocatedToClaim);
			} else {
				txtCorpBufferAllocatedToClaim.setValue("0.0");
				txtCorpBufferAllocatedToClaimEdit.setValue("0.0");
			}
			
			if (values.get(SHAConstants.GMC_AVAILABLE_SI) != null) {
				gmcAvailableSI = values.get(SHAConstants.GMC_AVAILABLE_SI);
				txtBalanceAvailable.setValue(values.get(SHAConstants.GMC_AVAILABLE_SI).toString());	
			} else {
				txtBalanceAvailable.setValue("0.0");
				txtBalanceAvailable.setValue("0.0");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private HorizontalLayout buildCorpBufferButtonLayout(){
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		//Vaadin8-setImmediate() horizontalLayout.setImmediate(true);
		btnSubmit = new Button();
		btnSubmit.setCaption("Submit");
		//Vaadin8-setImmediate() btnSubmit.setImmediate(true); 
		btnSubmit.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnSubmit.setWidth("-1px");
		btnSubmit.setHeight("-10px");
		btnSubmit.setDisableOnClick(true);
		//Vaadin8-setImmediate() btnSubmit.setImmediate(true);
		btnSubmit.setEnabled(false);
				
		btnCancel = new Button();
		btnCancel.setCaption("Cancel");
		//Vaadin8-setImmediate() btnCancel.setImmediate(true);
		btnCancel.addStyleName(ValoTheme.BUTTON_DANGER);
		btnCancel.setWidth("-1px");
		btnCancel.setHeight("-10px");
		//Vaadin8-setImmediate() btnCancel.setImmediate(true);
		
		corpBufferValuesUpdateListener();
		horizontalLayout.addComponents(btnSubmit,btnCancel);
		horizontalLayout.setSpacing(true);
		return horizontalLayout;
	
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	private void setReadOnly(FormLayout a_formLayout, boolean readOnly) {
		Iterator<Component> formLayoutLeftComponent = a_formLayout
				.getComponentIterator();
		while (formLayoutLeftComponent.hasNext()) {
			Component c = formLayoutLeftComponent.next();
			if (c instanceof com.vaadin.v7.ui.AbstractField) {
				if(c instanceof TextField){
					if (c.getStyleName() != "nonEdit") {
						TextField field = (TextField) c;
						field.setWidth("250px");
						field.setNullRepresentation("");
						field.setReadOnly(readOnly);
						field.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
					} else {
						TextField field = (TextField) c;
						field.setWidth("250px");
						field.setNullRepresentation("");
					}
				} 
			}
		}
	}
	
	private void addListener(final CheckBox chkBox)
	{	
		chkBox
		.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (null != event && null != event.getProperty() && null != event.getProperty().getValue()) {
					if (event.getProperty().getValue() == Boolean.TRUE) {
						txtCorpBufferAllocatedToClaimEdit.setVisible(true);
						txtCorpBufferAllocatedToClaimEdit.setStyleName(null);
						txtCorpBufferAllocatedToClaimEdit.setReadOnly(false);
						txtCorpBufferAllocatedToClaim.setVisible(false);
						btnSubmit.setEnabled(true);
					} else {
						txtCorpBufferAllocatedToClaimEdit.setVisible(false);
						txtCorpBufferAllocatedToClaim.setVisible(true);
						btnSubmit.setEnabled(false);
					}
				}	
			}		
		});
	}
	
	public void corpBufferValuesUpdateListener() {

		btnSubmit.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				btnSubmit.setEnabled(true);
				if (txtCorpBufferAllocatedToClaimEdit.getValue() != null && StringUtils.isNotEmpty(txtCorpBufferAllocatedToClaimEdit.getValue())) {
					Double updatedCorpBufferValue = Double.parseDouble(txtCorpBufferAllocatedToClaimEdit.getValue());
					Intimation intimation = intimationService.searchbyIntimationNo(bean.getIntimationNumber());
					if(intimation != null) {
						Double corpBufferLimit =  intimation.getPolicy() != null ? (intimation.getPolicy().getCorporateBuffer() != null ? intimation.getPolicy().getCorporateBuffer() : 0.0 ) : 0.0;
						Boolean limitExceededFlag = Boolean.FALSE;
						if (gmcAvailableSI > 0.0) {
							if (updatedCorpBufferValue > gmcAvailableSI) {
								limitExceededFlag = Boolean.TRUE;
							}
						}  else {
							if (updatedCorpBufferValue > corpBufferLimit) {
								limitExceededFlag = Boolean.TRUE;
							}
						}
						
						if (limitExceededFlag) {
							Label errorLabel = new Label("<b style = 'color: black;'>The Corporate Buffer Limit for current claim is exceeding the Corporate Buffer balance available for the policy</b>", ContentMode.HTML);
							Button okButton = new Button("Ok");
							okButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
							VerticalLayout layout = new VerticalLayout(errorLabel, okButton);
							layout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER);
							layout.setSpacing(true);
							layout.setMargin(true);
							
							final ConfirmDialog dialog = new ConfirmDialog();
							dialog.setCaption("");
							dialog.setClosable(false);
							dialog.setContent(layout);
							dialog.setResizable(false);
							dialog.setModal(true);
							dialog.show(getUI().getCurrent(), null, true);
							
							okButton.addClickListener(new ClickListener() {
								private static final long serialVersionUID = 7396240433865727954L;

								@Override
								public void buttonClick(ClickEvent event) {
									dialog.close();
									if (corpBufferAllocatedToClaim != null) {
										txtCorpBufferAllocatedToClaimEdit.setValue(corpBufferAllocatedToClaim);
									} else {
										txtCorpBufferAllocatedToClaimEdit.setValue("0.0");
									}
								}
							});
							
						} else {
							fireViewEvent(AllocateCorpBufferPresenter.SUBMIT_BUTTON_CLICK, updatedCorpBufferValue, bean);	
							Label successLabel = new Label("<b style = 'color: black;'>Corporate Buffer has been allocated successfully</b>", ContentMode.HTML);
							Button homeButton = new Button("Home");
							homeButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
							VerticalLayout layout = new VerticalLayout(successLabel, homeButton);
							layout.setComponentAlignment(homeButton, Alignment.MIDDLE_CENTER);
							layout.setSpacing(true);
							layout.setMargin(true);
							
							if (null != screenName && (screenName.equalsIgnoreCase(SHAConstants.WAIT_FOR_INPUT_SCREEN))) {
								homeButton.setCaption("Waiting For Input Home");
							}
							
							final ConfirmDialog dialog = new ConfirmDialog();
							dialog.setCaption("");
							dialog.setClosable(false);
							dialog.setContent(layout);
							dialog.setResizable(false);
							dialog.setModal(true);
							dialog.show(getUI().getCurrent(), null, true);
							
							homeButton.addClickListener(new ClickListener() {
								private static final long serialVersionUID = 7396240433865727954L;

								@Override
								public void buttonClick(ClickEvent event) {
									dialog.close();
									if (null != screenName && (SHAConstants.WAIT_FOR_INPUT_SCREEN.equalsIgnoreCase(screenName))) {
										fireViewEvent(MenuItemBean.ALLOCATE_CORP_BUFFER, null);
									}
									else {
										fireViewEvent(MenuItemBean.ALLOCATE_CORP_BUFFER, null);
									}
								}
							});
						}
					}
				} else {
					getErrorMessage("Corporate Buffer Limit for current claim is mandatory !!");
				}
			}

		});
		
		btnCancel.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				Label cancelLabel = new Label("<b style = 'color: black;'>Are you sure you want to cancel ?</b>", ContentMode.HTML);
				Button yesBtn = new Button("Yes");
				Button noBtn = new Button("No");
				yesBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
				HorizontalLayout horizontalLayout = new HorizontalLayout(yesBtn, noBtn);
				horizontalLayout.setSpacing(true);
				VerticalLayout layout = new VerticalLayout(cancelLabel, horizontalLayout);
				layout.setComponentAlignment(horizontalLayout, Alignment.BOTTOM_RIGHT);
				layout.setSpacing(true);
				layout.setMargin(true);
				
				if(null != screenName && (screenName.equalsIgnoreCase(SHAConstants.WAIT_FOR_INPUT_SCREEN))){
					yesBtn.setCaption("Waiting For Input Home");
				}
				
				final ConfirmDialog dialog = new ConfirmDialog();
				dialog.setCaption("");
				dialog.setClosable(false);
				dialog.setContent(layout);
				dialog.setResizable(false);
				dialog.setModal(true);
				dialog.show(getUI().getCurrent(), null, true);
				
				yesBtn.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 7396240433865727954L;

					@Override
					public void buttonClick(ClickEvent event) {
						dialog.close();

						if(null != screenName && (SHAConstants.WAIT_FOR_INPUT_SCREEN.equalsIgnoreCase(screenName))){
							
							fireViewEvent(MenuItemBean.ALLOCATE_CORP_BUFFER, null);
						}
						else {
							fireViewEvent(MenuItemBean.ALLOCATE_CORP_BUFFER, null);
						}
					}
				});
				
				noBtn.addClickListener(new ClickListener() {
					private static final long serialVersionUID = 7396240433865727954L;

					@Override
					public void buttonClick(ClickEvent event) {
						dialog.close();
					}
				});
			}
		});
	}
	
	public void getErrorMessage(String eMsg){
		
		Label label = new Label(eMsg, ContentMode.HTML);
		label.setStyleName("errMessage");
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(label);

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setCaption("Error");
		dialog.setClosable(true);
		dialog.setContent(layout);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.show(getUI().getCurrent(), null, true);
	   }
	
	
	
}
