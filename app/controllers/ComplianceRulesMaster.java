package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CpComplianceRule;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class ComplianceRulesMaster extends MedallionController {
	private static Logger log = Logger.getLogger(ComplianceRulesMaster.class);

	public static final List<SelectItem> cpComparisonValue = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CP_COMPARISON_VALUE);

	@Before(only = { "entry", "edit", "save", "confirming", "confirm", "back", "view", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> listOperator = UIHelper.signOperators();
		renderArgs.put("listOperator", listOperator);

		List<SelectItem> yesNo = UIHelper.yesNoOperators();
		renderArgs.put("yesNo", yesNo);

		renderArgs.put("cpComparisonValue", cpComparisonValue);

		String lookupInvestmentModelId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_INVESTMENT_MODEL);
		String lookupPositiveSecuritiesId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_POSITIVE_SECURITIES);
		String lookupForeignPortfolioPerIssueId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_FOREIGN_PORTFOLIO_PER_ISSUER);
		String lookupLocalEquityPortfolioPerIssuerId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_LOCAL_EQUITY_PORTFOLIO_PER_ISSUER);
		String lookupPositiveSecuritiesSectorId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_POSITIVE_SECURITIES_SECTOR);
		String lookupUpperLowerSecuritiesPriceId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE);

		String RULE_TYPE_INVESTMENT_MODEL = generalService.getLookup(lookupInvestmentModelId).getLookupCode();
		String RULE_TYPE_POSITIVE_SECURITIES = generalService.getLookup(lookupPositiveSecuritiesId).getLookupCode();
		String RULE_TYPE_FOREIGN_PORTFOLIO_PER_ISSUER = generalService.getLookup(lookupForeignPortfolioPerIssueId).getLookupCode();
		String RULE_TYPE_LOCAL_EQUITY_PORTFOLIO_PER_ISSUER = generalService.getLookup(lookupLocalEquityPortfolioPerIssuerId).getLookupCode();
		String RULE_TYPE_POSITIVE_SECURITIES_SECTOR = generalService.getLookup(lookupPositiveSecuritiesSectorId).getLookupCode();
		String RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE = generalService.getLookup(lookupUpperLowerSecuritiesPriceId).getLookupCode();

		renderArgs.put("ruleType_investmentModel", RULE_TYPE_INVESTMENT_MODEL);
		renderArgs.put("ruleType_positiveSecurities", RULE_TYPE_POSITIVE_SECURITIES);
		renderArgs.put("ruleType_foreignPortfolioPerIssuer", RULE_TYPE_FOREIGN_PORTFOLIO_PER_ISSUER);
		renderArgs.put("ruleType_localEquityPortfolioPerIssuer", RULE_TYPE_LOCAL_EQUITY_PORTFOLIO_PER_ISSUER);
		renderArgs.put("ruleType_positiveSecuritiesSector", RULE_TYPE_POSITIVE_SECURITIES_SECTOR);
		renderArgs.put("ruleType_upperLowerSecuritiesPrice", RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE);

		renderArgs.put("cpNAV", LookupConstants.CP_COMPARISON_VALUE_NAV);
		renderArgs.put("cpPortfolio", LookupConstants.CP_COMPARISON_VALUE_PORTFOLIO);
		renderArgs.put("cpAsset", LookupConstants.CP_COMPARISON_VALUE_ASSET);
		renderArgs.put("cpPaidOfCapital", LookupConstants.CP_COMPARISON_VALUE_PAID_OF_CAPITAL);
		renderArgs.put("cpMarketCapitalization", LookupConstants.CP_COMPARISON_VALUE_MARKET_CAPITALIZATION);

	}

	@Check("administration.complianceRuleMaster")
	public static void list() {
		log.debug("list. ");

		List<CpComplianceRule> cpComplianceRules = generalService.listComplianceRules();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
		render("ComplianceRulesMaster/list.html", cpComplianceRules);
	}

	@Check("administration.complianceRuleMaster")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CpComplianceRule cpComplianceRule = new CpComplianceRule();
		cpComplianceRule.setActive(false);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
		render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
	}

	@Check("administration.complianceRuleMaster")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CpComplianceRule cpComplianceRule = generalService.getComplianceRule(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
		render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
	}

	@Check("administration.complianceRuleMaster")
	public static void save(String mode, CpComplianceRule cpComplianceRule) {
		log.debug("save. mode: " + mode + " cpComplianceRule: " + cpComplianceRule);

		if (cpComplianceRule != null) {
			validation.required("Description is", cpComplianceRule.getDescription());
			// validation.range("Value is", cpComplianceRule.getValue(), 1,
			// 100);
			validation.required("Rule Type is", cpComplianceRule.getRuleType().getLookupCode());
			if (cpComplianceRule.getOperator() != null) {
				validation.required("Operator is", cpComplianceRule.getOperator());
				validation.required("Value is", cpComplianceRule.getValue());
			}

			String lookupUpperLowerSecuritiesPriceId = generalService.getValueParam(SystemParamConstants.PARAM_RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE);
			String RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE = generalService.getLookup(lookupUpperLowerSecuritiesPriceId).getLookupCode();
			if (cpComplianceRule.getComparisonValue() != null) {
				// logic ini mengikuti screen. Jika screen ada perubahan, logic
				// ini jg hrs mengikuti
				if (!Helper.isNullOrEmpty(RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE) && !cpComplianceRule.getRuleType().getLookupCode().equals(RULE_TYPE_UPPER_LOWER_SECURITIES_PRICE)) {
					validation.required("Comparison Value is", cpComplianceRule.getComparisonValue().getLookupId());
				}
			}
			validation.required("Rule code is", cpComplianceRule.getRuleCode());

			if (!validation.hasErrors() && mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				CpComplianceRule cpruledb = generalService.getComplianceRule(cpComplianceRule.getRuleCode());
				if (cpruledb != null)
					validation.addError("global", Messages.get("cp.rule.rule_code_exist", cpComplianceRule.getRuleCode()));
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
				render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
			} else {
				// String ruleCode = cpComplianceRule.getRuleCode();
				Long id = cpComplianceRule.getRuleId();
				// logger.debug("serialize id"+ruleCode+" session "+session.getId());
				serializerService.serialize(session.getId(), id, cpComplianceRule);
				confirming(id, mode);
			}
		} else {
			// flash.error("argument.null", cpComplianceRule);
			entry();
		}
	}

	@Check("administration.complianceRuleMaster")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		log.debug("deserialize id" + id + " session " + session.getId());
		CpComplianceRule cpComplianceRule = serializerService.deserialize(session.getId(), id, CpComplianceRule.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
		render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
	}

	@Check("administration.complianceRuleMaster")
	public static void confirm(CpComplianceRule cpComplianceRule, String mode) {
		log.debug("confirm. cpComplianceRule: " + cpComplianceRule + " mode: " + mode);

		try {
			if (cpComplianceRule == null)
				back(null, mode);
			generalService.saveCpComplianceRule(MenuConstants.CP_COMPLIANCE_RULE, session.get(UIConstants.SESSION_USER_KEY), cpComplianceRule, session.get(UIConstants.SESSION_USERNAME));
			list();
		} catch (MedallionException ex) {
			log.error(ex.getMessage(), ex);
			renderArgs.put("confirming", true);

			flash.error("Rule Master Code : ' " + cpComplianceRule.getRuleCode() + " ' " + Messages.get(ex.getMessage()));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
			render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
		}
	}

	@Check("administration.complianceRuleMaster")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		log.debug("deserialize id" + id + " session " + session.getId());
		CpComplianceRule cpComplianceRule = serializerService.deserialize(session.getId(), id, CpComplianceRule.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
		render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
	}

	@Check("administration.complianceRuleMaster")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpComplianceRule cpComplianceRule = generalService.getComplianceRule(id);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_RULE));
		render("ComplianceRulesMaster/entry.html", cpComplianceRule, mode);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CpComplianceRule cpComplianceRule = json.readValue(maintenanceLog.getNewData(), CpComplianceRule.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ComplianceRulesMaster/approval.html", cpComplianceRule, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveRuleMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveRuleMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void createDropdownListByComparisonValueCriteria(String[] comparisonValue) {
		log.debug("createDropdownListByComparisonValueCriteria. comparisonValue: " + comparisonValue);

		List<SelectItem> dropComparisonValue = new ArrayList<SelectItem>();

		if ((comparisonValue != null) && (comparisonValue.length > 0)) {
			dropComparisonValue.add(new SelectItem("", ""));
			for (String strComparisonValue : comparisonValue) {
				for (SelectItem availableSelectItem : cpComparisonValue) {
					if (availableSelectItem.value.equals(strComparisonValue)) {
						dropComparisonValue.add(availableSelectItem);
					}
				}
			}
		}

		renderJSON(dropComparisonValue);
	}
}
