/**
 * 
 */
package com.shaic.claim.reimbursement.rrc.services;

/**
 * @author ntv.vijayar
 *
 */

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;




@ViewInterface(SearchReviewRRCRequestView.class)
public class SearchReviewRRCRequestPresenter extends AbstractMVPPresenter<SearchReviewRRCRequestView>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEARCH_BUTTON_CLICK_REVIEW_RRC_REQUEST = "doReviewForProcessRRC";
	
	
	@EJB
	private ReviewRRCRequestService searchService;

	
	@SuppressWarnings({ "deprecation" })
	public void handleSearch(@Observes @CDIEvent(SEARCH_BUTTON_CLICK_REVIEW_RRC_REQUEST) final ParameterDTO parameters) {
		
		SearchReviewRRCRequestFormDTO searchFormDTO = (SearchReviewRRCRequestFormDTO) parameters.getPrimaryParameter();
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		String passWord=(String)parameters.getSecondaryParameter(1, String.class);
		
		view.list(searchService.search(searchFormDTO,userName,passWord));
	}
	
	
	
	
	
	@Override
	public void viewEntered() {
		
		
	}

}

