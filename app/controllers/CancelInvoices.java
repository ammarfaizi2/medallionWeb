package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;
import vo.BillingParameters;
import vo.CancelInvoiceSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsBilling;
import com.simian.medallion.model.CsBillingDetail;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CancelInvoices extends MedallionController {
	private static Logger log = Logger.getLogger(CancelInvoices.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "approval", "view" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE));
	}

	@Check("transaction.cancelInvoice")
	public static void cancel(CsBilling csBilling, String mode) {
		log.debug("cancel. csBilling: " + csBilling + " mode: " + mode);

		String menu = MenuConstants.CS_CANCEL_INVOICE;
		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);

		CsBilling trx = transactionService.createCancelInvoice(menu, username, userKey, csBilling);

		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(UIConstants.DATE_FORMAT);

		result.put("status", "success");
		result.put("billingKey", trx.getBillingKey());
		result.put("invoiceNo", trx.getInvoiceNo());
		result.put("billingStatus", trx.getStatus().trim());
		result.put("cancelDate", dateFormat.format(trx.getCancelDate()));
		renderJSON(result);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			JsonHelper jsonCsBilling = new JsonHelper().getCsBillingSerializer();
			// JsonHelper jsonCsBillingDetail = new
			// JsonHelper().getCsBillingDetailSerializer();
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			CsBilling csBilling = jsonCsBilling.deserialize(maintenanceLog.getNewData(), CsBilling.class);
			csBilling.calcaulte();

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_INVOICE));
			render("CancelInvoices/approval.html", csBilling, taskId, mode, from, operation, maintenanceLogKey);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			CsBilling csBilling = transactionService.approveCancelInvoice(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			String message = "Cancel For Invoice No : " + csBilling.getInvoiceNo() + " is Approved";
			renderJSON(Formatter.resultSuccess(message));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			CsBilling csBilling = new CsBilling();
			if (Helper.isNullOrEmpty(correction)) {
				csBilling = transactionService.approveCancelInvoice(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
			} else {
				csBilling = transactionService.approveCancelInvoice(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "");
			}

			String message = "Cancel For Invoice No : " + csBilling.getInvoiceNo() + " is Rejected";
			renderJSON(Formatter.resultSuccess(message));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.cancelInvoice")
	public static void view(Long key) {
		log.debug("view. key: " + key);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsBilling csBilling = transactionService.getBillingByBillingKey(key);
		csBilling.calcaulte();

		BillingParameters param = new BillingParameters();
		if (csBilling != null) {
			param.setBillingKey(csBilling.getBillingKey());
			param.setCustomerKey(csBilling.getCustomer().getCustomerKey().toString());
			param.setCustomer(csBilling.getCustomer().getCustomerNo());
			param.setCustomerDesc(csBilling.getCustomer().getCustomerName());

			param.setInvMonth(csBilling.getBillingMonth());
			param.setInvYear(csBilling.getBillingYear());

			param.setInvDate(csBilling.getInvoiceDate());
			param.setInvDueDate(csBilling.getDueDate());

			if (param.getDetails() != null) {
				for (CsBillingDetail dtl : csBilling.getDetails()) {
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

		List<CsBillingDetail> csBillingDetails = param.getDetails();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_INVOICE));
		render("CancelInvoices/cancel.html", csBilling, mode, csBillingDetails);
	}

	@Check("transaction.cancelInvoice")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_INVOICE));
		CancelInvoiceSearchParameters params = new CancelInvoiceSearchParameters();
		render("CancelInvoices/list.html", params);
	}

	public static void search(CancelInvoiceSearchParameters params, String param) {
		log.debug("search. params: " + params + " param: " + param);

		// logger.debug("Param = " + param);
		// logger.debug("Param = " + params.customerCode);
		//
		// log.debug("Param = " + param);
		// log.debug("Param = " + params.invoiceSearchNoOperator);
		//
		// List<CsBilling> csBillings =
		// transactionService.searchCancelInvoice(params.invoiceDateFrom
		// , params.invoiceDateTo
		// , params.customerCode
		// , UIHelper.withOperator(params.invoiceSearchNoOperator,
		// params.invoiceNoOperator));
		//
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_INVOICE));
		// render("CancelInvoices/grid.html", csBillings, param);
	}

	@Check("transaction.cancelInvoice")
	public static void paging(Paging page, CancelInvoiceSearchParameters param) {
		log.debug("paging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("cs.invoice_status", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
			page.addParams("cs.cancel_date", page.ISNULL, null);
			page.addParams("cs.invoice_date", page.GREATEQUAL, param.invoiceDateFrom);
			page.addParams("cs.invoice_date", page.LESSEQUAL, param.invoiceDateTo);
			page.addParams("cf.customer_no", page.EQUAL, param.customerCode);
			page.addParams("cs.invoice_no", param.invoiceNoOperator, UIHelper.withOperator(param.invoiceSearchNoOperator, param.invoiceNoOperator));

			page.addParams(Helper.searchAll("(cf.customer_name||cs.invoice_no||cf.customer_no||" + "to_char(cs.invoice_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||to_char(cs.due_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = transactionService.pagingCancelInvoice(page);
		}

		renderJSON(page);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit() {
		log.debug("edit. ");
	}

}