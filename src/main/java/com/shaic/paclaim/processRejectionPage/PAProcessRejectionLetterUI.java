package com.shaic.paclaim.processRejectionPage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.AbstractMVPView;
import org.vaadin.teemu.wizards.GWizard;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.claim.ReportDto;
import com.shaic.claim.intimation.create.dto.DocumentGenerator;
import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.shaic.claim.processrejection.search.SearchProcessRejectionTableDTO;
import com.shaic.domain.Claim;
import com.shaic.domain.ReferenceTable;
import com.shaic.newcode.wizard.dto.ProcessRejectionDTO;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.v7.ui.VerticalLayout;

public class PAProcessRejectionLetterUI extends AbstractMVPView implements PAProcessRejectionLetterView {
	
	
	@Inject
	private SearchProcessRejectionTableDTO searchDTO;
	
	ProcessRejectionDTO bean;
	
	private GWizard wizard;
	
	private Claim claim;
	
	private PreauthDTO preauthDTO;
	
	@Override
	public String getCaption() {
		return "Process Rejection Letter";
	}
	
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		
		

	}
	
	public void init(SearchProcessRejectionTableDTO searchDTO,ProcessRejectionDTO bean,GWizard wizard){
		
		this.searchDTO = searchDTO;
		this.bean = bean;
		this.wizard = wizard;
		
	}

	public Component getContent() {
		
		fireViewEvent(PAProcessRejectionLetterPresenter.GENERATED_PA_REJECTION_LETTER, this.bean, this.searchDTO,this.searchDTO.getIntimationDTO());
		
		
		DocumentGenerator docGen = new DocumentGenerator();
		ReportDto reportDto = new ReportDto();
		
		List<PreauthDTO> preauthDTOList = new ArrayList<PreauthDTO>();
		
		preauthDTOList.add(preauthDTO);
		if(claim != null){
			reportDto.setClaimId(claim.getClaimId());
		}

		reportDto.setBeanList(preauthDTOList);	
		/***
		 * New CR
		 * 
		 */
		String templateName = "PA_Reimb_RegistrationSuggestRejectionLetter";
		
		if(preauthDTO.getClaimDTO().getClaimType() != null && ReferenceTable.REIMBURSEMENT_CLAIM_TYPE_KEY.equals(preauthDTO.getClaimDTO().getClaimType().getId())) 
		{	
			templateName = "PA_Reimb_RegistrationSuggestRejectionLetter";
		}		
		else{
			templateName = "PARegistrationSuggestRejectionLetter";
		}
		
		/***
		 *    commented becoz of above New CR
		 * 
		 */
//		String templateName = "RegistrationSuggestRejectionLetter";
//		
//		if(preauthDTO.getKey() == null && ReferenceTable.CLAIM_REGISTERED_STATUS.equals(preauthDTO.getClaimDTO().getStatusId())){
//			templateName = "PARegistrationSuggestRejectionLetter";
//		}
		
		String filePath = docGen.generatePdfDocument(templateName, reportDto);
		
		final String finalFilePath = filePath;
		
		// ((VerticalLayout) window.getContent()).setSizeFull();
	

		Path p = Paths.get(finalFilePath);
		String fileName = p.getFileName().toString();
		
		this.searchDTO.setDocFilePath(finalFilePath);
		this.searchDTO.setDocSource("Process Rejection");
		this.searchDTO.setDocType(SHAConstants.PROCESS_REJECTION_LETTER);
		StreamResource.StreamSource s = SHAUtils.getStreamResource(finalFilePath);
		
		/*StreamResource.StreamSource s = new StreamResource.StreamSource() {

			public FileInputStream getStream() {
				try {

					File f = new File(finalFilePath);
					FileInputStream fis = new FileInputStream(f);
					return fis;

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};*/

		StreamResource r = new StreamResource(s, fileName);
		Embedded e = new Embedded();
		e.setSizeFull();
		e.setType(Embedded.TYPE_BROWSER);
		r.setMIMEType("application/pdf");
		e.setSource(r);
		SHAUtils.closeStreamResource(s);
		e.setHeight("700px");
		
	
		
		
		
		VerticalLayout mainVertical = new VerticalLayout(e);
		
	    return mainVertical;
		
	}
	

	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(ProcessRejectionDTO bean) {
		this.bean = bean;
		
	}

	@Override
	public void setupReferences(Map<String, Object> referenceData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onAdvance() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onBack() {
		
		return true;
	}

	@Override
	public boolean onSave() {
		return true;
	}


	@Override
	public void openPdfFileInWindow(Claim claim, PreauthDTO preauthDTO) {
		this.claim = claim;
		this.preauthDTO = preauthDTO;
	}



}
