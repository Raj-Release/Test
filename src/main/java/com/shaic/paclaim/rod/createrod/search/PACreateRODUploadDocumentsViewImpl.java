package com.shaic.paclaim.rod.createrod.search;

import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdiproperties.Localizer.TextBundleUpdated;
import org.vaadin.addon.cdiproperties.TextBundle;

import com.shaic.claim.rod.wizard.dto.ReceiptOfDocumentsDTO;
import com.shaic.claim.rod.wizard.dto.UploadDocumentDTO;
import com.vaadin.ui.Component;

public class PACreateRODUploadDocumentsViewImpl extends AbstractMVPView implements PACreateRODUploadDocumentsView
{

	
	@Inject
	private PACreateRODUploadDocumentsPage uploadDocumentsPage;


	@Inject
	private TextBundle tb;
	
	private String strCaptionString;
	
	private ReceiptOfDocumentsDTO bean;
	
	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(ReceiptOfDocumentsDTO bean) {
		localize(null);
		this.bean = bean;
		uploadDocumentsPage.init(bean);
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getContent() {
		Component comp = uploadDocumentsPage.getContent();
		fireViewEvent(PACreateRODUploadDocumentsPresenter.CREATE_ROD_UPLOAD_DOC_SETUP_DROPDOWN_VALUES, bean);
		// TODO Auto-generated method stub
		return comp;
	}

	@Override
	public void setupReferences(Map<String, Object> referenceData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onAdvance() {
		uploadDocumentsPage.setTableValuesToDTO();
		return uploadDocumentsPage.validatePage();
	}

	@Override
	public boolean onBack() {
		uploadDocumentsPage.setTableValuesToDTO();
		bean.setIsComparisonDone(false);
		this.bean.getDocumentDetails().setIsEditable(Boolean.TRUE);
		return true;
	}

	@Override
	public boolean onSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUpDropDownValues(Map<String, Object> referenceDataMap) {
			uploadDocumentsPage.setFileTypeValues(referenceDataMap);
	}
	
	protected void localize(
            @Observes(notifyObserver = Reception.IF_EXISTS) @TextBundleUpdated final ParameterDTO parameterDto) {
             strCaptionString = tb.getText(textBundlePrefixString() + "uploaddocuments");//String.valueOf(propertyId).toLowerCase());
        }
    
	@Override
	public String getCaption() {
		//return "Document Details";
		return strCaptionString;
	}
	

	private String textBundlePrefixString()
	{
		return "create-rod-";
	}

	/*@Override
	public void loadUploadedDocsTableValues(
			List<UploadDocumentDTO> uploadDocList) {
		
		this.uploadDocumentsPage.loadUploadedDocsTableValues(uploadDocList);
		
	}*/
	
	@Override
	public void loadUploadedDocsTableValues(
			UploadDocumentDTO uploadDocList) {
		
		this.uploadDocumentsPage.loadUploadedDocsTableValues(uploadDocList);
		
	}
	
	@Override
	public void deleteUploadDocumentDetails(UploadDocumentDTO dto) {
		uploadDocumentsPage.deleteUploadDocumentDetails(dto);
	}

	@Override
	public void editUploadDocumentDetails(UploadDocumentDTO dto) {
		uploadDocumentsPage.editUploadedDocumentDetails(dto);
		
	}

	@Override
	public void resetPage() {
		uploadDocumentsPage.reset();
		
	}

	@Override
	public void loadAlreadyUploadedDocsTable(UploadDocumentDTO uploadDocList) {
		// TODO Auto-generated method stub
		uploadDocumentsPage.attachDocForROD(uploadDocList);
	}

	
}
