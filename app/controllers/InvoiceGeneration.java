package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import play.mvc.With;
import vo.BillingParameters;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.CsBilling;
import com.simian.medallion.model.CsBillingDetail;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;

@With(Secure.class)
public class InvoiceGeneration extends MedallionController {
	private static Logger log = Logger.getLogger(InvoiceGeneration.class);

	@Check("transaction.invoiceGeneration")
	public static void list(BillingParameters param) {
		log.debug("list. param: " + param);

		if (param == null) {
			param = new BillingParameters();
			param.setIncludeZeroAmount(new Boolean(false));
			param.setFilter(BillingParameters.FILTER_ALL);
			param.setIncludeZeroAmount(new Boolean(false));

			// Calendar cal = Calendar.getInstance();
			// cal.setTime(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
			// logger.debug("Month", formatMonth(cal.get(Calendar.MONTH)));
			// param.setInvMonth(formatMonth(cal.get(Calendar.MONTH)));
			// param.setInvYear(String.valueOf(cal.get(Calendar.YEAR)));
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_INVOICE_GENERATION));
		render("InvoiceGeneration/list.html", param);
	}

	@Check("transaction.invoiceGeneration")
	public static void generate(String billingKeys) {
		log.debug("generate. billingKeys: " + billingKeys);

		/*
		 * log.debug("param.customerSearchKey "+param.getCustomerKey());
		 * log.debug("param.accountSearchKey "+param.getAccountKey());
		 * log.debug("param.billingMonth "+param.getInvMonth());
		 * log.debug("param.billingYear "+param.getInvYear()); //
		 * page.addParams("c.customerKey",page.EQUAL,param.getCustomerKey()); //
		 * page.addParams("b.status ",page.EQUAL,'O');
		 * page.addParams("b.billingMonth",page.EQUAL, param.getInvMonth());
		 * page.addParams("b.billingYear",page.EQUAL,param.getInvYear()); page =
		 * transactionService
		 * .pagingBilling(page,param.getIncludeZeroAmount(),param
		 * .getBillingKeys());
		 */
		BillingParameters param = new BillingParameters();
		try {
			transactionService.updateBillingStatus(billingKeys.split("_"));
			param.setMessage("Generate Invoice Successfully");
			param.setBillingKeys(billingKeys);

		} catch (Exception e) {
			param.setMessage("Generate Fail " + e.getMessage());
		}
		renderJSON(param);

		// renderJSON(page);
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_INVOICE_GENERATION));
		// render("InvoiceGeneration/list.html", param);
	}

	@Check("transaction.invoiceGeneration")
	public static void search(Paging page, BillingParameters param) {
		log.debug("search. page: " + page + " param: " + param);

		if (param.isQuery()) {
			//String mode = "";
			log.debug("Page " + page.getsEcho());
			log.debug("Page " + page.getiDisplayStart());
			log.debug("Page " + page.getiDisplayLength());

			log.debug("param.customerSearchKey " + param.getCustomerKey());
			log.debug("param.accountSearchKey " + param.getAccountKey());
			log.debug("Billing keys " + param.getBillingKeys());
			log.debug("getsSearch " + page.getsSearch());
			log.debug("getsiSortCol " + page.getiSortCol_0());
			log.debug("getsiSortDir " + page.getsSortDir_0());

			log.debug("include zero 1" + param.getIncludeZeroAmount());
			if (param.getIncludeZeroAmount() == null) {
				param.setIncludeZeroAmount(new Boolean(false));
			}

			log.debug("include zero  2" + param.getIncludeZeroAmount());

			page.addParams("cs.customer_key", page.EQUAL, param.getCustomerKey());
			page.addParams("cs.billing_month", page.EQUAL, param.getInvMonth());
			page.addParams("cs.billing_year", page.EQUAL, param.getInvYear());

			if ((param.getBillingKeys().equals("")) || (param.getBillingKeys() == null)) {
				//mode = UIConstants.DISPLAY_MODE_ENTRY;
				page.addParams("cs.invoice_status ", page.EQUAL, 'O');
			} else {
				//mode = UIConstants.DISPLAY_MODE_VIEW;
				page.addParams("cs.invoice_status ", page.EQUAL, 'A');
			}
			page.addParams(Helper.searchAll("(cs.invoice_no||cf.customer_no||cf.customer_name||" + "to_char(cs.invoice_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(cs.due_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			/*
			 * if ((!param.getBillingKey().equals("")) &&
			 * (param.getBillingKeys()!= null)){ try{
			 * transactionService.updateBillingStatus
			 * (param.getBillingKeys().split("_"));
			 * param.setMessage("Generate Success");
			 * 
			 * }catch(Exception e) {
			 * param.setMessage("Generate Fail "+e.getMessage()); } }
			 */
			page = transactionService.pagingBilling(page, param.getIncludeZeroAmount(), param.getBillingKeys());
			log.debug("page ---->>>>>" + page);
		}

		renderJSON(page);
	}

	/*
	 * public static void search(BillingParameters param) {
	 * logger.debug("customer  "+param.getCustomerKey());
	 * logger.debug("account  "+param.getAccountKey());
	 * logger.debug("month  "+param.getInvMonth());
	 * logger.debug("year  "+param.getInvYear());
	 * logger.debug("include  "+param.isIncludeZeroAmount());
	 * 
	 * if (BillingParameters.FILTER_CUSTOMER_NO.equals(param.getFilter())) {
	 * validation.required("Customer", param.getCustomer()); if
	 * (param.getCustomer() == null || "".equals(param.getCustomer())) {
	 * param.setCustomerDesc(null); } }
	 * 
	 * if (BillingParameters.FILTER_ACCOUNT_NO.equals(param.getFilter())) {
	 * validation.required("Account", param.getAccount()); if
	 * (param.getAccount() == null || "".equals(param.getAccount())) {
	 * param.setAccountDesc(null); } }
	 * 
	 * validation.required("Invoice Month", param.getInvMonth());
	 * validation.required("Invoice Year", param.getInvYear());
	 * 
	 * List<CsBilling> billings = new ArrayList<CsBilling>();
	 * 
	 * if (!validation.hasErrors()) { if (param.isIncludeZeroAmount() == null) {
	 * param.setIncludeZeroAmount(new Boolean(false)); } //billings =
	 * transactionService.getBilling(param.getCustomerKey(),
	 * param.getAccountKey(), param.getInvMonth(), param.getInvYear(),
	 * param.isIncludeZeroAmount()); }
	 * 
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants
	 * .CS_INVOICE_GENERATION)); render("InvoiceGeneration/list.html", param,
	 * billings); }
	 */
	/*
	 * public static void updateBillingDate(BillingParameters param){
	 * logger.debug("billingKey "+param.getBillingKey());
	 * logger.debug("invoiceDate "+param.getInvDate());
	 * logger.debug("invoiceDueDate "+param.getInvDueDate());
	 * 
	 * String validMonth = param.getInvMonth(); String validYear =
	 * param.getInvYear(); SimpleDateFormat yyyyMM = new
	 * SimpleDateFormat("yyyyMM"); SimpleDateFormat yyyyMMdd = new
	 * SimpleDateFormat("yyyyMMdd");
	 * 
	 * 
	 * Calendar cal = Calendar.getInstance();
	 * cal.setTime(generalService.getApplicationDate
	 * (UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate()); String appYYYYMM
	 * = yyyyMM.format(cal.getTime()); String guiYYYYMM = param.getInvYear() +
	 * param.getInvMonth();
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); Integer result =
	 * new Integer(0);
	 * 
	 * 
	 * 
	 * // String guiYYYYMM = param.getInvYear() + param.getInvMonth();
	 * 
	 * if (validMonth != null && validYear != null && param.getInvDate() != null
	 * ) { String endInvDate = validYear+validMonth; String strInvDate =
	 * yyyyMM.format(param.getInvDate()); if (strInvDate.compareTo(endInvDate)
	 * <= 0) { validation.addError("Invoice Date",
	 * "billing.process.invdatecannotbelestthenendofinvoice"); } } if
	 * (!validation.hasErrors()) { try{ // Date dateInvoiceDate =
	 * sdf.parse(param.getInvDate()); // Date dateDueDate =
	 * sdf.parse(param.getInvDueDate()); Date dateInvoiceDate =
	 * param.getInvDate(); Date dateDueDate = param.getInvDueDate(); CsBilling
	 * csBilling = transactionService.updateBillingDate(param.getBillingKey(),
	 * dateInvoiceDate, dateDueDate);
	 * 
	 * if (csBilling.getInvoiceDate().equals(dateInvoiceDate) &&
	 * csBilling.getDueDate().equals(dateDueDate)) { result = new Integer(1); }
	 * }catch(Exception e) { result = new Integer(0); } }
	 * 
	 * renderJSON(result); }
	 */
	@Check("transaction.invoiceGeneration")
	public static void updateBillingDate(Long billingKey, String invoiceDate, String dueDate) {
		log.debug("updateBillingDate. billingKey: " + billingKey + " invoiceDate: " + invoiceDate + " dueDate: " + dueDate);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Integer result = new Integer(0);
		try {
			Date dateInvoiceDate = sdf.parse(invoiceDate);
			Date dateDueDate = sdf.parse(dueDate);
			CsBilling csBilling = transactionService.updateBillingDate(billingKey, dateInvoiceDate, dateDueDate);

			if (csBilling.getInvoiceDate().equals(dateInvoiceDate) && csBilling.getDueDate().equals(dateDueDate)) {
				result = new Integer(1);
			}
		} catch (Exception e) {
			result = new Integer(0);
		}
		renderJSON(result);
	}

	@Check("transaction.invoiceGeneration")
	public static void getBillingByBillingKey(Long billingKey) {
		log.debug("getBillingByBillingKey. billingKey: " + billingKey);

		CsBilling billing = transactionService.getBillingByBillingKey(billingKey);

		BillingParameters param = new BillingParameters();
		if (billing != null) {
			param.setBillingKey(billing.getBillingKey());
			param.setCustomerKey(billing.getCustomer().getCustomerKey().toString());
			param.setCustomer(billing.getCustomer().getCustomerNo());
			param.setCustomerDesc(billing.getCustomer().getCustomerName());

			param.setInvNo(billing.getInvoiceNo());

			param.setInvMonth(billing.getBillingMonth());
			param.setInvYear(billing.getBillingYear());

			param.setInvDate(billing.getInvoiceDate());
			param.setInvDueDate(billing.getDueDate());

			if (param.getDetails() != null) {
				for (CsBillingDetail dtl : billing.getDetails()) {
					CsBillingDetail newbd = new CsBillingDetail();
					GnLookup lookup = new GnLookup();
					lookup.setLookupDescription(dtl.getChargeGroup().getLookupDescription());
					newbd.setChargeGroup(lookup);
					newbd.setBillingAmount(dtl.getBillingAmount());
					newbd.setBillingFee(dtl.getBillingFee());
					newbd.setBillingTax(dtl.getBillingTax());
					param.getDetails().add(newbd);
				}
			}
		}

		renderJSON(param);
	}

	@Check("transaction.invoiceGeneration")
	public static void reset() {
		log.debug("reset. ");

		list(null);
	}

	/*
	 * public static void search (Paging page, BillingParameters param){
	 * log.debug("Page "+page.getsEcho());
	 * log.debug("Page "+page.getiDisplayStart());
	 * log.debug("Page "+page.getiDisplayLength());
	 * 
	 * log.debug("param.customerSearchKey "+param.getCustomerKey());
	 * log.debug("param.accountSearchKey "+param.getAccountKey());
	 * log.debug("getsSearch "+page.getsSearch());
	 * log.debug("getsiSortCol "+page.getiSortCol_0());
	 * log.debug("getsiSortDir "+page.getsSortDir_0());
	 * 
	 * 
	 * log.debug("include zero 1"+param.getIncludeZeroAmount()); if
	 * (param.getIncludeZeroAmount() == null) { param.setIncludeZeroAmount(new
	 * Boolean(false)); }
	 * 
	 * log.debug("include zero  2"+param.getIncludeZeroAmount());
	 * page.addParams("c.customerKey",page.EQUAL,param.getCustomerKey());
	 * page.addParams("b.status ",page.EQUAL,'O');
	 * page.addParams("b.billingMonth",page.EQUAL, param.getInvMonth());
	 * page.addParams("b.billingYear",page.EQUAL,param.getInvYear());
	 * 
	 * page =
	 * transactionService.pagingBilling(page,param.getIncludeZeroAmount());
	 * renderJSON(page); }
	 */
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);
	}

	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);
	}

	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}
}
