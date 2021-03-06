package com.shaic.claim.registration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.preauth.view.ViewPreviousClaimsTable;
import com.shaic.claim.premedical.dto.PreviousClaimsTableDTO;
import com.shaic.domain.Claim;
import com.shaic.domain.ClaimService;
import com.shaic.domain.Insured;
import com.shaic.domain.InsuredService;
import com.shaic.domain.IntimationService;
import com.shaic.domain.MasterService;
import com.shaic.domain.OPClaim;
import com.shaic.domain.PEDValidationService;
import com.shaic.domain.Policy;
import com.shaic.domain.PolicyService;
import com.shaic.domain.PreauthService;
import com.shaic.domain.ViewTmpClaim;
import com.shaic.domain.ViewTmpIntimation;
import com.shaic.domain.preauth.ViewTmpDiagnosis;
import com.shaic.domain.preauth.ViewTmpPreauth;
import com.shaic.ims.bpm.claim.DBCalculationService;
import com.shaic.newcode.wizard.domain.PreviousClaimMapper;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
//import com.google.inject.internal.asm.Label;

@ViewScoped
public class ViewPreviousClaims extends Window {

	//private static final String GROUP = "Group";
	private static final String FRESH = "Fresh";
	private static final String RISK_WISE = "Risk Wise";
	private static final String POLICY_WISE = "Policy Wise";
	private static final String INSURED_WISE = "Insured Wise";

	@EJB
	private PEDValidationService pedValidationService;
	
	@EJB
	private PreauthService preauthService;

	@EJB
	private ClaimService claimService;
	@EJB
	private PolicyService policyService;
	@EJB
	private IntimationService intimationService;
	@EJB
	private InsuredService insuredService;
	@EJB
	private MasterService masterService;

	private VerticalLayout claimlayout;
	
	private ComboBox cmbPolicyYear;
	

	@Inject
	private ViewPreviousClaimsTable preauthPreviousClaimsTable;

	private static final long serialVersionUID = -2905296721396984772L;

	public void showValues(String intimationNumber) {

		claimlayout = getPrivousClaimsLayout(intimationNumber);
		claimlayout.setMargin(true);
		this.setContent(claimlayout);
		final ViewTmpIntimation intimation = intimationService
				.searchbyIntimationNoFromViewIntimation(intimationNumber);
		getClaimByPolicyWise(intimation);
	}
	
	public void showValuesForOP(Long policyKey){
		
		claimlayout = getPrivousClaimsLayoutForOP(policyKey);
		claimlayout.setMargin(true);	
		this.setContent(claimlayout);
		
		getClaimByPolicyWiseForOP(policyKey);
		
	}

	@PostConstruct
	public void initView() {
		setCaption("Previous Claim Details");
		this.setHeight("400px");
		this.setWidth("950px");
		setModal(true);
		setClosable(true);
		setResizable(true);

	}

	public VerticalLayout getPrivousClaimsLayout(String intimationNumber) {
		HorizontalLayout optionLayout = buildViewType(intimationNumber);
		optionLayout.setWidth("100%");
		optionLayout.setSpacing(true);
		optionLayout.setMargin(true);
		claimlayout = new VerticalLayout();
		claimlayout.setMargin(true);
		
		
		Label policyLabel = new Label("Policy Year");
		policyLabel.addStyleName("");
		cmbPolicyYear = new ComboBox("Policy Year");
		BeanItemContainer<SelectValue> policyYearValues = getPolicyYearValues();
		
		cmbPolicyYear.setContainerDataSource(policyYearValues);
		cmbPolicyYear.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbPolicyYear.setItemCaptionPropertyId("value");
		
		claimlayout.addComponent(optionLayout);
//		claimlayout.addComponent(policyLabel);
		claimlayout.addComponent(cmbPolicyYear);
		claimlayout.setComponentAlignment(cmbPolicyYear, Alignment.MIDDLE_LEFT);
		
		final ViewTmpIntimation intimation = intimationService
				.searchbyIntimationNoFromViewIntimation(intimationNumber);
		
		addListener(intimation);
		
	
		
		return claimlayout;
	}
	
	public BeanItemContainer<SelectValue> getPolicyYearValues(){
		
		List<SelectValue> selectValueList = new ArrayList<SelectValue>();
		BeanItemContainer<SelectValue> container = new BeanItemContainer<SelectValue>(SelectValue.class);
		
		SelectValue selected = new SelectValue();
		selected.setId(1l);
		selected.setValue("All");
		selectValueList.add(selected);
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.YEAR, 1);
		int intYear = instance.get(Calendar.YEAR);
		Long year = Long.valueOf(intYear);
		for(Long i= year;i>=year-6;i--){
			SelectValue selectValue = new SelectValue();
			Long j = i-1;
			selectValue.setId(j);
			selectValue.setValue(""+j.intValue()+" - "+i.intValue());
			selectValueList.add(selectValue);
		}
		container.addAll(selectValueList);
		
		return container;
	}
	
	public VerticalLayout getPrivousClaimsLayoutForOP(Long policyKey) {
		HorizontalLayout optionLayout = buildViewTypeForOp(policyKey);
		optionLayout.setWidth("100%");
		optionLayout.setSpacing(true);
		optionLayout.setMargin(true);
		claimlayout = new VerticalLayout();
		claimlayout.setMargin(false);
		claimlayout.addComponent(optionLayout);
		return claimlayout;
		
	}
	

	public HorizontalLayout buildViewType(String intimationNo) {
		final ViewTmpIntimation intimation = intimationService
				.searchbyIntimationNoFromViewIntimation(intimationNo);
		String policyNumber = intimation.getPolicyNumber();
		Policy policy = policyService.getPolicy(policyNumber);
		final OptionGroup viewType = getOptionGroup(policy);
		viewType.addValueChangeListener(new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if (viewType.getValue() != null) {
					if (viewType.getValue().equals(POLICY_WISE)) {
						getClaimByPolicyWise(intimation);
						claimlayout.removeComponent(cmbPolicyYear);
						claimlayout.addComponent(cmbPolicyYear,1);
					} else if (viewType.getValue().equals(INSURED_WISE)) {
						getClaimByInsuredWise(intimation);
						claimlayout.removeComponent(cmbPolicyYear);
						
					} else if (viewType.getValue().equals(RISK_WISE)) {
						getClaimByRiskWise(intimation);
						claimlayout.removeComponent(cmbPolicyYear);
					}
				}
			
			}

		});
		getViewButton(intimation, viewType);
		HorizontalLayout optionLayout = new HorizontalLayout();
		optionLayout.addComponent(viewType);
		
//		optionLayout.addComponent(viewBtn);
		return optionLayout;
	}
	
	
	
	public HorizontalLayout buildViewTypeForOp(Long policyKey) {
		
		final Long policyId = policyKey;
       
		Policy policy = policyService.getPolicyByKey(policyKey);
		final OptionGroup viewType = getOptionGroup(policy);
		viewType.addValueChangeListener(new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if (viewType.getValue() != null) {
					if (viewType.getValue().equals(POLICY_WISE)) {
						getClaimByPolicyWiseForOP(policyId);
					} else if (viewType.getValue().equals(INSURED_WISE)) {
					//getClaimByInsuredWise(intimation);
					} else if (viewType.getValue().equals(RISK_WISE)) {
//						getClaimByRiskWise(intimation);
					}
				}
			
			}

			
		});
		//NativeButton viewBtn = getViewButtonForOP(policyKey, viewType);
		getViewButtonForOP(policyKey, viewType);
		HorizontalLayout optionLayout = new HorizontalLayout();
		optionLayout.addComponent(viewType);
//		optionLayout.addComponent(viewBtn);
		return optionLayout;
	}
	

	public OptionGroup getOptionGroup(Policy policy) {
		OptionGroup viewType = new OptionGroup();
//		//Vaadin8-setImmediate() viewType.setImmediate(false);
		viewType.addItem(POLICY_WISE);
		viewType.addItem(INSURED_WISE);
//		if ((policy.getPolicyType() != null && !StringUtils.equals(policy.getPolicyType().getValue(),""))
//				&& StringUtils.equalsIgnoreCase(policy.getPolicyType().getValue(),GROUP))
			viewType.addItem(RISK_WISE);
		viewType.setStyleName("inlineStyle");

		viewType.setValue(POLICY_WISE);
		return viewType;
	}

	private NativeButton getViewButton(final ViewTmpIntimation intimation,
			final OptionGroup viewType) {
		NativeButton viewBtn = new NativeButton();
		viewBtn.setCaption("View");
		viewBtn.setData(viewType);
		viewBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = -676865664871344469L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (viewType.getValue() != null) {
					if (viewType.getValue().equals(POLICY_WISE)) {
						getClaimByPolicyWise(intimation);
					} else if (viewType.getValue().equals(INSURED_WISE)) {
						getClaimByInsuredWise(intimation);
					} else if (viewType.getValue().equals(RISK_WISE)) {
						getClaimByRiskWise(intimation);
					}
				}

			}

		});
		return viewBtn;
	}
	
	private NativeButton getViewButtonForOP(final Long policyKey,
			final OptionGroup viewType) {
		NativeButton viewBtn = new NativeButton();
		viewBtn.setCaption("View");
		viewBtn.setData(viewType);
		viewBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = -676865664871344469L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (viewType.getValue() != null) {
					if (viewType.getValue().equals(POLICY_WISE)) {
						getClaimByPolicyWiseForOP(policyKey);
					} else if (viewType.getValue().equals(INSURED_WISE)) {
//						getClaimByInsuredWise(intimation);
					} else if (viewType.getValue().equals(RISK_WISE)) {
//						getClaimByRiskWise(intimation);
					}
				}

			}

		});
		return viewBtn;
	}
	
	private void addListener(final ViewTmpIntimation intimation){
		
		
		  cmbPolicyYear.addValueChangeListener(new ValueChangeListener() {
	   			
	   			@Override
	   			public void valueChange(ValueChangeEvent event) {
	   				SelectValue value = (SelectValue) event.getProperty().getValue();
	   				if(value != null){
	   					getClaimByPolicyYear(intimation, value.getId());
	   				}
	   			}
	   		});
		
	}

	private void getClaimByRiskWise(final ViewTmpIntimation intimation) {
		List<ViewTmpIntimation> intimationlist = getRiskWiseClaimList(intimation);
	List<PreviousClaimsTableDTO> claimList = getClaimList(intimationlist);
	
		preauthPreviousClaimsTable.setTableList(claimList);
		preauthPreviousClaimsTable.init(RISK_WISE, false, false);
		claimlayout.addComponent(preauthPreviousClaimsTable);
	}

	private void getClaimByInsuredWise(final ViewTmpIntimation intimation) {
		
		//List<ViewTmpIntimation> intimationlist = getInsuredWiseClaimList(intimation);
		Claim claimsByIntimationNumber = claimService.getClaimsByIntimationNumber(intimation.getIntimationId());
		DBCalculationService dbCalculationService = new DBCalculationService();
		List<PreviousClaimsTableDTO> previousClaimDTOList = dbCalculationService.getPreviousClaims(claimsByIntimationNumber.getKey(), claimsByIntimationNumber.getIntimation().getPolicy().getKey(), 
				claimsByIntimationNumber.getIntimation().getInsured().getKey(), SHAConstants.INSURED_WISE_SEARCH_TYPE);
		
//		List<PreviousClaimsTableDTO> claimList = getClaimList(intimationlist);
		preauthPreviousClaimsTable.init(INSURED_WISE, false, false);
		preauthPreviousClaimsTable.setTableList(previousClaimDTOList);

		claimlayout.addComponent(preauthPreviousClaimsTable);
	}

	private void getClaimByPolicyWise(final ViewTmpIntimation intimation) {

		
		/*List<ViewTmpClaim> currentClaim = claimService.getTmpClaimByIntimation(intimation.getKey());
		
		String policyNumber =intimation.getPolicyNumber();
		List<ViewTmpClaim> previousclaimsList = new ArrayList<ViewTmpClaim>();
		List<ViewTmpClaim> claimsByPolicyNumber = claimService
				.getViewTmpClaimsByPolicyNumber(policyNumber);
		
		Policy byPolicyNumber = policyService.getByPolicyNumber(policyNumber);
		previousclaimsList.addAll(claimsByPolicyNumber);
		
		previousclaimsList = getPreviousClaimForPreviousPolicy(byPolicyNumber.getRenewalPolicyNumber(), previousclaimsList);
		
		List<PreviousClaimsTableDTO> previousClaimDTOList = preauthService
				.getPreviousClaims(previousclaimsList,
						currentClaim.get(0).getClaimId());*/
		
		Claim claimsByIntimationNumber = claimService.getClaimsByIntimationNumber(intimation.getIntimationId());
		
		DBCalculationService dbCalculationService = new DBCalculationService();
		List<PreviousClaimsTableDTO> previousClaimDTOList = dbCalculationService.getPreviousClaims(claimsByIntimationNumber.getKey(), claimsByIntimationNumber.getIntimation().getPolicy().getKey(), 
				claimsByIntimationNumber.getIntimation().getInsured().getKey(), SHAConstants.POLICY_WISE_SEARCH_TYPE);

		preauthPreviousClaimsTable.init(POLICY_WISE, false, false);
		preauthPreviousClaimsTable.setTableList(previousClaimDTOList);
		claimlayout.addComponent(preauthPreviousClaimsTable);
	}
	
	public List<ViewTmpClaim> getPreviousClaimForPreviousPolicy(String policyNumber, List<ViewTmpClaim> generatedList) {
		try {
			Policy renewalPolNo = policyService.getByPolicyNumber(policyNumber);
			if(renewalPolNo != null) {
				List<ViewTmpClaim> previousPolicyPreviousClaims = claimService.getViewTmpClaimsByPolicyNumber(renewalPolNo.getPolicyNumber());
				if(previousPolicyPreviousClaims != null && !previousPolicyPreviousClaims.isEmpty()) {
					for (ViewTmpClaim viewTmpClaim : previousPolicyPreviousClaims) {
						if(!generatedList.contains(viewTmpClaim)) {
							generatedList.add(viewTmpClaim);
						}
					}
				}
				if(renewalPolNo != null && renewalPolNo.getRenewalPolicyNumber() != null ) {
					getPreviousClaimForPreviousPolicy(renewalPolNo.getRenewalPolicyNumber(), generatedList);
				} else {
					return generatedList;
				}
			}
		} catch(Exception e) {
			
		}
		return generatedList;
	}
	
	public List<ViewTmpClaim> getPreviousClaimInsuedWiseForPreviousPolicy(String policyNumber, List<ViewTmpClaim> generatedList,String healthCardNumber) {
		
			try {
				Policy renewalPolNo = policyService.getByPolicyNumber(policyNumber);
				if(renewalPolNo != null) {
					List<ViewTmpClaim> previousPolicyPreviousClaims = claimService.getViewTmpClaimsByPolicyNumber(renewalPolNo.getPolicyNumber());
					if(previousPolicyPreviousClaims != null && !previousPolicyPreviousClaims.isEmpty()) {
						for (ViewTmpClaim viewTmpClaim : previousPolicyPreviousClaims) {
							if(viewTmpClaim.getIntimation().getInsured().getHealthCardNumber().equalsIgnoreCase(healthCardNumber)){
								if(!generatedList.contains(viewTmpClaim)) {
									generatedList.add(viewTmpClaim);
								}
							}
						}
					}
					if(renewalPolNo != null && renewalPolNo.getRenewalPolicyNumber() != null ) {
						getPreviousClaimInsuedWiseForPreviousPolicy(renewalPolNo.getRenewalPolicyNumber(), generatedList,healthCardNumber);
					} else {
						return generatedList;
					}
				}
			} catch(Exception e) {
			
		}
		return generatedList;
	}
	
	
	public List<ViewTmpClaim> getPreviousClaimForPreviousPolicy(List<ViewTmpClaim> claimsListByPolicyNumber, List<ViewTmpClaim> generatedList) {
		for (ViewTmpClaim viewTmpClaim : claimsListByPolicyNumber) {
			if(viewTmpClaim.getIntimation() != null && viewTmpClaim.getIntimation().getPolicy() != null && viewTmpClaim.getIntimation().getPolicyNumber() != null) {
				System.out.println("PRevious Policy Number " + viewTmpClaim.getIntimation().getPolicyNumber());
				if(!generatedList.contains(viewTmpClaim)) {
					generatedList.add(viewTmpClaim);
					List<ViewTmpClaim> viewTmpClaimsByPolicyNumber = claimService.getViewTmpClaimsByPolicyNumber(viewTmpClaim.getIntimation().getPolicyNumber());
					getPreviousClaimForPreviousPolicy(viewTmpClaimsByPolicyNumber, generatedList);
				}
			}
		}
		return generatedList;
	}
	
	private void getClaimByPolicyYear(final ViewTmpIntimation intimation,Long policyYear){
		Long policyYearFrom = policyYear;
		Long policyYearTo = policyYear + 1l;
		Policy policyByPolicyNubember = policyService.getPolicyByPolicyNubember(intimation.getPolicyNumber());
		if(policyByPolicyNubember.getPolicyYear() != null && policyByPolicyNubember.getPolicyYear().equals(policyYearFrom)
				|| policyByPolicyNubember.getPolicyYear().equals(policyYearTo)
				|| policyYear.equals(1l)){
			getClaimByPolicyWise(intimation);
		}else{
			preauthPreviousClaimsTable.init(POLICY_WISE, false, false);
			claimlayout.addComponent(preauthPreviousClaimsTable);
			Notification.show("No records for this time period", Type.ERROR_MESSAGE);
		}
	}
	
	private void getClaimByPolicyWiseForOP(Long policyKey){
		Policy policy = policyService.getPolicyByKey(policyKey);
		List<OPClaim> claimsByPolicyNumber = claimService
				.getOPClaimsByPolicyNumber(policy.getPolicyNumber());
		List<PreviousClaimsTableDTO> claimList = preauthService.getPreviousClaimForOP(claimsByPolicyNumber);

		preauthPreviousClaimsTable.init(POLICY_WISE, false, false);
		preauthPreviousClaimsTable.setTableList(claimList);
		claimlayout.addComponent(preauthPreviousClaimsTable);
	}

	private List<ViewTmpIntimation> getRiskWiseClaimList(final ViewTmpIntimation intimation) {
		List<ViewTmpIntimation> intimationlist = policyService
				.getTmpIntimationListByPolicy(intimation
						.getPolicyNumber());
		Policy policy = policyService.getPolicy(intimation
				.getPolicyNumber());
		intimationlist.remove(intimation);
		if (policy.getRenewalPolicyNumber() != null
				&& !policy.getPolicyType().equals(FRESH)) {
			List<ViewTmpIntimation> previousIntimationlist = policyService
					.getTmpIntimationListByPolicy(policy.getRenewalPolicyNumber());
			if (previousIntimationlist.size() != 0)
				intimationlist.addAll(previousIntimationlist);

		}
		return intimationlist;
	}

	/*private List<ViewTmpIntimation> getInsuredWiseClaimList(final ViewTmpIntimation intimation) {
		List<ViewTmpIntimation> intimationlist = policyService
				.getViewTmpIntimationListByInsured(String.valueOf(intimation.getInsured()
						.getInsuredId()));
		String PreviousInsuredId = getPreviousInsuredNumber(intimation);
		intimationlist.remove(intimation);
		if (PreviousInsuredId != null) {
			List<ViewTmpIntimation> previousIntimationlist = policyService
					.getViewTmpIntimationListByInsured(PreviousInsuredId);
			if (previousIntimationlist.size() != 0) {
				intimationlist.addAll(previousIntimationlist);
			}

		}
		
		return intimationlist;
	}*/
	

	/*private List<ViewTmpIntimation> getPolicyWiseClaimList(final ViewTmpIntimation intimation) {
		List<ViewTmpIntimation> intimationlist = policyService
				.getTmpIntimationListByPolicy(intimation.getPolicy()
						.getPolicyNumber());
		Policy policy = policyService.getPolicy(intimation.getPolicy()
				.getPolicyNumber());
		intimationlist.remove(intimation);
		if (policy.getRenewalPolicyNumber() != null) {
			List<ViewTmpIntimation> previousIntimationlist = policyService
					.getTmpIntimationListByPolicy(policy.getRenewalPolicyNumber());
			if (previousIntimationlist.size() != 0)
				intimationlist.addAll(previousIntimationlist);

		}
		return intimationlist;
	}*/

	/*private ViewIntimationStatus getClaimStatus(Item item) {
		Claim a_claim = claimService.getClaimByKey((Long) (item
				.getItemProperty("key").getValue()));

		Intimation intimation = a_claim.getIntimation();

		Hospitals hospital = policyService.getVWHospitalByKey(intimation
				.getHospital());

		// IntimationsDto a_intimationDto = DtoConverter
		// .intimationToIntimationDTO(intimation, hospital);
		// ClaimDto a_claimDto = DtoConverter.claimToClaimDTO(a_claim,
		// hospital);

		DtoConverter dtoConverter = new DtoConverter();
		ClaimDto a_claimDto = claimService.claimToClaimDTO(a_claim);

		// ViewIntimationStatus intimationStatus = new ViewIntimationStatus(
		// a_claimDto, a_intimationDto,
		// intimation.getPolicy().getStatus() == null);

		ViewIntimationStatus intimationStatus = new ViewIntimationStatus(
				a_claimDto, a_claimDto.getNewIntimationDto(), intimation
						.getPolicy().getActiveStatus() == null);

		return intimationStatus;
	}*/

	private String getPreviousInsuredNumber(ViewTmpIntimation a_intimation) {

		/*TmpInsured tmpInsured = insuredService.getInsured(a_intimation
				.getPolicy().getInsuredId());*/
		Insured insured = insuredService.getCLSInsured(String.valueOf(a_intimation
				.getInsured().getInsuredId()));
		
		// TmpInsured tmpInsured = insuredService.getInsured(a_intimation
		// .getPolicy().getPolicyNumber(), a_intimation.getPolicy()
		// .getInsuredFirstName(), a_intimation.getPolicy()
		// .getInsuredDob());
		if (insured != null)
			return insured.getRelationshipwithInsuredId().getValue();
		return null;

	}

	private List<PreviousClaimsTableDTO> getClaimList(
			List<ViewTmpIntimation> intimationlist) {
		List<PreviousClaimsTableDTO> claimsTableDTOs = new ArrayList<PreviousClaimsTableDTO>();
		PreviousClaimMapper previousClaimMapper = PreviousClaimMapper.getInstance();
		for (ViewTmpIntimation a_intimation : intimationlist) {
			ViewTmpClaim a_claim = claimService.getTmpClaimforIntimation(a_intimation
					.getKey());
			List<ViewTmpDiagnosis> pedValidationsList = pedValidationService
					.getDiagnosisFromViewTmpDiagnosis(a_intimation.getKey());
			if (a_claim != null) {
				PreviousClaimsTableDTO previousClaimsTableDTO = previousClaimMapper
						.getPreviousClaimsTableDTO(a_claim);
				
				if(previousClaimsTableDTO.getKey() != null){
					ViewTmpPreauth previousPreauth = preauthService.getPreviousPreauthFromTmpPreauth(previousClaimsTableDTO.getKey());
					if(previousPreauth != null){
						previousClaimsTableDTO.setClaimAmount(preauthService.getPreauthReqAmtFromTmpPreauth(previousPreauth.getKey(), previousClaimsTableDTO.getKey()));
					}
					Long claimedAmountForROD = preauthService.getClaimedAmountFromTmpReimbursement(previousClaimsTableDTO.getKey());
					if(claimedAmountForROD != null && claimedAmountForROD > 0){
						previousClaimsTableDTO.setClaimAmount(String.valueOf(claimedAmountForROD));
					}
				}
				
				//Date tempDate = SHAUtils.formatTimestamp(previousClaimsTableDTO.getAdmissionDate());
				if(a_intimation.getAdmissionDate() != null){
					previousClaimsTableDTO.setAdmissionDate(SHAUtils.formatDate(a_intimation.getAdmissionDate()));
				}
				
				claimsTableDTOs.add(previousClaimsTableDTO);
				String digonizeString = " ";
				if (pedValidationsList.size() != 0
						&& pedValidationsList != null) {

					for (ViewTmpDiagnosis pedValidation : pedValidationsList) {
						digonizeString = (digonizeString == " " ? ""
								: digonizeString + ", ")
								+ masterService.getDiagnosis(pedValidation
										.getDiagnosisId());
					}
				}
				previousClaimsTableDTO.setDiagnosis(digonizeString);
			}
		}

		return claimsTableDTOs;
	}
	
	
	
	
}
