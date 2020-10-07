package com.shaic.paclaim.rod.enterbilldetails.search;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.reimbursement.rod.enterbilldetails.search.SearchEnterBillDetailFormDTO;

@ViewInterface(PASearchEnterBillDetailsView.class)
public class PASearchEnterBillDetailsPresenter extends AbstractMVPPresenter<PASearchEnterBillDetailsView>{

	
	private static final long serialVersionUID = 1L;

	public static final String SEARCH_BUTTON_CLICK = "doSearchbilTable_PA";
	
	@EJB
	private PASearchEnterBillDetailsService searchService;

	
	@SuppressWarnings({ "deprecation" })
	public void handleSearch(@Observes @CDIEvent(SEARCH_BUTTON_CLICK) final ParameterDTO parameters) {
		
		SearchEnterBillDetailFormDTO searchFormDTO = (SearchEnterBillDetailFormDTO) parameters.getPrimaryParameter();
		
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		String passWord=(String)parameters.getSecondaryParameter(1, String.class);
		
		view.list(searchService.search(searchFormDTO,userName,passWord));
	}
	
	@Override
	public void viewEntered() {
		
		
	}
}
