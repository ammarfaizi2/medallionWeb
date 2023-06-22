package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.vo.SelectItem;

//@Check("administration.securityType")
public class SecurityTypes extends MedallionController {
	private static Logger log = Logger.getLogger(SecurityTypes.class);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> prices = new ArrayList<SelectItem>();
		prices.add(new SelectItem(0.01, "Percent"));
		prices.add(new SelectItem(1, "Amount"));
		renderArgs.put("prices", prices);

		List<SelectItem> holdingTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._HOLDING_TYPE);
		renderArgs.put("holdingTypes", holdingTypes);

		List<SelectItem> cbestMessageTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CBEST_MESSAGE_TYPE);
		renderArgs.put("cbestMessageTypes", cbestMessageTypes);

		List<SelectItem> valuationMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._VALUATION_METHOD);
		for (SelectItem selectItem : valuationMethod) {
			if (selectItem.value.equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
				renderArgs.put("valueForValuationFairValue", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
				renderArgs.put("valueForValuationAmortization", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.VALUATION_METHOD_NOT_AVAILABLE)) {
				renderArgs.put("valueForValuationNA", selectItem.text);
			}
		}
		renderArgs.put("valuationMethod", valuationMethod);

		List<SelectItem> amortizationMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._AMORTIZATION_METHOD);
		for (SelectItem selectItem : amortizationMethod) {
			if (selectItem.value.equals(LookupConstants.AMORTIZATION_METHOD_SL)) {
				renderArgs.put("valueForAmortizationSL", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.AMORTIZATION_METHOD_EIR)) {
				renderArgs.put("valueForAmortizationEIR", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.AMORTIZATION_METHOD_NPV)) {
				renderArgs.put("valueForAmortizationNPV", selectItem.text);
			}
		}
		renderArgs.put("amortizationMethod", amortizationMethod);

		GnLookup registrarNA = generalService.getLookup(LookupConstants._REGISTRAR_NA);
		renderArgs.put("registrarNA", registrarNA);

		List<SelectItem> marketPrice = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._MARKET_PRICE);
		for (SelectItem selectItem : marketPrice) {
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_CLOSING)) {
				renderArgs.put("valueForMarketPriceClose", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_LOW)) {
				renderArgs.put("valueForMarketPriceLow", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_HIGH)) {
				renderArgs.put("valueForMarketPriceHigh", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_NA)) {
				renderArgs.put("valueForMarketPriceNA", selectItem.text);
			}
		}
		renderArgs.put("marketPrice", marketPrice);

		renderArgs.put("valuationMethodAmortized", LookupConstants.VALUATION_METHOD_AMORTIZED);
		renderArgs.put("valuationMethodFairValue", LookupConstants.VALUATION_METHOD_FAIR_VALUE);
		renderArgs.put("valuationMethodNA", LookupConstants.VALUATION_METHOD_NOT_AVAILABLE);
		renderArgs.put("marketPriceClose", LookupConstants.MARKET_PRICE_CLOSING);
		renderArgs.put("marketPriceHigh", LookupConstants.MARKET_PRICE_HIGH);
		renderArgs.put("marketPriceLow", LookupConstants.MARKET_PRICE_LOW);
		renderArgs.put("marketPriceNA", LookupConstants.MARKET_PRICE_NA);
		renderArgs.put("amortizationSL", LookupConstants.AMORTIZATION_METHOD_SL);
		renderArgs.put("amortizationEIR", LookupConstants.AMORTIZATION_METHOD_EIR);
		renderArgs.put("amortizationNPV", LookupConstants.AMORTIZATION_METHOD_NPV);
	}

	@Check("administration.securityType")
	public static void list() {
		log.debug("list. ");

		List<ScTypeMaster> securityTypes = securityService.listSecurityTypes();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
		render(securityTypes);
	}

	@Check("administration.securityType")
	public static void view(String id, boolean isNewRec) {
		log.debug("view. id: " + id + " isNewRec: " + isNewRec);

		// List<SelectItem> prices = new ArrayList<SelectItem>();
		// prices.add(new SelectItem(0.01, "Percent"));
		// prices.add(new SelectItem(1, "Amount"));
		// renderArgs.put("prices", prices);
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			ScTypeMaster securityType = securityService.getSecurityType(id);

			securityType.setCheckTrade(securityType.getValuationMethodTrade() != null ? true : false);
			securityType.setCheckAfs(securityType.getValuationMethodAFS() != null ? true : false);
			securityType.setCheckHtm(securityType.getValuationMethodHTM() != null ? true : false);

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
			render("SecurityTypes/detail.html", securityType, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.securityType")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		ScTypeMaster securityType = new ScTypeMaster();
		securityType.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		boolean isNewRec = true;
		// securityType.setIsActive(true);
		// securityType.setCheckTrade(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
		render("SecurityTypes/detail.html", securityType, mode, isNewRec);
	}

	@Check("administration.securityType")
	public static void edit(String id, boolean isNewRec) {
		log.debug("edit. id: " + id + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		ScTypeMaster securityType = securityService.getSecurityType(id);

		securityType.setCheckTrade(securityType.getValuationMethodTrade() != null ? true : false);
		securityType.setCheckAfs(securityType.getValuationMethodAFS() != null ? true : false);
		securityType.setCheckHtm(securityType.getValuationMethodHTM() != null ? true : false);

		String status = securityType.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
		render("SecurityTypes/detail.html", securityType, mode, isNewRec, status);
	}

	@Check("administration.securityType")
	public static void save(ScTypeMaster securityType, String mode, boolean isNewRec, String status) {
		log.debug("save. securityType: " + securityType + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		// Validate here
		if (securityType != null) {
			log.debug("security class >>" + securityType.getSecurityClass().getLookupCode());
			validation.required("Security Type is", securityType.getSecurityType());
			validation.required("Description is", securityType.getDescription());
			validation.required("Security Group is", securityType.getSecurityGroup().getLookupCode());
			validation.required("Security Class is", securityType.getSecurityClass().getLookupCode());
			if (securityType.getIsPrice() == true) {
				validation.required("Price As is", securityType.getPriceUnit());
			}
			/*
			 * if (!securityType.isCheckTrade() && !securityType.isCheckAfs() &&
			 * !securityType.isCheckHtm()){ securityType.setCheckTrade(null);
			 * validation.required("TRADE is", securityType.isCheckTrade()); }
			 */

			if ((securityType.isCheckTrade() != null) && (securityType.isCheckTrade())) {
				validation.required("Valuation Method For TRADE is", securityType.getValuationMethodTrade().getLookupId());
			}

			if (securityType.isCheckAfs()) {
				validation.required("Valuation Method For AFS is", securityType.getValuationMethodAFS().getLookupId());
			}

			if (securityType.isCheckHtm()) {
				validation.required("Valuation Method For HTM is", securityType.getValuationMethodHTM().getLookupId());
			}

			if (securityType.getValuationMethodTrade() != null) {
				if (securityType.getValuationMethodTrade().getLookupId().equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
					validation.required("Market Price For TRADE is", securityType.getMarketPriceTrade().getLookupId());
				}

				if (securityType.getValuationMethodTrade().getLookupId().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
					validation.required("Market Price For TRADE is", securityType.getMarketPriceTrade().getLookupId());
					validation.required("Amortization Method For TRADE is", securityType.getAmortizationMethodTrade().getLookupId());
				}
			}

			if (securityType.getValuationMethodAFS() != null) {
				if (securityType.getValuationMethodAFS().getLookupId().equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
					validation.required("Market Price For AFS is", securityType.getMarketPriceAFS().getLookupId());
				}

				if (securityType.getValuationMethodAFS().getLookupId().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
					validation.required("Market Price For AFS is", securityType.getMarketPriceAFS().getLookupId());
					validation.required("Amortization Method For AFS is", securityType.getAmortizationMethodAFS().getLookupId());
				}
			}

			if (securityType.getValuationMethodHTM() != null) {
				if (securityType.getValuationMethodHTM().getLookupId().equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
					validation.required("Market Price For HTM is", securityType.getMarketPriceHTM().getLookupId());
				}

				if (securityType.getValuationMethodHTM().getLookupId().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
					validation.required("Market Price For HTM is", securityType.getMarketPriceHTM().getLookupId());
					validation.required("Amortization Method For HTM is", securityType.getAmortizationMethodHTM().getLookupId());
				}
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
				render("SecurityTypes/detail.html", securityType, mode, isNewRec, status);
			} else {
				String id = securityType.getSecurityType();
				serializerService.serialize(session.getId(), id, securityType);
				confirming(id, mode, isNewRec, status);
			}
		} else {
			// flash.error("argument.null", securityType);
			entry();
		}
	}

	@Check("administration.securityType")
	public static void confirming(String id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		renderArgs.put("confirming", true);
		ScTypeMaster securityType = serializerService.deserialize(session.getId(), id, ScTypeMaster.class);
		securityType.setCheckTrade(securityType.getValuationMethodTrade() != null ? true : false);
		securityType.setCheckAfs(securityType.getValuationMethodAFS() != null ? true : false);
		securityType.setCheckHtm(securityType.getValuationMethodHTM() != null ? true : false);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
		render("SecurityTypes/detail.html", securityType, mode, isNewRec, status);
	}

	@Check("administration.securityType")
	public static void confirm(ScTypeMaster securityType, String mode, boolean isNewRec, String status) {
		log.debug("confirm. securityType: " + securityType + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		try {
			if (securityType == null)
				back(null, mode, isNewRec, status);
			// DUPLICATE SEC TYPE VALIDATION
			List<ScTypeMaster> securityTypes = securityService.listSecurityTypes();
			for (ScTypeMaster secTypeInTable : securityTypes) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					if (secTypeInTable.getSecurityType().equals(securityType.getSecurityType())) {
						throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
					}
				}
			}
			securityService.saveSecurityType(MenuConstants.SC_SECURITY_TYPE, securityType, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Security Type : '" + securityType.getSecurityType() + "' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
			render("SecurityTypes/detail.html", securityType, mode, confirming, status);
		}
	}

	@Check("administration.securityType")
	public static void back(String id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		ScTypeMaster securityType = serializerService.deserialize(session.getId(), id, ScTypeMaster.class);

		securityType.setCheckTrade(securityType.getValuationMethodTrade() != null ? true : false);
		securityType.setCheckAfs(securityType.getValuationMethodAFS() != null ? true : false);
		securityType.setCheckHtm(securityType.getValuationMethodHTM() != null ? true : false);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY_TYPE));
		render("SecurityTypes/detail.html", securityType, mode, isNewRec, status);
	}

	public static void approval(String taskId, String group, String keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			ScTypeMaster securityType = json.readValue(maintenanceLog.getNewData(), ScTypeMaster.class);

			securityType.setCheckTrade(securityType.getValuationMethodTrade() != null ? true : false);
			securityType.setCheckAfs(securityType.getValuationMethodAFS() != null ? true : false);
			securityType.setCheckHtm(securityType.getValuationMethodHTM() != null ? true : false);

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("SecurityTypes/approval.html", securityType, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveSecurityType(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			securityService.approveSecurityType(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
