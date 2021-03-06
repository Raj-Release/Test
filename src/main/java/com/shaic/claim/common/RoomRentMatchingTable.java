package com.shaic.claim.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.ViewComponent;
import org.vaadin.dialogs.ConfirmDialog;

import com.galaxyalert.utils.GalaxyAlertBox;
import com.galaxyalert.utils.GalaxyButtonTypesEnum;
import com.galaxyalert.utils.GalaxyTypeofMessage;
import com.shaic.arch.SHAConstants;
import com.shaic.claim.preauth.wizard.dto.PreauthDTO;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class RoomRentMatchingTable extends ViewComponent {

	private static final long serialVersionUID = 2524261416799886411L;
	
	private Map<RoomRentMatchingDTO, HashMap<String, AbstractField<?>>> tableItem = new HashMap<RoomRentMatchingDTO, HashMap<String, AbstractField<?>>>();

	BeanItemContainer<RoomRentMatchingDTO> data = new BeanItemContainer<RoomRentMatchingDTO>(RoomRentMatchingDTO.class);

	private Table table;

	private Button btnAdd;
	
	/*private Map<String, Object> referenceData;

	private List<String> errorMessages;
	
	private static Validator validator;
	
	private String presenterString;*/
	
	private PreauthDTO mainDTO;
	
	public TextField dummyField;
	
	@Inject
	private Instance<NursingChargesMatchingTable> nursingMatchingTable;

	private Button proceedButton;
	
	public void init(ConfirmDialog dialog, PreauthDTO mainDTO) {
			this.mainDTO = mainDTO;
		//public void init(Long hospitalKey, String presenterString,List<DiagnosisDetailsTableDTO> diagnosisList) {
			//ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			dummyField = new TextField();
			//validator = factory.getValidator();
			//this.errorMessages = new ArrayList<String>();
			
			VerticalLayout layout = new VerticalLayout();
			layout.setMargin(true);
			
			initTable(layout);
			table.setWidth("100%");
			//table.setHeight("30%");
			/**
			 * Height is set for table visiblity.
			 * */
//			table.setHeight("160px");
			table.setPageLength(table.getItemIds().size());
			table.setFooterVisible(true);
			calculateTotal(mainDTO.getRoomRentMappingDTOList());
			
//			addListener();
			
			layout.addComponent(table);
			proceedButton = new Button("Proceed");
			proceedButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
			addListener(dialog);
			layout.addComponent(proceedButton);

			setCompositionRoot(layout);
		}
	
	private void showErrorPopup(String eMsg) {
		/*final ConfirmDialog dialog = new ConfirmDialog();
		dialog.setCaption("");
		dialog.setClosable(true);
		VerticalLayout verticalLayout = new VerticalLayout(label);
		verticalLayout.setMargin(true);
		dialog.setContent(verticalLayout);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.show(getUI().getCurrent(), null, true);*/
		
		HashMap<String, String> buttonsNamewithType = new HashMap<String, String>();
		buttonsNamewithType.put(GalaxyButtonTypesEnum.OK.toString(), "OK");
		GalaxyAlertBox.createErrorBox(eMsg, buttonsNamewithType);
	}
	
	private void addListener(final ConfirmDialog dialog ) {
		proceedButton.addClickListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 7397593762586516039L;

			@Override
			public void buttonClick(ClickEvent event) {
				List<RoomRentMatchingDTO> values = getValues();
//				String totalRoomRentValue = table.getColumnFooter("allowedNoOfDays");
				Integer roomRentDays = 0;
				Integer allocatedDays = 0;
				
				Integer icuRoomDays = 0;
				Integer allocatedICUDays = 0;
				for (RoomRentMatchingDTO dto : values) {
					if(dto.getItemName().equalsIgnoreCase(SHAConstants.ROOM_RENT_MAPPING)) {
						roomRentDays += dto.getAllowedNoOfDays() != null ? dto.getAllowedNoOfDays().intValue() : 0;
						List<NursingChargesMatchingDTO> nursingChargesDTOList = dto.getNursingChargesDTOList();
						for (NursingChargesMatchingDTO nursingDTO : nursingChargesDTOList) {
							allocatedDays += nursingDTO.getMapToRoomDays() != null ? nursingDTO.getMapToRoomDays().intValue() : 0;
						}
					} else if(dto.getItemName().equalsIgnoreCase(SHAConstants.ICU_ROOM_RENT_MAPPING)) {
						icuRoomDays += dto.getAllowedNoOfDays() != null ? dto.getAllowedNoOfDays().intValue() : 0;
						List<NursingChargesMatchingDTO> nursingChargesDTOList = dto.getNursingChargesDTOList();
						for (NursingChargesMatchingDTO nursingDTO : nursingChargesDTOList) {
							allocatedICUDays += nursingDTO.getMapToRoomDays() != null ? nursingDTO.getMapToRoomDays().intValue() : 0;
						}
					}
				}
				if(!roomRentDays.equals(allocatedDays)) {
//				if(false) {
					String errorMessgage = "Allocated Total Nursing days should be equal to total Room Rent Allowed Days.";
//					Label label = new Label(errorMessgage, ContentMode.HTML);
					showErrorPopup(errorMessgage);
				} else {
					dialog.close();
					List<RoomRentMatchingDTO> roomRentDTOList = new ArrayList<RoomRentMatchingDTO>();
					List<RoomRentMatchingDTO> icuRoomRentDTOList = new ArrayList<RoomRentMatchingDTO>();
					List<RoomRentMatchingDTO> values2 = getValues();
					for (RoomRentMatchingDTO roomRentDTO : values2) {
						if(roomRentDTO.getItemName().equalsIgnoreCase(SHAConstants.ROOM_RENT_MAPPING)) {
							roomRentDTOList.add(roomRentDTO);
						} else {
							icuRoomRentDTOList.add(roomRentDTO);
						}
					}
					if(!mainDTO.getIsOneMapping()) {
						mainDTO.setRoomRentMappingDTOList(roomRentDTOList);
					}
					if(!mainDTO.getIsICUoneMapping()) {
						mainDTO.setIcuRoomRentMappingDTOList(icuRoomRentDTOList);
					}
					
					dummyField.setValue("0");
				}
			}
		});
	}
	
	private Integer getSumOfAllocatedDays(Long roomRentId, List<RoomRentMatchingDTO> values, Long nursingId, String roomRentName, String nursingName) {
		Integer allocatedDays = 0;
		for (RoomRentMatchingDTO dto : values) {
			if(!dto.getIdentityId().equals(roomRentId) && dto.getItemName().equalsIgnoreCase(roomRentName)) {
				List<NursingChargesMatchingDTO> nursingChargesDTOList = dto.getNursingChargesDTOList();

				for (NursingChargesMatchingDTO commonDTOList : nursingChargesDTOList) {
					if(commonDTOList.getMappingId().equals(nursingId)) {
						allocatedDays += commonDTOList.getMapToRoomDays() != null ? commonDTOList.getMapToRoomDays().intValue() : 0;
					}
				}
			}
		}
		
		return allocatedDays;
	}
	
	void initTable(VerticalLayout layout) {
		// Create a data source and bind it to a table
		table = new Table("Room Rent List", data);
		table.addStyleName("generateColumnTable");
		table.setWidth("100%");
		table.setPageLength(table.getItemIds().size());
		
		table.addGeneratedColumn("Mapping", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 5936665477260011479L;

			@Override
		      public Object generateCell(final Table source, final Object itemId, Object columnId) {
		    	final Button mapButton = new Button("Map Details");
		    	mapButton.setStyleName("link");
		    	mapButton.setData(itemId);
		    	//RoomRentMatchingDTO dto = (RoomRentMatchingDTO) itemId;
		    	mapButton.addClickListener(new Button.ClickListener() {
					private static final long serialVersionUID = 6100598273628582002L;

					public void buttonClick(ClickEvent event) {
						RoomRentMatchingDTO currentDTO = (RoomRentMatchingDTO) event.getButton().getData();
			        	List<RoomRentMatchingDTO> values = getValues();
			        	List<NursingChargesMatchingDTO> currentNursingChargesDTOList = currentDTO.getNursingChargesDTOList();
			        	for (NursingChargesMatchingDTO currentNursing : currentNursingChargesDTOList) {
//			        		if(currentDTO.getAllocatedValues().size() == 0) {
//			        			currentNursing.setMapToRoomDays(0);
//			        		} else {
//			        			currentNursing.setMapToRoomDays(currentDTO.getAllocatedValues().get(currentNursing.getId()));
//			        		}
			        		Integer allocateDays = getSumOfAllocatedDays(currentDTO.getIdentityId(), values, currentNursing.getMappingId(), currentDTO.getItemName(), currentNursing.getItemName());
			        				if(null != allocateDays)
			        						currentNursing.setAllocatedClaimedNoOfDays(allocateDays.doubleValue());
			        		currentNursing.setUnAllocatedDays(currentNursing.getClaimedNoOfDays() - currentNursing.getAllocatedClaimedNoOfDays());
						}
			        	
			        	
//			        	for (RoomRentMatchingDTO dto : values) {
//							if(dto.getStatus()) {
//								List<NursingChargesMatchingDTO> nursingChargesDTOList = dto.getNursingChargesDTOList();
//								List<NursingChargesMatchingDTO> nursingChargesDTOList2 = currentDTO.getNursingChargesDTOList();
//								for (NursingChargesMatchingDTO commonDTOList : nursingChargesDTOList) {
//									for (NursingChargesMatchingDTO currentDTOList : nursingChargesDTOList2) {
//										if(currentDTOList.getBillNumber().equalsIgnoreCase(commonDTOList.getBillNumber())) {
//											currentDTOList.setAllocatedClaimedNoOfDays(commonDTOList.getAllocatedClaimedNoOfDays() + (commonDTOList.getMapToRoomDays() != null ? commonDTOList.getMapToRoomDays(): 0) );
//											currentDTOList.setUnAllocatedDays(currentDTOList.getClaimedNoOfDays() - currentDTOList.getAllocatedClaimedNoOfDays());
//										}
//									}
//								}
//							}
//						}
			        	final ConfirmDialog dialog = new ConfirmDialog();
			        	NursingChargesMatchingTable nursingChargesMatchingTable = nursingMatchingTable.get();
			        	nursingChargesMatchingTable.init(currentDTO, dialog);
			        	List<NursingChargesMatchingDTO> nursingChargesDTOList = currentDTO.getNursingChargesDTOList();
			        	for (NursingChargesMatchingDTO dto : nursingChargesDTOList) {
			        		nursingChargesMatchingTable.addBeanToList(dto);
						}
			        	
			        	nursingChargesMatchingTable.dummyField.addValueChangeListener(new ValueChangeListener() {
						
							private static final long serialVersionUID = 8072523021769150403L;

							@Override
							public void valueChange(ValueChangeEvent event) {
								generateStatusColumn();
							}
						});
			        	
			    		dialog.setCaption("");
			    		dialog.setClosable(false);
			    		dialog.setContent(nursingChargesMatchingTable);
			    		dialog.setResizable(false);
			    		dialog.setModal(true);
			    		
			    		dialog.show(getUI().getCurrent(), null, true);
			        } 
			    });
		        return mapButton;
		      }
		    });
		
			generateStatusColumn();
		
		table.setVisibleColumns(new Object[] { "itemName", "billNumber", "claimedNoOfDays", "allowedNoOfDays","perDayAmount", "Mapping", "status" });

		table.setColumnHeader("itemName", "Item Name");
		table.setColumnHeader("billNumber", "Bill No");
		table.setColumnHeader("claimedNoOfDays", "No of Days Claimed");
		table.setColumnHeader("perDayAmount", "Room Rent Per Day Amt");
		table.setColumnHeader("allowedNoOfDays", "No of Days Allowed");
		table.setColumnHeader("status", "Mapping Status");
		table.setEditable(true);
//		manageListeners();

		// Use a custom field factory to set the edit fields as immediate.
		// This is used when the table is in editable mode.
		table.setTableFieldFactory(new ImmediateFieldFactory());
	}

	private void generateStatusColumn() {
		table.removeGeneratedColumn("status");
		table.addGeneratedColumn("status", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

		@Override
		  public Object generateCell(final Table source, final Object itemId, Object columnId) {
			
			btnAdd = new Button();
			RoomRentMatchingDTO dto = (RoomRentMatchingDTO) itemId;
			
			if(dto.getStatus())
			{
				btnAdd.setIcon(new ThemeResource("images/yesstatus.png"));
			}
			else
			{
				btnAdd.setIcon(new ThemeResource("images/cross_mark_black.jpg"));
			}
			btnAdd.setStyleName(ValoTheme.BUTTON_BORDERLESS);
			return btnAdd;
		}
});
	}
	
	public Double calculateTotal(List<RoomRentMatchingDTO> dtoList) {
		List<RoomRentMatchingDTO> itemIconPropertyId = (List<RoomRentMatchingDTO>) table.getItemIds();
		if(dtoList != null) {
			itemIconPropertyId = dtoList;
		}
		Double claimedDaysTotal = 0d;
		Double allowedDaysTotal = 0d;
		for (RoomRentMatchingDTO dto : itemIconPropertyId) {
		   
		Double claimedDays = 0d;
		if(null != dto.getClaimedNoOfDays())
			claimedDays = dto.getClaimedNoOfDays();
	    claimedDaysTotal += claimedDays != null ? claimedDays : 0;
	    Double allowedDays = 0d;
	    if(null != dto.getAllowedNoOfDays())
		 allowedDays = dto.getAllowedNoOfDays();
		allowedDaysTotal +=  null != allowedDays ? allowedDays : 0;
		}
		
		table.setColumnFooter("claimedNoOfDays", String.valueOf(claimedDaysTotal));
		table.setColumnFooter("allowedNoOfDays", String.valueOf(allowedDaysTotal));
		return allowedDaysTotal;
	}
	
	public class ImmediateFieldFactory extends DefaultFieldFactory {
		private static final long serialVersionUID = -2192723245525925990L;

		@Override
		public Field<?> createField(Container container, Object itemId,
				Object propertyId, Component uiContext) {
			RoomRentMatchingDTO roomRentDTO = (RoomRentMatchingDTO) itemId;
			//Map<String, AbstractField<?>> tableRow = null;
			
			if (tableItem.get(roomRentDTO) == null) {
				//tableRow = new HashMap<String, AbstractField<?>>();
				tableItem.put(roomRentDTO, new HashMap<String, AbstractField<?>>());
			} /*else {
				//tableRow = tableItem.get(roomRentDTO);
			}*/
			
				Field<?> field = super.createField(container, itemId,
						propertyId, uiContext);
				if(field instanceof TextField) {
					field.setReadOnly(true);
					field.setWidth("100px");
					field.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
				}
				return field;
		}
	}
	
	@SuppressWarnings("unchecked")
	 public List<RoomRentMatchingDTO> getValues() {
		List<RoomRentMatchingDTO> itemIds = (List<RoomRentMatchingDTO>) this.table.getItemIds() ;
		return itemIds;
	 }
	
	public void addBeanToList(RoomRentMatchingDTO roomrentDTO) {
		data.addItem(roomrentDTO);
	}
	
	
}
