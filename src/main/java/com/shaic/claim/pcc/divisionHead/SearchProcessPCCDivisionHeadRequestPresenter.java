package com.shaic.claim.pcc.divisionHead;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;

import com.shaic.claim.pcc.dto.SearchProcessPCCRequestFormDTO;

@ViewInterface(SearchPccDivisionHeadView.class)
public class SearchProcessPCCDivisionHeadRequestPresenter extends AbstractMVPPresenter<SearchPccDivisionHeadView> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 850117920646287903L;
	
	public static final String SEARCH_PCC_DIVISION_HEAD_RQUEST = "search_pcc_division_head_rquest";
	
	@EJB
	private DivisionHeadRequestService searchService;
	
	@SuppressWarnings({ "deprecation" })
	public void handleSearch(@Observes @CDIEvent(SEARCH_PCC_DIVISION_HEAD_RQUEST) final ParameterDTO parameters) {
		
		SearchProcessPCCRequestFormDTO searchFormDTO = (SearchProcessPCCRequestFormDTO) parameters.getPrimaryParameter();
		
		String userName=(String)parameters.getSecondaryParameter(0, String.class);
		String passWord=(String)parameters.getSecondaryParameter(1, String.class);
		
		view.list(searchService.search(searchFormDTO,userName,passWord));
	}

	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}

}
