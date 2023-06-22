package controllers;

import helpers.UIConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.CustodyEndOfDayItem;
import com.simian.medallion.vo.SelectItem;

public class CustodyEndOfDay extends Registry {
	private static Logger log = Logger.getLogger(CustodyEndOfDay.class);

	private static final String ALL = "ALL";
	private static final String CIF = "CIF";
	private static final String ACCOUNT = "ACCOUNT";

	@Before
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> listCustomer = new ArrayList<SelectItem>();
		listCustomer.add(new SelectItem(ALL, ALL));
		listCustomer.add(new SelectItem(CIF, CIF));
		renderArgs.put("listCustomer", listCustomer);

		List<SelectItem> listAccount = new ArrayList<SelectItem>();
		listAccount.add(new SelectItem(ALL, ALL));
		listAccount.add(new SelectItem(ACCOUNT, ACCOUNT));
		renderArgs.put("listAccount", listAccount);
	}

	@Check("transaction.custodyEndOdDay")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CustodyEndOfDayItem eod = new CustodyEndOfDayItem();
		eod.setFromDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		eod.setCfmasterFilter(ALL);
		eod.setRgInvtAcctFilter(ALL);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS));
		eod.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS + eod.getSessionTag());
		eod.setProcessMark(params.get(PROCESSMARK));
		render("CustodyEndOfDay/list.html", eod, mode);
	}

	@Check("transaction.custodyEndOdDay")
	public static void process(CustodyEndOfDayItem eod) {
		log.debug("process. eod: " + eod);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		String sessionUuidX = session.get(PROCESSMARK + MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS + eod.getSessionTag());
		if (sessionUuidX == null) {
			session.put(PROCESSMARK + MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS + eod.getSessionTag(), eod.getProcessMark());
			sessionUuidX = session.get(PROCESSMARK + MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS + eod.getSessionTag());
		}
		if (isDoubleSubmission(MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS + eod.getSessionTag())) {
			list();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Date businessDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		Date fromDate = eod.getFromDate();
		String customerFilter = eod.getCfmasterFilter();
		String customerNo = eod.getCfmaster().getCustomerNo();
		String accountFilter = eod.getRgInvtAcctFilter();
		String accountNo = eod.getRgInvtAcct().getAccountNumber();

		if (!"ALL".equals(customerFilter)) {
			validation.required("CIF No", eod.getCfmaster().getCustomerNo());
			if (eod.getCfmaster().getCustomerNo() == null || "".equals(eod.getCfmaster().getCustomerNo())) {
				eod.getCfmaster().setCustomerName(null);
			}
		}
		if (!"ALL".equals(accountFilter)) {
			validation.required("Account No", eod.getRgInvtAcct().getAccountNumber());
			if (eod.getRgInvtAcct().getAccountNumber() == null || "".equals(eod.getRgInvtAcct().getAccountNumber())) {
				eod.getRgInvtAcct().setName(null);
			}
		}

		// validation.clear();

		if (fromDate == null) {
			validation.addError("Process Date", "custody.eod.invalidprocessdate");
		} else {
			String fromDateYYYYMMDD = sdf.format(fromDate);
			String businessDateYYYYMMDD = sdf.format(businessDate);

			if (fromDateYYYYMMDD.compareTo(businessDateYYYYMMDD) > 0) {
				validation.addError("Process Date", "custody.eod.processdategreaterthenappdate");
			}
		}

		List<String> logs = new ArrayList<String>();

		if (!validation.hasErrors()) {
//			logs = taService.eodCustody(fromDate, fromDate, customerFilter, customerNo, accountFilter, accountNo);
			if("ALL".equals(customerFilter) && "ALL".equals(accountFilter)){
				logs = taService.eodCustody_ALL(fromDate);
			}
			else{
				logs = taService.eodCustody(fromDate, fromDate, customerFilter, customerNo, accountFilter, accountNo);				
			}
		}
		eod.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS + eod.getSessionTag());
		eod.setProcessMark(params.get(PROCESSMARK));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_PORTFOLIO_VALUATION_PROCESS));

		log.debug("accountnumber " + eod.getRgInvtAcct().getAccountNumber());
		log.debug("name " + eod.getRgInvtAcct().getName());

		render("CustodyEndOfDay/list.html", eod, logs, mode);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit() {
		log.debug("edit. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);

		return;
	}

}
