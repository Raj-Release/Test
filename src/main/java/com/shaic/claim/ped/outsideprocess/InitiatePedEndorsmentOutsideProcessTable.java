package com.shaic.claim.ped.outsideprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.vaadin.addon.cdimvp.ViewComponent;
import org.vaadin.csvalidation.CSValidator;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.pedrequest.approve.PEDRequestDetailsApprovePresenter;
import com.shaic.claim.pedrequest.process.PEDRequestDetailsProcessPresenter;
import com.shaic.claim.pedrequest.teamlead.PEDRequestDetailsTeamLeadPresenter;
import com.shaic.claim.pedrequest.view.ViewPEDRequestPresenter;
import com.shaic.claim.pedrequest.view.ViewPEDTableDTO;
import com.shaic.domain.ReferenceTable;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class InitiatePedEndorsmentOutsideProcessTable extends ViewComponent {
	
	private Map<ViewPEDTableDTO, HashMap<String, AbstractField<?>>> tableItem = new HashMap<ViewPEDTableDTO, HashMap<String, AbstractField<?>>>();

	/***
	 * Bean object fetch from db
	 */
	BeanItemContainer<ViewPEDTableDTO> data = new BeanItemContainer<ViewPEDTableDTO>(ViewPEDTableDTO.class);
	
	private Table table;

	private Button btnAdd;
	
	private Map<String, Object> referenceData;
	
	private BeanItemContainer<SelectValue> icdBlock;
	
	private BeanItemContainer<SelectValue> icdCode;
	
	private BeanItemContainer<SelectValue> description;
	
	private String pedCode;
	
	private List<String> errorMessages;
	
	private static Validator validator;
	
	public List<ViewPEDTableDTO> deletedDTO;
	
	private String presenterString;
	
	private Long intimationKey;
	
	private Long insuredKey;
	
	private Long suggestionKey;
	
	public void init(String presenterString) {
		
		this.presenterString = presenterString;
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		this.errorMessages = new ArrayList<String>();
		deletedDTO = new ArrayList<ViewPEDTableDTO>();
		btnAdd = new Button();
		btnAdd.setStyleName("link");
		btnAdd.setIcon(new ThemeResource("images/addbtn.png"));
		HorizontalLayout btnLayout = new HorizontalLayout(btnAdd);
		btnLayout.setWidth("100%");
		btnLayout.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(btnLayout);
		
		initTable();
		table.setWidth("100%");
		table.setPageLength(table.getItemIds().size());
		
		addListener();
		
		layout.addComponent(table);

		setCompositionRoot(layout);
	}
	
	private void addListener() {
		btnAdd.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 5852089491794014554L;

			@Override
			public void buttonClick(ClickEvent event) {
				//if(data.size()==0){
				BeanItem<ViewPEDTableDTO> addItem = data.addItem(new ViewPEDTableDTO());
				//btnAdd.setVisible(false);
				//}
//				manageListeners();
			}
		});
	}
	
	
	public void setReferenceData(Map<String, Object> referenceData) {
		this.referenceData = referenceData;
	}
	
	void initTable() {
		// Create a data source and bind it to a table
		table = new Table("", data);
		table.addStyleName("generateColumnTable");
		table.setWidth("100%");
		table.setPageLength(table.getItemIds().size());
		
		table.addGeneratedColumn("Delete", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 5936665477260011479L;

			@Override
			public Object generateCell(final Table source, final Object itemId,
					Object columnId) {
//				final Button deleteButton = new Button("Delete");
//				deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
//				deleteButton.setWidth("-1px");
//				deleteButton.setHeight("-10px");
//				ViewPEDTableDTO dto = (ViewPEDTableDTO) itemId;
//				if (dto.getEnableOrDisable() != null) {
//					deleteButton.setEnabled(dto.getEnableOrDisable());
//				}
//				deleteButton.setData(itemId);
//				deleteButton.addClickListener(new Button.ClickListener() {
//					private static final long serialVersionUID = 6100598273628582002L;
//
//					public void buttonClick(ClickEvent event) {
//						Object currentItemId = event.getButton().getData();
//						table.removeItem(currentItemId);
////						if (table.getItemIds().size() > 1) {
////							
////						} else {
////							HorizontalLayout layout = new HorizontalLayout(
////									new Label("One Diagnosis is Mandatory."));
////							layout.setMargin(false);
////							layout.setWidth("100%");
////
////							final ConfirmDialog dialog = new ConfirmDialog();
////							dialog.setCaption("");
////							// dialog.setClosable(false);
////							dialog.setClosable(true);
////							dialog.setContent(layout);
////							dialog.setWidth("250px");
////							// dialog.setResizable(false);
////							dialog.setResizable(true);
////							dialog.setModal(true);
////							dialog.show(getUI().getCurrent(), null, true);
////						}
//					}
//				});
//				// deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
//				return deleteButton;
				

				final Button deleteButton = new Button("Delete");
//				ViewPEDTableDTO dto = (ViewPEDTableDTO) itemId;
//				Boolean isEnabled = (null != dto && null != dto.getRecTypeFlag() && dto.getRecTypeFlag().toLowerCase().equalsIgnoreCase("c") ) ? false: true;
//				deleteButton.setEnabled(isEnabled);
				deleteButton.setData(itemId);
				deleteButton.addClickListener(new Button.ClickListener() {
					private static final long serialVersionUID = 6100598273628582002L;

					public void buttonClick(ClickEvent event) {
						final ViewPEDTableDTO currentItemId = (ViewPEDTableDTO) event.getButton().getData();
						if (table.getItemIds().size() > 1) {
							
							ConfirmDialog dialog = ConfirmDialog
									.show(getUI(),
											"Confirmation",
											"Do you want to Delete ?",
											"No", "Yes", new ConfirmDialog.Listener() {

												public void onClose(ConfirmDialog dialog) {
													if (!dialog.isConfirmed()) {
														// Confirmed to continue
														ViewPEDTableDTO dto =  (ViewPEDTableDTO)currentItemId;
														if(dto.getKey() != null && dto.getPedCode() != null) {
															deletedDTO.add((ViewPEDTableDTO)currentItemId);
														}
														table.removeItem(currentItemId);
													} else {
														// User did not confirm
													}
												}
											});
							dialog.setClosable(false);
							
						} else {
							HorizontalLayout layout = new HorizontalLayout(
									new Label("One Diagnosis is Mandatory."));
							layout.setMargin(true);
							final ConfirmDialog dialog = new ConfirmDialog();
							dialog.setCaption("");
							dialog.setClosable(true);
							dialog.setContent(layout);
							dialog.setResizable(false);
							dialog.setModal(true);
							dialog.show(getUI().getCurrent(), null, true);
						}
						
//						if (table.getItemIds().size() > 1) {
//							
//						} else {
//							HorizontalLayout layout = new HorizontalLayout(
//									new Label("One Diagnosis is Mandatory."));
//							layout.setMargin(false);
//							layout.setWidth("100%");
//
//							final ConfirmDialog dialog = new ConfirmDialog();
//							dialog.setCaption("");
//							// dialog.setClosable(false);
//							dialog.setClosable(true);
//							dialog.setContent(layout);
//							dialog.setWidth("250px");
//							// dialog.setResizable(false);
//							dialog.setResizable(true);
//							dialog.setModal(true);
//							dialog.show(getUI().getCurrent(), null, true);
//						}
					}
				});
				// deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
				
				if(suggestionKey != null && (ReferenceTable.PED_SUGGESTION_SUG004.equals(suggestionKey) || ReferenceTable.PED_SUGGESTION_SUG010.equals(suggestionKey))){
					deleteButton.setEnabled(false);
				}
				else{
					deleteButton.setEnabled(true);
				}
				
				return deleteButton;
			
			}
		});
		
		table.setVisibleColumns(new Object[] { "pedCode","description", "icdChapter", "icdBlock", "icdCode", "source","doctorRemarks", "othersSpecify","Delete" });

		table.setColumnHeader("pedCode", "Description");
		table.setColumnHeader("description", "Ped Code");
		table.setColumnHeader("icdChapter", "ICD Chapter");
		table.setColumnHeader("icdBlock", "ICD Block");
		table.setColumnHeader("icdCode", "ICD Code");
		table.setColumnHeader("source", "Source");
		table.setColumnHeader("othersSpecify", "Others Specify");
		table.setColumnHeader("doctorRemarks", "Doctors Remarks");
		table.setEditable(true);
//		manageListeners();

		// Use a custom field factory to set the edit fields as immediate.
		// This is used when the table is in editable mode.
		table.setTableFieldFactory(new ImmediateFieldFactory());

	}
	
	public void manageListeners() {

		for (ViewPEDTableDTO newInitiatePedEndorsement : tableItem.keySet()) {
			HashMap<String, AbstractField<?>> combos = tableItem.get(newInitiatePedEndorsement);

			final GComboBox icdChapterCombo = (GComboBox) combos.get("icdChapter");
			final GComboBox icdBlockCombo = (GComboBox) combos.get("icdBlock");
			final GComboBox ickCodeCombo = (GComboBox) combos.get("icdCode");
			final TextField descriptionCombo=(TextField)combos.get("description");
			
			addICDChapterListener(icdChapterCombo, icdBlockCombo);
			if(newInitiatePedEndorsement.getIcdChapter() != null) {
				addICDBlock(newInitiatePedEndorsement.getIcdChapter().getId(), icdBlockCombo, newInitiatePedEndorsement.getIcdBlock() );
			}

		}
	}
	
	public class ImmediateFieldFactory extends DefaultFieldFactory {
		private static final long serialVersionUID = -2192723245525925990L;

		@Override
		public Field<?> createField(Container container, Object itemId,
				Object propertyId, Component uiContext) {
			ViewPEDTableDTO initiateDTO = (ViewPEDTableDTO) itemId;
			Map<String, AbstractField<?>> tableRow = null;

			if (tableItem.get(initiateDTO) == null) {
				tableRow = new HashMap<String, AbstractField<?>>();
				tableItem.put(initiateDTO, new HashMap<String, AbstractField<?>>());
			} else {
				tableRow = tableItem.get(initiateDTO);
			}
			
			if("pedCode".equals(propertyId)) {
				GComboBox box = new GComboBox();
				addPedCodeValues(box);
				tableRow.put("pedCode", box); 
				final TextField pedCodeValue=(TextField)tableRow.get("description");
				box.setData(initiateDTO);
				addPEDCodeListener(box, pedCodeValue);
				return box;
			} 
				else if ("icdChapter".equals(propertyId)) {
				GComboBox box = new GComboBox();
				box.setWidth("150px");
				tableRow.put("icdChapter", box);
				final GComboBox icdBlock = (GComboBox) tableRow.get("icdBlock");
				box.setData(initiateDTO);
				addICDChapterListener(box, icdBlock);
				addICDChapterValues(box);
				return box;
			} else if ("icdBlock".equals(propertyId)) {
				GComboBox box = new GComboBox();
				box.setWidth("150px");
				box.setData(initiateDTO);
				tableRow.put("icdBlock", box);
				GComboBox icdCodeCmb = (GComboBox) tableRow.get("icdCode");
				addICDBlockListener(box, icdCodeCmb);
				if(initiateDTO.getIcdChapter() != null) {
					addICDBlock(initiateDTO.getIcdChapter().getId(), box, initiateDTO.getIcdBlock());
				}
				return box;
			} else if("icdCode".equals(propertyId)) {
				GComboBox box = new GComboBox();
				box.setWidth("150px");
				box.setData(initiateDTO);
				tableRow.put("icdCode", box);
				if(initiateDTO.getIcdBlock() != null) {
					addICDCode(initiateDTO.getIcdBlock().getId(), box, initiateDTO.getIcdCode());
				}
				return box;
			} else if("source".equals(propertyId)) {
				GComboBox box = new GComboBox();
				box.setWidth("200px");
				tableRow.put("source", box);
				box.setData(initiateDTO);
				addCommonValues(box, "source");
				TextField othersText=(TextField)tableRow.get("othersSpecify");
				addSourceListener(box, othersText);
				return box;
			}
			else if("othersSpecify".equals(propertyId)) {
				TextField field = new TextField();
				field.setWidth("200px");
				field.setNullRepresentation("");
				field.setEnabled(false);
				field.setMaxLength(100);
				CSValidator validator = new CSValidator();
				validator.extend(field);
				validator.setRegExp("^[a-zA-Z 0-9.]*$");
				validator.setPreventInvalidTyping(true);
				tableRow.put("othersSpecify", field);
				if(initiateDTO.getSource() != null) {
					if(("Others").equals(initiateDTO.getSource().getValue())){
						field.setEnabled(true);
					}
				}
				return field;
			}
			else if("doctorRemarks".equals(propertyId)) {
				TextField field = new TextField();
				field.setWidth("200px");
				field.setNullRepresentation("");
				field.setMaxLength(100);
				//Vaadin8-setImmediate() field.setImmediate(true);
				field.setReadOnly(false);
				tableRow.put("doctorRemarks", field);
				return field;
			}
			else if("description".equals(propertyId)) {
//				ComboBox box = new ComboBox();
//				box.setWidth("150px");
//				tableRow.put("description", box);
//				box.setData(initiateDTO);
				TextField field=new TextField();
				field.setWidth("200px");
				field.setNullRepresentation("");
				field.setEnabled(false);
				field.setData(initiateDTO);
				field.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
				tableRow.put("description", field);
				if(initiateDTO.getPedCode() != null) {
					//addPEDCode(initiateDTO.getDescription().getId(), field, initiateDTO.getDescription());
				}
				return field;
			}
//			else if("description".equals(propertyId)) {
//				TextField field = new TextField();
//				field.setWidth("200px");
//				field.setNullRepresentation("");
//				field.setReadOnly(false);
//				field.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
//				tableRow.put("description", field);
//				return field;
//			}
			else {
				Field<?> field = super.createField(container, itemId,
						propertyId, uiContext);

				if (field instanceof TextField)
					field.setWidth("100%");
				return field;
			}
		}
	}
	
	private void addPedCodeValues(GComboBox pedCombo) {
		@SuppressWarnings("unchecked")
		BeanItemContainer<SelectValue> diagnosis = (BeanItemContainer<SelectValue>) referenceData
				.get("pedCode");
		pedCombo.setContainerDataSource(diagnosis);
		pedCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		pedCombo.setItemCaptionPropertyId("value");
		pedCombo.setNullSelectionAllowed(false);
		
	    pedCombo.setValue(referenceData.get("pedCode"));
		
	}
	
	private void addCommonValues(ComboBox diagnosisCombo, String tableColumnName) {
		@SuppressWarnings("unchecked")
		BeanItemContainer<SelectValue> commonValues = (BeanItemContainer<SelectValue>) referenceData
				.get(tableColumnName);
		diagnosisCombo.setContainerDataSource(commonValues);
		diagnosisCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		diagnosisCombo.setItemCaptionPropertyId("value");
		
		diagnosisCombo.setValue(referenceData.get(tableColumnName));
		
	}
	
	@SuppressWarnings("unchecked")
	public void addICDChapterValues(ComboBox comboBox) {
		BeanItemContainer<SelectValue> icdChapter = (BeanItemContainer<SelectValue>) referenceData
				.get("icdChapter");
		comboBox.setContainerDataSource(icdChapter);
		comboBox.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		comboBox.setItemCaptionPropertyId("value");
		comboBox.setNullSelectionAllowed(false);

	}
	
	@SuppressWarnings("unused")
	private void addICDChapterListener(final GComboBox icdChpterCombo,
			final GComboBox icdBlockCombo) {
		if (icdChpterCombo != null) {
			icdChpterCombo.addListener(new Listener() {
				private static final long serialVersionUID = -4865225814973226596L;

				@Override
				public void componentEvent(Event event) {
					GComboBox component = (GComboBox) event.getComponent();
					ViewPEDTableDTO newInitiatePedDTO = (ViewPEDTableDTO) component.getData();
					HashMap<String, AbstractField<?>> hashMap = tableItem.get(newInitiatePedDTO);
					GComboBox comboBox = (GComboBox) hashMap.get("icdBlock");
					if (newInitiatePedDTO != null) {
						if(newInitiatePedDTO.getIcdChapter() != null) {
							if(comboBox != null) {
								addICDBlock(newInitiatePedDTO.getIcdChapter().getId(), comboBox, newInitiatePedDTO.getIcdBlock());
							}
						}
						
						if(component.getValue() != null){ 
							
							Long diagKey = newInitiatePedDTO.getPedCode() != null ? newInitiatePedDTO.getPedCode().getId() : null;
							GComboBox icdChapterCombo = (GComboBox) hashMap.get("pedCode");
							SelectValue icdChapter = component.getValue() != null ? (SelectValue) component.getValue() : null;       //  (SelectValue)icdChapterCombo.getValue();   
									
							if(icdChapter != null && icdChapter.getId() != null && suggestionKey != null && !(suggestionKey.equals(ReferenceTable.PED_SUGGESTION_SUG002)) && !(suggestionKey.equals(ReferenceTable.PED_SUGGESTION_SUG004)) && diagKey != null){
															
								checkPEDAlreadyAvailable(suggestionKey, diagKey, icdChapter.getId(), newInitiatePedDTO);
							}
						}
												
					}
				}
			});
		}

	}
	
	@SuppressWarnings("unused")
	private void addPEDCodeListener(final GComboBox diagCombo,
			final TextField icdBlockCombo) {
		if (diagCombo != null) {
			diagCombo.addListener(new Listener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void componentEvent(Event event) {
					GComboBox component = (GComboBox) event.getComponent();
					ViewPEDTableDTO newInitiatePedDTO = (ViewPEDTableDTO) component.getData();
					HashMap<String, AbstractField<?>> hashMap = tableItem.get(newInitiatePedDTO);
					TextField field = (TextField) hashMap.get("description");
					final GComboBox icdChapterCombo = (GComboBox) hashMap.get("icdChapter");
					SelectValue icdChapter = (SelectValue)icdChapterCombo.getValue();
//					GComboBox diagCombo = (GComboBox) hashMap.get("pedCode");
					SelectValue diagSelect = (SelectValue) component.getValue();
					Long diagKey = diagSelect != null ? diagSelect.getId() : null;   //  newInitiatePedDTO.getPedCode().getId();
					
					if (diagSelect != null && newInitiatePedDTO != null) {
						if(newInitiatePedDTO.getPedCode() != null) {
							if(field != null) {
								addPEDCode(newInitiatePedDTO.getPedCode().getId(), field, null);
							}
							
							if(icdChapter != null && icdChapter.getId() != null && suggestionKey != null && !(suggestionKey.equals(ReferenceTable.PED_SUGGESTION_SUG002)) && !(suggestionKey.equals(ReferenceTable.PED_SUGGESTION_SUG004)) && diagKey != null){
								
								checkPEDAlreadyAvailable(suggestionKey, diagKey, icdChapter.getId(), newInitiatePedDTO);
							}
						}
						
						
					}
				}
			});
		}
	}
	@SuppressWarnings("unused")
	private void addICDBlockListener(final ComboBox icdBlockCombo,
			final ComboBox icdCodeCombo) {
		if (icdBlockCombo != null) {
			icdBlockCombo.addListener(new Listener() {
				private static final long serialVersionUID = -4865225814973226596L;

				@Override
				public void componentEvent(Event event) {
					ComboBox component = (ComboBox) event.getComponent();
					ViewPEDTableDTO newInitiatePedEndorsementDTO = (ViewPEDTableDTO) component.getData();
					HashMap<String, AbstractField<?>> hashMap = tableItem.get(newInitiatePedEndorsementDTO);
					ComboBox comboBox = (ComboBox) hashMap.get("icdCode");
					
					if (newInitiatePedEndorsementDTO != null) {
						if(newInitiatePedEndorsementDTO.getIcdBlock() != null) {
							
							if(comboBox != null) {
								addICDCode(newInitiatePedEndorsementDTO.getIcdBlock().getId(), comboBox, newInitiatePedEndorsementDTO.getIcdCode());
							}
						}
						
					}
				}				
			});
		}

	}
	
	@SuppressWarnings("unused")
	private void addSourceListener(final ComboBox icdBlockCombo,
			final TextField icdCodeCombo) {
		if (icdBlockCombo != null) {
			icdBlockCombo.addListener(new Listener() {
				private static final long serialVersionUID = -4865225814973226596L;

				@Override
				public void componentEvent(Event event) {
					ComboBox component = (ComboBox) event.getComponent();
					ViewPEDTableDTO newInitiatePedEndorsementDTO = (ViewPEDTableDTO) component.getData();
					HashMap<String, AbstractField<?>> hashMap = tableItem.get(newInitiatePedEndorsementDTO);
					TextField text = (TextField) hashMap.get("othersSpecify");
					if (newInitiatePedEndorsementDTO != null) {
						if(newInitiatePedEndorsementDTO.getSource() != null) {
							if(("Others").equals(newInitiatePedEndorsementDTO.getSource().getValue())){
								if(text != null){
								text.setEnabled(true);
								}

							}
							else
							{
								if(text != null){
									text.setValue(null);
									text.setEnabled(false);
								}
							}
						}
					}
				}
			});
		}

	}
	
	public void addICDBlock(Long icdChpterComboKey, ComboBox icdBlockCombo, SelectValue value) {
		if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATOR)){
			fireViewEvent(ViewPEDRequestPresenter.GET_ICD_BLOCK, icdChpterComboKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_PROCESSOR)){
			fireViewEvent(PEDRequestDetailsProcessPresenter.GET_ICD_BLOCK, icdChpterComboKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_APPROVER)){
			fireViewEvent(PEDRequestDetailsApprovePresenter.GET_ICD_BLOCK, icdChpterComboKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATE_OUTSIDE_PROCESS)){
			fireViewEvent(InitiatePedPresenter.GET_ICD_BLOCK, icdChpterComboKey);
		}
		else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_TEAM_LEAD)){
			fireViewEvent(PEDRequestDetailsTeamLeadPresenter.PED_TL_GET_ICD_BLOCK, icdChpterComboKey);
		}
		icdBlockCombo.setContainerDataSource(icdBlock);
		icdBlockCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		icdBlockCombo.setItemCaptionPropertyId("value");
		icdBlockCombo.setNullSelectionAllowed(false);
		
		if(value != null) {
			icdBlockCombo.setValue(value);
		}
	}
	
	public void addPEDCode(Long icdChpterComboKey, TextField field, SelectValue value) {
		
		
		if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATOR)){
			fireViewEvent(ViewPEDRequestPresenter.GET_PED_CODE, icdChpterComboKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_PROCESSOR)){
			fireViewEvent(PEDRequestDetailsProcessPresenter.GET_PED_CODE, icdChpterComboKey);
		}
		else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_APPROVER)){
			fireViewEvent(PEDRequestDetailsApprovePresenter.GET_PED_CODE, icdChpterComboKey);
		}
		else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATE_OUTSIDE_PROCESS)){
			fireViewEvent(InitiatePedPresenter.GET_PED_CODE, icdChpterComboKey);
		}
		else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_TEAM_LEAD)){
			fireViewEvent(PEDRequestDetailsTeamLeadPresenter.PED_TL_GET_PED_CODE, icdChpterComboKey);
		}
//		icdBlockCombo.setContainerDataSource(description);
//		icdBlockCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
//		icdBlockCombo.setItemCaptionPropertyId("value");
//		
//		if(value != null) {
//			icdBlockCombo.setValue(value);
//		}
		field.setValue(this.pedCode);
	}
	
	public void addICDCode(Long icdBlockKey, ComboBox icdCodeCombo, SelectValue value) {
		
		if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATOR)){
			fireViewEvent(ViewPEDRequestPresenter.GET_ICD_CODE, icdBlockKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_PROCESSOR)){
			fireViewEvent(PEDRequestDetailsProcessPresenter.GET_ICD_CODE, icdBlockKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_APPROVER)){
			fireViewEvent(PEDRequestDetailsApprovePresenter.GET_ICD_CODE, icdBlockKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATE_OUTSIDE_PROCESS)){
			fireViewEvent(InitiatePedPresenter.GET_ICD_CODE, icdBlockKey);
		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_TEAM_LEAD)){
			fireViewEvent(PEDRequestDetailsTeamLeadPresenter.PED_TL_GET_ICD_CODE, icdBlockKey);
		}
		
		
		
		icdCode.addBean(value);
		icdCodeCombo.setContainerDataSource(icdCode);
		icdCodeCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		icdCodeCombo.setItemCaptionPropertyId("value");
		icdCodeCombo.setNullSelectionAllowed(false);
		
		if(value != null) {
			icdCodeCombo.setValue(value);
		}
	}
	
	public void setIcdBlock(BeanItemContainer<SelectValue> icdBlockSelectValueContainer){
		this.icdBlock =	icdBlockSelectValueContainer;
	}
	
    public void setIcdCode(BeanItemContainer<SelectValue> icdCodeSelectValueContainer){
    	this.icdCode = icdCodeSelectValueContainer;
    }
//    public void setPEDCode(BeanItemContainer<SelectValue> icdCodeSelectValueContainer){
//    	this.description=icdCodeSelectValueContainer;
//    }
    
    public void setPEDCode(String pedCode){
    	this.pedCode=pedCode;
    }

    
    public List<ViewPEDTableDTO> getValues() {
    	@SuppressWarnings("unchecked")
		List<ViewPEDTableDTO> itemIds = (List<ViewPEDTableDTO>) this.table.getItemIds() ;
    	return itemIds;
    }
    
    public void addBeanToList(ViewPEDTableDTO pedValidationDTO) {
    	data.addBean(pedValidationDTO);

//    	manageListeners();
    }
    public boolean isValid(Boolean isSumInsuredLocked) {
		boolean hasError = false;
		errorMessages.removeAll(getErrors());
		@SuppressWarnings("unchecked")
		Collection<ViewPEDTableDTO> itemIds = (Collection<ViewPEDTableDTO>) table
				.getItemIds();
        
		for (ViewPEDTableDTO bean : itemIds) {

			if(bean.getSource() != null && bean.getSource().getId().equals(ReferenceTable.OTHERSPECIFY) && bean.getOthersSpecify() == null) {
   				hasError = true;
   				errorMessages.add("Please Enter Others Specify details.");
   			}
			
			if(bean.getPedCode() != null && bean.getPedCode().getId().equals(ReferenceTable.PED_OTHER_CATEGORY) && (bean.getDoctorRemarks() == null || (bean.getDoctorRemarks() != null && bean.getDoctorRemarks().equalsIgnoreCase("")))) {
   				hasError = true;
   				errorMessages.add("Please Enter Doctor Remarks.");
   			}
			
			//R1082 - UAT
			/*if(isSumInsuredLocked && (bean.getDoctorRemarks() == null || (bean.getDoctorRemarks() != null && bean.getDoctorRemarks().equalsIgnoreCase("")))){
				hasError = true;
   				errorMessages.add("Please Enter Doctor Remarks.");
			}*/
			
			Set<ConstraintViolation<ViewPEDTableDTO>> validate = validator
					.validate(bean);

			if (validate.size() > 0) {
				//R1082 - UAT
				//if(!isSumInsuredLocked){
					hasError = true;
					for (ConstraintViolation<ViewPEDTableDTO> constraintViolation : validate) {
						errorMessages.add(constraintViolation.getMessage());
					}
				//}
			}
		}
		return !hasError;
	}
    
   	public List<String> getErrors()
   	{
   		return this.errorMessages;
   	}

   	public void disableAdd(Boolean value){
   		if(value){
   			btnAdd.setEnabled(false);
   		}else{
   			btnAdd.setEnabled(true);
   		}
   	}
   	
   	public void checkPEDAlreadyAvailable(Long suggestionKey, Long diagKey, Long icdChapterKey, ViewPEDTableDTO newInitiatePedDTO){
   		   		
   		int count = 0;
   		if(table.getItemIds() != null && table.getItemIds().size() >0){
   			List<ViewPEDTableDTO> tableList = (List<ViewPEDTableDTO>)table.getItemIds();
   			for (ViewPEDTableDTO viewPEDTableDTO : tableList) {
				if(viewPEDTableDTO.getPedCode() != null && viewPEDTableDTO.getPedCode().getId().equals(diagKey)
						&& viewPEDTableDTO.getIcdChapter() != null && viewPEDTableDTO.getIcdChapter().getId().equals(icdChapterKey)){
					count++;
				}
			}
   		}
   		
		if(count<=1){
	   		WeakHashMap<Integer,Object> inputMap = new WeakHashMap<Integer,Object>();
	   		inputMap.put(1, suggestionKey);
	   		inputMap.put(2, diagKey);
	   		inputMap.put(3, icdChapterKey);
	   		inputMap.put(4, intimationKey);
	   		inputMap.put(5, insuredKey);
	   		
	   		if(presenterString != null && (SHAConstants.PED_INITIATE_OUTSIDE_PROCESS).equalsIgnoreCase(presenterString)){
	   			fireViewEvent(InitiatePedPresenter.GET_PED_AVAILABLE_DETAILS_INITIATOR, inputMap, newInitiatePedDTO);
	   		}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_INITIATOR)){
				fireViewEvent(ViewPEDRequestPresenter.GET_PED_AVAILABLE_DETAILS, inputMap, newInitiatePedDTO);
			}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_PROCESSOR)){
				fireViewEvent(PEDRequestDetailsProcessPresenter.GET_PED_AVAILABLE_DETAILS_PROCESSOR, inputMap);
			}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_APPROVER)){
				fireViewEvent(PEDRequestDetailsApprovePresenter.GET_PED_AVAILABLE_DETAILS_APPROVER, inputMap);
			}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_TEAM_LEAD)){
				fireViewEvent(PEDRequestDetailsTeamLeadPresenter.GET_PED_AVAILABLE_DETAILS_TEAM_LEAD, inputMap, newInitiatePedDTO);
			}
		}
		else{
			if(presenterString != null && (SHAConstants.PED_INITIATE_OUTSIDE_PROCESS).equalsIgnoreCase(presenterString)){
	   			fireViewEvent(InitiatePedPresenter.SHOW_DUP_PED_AVAILABLE_OUTSIDE_PROCESS, newInitiatePedDTO);
	   		}
			
		}
   	}
   	
	public void setIntimationKey(Long intimationKey){
   		this.intimationKey = intimationKey;
   	}
   	
   	public void setSuggestionKey(Long suggestionKey){
   		this.suggestionKey = suggestionKey;
   	}

   	public void setInsuredKey(Long insuredKey) {
		this.insuredKey = insuredKey;
	}

	public void resetPEDDetailsTable(ViewPEDTableDTO newInitiatePedDTO){
    	table.removeItem(newInitiatePedDTO);
    	data.addItem(new ViewPEDTableDTO());
    }
    
   	public void checkPEDAlreadyAvailableBySuggestion(Long suggestionKey){
   		
   		if(table.getItemIds() != null && table.getItemIds().size() >0){
   			List<ViewPEDTableDTO> tableList = (List<ViewPEDTableDTO>)table.getItemIds();
   			for (ViewPEDTableDTO viewPEDTableDTO : tableList) {
				if(viewPEDTableDTO.getPedCode() != null && viewPEDTableDTO.getPedCode().getId() != null 
						&& viewPEDTableDTO.getIcdChapter() != null && viewPEDTableDTO.getIcdChapter().getId() != null){
					WeakHashMap<Integer,Object> inputMap = new WeakHashMap<Integer,Object>();
			   		inputMap.put(1, suggestionKey);
			   		inputMap.put(2, viewPEDTableDTO.getPedCode().getId());
			   		inputMap.put(3, viewPEDTableDTO.getIcdChapter().getId());
			   		inputMap.put(4, intimationKey);
			   		inputMap.put(5, insuredKey);
			   		
					
			   		if(presenterString != null && (SHAConstants.PED_INITIATE_OUTSIDE_PROCESS).equalsIgnoreCase(presenterString)){
						fireViewEvent(InitiatePedPresenter.GET_PED_AVAILABLE_DETAILS_INITIATOR, inputMap, viewPEDTableDTO);
					}else if(presenterString != null && presenterString.equalsIgnoreCase(SHAConstants.PED_APPROVER)){
						fireViewEvent(PEDRequestDetailsApprovePresenter.GET_PED_AVAILABLE_DETAILS_APPROVER, inputMap, viewPEDTableDTO);
					}		

				}
			}
   		}
   	}
   	
	public void clearObject() {
   		SHAUtils.setClearTableItemForPed(tableItem);
   		SHAUtils.setClearReferenceData(referenceData);
     	}
}
