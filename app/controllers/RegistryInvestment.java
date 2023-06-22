package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import play.i18n.Messages;
import play.mvc.Before;
import vo.CustomerSearchParameters;
import vo.InvestmentAccountSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnBranch;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class RegistryInvestment extends Registry {
	private static Logger log = Logger.getLogger(RegistryInvestment.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		renderArgs.put("stringOperators", UIHelper.stringOperators());
	}

	@Before(only = { "edit", "view", "entry", "save", "confirming", "back", "approval", "confirm" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("statusType", UIHelper.yesNoOperators());
		renderArgs.put("taInvestorType", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._RG_INVESTOR_TYPE));
		renderArgs.put("costMethodType", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._COST_METHOD));
		renderArgs.put("sendOption", UIHelper.sendOptionOperators());
	}

	@Check("maintenance.registryInvestment.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		List<RgInvestmentaccount> invs = null;
		log.debug("[LIST] [BEFORE] Mode : >>>> " + mode + " Param : " + param);
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			log.debug("masuk : >>>> " + mode + " dunx : " + param);
			mode = UIConstants.DISPLAY_MODE_VIEW;
			String params[] = param.split(",");
			log.debug("[LIST] [AFTER] Mode : >>>> " + mode + " Param : " + params[0]);
			// group = "CUSTOMER";
			Investors.list(mode, params[0]);
		} else {
			InvestmentAccountSearchParameters params = new InvestmentAccountSearchParameters();
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_VIEW));
			}
			RgProduct rgProduct = new RgProduct();
			// render("RegistryInvestment/listSearch.html", invs, mode, param);
			log.debug("Param = " + param);
			render("RegistryInvestment/listPaging.html", invs, mode, param, params, rgProduct);
		}
	}

	public static void investmentPaging(Paging page, InvestmentAccountSearchParameters param) {
		log.debug("investmentPaging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("c.domain", Paging.EQUAL, LookupConstants.DOMAIN_INVESTOR);
			page.addParams("inv.accountNumber", param.InvestmentAccountNoOperator, UIHelper.withOperator(param.InvestmentAccountSearchNo, param.InvestmentAccountNoOperator));
			page.addParams("inv.name", param.InvestmentAccountNameOperator, UIHelper.withOperator(param.InvestmentAccountSearchName, param.InvestmentAccountNameOperator));
			page.addParams("inv.rgProduct.productCode", page.EQUAL, param.InvestmentAccountFundCode);
			page.addParams("inv.cifWaperd", param.InvestmentAccountCifAperdOperator, UIHelper.withOperator(param.InvestmentAccountSearchCifAperd, param.InvestmentAccountCifAperdOperator));
			page.addParams(Helper.searchAll("(inv.accountNumber||inv.name||inv.rgProduct.productCode||inv.cifWaperd||inv.customer.customerNo||inv.recordStatus||inv.active||inv.thirdPartyBySaCode.thirdPartyCode)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = taService.pagingInvestment(page);
		}

		renderJSON(page);
	}

	public static void search(InvestmentAccountSearchParameters params) {
		log.debug("search. params: " + params);

		List<RgInvestmentaccount> invs = taService.searchInvestmentAccount(UIHelper.withOperator(params.InvestmentAccountSearchNo, params.InvestmentAccountNoOperator), UIHelper.withOperator(params.InvestmentAccountSearchName, params.InvestmentAccountNameOperator));
		render("RegistryInvestment/list.html", invs);
	}

	@Check("maintenance.registryInvestment.edit")
	public static void edit(String id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		RgInvestmentaccount inv = taService.loadInvestmentForSave(id);
		// inv.getRgProduct().setBankAccount(null);
		inv.getRgProduct().setBankAccounts(null);
//		try {
//			String test = json.writeValueAsString(inv);
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			log.error(e.getMessage(), e);
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			log.error(e.getMessage(), e);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			log.error(e.getMessage(), e);
//		}
		Long customerKey = inv.getCustomer().getCustomerKey();
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(customerKey);
		inv.putToList();
		inv.setOpeningSABranch(generalService.getThirdParty(inv.getOpeningSABranchKey()));
		String status = inv.getRecordStatus().trim();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
		render("RegistryInvestment/detail.html", inv, mode, param, addressType, status);
	}

	@Check("maintenance.registryInvestment.view")
	public static void view(String id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		RgInvestmentaccount inv = taService.loadInvestmentForSave(id);
		inv.getCustomer().setContacts(null);
		Long customerKey = inv.getCustomer().getCustomerKey();
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(customerKey);
		inv.putToList();
		// inv.setOpeningSABranch(generalService.getThirdPartyReference(inv.getOpeningSABranchKey()));
		inv.setOpeningSABranch(generalService.getThirdParty(inv.getOpeningSABranchKey()));
		if (param != null) {
			if (param.equals("register-invt-acct")) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			} else if (param.equals("view")) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_VIEW));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
			}
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_VIEW));
		}
		render("RegistryInvestment/detail.html", inv, mode, param, addressType);
	}

	@Check("maintenance.registryInvestment.register")
	public static void entry(long customerKey, String param) {
		log.debug("entry. customerKey: " + customerKey + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		RgInvestmentaccount inv = new RgInvestmentaccount();
		// show for customer properties
		CfMaster customer = customerService.getCustomer(customerKey);
		inv.setCustomer(customer);
		inv.setName(customer.getCustomerName());
		inv.setLookupByInvestorType(new GnLookup(LookupConstants.RG_INVESTOR_TYPE_INVESTOR));
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(customerKey);
		// =============================
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
		render("RegistryInvestment/detail.html", inv, mode, param, customer, addressType);
	}

	@Check("maintenance.registryInvestment.save")
	public static void save(RgInvestmentaccount inv, String mode, String param, String status) {
		log.debug("save. inv: " + inv + " mode: " + mode + " param: " + param + " status: " + status);

		if (inv == null)
			listInvestor(UIConstants.DISPLAY_MODE_VIEW, "register-invt-acct");
		log.debug("PARAM = " + param);
		validation.required("Fund Code is", inv.getRgProduct().getProductCode());
		validation.required("Name is", inv.getName());
		validation.required("SA Code is", inv.getThirdPartyBySaCode().getThirdPartyKey());
		validation.required("Account Type is", inv.getLookupByInvestorType().getLookupId());
		validation.required("CIF APERD is", inv.getCifWaperd());
		validation.required("Account No is", inv.getBankAccount().getAccountNo());
		log.debug("currency code product = " + inv.getRgProduct().getCurrency().getCurrencyCode() + "/");
		log.debug("currency code bank acc = " + inv.getBankAccount().getCurrency().getCurrencyCode() + "/");
		if (!inv.getRgProduct().getCurrency().getCurrencyCode().equals(inv.getBankAccount().getCurrency().getCurrencyCode())) {
			validation.addError("", "Currency Bank Account for Fund should be same " + inv.getRgProduct().getCurrency().getCurrencyCode());
		}

		try {
			taService.validateSellingAgentCifWaperd(inv);
		} catch (MedallionException e) {
			validation.addError("", e.getMessage());
		}

		if (!inv.getAccountNumber().isEmpty() && !inv.isActive() && status.equals(LookupConstants.__RECORD_STATUS_APPROVED)) {
			RgPortfolio portfolio = taService.loadLastPortfolioByAccountNumber(inv.getAccountNumber());
			BigDecimal outstandingUnit = taService.loadOutstandingWithoutGoodFundDate(inv.getRgProduct().getProductCode(), inv.getAccountNumber());
			BigDecimal availableUnit = new BigDecimal(0);
			if (portfolio != null)
				availableUnit = portfolio.getUnit().subtract(outstandingUnit);

			if (availableUnit.compareTo(BigDecimal.ZERO) > 0) {
				validation.addError("", "This account can not be inactive cause still have available unit");
			}
		}
		// }
		// logger.debug("inv "+inv.getCustomer().getContacts().size());

		if (validation.hasErrors()) {
			CfMaster customer = customerService.getCustomer(inv.getCustomer().getCustomerKey());
			List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(inv.getCustomer().getCustomerKey());
			inv.setCustomer(customer);
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
			}
			render("RegistryInvestment/detail.html", inv, mode, addressType, status);
		} else {
			String accountnumber = inv.getAccountNumber();
			Long customerKey = inv.getCustomer().getCustomerKey();
			// logger.debug("Opening SA Branch >>>> "+
			// inv.getOpeningSABranchKey());
			// inv.setOpeningSABranch(generalService.getThirdPartyReference(inv.getOpeningSABranchKey()));
			// inv.setOpeningSABranch(generalService.getThirdParty(inv.getOpeningSABranchKey()));
			serializerService.serialize(session.getId(), accountnumber, inv);
			confirming(accountnumber, mode, param, customerKey, status);
		}
	}

	@Check("maintenance.registryInvestment.save")
	public static void confirming(String id, String mode, String param, Long customerKey, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " param: " + param + " customerKey: " + customerKey + " status: " + status);

		renderArgs.put("confirming", true);
		RgInvestmentaccount inv = serializerService.deserialize(session.getId(), id, RgInvestmentaccount.class);
		// show for customer properties
		CfMaster customer = customerService.getCustomer(customerKey);
		inv.setCustomer(customer);
		inv.setOpeningSABranch(generalService.getThirdParty(inv.getOpeningSABranchKey()));
		// inv.setOpeningSABranch(generalService.getThirdPartyReference(inv.getOpeningSABranchKey()));
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(customerKey);
		// =============================
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
		}
		render("RegistryInvestment/detail.html", inv, mode, param, addressType, customerKey, status);
	}

	@Check("maintenance.registryInvestment.save")
	public static void confirm(RgInvestmentaccount inv, String mode, String param, String group, String status) {
		log.debug("confirm. inv: " + inv + " mode: " + mode + " param: " + param + " group: " + group + " status: " + status);

		try {
			if (inv == null)
				listInvestor(UIConstants.DISPLAY_MODE_VIEW, "register-invt-acct");

			// if (isNewRec == true ) {
			// List<RgInvestmentaccount> invs = taService.listInvestment();
			// for(RgInvestmentaccount invInTable : invs) {
			// if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
			// String accountNoInTable = invInTable.getAccountNumber();
			// Long custKeyInTable = invInTable.getCustomer().getCustomerKey();
			//
			// logger.debug("accountNoInTable >>> "+ accountNoInTable);
			// logger.debug("custKeyInTable >>> "+ custKeyInTable);
			// String accountNo = inv.getAccountNumber();
			// Long custKey = inv.getCustomer().getCustomerKey();
			// logger.debug("accountNo >>> "+ accountNo);
			// logger.debug("custKey >>> "+ custKey);
			// renderArgs.put("confirming", true);
			// if ((accountNoInTable.equals(accountNo))) {
			// flash.error("Investment Account No :  ' "
			// +inv.getAccountNumber()+ " ' " +
			// ExceptionConstants.DATA_DUPLICATE);
			// //boolean confirming = true;
			// render("RegistryInvestment/detail.html", inv, mode, isNewRec);
			// }
			// }
			// }
			// }

			/*
			 * inv.putToSet(); Helper.showProperties(inv);
			 */
			/*
			 * if ((inv.getRgProduct().getDivInvestorOpt()==null) ||
			 * (inv.getRgProduct().getDivInvestorOpt()==false)) {
			 * inv.setDivIopByCashPct(null);
			 * inv.setDivIopByReinvestmentPct(null);
			 * inv.setDivIopByRedeemPct(null); }
			 */

			String menuCode = null;
			if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
				menuCode = MenuConstants.RG_ACCOUNT_ENTRY;
			} else {
				menuCode = MenuConstants.RG_ACCOUNT_EDIT;
				param = UIConstants.DISPLAY_MODE_EDIT;
			}

			inv.setBankAccounts(new HashSet<BnAccount>());

			inv.getBankAccounts().add(inv.getBankAccount());

			inv.setBranch(new GnBranch());
			inv.getBranch().setBranchKey(Long.parseLong(generalService.getValueParam(SystemParamConstants.PARAM_BANK_BRANCH)));
			inv.setLookupByCostMethod(new GnLookup(generalService.getValueParam(SystemParamConstants.PARAM_BANK_COST)));
			inv.setJointDate(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID));
			if (inv.isActive() == false) {
				inv.setInActiveDate(inv.getJointDate());
			}
			log.debug("PARAM CONFIRM = " + param);

			inv = taService.saveInvestment(menuCode, inv, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				status = inv.getRecordStatus().trim();
				renderArgs.put("confirming", true);
				render("RegistryInvestment/detail.html", inv, mode, param, status);
			} else {
				list(mode, param);
			}

		} catch (MedallionException ex) {
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY))
				flash.error("This account is " + Messages.get(ex.getMessage()));
			else
				flash.error("Account No : ' " + inv.getAccountNumber() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);
			CfMaster customer = customerService.getCustomer(inv.getCustomer().getCustomerKey());
			List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(inv.getCustomer().getCustomerKey());
			inv.setCustomer(customer);
			param = null;
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				param = UIConstants.DISPLAY_MODE_EDIT;
			}
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
			}
			render("RegistryInvestment/detail.html", inv, mode, param, addressType, status);
		}
	}

	@Check("maintenance.registryInvestment.save")
	public static void back(String id, String mode, String param, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " param: " + param + " status: " + status);

		RgInvestmentaccount inv = serializerService.deserialize(session.getId(), id, RgInvestmentaccount.class);
		// show for customer properties
		CfMaster customer = customerService.getCustomer(inv.getCustomer().getCustomerKey());
		inv.setCustomer(customer);
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(inv.getCustomer().getCustomerKey());
		// =============================
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_EDIT));
		}
		render("RegistryInvestment/detail.html", inv, mode, param, addressType, status);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			RgInvestmentaccount inv = json.readValue(maintenanceLog.getNewData(), RgInvestmentaccount.class);
			// show for customer properties
			CfMaster customer = customerService.getCustomer(inv.getCustomer().getCustomerKey());
			inv.setCustomer(customer);
			BnAccount bankAccount = bankAccountService.getBankAccount(inv.getBankAccount().getBankAccountKey());
			inv.getBankAccount().setBankCode(bankAccount.getBankCode());
			inv.getBankAccount().setCurrency(bankAccount.getCurrency());

			GnThirdParty sellingAgent = generalService.getThirdParty(inv.getThirdPartyBySaCode().getThirdPartyKey());
			inv.setThirdPartyBySaCode(sellingAgent);
			List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(inv.getCustomer().getCustomerKey());
			// =============================
			// show list bankAccount on grid
			/*
			 * Set<BnAccount> bankAccounts = inv.getBankAccounts();
			 * inv.setListBankAccounts(new ArrayList<BnAccount>(bankAccounts));
			 */
			// =============================
			// inv.setOpeningSABranch(generalService.getThirdPartyReference(inv.getOpeningSABranchKey()));
			// inv.setOpeningSABranch(generalService.getThirdParty(inv.getOpeningSABranchKey()));
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("RegistryInvestment/approval.html", inv, mode, taskId, operation, maintenanceLogKey, addressType, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			taService.approveInvestmentAccount(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			taService.approveInvestmentAccount(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void inActiveDateDefault() {
		log.debug("inActiveDateDefault. ");

		GnApplicationDate applicationDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		SimpleDateFormat dateFormat = new SimpleDateFormat(UIConstants.DATE_FORMAT);
		renderText(dateFormat.format(applicationDate.getCurrentBusinessDate()));
	}

	public static void listInvestor(String mode, String param) {
		log.debug("listInvestor. mode: " + mode + " param: " + param);

		// param = "register-cust-acct";
		String breadcrumb = "Inquiry";
		CustomerSearchParameters params = new CustomerSearchParameters();
		//String module = null;
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_INVESTOR_VIEW));
		}
		if (param != null) {
			if (param.contains("cust-acct")) {
				//module = "Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			}
			if (param.contains("bank-acct")) {
				//module = "Bank Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			}
			if (param.contains("invt-acct")) {
				//module = "Investment Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			}
		}
		renderTemplate("Investors/list.html", UIConstants.DISPLAY_MODE_VIEW, "register-invt-acct", "Investment Account", breadcrumb, params);
	}
}
