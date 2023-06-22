package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.BnBalanceSearchParameter;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.Tools;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.BnBalance;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class BankAccountsBalances extends Registry {
	private static Logger log = Logger.getLogger(BankAccountsBalances.class);

	@Before
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		List<SelectItem> yesNoOperators = UIHelper.yesNoOperators();
		renderArgs.put("yesNoOperators", yesNoOperators);

		List<SelectItem> interestBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCRUAL_BASE);
		renderArgs.put("interestBase", interestBase);

		List<SelectItem> yearBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._YEAR_BASE);
		renderArgs.put("yearBase", yearBase);
	}

	@Check("transaction.bankAccountsBalances")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		BnBalanceSearchParameter params = new BnBalanceSearchParameter();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/list.html", params, mode);
	}

	public static void search(BnBalanceSearchParameter params) {
		log.debug("search. params: " + params);
		List<BnBalance> balances = bankAccountService.searchBnBalance(params.dateFrom, params.dateTo, params.fundCode, UIHelper.withOperator(params.accountSearchNo, params.accountNoOperator));
		for (BnBalance bnBalance : balances) {
			bnBalance.setBnAccount(bankAccountService.getBankAccount(bnBalance.getBnAccount().getBankAccountKey()));
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/grid.html", balances);
	}

	@Check("transaction.bankAccountsBalances")
	public static void pagingBnBalance(Paging page, BnBalanceSearchParameter param) {
		log.debug("pagingBnBalance. page: " + page + " param: " + param);

		if (param.isQuery()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			page.addParams("F.FUND_CODE", page.EQUAL, param.fundCode);
			page.addParams("B.ACCOUNT_NO", param.accountNoOperator, UIHelper.withOperator(param.accountSearchNo, param.accountNoOperator));
			if (param.dateFrom != null)
				page.addParams("A.BALANCE_DATE", page.GREATEQUAL, sdf.format(param.dateFrom));
			if (param.dateTo != null)
				page.addParams("A.BALANCE_DATE", page.LESSEQUAL, sdf.format(param.dateTo));
			page.addParams("1", page.EQUAL, 1);

			page.addParams(Helper.searchAll("(B.ACCOUNT_NO||B.NAME||a.BALANCE_DATE||" + "A.BALANCE_AMOUNT||to_char(A.BALANCE_DATE,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = bankAccountService.pagingBnBalance(page);
		}
		renderJSON(page);
	}

	@Check("transaction.bankAccountsBalances")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		BnBalance balance = bankAccountService.getBankBalance(id);
		balance.setFund(fundService.getFaMasterByCustomer(balance.getBnAccount().getCustomer().getCustomerNo()));
		balance.setYearBase(generalService.getLookup(balance.getYearBase().getLookupId()));
		balance.setInterestBase(generalService.getLookup(balance.getInterestBase().getLookupId()));
		String status = balance.getRecordStatus().trim();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/entry.html", balance, mode, status);
	}

	@Check("transaction.bankAccountsBalances")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		BnBalance balance = new BnBalance();
		balance.setActive(false);
		GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
		balance.setBalanceDate(current.getCurrentBusinessDate());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/entry.html", balance, mode);
	}

	@Check("transaction.bankAccountsBalances")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		BnBalance balance = bankAccountService.getBankBalance(id);
		balance.setFund(fundService.getFaMasterByCustomer(balance.getBnAccount().getCustomer().getCustomerNo()));
		balance.setYearBase(generalService.getLookup(balance.getYearBase().getLookupId()));
		balance.setInterestBase(generalService.getLookup(balance.getInterestBase().getLookupId()));
		if (balance.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || balance.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}
		String status = balance.getRecordStatus().trim();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/entry.html", balance, mode, status);
	}

	@Check("transaction.bankAccountsBalances")
	public static void save(String mode, BnBalance balance, String status) {
		log.debug("save. mode: " + mode + " balance: " + balance + " status: " + status);

		if (balance != null) {
			validation.required("Fund Code is", balance.getFund().getFundKey());
			validation.required("Account No is", balance.getBnAccount().getBankAccountKey());
			validation.required("Date is", balance.getBalanceDate());
			validation.required("Balance Amount is", balance.getBalanceAmount());

			if (balance.getBalanceAmount() != null) {
				if (balance.getBalanceAmount().doubleValue() <= 0) {
					if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
						if (balance.getActive()) {
							validation.addError("", ExceptionConstants.DATA_NO_ZERO);
						}
						if (!balance.getActive() && status.trim().equals(LookupConstants.__RECORD_STATUS_REJECTED)) {
							validation.addError("", ExceptionConstants.DATA_NO_ZERO);
						}
					} else {
						validation.addError("", ExceptionConstants.DATA_NO_ZERO);
					}
				}
			}

			GnApplicationDate current = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID);
			if (fmtYYYYMMDD(balance.getBalanceDate()).compareTo(fmtYYYYMMDD(current.getCurrentBusinessDate())) > 0) {
				validation.addError("", ExceptionConstants.DATA_GREATER_APPLICATION_DATE);
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
				render("BankAccountsBalances/entry.html", balance, mode, status);
			} else {
				Long id = balance.getBalanceKey();
				serializerService.serialize(session.getId(), id, balance);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", balance);
			entry();
		}
	}

	@Check("transaction.bankAccountsBalances")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		BnBalance balance = serializerService.deserialize(session.getId(), id, BnBalance.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/entry.html", balance, mode, status);
	}

	@Check("transaction.bankAccountsBalances")
	public static void confirm(BnBalance balance, String mode, String status) {
		log.debug("confirm. balance: " + balance + " mode: " + mode + " status: " + status);

		try {
			if (balance == null)
				back(null, mode, status);
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				BnBalance bnBalance = bankAccountService.getBankBalanceByAccountAndDate(balance.getBnAccount().getBankAccountKey(), balance.getBalanceDate());
				if (bnBalance != null) {
					throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
				}
			}
			bankAccountService.saveBankBalance(MenuConstants.BN_ACCOUNT_BALANCE, balance, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Account No '" + balance.getBnAccount().getAccountNo() + "' " + Messages.get(e.getMessage()));
			log.error(e.getMessage(), e);
			renderArgs.put("confirming", true);
			log.debug("error confirm Bank Balance " + e.getErrorCode() + " :  " + e.getMessage());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
			render("BankAccountsBalances/entry.html", balance, mode, status);
		}
	}

	@Check("transaction.bankAccountsBalances")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		BnBalance balance = serializerService.deserialize(session.getId(), id, BnBalance.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_ACCOUNT_BALANCE));
		render("BankAccountsBalances/entry.html", balance, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			BnBalance balance = json.readValue(maintenanceLog.getNewData(), BnBalance.class);

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("BankAccountsBalances/approval.html", balance, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.debug("error approval Bank Balance " + e.getMessage());
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			bankAccountService.approveBankBalance(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			bankAccountService.approveBankBalance(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void getInterestAmount(BigDecimal amount, Date prosesDate, String bank) {
		log.debug("getInterestAmount. amount: " + amount + " prosesDate: " + prosesDate + " bank: " + bank);

		String frequency;
		Date nextpaymentDate;
		Date firstCouponDate = null;
		try {
			frequency = Tools.parseYYYY(prosesDate);
			nextpaymentDate = Tools.parseMMDDYYYY(Tools.fmtMMDDYYYY(prosesDate));
			firstCouponDate = Tools.parseDate("01/01/1900", "dd/MM/yyyy");
		} catch (ParseException e1) {
			frequency = "";
			nextpaymentDate = new Date();
			log.error("error parsing date getInterestAmount " + e1.getMessage(), e1);
		}
		String account = null;
		try {
			BnAccount bnAccount = bankAccountService.getBankAccount(Long.parseLong(bank));

			GnLookup interestBase = generalService.getLookup(bnAccount.getInterestBase());
			GnLookup yearBase = generalService.getLookup(bnAccount.getYearBase());

			//Date lastPaymentDate = generalService.minusWorkingDate(nextpaymentDate, 1);

			Date datePrev = generalService.getPreviousWorkDate(prosesDate);

			try {
				datePrev = Tools.parseMMDDYYYY(Tools.fmtMMDDYYYY(datePrev));
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
			}

			//long diff = prosesDate.getTime() - lastPaymentDate.getTime();
			//int noOfDays = (int) (diff / 86400000); // 1000 * 60 * 60 * 24

			//BigDecimal interest = transactionService.calculateBalanceInterest(amount, bnAccount.getInterestRate(), datePrev, nextpaymentDate, interestBase.getLookupCode(), yearBase.getLookupCode(), frequency, false, null, null);
			BigDecimal interest = 
				transactionService.calculateBalanceInterest(amount, bnAccount.getInterestRate(), datePrev, nextpaymentDate, firstCouponDate,
						interestBase.getLookupCode(), yearBase.getLookupCode(), frequency, false, null, null, false);
			bnAccount.setAccruedInterest(interest);

			JsonHelper json = new JsonHelper().getBankAccountSerializer();
			account = json.serialize(bnAccount);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		renderJSON(account);
	}
}