package com.shaic.claim.pcc.zonalMedicalHead;

import com.shaic.arch.GMVPView;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.pcc.dto.PccDetailsTableDTO;
import com.shaic.claim.pcc.dto.SearchProcessPCCRequestTableDTO;
import com.vaadin.v7.data.util.BeanItemContainer;

public interface ProcessPCCZonalMedicalHeadRequestWizardView extends GMVPView {
	    
	void buildSuccessLayout();
	
	void generateResponseLayout();
	
	void generateAssignLayout();
	
	void generateFieldsBasedOnNegotiation(Boolean isChecked);
	
}
