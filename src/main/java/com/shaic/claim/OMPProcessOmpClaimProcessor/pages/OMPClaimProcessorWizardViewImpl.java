package com.shaic.claim.OMPProcessOmpClaimProcessor.pages;

import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class OMPClaimProcessorWizardViewImpl extends AbstractMVPView implements OMPProcessOmpClaimProcessorPageWizard {

	private static final long serialVersionUID = -1756934701433733987L;
	
	@Inject
	private Instance<OMPNewClaimProcessorPageUI> claimProcessorPageUIInstance;	 
	
	private OMPNewClaimProcessorPageUI ompClaimProcessorPageUI;
	
	private OMPClaimProcessorDTO bean;

	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getCaption() {
		return "Bill Hospitalization";
	}


	

	@Override
	public void buildSuccessLayout() {
		// TODO Auto-generated method stub
		
		Label successLabel = new Label("<b style = 'color: green;'>Claim processor completed successfully !!!</b>", ContentMode.HTML);
		
//		Label noteLabel = new Label("<b style = 'color: red;'>  In case of query next step would be </br> viewing the letter and confirming </b>", ContentMode.HTML);
		
		Button homeButton = new Button("Claim Processor Home");
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

				fireViewEvent(MenuItemBean.OMP_PROCESS_OMP_CLAIM_PROCESSOR, null);
				
			}
		});
	}



	@Override
	public void init(OMPClaimProcessorDTO bean, BeanItemContainer<SelectValue> classification, BeanItemContainer<SelectValue> subClassification, 
			BeanItemContainer<SelectValue> paymentTo, BeanItemContainer<SelectValue> paymentMode, BeanItemContainer<SelectValue> eventCode, 
			BeanItemContainer<SelectValue> currencyValue, BeanItemContainer<SelectValue> negotiatorName, BeanItemContainer<SelectValue> modeOfReciept, 
			BeanItemContainer<SelectValue> documentRecievedFrom, BeanItemContainer<SelectValue> documentType, BeanItemContainer<SelectValue> country) {
		
		
		 setSizeFull();
		 this.bean = bean;
		 ompClaimProcessorPageUI = claimProcessorPageUIInstance.get();
		 ompClaimProcessorPageUI.init(bean, classification,  subClassification,paymentTo,paymentMode,eventCode,
				 currencyValue,negotiatorName,modeOfReciept,documentRecievedFrom,documentType,country);
		 setCompositionRoot(ompClaimProcessorPageUI);
	}
	
	
	
	private void initBinder() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateFieldsOnNegotiate(HorizontalLayout horizontalLayout, BeanItemContainer<SelectValue> negotiatorName) {
		ompClaimProcessorPageUI.generateNegotiate(horizontalLayout, negotiatorName);
		
	}

	@Override
	public void generateFieldsForRejection(HorizontalLayout horizontalLayout) {
		ompClaimProcessorPageUI.generateRejection(horizontalLayout);
		
	}

	@Override
	public void generateFieldsOnApproval(HorizontalLayout horizontalLayout) {
//		ompClaimProcessorPageUI.generateApproval(horizontalLayout);
		
	}

	@Override
	public void setReferenceDate(Map<String, Object> referenceDataMap) {
//		ompClaimProcessorPageUI.setReferenceDate(referenceDataMap);
		
	}

	@Override
	public void buildSaveLayout() {
		

		
			Label saveLabel = new Label("<b style = 'color: green;'> Negotiation Record Saved Successfully!!!</b>", ContentMode.HTML);
			
			Button homeButton = new Button("OK");
			homeButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			HorizontalLayout horizontalLayout = new HorizontalLayout(homeButton);
			horizontalLayout.setMargin(true);
			
			VerticalLayout layout = new VerticalLayout(saveLabel, horizontalLayout);
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
//					fireViewEvent(MenuItemBean.OMP_PROCESS_OMP_CLAIM_PROCESSOR, null);
					
				}
			});
				
	}

	@Override
	public void cancelIntimation() {
		ompClaimProcessorPageUI.cancelIntimation();
		
	}

	@Override
	public void generateFieldsOnApprove(
			OMPClaimCalculationViewTableDTO calculationViewTableDTO) {
		ompClaimProcessorPageUI.generateApprovalRemark(calculationViewTableDTO);
		
	}

	@Override
	public void generateFieldsOnRejectionRemark(
			OMPClaimCalculationViewTableDTO calculationViewTableDTO) {
		ompClaimProcessorPageUI.generateRejectionRemark(calculationViewTableDTO);
		
	}

	

}
