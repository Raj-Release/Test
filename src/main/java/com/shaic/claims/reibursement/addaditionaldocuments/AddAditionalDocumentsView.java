/**
 * 
 */
package com.shaic.claims.reibursement.addaditionaldocuments;

import org.vaadin.teemu.wizards.event.GWizardListener;

import com.shaic.arch.GMVPView;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.vaadin.v7.data.util.BeanItemContainer;

/**
 * @author ntv.srikanthp
 *
 */
public interface AddAditionalDocumentsView  extends GMVPView, GWizardListener {
	void buildSuccessLayout();
	void buildRRCRequestSuccessLayout(String rrcRequestNo);
	void buildValidationUserRRCRequestLayout(Boolean isValid);
	void loadRRCRequestDropDownValues(BeanItemContainer<SelectValue> mastersValueContainer);
	//void setWizardPageReferenceData(Map<String,Object> referenceData);
	void setsubCategoryValues(BeanItemContainer<SelectValue> selectValueContainer,GComboBox subCategory,SelectValue value);
	void setsourceValues(BeanItemContainer<SelectValue> selectValueContainer,GComboBox source,SelectValue value);

}
