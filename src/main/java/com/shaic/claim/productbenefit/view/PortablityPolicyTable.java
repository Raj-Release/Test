package com.shaic.claim.productbenefit.view;



import javax.inject.Inject;

import com.shaic.arch.table.GBaseTable;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.ValoTheme;


public class PortablityPolicyTable extends
GBaseTable<PortablitiyPolicyDTO> {


private static final long serialVersionUID = 7031963170040209948L;


private Window popup;

@Inject
private ViewPortablityDetails viewPortablityDetails;



@SuppressWarnings("unused")


/*public static final Object[] NATURAL_COL_ORDER = new Object[] { "select",
	"serialNumber","insuredName", "productName", "policyNo", "policyType"};*/



@Override
public void removeRow() {
// TODO Auto-generated method stub

}

@Override
public void initTable() {
table.removeAllItems();
table.setWidth("100%");
table.setContainerDataSource(new BeanItemContainer<PortablitiyPolicyDTO>(
		PortablitiyPolicyDTO.class));
 Object[] NATURAL_COL_ORDER = new Object[] {/* "select",*/
	"serialNumber","insuredName", "productName", "policyNo", "policyType"};
table.setVisibleColumns(NATURAL_COL_ORDER);
table.setPageLength(table.size()+4);
table.setHeight("200px");

table.removeGeneratedColumn("viewDetails");
table.addGeneratedColumn("viewDetails", new Table.ColumnGenerator() {
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
      public Object generateCell(final Table source, final Object itemId, Object columnId) {
    	Button button = new Button("ViewDetails");
    	
    	final PortablitiyPolicyDTO tableDto = (PortablitiyPolicyDTO) itemId;
    	button.addClickListener(new Button.ClickListener() {
	        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {

				getPortablityDetails(tableDto);
				
			} 
	    });
    	
    	button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    	button.setWidth("150px");
    	button.addStyleName(ValoTheme.BUTTON_LINK);
    	return button;
      }
});


table.setPageLength(table.size());
}

@Override
public void tableSelectHandler(PortablitiyPolicyDTO t) {
//TODO:
}

public void getPortablityDetails(PortablitiyPolicyDTO dto) {

	viewPortablityDetails.init(dto);
	popup = new com.vaadin.ui.Window();
	popup.setCaption("Portablity Details");
	popup.setWidth("60%");
	popup.setHeight("75%");
	popup.setContent(viewPortablityDetails);
	popup.setClosable(true);
	popup.center();
	popup.setResizable(false);
	popup.addCloseListener(new Window.CloseListener() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void windowClose(CloseEvent e) {
			System.out.println("Close listener called");
		}
	});

	popup.setModal(true);
	UI.getCurrent().addWindow(popup);
}

@Override
public String textBundlePrefixString() {
return "portablity-policy-";
}


}

