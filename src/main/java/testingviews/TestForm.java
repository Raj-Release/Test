//package testingviews;
//
//import javax.annotation.PostConstruct;
//
//import org.vaadin.addon.cdimvp.ViewComponent;
//
//import com.shaic.arch.EnhancedFieldGroupFieldFactory;
//import com.shaic.claim.registration.ackhoscomm.search.SearchAcknowledgeHospitalCommunicationFormDTO;
//import com.shaic.claim.registration.ackhoscomm.search.SearchAcknowledgeHospitalCommunicationPresenter;
//import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
//import com.vaadin.ui.Alignment;
//import com.vaadin.ui.Button;
//import com.vaadin.v7.ui.ComboBox;
//import com.vaadin.ui.FormLayout;
//import com.vaadin.ui.GridLayout;
//import com.vaadin.v7.ui.HorizontalLayout;
//import com.vaadin.ui.Panel;
//import com.vaadin.v7.ui.TextField;
//import com.vaadin.v7.ui.VerticalLayout;
//import com.vaadin.ui.Button.ClickEvent;
//
//public class TestForm extends ViewComponent {
//
//	private BeanFieldGroup<TestFormDTO> binder;
//
//	TextField txtIntimationNo;
//
//	TextField txtPolicyNo;
//
//	Button ptmrSearchBtn;
//
//	@PostConstruct
//	public void init() {
//		VerticalLayout buildPreauthSearchLayuout = buildPreauthSearchLayuout();
//		buildPreauthSearchLayuout.setWidth("100%");
//		buildPreauthSearchLayuout.setHeight("100%");
//		buildPreauthSearchLayuout.setSpacing(true);
//		buildPreauthSearchLayuout.setMargin(true);
//		Panel preauthPanel = new Panel("Process Translation / Misc Request");
//		preauthPanel.setContent(buildPreauthSearchLayuout);
//		setCompositionRoot(preauthPanel);
//
//	}
//
//	private VerticalLayout buildPreauthSearchLayuout() {
//		this.binder = new BeanFieldGroup<TestFormDTO>(
//				TestFormDTO.class);
//		binder.setFieldFactory(new EnhancedFieldGroupFieldFactory());
//
//		txtIntimationNo = binder.buildAndBind("Intimation No", "intimationNo",
//				TextField.class);
//		
//		txtPolicyNo = binder.buildAndBind("Policy No", "policyNo",
//				TextField.class);
//
//		ptmrSearchBtn = new Button("Search");
//		HorizontalLayout buttonHLyaout = new HorizontalLayout(ptmrSearchBtn);
//		buttonHLyaout.setWidth("80%");
//		buttonHLyaout.setComponentAlignment(ptmrSearchBtn,
//				Alignment.MIDDLE_CENTER);
//		GridLayout searchFormGLayout = new GridLayout(2, 1, new FormLayout(
//				txtIntimationNo ), new FormLayout(txtPolicyNo));
//		searchFormGLayout.setWidth("80%");
//		addListener();
//		return new VerticalLayout(searchFormGLayout, buttonHLyaout);
//
//	}
//
//	public void addListener() {
//
//		ptmrSearchBtn.addClickListener(new Button.ClickListener() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				TestFormDTO searchProcessRejectionFormDTO = new TestFormDTO();
////				fireViewEvent(TestPresenter.SEARCH_BUTTON_CLICK,
////						searchProcessRejectionFormDTO);
//			}
//		});
//	}
//
//}
