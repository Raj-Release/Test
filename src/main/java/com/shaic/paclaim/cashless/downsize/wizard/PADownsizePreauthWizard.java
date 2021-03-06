package com.shaic.paclaim.cashless.downsize.wizard;

import java.util.List;
import java.util.Map;

import com.shaic.arch.GMVPView;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.preauth.dto.MedicalDecisionTableDTO;
import com.shaic.domain.preauth.ExclusionDetails;
import com.vaadin.v7.data.util.BeanItemContainer;

public interface PADownsizePreauthWizard  extends GMVPView {
	
	
	
	void buildSuccessLayout(String Message);

	void setDiagnosisSumInsuredValuesFromDB(List<MedicalDecisionTableDTO> medicalDecisionTableList);
	
	void setDownsizeAmount(Double amount);

	void setHospitalizationDetails(Map<Integer, Object> hospitalizationDetails);

	void setBalanceSumInsured(Double balanceSI, List<Double> copayValue);

	void setWizardPageReferenceData(Map<String, Object> referenceData);

	void setDiagnosisSumInsuredValuesFromDB(
			Map<String, Object> medicalDecisionTableValue, String diagnosis);

	void setExclusionDetails(
			BeanItemContainer<ExclusionDetails> icdCodeContainer);
	
	void showErrorMessage();
	
	
	void buildRRCRequestSuccessLayout(String rrcRequestNo);
	void buildValidationUserRRCRequestLayout(Boolean isValid);
	void loadRRCRequestDropDownValues(BeanItemContainer<SelectValue> mastersValueContainer);

	void viewClaimAmountDetails();

	void viewBalanceSumInsured(String intimationId);
	void setsubCategoryValues(BeanItemContainer<SelectValue> selectValueContainer,GComboBox subCategory,SelectValue value);
	void setsourceValues(BeanItemContainer<SelectValue> selectValueContainer,GComboBox source,SelectValue value);


}
