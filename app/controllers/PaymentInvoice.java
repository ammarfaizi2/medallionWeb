package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.CsBilling;
import com.simian.medallion.model.CsBillingDetail;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;

import helpers.UIConstants;
import helpers.UIHelper;
import play.mvc.With;
import vo.BillingParameters;

@With(Secure.class)
public class PaymentInvoice extends MedallionController {
	private static Logger logger = Logger.getLogger(PaymentInvoice.class);
	
	@Check("transaction.paymentInvoice")
	public static void list(BillingParameters param) {
		if (param == null) {
			param = new BillingParameters();
			
			Date appDate = generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
			param.setInvDate(appDate);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_INVOICE_PAYMENT));
		render("PaymentInvoice/list.html", param);
	}
	
	@Check("transaction.paymentInvoice")
	public static void generate(String billingKeys, String payDate) {
		BillingParameters param = new BillingParameters();
		
		logger.debug("billingKeys = "+billingKeys.replaceAll("_", ","));
		try{
			String invoiceNo = transactionService.updateBillingPaymentStatus(billingKeys.replaceAll("_", ","), payDate);param.setMessage("Your Payment No is "+invoiceNo);
			
		}catch(Exception e) {
			param.setMessage("Generate Fail "+e.getMessage());
		}
		renderJSON(param);
	}
	
	@Check("transaction.paymentInvoice")
	public static void search (Paging page, BillingParameters param){
		if(param.isQuery()){
			String mode = "";
//			logger.debug("Page "+page.getsEcho());
//			logger.debug("Page "+page.getiDisplayStart());
//			logger.debug("Page "+page.getiDisplayLength());
//			
//			logger.debug("param.customerSearchKey "+param.getCustomerKey());
//			logger.debug("param.invDate "+param.getInvDate());
//			logger.debug("param.invMonth "+param.getInvMonth());
//			logger.debug("param.invYear "+param.getInvYear());
//			logger.debug("getsSearch "+page.getsSearch());
//			logger.debug("getsiSortCol "+page.getiSortCol_0());
//			logger.debug("getsiSortDir "+page.getsSortDir_0());
			
			if(!Helper.isNullOrEmpty(param.getCustomerKey()))page.addParams("cs.customer_key",page.EQUAL,param.getCustomerKey());
			//page.addParams("to_char(cs.due_date,'yyyyMMdd')", page.LESSEQUAL, Helper.formatYMD(param.getInvDate()));
			page.addParams("cs.invoice_status ",page.EQUAL,'A');
			page.addParams("cs.payment_date", page.ISNULL, null);
			page.addParams("cs.inv_payment_number", page.ISNULL, null);
			if(!Helper.isNullOrEmpty(param.getInvMonth()))page.addParams("cs.billing_month",page.EQUAL, param.getInvMonth());
			if(!Helper.isNullOrEmpty(param.getInvYear()))page.addParams("cs.billing_year",page.EQUAL,param.getInvYear());
			page.addParams(Helper.searchAll("(cs.invoice_no||cf.customer_no||cf.customer_name||TO_CHAR(cs.invoice_date, 'DD/MM/YYYY')||TO_CHAR(cs.due_date, 'DD/MM/YYYY')||to_char((to_date(billing_month,'MM')),'MONTH')||billing_year||csd.billing_amount)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = transactionService.pagingBillingPayment(page);
//			page = transactionService.pagingBilling(page,param.getIncludeZeroAmount(),param.getBillingKeys());
//			logger.debug("page ---->>>>>"+page);
		}
		
		renderJSON(page);
	}
	
	@Check("transaction.paymentInvoice")
	public static void reset() {
		list(null);
	}
	
	public static void entry(String mode) {}
	
	public static void edit(String mode) {}
	
	public static void view(String mode) {}
}





