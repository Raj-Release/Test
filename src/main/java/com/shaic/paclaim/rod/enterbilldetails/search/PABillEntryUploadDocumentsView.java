package com.shaic.paclaim.rod.enterbilldetails.search;

import java.util.Map;

import org.vaadin.teemu.wizards.WizardStep;

import com.shaic.arch.GMVPView;
import com.shaic.claim.rod.wizard.dto.ReceiptOfDocumentsDTO;
import com.shaic.claim.rod.wizard.dto.UploadDocumentDTO;

public interface PABillEntryUploadDocumentsView extends GMVPView, WizardStep<ReceiptOfDocumentsDTO>{

	void setUpDropDownValues(Map<String, Object> referenceDataMap);
	void loadUploadedDocsTableValues(UploadDocumentDTO uploadDocList);
	void deleteUploadDocumentDetails(UploadDocumentDTO dto);
	void editUploadDocumentDetails(UploadDocumentDTO dto);
	void resetPage();
}
