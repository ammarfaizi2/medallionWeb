package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.BillingParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.FaMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class BillingProcesses extends MedallionController {
	private static Logger logger = Logger.getLogger(BillingProcesses.class);
	
	public static Map<String, List<String>> billingLogMap = new HashMap<String, List<String>>();
	
	@Before(only={"list", "dedupe"})
	public static void setupList() {
		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);
	}
	
	@Check("transaction.billingProcess")
	public static void list(BillingParameters param) {
		if (param == null) {
			param = new BillingParameters();
		
			//Date date = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
			
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(date);
			param.setFilter(BillingParameters.FILTER_ALL);
			//param.setInvMonth(formatMonth(cal.get(Calendar.MONTH)));
			//param.setInvYear(String.valueOf(cal.get(Calendar.YEAR)));
			//param.setInvDate(cal.getTime());
			//param.setInvDueDate(cal.getTime());
		}
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CS_BILLING_PROCESS+param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_BILLING_PROCESS));
		render("BillingProcesses/list.html", param);
	}
	
	public static void entry(String mode) {
		
	}
	
	public static void edit(String mode) {
		
	}
	
	public static void view(String mode) {
		
	}
	
	@Check("transaction.billingProcess")
	public static void reprocess(BillingParameters param) {
		logger.debug("Filter : "+param.getFilter());
		logger.debug("customerNo : "+param.getCustomerKey());
		logger.debug("month : "+param.getInvMonth());
		logger.debug("year : "+param.getInvYear());
		logger.debug("InvDate : "+param.getInvDate());
		logger.debug("InvDueDate : "+param.getInvDueDate());
		
		int jumlah = transactionService.getInvoiceCount(param.getCustomerKey(), param.getInvMonth(), param.getInvYear(), param.getInvDate(), param.getInvDueDate());
		renderJSON(jumlah);
	}
	
	@Check("transaction.billingProcess")
	public static void process(BillingParameters param) {
		logger.debug("filter "+param.getFilter());
		logger.debug("month "+param.getInvMonth());
		logger.debug("year "+param.getInvYear());
		logger.debug("InvDate "+param.getInvDate());
		logger.debug("DueDate "+param.getInvDueDate());
		logger.debug("Consolidate "+param.isConsolidate());
		
		logger.debug("Customer "+param.getCustomer());
		logger.debug("CustomerKey "+param.getCustomerKey());
		logger.debug("CustomerDesc "+param.getCustomerDesc());
		String sessionUuidX = (String) session.get(PROCESSMARK + MenuConstants.CS_BILLING_PROCESS+param.getSessionTag());
		if(sessionUuidX ==null){
			session.put(PROCESSMARK + MenuConstants.CS_BILLING_PROCESS+param.getSessionTag(), param.getProcessMark());
			sessionUuidX = (String) session.get(PROCESSMARK + MenuConstants.CS_BILLING_PROCESS+param.getSessionTag());
		}
		if (isDoubleSubmission(MenuConstants.CS_BILLING_PROCESS+param.getSessionTag())) { list(param); }
		
		if (BillingParameters.FILTER_CUSTOMER_NO.equals(param.getFilter())) {
			validation.required("Customer", param.getCustomer());
			if (param.getCustomer() == null || "".equals(param.getCustomer())) {
				param.setCustomerDesc(null);
			}
		}
		
		validation.required("Invoice Month", param.getInvMonth());
		validation.required("Invoice Year", param.getInvYear());
		validation.required("Invoice Date", param.getInvDate());
		validation.required("Invoice Due Date", param.getInvDueDate());
		
		boolean validMonth = Helper.isMonth(param.getInvMonth());
		if (!validMonth) {
			validation.addError("Invoice Month", "billing.process.invalidmonth");
		}
		
		boolean validYear = Helper.isYear(param.getInvYear());
		if (!validYear) {
			validation.addError("Invoice Year", "billing.process.invalidyear");
		}
		
		if (validMonth && validYear) {
			Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
			
			if (Helper.isGreaterYM(Helper.toDate(param.getInvYear(), param.getInvMonth()), appDate)) {
				validation.addError("Invoice Year", "billing.process.cannotbegreaterthenappmonthyear");	
			}
			
			Date lastDate = Helper.getLastDate(Helper.toDate(param.getInvYear(), param.getInvMonth()));
			if (param.getInvDate() != null && Helper.isLessYMD(param.getInvDate(), lastDate)) {
				validation.addError("Invoice Date", "billing.process.invdatecannotbelestthenendofinvoice");	
			}
		}
		
		if (param.getInvDate() != null && param.getInvDueDate() != null && Helper.isLessEqualYMD(param.getInvDueDate(), param.getInvDate())) {
			validation.addError("Invoice Due Date", "billing.process.invduedatecannotbelesttheninvdate");
		}
		
		List<String> logs = new ArrayList<String>();
		logger.debug("validation.hasErrors() "+validation.hasErrors());
		if (!validation.hasErrors()) {
			try{
				String username = session.get(UIConstants.SESSION_USERNAME);
				logs = transactionService.billingProcess(param.getFilter(), param.getCustomer(), param.getAccount(), param.getInvMonth()+param.getInvYear(), param.getInvDate(), param.getInvDueDate(), username);
			}catch(Exception e) {
				logs.add("Fail execute Billing process "+e.getMessage());
			}
		}
		
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CS_BILLING_PROCESS+param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_BILLING_PROCESS));
		render("BillingProcesses/list.html", param, logs);
	}
	
	@Check("transaction.billingProcess")
	public static void processAjax(BillingParameters param) {
		logger.debug("processAjaxBilling "+param.toString());
		
		List<String> billingLogs = new ArrayList<String>();
		
		Map<String, String> validations = new HashMap<String, String>();
		
		if (BillingParameters.FILTER_CUSTOMER_NO.equals(param.getFilter())) {
			if (Helper.isEmpty(param.getCustomer())) validations.put("customerCodeErr", "Required");
			if (param.getCustomer() == null || "".equals(param.getCustomer())) {
				param.setCustomerDesc(null);
			}
		}
		
		if (Helper.isEmpty(param.getInvMonth())) validations.put("invMonthErr", "Required");
		if (Helper.isEmpty(param.getInvYear())) validations.put("invYearErr", "Required");
		if (Helper.isNull(param.getInvDate())) validations.put("invDateErr", "Required");
		if (Helper.isNull(param.getInvDueDate())) validations.put("invDueDateErr", "Required");
		
		
		boolean validMonth = Helper.isMonth(param.getInvMonth());
		if (!validMonth) {
			validations.put("invMonthErr", Messages.get("billing.process.invalidmonth"));
		}
		
		boolean validYear = Helper.isYear(param.getInvYear());
		if (!validYear) {
			validations.put("invYearErr", Messages.get("billing.process.invalidyear"));
		}
		
		if (validMonth && validYear) {
			Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
			
			if (Helper.isGreaterYM(Helper.toDate(param.getInvYear(), param.getInvMonth()), appDate)) {
				validations.put("invYearErr", Messages.get("billing.process.cannotbegreaterthenappmonthyear"));
			}
			
			Date lastDate = Helper.getLastDate(Helper.toDate(param.getInvYear(), param.getInvMonth()));
			if (param.getInvDate() != null && Helper.isLessYMD(param.getInvDate(), lastDate)) {
				validations.put("invDateErr", Messages.get("billing.process.invdatecannotbelestthenendofinvoice"));
			}
		}

		if (param.getInvDate() != null && param.getInvDueDate() != null && Helper.isLessEqualYMD(param.getInvDueDate(), param.getInvDate())) {
			validations.put("invDueDateErr", Messages.get("billing.process.invduedatecannotbelesttheninvdate"));
		}
		
		if (validations.isEmpty()) {
			try{
				String username = session.get(UIConstants.SESSION_USERNAME);
				logger.error("start billingProcess ");
				billingLogs = transactionService.billingProcess(param.getFilter(), param.getCustomer(), param.getAccount(), param.getInvMonth()+param.getInvYear(), param.getInvDate(), param.getInvDueDate(), username);
				billingLogMap.put(param.getFilter(), billingLogs);
				logger.error("finish billingProcess ");
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
				billingLogs.add("Fail execute Billing Process "+e.getMessage());
					
			}
		}else{
			billingLogMap.remove(param.getCustomer());
		}
		logger.error("end process");
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("errorSize", validations.size());
		result.put("validations", validations);
		renderJSON(result);
	}
	
	@Check("transaction.billingProcess")
	public static void processAjaxLog(BillingParameters param) {
		logger.debug("processAjaxLog "+param.toString());
		
		// Status
		// W : Waiting process to finish
		// F : Process Finish
		// G : Gone stop process (not implemented) 
		
		String billingLogKey = param.getFilter();
		List<String> billingLogs = billingLogMap.get(billingLogKey);
		
		String status = (billingLogs == null) ?  "W" : (billingLogs.isEmpty()) ? "W" : "F";		
		if ("F".equals(status)) billingLogMap.remove(billingLogKey);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("logs", billingLogs);
		result.put("status", status);
		renderJSON(result);
	}
}
