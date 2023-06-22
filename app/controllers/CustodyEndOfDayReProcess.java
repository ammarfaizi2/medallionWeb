package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.Before;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.vo.CustodyEndOfDayItem;

public class CustodyEndOfDayReProcess extends Registry {
	private static Logger log = Logger.getLogger(CustodyEndOfDayReProcess.class);

	@Check("transaction.custodyEndOfDayProcess")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		CustodyEndOfDayItem eod = new CustodyEndOfDayItem();
		// eod.setFromDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		// eod.setToDate(eod.getFromDate());
		eod.setFromDate(null);
		eod.setToDate(null);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_REPROCESS_PORTFOLIO));
		render("CustodyEndOfDayReprocess/list.html", eod, mode);
	}

	@Check("transaction.custodyEndOfDayProcess")
	public static void getBackDatedDate(String accountNo) {
		log.debug("getBackDatedDate. accountNo: " + accountNo);

		Map<String, Object> mapGetBackDatedDate = new HashMap<String, Object>();
		Date applicationDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		Date fromDate = taService.getBackDatedDate(accountNo, applicationDate);
		mapGetBackDatedDate.put("isBackDate", "0");
		if (fromDate == null) {
			fromDate = taService.getLastEod(accountNo);
		}else{
			mapGetBackDatedDate.put("isBackDate", "1");
			mapGetBackDatedDate.put("valBackDate", fromDate.getTime());
		}
		
		if(fromDate==null) fromDate=applicationDate;
		mapGetBackDatedDate.put("fromDate", fromDate.getTime());
		//renderJSON(fromDate.getTime());
		renderJSON(mapGetBackDatedDate);
	}

	@Check("transaction.custodyEndOfDayProcess")
	public static void getLastEODDate(String accountNo) {
		log.debug("getLastEODDate. accountNo: " + accountNo);

		Date applicationDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		Date lastEod = taService.getLastEod(accountNo);
		if(lastEod == null) lastEod=applicationDate;
		//if (lastEod == null) renderJSON ("");
		//else renderJSON(lastEod.getTime());	
		renderJSON(lastEod.getTime());	
	}

	@Check("transaction.custodyEndOfDayProcess")
	public static void process(CustodyEndOfDayItem eod) {
		log.debug("process. eod: " + eod);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		
		Date fromDate = eod.getFromDate();
		Date toDate = eod.getToDate();
		String accountNo = eod.getRgInvtAcct().getAccountNumber();
		log.debug("++++++++++++++++++process fromDate:"+fromDate+", toDate:"+toDate);
		List<String> logs = new ArrayList<String>();
		
		//List<String> logs = taService.eodCustodyReProcess(fromDate, toDate, accountNo);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		
		while (fromDate.compareTo(toDate) <=0) {
			List<String> logsProcess = taService.eodCustodyReProcess2(fromDate, accountNo);
			logs.addAll(logsProcess);
			cal.add(Calendar.DATE, 1);
			fromDate = cal.getTime();
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_REPROCESS_PORTFOLIO));
		render("CustodyEndOfDayReprocess/list.html", eod, logs, mode, fromDate, toDate);
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
