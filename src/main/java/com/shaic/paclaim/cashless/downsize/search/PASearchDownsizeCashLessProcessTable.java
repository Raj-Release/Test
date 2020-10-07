package com.shaic.paclaim.cashless.downsize.search;

import java.util.List;

import javax.ejb.EJB;

import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.table.GBaseTable;
import com.shaic.domain.PreauthService;
import com.shaic.domain.ReferenceTable;
import com.shaic.domain.preauth.Preauth;
import com.shaic.domain.reimbursement.ReimbursementService;
import com.shaic.main.navigator.ui.PAMenuPresenter;
import com.shaic.paclaim.cashless.withdraw.search.PASearchWithdrawCashLessProcessTableDTO;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

public class PASearchDownsizeCashLessProcessTable extends
		GBaseTable<PASearchWithdrawCashLessProcessTableDTO> {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private PreauthService preauthService;
	
	@EJB
	private ReimbursementService reimbursementService;

	public static final Object[] VISIBLE_COL_ORDER = new Object[] {
		"serialNumber", "intimationNo", "claimNo", "lob", "insuredPatientName",
		"diagnosis", "hospitalName", "hospitalAddress", "claimStatus" };

	@Override
	public void removeRow() {
		table.removeAllItems();

	}

	@Override
	public void initTable() {
		setSizeFull();
		table.removeAllItems();
		table.setContainerDataSource(new BeanItemContainer<PASearchWithdrawCashLessProcessTableDTO>(
				PASearchWithdrawCashLessProcessTableDTO.class));
		table.setVisibleColumns(VISIBLE_COL_ORDER);
	}

	@Override
	public void tableSelectHandler(PASearchWithdrawCashLessProcessTableDTO t) {
		
		Long key = t.getKey();
		Preauth preauthById = preauthService.getPreauthById(key);
		
		List<Preauth> preauthByClaimKey = preauthService
				.getPreauthListByDescendingOrder(preauthById.getClaim().getKey());
		
		Preauth approvedPreauth = null;
		
		for (Preauth preauth : preauthByClaimKey) {
			if(! preauth.getStatus().getKey().equals(ReferenceTable.PREAUTH_REJECT_STATUS) && ! preauth.getStatus().getKey().equals(ReferenceTable.ENHANCEMENT_REJECT_STATUS)){
				approvedPreauth = preauth;
				break;
			}
		}
		
		if(approvedPreauth != null){
			t.setKey(approvedPreauth.getKey());
//			List<Reimbursement> reimbursementByClaimKey = reimbursementService.getReimbursementByClaimKey(preauthById.getClaim().getKey());
			//TODO
			
			Boolean reimbursementStatusForDownsizeWithdraw = reimbursementService.getReimbursementStatusForDownsizeWithdraw(preauthById.getClaim().getKey());
			
			if(reimbursementStatusForDownsizeWithdraw){
				if(approvedPreauth.getStatus().getKey().equals(ReferenceTable.WITHDRAW_APPROVED_STATUS)){
					
					getErrorMessage("Withdraw is approved for this intimation");
					
				}else if(! approvedPreauth.getStatus().getKey().equals(ReferenceTable.DOWNSIZE_ESCALATION_STATUS) 
						&& ! approvedPreauth.getStatus().getKey().equals(ReferenceTable.SPECIALIST_REPLY_RECEIVED)
						&& ! approvedPreauth.getStatus().getKey().equals(ReferenceTable.DOWNSIZE_SUBMIT_SPECIALIST_REPLY)){
					fireViewEvent(PAMenuPresenter.PA_DOWNSIZE_PREAUTH_PAGE_VIEW, t);
					
				}else{
					getErrorMessage("This intimation is already processing in Process Downsize Request.");
				}
			}else{
				getErrorMessage("Downsize request cannot be processed, since an reimbursement is already existing for this intimation");
				
			}
		}else{
			getErrorMessage("Downsize can not be processed, since Preauth is already Rejected.");
		}
		
	}

	@Override
	public String textBundlePrefixString() {
		return "search-withdraw-";
	}
	
	protected void tablesize(){
		table.setPageLength(table.size()+1);
		int length =table.getPageLength();
		if(length>=6){
			table.setPageLength(6);
		}
		
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
