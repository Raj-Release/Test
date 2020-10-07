package com.shaic.claim.processdatacorrectionhistorical.search;

import java.util.List;
import java.util.Map;

import com.shaic.arch.GMVPView;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.processdatacorrection.search.DiganosisCorrectionDTO;
import com.shaic.claim.processdatacorrection.search.ImplantCorrectionDTO;
import com.shaic.claim.processdatacorrection.search.ProcedureCorrectionDTO;
import com.shaic.claim.processdatacorrection.search.SpecialityCorrectionDTO;
import com.shaic.claim.processdatacorrection.search.TreatingCorrectionDTO;
import com.shaic.claim.scoring.HospitalScoringDTO;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.ComboBox;

public interface DataCorrectionHistoricalView extends GMVPView{

	public void setReferenceData(Map<String, Object> referenceData);

	void buildSuccessLayout();
	
	void setScoringchanges(Boolean scoringchanges,List<HospitalScoringDTO> scoringDTOs);
	
void getcorrectionProcedureValues(BeanItemContainer<SelectValue> procedures);
	
	void addSpecialityEdited(SpecialityCorrectionDTO specialityCorrectionDTO);
	
	void removeSpecialityEdited(Long key);
	
	void addSpecialityProcedure(Long specId,ComboBox prodcomboBox,SelectValue procValue);

	void addDiganosisEdited(DiganosisCorrectionDTO diganosisCorrectionDTO);

	void deleteactualDiganosis(Long key);
	
	void addProcedureEdited(ProcedureCorrectionDTO procedureCorrectionDTO);

	void deleteactualProcedure(Long key);
	
	void addTreatingEdited(TreatingCorrectionDTO treatingCorrectionDTO);

	void deleteactualTreating(Long key);
	
}
