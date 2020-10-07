package com.shaic.claim.processdatacorrection.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.cdimvp.ViewComponent;
import org.vaadin.csvalidation.CSValidator;

import com.shaic.arch.SHAConstants;
import com.shaic.arch.SHAUtils;
import com.shaic.arch.components.GComboBox;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.claim.preauth.wizard.dto.TreatingDoctorDTO;
import com.shaic.claim.processdatacorrection.search.SpecialityCorrectionTable.ImmediateFieldFactory;
import com.shaic.claim.processdatacorrectionhistorical.search.DataCorrectionHistoricalPresenter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.Table.Align;

public class TreatingCorrectionTabel extends ViewComponent{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1910148376564000628L;

	private Map<TreatingCorrectionDTO, HashMap<String, AbstractField<?>>> tableItem = new HashMap<TreatingCorrectionDTO, HashMap<String, AbstractField<?>>>();

	BeanItemContainer<TreatingCorrectionDTO> data = new BeanItemContainer<TreatingCorrectionDTO>(TreatingCorrectionDTO.class);

	private Table table;

	private List<String> errorMessages;
	
	public Boolean isValueChanges =false;
	
	public String presenterString;
	
	private Button btnAdd;
	
	public List<TreatingCorrectionDTO> deletedDTO;
	
	private List<Long> editedKeys;

	public void init(String presenterString){
		
		this.presenterString = presenterString;
		this.errorMessages = new ArrayList<String>();
		this.editedKeys = new ArrayList<Long>();
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);	
		initTable(layout);		
		
		table.setWidth("70%");
		table.setPageLength(table.getItemIds().size());
		
		layout.addComponent(table);
		setCompositionRoot(layout);

	}

	@SuppressWarnings("deprecation")
	void initTable(VerticalLayout layout){

		table = new Table("Treating Doctor Details", data);
		table.addStyleName("generateColumnTable");
		table.setPageLength(table.getItemIds().size());
		
		table.addGeneratedColumn("edit", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 5936665477260011479L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				final Button edit = new Button("");
				edit.setEnabled(true);
				edit.setIcon(FontAwesome.EDIT);
				edit.setData(itemId);
				edit.addClickListener(new Button.ClickListener() {
					private static final long serialVersionUID = 6100598273628582002L;
					public void buttonClick(ClickEvent event) {
						Object currentItemId = event.getButton().getData();
						TreatingCorrectionDTO dto = (TreatingCorrectionDTO) currentItemId;
						if(!editedKeys.contains(dto.getKey())){
							editedKeys.add(dto.getKey());
							if(presenterString.equalsIgnoreCase(SHAConstants.DATA_VALIDATION)){
								fireViewEvent(DataCorrectionPresenter.EDIT_TREATING_CORRECTION_VALUES,dto);	
							}else if(presenterString.equalsIgnoreCase(SHAConstants.DATA_VALIDATION_HISTORICAL)){
								fireViewEvent(DataCorrectionHistoricalPresenter.EDIT_TREATING_CORRECTION_VALUES_HIST,dto);	
							}
						}else{
							SHAUtils.showMessageBoxWithCaption("Data already in correction", "Information");
						}
					} 
				});
				edit.addStyleName(ValoTheme.BUTTON_BORDERLESS);
				return edit;
			}
		});
		table.setVisibleColumns(new Object[] { "treatingDoctorName","qualification","edit"});
		table.setColumnHeader("treatingDoctorName", "Treating Doctor Name");
		table.setColumnHeader("qualification", "Qualification");
		table.setColumnHeader("edit", "");
				
		table.setColumnAlignment("treatingDoctorName",Align.CENTER);
		table.setColumnAlignment("qualification",Align.CENTER);
		table.setColumnAlignment("edit",Align.CENTER);
		
		table.setEditable(true);
		table.setTableFieldFactory(new ImmediateFieldFactory());

	}

	public class ImmediateFieldFactory extends DefaultFieldFactory {

		public Field<?> createField(Container container, Object itemId,
				Object propertyId, Component uiContext) {

			TreatingCorrectionDTO treatingDoctorDTO = (TreatingCorrectionDTO)itemId;
			Map<String, AbstractField<?>> tableRow = null;
			if (tableItem.get(treatingDoctorDTO) == null) {
				tableRow = new HashMap<String, AbstractField<?>>();
				tableItem.put(treatingDoctorDTO, new HashMap<String, AbstractField<?>>());
			} else {
				tableRow = tableItem.get(treatingDoctorDTO);
			}

			if("treatingDoctorName".equals(propertyId)){
				TextField box = new TextField();
				box.setWidth("70%");
				box.setNullRepresentation("");
				tableRow.put("treatingDoctorName", box);
				box.setData(treatingDoctorDTO);
				if(treatingDoctorDTO.getTreatingDoctorName() != null){
					box.setValue(treatingDoctorDTO.getTreatingDoctorName());
				}
				box.setEnabled(false);					
				return box;
			} else if("qualification".equals(propertyId)){
				TextField box = new TextField();
				box.setWidth("70%");
				box.setNullRepresentation("");
				tableRow.put("qualification", box);
				box.setData(treatingDoctorDTO);
				if(treatingDoctorDTO.getQualification() != null){
					box.setValue(treatingDoctorDTO.getQualification());
				}
				box.setEnabled(false);					
				return box;
			}else {
				Field<?> field = super.createField(container, itemId,propertyId, uiContext);

				if (field instanceof TextField)
					field.setWidth("100%");
				return field;
			}
		}
	}

	public List<TreatingCorrectionDTO> getValues() {
		@SuppressWarnings("unchecked")
		List<TreatingCorrectionDTO> itemIds = (List<TreatingCorrectionDTO>) this.table.getItemIds();
		return itemIds;
	}

	public void removeAllItems(){
		table.removeAllItems();
	}

	public void addBeanToList(TreatingCorrectionDTO treatingCorrectionDTO) {
		data.addItem(treatingCorrectionDTO);
	}
	
	public void addBeansToList(List<TreatingCorrectionDTO> treatingCorrectionDTOs){
		
		for(TreatingCorrectionDTO treatingCorrectionDTO: treatingCorrectionDTOs){
			addBeanToList(treatingCorrectionDTO);
		}
	}

	public List<String> getErrors() {
		return this.errorMessages;
	}
	
	public void removeTreatingEdited(Long Key){
		if(Key !=null && editedKeys !=null){	
			editedKeys.remove(Key);
		}
	}

}
