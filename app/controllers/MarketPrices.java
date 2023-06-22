package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.MarketPriceSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScMarketPrice;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class MarketPrices extends MedallionController {
	private static Logger log = Logger.getLogger(MarketPrices.class);

	@Before(only = { "list", "dedupe" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		String securityPrice = LookupConstants._SECURITY_PRICE_GROUP;
		renderArgs.put("securityPrice", securityPrice);

		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);

		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);
		// GnLookup categorySub =
		// generalService.getLookup(LookupConstants.SA_TRANSACTION_CATEGORY_SUBS);
		// renderArgs.put("sub", categorySub.getLookupDescription());
	}

	@Before(unless = { "list entry edit save back" })
	public static void setup(ScMarketPrice marketPrice) {
		log.debug("setup. marketPrice: " + marketPrice);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("transaction.marketPrice")
	public static void list(String mode) {
		log.debug("list. mode: " + mode);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
		render("MarketPrices/list.html");
	}

	/*
	 * public static void search(MarketPriceSearchParameters params) { //
	 * if(params.marketPriceSearchAccountKey != null){
	 * if(params.marketPriceSearchGroupCodeId != null){ List<ScMarketPrice>
	 * marketPrices =securityService.searchMarketPrice(
	 * params.marketPriceSearchMarketDate, params.marketPriceSearchGroupCodeId);
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
	 * render("MarketPrices/grid.html", marketPrices); } //
	 * if(params.marketPriceSearchAccountKey == null){
	 * if(params.marketPriceSearchGroupCodeId == null){ List<ScMarketPrice>
	 * marketPrices =securityService.searchMarketPrice(
	 * params.marketPriceSearchMarketDate, null);
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
	 * render("MarketPrices/grid.html", marketPrices); } }
	 */

	@Check("transaction.marketPrice")
	public static void pagingList(Paging page, MarketPriceSearchParameters param) {
		log.debug("pagingList. page: " + page + " param: " + param);

		page.addParams("mp.marketDate", page.GREATEQUAL, param.marketPriceSearchFrom);
		page.addParams("mp.marketDate", page.LESSEQUAL, param.marketPriceSearchTo);
		page.addParams("spg.lookupId", page.EQUAL, param.groupCodeId);
		page.addParams("st.securityType", page.EQUAL, param.securityType);
		page.addParams("sc.securityKey", page.EQUAL, param.securityKey);
		page.addParams(Helper.searchAll("(st.securityType||sc.securityId||mp.closingPrice||mp.highestPrice||mp.lowestPrice||" + "to_char(mp.marketDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||mp.isActive)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = securityService.pagingMarketPrice(page);
		renderJSON(page);
	}

	@Check("transaction.marketPrice")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		ScMarketPrice marketPrice = new ScMarketPrice();
		marketPrice.setMarketDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		marketPrice.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		boolean specialCustomer = false;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
		render("MarketPrices/detail.html", marketPrice, specialCustomer, mode);
	}

	@Check("transaction.marketPrice")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		ScMarketPrice marketPrice = securityService.getMarketPrice(id);
		String status = marketPrice.getRecordStatus();
		// if (marketPrice.getAccount() != null){
		// renderArgs.put("specialCustomer",true);
		// }else {
		// renderArgs.put("specialCustomer",false);
		// }
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
		render("MarketPrices/detail.html", marketPrice, mode, status);
	}

	@Check("transaction.marketPrice")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScMarketPrice marketPrice = securityService.getMarketPrice(id);
		// if (marketPrice.getAccount() != null){
		// renderArgs.put("specialCustomer",true);
		// }else {
		// renderArgs.put("specialCustomer",false);
		// }
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
		render("MarketPrices/detail.html", marketPrice, mode);

	}

	@Check("transaction.marketPrice")
	public static void save(ScMarketPrice marketPrice, String mode, boolean specialCustomer, String status) {
		log.debug("save. marketPrice: " + marketPrice + " mode: " + mode + " specialCustomer: " + specialCustomer + " status: " + status);

		// Validate here
		if (marketPrice != null) {
			validation.required("Price Date is", marketPrice.getMarketDate());
			validation.required("Price Reference is", marketPrice.getSecurityPriceGroup().getLookupCode());
			validation.required("Security Type is", marketPrice.getSecurity().getSecurityType().getSecurityType());
			validation.required("Security Code is", marketPrice.getSecurity().getSecurityId());
			validation.required("Closing Price is", marketPrice.getClosingPrice());
			validation.required("High Price is", marketPrice.getHighestPrice());
			validation.required("Low Price is", marketPrice.getLowestPrice());
			if (marketPrice.getHighestPrice() != null && marketPrice.getClosingPrice() != null && marketPrice.getLowestPrice() != null) {
				if ((marketPrice.getHighestPrice().compareTo(marketPrice.getClosingPrice()) < 0) || (marketPrice.getClosingPrice().compareTo(marketPrice.getLowestPrice()) < 0)) {
					validation.required("Reason is", marketPrice.getRemarks());
				}
			}

			/*
			 * if(specialCustomer == true){
			 * renderArgs.put("specialCustomer",true);
			 * validation.required("Customer No is",
			 * marketPrice.getAccount().getAccountNo()); }
			 */if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
				render("MarketPrices/detail.html", marketPrice, mode, specialCustomer, status);
			} else {
				Long id = marketPrice.getMarketPriceKey();
				serializerService.serialize(session.getId(), marketPrice.getMarketPriceKey(), marketPrice);
				confirming(id, mode, status);
			}
		} else {
			// String url = Router.getFullUrl("Application.index");
			// redirect(url);
			// flash.error("argument.null",marketPrice);
			entry();
		}
	}

	@Check("transaction.marketPrice")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		ScMarketPrice marketPrice = serializerService.deserialize(session.getId(), id, ScMarketPrice.class);
		/*
		 * if (marketPrice.getAccount().getCustodyAccountKey() == null){
		 * renderArgs.put("specialCustomer",false); } if
		 * (marketPrice.getAccount().getCustodyAccountKey() != null) {
		 * renderArgs.put("specialCustomer",true); }
		 */flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
		render("MarketPrices/detail.html", marketPrice, mode, status);
	}

	@Check("transaction.marketPrice")
	public static void confirm(String mode, ScMarketPrice marketPrice, ScTypeMaster securityType, String status) {
		log.debug("confirm. mode: " + mode + " marketPrice: " + marketPrice + " securityType: " + securityType + " status: " + status);

		try {
			if (marketPrice == null) {
				// String url = Router.getFullUrl("Application.index");
				// redirect(url);
				back(null, mode, status);
			}
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				if (marketPrice.getSecurity() != null && marketPrice.getSecurityPriceGroup() != null) {
					ScMarketPrice oldScMarketPrice = securityService.getMarketPrice(marketPrice.getMarketDate(), marketPrice.getSecurity().getSecurityKey(), marketPrice.getSecurityPriceGroup().getLookupId());
					if (oldScMarketPrice != null)
						throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
				}
			}
			securityService.saveMarketPrice(MenuConstants.SC_MARKET_PRICE, marketPrice, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list(mode);
		} catch (MedallionException e) {
			// flash.error("Duplicate Error! Code : ' "+marketPrice.getSecurity().getSecurityId()+" ' Already Exist",
			// marketPrice.getSecurity().getSecurityId());
			flash.error("Security Code : ' " + marketPrice.getSecurity().getSecurityId() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
			render("MarketPrices/detail.html", marketPrice, mode, confirming, status);
		}
	}

	@Check("transaction.marketPrice")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		ScMarketPrice marketPrice = serializerService.deserialize(session.getId(), id, ScMarketPrice.class);
		// if (marketPrice.getAccount().getCustodyAccountKey() == null){
		/*
		 * if ((marketPrice.getAccount() ==
		 * null)||((marketPrice.getAccount().getAccountNo().equals("")))){
		 * logger.debug("account >>>" +marketPrice.getAccount().getAccountNo());
		 * renderArgs.put("specialCustomer",false); }else{
		 * renderArgs.put("specialCustomer",true); }
		 */flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_MARKET_PRICE));
		render("MarketPrices/detail.html", marketPrice, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			log.debug("masuk sini ga??");

			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			ScMarketPrice marketPrice = json.readValue(maintenanceLog.getNewData(), ScMarketPrice.class);
			/*
			 * if (marketPrice.getAccount() != null){
			 * renderArgs.put("specialCustomer",true); }else{
			 * renderArgs.put("specialCustomer",false); }
			 */
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("MarketPrices/approval.html", marketPrice, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// //('${ctx}' + url + '?taskId=' + taskId + '&keyId=' + key +
		// '&maintenanceLogKey=' + maintenanceLogKey);
		// try {
		// securityService.approve(taskId, operation, maintenanceLogKey);
		// } catch (MedallionException me) {
		// logger.debug(me.getMessage(),me);
		// }
		//
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveMarketPrice(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveMarketPrice(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
