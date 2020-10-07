package com.shaic.claim.userproduct.document.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.vaadin.addon.cdimvp.ViewComponent;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.SHAConstants;
import com.shaic.claim.userproduct.document.ApplicableCpuDTO;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.v7.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

public class UserMgmtApplicableCpuTable  extends ViewComponent{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<UserMgmtApplicableCpuDTO, HashMap<String, AbstractField<?>>> tableItem = new HashMap<UserMgmtApplicableCpuDTO, HashMap<String, AbstractField<?>>>();
	
	BeanItemContainer<UserMgmtApplicableCpuDTO> container = new BeanItemContainer<UserMgmtApplicableCpuDTO>(UserMgmtApplicableCpuDTO.class);

	private Table table;
	
	private UserMgmtApplicableCpuDTO userMgmtApplicableCpuDTO;
	
	
	@PostConstruct
	public void init(){
		container.removeAllItems();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		initTable();
		table.setWidth("60%");
		table.setPageLength(table.getItemIds().size());
		table.setFooterVisible(true);
		layout.addComponent(table);

		setCompositionRoot(layout);
		
		
		
	}
	
	public void setReferenceData(Map<String, Object> referenceData) {
		//this.referenceData = referenceData;
	}
	
	void initTable() {
		// Create a data source and bind it to a table
		table = new Table("", container);
		container.removeAllItems();
		table.addStyleName("generateColumnTable");
		table.setPageLength(table.getItemIds().size());
		
		
		table.removeGeneratedColumn("Accessibility");
		
		table.addGeneratedColumn("Accessibility", new Table.ColumnGenerator() {
		    
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				
				final UserMgmtApplicableCpuDTO rodDTO = (UserMgmtApplicableCpuDTO) itemId;
				if(rodDTO.getCpuCodewithName() != null){
					
						final CheckBox chkBox = new CheckBox();
						chkBox.setData(false);
						UserMgmtApplicableCpuDTO uploadDocDTO = (UserMgmtApplicableCpuDTO)itemId;
						if(uploadDocDTO.getCheckBoxValueAccesibility() != null && uploadDocDTO.getCheckBoxValueAccesibility())
						{
							chkBox.setValue(true);
							/*CheckBox gmcBox = rodDTO.getGmcBox();
							CheckBox retailBox = rodDTO.getRetailBox();
							if(gmcBox != null || gmcBox == null) {
							gmcBox.setEnabled(true);  }
							if(retailBox != null || retailBox == null) {
							retailBox.setEnabled(true); }*/
						}
						else
						{
							/*rodDTO.setGmcCheckBox(false);
							rodDTO.setRetailCheckBox(false);*/
							chkBox.setValue(false);
							
							uploadDocDTO.setGmcCheckBox(false);
							uploadDocDTO.setRetailCheckBox(false);
						}
						
						
						chkBox.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								Boolean value = (Boolean) event.getProperty().getValue();
								
								if(value != null){
								   if(value){
									   /*reConfirmMessage(rodDTO,chkBox);*/
									   rodDTO.setAccessability(true);
									   rodDTO.setCheckBoxValueAccesibility(true);
									   CheckBox gmcBox = rodDTO.getGmcBox();
									   CheckBox retailBox = rodDTO.getRetailBox();
									   if(gmcBox != null){
										   gmcBox.setEnabled(true);
									   }
									   if(retailBox != null){
										   retailBox.setEnabled(true);
									   }
									  
								   }else {
									   rodDTO.setAccessability(false);
									   rodDTO.setCheckBoxValueAccesibility(false);
									   rodDTO.setActiveStatus(0l);
									   chkBox.setValue(false);
									   CheckBox gmcBox = rodDTO.getGmcBox();
									   CheckBox retailBox = rodDTO.getRetailBox();
									   if(gmcBox != null){
										   gmcBox.setEnabled(false);
									   }
									   if(retailBox != null){
										   retailBox.setEnabled(false);
									   }
									  /* rodDTO.setGmcCheckBox(true);
									   rodDTO.setRetailIsEnabled(true);*/
								   }
								}
								
							}
						});
						return chkBox;
				}else {
					return null;
				}
		}
		});
		table.removeGeneratedColumn("GMC");
		
		table.addGeneratedColumn("GMC", new Table.ColumnGenerator() {
		    
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				
				final UserMgmtApplicableCpuDTO rodDTO = (UserMgmtApplicableCpuDTO) itemId;
				if(rodDTO.getCpuCodewithName() != null){
					
						final CheckBox chkBox = new CheckBox();
						rodDTO.setGmcBox(chkBox);
						chkBox.setData(false);
						UserMgmtApplicableCpuDTO uploadDocDTO = (UserMgmtApplicableCpuDTO)itemId;
						
						if(uploadDocDTO.getCheckBoxValueAccesibility() != null && uploadDocDTO.getCheckBoxValueAccesibility()) {
							chkBox.setEnabled(true);
						}else {
							chkBox.setEnabled(false);
						}
						
						if(null != rodDTO.getGmcCheckBox() && rodDTO.getGmcCheckBox()) {
							chkBox.setValue(true);
							//chkBox.setEnabled(true);
						}else{
							chkBox.setValue(false);
							//chkBox.setEnabled(false);
						}
						
						
						chkBox.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								Boolean value = (Boolean) event.getProperty().getValue();
								
								if(value != null){
								   if(value){
									   rodDTO.setGmc(true);
									   rodDTO.setGmcCheckBox(true);
									 //  rodDTO.setActiveStatus(0l);
									   chkBox.setValue(true);
//									   reConfirmMessage(rodDTO,chkBox);
									   rodDTO.setAccessability(true);
									   rodDTO.setCheckBoxValueAccesibility(true);
								   }else {
									   rodDTO.setGmc(false);
									   rodDTO.setGmcCheckBox(false);
									 //  rodDTO.setActiveStatus(0l);
									   chkBox.setValue(false);
								   }
								}
								
							}
						});
						return chkBox;
				}else {
					return null;
				}
		}
		});
		table.removeGeneratedColumn("Retail");
		
		table.addGeneratedColumn("Retail", new Table.ColumnGenerator() {
		    
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				
 				final UserMgmtApplicableCpuDTO rodDTO = (UserMgmtApplicableCpuDTO) itemId;
				if(rodDTO.getCpuCodewithName() != null){
					
						final CheckBox chkBox = new CheckBox();
						rodDTO.setRetailBox(chkBox);
						chkBox.setData(false);
						UserMgmtApplicableCpuDTO uploadDocDTO = (UserMgmtApplicableCpuDTO)itemId;

						if(uploadDocDTO.getCheckBoxValueAccesibility() != null && uploadDocDTO.getCheckBoxValueAccesibility()) {
							chkBox.setEnabled(true);
						}else {
							chkBox.setEnabled(false);
						}
						
						
						if(rodDTO.getRetailCheckBox() != null && rodDTO.getRetailCheckBox())
						{
							chkBox.setValue(true);
							//chkBox.setEnabled(true);
						}
						else
						{
							chkBox.setValue(false);
							//chkBox.setEnabled(false);
						}
						
						chkBox.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								Boolean value = (Boolean) event.getProperty().getValue();
								
								if(value != null){
								   if(value){
									   rodDTO.setRetail(true);
									   rodDTO.setRetailCheckBox(true);
									  // rodDTO.setActiveStatus(0l);
									   chkBox.setValue(true);
//									   reConfirmMessage(rodDTO,chkBox);
									   rodDTO.setAccessability(true);
									   rodDTO.setCheckBoxValueAccesibility(true);
								   }else {
									   rodDTO.setRetail(false);
									   rodDTO.setRetailCheckBox(false);
									  // rodDTO.setActiveStatus(0l);
									   chkBox.setValue(false);
								   }
								}
								
							}
						});
						return chkBox;
				}else {
					return null;
				}
		}
		});
		
		
		table.setVisibleColumns(new Object[] {"sno","cpuCodewithName","Accessibility","GMC","Retail"});
		table.setColumnHeader("sno", "S.No");
		table.setColumnHeader("cpuCodewithName", "CPU Code With Name");
		table.setColumnWidth("sno", 45);
		table.setColumnWidth("cpuCodewithName", 400);
		
		
		table.setEditable(true);
		

		table.setTableFieldFactory(new ImmediateFieldFactory());       
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void setTableList(final List<UserMgmtApplicableCpuDTO> list) {
		table.removeAllItems();
		for (final UserMgmtApplicableCpuDTO bean : list) {
			table.addItem(bean);
		}
		table.sort();
	}
	
	public class ImmediateFieldFactory extends DefaultFieldFactory {

	
		private static final long serialVersionUID = 1L;
		
		@Override
		public Field<?> createField(Container container, Object itemId,
				Object propertyId, Component uiContext){
			UserMgmtApplicableCpuDTO preHospitalizationDTO = (UserMgmtApplicableCpuDTO) itemId;
			//Map<String, AbstractField<?>> tableRow = null;

			if (tableItem.get(preHospitalizationDTO) == null) {
				tableItem.put(preHospitalizationDTO, new HashMap<String, AbstractField<?>>());
			}
				if("cpucodewithName".equals(propertyId)){
				
				TextField field = new TextField();
				field.setReadOnly(true);
				field.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
				field.setWidth("450px");
				field.setNullRepresentation("");
				field.setDescription(preHospitalizationDTO.getCpuCodewithName());
				return field;
				
				}
				else {
					Field<?> field = super.createField(container, itemId,
							propertyId, uiContext);

					if (field instanceof TextField)
						((TextField) field).setNullRepresentation("");
						field.setReadOnly(true);
						field.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
						field.setWidth("450px");
					return field;
				}
			
		}
		
	}
	
	 public void addBeanToList(UserMgmtApplicableCpuDTO cpuDetails) {
	    	container.addItem(cpuDetails);
	    }
	 
	 
	 public List<UserMgmtApplicableCpuDTO> getValues() {
	    	@SuppressWarnings("unchecked")
			List<UserMgmtApplicableCpuDTO> itemIds = (List<UserMgmtApplicableCpuDTO>) this.table.getItemIds() ;
	    	return itemIds;
	    }

	/*public void reConfirmMessage(final UserMgmtApplicableCpuDTO rodDTO,final CheckBox chkBox){
		
		ConfirmDialog dialog = ConfirmDialog.show(getUI(),"Confirmation", "Do You want to Edit?",
		        "No", "Yes", new ConfirmDialog.Listener() {
				
		            public void onClose(ConfirmDialog dialog) {
		                if (!dialog.isConfirmed()) {
		                	rodDTO.setAccessability(true);
		                	rodDTO.setCheckBoxValueAccesibility(true);
		                	
		                } else {
		                    chkBox.setValue(false);
		                    dialog.close();
		                   
		                }
		            }
		        });
		dialog.setStyleName(Reindeer.WINDOW_BLACK);
		
		dialog.addCloseListener(new CloseListener() {
            private static final long serialVersionUID = -4381415904461841881L;

            public void windowClose(CloseEvent e) {
//                System.out.println("close called");
            	chkBox.setData(false);
            }
        });
		
	}	*/
	
	public void reConfirmMessageForCancel(final UserMgmtApplicableCpuDTO rodDTO,final CheckBox chkBox){
			
			ConfirmDialog dialog = ConfirmDialog.show(getUI(),"Confirmation", "Do You want to Edit?",
			        "No", "Yes", new ConfirmDialog.Listener() {
	
			            public void onClose(ConfirmDialog dialog) {
			                if (!dialog.isConfirmed()) {
			                	rodDTO.setAccessability(false);
			                	rodDTO.setCheckBoxValueAccesibility(false);
			                	rodDTO.setActiveStatus(0l);
			                	chkBox.setValue(false);
			                	chkBox.setData(false);
			                } else {
			                    chkBox.setData(true);
			                    chkBox.setValue(true);
			                    dialog.close();
			                    
			                }
			            }
			           
			        });
			dialog.setStyleName(Reindeer.WINDOW_BLACK);
			
			dialog.addCloseListener(new CloseListener() {
	            private static final long serialVersionUID = -4381415904461841881L;

	            public void windowClose(CloseEvent e) {
//	                System.out.println("close called");
	            	chkBox.setData(false);
	            }
	        });
			
		}
	 public void addBeanToListUserMgmt(UserMgmtApplicableCpuDTO cpuDetails) {
	    	container.addItem(cpuDetails);
	    }


}
