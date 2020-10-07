package com.shaic.ims.main;

import javax.annotation.PostConstruct;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdimvp.ViewComponent;
import org.vaadin.addon.cdiproperties.Localizer.TextBundleUpdated;
import org.vaadin.addon.cdiproperties.annotation.LabelProperties;

import com.shaic.arch.utils.Lang;
import com.shaic.domain.Intimation;
import com.shaic.domain.IntimationService;
import com.shaic.domain.Policy;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.navigator.View;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.navigator.View;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

@SuppressWarnings("serial")
@ViewScoped
@CDIView(value = MenuItemBean.INDEX)
public class HelpWindow extends ViewComponent implements View {
    @Inject
    private Lang lang;
    @Inject
    @LabelProperties(valueKey = "helpwindow-content", contentMode = ContentMode.HTML)
    private Label label;

    @EJB
	private IntimationService intimationService;
    
    BPMClientContext clientTest;
    
    @Inject
	private Policy policy;

	@Inject
	private Intimation intimation;

	
    @PostConstruct
    public void init() {
        final VerticalLayout mainLayout = new VerticalLayout(label);
        setCompositionRoot(mainLayout);
        localize(null);
        clientTest = new  BPMClientContext();
//        testServiceCall();
//        testIntimationMapper();
          testHumanTask();
        
    }

    private void testServiceCall() {
//    	IntimationMessage message = new IntimationMessage();
//    	message.setIntimationNumber("1000");
//    	message.setIsClaimPending(true);
//    	message.setIsPolicyValid(false);
//    	message.setIsBalanceSIAvailable(true);
//    	clientTest.getTaskList().execute("zonaluser1", message);
	}
    
    private void testIntimationMapper()
    {
//    	System.out.println("\n\n\n\n\n test intimation method called");
//    	Intimation intimation1 = new Intimation();
//    	intimation.getPolicy().setKey(1L);
//    	intimation.getPolicy().setPolicyNumber("2223433");
//    	IntimationMessage message = new IntimationMessage();
//    	message.setIntimationNumber("1000");
//    	message.setIsClaimPending(true);
//    	message.setIsPolicyValid(false);
//    	message.setIsBalanceSIAvailable(true);
    	//clientTest.getTaskList().execute("zonaluser1", message);
     }

    private void testHumanTask()
    {
    	//clientTest.getHumanTask();
    }
	void localize(
            @Observes(notifyObserver = Reception.IF_EXISTS) @TextBundleUpdated final ParameterDTO parameters) {
        setCaption(lang.getText("helpwindow-caption"));
    }
}