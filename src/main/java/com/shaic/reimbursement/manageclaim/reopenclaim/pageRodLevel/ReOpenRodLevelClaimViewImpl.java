package com.shaic.reimbursement.manageclaim.reopenclaim.pageRodLevel;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.ViewDetails;
import com.shaic.claim.ViewDetails.ViewLevels;
import com.shaic.claim.viewEarlierRodDetails.ViewDocumentDetailsDTO;
import com.shaic.ims.carousel.RevisedCarousel;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.shaic.main.navigator.ui.Toolbar;
import com.shaic.reimbursement.manageclaim.reopenclaim.searchRodLevel.SearchReOpenClaimRodLevelTableDTO;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class ReOpenRodLevelClaimViewImpl extends AbstractMVPView implements ReOpenRodLevelClaimView {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Instance<ReOpenRodLevelClaimPage> reOpenClaimInstance;
	
	private ReOpenRodLevelClaimPage reOpenClaimObj;
	
	@Inject
	private Instance<RevisedCarousel> commonCarouselInstance;
	
	@Inject
	private ViewDetails viewDetails;
	
	@Inject
	private Toolbar toolbar;
	
	
	@Override
	public void init(SearchReOpenClaimRodLevelTableDTO searchDTO){
		
		reOpenClaimObj = reOpenClaimInstance.get();
		reOpenClaimObj.init(searchDTO);
		
		RevisedCarousel intimationDetailsCarousel = commonCarouselInstance.get();
//		intimationDetailsCarousel.init(searchDTO.getIntimationDto(),searchDTO.getClaimDto(),"Re-Open Claim");
		intimationDetailsCarousel.init(searchDTO.getIntimationDto(),searchDTO.getClaimDto(),"Re-Open Claim",searchDTO.getDiagnosis());
		
		viewDetails.initView(searchDTO.getIntimationDto().getIntimationId(),searchDTO.getReimbursmentKey(), ViewLevels.PREAUTH_MEDICAL, false,"Re-Open Claim");
		
		HorizontalLayout horLayout = new HorizontalLayout(viewDetails);
		horLayout.setWidth("100%");
		horLayout.setComponentAlignment(viewDetails, Alignment.TOP_RIGHT);
		
		VerticalLayout mainVertical = new VerticalLayout(intimationDetailsCarousel,horLayout,reOpenClaimObj);
		mainVertical.setSpacing(true);
		
		setCompositionRoot(mainVertical);
	}
	
	

	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setUpReference(ReOpenRodLevelClaimDTO reopenClaimDTO,BeanItemContainer<SelectValue> reasonForReOpen) {
		reOpenClaimObj.setUpReference(reopenClaimDTO,reasonForReOpen);
	}



	@Override
	public void buildSuccessLayout() {
		Label successLabel = new Label(
				"<b style = 'color: black;'> Selected Claims has been Re-Opened successfully !!!.</b>",
				ContentMode.HTML);

		// Label noteLabel = new
		// Label("<b style = 'color: red;'>  In case of query next step would be </br> viewing the letter and confirming </b>",
		// ContentMode.HTML);

		Button homeButton = new Button("Home Page");
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
				toolbar.countTool();
				fireViewEvent(MenuItemBean.RE_OPEN_CLAIM_ROD_LEVEL, null);

			}
		});
	}



	@Override
	public void setTableList(List<ViewDocumentDetailsDTO> listDocumentDetails) {
		reOpenClaimObj.setTableList(listDocumentDetails);
	}

}
