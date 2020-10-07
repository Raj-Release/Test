package com.shaic.claim;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.ViewComponent;

import com.shaic.arch.SHAConstants;
import com.shaic.domain.IntimationService;
import com.shaic.domain.Policy;
import com.shaic.domain.PolicyService;
import com.shaic.domain.ReferenceTable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.sun.jersey.api.client.WebResource.Builder;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.VerticalLayout;

public class SumInsuredBonusAlertUI extends ViewComponent{

	private static final long serialVersionUID = 1L;


	@Inject
	private Instance<SumInsuredBonusAlertTable> sumInsuredBonusAlertTableInstance;

	private SumInsuredBonusAlertTable sumInsuredBonusAlertTableobj;

	private Button btnCancel;

	private VerticalLayout mainVertical;

	private VerticalLayout bonusVLayout;	

	private VerticalLayout generatedTabelLayout;

	@EJB
	private IntimationService intimationService;

	@EJB
	private PolicyService policyService;

	private SumInsuredBonusAlertDTO bonusAlertDTO;

	private Window popupWindow;	

	public void setPopupWindow(Window popupWindow) {
		this.popupWindow = popupWindow;
	}

	public Window getPopupWindow() {
		return popupWindow;
	}

	public void setbonusAlertDTO(SumInsuredBonusAlertDTO bonusAlertDTO) {
		this.bonusAlertDTO = bonusAlertDTO;
	}


	public void init(String strPolicyNo){

		mainVertical = new VerticalLayout(buildBonusLayout(strPolicyNo),generatedTabelLayout(strPolicyNo));
		mainVertical.setSpacing(true);	    	        
		setCompositionRoot(mainVertical);
	}

	private VerticalLayout buildBonusLayout(String strPolicyNo){

		VerticalLayout bonusLayout=new VerticalLayout();
		Policy intimationObject = intimationService.getPolicyByPolicyNubember(strPolicyNo);


		if(strPolicyNo!=null && intimationObject.getProduct().getCode()!=null && 
				(ReferenceTable.getCumulativeProductBonusList().contains(intimationObject.getProduct().getCode()))) {

			//Bancs Changes Start
			Policy policyObj = null;
			Builder builder = null;
			String strDmsViewURL = null;

			if(strPolicyNo != null){
				policyObj = policyService.getByPolicyNumber(strPolicyNo);
				if (policyObj != null) {
					if(policyObj.getPolicySource() != null && policyObj.getPolicySource().equalsIgnoreCase(SHAConstants.BANCS_POLICY)){
						Notification.show("ERROR", "Bonus Logic Details Not Applicable For Release 1.0", Type.ERROR_MESSAGE);
					}else{
						strDmsViewURL = BPMClientContext.CUMULATIVE_BONUS_URL;
						strDmsViewURL = strDmsViewURL.concat(strPolicyNo);
						BrowserFrame browserFrame = new BrowserFrame("",
								new ExternalResource(strDmsViewURL!=null?strDmsViewURL:""));											
						browserFrame.setHeight("330px");
						browserFrame.setWidth("100%");						
						bonusLayout.addComponent(browserFrame);						
						bonusLayout.setSizeFull();																								
					}
				}
			}												
		}else {
			Notification.show("ERROR", "Bonus Logic Details Not Applicable", Type.ERROR_MESSAGE);
		}
		return bonusLayout;
	}


	public VerticalLayout generatedTabelLayout(String strPolicyNo) {

		VerticalLayout generatedTabelLayout=new VerticalLayout();
		generatedTabelLayout.removeAllComponents();
		if(sumInsuredBonusAlertTableobj == null) {
			sumInsuredBonusAlertTableobj = sumInsuredBonusAlertTableInstance.get();
		}
		sumInsuredBonusAlertTableobj.init(strPolicyNo,bonusAlertDTO);
			
		if(btnCancel == null) {
			btnCancel = new Button("OK");
			btnCancel.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			addCancelButtonListener();
		}		
		generatedTabelLayout.addComponent(sumInsuredBonusAlertTableobj);
		generatedTabelLayout.setSpacing(true);
		HorizontalLayout hLayout = new HorizontalLayout(btnCancel);
		hLayout.setSpacing(true);
		generatedTabelLayout.addComponent(hLayout);
		generatedTabelLayout.setComponentAlignment(hLayout, Alignment.MIDDLE_CENTER);
		return generatedTabelLayout;
	}

	private void addCancelButtonListener() {
		btnCancel.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {				
				getPopupWindow().close();
			}
		});
	}
}
