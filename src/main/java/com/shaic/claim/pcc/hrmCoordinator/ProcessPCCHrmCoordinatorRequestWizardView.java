package com.shaic.claim.pcc.hrmCoordinator;

import com.shaic.arch.GMVPView;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;

public interface ProcessPCCHrmCoordinatorRequestWizardView extends GMVPView {
	
	void generateFieldsBasedOnNegotiation(Boolean isChecked);
	
	public void buildSuccessLayout();
}
