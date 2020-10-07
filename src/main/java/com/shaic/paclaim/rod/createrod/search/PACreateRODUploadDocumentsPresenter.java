package com.shaic.paclaim.rod.createrod.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.rod.wizard.dto.ReceiptOfDocumentsDTO;
import com.shaic.claim.rod.wizard.dto.UploadDocumentDTO;
import com.shaic.domain.MasterService;
import com.shaic.domain.ReferenceTable;
import com.vaadin.v7.data.util.BeanItemContainer;

@ViewInterface(PACreateRODUploadDocumentsView.class)
public class PACreateRODUploadDocumentsPresenter extends AbstractMVPPresenter<PACreateRODUploadDocumentsView>{

	
public static final String CREATE_ROD_UPLOAD_DOC_SETUP_DROPDOWN_VALUES = "create_rod_upload_doc_setup_dropdown_values_PA";
	
	public static final String SUBMIT_UPLOADED_DOCUMENTS = "submit_uploaded_documents_PA";
	
	public static final String DELETE_UPLOADED_DOCUMENTS = "delete_uploaded_documents_PA";
	
	public static final String EDIT_UPLOADED_DOCUMENTS = "edit_uploaded_documents_PA";
	
	public static final String ALREADY_UPLOADED_DOCUMENTS = "already_uploaded_documents_PA";

	
	//public static final String SETUP_DOCUMENT_CHECKLIST_TABLE_VALUES = "setup_doc_checklist_tbl_values";
	
	
	@EJB
	private MasterService masterService;
	
	/*@EJB
	private AcknowledgementDocumentsReceivedService ackDocReceivedService;*/

	
	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}
	
	
	public void setUpDropDownValues(
			@Observes @CDIEvent(CREATE_ROD_UPLOAD_DOC_SETUP_DROPDOWN_VALUES) final ParameterDTO parameters) {
		ReceiptOfDocumentsDTO bean = (ReceiptOfDocumentsDTO) parameters.getPrimaryParameter();
		Map<String, Object> referenceDataMap = new HashMap<String, Object>();
		BeanItemContainer<SelectValue> beanContainer = masterService.getSelectValueContainer(ReferenceTable.PA_ROD_UPLOAD_DOC_TABLE_FILE_TYPE);
		//if((ReferenceTable.CLAIM_TYPE_CASHLESS).equalsIgnoreCase(bean.getClaimDTO().getClaimTypeValue()) && (SHAConstants.YES_FLAG).equalsIgnoreCase(bean.getDocumentDetails().getHospitalizationFlag()))
		{
			List<SelectValue> selectValueList = beanContainer.getItemIds();
			List<SelectValue> finalBillTypeList = new ArrayList<SelectValue>();
			BeanItemContainer<SelectValue> billTypeContainer = null;
			if(null != selectValueList && !selectValueList.isEmpty())
			{
				billTypeContainer = new BeanItemContainer<SelectValue>(
							SelectValue.class);
				
				for (SelectValue selectValue : selectValueList) {
					{
												
						if((ReferenceTable.CLAIM_TYPE_CASHLESS).equalsIgnoreCase(bean.getClaimDTO().getClaimTypeValue()) && (SHAConstants.YES_FLAG).equalsIgnoreCase(bean.getDocumentDetails().getHospitalizationFlag()))
						{
							
							
							/*if(null != bean.getClaimDTO().getNewIntimationDto() && null != bean.getClaimDTO().getNewIntimationDto().getPolicy() && 
									null != bean.getClaimDTO().getNewIntimationDto().getPolicy().getProduct() && 
									null != bean.getClaimDTO().getNewIntimationDto().getPolicy().getProduct().getKey() &&
									(ReferenceTable.GPA_PRODUCT_KEY.equals(bean.getClaimDTO().getNewIntimationDto().getPolicy().getProduct().getKey()))){*/	
								
								
								if(null != selectValue.getValue() && ((selectValue.getValue().endsWith(SHAConstants.BYPASS_BILL_TYPE_FOR_CASHLESS))
										|| (selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_DISCHARGE_SUMMARY)) || 
										(selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_FINAL_BILL)) ||
										(selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_MEDICAL_BILL)) ||
										(selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_PRESCRIPTION))  ||
										(selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_INVESTIGATION_REPORT)) ||
										(selectValue.getValue().endsWith(SHAConstants.OTHERS)) ||
										(selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_CLAIM_FORM)) || 
										(selectValue.getValue().endsWith(SHAConstants.FILE_TYPE_BILLS))))
								{
									finalBillTypeList.add(selectValue);
								}
							//}
							else
							{
								 if(null != selectValue.getValue() && (selectValue.getValue().endsWith(SHAConstants.BYPASS_BILL_TYPE_FOR_CASHLESS)))
									{
										finalBillTypeList.add(selectValue);
									}
							}
							
						}
						else
						{
							if(null != selectValue.getValue() && !(selectValue.getValue().endsWith(SHAConstants.BYPASS_BILL_TYPE_FOR_CASHLESS)))
							{
								finalBillTypeList.add(selectValue);

							}
						}
					}
					
				}
				billTypeContainer.addAll(finalBillTypeList);
			}
			referenceDataMap.put("fileType", billTypeContainer);
		}
		/*else
		{
			referenceDataMap.put("fileType", beanContainer);
		}*/
		view.setUpDropDownValues(referenceDataMap);
		
		}
	
	
	public void submitUploadedDocuments(
			@Observes @CDIEvent(SUBMIT_UPLOADED_DOCUMENTS) final ParameterDTO parameters) {
		
		//ReceiptOfDocumentsDTO rodDTO = (ReceiptOfDocumentsDTO)parameters.getPrimaryParameter();
		//List<UploadDocumentDTO> uploadDocLst = (List<UploadDocumentDTO>) parameters.getPrimaryParameter();
		UploadDocumentDTO uploadDoc = (UploadDocumentDTO) parameters.getPrimaryParameter();
		//view.loadUploadedDocsTableValues(uploadDocLst);
		view.loadUploadedDocsTableValues(uploadDoc);
		
	}
	
	public void submitAlreadyUploadedDocuments(
			@Observes @CDIEvent(ALREADY_UPLOADED_DOCUMENTS) final ParameterDTO parameters) {
		
		//ReceiptOfDocumentsDTO rodDTO = (ReceiptOfDocumentsDTO)parameters.getPrimaryParameter();
		//List<UploadDocumentDTO> uploadDocLst = (List<UploadDocumentDTO>) parameters.getPrimaryParameter();
		UploadDocumentDTO uploadDoc = (UploadDocumentDTO) parameters.getPrimaryParameter();
		//view.loadUploadedDocsTableValues(uploadDocLst);
		//view.loadUploadedDocsTableValues(uploadDoc);
		view.loadAlreadyUploadedDocsTable(uploadDoc);
		
	}
	
	public void deleteUploadedDocumentDetails(
			@Observes @CDIEvent(DELETE_UPLOADED_DOCUMENTS) final ParameterDTO parameters) {
		
		UploadDocumentDTO uploadDTO = (UploadDocumentDTO)parameters.getPrimaryParameter();
		view.deleteUploadDocumentDetails(uploadDTO);
		
		//List<DocumentDetailsDTO> documentDetailsDTO = rodService.getDocumentDetailsDTO(claimKey);
		
	//view.setDocumentDetailsDTOForValidation(documentDetailsDTO);
	}
	
	
	public void editUploadedDocumentDetails(
			@Observes @CDIEvent(EDIT_UPLOADED_DOCUMENTS) final ParameterDTO parameters) {
		
		UploadDocumentDTO uploadDTO = (UploadDocumentDTO)parameters.getPrimaryParameter();
		view.editUploadDocumentDetails(uploadDTO);
		
	}
	
	
	
	/*public void setUpDocumentCheckListValues(
			@Observes @CDIEvent(SETUP_DOCUMENT_CHECKLIST_TABLE_VALUES) final ParameterDTO parameters) {
		List<DocumentCheckListDTO> documentCheckListDTO = ackDocReceivedService.getDocumentCheckListValues(masterService);
		view.setDocumentCheckListTableValues(documentCheckListDTO);
		
		}
*/
}
