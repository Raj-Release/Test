package com.shaic.claim.reimbursement.paymentprocesscpuview;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.vaadin.addon.cdimvp.AbstractMVPPresenter;
import org.vaadin.addon.cdimvp.AbstractMVPPresenter.ViewInterface;
import org.vaadin.addon.cdimvp.CDIEvent;
import org.vaadin.addon.cdimvp.ParameterDTO;

import com.shaic.arch.SHAConstants;
import com.shaic.claim.reimbursement.paymentprocesscpu.PaymentProcessCpuService;
@ViewInterface(PaymentProcessCpuPageView.class)

public class PaymentProcessCpuPagePresenter extends AbstractMVPPresenter<PaymentProcessCpuPageView>{
	
	
	public static final String SUBMIT_BUTTON_CLICK = "PaymentProcessCpuSubmit";
	
	public static final String DISCHARGEVOUCHER_BUTTON_CLICK = "Dischargevoucher Button Click";
	
	public static final String PAYMENT_AND_DISCHARGE_BUTTON_CLICK = "Payment and Discharge Button Click";
	
	public static final String HOSP_PAYMENT_LETTER_CLICK = "Hospital Paymentletter Click";
	
	@EJB
	private PaymentProcessCpuService paymentProcessService;    
	
	
	@SuppressWarnings({ "deprecation" })
	public void submitPayment(@Observes @CDIEvent(SUBMIT_BUTTON_CLICK) final ParameterDTO parameters) {
		
		PaymentProcessCpuPageDTO paymentDTO = (PaymentProcessCpuPageDTO) parameters.getPrimaryParameter();
		
     	paymentProcessService.getPaymentClaimsDetails(paymentDTO);
    }
	
	@SuppressWarnings({ "deprecation" })
	public void generateDishcargeVoucher(@Observes @CDIEvent(DISCHARGEVOUCHER_BUTTON_CLICK) final ParameterDTO parameters) {
		
		PaymentProcessCpuPageDTO paymentDTO = (PaymentProcessCpuPageDTO) parameters.getPrimaryParameter();
		
     	String templateName = paymentProcessService.getPaymentTemplateNameWithVersion (SHAConstants.DISCHARGE_VOUCHER, paymentDTO.getModifiedDate());
     	view.generateDischargeVoucherLetter(templateName,paymentDTO);
    }
	
	@SuppressWarnings({ "deprecation" })
	public void generatePaymentAndDishcarge(@Observes @CDIEvent(PAYMENT_AND_DISCHARGE_BUTTON_CLICK) final ParameterDTO parameters) {
		
		PaymentProcessCpuPageDTO paymentDTO = (PaymentProcessCpuPageDTO) parameters.getPrimaryParameter();
		
		String templateName = paymentProcessService.getPaymentTemplateNameWithVersion(SHAConstants.PAYMENT_AND_DISCHARGE_VOUCHER, paymentDTO.getModifiedDate());
		view.generatePaymentAndDischargeLetter(templateName,paymentDTO);
    }
	
	@SuppressWarnings({ "deprecation" })
	public void generateHospPaymentLetter(@Observes @CDIEvent(HOSP_PAYMENT_LETTER_CLICK) final ParameterDTO parameters) {
		
		PaymentProcessCpuPageDTO paymentDTO = (PaymentProcessCpuPageDTO) parameters.getPrimaryParameter();
		
		String templateName = paymentProcessService.getPaymentTemplateNameWithVersion(SHAConstants.HOSPITAL_PAYMENT_LETTER, paymentDTO.getModifiedDate());
		view.generateHospPaymentLetter(templateName,paymentDTO);
    }
	
	@Override
	public void viewEntered() {
		// TODO Auto-generated method stub
		
	}
	
	

}