package com.shaic.claim.pcc.zonalCoordinator;

import com.shaic.arch.GMVPView;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;

public interface ProcessPCCZonalCoordinatorRequestWizardView extends GMVPView {
	
	void generateFieldsBasedOnNegotiation(Boolean isChecked);
	
	public void buildSuccessLayout();

}
