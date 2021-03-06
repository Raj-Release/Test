/**
 * 
 */
package com.shaic.reimbursement.rod.uploadinvestication.search;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;



/**
 * @author ntv.narenj
 *
 */

@ViewInterface(SearchUploadInvesticationView.class)
public class SearchUploadInvesticationPresenter extends AbstractMVPPresenter<SearchUploadInvesticationView>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEARCH_BUTTON_CLICK = "doSearchupTable";
	
	@EJB
	private SearchUploadInvesticationService searchService;

	
	@SuppressWarnings({ "deprecation" })
	public void handleSearch(@Observes @CDIEvent(SEARCH_BUTTON_CLICK) final ParameterDTO parameters) {
		
		SearchUploadInvesticationFormDTO searchFormDTO = (SearchUploadInvesticationFormDTO) parameters.getPrimaryParameter();
		
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		String passWord=(String)parameters.getSecondaryParameter(1, String.class);
		
		view.list(searchService.search(searchFormDTO,userName,passWord));
	}
	
	@Override
	public void viewEntered() {
		
		
	}

}
