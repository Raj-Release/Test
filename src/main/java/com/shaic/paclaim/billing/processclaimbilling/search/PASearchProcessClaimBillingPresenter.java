/**
 * 
 */
package com.shaic.paclaim.billing.processclaimbilling.search;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.reimbursement.billing.processclaimbilling.search.SearchProcessClaimBillingFormDTO;



/**
 * @author ntv.narenj
 *
 */

@ViewInterface(PASearchProcessClaimBillingView.class)
public class PASearchProcessClaimBillingPresenter extends AbstractMVPPresenter<PASearchProcessClaimBillingView>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEARCH_BUTTON_CLICK = "do_pa_search_claim_billing";
	
	@EJB
	private PASearchProcessClaimBillingService searchService;

	
	@SuppressWarnings({ "deprecation" })
	public void handleSearch(@Observes @CDIEvent(SEARCH_BUTTON_CLICK) final ParameterDTO parameters) {
		
		SearchProcessClaimBillingFormDTO searchFormDTO = (SearchProcessClaimBillingFormDTO) parameters.getPrimaryParameter();
		
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		String passWord=(String)parameters.getSecondaryParameter(1, String.class);
		
		view.list(searchService.search(searchFormDTO,userName,passWord));
	}
	
	@Override
	public void viewEntered() {
		
		
	}

}
