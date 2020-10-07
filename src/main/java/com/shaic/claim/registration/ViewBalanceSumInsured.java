package com.shaic.claim.registration;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import com.shaic.domain.Intimation;
import com.shaic.domain.IntimationService;
import com.shaic.domain.PolicyService;
import com.shaic.domain.TmpCPUCode;
import com.shaic.newcode.wizard.dto.NewIntimationDto;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class ViewBalanceSumInsured extends Window {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbsoluteLayout mainLayout;
	private HorizontalLayout balanceSuminsuredhorizontal;
	private FormLayout formlayoutRight;
	private FormLayout formlayoutleft;
	private TextField balanceSI2;
	private TextField provAmt;
	private TextField sectionSI;
	private TextField balanceSI1;
	private TextField outstandingClaim;
	private TextField totalSI;
	private TextField cumulativeBonus;
	private TextField originalSI;
	private TextField age;
	private TextField balanceSIInsuredName;

	private TmpCPUCode tmpCpuCode;

	

	@EJB
	private IntimationService intimationService;

	@EJB
	private PolicyService policyService;
	

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public ViewBalanceSumInsured() {

	}

	@PostConstruct
	public void initView() {
		buildMainLayout();

		setCaption("Balance Sum Insured");
		setContent(mainLayout);
		setHeight("400px");
		setWidth("950px");
		setModal(true);
		setClosable(true);
		setResizable(true);

	}

	public void bindFieldGroup(String intimationNo) {
		Intimation intimation = intimationService.searchbyIntimationNo(intimationNo);

		NewIntimationDto intimationDto = intimationService.getIntimationDto(intimation);
		BeanItem<NewIntimationDto> item = new BeanItem<NewIntimationDto>(
				intimationDto);
		FieldGroup binder = new FieldGroup(item);
		binder.bindMemberFields(this);
		if (intimationDto != null) {
			tmpCpuCode = new TmpCPUCode();

			if (intimationDto.getCpuId() != null
					&& intimationDto.getCpuId() != 0) {
				tmpCpuCode = policyService.getTmpCpuCode(intimationDto
						.getCpuId());
			}
			balanceSIInsuredName.setValue(intimationDto.getInsuredPatientName());
			age.setValue(intimationDto.getInsuredAge());
			originalSI.setValue(intimationDto.getPolicy() != null ? String
					.valueOf(intimationDto.getInsuredPatient().getInsuredSumInsured())
					: "0");
			cumulativeBonus
					.setValue(intimationDto.getPolicy().getCummulativeBonus() != null ? String
							.valueOf(intimationDto.getPolicy().getCummulativeBonus()) : "0");
			Double value = ((originalSI.getValue() == null || originalSI
					.getValue() == "") ? Double.valueOf("0") : Double
					.valueOf(originalSI.getValue()))
					+ ((cumulativeBonus.getValue() == null || cumulativeBonus
							.getValue() != "") ? Double.valueOf("0") : Double
							.valueOf(cumulativeBonus.getValue()));
			String totalSumInsured = String.valueOf(value);
			totalSI.setValue(totalSumInsured);

			outstandingClaim.setValue("0");

			Double balanceSIValue = value
					- Double.valueOf(outstandingClaim.getValue());

			balanceSI1.setValue(String.valueOf(balanceSIValue));
			if (tmpCpuCode != null) {
				provAmt.setValue(tmpCpuCode.getProvisionAmount() != null ? tmpCpuCode
						.getProvisionAmount().toString() : "");
			}
			Double balanceSI1Value = Double
					.valueOf((balanceSI1.getValue() == null || balanceSI1
							.getValue() == "") ? "0" : balanceSI1.getValue())
					- Double.valueOf((provAmt.getValue() == null || provAmt
							.getValue() == "") ? "0" : provAmt.getValue());
			String totalBalanceSI = String.valueOf(balanceSI1Value);
			balanceSI2.setValue(totalBalanceSI);

		}
		setReadOnly(formlayoutleft);
		setReadOnly(formlayoutRight);
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	private void setReadOnly(FormLayout a_formLayout) {
		Iterator<Component> formLayoutLeftComponent = a_formLayout
				.getComponentIterator();
		while (formLayoutLeftComponent.hasNext()) {
			Component c = formLayoutLeftComponent.next();
			if (c instanceof com.vaadin.v7.ui.AbstractField) {
				TextField field = (TextField) c;
				field.setWidth("440px");
				field.setReadOnly(true);
				field.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
			}
		}
	}

	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		//Vaadin8-setImmediate() mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		balanceSuminsuredhorizontal = buildBalanceSuminsuredhorizontal();
		mainLayout.addComponent(balanceSuminsuredhorizontal,
				"top:0.0px;left:0.0px;");

		return mainLayout;
	}

	private HorizontalLayout buildBalanceSuminsuredhorizontal() {
		// common part: create layout
		balanceSuminsuredhorizontal = new HorizontalLayout();
		//Vaadin8-setImmediate() balanceSuminsuredhorizontal.setImmediate(false);
		balanceSuminsuredhorizontal.setWidth("100.0%");
		balanceSuminsuredhorizontal.setHeight("100.0%");
		balanceSuminsuredhorizontal.setMargin(false);

		// formlayoutleft
		formlayoutleft = buildFormlayoutleft();
		balanceSuminsuredhorizontal.addComponent(formlayoutleft);

		// formlayoutRight
		formlayoutRight = buildFormlayoutRight();
		balanceSuminsuredhorizontal.addComponent(formlayoutRight);

		return balanceSuminsuredhorizontal;
	}

	private FormLayout buildFormlayoutleft() {
		// common part: create layout
		formlayoutleft = new FormLayout();
		//Vaadin8-setImmediate() formlayoutleft.setImmediate(false);
		formlayoutleft.setWidth("-1px");
		formlayoutleft.setHeight("-1px");
		formlayoutleft.setMargin(true);
		formlayoutleft.setSpacing(true);

		// textField_23
		balanceSIInsuredName = new TextField();
		balanceSIInsuredName.setCaption("Name of the Insured");
		formlayoutleft.addComponent(balanceSIInsuredName);

		// textField_24
		age = new TextField();
		age.setCaption("Age");
		formlayoutleft.addComponent(age);

		// textField_25
		originalSI = new TextField();
		originalSI.setCaption("Original SI");
		formlayoutleft.addComponent(originalSI);

		// textField_26
		cumulativeBonus = new TextField();
		cumulativeBonus.setCaption("Cumulative Bonus");
		formlayoutleft.addComponent(cumulativeBonus);

		// textField_27
		totalSI = new TextField();
		totalSI.setCaption("Total SI");
		formlayoutleft.addComponent(totalSI);

		return formlayoutleft;
	}

	private FormLayout buildFormlayoutRight() {
		// common part: create layout
		formlayoutRight = new FormLayout();
		//Vaadin8-setImmediate() formlayoutRight.setImmediate(false);
		formlayoutRight.setMargin(true);
		formlayoutRight.setSpacing(true);
		outstandingClaim = new TextField();
		outstandingClaim.setCaption("Claims Outstanding");
		formlayoutRight.addComponent(outstandingClaim);

		// textField_33
		balanceSI1 = new TextField();
		balanceSI1.setCaption("Balance SI");
		formlayoutRight.addComponent(balanceSI1);

		sectionSI = new TextField();
		sectionSI.setCaption("Section II SI");
		formlayoutRight.addComponent(sectionSI);

		// textField_35
		provAmt = new TextField();
		provAmt.setCaption("Provision Amt");
		formlayoutRight.addComponent(provAmt);

		// textField_36
		balanceSI2 = new TextField();
		balanceSI2.setCaption("Balance SI");
		formlayoutRight.addComponent(balanceSI2);
		return formlayoutRight;
	}

}
