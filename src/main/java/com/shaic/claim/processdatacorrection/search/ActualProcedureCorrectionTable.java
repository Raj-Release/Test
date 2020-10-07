package com.shaic.claim.processdatacorrection.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.cdimvp.ViewComponent;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.processdatacorrection.search.ProcedureCorrectionTable.ImmediateFieldFactory;
import com.shaic.claim.processdatacorrectionhistorical.search.DataCorrectionHistoricalPresenter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.Table.Align;

public class ActualProcedureCorrectionTable  extends ViewComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8224234108641261062L;

	private Map<ProcedureCorrectionDTO, HashMap<String, AbstractField<?>>> tableItem = new HashMap<ProcedureCorrectionDTO, HashMap<String, AbstractField<?>>>();

	BeanItemContainer<ProcedureCorrectionDTO> data = new BeanItemContainer<ProcedureCorrectionDTO>(ProcedureCorrectionDTO.class);

	private Table table;

	private Map<String, Object> referenceData;

	private List<String> errorMessages;
	
	private String presenterString;
	
	private Boolean iscodechanged = false;
	
	private Boolean isnamechanged = false;

	public void init(String presenterString){
		this.presenterString = presenterString;
		this.errorMessages = new ArrayList<String>();

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		initTable(layout);

		table.setWidth("70%");
		table.setPageLength(table.getItemIds().size());
		
		layout.addComponent(table);
		setCompositionRoot(layout);

	}

	public void setReferenceData(Map<String, Object> referenceData) {
		this.referenceData = referenceData;
	}

	@SuppressWarnings("deprecation")
	void initTable(VerticalLayout layout){

		table = new Table("Actual Procedure List", data);
		table.addStyleName("generateColumnTable");
		table.setPageLength(table.getItemIds().size());

		table.addGeneratedColumn("Delete", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 5936665477260011479L;
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				final Button deleteButton = new Button("Delete");
				deleteButton.setData(itemId);
				deleteButton.addClickListener(new Button.ClickListener() {
					private static final long serialVersionUID = 6100598273628582002L;

					public void buttonClick(ClickEvent event) {
						Object currentItemId = event.getButton().getData();
						ProcessDataCorrectionDTO dto =  (ProcessDataCorrectionDTO)currentItemId;
						if(presenterString.equalsIgnoreCase(SHAConstants.DATA_VALIDATION)){
							fireViewEvent(DataCorrectionPresenter.DELETE_PROCEDURE_CORRECTION_VALUES,dto.getKey());	
						}else if(presenterString.equalsIgnoreCase(SHAConstants.DATA_VALIDATION_HISTORICAL)){
							fireViewEvent(DataCorrectionHistoricalPresenter.DELETE_PROCEDURE_CORRECTION_VALUES_HIST,dto.getKey());	
						}
						table.removeItem(currentItemId);
					} 
				});
				deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
				return deleteButton;

			}
		});
		table.setVisibleColumns(new Object[] {"proposedProcedureName","proposedProcedureCode","Delete"});
		table.setColumnHeader("proposedProcedureName", "Actual Procedure Name");
		table.setColumnHeader("proposedProcedureCode", "Actual Procedure Code");
		table.setEditable(true);
		
		table.setColumnAlignment("proposedProcedureName",Align.CENTER);
		table.setColumnAlignment("proposedProcedureCode",Align.CENTER);

		table.setTableFieldFactory(new ImmediateFieldFactory());

	}

	public class ImmediateFieldFactory extends DefaultFieldFactory {

		public Field<?> createField(Container container, Object itemId,
				Object propertyId, Component uiContext) {

			ProcedureCorrectionDTO procedureCorrectionDTO = (ProcedureCorrectionDTO)itemId;
			Map<String, AbstractField<?>> tableRow = null;
			if (tableItem.get(procedureCorrectionDTO) == null) {
				tableRow = new HashMap<String, AbstractField<?>>();
				tableItem.put(procedureCorrectionDTO, new HashMap<String, AbstractField<?>>());
			} else {
				tableRow = tableItem.get(procedureCorrectionDTO);
			}

		 if("proposedProcedureName".equals(propertyId)){
				GComboBox box = new GComboBox();
				addProcedureNameValues(box);
				tableRow.put("proposedProcedureName", box);
				box.setData(procedureCorrectionDTO);
				addProcedureNameListener(box);
				return box;
			} else if("proposedProcedureCode".equals(propertyId)){
				GComboBox box = new GComboBox();
				addProcedureCodeValues(box);
				tableRow.put("proposedProcedureCode", box);
				box.setData(procedureCorrectionDTO);
				addProcedureCodeListener(box);
				return box;
			} else {
				Field<?> field = super.createField(container, itemId,propertyId, uiContext);

				if (field instanceof TextField)
					field.setWidth("100%");
				return field;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addProcedureNameValues(ComboBox box) {
		
		BeanItemContainer<SelectValue> procedure = (BeanItemContainer<SelectValue>) referenceData.get("procedureName");
		box.setContainerDataSource(procedure);
		box.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		box.setItemCaptionPropertyId("value");
	}
	
	private void addProcedureCodeValues(ComboBox box) {
		@SuppressWarnings("unchecked")
		BeanItemContainer<SelectValue> procedureCode = (BeanItemContainer<SelectValue>) referenceData.get("procedureCode");
		box.setContainerDataSource(procedureCode);
		box.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		box.setItemCaptionPropertyId("value");
		
	}
	
	public List<ProcedureCorrectionDTO> getValues() {
		@SuppressWarnings("unchecked")
		List<ProcedureCorrectionDTO> itemIds = (List<ProcedureCorrectionDTO>) this.table.getItemIds();
		return itemIds;
	}

	public void removeAllItems(){
		table.removeAllItems();
	}

	public void addBeanToList(ProcedureCorrectionDTO procedureCorrectionDTO) {
		data.addItem(procedureCorrectionDTO);
	}
	
	public void addBeansToList(List<ProcedureCorrectionDTO> procedureCorrectionDTOs){

		for(ProcedureCorrectionDTO procedureCorrectionDTO: procedureCorrectionDTOs){
			addBeanToList(procedureCorrectionDTO);
		}
	}

	
	@SuppressWarnings("unused")
	private void addProcedureCodeListener(ComboBox procedureCode) {
		if (procedureCode != null) {
			procedureCode.addListener(new Listener() {
				private static final long serialVersionUID = -4865225814973226596L;

				@Override
				public void componentEvent(Event event) {
					if(!isnamechanged){
						iscodechanged =true;
						ComboBox component = (ComboBox) event.getComponent();
						ProcedureCorrectionDTO procedureDTO = (ProcedureCorrectionDTO) component.getData();
						HashMap<String, AbstractField<?>> hashMap = tableItem.get(procedureDTO);
						ComboBox procedureNameCombo = (ComboBox) hashMap.get("proposedProcedureName");
						if(null != procedureNameCombo) {
							if(null != procedureDTO.getProposedProcedureCode()
									&& procedureDTO.getProposedProcedureCode().getId() !=null){
								procedureNameCombo.setValue(procedureDTO.getProposedProcedureCode());
							}				
						}
					}
					isnamechanged =false;
				} 
			});
		}
	}
	
	private void addProcedureNameListener(ComboBox procedureName) {
		if (procedureName != null) {
			procedureName.addListener(new Listener() {
				private static final long serialVersionUID = -4865225814973226596L;

				@Override
				public void componentEvent(Event event) {
					if(!iscodechanged){
						isnamechanged = true;
						ComboBox component = (ComboBox) event.getComponent();
						ProcedureCorrectionDTO procedureDTO = (ProcedureCorrectionDTO) component.getData();
						HashMap<String, AbstractField<?>> hashMap = tableItem.get(procedureDTO);
						ComboBox procedureCode = (ComboBox) hashMap.get("proposedProcedureCode");

						if(null != procedureCode) {
							if(null != procedureDTO.getProposedProcedureName()
									&& procedureDTO.getProposedProcedureName().getId() !=null){
								procedureCode.setValue(procedureDTO.getProposedProcedureName());
							}				
						}
					}
					iscodechanged =false;
				} 
			});
		}
	}

	public List<String> getErrors() {
		return this.errorMessages;
	}
	
	
}
