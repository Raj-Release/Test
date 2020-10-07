package com.shaic.claim.cashlessprocess.processicac.search;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.vaadin.addon.cdimvp.ViewComponent;
import org.vaadin.dialogs.ConfirmDialog;

import com.shaic.arch.EnhancedFieldGroupFieldFactory;
import com.shaic.arch.fields.dto.SelectValue;
import com.shaic.arch.table.Searchable;
import com.shaic.domain.MasterService;
import com.shaic.domain.ReferenceTable;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.ErrorMessage;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.themes.ValoTheme;

public class SearchProcessICACReqForm extends ViewComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private SearchProcessICACTable searchProcessICACTable;
	private BeanFieldGroup<SearchProcessICACReqFormDTO> binder;

	private TextField txtIntimationNo;
	private ComboBox cmbClmType;

	private Button getTaskSearchButton;
	private Button resetBtn;
	
	private VerticalLayout buildWithFormSearchLayout;
	
	private ArrayList<Component> mandatoryFields = new ArrayList<Component>();
	
	private Searchable searchable;

	private BeanItemContainer<SelectValue> ClmStatusContainer;
	
	@EJB
	private MasterService masterService;
	
	public void addSearchListener(Searchable searchable) {
		this.searchable = searchable;
	}

	public SearchProcessICACReqFormDTO getSearchDTO() {
		try {
			this.binder.commit();
			SearchProcessICACReqFormDTO bean = this.binder
					.getItemDataSource().getBean();
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	
	@PostConstruct
	public void init() {
		initBinder();
		//buildPreauthSearchLayout = buildPreauthSearchLayout();
		buildWithFormSearchLayout  = new VerticalLayout();
		Panel processRejectionPanel	= new Panel();
		processRejectionPanel.setWidth("100%");
		processRejectionPanel.setHeight("50%");
		processRejectionPanel.setCaption("Process ICAC Request");
		processRejectionPanel.addStyleName("panelHeader");
		processRejectionPanel.addStyleName("g-search-panel");
		processRejectionPanel.setContent(buildWithdrawSearchLayout());
		buildWithFormSearchLayout.addComponent(processRejectionPanel);
		buildWithFormSearchLayout.setComponentAlignment(processRejectionPanel, Alignment.MIDDLE_LEFT);
		setCompositionRoot(buildWithFormSearchLayout);
		addListener();
	}
	
	private void initBinder() {
		this.binder = new BeanFieldGroup<SearchProcessICACReqFormDTO>(
				SearchProcessICACReqFormDTO.class);
		this.binder
				.setItemDataSource(new SearchProcessICACReqFormDTO());
		this.binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
	}

	private VerticalLayout buildWithdrawSearchLayout() {
		
		 AbsoluteLayout absoluteLayout_3 =  new AbsoluteLayout();
		 VerticalLayout verticalLayout = new VerticalLayout();
		 verticalLayout.setWidth("100.0%");
		 verticalLayout.setMargin(false);		 
		 absoluteLayout_3.setWidth("100.0%");
		 absoluteLayout_3.setHeight("150px");
		
		txtIntimationNo = binder.buildAndBind("Intimation Number", "intimationNo",
				TextField.class);		
		cmbClmType = binder.buildAndBind("Claim Type", "clmType", ComboBox.class);
		ClmStatusContainer = masterService.getMasterValueByReference(ReferenceTable.CLAIM_TYPE);
		
		cmbClmType.setContainerDataSource(ClmStatusContainer);
		cmbClmType.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cmbClmType.setItemCaptionPropertyId("value");

		
		FormLayout formLayout1 = new FormLayout(txtIntimationNo);
		FormLayout formLayout2 = new FormLayout(cmbClmType);
		formLayout2.setWidth("100%");
		formLayout2.setMargin(true);
		HorizontalLayout searchFormLayout = new HorizontalLayout(formLayout1,formLayout2);
		searchFormLayout.setMargin(true);
		searchFormLayout.setWidth("100%");
		searchFormLayout.setComponentAlignment(formLayout1 , Alignment.MIDDLE_LEFT);
		searchFormLayout.setComponentAlignment(formLayout2 , Alignment.MIDDLE_RIGHT);
		absoluteLayout_3.addComponent(searchFormLayout);
		

		getTaskSearchButton = new Button();
		getTaskSearchButton.setCaption("Get Tasks");
		getTaskSearchButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		getTaskSearchButton.setWidth("-1px");
		getTaskSearchButton.setHeight("-10px");
		getTaskSearchButton.setDisableOnClick(true);
		absoluteLayout_3
		.addComponent(getTaskSearchButton, "top:80.0px;left:220.0px;");
		
		resetBtn = new Button();
		resetBtn.setCaption("Reset");
		resetBtn.addStyleName(ValoTheme.BUTTON_DANGER);
		resetBtn.setWidth("-1px");
		resetBtn.setHeight("-10px");
		absoluteLayout_3.addComponent(resetBtn, "top:80.0px;left:329.0px;");
		verticalLayout.addComponent(absoluteLayout_3);
		verticalLayout.setWidth("650px");
		return verticalLayout; 
	}

	public void addListener() {
		getTaskSearchButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				getTaskSearchButton.setEnabled(true);
					searchable.doSearch();
					searchProcessICACTable.tablesize();
			/*else
			{
				Notification.show("Intimation number or ROD number is mandatory for search ",Notification.TYPE_ERROR_MESSAGE);
			}*/
			}			
		});
		
		resetBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//searable.doSearch();
				resetAlltheValues();
			}
		});
	}
	
	public void resetAlltheValues() 
	{
		
		Iterator<Component> componentIterator = buildWithFormSearchLayout.iterator();
			while(componentIterator.hasNext()) 
			{
				Component searchScrnComponent = componentIterator.next() ;
				if(searchScrnComponent instanceof  Panel )
				{	
					Panel panel = (Panel)searchScrnComponent;
					Iterator<Component> searchScrnCompIter = panel.iterator();
					while (searchScrnCompIter.hasNext())
					{
						Component verticalLayoutComp = searchScrnCompIter.next();
						VerticalLayout vLayout = (VerticalLayout)verticalLayoutComp;
						Iterator<Component> vLayoutIter = vLayout.iterator();
						while(vLayoutIter.hasNext())
						{
							Component absoluteComponent = vLayoutIter.next();
							AbsoluteLayout absLayout = (AbsoluteLayout)absoluteComponent;
							Iterator<Component> absLayoutIter = absLayout.iterator();
							while(absLayoutIter.hasNext())
							{
								Component horizontalComp = absLayoutIter.next();
								if(horizontalComp instanceof HorizontalLayout)
								{
									HorizontalLayout hLayout = (HorizontalLayout)horizontalComp;
									Iterator<Component> formLayComp = hLayout.iterator();
									while(formLayComp.hasNext())
									{
										Component formComp = formLayComp.next();
										FormLayout fLayout = (FormLayout)formComp;
										Iterator<Component> formComIter = fLayout.iterator();
									
										while(formComIter.hasNext())
										{
											Component indivdualComp = formComIter.next();
											if(indivdualComp != null) 
											{
												if(indivdualComp instanceof Label) 
												{
													continue;
												}	
												if(indivdualComp instanceof TextField) 
												{
													TextField field = (TextField) indivdualComp;
													field.setValue("");
												} 
												else if(indivdualComp instanceof ComboBox)
												{
													ComboBox field = (ComboBox) indivdualComp;
													field.setValue(null);
												}	 
									// Remove the table if exists..	
									//removeTableFromLayout();
											}
										}
									}
								}
							}
						}
					}
					
				}
				removeTableFromLayout();
			}
	}
	
	private void removeTableFromLayout()
	{
		if(null != searchable)
		{
			searchable.resetSearchResultTableValues();
		}
	}

	public void refresh()
	{
		System.out.println("---inside the refresh----");
		resetAlltheValues();
	}
	
	protected void showOrHideValidation(Boolean isVisible) {
		for (Component component : mandatoryFields) {
			AbstractField<?>  field = (AbstractField<?>)component;
			field.setRequired(!isVisible);
			field.setValidationVisible(isVisible);
		}
	}
	
	private boolean validatePage() {
		Boolean hasError = false;
		showOrHideValidation(true);
		StringBuffer eMsg = new StringBuffer();
		int count=0;
		
		if (!this.binder.isValid()) {

			for (Field<?> field : this.binder.getFields()) {
				ErrorMessage errMsg = ((AbstractField<?>) field)
						.getErrorMessage();
				if (errMsg != null) {
					eMsg.append(errMsg.getFormattedHtmlMessage());
					count++;
				}
				hasError = true;
			}
		}
		if (hasError) {
			if(count==2){
			setRequired(true);
			Label label = new Label(eMsg.toString(), ContentMode.HTML);
			label.setStyleName("errMessage");
			VerticalLayout layout = new VerticalLayout();
			layout.setMargin(true);
			layout.addComponent(label);

			ConfirmDialog dialog = new ConfirmDialog();
			dialog.setCaption("Errors");
			dialog.setClosable(true);
			dialog.setContent(layout);
			dialog.setResizable(false);
			dialog.setModal(true);
			dialog.show(getUI().getCurrent(), null, true);
			}

			hasError = true;
			if(count==2){
			return !hasError;
			}
			return hasError;
		} 
			showOrHideValidation(false);
			return true;
		}
	
	private void setRequired(Boolean isRequired) {

		if (!mandatoryFields.isEmpty()) {
			for (int i = 0; i < mandatoryFields.size(); i++) {
				AbstractField<?> field = (AbstractField<?>) mandatoryFields
						.get(i);
				field.setRequired(isRequired);
			}
		}
	}

	}
