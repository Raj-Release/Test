package com.shaic.claim.processdatacorrection.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;

import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.preauth.PreauthWizard;
import com.shaic.claim.processtranslation.search.SearchProcessTranslationFormDTO;
import com.shaic.claim.scoring.HospitalScoringDTO;
import com.shaic.domain.MasterService;
import com.shaic.domain.PreauthService;
import com.shaic.domain.ReferenceTable;
import com.shaic.ims.bpm.claim.BPMClientContext;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.ComboBox;

@ViewInterface(DataCorrectionView.class)
public class DataCorrectionPresenter extends AbstractMVPPresenter<DataCorrectionView>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -256384569425809859L;

	@EJB
	private DBCalculationService dbCalculationService;
	
	@EJB
	private MasterService masterService;
	
	@EJB
	private PreauthService preauthService;
	
	@EJB
	private SearchProcessDataCorrectionService correctionService;
	
	protected Map<String, Object> referenceData = new HashMap<String, Object>();

	public static final String SET_TABLE_DATA = "set_table_data";
	
	public static final String SUBMIT_BUTTON_DATA_CODING = "submit_button_data_coding";
	
	public static final String SUBMIT_HOSPITAL_SCORING = "submit_hospital_scoring";
	
	public static final String GET_CORRECTION_PROCEDURE_VALUES = "get_correction_procedure_values";

	public static final String EDIT_CORRECTION_SPECIALITY_VALUES = "edit_correction_speciality_values";
	
	public static final String DELETE_CORRECTION_SPECIALITY_VALUES = "delete_correction_speciality_values";
	
	public static final String ADD_SPECIALITY_PROCEDURE_VALUES = "add_speciality_procedure_values";

	public static final String EDIT_DIGANOSIS_CORRECTION_VALUES = "edit_diganosis_correction_values";

	public static final String DELETE_DIGANOSIS_CORRECTION_VALUES = "delete_diganosis_correction_values";
	
	public static final String EDIT_PROCEDURE_CORRECTION_VALUES = "edit_procedure_correction_values";

	public static final String DELETE_PROCEDURE_CORRECTION_VALUES = "delete_procedure_correction_values";
	
	public static final String EDIT_TREATING_CORRECTION_VALUES = "edit_treating_correction_values";

	public static final String DELETE_TREATING_CORRECTION_VALUES = "delete_treating_correction_values";
	
	public static final String EDIT_IMPLANT_CORRECTION_VALUES = "edit_implant_correction_values";

	public static final String DELETE_IMPLANT_CORRECTION_VALUES = "delete_implant_correction_values";
	
	public static final String ACTUAL_IMPLANT_APPLICABLE_CHANGED = "actual_implant_applicable_changed";
	
	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}
	
	public void setReferenceData(@Observes @CDIEvent(SET_TABLE_DATA) final ParameterDTO parameters) {
		
		referenceData.put("treatmentType", masterService.getSelectValueContainer(ReferenceTable.TREATMENT_MANAGEMENT));
		referenceData.put("roomCategory", masterService.getSelectValueContainer(ReferenceTable.ROOM_CATEGORY));
		referenceData.put("diagnosisName", masterService.getDiagnosisList());
		BeanItemContainer<SelectValue> procedureListNames = preauthService.getProcedureListNames();
		referenceData.put("procedureName", procedureListNames);
		referenceData.put("procedureCode", procedureListNames);
		referenceData.put("specialityType", preauthService.getAllSpecialityType());
		
		view.setReferenceData(referenceData);
	}
	
	public void saveCorrectionDatas(@Observes @CDIEvent(SUBMIT_BUTTON_DATA_CODING) final ParameterDTO parameters) {
				
		ProcessDataCorrectionDTO dataCorrectionDTO = (ProcessDataCorrectionDTO) parameters.getPrimaryParameter();
		String userName = (String)parameters.getSecondaryParameter(0, String.class);
		correctionService.saveDataCorrection(dataCorrectionDTO,userName);
		view.buildSuccessLayout();
		
	}
	
	public void setScoringchanges(@Observes @CDIEvent(SUBMIT_HOSPITAL_SCORING) final ParameterDTO parameters) {
		List<HospitalScoringDTO> scoringDTOs = (List<HospitalScoringDTO>) parameters.getPrimaryParameter();
		view.setScoringchanges(correctionService.iscScoringChangesmade(scoringDTOs),scoringDTOs);
	}
	
	public void getcorrectionProcedureValues(@Observes @CDIEvent(GET_CORRECTION_PROCEDURE_VALUES) final ParameterDTO parameters)
	{
		Long specialistkey = (Long) parameters.getPrimaryParameter();
		BeanItemContainer<SelectValue>  procedures = preauthService.getProcedureforSpeciality(specialistkey);
		view.getcorrectionProcedureValues(procedures);
	}
	
	public void addSpecialityEdited(@Observes @CDIEvent(EDIT_CORRECTION_SPECIALITY_VALUES) final ParameterDTO parameters)
	{
		SpecialityCorrectionDTO specialistdto = (SpecialityCorrectionDTO) parameters.getPrimaryParameter();
		specialistdto.setHasChanges(true);
		view.addSpecialityEdited(specialistdto);
	}
	
	public void deleteactualSpeciality(@Observes @CDIEvent(DELETE_CORRECTION_SPECIALITY_VALUES) final ParameterDTO parameters)
	{
		Long specialistKey = (Long) parameters.getPrimaryParameter();
		view.removeSpecialityEdited(specialistKey);
	}
	
	public void addSpecialityProcedure(@Observes @CDIEvent(ADD_SPECIALITY_PROCEDURE_VALUES) final ParameterDTO parameters)
	{
		Long specId = (Long) parameters.getPrimaryParameter();
		ComboBox prodcomboBox = (ComboBox)parameters.getSecondaryParameter(0, ComboBox.class);
		SelectValue procValue = (SelectValue)parameters.getSecondaryParameter(1, SelectValue.class);
		view.addSpecialityProcedure(specId,prodcomboBox,procValue);
	}
	
	public void addDiganosisEdited(@Observes @CDIEvent(EDIT_DIGANOSIS_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		DiganosisCorrectionDTO diganosisCorrectionDTO = (DiganosisCorrectionDTO) parameters.getPrimaryParameter();
		diganosisCorrectionDTO.setHasChanges(true);
		view.addDiganosisEdited(diganosisCorrectionDTO);
	}
	
	public void deleteactualDiganosis(@Observes @CDIEvent(DELETE_DIGANOSIS_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		Long specialistKey = (Long) parameters.getPrimaryParameter();
		view.deleteactualDiganosis(specialistKey);
	}
	
	public void addProcedureEdited(@Observes @CDIEvent(EDIT_PROCEDURE_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		ProcedureCorrectionDTO procedureCorrectionDTO = (ProcedureCorrectionDTO) parameters.getPrimaryParameter();
		procedureCorrectionDTO.setHasChanges(true);
		view.addProcedureEdited(procedureCorrectionDTO);
	}
	
	public void deleteactualProcedure(@Observes @CDIEvent(DELETE_PROCEDURE_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		Long procedureKey = (Long) parameters.getPrimaryParameter();
		view.deleteactualProcedure(procedureKey);
	}
	public void addTreatingEdited(@Observes @CDIEvent(EDIT_TREATING_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		TreatingCorrectionDTO treatingCorrectionDTO = (TreatingCorrectionDTO) parameters.getPrimaryParameter();
		treatingCorrectionDTO.setHasChanges(true);
		view.addTreatingEdited(treatingCorrectionDTO);
	}
	
	public void deleteactualTreating(@Observes @CDIEvent(DELETE_TREATING_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		Long treatingKey = (Long) parameters.getPrimaryParameter();
		view.deleteactualTreating(treatingKey);
	}
	
	public void addImplantEdited(@Observes @CDIEvent(EDIT_IMPLANT_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		ImplantCorrectionDTO implantCorrectionDTO = (ImplantCorrectionDTO) parameters.getPrimaryParameter();
		implantCorrectionDTO.setHasChanges(true);
		view.addImplantEdited(implantCorrectionDTO);
	}
	
	public void deleteactualImplant(@Observes @CDIEvent(DELETE_IMPLANT_CORRECTION_VALUES) final ParameterDTO parameters)
	{
		Long implantKey = (Long) parameters.getPrimaryParameter();
		view.deleteactualImplant(implantKey);
	}
	
	public void generateFieldsBasedOnImplantApplicable(@Observes @CDIEvent(ACTUAL_IMPLANT_APPLICABLE_CHANGED) final ParameterDTO parameters)
	{
		Boolean isCked = (Boolean) parameters.getPrimaryParameter();
		view.generateFieldsBasedOnImplantApplicable(isCked);
	}
	
}
