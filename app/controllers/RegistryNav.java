package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Router;
import play.mvc.With;
import vo.NavSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgNavId;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class RegistryNav extends Registry {
	private static Logger log = Logger.getLogger(RegistryNav.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);

		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);

	}

	@Check("transaction.registryNav")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
		NavSearchParameters params = new NavSearchParameters();
		render("RegistryNav/list.html", params);
	}

	@Check("transaction.registryNav")
	public static void search(Paging page, NavSearchParameters param) {
		log.debug("search. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("rn.id.navDate", page.GREATEQUAL, param.navSearchNavDateFrom);
			page.addParams("rn.id.navDate", page.LESSEQUAL, param.navSearchNavDateTo);
			page.addParams("rn.id.productCode", page.EQUAL, param.navSearchProductCode);
			page.addParams(Helper.searchAll("(to_char(rn.id.navDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')" + "||rn.id.productCode||rn.nav||rn.recordStatus||" + "rn.isActive)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingNAVPrice(page);
		}

		renderJSON(page);
	}

	@Check("transaction.registryNav")
	public static void edit(String productCode, String navDate, boolean isNewRec) {
		log.debug("edit. productCode: " + productCode + " navDate: " + navDate + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		RgNav nav = new RgNav();
		isNewRec = false;
		try {
			RgNavId id = new RgNavId(productCode, parseDate(navDate, appProp.getDateFormat()));
			nav = taService.loadNav(id);
			RgProduct prod = taService.loadProduct(nav.getRgProduct().getProductCode());
			nav.setOffer(prod.getOfferPricePct());
			nav.setBid(prod.getBidPricePct());
			if ((nav.getBidPricePct() != null) && (nav.getOfferPricePct() != null)) {
				nav.setCheckBidOffer(true);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		String status = nav.getRecordStatus() + " ";
		log.debug("from edit status = --" + status + "--");

		log.debug("FROM EDIT isNewRec = " + isNewRec);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
		render("RegistryNav/detail.html", nav, mode, isNewRec, status);
	}

	@Check("transaction.registryNav")
	public static void view(String productCode, String navDate) {
		log.debug("view. productCode: " + productCode + " navDate: " + navDate);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgNav nav = new RgNav();

		try {
			RgNavId id = new RgNavId(productCode, parseDate(navDate, appProp.getDateFormat()));
			nav = taService.loadNav(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
		render("RegistryNav/detail.html", nav, mode);
	}

	@Check("transaction.registryNav")
	public static void entry(boolean isNewRec) {
		log.debug("entry. isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		RgNav nav = new RgNav();
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		String navDate = dateFormat.format(applicationDate);
		isNewRec = true;
		// nav.setId(new RgNavId("", applicationDate));
		log.debug("FROM ENTRY isNewRec = " + isNewRec);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
		render("RegistryNav/detail.html", nav, mode, isNewRec, navDate);
	}

	@Check("transaction.registryNav")
	public static void save(RgNav nav, String mode, boolean isNewRec, String status) {
		log.debug("save. nav: " + nav + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (nav == null)
			entry(isNewRec);
		// logger.debug("NAV DATE = " +nav.getId().getNavDate());
		// logger.debug("Eff Date = " +nav.getRgProduct().getEffectiveDate());
		// logger.debug("Liq Date = "
		// +nav.getRgProduct().getLiquidDate());nav.rgProduct.productCode
		validation.required("Fund Code is", nav.getRgProduct().getProductCode());
		validation.required("Date is", nav.getId().getNavDate());
		validation.required("NAV/Unit is", nav.getNav());

		if (nav.isCheckBidOffer()) {
			validation.required("Offer is", nav.getOfferPricePct());
			validation.required("Bid is", nav.getBidPricePct());
		}
		if (validation.hasErrors()) {
			String navDate = new String();
			// if(nav.getId().getNavDate()!=null){
			// SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			// Date applicationDate =
			// generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			// navDate = dateFormat.format(applicationDate);
			// }
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
			render("RegistryNav/detail.html", nav, mode, isNewRec, status, navDate);
		} else {
			RgNavId id = nav.getId();
			// logger.debug("[save] apakah nilai id itu >>> " + id);
			serializerService.serialize(session.getId(), id, nav);
			log.debug("FROM SAVE isNewRec = " + isNewRec);
			confirming(id, mode, isNewRec, status);
		}
	}

	@Check("transaction.registryNav")
	public static void confirming(RgNavId id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		renderArgs.put("confirming", true);
		RgNav nav = serializerService.deserialize(session.getId(), id, RgNav.class);
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		String navDate = dateFormat.format(nav.getId().getNavDate());
		if (nav.getRgProduct().getMinNavAmt() != null) {
			if (nav.getNavAmount() == null || nav.getNavAmount().doubleValue() <= nav.getRgProduct().getMinNavAmt().doubleValue()) {
				flash.put("navMinAmtError", "NAV Amount is Less than Minimum Nav");
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
		render("RegistryNav/detail.html", nav, mode, isNewRec, status, navDate);
	}

	@Check("transaction.registryNav")
	public static void confirm(RgNav nav, String mode, boolean isNewRec, String status) {
		log.debug("confirm. nav: " + nav + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (nav == null)
			back(null, mode, isNewRec, status);
		try {
			try {
				Helper.showProperties(nav);
			} catch (Exception e) {
				String url = Router.getFullUrl("Application.index");
				redirect(url);
			}
			log.debug("FROM CONFIRM isNewRec = " + isNewRec);

			if (nav.getTotalUnit() == null || nav.getTotalUnit().equals(BigDecimal.ZERO)) {
				nav.setTotalUnit(null);
				log.debug("after totalUnit = " + nav.getTotalUnit());
			}
			if (nav.getNavAmount() == null || nav.getNavAmount().equals(BigDecimal.ZERO)) {
				nav.setNavAmount(null);
				log.debug("after navAmount = " + nav.getNavAmount());
			}

			// DUPLICATE FUND CODE & DATE
			// =======================================================================================
			List<RgNav> navs = taService.listNav();
			for (RgNav navInTable : navs) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					String fundCodeTable = navInTable.getId().getProductCode();
					String fundCode = nav.getId().getProductCode();
					Date navDateTable = navInTable.getId().getNavDate();
					Date navDate = nav.getId().getNavDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());

					if ((fundCodeTable.equals(fundCode)) && ((dateFormat.format(navDateTable)).equals(dateFormat.format(navDate)))) {
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
						flash.error("Fund Code : ' " + nav.getRgProduct().getProductCode() + " ' " + ExceptionConstants.DATA_DUPLICATE);
						boolean confirming = true;
						render("RegistryNav/detail.html", nav, mode, confirming, isNewRec, status);
					}
				}
			}
			// ==================================================================================================================
			taService.saveNav(MenuConstants.RG_NAV, nav, session.get("username"), "", isNewRec, session.get("userKey"));
			// taService.saveNav(nav);
			list();
		} catch (MedallionException e) {
			// flash.error(ex.getErrorCode(), ex.getParams());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
			flash.error("Fund Code : ' " + nav.getRgProduct().getProductCode() + " ' " + Messages.get(e.getMessage()));
			renderArgs.put("confirming", true);
			render("RegistryNav/detail.html", nav, mode, isNewRec, status);
		}
	}

	@Check("transaction.registryNav")
	public static void back(RgNavId id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (isNewRec == true) {
			mode = UIConstants.DISPLAY_MODE_ENTRY;
		} else {
			mode = UIConstants.DISPLAY_MODE_EDIT;
		}
		RgNav nav = serializerService.deserialize(session.getId(), id, RgNav.class);
		log.debug("checked value = " + nav.isCheckBidOffer());
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		String navDate = dateFormat.format(nav.getId().getNavDate());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_NAV));
		render("RegistryNav/detail.html", nav, mode, isNewRec, status, navDate);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			RgNav nav = json.readValue(maintenanceLog.getNewData(), RgNav.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("RegistryNav/approval.html", nav, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			taService.approveNav(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			taService.approveNav(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}

	}

	// public static RgNavId toObjectId(String data) {
	// RgNavId id = new RgNavId();
	// try{
	// String[] arr = data.split("_");
	// String productcode = arr[0];
	// Date navDate = parseYYYYMMDD(arr[1]);
	//
	// id = new RgNavId(productcode, navDate);
	// }catch(Exception e) { logger.error(e.getMessage(), e); }
	// return id;
	// }
}
