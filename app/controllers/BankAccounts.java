package controllers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.GnLookupDetail;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.BankAccountSearchParameters;

@With(Secure.class)
public class BankAccounts extends MedallionController {
	private static Logger log = Logger.getLogger(BankAccounts.class);

	// refs #56 note 41
	public static final String DEFAULT_PAYMENT_TYPE_ID = generalService.getValueParam(SystemParamConstants.PARAM_PAYMENT_TYPE_TRANSFER);
	public static final String DEFAULT_ACCOUNT_TYPE = generalService.getValueParam(SystemParamConstants.PARAM_DEFAULT_ACCOUNT_BANK_ACCOUNT);

	private static final DecimalFormat df = new DecimalFormat("#.####");
	private static final int NUMBER_DECIMAL_SET = 4;

	private static final List<SelectItem> paymentType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PAYMENT_TYPE);

	public static void test() {
		String acctN0= "123213";
		
		//Map mapResult = generalService.accountInquiry(acctN0, session.get(UIConstants.SESSION_USERNAME));
		/*System.out.println("+++++++++++++++++++++++"+mapResult.get("accountStatus"));
		System.out.println("2222222222:"+mapResult.get("availableBalance"));
		System.out.println("+++++++++++++++++++++++"+mapResult.get("errorMessage"));*/
		
		CsTransaction transaction = accountService.getTransaction(555L);
		GnUser operator = new GnUser();
		webserviceBridgeService.fundsTransfer(transaction, operator);
	}
	
	@Before(only = { "list", "dedupe" })
	public static void setupList() {
		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	// @Before(unless={"list", "pagingBankAccount", "dedupe", "save",
	// "confirm"})
	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		/*
		 * List<SelectItem> interestBase =
		 * generalService.listLookupsForDropDownAsSelectItem
		 * (UIConstants.SIMIAN_BANK_ID, LookupConstants._INTEREST_BASE);
		 * renderArgs.put("interestBase", interestBase);
		 */

		List<SelectItem> interestBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCRUAL_BASE);
		renderArgs.put("interestBase", interestBase);

		List<SelectItem> yearBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._YEAR_BASE);
		renderArgs.put("yearBase", yearBase);

		List<SelectItem> accountType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCOUNT_TYPE);
		renderArgs.put("accountType", accountType);

		renderArgs.put("paymentType", paymentType);
		
		renderArgs.put("defaultbankcode", getValueDefaultBankCode());
		
		GnSystemParameter interFaceFlag = generalService.getSystemParameter(SystemParamConstants.INTERFACE_ENABLED);
		renderArgs.put("coreIntegrated", (interFaceFlag != null)? interFaceFlag.getValue() : "0");

		/*
		 * GnSystemParameter systemParameter =
		 * generalService.getSystemParameter(
		 * SystemParamConstants.PARAM_BANK_ORG); GnThirdParty bankThirdParty =
		 * generalService
		 * .getThirdParty(Long.parseLong(systemParameter.getValue()));
		 * renderArgs.put("defaultbankcode",
		 * bankThirdParty.getThirdPartyCode());
		 */

	}

	public static void dedupe() {
		String mode = "view";
		String param = "bank-acct";
		String breadcrumb = "Register";
		render("BankAccounts/list.html", mode, param, breadcrumb);
	}

	/*
	 * @Check("maintenance.bankAccount.list") public static void list(String
	 * mode, String param) { if (mode != null) { if
	 * (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT)); }
	 * else { flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_VIEW)); } }
	 * render(mode, param); }
	 */

	@Check("maintenance.bankAccount.list")
	public static void list(String mode, String param) {
		BankAccountSearchParameters params = new BankAccountSearchParameters();
		if (mode != null) {
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_VIEW));
			}
		}
		renderTemplate("BankAccounts/list.html", params, mode, param);
	}

	public static void search(BankAccountSearchParameters params) {
		log.debug("PARAM PERTAMA == " + UIHelper.withOperator(params.bankAccountSearchNo, params.bankAccountNoOperator));
		log.debug("PARAM KEDUA == " + UIHelper.withOperator(params.bankAccountSearchName, params.bankAccountNameOperator));
		List<BnAccount> bankAccounts = bankAccountService.searchBankAccount(UIHelper.withOperator(params.bankAccountSearchNo, params.bankAccountNoOperator), UIHelper.withOperator(params.bankAccountSearchName, params.bankAccountNameOperator));
		render("BankAccounts/grid.html", bankAccounts);
	}

	public static void pagingBankAccount(Paging page, BankAccountSearchParameters param) {
		if (param.isQuery()) {

			page.addParams("a.accountNo", param.bankAccountNoOperator, UIHelper.withOperator(param.bankAccountSearchNo, param.bankAccountNoOperator));
			page.addParams("a.name", param.bankAccountNameOperator, UIHelper.withOperator(param.bankAccountSearchName, param.bankAccountNameOperator));

			page.addParams("1", page.EQUAL, 1);

			page.addParams(Helper.searchAll("(a.accountNo||a.name||a.balance)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = bankAccountService.pagingBankAccount(page);
		}
		renderJSON(page);
	}

	@Check("maintenance.bankAccount.view")
	public static void view(Long id, String param) {
		log.debug("PARAM = " + param);
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		BnAccount bankAccount = bankAccountService.getBankAccount(id);
		if (param != null) {
			if (param.equals("register-bank-acct")) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			} else if (param.equals(UIConstants.DISPLAY_MODE_VIEW)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_VIEW));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
			}
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_VIEW));
		}

		BigDecimal paymentChargeOverwriteVal = bankAccount.getPaymentCharge();
		/*
		 * if(bankAccount.getPaymentType() != null){ GnLookupDetail
		 * lookupDetailResult =
		 * generalService.getLookupDetailByFieldNameAndLookup("BANK_CHARGES",
		 * bankAccount.getPaymentType().getLookupId());
		 * bankAccount.setPaymentCharge(new
		 * BigDecimal(lookupDetailResult.getDetailValue())); }else{
		 * bankAccount.setPaymentCharge(BigDecimal.ZERO); }
		 */
		render("BankAccounts/detail.html", bankAccount, mode, param, paymentChargeOverwriteVal);
	}

	@Check("maintenance.bankAccount.register")
	public static void entry(long customerKey, String param) {
		log.debug("param = " + param);
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		BnAccount bankAccount = new BnAccount();
		CfMaster customer = customerService.getCustomer(customerKey);

		List<BnAccount> bankAccountsWithInvoiceAccount = bankAccountService.getListBankAccountByCustomerAndActivatedInvoiceAccount(customer.getCustomerKey());
		// form display purpose
		if (bankAccountsWithInvoiceAccount.size() == 0) {
			bankAccount.setInvoiceAccount(Boolean.TRUE);
		}

		bankAccount.setCustomer(customer);
		bankAccount.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		bankAccount.setName(bankAccount.getCustomer().getCustomerName());

		bankAccount.setInterestRate(new BigDecimal(0));
		bankAccount.setInterestRate(new BigDecimal(df.format(bankAccount.getInterestRate().setScale(NUMBER_DECIMAL_SET))));

		bankAccount.setInterestBase(new String(LookupConstants.ACCRUAL_BASE_ACTUAL));
		bankAccount.setYearBase(new String(LookupConstants.YEAR_BASE_ACT));

		GnSystemParameter systemParameter = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_ORG);
		GnThirdParty bankThirdParty = generalService.getThirdParty(Long.parseLong(systemParameter.getValue()));
		bankAccount.setBankCode(bankThirdParty);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
		render("BankAccounts/detail.html", mode, param, bankAccount, customer);
	}

	@Check("maintenance.bankAccount.edit")
	public static void edit(Long id, String param) {
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		BnAccount bankAccount = bankAccountService.getBankAccount(id);
		String status = bankAccount.getRecordStatus();
		log.debug("from edit status = --" + status + "--");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
		BigDecimal paymentChargeOverwriteVal = bankAccount.getPaymentCharge();

		/*
		 * if(bankAccount.getPaymentType() != null){ GnLookupDetail
		 * lookupDetailResult =
		 * generalService.getLookupDetailByFieldNameAndLookup("BANK_CHARGES",
		 * bankAccount.getPaymentType().getLookupId());
		 * 
		 * bankAccount.setPaymentCharge(new
		 * BigDecimal(lookupDetailResult.getDetailValue()));
		 * logger.debug("LOKUP VALUENYA "+lookupDetailResult.getDetailValue());
		 * logger
		 * .debug("LOKUPIDNYA "+bankAccount.getPaymentType().getLookupId());
		 * }else{ bankAccount.setPaymentCharge(BigDecimal.ZERO); }
		 */
		render("BankAccounts/detail.html", bankAccount, mode, param, status, paymentChargeOverwriteVal);
	}

	@Check("maintenance.bankAccount.save")
	public static void save(String mode, BnAccount bankAccount, Boolean overwriteBankChargesEl, BigDecimal paymentChargeOverwriteEl, String status, String param) {
		if (bankAccount != null) {
			Boolean overwriteBankChargesVal = new Boolean(false);
			BigDecimal paymentChargeOverwriteVal = null;
			bankAccount.setPaymentType(null);// set null

			validation.required("Account No is", bankAccount.getAccountNo());
			validation.required("Account Holder Name is", bankAccount.getName());
			validation.required("Bank Code is", bankAccount.getBankCode().getThirdPartyKey());
			// validation.required("Branch is", bankAccount.getBranch());
			validation.required("Currency is", bankAccount.getCurrency().getCurrencyCode());
			// validation.required("Payment Type is",
			// bankAccount.getPaymentType().getLookupId());
			// validation.required("BIC Code is", bankAccount.getBicCode());
			validation.required("Interest Rate is", bankAccount.getInterestRate());
			validation.required("Accrual Type is", bankAccount.getInterestBase());
			validation.required("Year Base is", bankAccount.getYearBase());

			/*
			 * if (bankAccount.getCustomer().getCustomerKey() != null) { if
			 * ((mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY)) &&
			 * (!Helper.isNullOrEmpty(bankAccount.getAccountNo())) &&
			 * (bankAccount.getBankCode() != null)) { long
			 * checkExistingCustomerAndAccountOnSameBank = bankAccountService
			 * .countByCustomerAndAccountAndBankCode(bankAccount
			 * .getCustomer().getCustomerKey(), bankAccount.getAccountNo(),
			 * bankAccount .getBankCode()); if
			 * (checkExistingCustomerAndAccountOnSameBank > 0) {
			 * validation.addError("",
			 * "Customer's bank account should be unique"); } } }
			 */

			if (bankAccount.getCustomer().getCustomerKey() != null) {
				long checkExistingCustomerAndAccountOnSameBank = 0;
				if ((!Helper.isNullOrEmpty(bankAccount.getAccountNo())) && (bankAccount.getBankCode() != null)) {
					checkExistingCustomerAndAccountOnSameBank = bankAccountService.countByCustomerAndAccountAndBankCode(bankAccount.getCustomer().getCustomerKey(), bankAccount.getAccountNo(), bankAccount.getBankCode());
				}

				if ((mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_ENTRY))) {
					if (checkExistingCustomerAndAccountOnSameBank > 0) {
						validation.addError("", ExceptionConstants.BANK_ACCOUNT_ACCOUNT_NO_NOT_IN_SAME_BANK_CODE_AND_CUSTOMER);
					}
				} else {
					BnAccount oldBankAccount = bankAccountService.getBankAccount(bankAccount.getBankAccountKey());
					if ((!oldBankAccount.getAccountNo().equals(bankAccount.getAccountNo())) && (oldBankAccount.getBankCode().getThirdPartyKey() != bankAccount.getBankCode().getThirdPartyKey())) {
						if (checkExistingCustomerAndAccountOnSameBank > 0) {
							validation.addError("", ExceptionConstants.BANK_ACCOUNT_ACCOUNT_NO_NOT_IN_SAME_BANK_CODE_AND_CUSTOMER);
						}
					}
				}

			}

			// now, compare currency from cs_account, please change currency for
			// compare to cf_master, when it ready
			if ((bankAccount.getInvoiceAccount() != null) && (bankAccount.getInvoiceAccount().equals(Boolean.TRUE)) && (!Helper.isNullOrEmpty(bankAccount.getAccountNo())) && (!Helper.isNullOrEmpty(bankAccount.getCurrency().getCurrencyCode()))) {
				log.debug("CUSTOME RKEYNYA " + bankAccount.getCustomer().getCustomerKey());
				try {
					CfMaster aCustomer = customerService.getCustomer(bankAccount.getCustomer().getCustomerKey());
					if (!aCustomer.getCurrency().getCurrencyCode().equalsIgnoreCase(bankAccount.getCurrency().getCurrencyCode())) {
						validation.addError("", "Bank Account for invoicing should be in [" + aCustomer.getCurrency().getCurrencyCode() + "]");
					}
				} catch (Exception ec) {
					log.debug("MASUK CATCH CUY");
				}
			}

			// validate selected invoice account not change to inactive
			if ((mode.equalsIgnoreCase(UIConstants.DISPLAY_MODE_EDIT)) && ((bankAccount.getInvoiceAccount() != null) && (bankAccount.getInvoiceAccount().equals(Boolean.TRUE))) && (((bankAccount.getIsActive() != null) && (bankAccount.getIsActive().equals(Boolean.FALSE))) || (bankAccount.getIsActive() == null))) {
				validation.addError("", "Bank Account for invoicing can not be inactive");
			}

			if ((overwriteBankChargesEl != null) && (overwriteBankChargesEl.equals(Boolean.TRUE))) {
				validation.required("Overwrite Payment Charge is", paymentChargeOverwriteEl);

				paymentChargeOverwriteVal = paymentChargeOverwriteEl;
				overwriteBankChargesVal = overwriteBankChargesEl;
			}

			if (validation.hasErrors()) {
				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
				}
				render("BankAccounts/detail.html", bankAccount, overwriteBankChargesVal, paymentChargeOverwriteVal, mode, status, param);
			} else {
				Long id = bankAccount.getBankAccountKey();
				serializerService.serialize(session.getId(), id, bankAccount);
				confirming(id, overwriteBankChargesVal, paymentChargeOverwriteVal, mode, status, param);
			}
		} else {
			// flash.error("argument.null", bankAccount);
			/*
			 * flash.put(UIConstants.BREADCRUMB,
			 * applicationService.getMenuBreadcrumb
			 * (MenuConstants.BN_ACCOUNT_ENTRY));
			 * renderTemplate("Customers/newList.html",
			 * UIConstants.DISPLAY_MODE_VIEW, "register-bank-acct",
			 * "Bank Account", "Register");
			 */
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				listCustomerBN2001(UIConstants.DISPLAY_MODE_VIEW, "register-bank-acct");
			} else {
				list(mode, param);
			}
		}
	}

	@Check("maintenance.bankAccount.save")
	public static void confirming(Long id, Boolean overwriteBankChargesVal, BigDecimal paymentChargeOverwriteVal, String mode, String status, String param) {
		renderArgs.put("confirming", true);

		BnAccount bankAccount = serializerService.deserialize(session.getId(), id, BnAccount.class);
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
		}
		render("BankAccounts/detail.html", bankAccount, overwriteBankChargesVal, paymentChargeOverwriteVal, mode, status, param);
	}

	@Check("maintenance.bankAccount.save")
	public static void confirm(Long id, String mode, BnAccount bankAccount, Boolean overwriteBankChargesEl, BigDecimal paymentChargeOverwriteEl, String status) {
		try {
			renderArgs.put("defaultbankcode", getValueDefaultBankCode());
			
			List<BnAccount> bankAccountDefaultForCustodyAccount = bankAccountService.listBankAccountDefaultForCustodyAccount(bankAccount.getBankAccountKey());
			log.debug("size bankAccountBySettlementAccountKey = " + bankAccountDefaultForCustodyAccount.size());
			for (BnAccount bnAccount : bankAccountDefaultForCustodyAccount) {
				if (bnAccount.getBankAccountKey().equals(bankAccount.getBankAccountKey())) {
					log.debug(">>> BANK ACCOUNT KEY DEFAULT IN CS_ACCOUNT = " + bankAccount.getBankAccountKey());
					if (bankAccount.getIsActive() == false) {
						throw new MedallionException(ExceptionConstants.DATA_DEFAULT);
					}
				}
			}

			List<BnAccount> bankAccountDefaultForRgInvestmentAccount = bankAccountService.listBankAccountDefaultForRgInvestmentAccount(bankAccount.getBankAccountKey());
			log.debug("size bankAccountByBankAccountKey = " + bankAccountDefaultForRgInvestmentAccount.size());
			for (BnAccount rgInvAccount : bankAccountDefaultForRgInvestmentAccount) {
				if (rgInvAccount.getBankAccountKey().equals(bankAccount.getBankAccountKey())) {
					log.debug(">>> BANK ACCOUNT KEY DEFAULT IN RG_INVESTMENT_ACCOUNT = " + bankAccount.getBankAccountKey());
					if (bankAccount.getIsActive() == false) {
						throw new MedallionException(ExceptionConstants.DATA_DEFAULT);
					}
				}
			}

			bankAccount.setInterestRate(new BigDecimal(df.format(bankAccount.getInterestRate().setScale(NUMBER_DECIMAL_SET))));

			// set default account type to Giro Type
			bankAccount.setAccountType(generalService.getLookup(DEFAULT_ACCOUNT_TYPE));

			if ((overwriteBankChargesEl != null) && (overwriteBankChargesEl.equals(Boolean.TRUE))) {

				bankAccount.setPaymentCharge(paymentChargeOverwriteEl);
			}

			String menuCode = null;
			if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
				menuCode = MenuConstants.BN_ACCOUNT_ENTRY;
			} else {
				menuCode = MenuConstants.BN_ACCOUNT_EDIT;
			}

			bankAccountService.saveBankAccount(menuCode, bankAccount, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				String param = "register-bank-acct";
				mode = UIConstants.DISPLAY_MODE_VIEW;
				Customers.list(mode, param); // hardcoded value for param!!
			} else {
				list(mode, "edit");
			}
		} catch (MedallionException e) {
			// logger.error(e.getMessage(), e);
			validation.clear();
			flash.error("Account No : ' " + bankAccount.getAccountNo() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			String param = null;
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				param = UIConstants.DISPLAY_MODE_EDIT;
			}
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
			}
			render("BankAccounts/detail.html", bankAccount, mode, confirming, status, param);
		}
	}

	@Check("maintenance.bankAccount.save")
	public static void back(Long id, Boolean overwriteBankChargesVal, BigDecimal paymentChargeOverwriteVal, String mode, String status, String param) {
		BnAccount bankAccount = serializerService.deserialize(session.getId(), id, BnAccount.class);
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_EDIT));
		}
		render("BankAccounts/detail.html", bankAccount, overwriteBankChargesVal, paymentChargeOverwriteVal, mode, status, param);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			BnAccount bankAccount = json.readValue(maintenanceLog.getNewData(), BnAccount.class);
			log.debug("bankAccount ===> " + bankAccount.getIsUpload());

			if (bankAccount.getInterestRate() != null)
				bankAccount.setInterestRate(new BigDecimal(df.format(bankAccount.getInterestRate().setScale(NUMBER_DECIMAL_SET))));

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			BigDecimal paymentChargeOverwriteVal = bankAccount.getPaymentCharge();

			/*
			 * if(bankAccount.getPaymentType() != null){ GnLookupDetail
			 * lookupDetailResult =
			 * generalService.getLookupDetailByFieldNameAndLookup
			 * ("BANK_CHARGES", bankAccount.getPaymentType().getLookupId());
			 * bankAccount.setPaymentCharge(new
			 * BigDecimal(lookupDetailResult.getDetailValue())); }
			 */

			render("BankAccounts/approval.html", bankAccount, mode, taskId, operation, maintenanceLogKey, from, paymentChargeOverwriteVal);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		try {
			bankAccountService.approveBankAccount(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		try {
			bankAccountService.approveBankAccount(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approvalCheckInvoiceAccount(Long maintenanceLogKey) {
		Map<String, Object> result = new HashMap<String, Object>();

		result = bankAccountService.checkInvoiceAccountForBankAccount(maintenanceLogKey);
		renderJSON(result);
	}

	private static String getValueDefaultBankCode() {
		GnSystemParameter systemParameter = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_ORG);
		GnThirdParty bankThirdParty = generalService.getThirdParty(Long.parseLong(systemParameter.getValue()));
		return bankThirdParty.getThirdPartyCode();
	}

	public static void getDefaultPaymentCharges(String paymentType) {
		GnLookupDetail lookupDetailResult = generalService.getLookupDetailByFieldNameAndLookup("BANK_CHARGES", paymentType);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("defaultpaymentcharge", lookupDetailResult.getDetailValue());

		renderJSON(result);
	}

	// refs #56 note 46
	public static void getPaymentTypeFromIncludedList(String bankCode) {
		List<SelectItem> newPaymentType = new ArrayList<SelectItem>();
		List<SelectItem> removedPaymentType = new ArrayList<SelectItem>();
		newPaymentType.addAll(paymentType);
		if (bankCode.equals(getValueDefaultBankCode())) {
			for (SelectItem selectItem : newPaymentType) {
				if (!selectItem.value.equals(DEFAULT_PAYMENT_TYPE_ID)) {
					removedPaymentType.add(selectItem);
				}
			}
		} else {
			for (SelectItem selectItem : newPaymentType) {
				if (selectItem.value.equals(DEFAULT_PAYMENT_TYPE_ID)) {
					removedPaymentType.add(selectItem);
				}
			}
		}

		newPaymentType.removeAll(removedPaymentType);

		renderJSON(newPaymentType);
	}

	@Check("maintenance.customer.list")
	public static void listCustomerBN2001(String mode, String param) {
		log.debug("Param >>>> " + param + " mode >>>> " + mode);
		// param = "register-cust-acct";
		String breadcrumb = "Inquiry";
		String module = null;
		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		} else if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_VIEW));
		}
		if (param != null) {
			if (param.contains("cust-acct")) {
				module = "Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			}
			if (param.contains("bank-acct")) {
				module = "Bank Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_ENTRY));
			}
			if (param.contains("invt-acct")) {
				module = "Investment Account";
				breadcrumb = "Register";
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_ACCOUNT_ENTRY));
			}
		}
		renderTemplate("Customers/newList.html", mode, param, module, breadcrumb);
	}
	
	public static void accountInquiry(String acctNo) {
		log.debug("===================================acctNo"+acctNo);
		
		Map mapResult = generalService.accountInquiry(acctNo, session.get(UIConstants.SESSION_USERNAME));
		System.out.println("+++++++++++++++++++++++"+mapResult.get("accountStatus"));
		System.out.println("+++++++++++++++++++++++"+mapResult.get("errorMessage"));

		renderJSON(mapResult);
	}
	
	public static void accountSummaryRestTest() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result = generalService.accountSummaryRest("1234567890");
		//result = generalService.getToken2();
		System.out.println(result);
	}
}