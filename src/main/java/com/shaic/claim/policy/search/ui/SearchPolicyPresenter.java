package com.shaic.claim.policy.search.ui;

import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.main.navigator.domain.MenuItemBean;
import com.vaadin.v7.data.Property.ValueChangeEvent;

/*
 * create intimation presenter is the presenter for CreateIntimationView. EJBs and other resources should
 * mainly be accessed in the presenter and results are pushed to the view
 * implementation trough it's API.
 */
@SuppressWarnings("serial")
@ViewInterface(SearchPolicyView.class)
public class SearchPolicyPresenter extends AbstractMVPPresenter<SearchPolicyView> {
    // CDI MVP includes a built-in CDI event qualifier @CDIEvent which
    // uses a String (method identifier) as it's member
    public static final String SEARCH_SUBMIT = "create_intimation_presenter_search_submit";
    public static final String SERACH_OPTION_CHANGES = "create_intimation_presenter_search_option_changes";
    public static final String RESET_VALUES = "create_intimation_presenter_reset_values";
    public static final String NAVIGATE_ADD_INTIMATION_SCREEN = "create_intimation_presenter_navigate_add_intimation_screen";
    
    protected void submitSearch(@Observes @CDIEvent(SEARCH_SUBMIT) final ParameterDTO parameters) {
        view.searchSubmit();
    }
    
    protected void showLayoutBasedOnSelectedItem(@Observes @CDIEvent(SERACH_OPTION_CHANGES) final ParameterDTO parameters) {
        view.showLayoutBasedOnSelectedItem((ValueChangeEvent) parameters.getPrimaryParameter());
    }
    
    protected void showPolicySearch(@Observes @CDIEvent(MenuItemBean.SEARCH_POLICY) final ParameterDTO parameters) {
    	view.showSearchPolicy();
    }
    
    protected void resetTheFormValues(@Observes @CDIEvent(RESET_VALUES) final ParameterDTO parameters) {
        view.resetAllValues();
    }
    
    protected void showCreateIntimation(@Observes @CDIEvent(NAVIGATE_ADD_INTIMATION_SCREEN) final ParameterDTO parameters) {
//    	TmpPolicy policy = (TmpPolicy) parameters.getPrimaryParameter();
    	
//    	Page.getCurrent().setUriFragment("!" + MenuItemBean.NEW_INTIMATION + "/" + policy.getPolSysId());
//    	Page.getCurrent().reload();
    }
    
    @Override
    public void viewEntered() {
    }

}
